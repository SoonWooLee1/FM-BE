package fashionmanager.develop.controller;

import fashionmanager.develop.dto.*;
import fashionmanager.develop.security.JwtTokenProvider;
import fashionmanager.develop.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService ms;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public MemberController(MemberService memberService, BCryptPasswordEncoder bCryptPasswordEncoder, JwtTokenProvider jwtTokenProvider) {
        this.ms = memberService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @GetMapping("/membernum/{memberNum}")
    public ResponseEntity<MemberDTO> selectByNum(@PathVariable int memberNum) {
        MemberDTO dto = ms.selectMemberByNum(memberNum);
        return (dto == null) ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PutMapping("/membernum/{memberNum}")
    public ResponseEntity<MemberDTO> updateMember(
            @PathVariable int memberNum,
            @RequestBody MemberDTO payload
    ) {
        payload.setMemberNum(memberNum);     // 경로 변수를 최종 신뢰
        MemberDTO updated = ms.updateMember(payload);
        return ResponseEntity.ok(updated);
    }


    @GetMapping("/selectmember")
    public ResponseEntity<List<MemberDTO>> selectMember(){
        List<MemberDTO> memberList = ms.selectMember();
        for (MemberDTO memberDTO : memberList){
            log.info("memberDTO:{}",memberDTO);
        }
        return ResponseEntity.ok(memberList);
    }

    @GetMapping("/selectmemberright")
    public ResponseEntity<List<MemberRightDTO>> selectMemberRight(){
        List<MemberRightDTO> memberRightList = ms.selectMemberRight();
        for (MemberRightDTO memberRightDTO : memberRightList){
            log.info("memberRightDTO:{}",memberRightDTO);
        }
        return ResponseEntity.ok(memberRightList);
    }

    @PostMapping("/selectmemberemail")
    public ResponseEntity<MemberDTO> selectMemberEmail(String mail){
        MemberDTO member = ms.selectMemberEmail(mail);
        if(member == null){
            return ResponseEntity.ok(null);
        }
        return ResponseEntity.ok(member);
    }

    @PostMapping("/insertadmin")
    public ResponseEntity<String> insertAdmin(@RequestBody InsertMemberDTO insertMemberDTO){
        List<MemberDTO> memberList = ms.selectMember();
        for (MemberDTO member : memberList){
            if(member.getMemberId().equals(insertMemberDTO.getMemberId())){
                log.info("이미 존재하는 관리자 아이디입니다.");
                return ResponseEntity.ok("이미 존재하는 관리자 아이디입니다.");
            }
        }

        int result = ms.insertAdmin(insertMemberDTO);
        if(result == 1){
            MemberDTO member = ms.selectMemberById(insertMemberDTO.getMemberId()); //관리자로 회원가입한 회원의 회원 번호를 확인하기 위한 코드
            int insertAdminNum = member.getMemberNum();
            AssignedRightDTO assignedRightDTO = new AssignedRightDTO();
            assignedRightDTO.setAssignedRightMemberStateNum(1);
            assignedRightDTO.setAssignedRightMemberNum(insertAdminNum);
            int result2 = ms.insertAdminRight(assignedRightDTO);
            if(result2 == 1){
                log.info("관리자 회원가입에 성공했습니다.");
                return ResponseEntity.ok("관리자 회원가입에 성공했습니다.");
            }else{
                log.info("2단계에서 관리자 회원가입에 실패했습니다.");
                return ResponseEntity.ok("관리자 회원가입에 실패했습니다.");
            }

        }else{
            log.info("1단계에서 관리자 회원가입에 실패했습니다.");
            return ResponseEntity.ok("관리자 회원가입에 실패했습니다.");
        }
    }

    @PostMapping("/insertmember")
    public ResponseEntity<String> insertMember(@RequestBody InsertMemberDTO insertMemberDTO){
        List<MemberDTO> memberList = ms.selectMember();
        for (MemberDTO member : memberList){
            if(member.getMemberId().equals(insertMemberDTO.getMemberId())){
                log.info("이미 존재하는 관리자 아이디입니다.");
                return ResponseEntity.ok("이미 존재하는 관리자 아이디입니다.");
            }
        }

        int result = ms.insertMember(insertMemberDTO);
        if(result == 1){
            MemberDTO member = ms.selectMemberById(insertMemberDTO.getMemberId()); //관리자로 회원가입한 회원의 회원 번호를 확인하기 위한 코드
            int insertAdminNum = member.getMemberNum();
            AssignedRightDTO assignedRightDTO = new AssignedRightDTO();
            assignedRightDTO.setAssignedRightMemberStateNum(2);
            assignedRightDTO.setAssignedRightMemberNum(insertAdminNum);
            int result2 = ms.insertMemberRight(assignedRightDTO);
            if(result2 == 1){
                log.info("회원가입에 성공했습니다.");
                return ResponseEntity.ok("회원가입에 성공했습니다.");
            }else{
                log.info("2단계에서 회원가입에 실패했습니다.");
                return ResponseEntity.ok("회원가입에 실패했습니다.");
            }

        }else{
            log.info("1단계에서 회원가입에 실패했습니다.");
            return ResponseEntity.ok("회원가입에 실패했습니다.");
        }
    }

    @PostMapping("/updateright")
    public ResponseEntity<String> updateRight(int num, int updateRight){
        int result = ms.updateRight(num,updateRight);
        if(result == 1){
            log.info("회원의 권한이 변경되었습니다.");
            return ResponseEntity.ok("회원의 권한이 변경되었습니다.");
        }else{
            log.info("회원의 권한 변경이 실패하였습니다.");
            return ResponseEntity.ok("회원의 권한 변경이 실패하였습니다.");
        }
    }

    @PostMapping("/updatestate")
    public ResponseEntity<String> updateState(String id, String updateState){
        int result = ms.updateState(id, updateState);
        if(result == 1){
            return ResponseEntity.ok("상태가 업데이트 되었다.");
        }else{
            return ResponseEntity.ok("상태 업데이트에 실패했습니다.");
        }
    }

    @PostMapping("/updatepassword")
    public ResponseEntity<Integer> updatePassword(String id, String changePassword, String checkPassword){
        if(changePassword.equals(checkPassword)){
            String updatePassword = bCryptPasswordEncoder.encode(checkPassword);
            int result = ms.updatePassword(id,updatePassword);
            if(result == 1){
                return ResponseEntity.ok(1);
            }else{
                return ResponseEntity.ok(0);
            }
        }else{
            return ResponseEntity.ok(0);
        }

    }

    @PostMapping("/memberlogin")
    public ResponseEntity<String> memberLogin(String memberId, String memberPwd){
        String result = ms.memberLogin(memberId, memberPwd);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/adminlogin")
    public ResponseEntity<String> adminLogin(String adminId, String adminPwd){
        String result = ms.adminLogin(adminId, adminPwd);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/auth")
    public ResponseEntity<Map<String, Object>> testToken(@RequestHeader("Authorization") String authHeader) {

        System.out.println("Authorization 헤더: " + authHeader);

        String token = authHeader.replace("Bearer ", "");
        System.out.println("토큰 추출: " + token);

        boolean result = jwtTokenProvider.validateToken(token);
        if(result){
            String memberId = jwtTokenProvider.getMemberIdFromToken(token);
            String memberEmail = jwtTokenProvider.getMemberEmailFromToken(token);
            String memberState = jwtTokenProvider.getMemberStateFromToken(token);
            int memberNum = jwtTokenProvider.getMemberNumFromToken(token);
            Map<String, Object> map = new HashMap<>();
            map.put("memberId", memberId);
            map.put("memberEmail", memberEmail);
            map.put("memberState", memberState);
            map.put("memberNum", memberNum);
            return ResponseEntity.ok(map);
        }else{
            Map<String, Object> map = new HashMap<>();
            map.put("memberId", null);
            map.put("memberEmail", null);
            map.put("memberState", null);
            map.put("memberNum", null);
            return ResponseEntity.ok(map);
        }

    }

}
