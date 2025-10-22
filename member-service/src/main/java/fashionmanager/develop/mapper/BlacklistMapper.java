package fashionmanager.develop.mapper;

import fashionmanager.develop.dto.BlacklistDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface BlacklistMapper {
    List<BlacklistDTO> selectBlacklist();
}
