package web.api.eventSourcing.query;

import lombok.Data;
import web.api.domain.AggregateRoot;
import web.api.domain.Member;
import web.api.eventSourcing.event.CartCreated;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Cart extends AggregateRoot<Long> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long seq;
    private Long memberId;
    @OneToOne
    private Member member;
    private Integer ea;
    @Transient
    private Set<CartItem> cartItems = new HashSet<>();
    private Long version;

    private LocalDateTime created;

    Cart() {
        super();
    }

    Cart(Long memberId, Member member, List<CartItem> checkoutItems) throws Exception {
        super(memberId);
        this.memberId = memberId;
        this.member = member;
        this.created = LocalDateTime.now();

        for (CartItem cartItem : checkoutItems) {
            this.with(cartItem);
        }
        applyChange(new CartCreated(memberId, member, cartItems, created));

    }

    public static Cart cart(Long orderId, Member orderMember, List<CartItem> cartItems) throws Exception {
        final Cart cart = new Cart(orderId, orderMember, cartItems);
        return cart;
    }

    private Cart with(CartItem cartItem) {

        for (CartItem item : cartItems) {
            if (item.isProductEqual(cartItem)) {
                item.merge(cartItem);
                return this;
            }
        }

        this.cartItems.add(cartItem);

        return this;
    }

    public void apply(CartCreated cartCreated) {
        this.memberId = cartCreated.getMemberId();
        this.member = cartCreated.getCartMember();
        this.cartItems = cartCreated.getCartItems();
    }


}
