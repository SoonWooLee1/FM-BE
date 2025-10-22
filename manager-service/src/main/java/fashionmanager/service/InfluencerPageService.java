package fashionmanager.service;

import fashionmanager.entity.PhotoEntity;
import fashionmanager.repository.PhotoRepository;
import fashionmanager.repository.MemberRepository;
import fashionmanager.common.PhotoType;
import fashionmanager.entity.InfluencerPageEntity;
import fashionmanager.dto.InfluencerPageCreateRequestDTO;
import fashionmanager.dto.InfluencerPageResponseDTO;
import fashionmanager.mapper.InfluencerPageMapper;
import fashionmanager.repository.InfluencerPageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor   // 생성자 자동으로 해주는 어노테이션
public class InfluencerPageService {

    private final InfluencerPageMapper influencerPageMapper;
    private final InfluencerPageRepository influencerPageRepository;
    private final MemberRepository memberRepository;

    /*이미지 파일 업로드 코드 추가*/
    private final PhotoRepository photoRepository;
//    private String UploadPath = "C:\\uploadFiles\\Influencer_Page";

    // 같은 프로젝트 패키지에 생성하게끔 만들어주는 경로
    private final String UploadPath =
            new File(System.getProperty("user.dir"), "uploadFiles/Influencer_Page").getAbsolutePath();


    // PhotoType에서 페이지 코드 가져옴
    private static final int INFLUENCER_PAGE_CODE = PhotoType.INFLUENCER_PAGE.getCode();


    // 페이지 조건 조회 또는 전체 조회 + 각 페이지별 사진 세팅
    public List<InfluencerPageResponseDTO> selectResultPage(String title,
                                                            String insta,
                                                            String phone,
                                                            Integer memberNum) {
        List<InfluencerPageResponseDTO> list =
                influencerPageMapper.selectResultPage(
                        title, insta, phone, memberNum);

        // 각각 페이지의 사진 목록을 조회해서 /files/influencer_page/파일명 형태의 URL로 세팅
        for (InfluencerPageResponseDTO dto : list) {
            int pageNum = dto.getNum();
            List<String> urls = photoRepository
                    .findAllByPostNumAndPhotoCategoryNum(pageNum, INFLUENCER_PAGE_CODE)
                    .stream()
                     // 정적 리소스 매핑과 맞춘 상대경로로 응답 (브라우저에서 바로 접근 가능)
                    .map(p -> "/files/influencer_page/" + p.getName())
                    .toList();
            dto.setPhotoPaths(urls);
        }
        return list;
    }


    // 페이지 생성 (+ 선택적 사진 업로드)
    @Transactional
    public InfluencerPageCreateRequestDTO insertInfluencerPage(InfluencerPageCreateRequestDTO req,
                                                               List<MultipartFile> postFiles) {
        InfluencerPageEntity entity = new InfluencerPageEntity();
        entity.setTitle(req.getTitle());
        entity.setContent(req.getContent());
        entity.setInsta(req.getInsta());
        entity.setPhone(req.getPhone());
        entity.setMemberNum(req.getMemberNum());

        InfluencerPageEntity saved = influencerPageRepository.save(entity);

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
                photoEntity.setPostNum(saved.getNum());                    //  반드시 postNum 설정
                photoEntity.setPhotoCategoryNum(INFLUENCER_PAGE_CODE);
                photoRepository.save(photoEntity);

                urls.add("/files/influencer_page/" + savedFileName);     // 추가
            }
        }

        // db에서 이름 가져오기
        String memberName = memberRepository.findById(saved.getMemberNum())
                .map(m -> m.getMemberName())
                .orElse(null);


        InfluencerPageCreateRequestDTO reqSaved = new InfluencerPageCreateRequestDTO();
        reqSaved.setNum(saved.getNum());
        reqSaved.setTitle(saved.getTitle());
        reqSaved.setContent(saved.getContent());
        reqSaved.setInsta(saved.getInsta());
        reqSaved.setPhone(saved.getPhone());
        reqSaved.setMemberNum(saved.getMemberNum());
        reqSaved.setMemberName(memberName);  // 추가
        reqSaved.setPhotoPaths(urls);      //  추가
        return reqSaved;
    }


    // 페이지 수정
    @Transactional
    public int updateInfluencerPage(InfluencerPageResponseDTO req) {
        return influencerPageRepository
                .findByNumAndMemberNumAndMember_MemberName(req.getNum(), req.getMemberNum(), req.getMemberName())
                .map(entity -> {
                    entity.setTitle(req.getTitle());
                    entity.setContent(req.getContent());
                    entity.setInsta(req.getInsta());
                    entity.setPhone(req.getPhone());
                    influencerPageRepository.save(entity);
                    return 1;
                })
                .orElse(0);
    }


    public int deleteInfluencerPageTitleAndMemberNum(String title, int memberNum) {
        return influencerPageRepository.deleteInfluencerPageTitleAndMemberNum(title, memberNum);
    }

}
