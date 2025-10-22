package fashionmanager.mapper;

import fashionmanager.dto.Criteria;
import fashionmanager.dto.SelectAllReviewPostDTO;
import fashionmanager.dto.SelectDetailReviewPostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewPostMapper {
    List<SelectAllReviewPostDTO> findAll();

    SelectDetailReviewPostDTO findById(int postNum);

    List<SelectAllReviewPostDTO> getListWithPaging(Criteria criteria);

    int getTotalCount();
}
