package fashionmanager.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "post_reaction")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PostReactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "num")
    private int num;

    @Column(name = "post_num", nullable = false)
    private int postNum;

    @Column(name = "post_category_num", nullable = false)
    private int postCategoryNum;

    @Column(name = "reaction_type", nullable = false)
    private String reactionType;

    @Column(name = "member_num", nullable = false)
    private int memberNum;
}
