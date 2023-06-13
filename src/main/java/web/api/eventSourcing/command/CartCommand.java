package web.api.eventSourcing.command;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

public class CartCommand {

    @Getter
    @Entity
    @NoArgsConstructor
    public static class CreateCart {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long memberId;

        @OneToMany
        private List<AddProduct> addProducts;

        private Long version;

        public CreateCart(Long memberId, List<AddProduct> addProducts) {
            this.memberId = memberId;
            this.addProducts = addProducts;
        }
    }

    @Getter
    @Entity
    @NoArgsConstructor
    public static class AddProduct {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long productId;
        private String name;
        private int ea;
    }

}
