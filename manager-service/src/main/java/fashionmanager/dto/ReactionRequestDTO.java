package fashionmanager.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class ReactionRequestDTO {
    private int postNum;
    private int postCategoryNum;
    private String reactionType;
    private int memberNum;
}
