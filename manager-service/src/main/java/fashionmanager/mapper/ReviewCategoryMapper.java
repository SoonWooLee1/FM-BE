package fashionmanager.mapper;

import fashionmanager.dto.ReviewCategoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewCategoryMapper {
    List<ReviewCategoryDTO> selectAllReviewCategories();
}
