package fashionmanager.repository;

import fashionmanager.entity.Message;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message,Integer> {
    @Modifying
    @Query("DELETE FROM Message r WHERE r.messageNum = :num")
    int deleteMessage(@Param("num")int num);

    @Modifying
    @Query("UPDATE Message r SET r.messageConfirmed = true WHERE r.messageNum = :messageNum")
    int updatemessageconfirm(@Param("messageNum") int messageNum);
}
