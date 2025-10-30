package fashionmanager.repository;

import fashionmanager.entity.Report;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface ReportRepository extends JpaRepository<Report, Integer> {

    @Modifying
    @Query("DELETE FROM Report r WHERE r.reportNum = :num")
    int deleteReport(@Param("num") int num);

    @Modifying
    @Query("UPDATE Report r SET r.reportState = :state WHERE r.reportNum = :num")
    int updateReportState(@Param("num") int num,@Param("state") String state);

    @Modifying
    @Query("UPDATE Member r SET r.memberReportCount = r.memberReportCount + 1 WHERE r.memberNum = :memberNum")
    int reportPlus(@Param("memberNum") int memberNum);

    @Modifying
    @Query("UPDATE Member r SET r.memberReportCount = r.memberReportCount - 1 WHERE r.memberNum = :memberNum")
    int reportMinus(@Param("memberNum") int memberNum);
}
