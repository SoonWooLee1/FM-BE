package fashionmanager.develop.mapper;

import fashionmanager.develop.dto.ReportDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ReportMapper {

    List<ReportDTO> selectReports();
}
