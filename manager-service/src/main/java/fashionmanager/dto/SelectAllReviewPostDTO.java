package fashionmanager.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SelectAllReviewPostDTO {
    private int num;
    private String title;
    private int memberNum;
    private String memberName;
    private int good;
    private int cheer;
}
