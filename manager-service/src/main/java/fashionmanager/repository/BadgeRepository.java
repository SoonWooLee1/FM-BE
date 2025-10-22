package fashionmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fashionmanager.entity.Badge;


public interface BadgeRepository extends JpaRepository<Badge, Integer> {}