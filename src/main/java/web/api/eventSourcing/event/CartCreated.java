package web.api.eventSourcing.event;

import lombok.Getter;
import web.api.domain.Member;
import web.api.eventSourcing.model.CartItem;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
public class CartCreated extends AbstractCartEvent{
    private Member cartMember;
    private Set<CartItem> cartItems;
    private LocalDateTime created;

    public CartCreated(Long cartId, Member member, Set<CartItem> cartItems, LocalDateTime created) {
        this.cartId = cartId;
        this.cartMember = member;
        this.cartItems = cartItems;
        this.created = created;
    }
}
