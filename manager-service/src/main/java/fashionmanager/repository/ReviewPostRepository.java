package fashionmanager.repository;

import fashionmanager.entity.ReviewPostEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewPostRepository extends JpaRepository<ReviewPostEntity, Integer> {
}
