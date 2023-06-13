package web.api.repository;


import org.springframework.data.repository.Repository;
import web.api.eventSourcing.query.Product;

public interface ProductJpaRepository extends Repository<Product, Long> {
    Product findByProductId(Long productId);

    void save(Product product);
}
