package fashionmanager.repository;

import fashionmanager.entity.FashionPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FashionPostRepository extends JpaRepository<FashionPostEntity, Integer> {
}
