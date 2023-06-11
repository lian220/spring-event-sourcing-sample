package web.api.eventSourcing.model;

import lombok.Getter;

@Getter
public class CartItem {
    private Product product;
    private int ea;

    public CartItem(Product product, int ea) {
        this.product = product;
        this.ea = ea;
    }

    public void merge(CartItem orderItem) {
        this.eq += orderItem.eq;
    }

    public boolean isProductEqual(CartItem cartItem) {
        return this.product == cartItem.product;
    }
}
