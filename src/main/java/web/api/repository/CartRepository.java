package web.api.repository;

import org.springframework.stereotype.Repository;
import web.api.eventSourcing.model.Cart;

@Repository
public interface CartRepository extends org.springframework.data.repository.Repository<Cart, Long> {
    void save(Cart cart);

    Cart findByMemberId(Long memberId);
}
