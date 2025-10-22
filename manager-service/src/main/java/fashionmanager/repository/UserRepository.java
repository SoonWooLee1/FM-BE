package fashionmanager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import fashionmanager.entity.User;



public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUserId(String userId);
    boolean existsByUserEmail(String userEmail);

}