package web.api.repository;

import org.springframework.stereotype.Repository;
import web.api.eventSourcing.model.CartItem;

@Repository
public interface CartItemRepository extends org.springframework.data.repository.Repository<CartItem, Long> {
    void save(CartItem cartItem);

    CartItem findByCartSeq(long seq);
}
