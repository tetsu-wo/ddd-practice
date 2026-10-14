package domain.products_inventory;

import java.util.Optional;

public interface ProductRepository {
    Optional<domain.products_inventory.Product> findById(String productId);
    void save(domain.products_inventory.Product product);
}
