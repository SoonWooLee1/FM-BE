package fashionmanager.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class PageDTO {
    private int startPage;
    private int endPage;
    private boolean prev, next;

    private int total;
    private Criteria criteria;

    public PageDTO(Criteria criteria, int total) {
        this.criteria = criteria;
        this.total = total;

        int pageCount = 10;

        this.endPage = (int) (Math.ceil(criteria.getPageNum() / (double) pageCount)) * pageCount;
        this.startPage = this.endPage - pageCount + 1;
        int realEndPage = (int) (Math.ceil((total * 1.0) / criteria.getAmount()));
        if(realEndPage < this.endPage) {
            this.endPage = realEndPage;
        }

        this.prev = this.startPage > 1;
        this.next = this.endPage < realEndPage;
    }
}
