package web.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import web.api.domain.Member;
import web.api.eventSourcing.command.CartCommand;
import web.api.eventSourcing.model.Cart;
import web.api.eventSourcing.model.CartItem;
import web.api.eventSourcing.model.Product;
import web.api.repository.MemberJpaRepository;
import web.api.repository.ProductJpaRepository;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class CartService {

    private final ProductJpaRepository productJpaRepository;
    private final MemberJpaRepository memberJpaRepository;
    public Cart createCart(CartCommand.CreateCart cartCreatedProduct) throws Exception {

        final List<CartItem> addProducts = cartCreatedProduct.getAddProducts().stream().map(checkoutItem -> {
            final Product readProduct = productJpaRepository.findByProductId(checkoutItem.getProductId());

            final Product modelProduct = new Product(readProduct.getProductId(),
                    readProduct.getName());

            return new CartItem(modelProduct, checkoutItem.getQuantity());
        }).collect(Collectors.toList());

        final Member readMember = memberJpaRepository.findByMemberId(cartCreatedProduct.getMemberId());
        final Cart newCart = Cart.cart(1L, readMember, addProducts);

//        orderEventHandler.save(newOrder);

        return newCart;
    }
}
