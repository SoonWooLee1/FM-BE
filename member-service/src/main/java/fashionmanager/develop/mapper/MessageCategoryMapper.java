package fashionmanager.develop.mapper;

import fashionmanager.develop.dto.MessageCategoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageCategoryMapper {
    List<MessageCategoryDTO> selectAllMessageCategories();
}
