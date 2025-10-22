package fashionmanager.mapper;

import fashionmanager.dto.HashTagAndPostDTO;
import fashionmanager.dto.HashTagDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HashTagMapper {
    List<HashTagDTO> selectHashTags();

    List<HashTagAndPostDTO> selectHashTagsAndPosts();
}
