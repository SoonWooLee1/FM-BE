package fashionmanager.mapper;

import fashionmanager.dto.PhotoCategoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PhotoCategoryMapper {
    List<PhotoCategoryDTO> selectAllPhotoCategories();
}
