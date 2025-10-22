package fashionmanager.mapper;

import fashionmanager.dto.Criteria;
import fashionmanager.dto.SelectAllMentoringPostDTO;
import fashionmanager.dto.SelectDetailMentoringPostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MentoringPostMapper {
    List<SelectAllMentoringPostDTO> findAll();

    SelectDetailMentoringPostDTO findById(int postNum);

    List<SelectAllMentoringPostDTO> getListWithPaging(Criteria criteria);

    int getTotalCount();
}
