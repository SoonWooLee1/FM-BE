package fashionmanager.mapper;

import fashionmanager.dto.MessageCategoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageCategoryMapper {
    List<MessageCategoryDTO> selectAllMessageCategories();
}
