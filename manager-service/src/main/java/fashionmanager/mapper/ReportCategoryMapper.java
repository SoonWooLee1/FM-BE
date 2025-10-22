package fashionmanager.mapper;

import fashionmanager.dto.ReportCategoryDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportCategoryMapper {
    List<ReportCategoryDTO> selectAllReportCategories();
}
