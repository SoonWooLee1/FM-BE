package fashionmanager.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Criteria {
    private int pageNum;  // 현재 페이지 번호
    private int amount;   // 한 페이지에 보여줄 게시물 수

    private String type;
    private String keyword;

    public Criteria(int pageNum, int amount) {
        this.pageNum = pageNum;
        this.amount = amount;
    }

    public Criteria() {
        this(1,10);
    }
}
