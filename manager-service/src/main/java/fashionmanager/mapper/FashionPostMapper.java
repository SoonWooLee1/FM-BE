package fashionmanager.mapper;

import fashionmanager.dto.Criteria;
import fashionmanager.dto.SelectAllFashionPostDTO;
import fashionmanager.dto.SelectDetailFashionPostDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FashionPostMapper {


    List<SelectAllFashionPostDTO> findAll();

    SelectDetailFashionPostDTO findById(int postNum);

    List<SelectAllFashionPostDTO> getListWithPaging(Criteria criteria);

    int getTotalCount();

}
