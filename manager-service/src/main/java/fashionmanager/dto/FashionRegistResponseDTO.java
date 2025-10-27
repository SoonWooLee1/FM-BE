package fashionmanager.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class FashionRegistResponseDTO {
    private int num;
    private String title;
    private String content;
    private int memberNum;
    private int good = 0;
    private int cheer = 0;
    private double temp;
    private List<Integer> hashtag;
    private List<String> items;
}
