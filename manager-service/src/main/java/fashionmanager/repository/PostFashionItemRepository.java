package fashionmanager.repository;

import fashionmanager.entity.FashionItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostFashionItemRepository  extends JpaRepository<FashionItemEntity, Integer> {

}
