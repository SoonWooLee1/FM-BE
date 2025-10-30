package fashionmanager.develop.mapper;

import fashionmanager.develop.dto.MemberDTO;
import fashionmanager.develop.dto.MemberRightBadgeDTO;
import fashionmanager.develop.dto.MemberRightDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MemberMapper {
    MemberDTO selectMessageAllow(@Param("selectMemberId")String memberId);

    MemberDTO selectMemberByNum(@Param("selectMemberNum")int memberNum);

    int updateMemberByNum(MemberDTO dto);

    List<MemberDTO> selectMember();

    MemberDTO selectMemberById(@Param("selectMemberId")String memberId);

    MemberDTO selectMemberByEmail(@Param("selectMemberEmail")String memberEmail);

    MemberRightDTO selectMemberRightById(@Param("selectMemberId")String memberId);

    MemberDTO selectMemberByEmailAndId(@Param("selectMemberEmail")String mail, @Param("selectMemberId")String id);

    List<MemberRightDTO> selectMemberRight();

    List<MemberRightBadgeDTO> selectMemberRightBadge();
}
