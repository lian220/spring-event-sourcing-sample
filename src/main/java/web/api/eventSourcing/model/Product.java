package web.api.eventSourcing.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
@Entity
public class Product {
    @Id
    private Long productId;
    private String name;
    private String ea;

    public Product(Long productId, String name) {
        this.productId = productId;
        this.name = name;
    }

    public Product() {

    }
}
