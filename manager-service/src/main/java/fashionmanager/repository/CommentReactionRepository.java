package fashionmanager.repository;

import fashionmanager.entity.CommentReaction;
import fashionmanager.entity.CommentReactionId;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, CommentReactionId> {
    Optional<CommentReaction> findByMemberNumAndCommentNum(Integer memberNum, Integer commentNum);
}