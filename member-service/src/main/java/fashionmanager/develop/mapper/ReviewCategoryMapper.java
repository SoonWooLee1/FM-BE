package fashionmanager.develop.mapper;

import fashionmanager.develop.dto.ReviewCategoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReviewCategoryMapper {
    List<ReviewCategoryDTO> selectAllReviewCategories();
}
