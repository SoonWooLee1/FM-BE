package fashionmanager.service;

import fashionmanager.entity.PhotoEntity;
import fashionmanager.repository.PhotoRepository;
import fashionmanager.repository.MemberRepository;
import fashionmanager.common.PhotoType;
import fashionmanager.entity.MenteeApplyEntity;
import fashionmanager.dto.MenteeApplyCreateRequestDTO;
import fashionmanager.dto.MenteeApplyResponseDTO;
import fashionmanager.mapper.MenteeApplyMapper;
import fashionmanager.repository.MenteeApplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor   // 생성자 자동으로 해주는 어노테이션
public class MenteeApplyService {

    private final MenteeApplyMapper menteeApplyMapper;
    private final MenteeApplyRepository menteeRepository;
    private final MemberRepository  memberRepository;

    /*이미지 파일 업로드 코드 추가*/
    private final PhotoRepository photoRepository;
//    private String UploadPath = "C:\\uploadFiles\\Mentee_Apply";

    // 같은 프로젝트 패키지에 생성하게끔 만들어주는 경로
    private final String UploadPath =
            new File(System.getProperty("user.dir"), "uploadFiles/Mentee_Apply").getAbsolutePath();

    // PhotoType에서 페이지 코드 가져옴
    private static final int MENTEE_APPLY_CODE = PhotoType.MENTEE_APPLY.getCode();



    // 멘토링 신청 조회
    public List<MenteeApplyResponseDTO> selectResultApply(String content,
                                                          String accept,
                                                          Integer mentoringPostNum,
                                                          String memberName) {
        List<MenteeApplyResponseDTO> list =
                menteeApplyMapper.selectResultApply(
                        content, accept, mentoringPostNum, memberName);

        // 각각 페이지의 사진 목록을 조회해서 /files/influencer_page/파일명 형태의 URL로 세팅
        for (MenteeApplyResponseDTO dto : list) {
            int pageNum = dto.getNum();
            List<String> urls = photoRepository
                    .findAllByPostNumAndPhotoCategoryNum(pageNum, MENTEE_APPLY_CODE)
                    .stream()
                    // 정적 리소스 매핑과 맞춘 상대경로로 응답 (브라우저에서 바로 접근 가능)
                    .map(p -> "/files/mentee_apply/" + p.getName())
                    .toList();
            dto.setPhotoPaths(urls);
        }
        return list;
    }


    @Transactional
    public MenteeApplyCreateRequestDTO insertMenteeApply(MenteeApplyCreateRequestDTO req,
                                                         List<MultipartFile> postFiles) {
//         신청서 상태는 회원이 넣는 것이 아니기 때문에 null 혹은 blank 일떄는 default 상태 -> '대기'
        if (req.getAccept() == null || req.getAccept().isBlank()) {
            req.setAccept("대기");
        }

        MenteeApplyEntity entity = new  MenteeApplyEntity();
        entity.setContent(req.getContent());
        entity.setAccept(req.getAccept());
        entity.setMentoringPostNum(req.getMentoringPostNum());
        entity.setMemberNum(req.getMemberNum());

        MenteeApplyEntity entitySaved = menteeRepository.save(entity);

        /*  사진 저장: 디스크 저장 + photo 테이블 insert (postNum과 카테고리 코드 필수) */
        List<String> urls = new ArrayList<>();   // 추가

        if (postFiles != null && !postFiles.isEmpty()) {
            File uploadDir = new File(UploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }
            for (MultipartFile imageFile : postFiles) {
                // 원본 파일 이름 저장
                String originalFileName = imageFile.getOriginalFilename();
                // 원본 파일 이름이 없거나 혹은 파일 이름에 .이 포함 되어 있지 않으면 예외 처리
                if (originalFileName == null || !originalFileName.contains(".")) {
                    throw new RuntimeException("유효하지 않은 파일명입니다.");
                }
                // 확장자명 뽑기
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                // 파일 이름 리네임
                String savedFileName = UUID.randomUUID().toString() + extension;
                File targetFile = new File(UploadPath + File.separator + savedFileName);
                try {
                    imageFile.transferTo(targetFile);
                } catch (IOException e) {
                    throw new RuntimeException("파일 저장에 실패했습니다", e);
                }

                PhotoEntity photoEntity = new PhotoEntity();
                photoEntity.setName(savedFileName);
                photoEntity.setPath(UploadPath);
                photoEntity.setPhotoCategoryNum(MENTEE_APPLY_CODE);
                photoEntity.setPostNum(entitySaved.getNum());
                photoRepository.save(photoEntity);

                urls.add("/files/mentee_apply/" + savedFileName);     // 추가
            }
        }
        // db에서 이름 가져오기
        String memberName = memberRepository.findById(entitySaved.getMemberNum())
                .map(m -> m.getMemberName())
                .orElse(null);


        MenteeApplyCreateRequestDTO reqSaved = new MenteeApplyCreateRequestDTO();
        reqSaved.setNum(entitySaved.getNum());
        reqSaved.setContent(entitySaved.getContent());
        reqSaved.setAccept(entitySaved.getAccept());
        reqSaved.setMentoringPostNum(entitySaved.getMentoringPostNum());
        reqSaved.setMemberNum(entitySaved.getMemberNum());
        reqSaved.setMemberName(memberName);  // 추가
        reqSaved.setPhotoPaths(urls);      //  추가
        return reqSaved;
    }



    // 멘토링 신천서 수정
    @Transactional
    public int updateMenteeApply(MenteeApplyResponseDTO req) {
        return menteeRepository.findByNumAndMemberNum(req.getNum(), req.getMemberNum())
                .map(entity -> {
                    entity.setContent(req.getContent());
                    menteeRepository.save(entity);
                    return 1;
                })
                .orElse(0);
    }

    public int deleteMenteeApplyByMentoringPostNumAndMemberNum(String content, int mentoringPostNum, int memberNum) {
        return menteeRepository.deleteMenteeApplyByMentoringPostNumAndMemberNum(content, mentoringPostNum, memberNum);
    }
}
