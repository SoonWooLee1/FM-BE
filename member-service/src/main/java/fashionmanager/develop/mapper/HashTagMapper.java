package fashionmanager.develop.mapper;

import fashionmanager.develop.dto.HashTagAndPostDTO;
import fashionmanager.develop.dto.HashTagDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HashTagMapper {
    List<HashTagDTO> selectHashTags();

    List<HashTagAndPostDTO> selectHashTagsAndPosts();
}
