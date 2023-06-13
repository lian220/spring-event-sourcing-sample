package web.api.eventSourcing.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import web.api.eventSourcing.query.Cart;
import web.api.eventSourcing.query.CartItem;
import web.api.eventSourcing.query.Product;
import web.api.repository.CartItemRepository;
import web.api.repository.CartRepository;
import web.api.repository.ProductJpaRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class CartEventProjector extends AbstractEventProjector{

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductJpaRepository productJpaRepository;

    public void execute(Cart event) {
        Cart cart = cartRepository.findByMemberId(event.getMemberId());
        if(cart != null) event.setSeq(cart.getSeq());
        cartRepository.save(event);
        for (CartItem cartItem: event.getCartItems()) {
            CartItem cartItemOrigin = cartItemRepository.findByCartSeq(cart.getSeq());
            if(cartItemOrigin!=null)
                cartItem.setCartSeq(cartItemOrigin.getCartSeq());
            else
                cartItem.setCartSeq(cart.getSeq());
            cartItemRepository.save(cartItem);

            Product product = productJpaRepository.findByProductId(cartItem.getProduct().getProductId());
            product.setEa(product.getEa() - cartItem.getEa());
            productJpaRepository.save(product);
        }
    }

}
