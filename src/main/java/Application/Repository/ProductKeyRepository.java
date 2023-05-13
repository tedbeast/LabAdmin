package Application.Repository;

import Application.Model.ProductKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductKeyRepository extends JpaRepository<ProductKey, Long> {
}
