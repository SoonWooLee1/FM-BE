package fashionmanager.controller;

import fashionmanager.dto.InfluencerApplyCreateRequestDTO;
import fashionmanager.dto.InfluencerApplyResponseDTO;
import fashionmanager.service.InfluencerApplyService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/influencerApply")
@Slf4j
public class InfluencerApplyController {

    private final InfluencerApplyService influencerApplyService;

    @Autowired
    public InfluencerApplyController(InfluencerApplyService influencerService) {
        this.influencerApplyService = influencerService;
    }

    // 인플루언서 신청 조회
    @GetMapping("/selectInfluencerApply")
    public ResponseEntity<List<InfluencerApplyResponseDTO>> selectResultApply(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String accept,
            @RequestParam(required = false) Integer memberNum,
            @RequestParam(required = false) String memberName){
        List<InfluencerApplyResponseDTO> influencerApplyList
                = influencerApplyService.selectResultApply(
                        title,content,accept,memberNum,memberName);
        for(InfluencerApplyResponseDTO InfluencerApply : influencerApplyList){
            log.info("인플루언서 신청 조회: {}", influencerApplyList.size());
        }
        return ResponseEntity.ok(influencerApplyList);
    }

    // 인플루언서 신청 생성 + 사진 추가
    //  consumes = 이거 써서 반드시 multipart/form-data 로 요청해야 함 (JSON+파일 동시 전송), 이미지 멀티로 보낼때
    @PostMapping(value = "/insertInfluencerApply", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<InfluencerApplyCreateRequestDTO> insertInfluencerApply(
                                    @ModelAttribute InfluencerApplyCreateRequestDTO req,
                                    @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        InfluencerApplyCreateRequestDTO saved = influencerApplyService.insertInfluencerApply(req, files);

        if (saved == null) {
            log.info("인플루언서 신청 실패: {}", req);
            // 400 Bad Request 로 처리
            throw new IllegalArgumentException("인플루언서 신청에 실패했습니다.");
        }
        log.info("인플루언서 신청 완료!: {}", saved);
        return ResponseEntity.ok(saved);
    }



    // 인플루언서 신청 수정
    @PutMapping("/updateInfluencerApply")
    public ResponseEntity<Map<String, Object>> updateInfluencerApply(
                                                @RequestBody InfluencerApplyResponseDTO req) {
        int result = influencerApplyService.updateInfluencerApply(req);

        if (result != 1) {
            log.info("인플루언서 신청서 수정 실패: {}", result);
            // 수정 대상이 없거나 충돌 등 → 404로 명확히
            throw new EntityNotFoundException("수정 대상 신청서를 찾을 수 없습니다.");
        }

        Map<String, Object> body = new HashMap<>();
        body.put("인플루언서 신청서 수정", result);
        log.info("인플루언서 신청서 수정 완료!: {}", result);
        return ResponseEntity.ok(body);
    }




    // 인플루언서 신청 취소
    @DeleteMapping("/deleteInfluencerApply")
    public ResponseEntity<Map<String, Object>> deleteInfluencerApplyByTitleAndMemberNum(
                                                            @RequestParam String title,
                                                            @RequestParam int memberNum) {
        int deleted = influencerApplyService.deleteInfluencerApplyByTitleAndMemberNum(title, memberNum);
        if (deleted <= 0) {
            log.info("인플루언서 신청서 삭제 실패: {}", deleted);
            // 삭제 대상 없음 → 404
            throw new EntityNotFoundException("삭제할 신청서를 찾을 수 없습니다.");
        }
        Map<String, Object> body = new HashMap<>();
        body.put("인플루언서 신청서 삭제", deleted);
        log.info("인플루언서 신청서 삭제 완료!: {}", deleted);
        return ResponseEntity.ok(body);
    }

}