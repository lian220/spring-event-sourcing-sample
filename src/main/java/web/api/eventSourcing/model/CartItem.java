package web.api.eventSourcing.model;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@NoArgsConstructor
public class CartItem {
    @Id
    private long cartSeq;
    @OneToOne
    private Product product;
    private int ea;

    public CartItem(Product product, int ea) {
        this.product = product;
        this.ea = ea;
    }

    public void merge(CartItem orderItem) {
        this.ea += orderItem.ea;
    }

    public boolean isProductEqual(CartItem cartItem) {
        return this.product == cartItem.product;
    }
}
