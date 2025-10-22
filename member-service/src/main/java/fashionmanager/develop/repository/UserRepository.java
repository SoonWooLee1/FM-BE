package fashionmanager.develop.repository;

import fashionmanager.develop.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUserId(String userId);
    boolean existsByUserEmail(String userEmail);

}