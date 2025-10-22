package fashionmanager.mapper;

import fashionmanager.dto.MemberDTO;
import fashionmanager.dto.MemberRightDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {
    MemberDTO selectMessageAllow(@Param("selectMemberId")String memberId);

    MemberDTO selectMemberByNum(@Param("selectMemberNum")int memberNum);

    List<MemberDTO> selectMember();

    MemberDTO selectMemberById(@Param("selectMemberId")String memberId);

    MemberRightDTO selectMemberRightById(@Param("selectMemberId")String memberId);
}
