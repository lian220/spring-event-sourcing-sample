package web.api.eventSourcing.event;

import lombok.Getter;
import lombok.NoArgsConstructor;
import web.api.domain.Member;
import web.api.eventSourcing.model.CartItem;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@NoArgsConstructor
public class CartCreated extends AbstractCartEvent{
    private Member cartMember;
    private Set<CartItem> cartItems;
    private LocalDateTime created;

    public CartCreated(Long memberId, Member member, Set<CartItem> cartItems, LocalDateTime created) {
        this.memberId = memberId;
        this.cartMember = member;
        this.cartItems = cartItems;
        this.created = created;
    }

    public Long getMemberId() {
        return this.memberId;
    }
}
