package fashionmanager.develop.service;

import fashionmanager.develop.dto.*;
import fashionmanager.develop.entity.AssignedRight;
import fashionmanager.develop.entity.Member;
import fashionmanager.develop.mapper.MemberMapper;
import fashionmanager.develop.repository.AssignedRightRepository;
import fashionmanager.develop.repository.MemberRepository;
import fashionmanager.develop.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class MemberService {

    private final MemberMapper memberMapper;

    private final MemberRepository memberRepository;
    private final AssignedRightRepository assignedRightRepository;

    private final ModelMapper modelMapper;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public MemberService(MemberMapper memberMapper, MemberRepository memberRepository, AssignedRightRepository assignedRightRepository, ModelMapper modelMapper, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.memberMapper = memberMapper;
        this.memberRepository = memberRepository;
        this.assignedRightRepository = assignedRightRepository;
        this.modelMapper = modelMapper;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    public MemberDTO selectMessageAllow(String memberId){
        return memberMapper.selectMessageAllow(memberId);
    }

    public MemberDTO selectMemberByNum(int memberNum){
        return memberMapper.selectMemberByNum(memberNum);
    }

    @Transactional
    public MemberDTO updateMember(MemberDTO dto) {
        // int는 null이 불가능하므로 0이면 "값이 세팅되지 않음"으로 판단
        if (dto.getMemberNum() == 0) {
            throw new IllegalArgumentException("memberNum is required");
        }

        // ✅ 비밀번호 해시 추가 (있을 때만)
        if (dto.getMemberPwd() != null && !dto.getMemberPwd().isBlank()) {
            // 이미 bcrypt 형태($2a$로 시작)면 재인코딩 방지
            if (!dto.getMemberPwd().startsWith("$2a$") &&
                    !dto.getMemberPwd().startsWith("$2b$") &&
                    !dto.getMemberPwd().startsWith("$2y$")) {

                dto.setMemberPwd(bCryptPasswordEncoder.encode(dto.getMemberPwd()));
            }
        } else {
            // 비번 안 바꿀 경우 null로 처리 → Mapper의 <if>에서 제외
            dto.setMemberPwd(null);
        }

        // 최소 한 개라도 수정 값이 있는지 체크(문자열만 예시)
        boolean hasAny =
                (dto.getMemberId() != null) ||
                        (dto.getMemberPwd() != null) ||
                        (dto.getMemberEmail() != null) ||
                        (dto.getMemberName() != null);

        if (!hasAny) {
            throw new IllegalArgumentException("No updatable fields provided");
        }

        int updated = memberMapper.updateMemberByNum(dto);
        if (updated == 0) {
            throw new IllegalStateException("No member updated");
        }

        return memberMapper.selectMemberByNum(dto.getMemberNum());
    }

    public List<MemberDTO> selectMember() {
        return memberMapper.selectMember();
    }

    public int insertAdmin(InsertMemberDTO insertMemberDTO) {
        boolean check1 = insertMemberDTO.getMemberId() == null || "".equals(insertMemberDTO.getMemberId());
        boolean check2 = insertMemberDTO.getMemberPwd() == null || "".equals(insertMemberDTO.getMemberPwd());
        boolean check3 = insertMemberDTO.getMemberName() == null || "".equals(insertMemberDTO.getMemberName());
        boolean check4 = insertMemberDTO.getMemberEmail() == null || "".equals(insertMemberDTO.getMemberEmail());
        boolean check5 = insertMemberDTO.getMemberAge() == 0;
        boolean check6 = !insertMemberDTO.getMemberGender().equals("남성") && !insertMemberDTO.getMemberGender().equals("여성");
        if (check1 || check2 || check3 || check4 || check5 || check6) {
            return 0;
        }
        insertMemberDTO.setMemberPwd(bCryptPasswordEncoder.encode(insertMemberDTO.getMemberPwd()));
        memberRepository.save(modelMapper.map(insertMemberDTO, Member.class));
        return 1;
    }

    public int insertMember(InsertMemberDTO insertMemberDTO) {
        boolean check1 = insertMemberDTO.getMemberId() == null || "".equals(insertMemberDTO.getMemberId());
        boolean check2 = insertMemberDTO.getMemberPwd() == null || "".equals(insertMemberDTO.getMemberPwd());
        boolean check3 = insertMemberDTO.getMemberName() == null || "".equals(insertMemberDTO.getMemberName());
        boolean check4 = insertMemberDTO.getMemberEmail() == null || "".equals(insertMemberDTO.getMemberEmail());
        boolean check5 = insertMemberDTO.getMemberAge() == 0;
        boolean check6 = !insertMemberDTO.getMemberGender().equals("남성") && !insertMemberDTO.getMemberGender().equals("여성");
        if (check1 || check2 || check3 || check4 || check5 || check6) {
            return 0;
        }
        insertMemberDTO.setMemberPwd(bCryptPasswordEncoder.encode(insertMemberDTO.getMemberPwd()));
        memberRepository.save(modelMapper.map(insertMemberDTO, Member.class));
        return 1;
    }

    public int insertAdminRight(AssignedRightDTO assignedRightDTO) {
        boolean check1 = assignedRightDTO.getAssignedRightMemberStateNum() == 0;
        boolean check2 = assignedRightDTO.getAssignedRightMemberStateNum() == 0;
        if (check1 || check2) {
            return 0;
        }

        assignedRightRepository.save(modelMapper.map(assignedRightDTO, AssignedRight.class));
        return 1;
    }

    public int insertMemberRight(AssignedRightDTO assignedRightDTO) {
        boolean check1 = assignedRightDTO.getAssignedRightMemberStateNum() == 0;
        boolean check2 = assignedRightDTO.getAssignedRightMemberStateNum() == 0;
        if (check1 || check2) {
            return 0;
        }

        assignedRightRepository.save(modelMapper.map(assignedRightDTO, AssignedRight.class));
        return 1;
    }

    @Transactional
    public int updateRight(int num, int updateRight) {
        boolean check1 = num == 0;
        boolean check2 = updateRight == 0;
        if (check1 || check2) {
            return 0;
        }
        int result = assignedRightRepository.updateRight(num,updateRight);
        if (result == 1) {
            return 1;
        }else{
            return 0;
        }
    }

    public MemberDTO selectMemberById(String memberId){
        MemberDTO member = memberMapper.selectMemberById(memberId);
        if(member == null){
            log.info("존재하지 않는 회원입니다.");
            return null;
        }else{
            log.info("존재하는 회원입니다,");
            return member;
        }
    }

    public MemberRightDTO selectMemberRightById(String memberId){
        MemberRightDTO memberRight = memberMapper.selectMemberRightById(memberId);
        if(memberRight == null){
            log.info("존재하지 않는 회원입니다.");
            return null;
        }else{
            log.info("존재하는 회원입니다,");
            return memberRight;
        }
    }

    public String memberLogin(String memberId, String memberPwd) {
        MemberRightDTO member = memberMapper.selectMemberRightById(memberId);
        if(member == null){
            log.info("존재하지 않는 회원");
            return "존재하지 않는 회원";
        }else{
            boolean check = bCryptPasswordEncoder.matches(memberPwd, member.getMemberPwd());
            if(!check){
                log.info("비밀번호 불일치");
                return "비밀번호 불일치";
            }else{
                log.info("비밀번호 일치");
                String token = jwtTokenProvider.createToken(member);
                return token;
            }
        }
    }

    public MemberDTO selectMemberEmail(String mail) {
        MemberDTO member = memberMapper.selectMemberByEmail(mail);
        if(member == null){
            return null;
        }
        return member;
    }

    @Transactional
    public int updatePassword(String id, String updatePassword) {
        int result = memberRepository.updatePassword(id, updatePassword);
        if(result == 1){
            return 1;
        }else{
            return 0;
        }
    }

    public String adminLogin(String adminId, String adminPwd) {
        MemberRightDTO member = memberMapper.selectMemberRightById(adminId);
        if(member.getMemberStateNum() != 1 || member == null){
            log.info("존재하지 않는 관리자 회원");
            return "존재하지 않는 관리자 회원";
        }else{
            boolean check = bCryptPasswordEncoder.matches(adminPwd, member.getMemberPwd());
            if(!check){
                log.info("비밀번호 불일치");
                return "비밀번호 불일치";
            }else{
                log.info("비밀번호 일치");
                String token = jwtTokenProvider.createToken(member);
                return token;
            }
        }
    }


    public List<MemberRightDTO> selectMemberRight() {
        return memberMapper.selectMemberRight();
    }

    public List<MemberRightBadgeDTO> selectMemberRightBadge() {
        return memberMapper.selectMemberRightBadge();
    }

    @Transactional
    public int updateState(String id, String updateState) {
        return memberRepository.updateState(id, updateState);
    }

}



