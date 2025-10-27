package fashionmanager.develop.repository;

import fashionmanager.develop.entity.Member;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface MemberRepository extends JpaRepository<Member,Integer> {

    @Modifying
    @Query(value = "UPDATE Member r SET r.memberPwd = :updatePassword WHERE r.memberId = :id")
    int updatePassword(@Param("id")String id, @Param("updatePassword")String updatePassword);

    @Modifying
    @Query(value = "UPDATE Member r SET r.memberStatus = :updateState WHERE r.memberId = :id")
    int updateState(@Param("id")String id, @Param("updateState")String updateState);
}
