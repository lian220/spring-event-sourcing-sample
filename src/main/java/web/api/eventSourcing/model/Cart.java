package web.api.eventSourcing.model;

import lombok.Data;
import web.api.domain.AggregateRoot;
import web.api.domain.Member;
import web.api.eventSourcing.event.CartCreated;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Cart extends AggregateRoot<Long> {
    private Long cartId;
    private Member member;
    private Integer ea;
    private Set<CartItem> cartItems = new HashSet<>();

    private LocalDateTime created;

    Cart() {
        super();
    }

    Cart(Long cartId, Member member, List<CartItem> checkoutItems) throws Exception {
        super(cartId);
        this.cartId = cartId;
        this.member = member;
        this.created = LocalDateTime.now();

        for (CartItem cartItem : cartItems) {
            this.with(cartItem);
        }
        applyChange(new CartCreated(cartId, member, cartItems, created));

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

}
