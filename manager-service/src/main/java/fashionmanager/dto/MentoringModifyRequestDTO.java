package fashionmanager.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class MentoringModifyRequestDTO {
    private int num;
    private String title;
    private String content;
    private boolean finish;
    private int authorNum;
}
