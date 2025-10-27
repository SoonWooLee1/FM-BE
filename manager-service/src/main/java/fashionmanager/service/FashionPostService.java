package fashionmanager.service;

import fashionmanager.entity.*;
import fashionmanager.entity.pk.FashionHashTagPK;
import fashionmanager.entity.pk.FashionPostItemPK;
import fashionmanager.mapper.FashionPostMapper;
import fashionmanager.dto.*;
import fashionmanager.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class FashionPostService {
    private final FashionPostRepository fashionPostRepository;
    private final FashionHashRepository fashionHashRepository;
    private final FashionItemRepository fashionItemRepository;
    private final PhotoRepository photoRepository;
    private final FashionPostMapper fashionPostMapper;
    private final PostReactionRepository postReactionRepository;
    private final PostFashionItemRepository postFashionItemRepository;
    private static final Set<String> VALID_REACTION_TYPES = Set.of("good", "cheer");

    // 실행 경로 기준 업로드 루트 (WebConfig의 file.upload-root 기본값과 일치)
    @Value("${file.upload-root}")
    private String uploadRoot;

    // 게시물/아이템 하위 폴더
    private static final String DIR_POST  = "fashion";
    private static final String DIR_ITEM  = "fashion_items";


    @Autowired
    public FashionPostService(FashionPostRepository fashionPostRepository, FashionHashRepository fashionHashRepository,
                              FashionItemRepository fashionItemRepository, PhotoRepository photoRepository,
                              FashionPostMapper fashionPostMapper, PostReactionRepository postReactionRepository,
                              PostFashionItemRepository postFashionItemRepository) {
        this.fashionPostRepository = fashionPostRepository;
        this.fashionHashRepository = fashionHashRepository;
        this.fashionItemRepository = fashionItemRepository;
        this.photoRepository = photoRepository;
        this.fashionPostMapper = fashionPostMapper;
        this.postReactionRepository = postReactionRepository;
        this.postFashionItemRepository = postFashionItemRepository;
    }

    public List<SelectAllFashionPostDTO> getPostList() {
        return fashionPostMapper.findAll();
    }

    public List<SelectAllFashionPostDTO> getPostListByPage(Criteria criteria) {
        log.info("Criteria 설정만큼 List 갖고 오기: " + criteria);
        return fashionPostMapper.getListWithPaging(criteria);
    }

    public int getTotal() {
        log.info("get total count");
        return fashionPostMapper.getTotalCount();
    }

    public SelectDetailFashionPostDTO getDetailPost(int postNum) {
        SelectDetailFashionPostDTO postDetail = fashionPostMapper.findById(postNum);
        if (postDetail == null) {
            throw new IllegalArgumentException("해당 게시글을 찾을 수 없습니다.");
        }
        int good = postDetail.getGood();
        int cheer = postDetail.getCheer();
        double temp = 0.0;
        if (good + cheer > 0) {
            temp = ((double) good / (good + cheer)) * 100.0;
        }
        postDetail.setTemp(temp);

        if (postDetail.getPhotos() != null) {
            for (PhotoDTO photo : postDetail.getPhotos()) {
                // DB에 저장된 물리 경로에서 폴더명만 추출 (fashion / fashion_items)
                String folder = extractFolderName(photo.getPath()); // ex) fashion or fashion_items
                photo.setImageUrl("/files/" + folder + "/" + photo.getName());
            }
        }
        return postDetail;
    }

    private String extractFolderName(String path) {
        if (path == null || path.isEmpty()) return "";
        return new File(path).getName();
    }

    @Transactional
    public FashionRegistResponseDTO registPost(FashionRegistRequestDTO newPost, List<MultipartFile> postFiles,
                                               List<MultipartFile> itemFiles) {
        /* 설명. 1. fashion_post table에 게시글 등록 */
        if (newPost.getTitle() == null || newPost.getTitle().isBlank()) {
            throw new IllegalArgumentException("제목을 입력해주세요");
        }
        FashionPostEntity fashionPostEntity = changeToRegistPost(newPost);
        FashionPostEntity registFashionPost = fashionPostRepository.save(fashionPostEntity);
        int postNum = registFashionPost.getNum();

        /* 설명. 2. fashion_hash table에 해시태그 등록 */
        for (Integer tagNums : newPost.getHashtag()) {
            FashionHashTagPK fashionHashTagPK = new FashionHashTagPK(postNum, tagNums);
            FashionHashTagEntity fashionHashTagEntity = new FashionHashTagEntity(fashionHashTagPK);
            fashionHashRepository.save(fashionHashTagEntity);
        }
        savePhotos(postNum, DIR_POST, 1, postFiles); // 게시글 이미지
        savePhotos(postNum, DIR_ITEM, 4, itemFiles); // 아이템 이미지

        List<String> savedItemNames = new ArrayList<>();
        for (String itemName : newPost.getItems()) { // 전달받은 아이템 이름 목록 순회
            // 1. FashionItemEntity 생성 (price, link는 기본값/null)
            FashionItemEntity newItem = new FashionItemEntity();
            newItem.setName(itemName);

            // 2. fashion_item 테이블에 무조건 저장 (중복 검사 없음)
            FashionItemEntity savedItem = postFashionItemRepository.save(newItem);
            int itemNum = savedItem.getNum(); // 저장 후 생성된 ID 가져오기

            // 3. post_item 중간 테이블에 게시글과 아이템 관계 저장
            FashionPostItemPK fashionPostItemPK = new FashionPostItemPK(postNum, itemNum);
            FashionPostItemEntity fashionPostItemEntity = new FashionPostItemEntity(fashionPostItemPK);
            fashionItemRepository.save(fashionPostItemEntity);

            savedItemNames.add(itemName); // 저장된 아이템 이름을 응답 리스트에 추가
        }

        /* 설명. 5. responseDTO 생성 */
        FashionRegistResponseDTO response = new FashionRegistResponseDTO();
        response.setNum(postNum);
        response.setTitle(registFashionPost.getTitle());
        response.setContent(registFashionPost.getContent());
        response.setHashtag(newPost.getHashtag());
        response.setItems(savedItemNames);
        response.setMemberNum(registFashionPost.getMemberNum());
        return response;
    }

    private FashionPostEntity changeToRegistPost(FashionRegistRequestDTO newPost) {
        FashionPostEntity fashionPostEntity = new FashionPostEntity();
        fashionPostEntity.setTitle(newPost.getTitle());
        fashionPostEntity.setContent(newPost.getContent());
        fashionPostEntity.setGood(0);
        fashionPostEntity.setCheer(0);
        fashionPostEntity.setMemberNum(newPost.getMemberNum());
        return fashionPostEntity;
    }

    private void savePhotos(int postNum, String subDir, int categoryNum, List<MultipartFile> files) {
        if (files == null || files.isEmpty()) return;

        File uploadDir = new File(uploadRoot, subDir); // ex) {user.dir}/uploadFiles/fashion
        if (!uploadDir.exists() && !uploadDir.mkdirs())
            throw new RuntimeException("이미지 저장 폴더 생성 실패: " + uploadDir.getAbsolutePath());

        for (MultipartFile f : files) {
            if (f.isEmpty()) continue;
            String orig = f.getOriginalFilename();
            String ext = (orig != null && orig.contains(".")) ? orig.substring(orig.lastIndexOf(".")) : "";
            String savedName = UUID.randomUUID() + ext;

            File target = new File(uploadDir, savedName);
            try {
                f.transferTo(target);
            } catch (IOException e) {
                throw new RuntimeException("파일 저장 실패: " + target.getAbsolutePath(), e);
            }

            PhotoEntity p = new PhotoEntity();
            p.setName(savedName);
            p.setPath(uploadDir.getAbsolutePath()); // DB에는 물리경로 저장 (…/uploadFiles/fashion)
            p.setPostNum(postNum);
            p.setPhotoCategoryNum(categoryNum);
            photoRepository.save(p);
        }
    }


    @Transactional
    public FashionModifyResponseDTO modifyPost(int postNum, FashionModifyRequestDTO updatePost,
                                               List<MultipartFile> postFiles, List<MultipartFile> itemFiles) {
        /* 설명. 1. 수정할 게시글이 존재하는지 확인 */
        FashionPostEntity fashionPostEntity = fashionPostRepository.findById(postNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. id=" + postNum));

        /* 2. 게시글의 기본 정보(제목, 내용) 수정 */
        fashionPostEntity.setTitle(updatePost.getTitle());
        fashionPostEntity.setContent(updatePost.getContent());

        /* 3. 해시태그 업데이트 (비교 후 변경분만 반영) */
        List<Integer> updateTags = updateHashtags(postNum, updatePost.getHashtag());

        /* 4. 아이템 업데이트 (비교 후 변경분만 반영) */
        List<Integer> updateItems = updateItems(postNum, updatePost.getItems());

        /* 5. 사진 업데이트 (파일 처리는 기존의 'Delete & Insert' 방식 유지) */
        updatePhotos(fashionPostEntity, uploadRoot, postFiles, 1); // 게시물 사진 업데이트
        updatePhotos(fashionPostEntity, uploadRoot, itemFiles, 4); // 패션 아이템 사진 업데이트

        /* 6. 최종 응답 DTO 생성 */
        FashionModifyResponseDTO response = new FashionModifyResponseDTO();
        response.setNum(postNum);
        response.setTitle(fashionPostEntity.getTitle());
        response.setContent(fashionPostEntity.getContent());
        response.setMemberNum(fashionPostEntity.getMemberNum());
        response.setHashtag(updateTags);
        response.setItems(updateItems);
        return response;
    }

    private List<Integer> updateHashtags(int postNum, List<Integer> newHashTagsId) {
        /* 설명. 1. DB에서 현재 해시태그 ID 목록 조회 */
        List<Integer> currentHashTagIds = fashionHashRepository.findAllByFashionHashTagPK_PostNum(postNum)
                .stream()
                .map(tag -> tag.getFashionHashTagPK().getTagNum())
                .collect(Collectors.toList());

        /* 설명. 2. 삭제할 해시태그 계산 */
        List<Integer> tagsToRemove = currentHashTagIds.stream()
                .filter(id -> !newHashTagsId.contains(id))
                .collect(Collectors.toList());
        if (!tagsToRemove.isEmpty()) {
            fashionHashRepository.deleteAllByFashionHashTagPK_PostNumAndFashionHashTagPK_TagNumIn(postNum, tagsToRemove);
        }

        /* 설명. 3. 추가할 해시태그 계산 */
        List<Integer> tagsToAdd = newHashTagsId.stream()
                .filter(id -> !currentHashTagIds.contains(id))
                .collect(Collectors.toList());
        for (Integer tagNum : tagsToAdd) {
            FashionHashTagPK pk = new FashionHashTagPK(postNum, tagNum);
            fashionHashRepository.save(new FashionHashTagEntity(pk));
        }
        return newHashTagsId;
    }

    private List<Integer> updateItems(int postNum, List<Integer> newItemId) {
        /* 설명. 1. DB에서 현재 아이템 ID 목록 조회 */
        List<Integer> currentItemIds = fashionItemRepository.findAllByFashionPostItemPK_PostNum(postNum)
                .stream()
                .map(item -> item.getFashionPostItemPK().getItemNum())
                .collect(Collectors.toList());

        /* 설명. 2. 삭제할 아이템 계산 */
        List<Integer> itemsToRemove = currentItemIds.stream()
                .filter(id -> !newItemId.contains(id))
                .collect(Collectors.toList());
        if (!itemsToRemove.isEmpty()) {
            fashionItemRepository.deleteAllByFashionPostItemPK_PostNumAndFashionPostItemPK_ItemNumIn(postNum, itemsToRemove);
        }

        /* 설명. 3. 추가할 아이템 계산 */
        List<Integer> itemsToAdd = newItemId.stream()
                .filter(id -> !currentItemIds.contains(id))
                .collect(Collectors.toList());
        for (Integer itemNum : itemsToAdd) {
            FashionPostItemPK pk = new FashionPostItemPK(postNum, itemNum);
            fashionItemRepository.save(new FashionPostItemEntity(pk));
        }
        return newItemId;
    }

    private void updatePhotos(FashionPostEntity post, String uploadPath, List<MultipartFile> newImageFiles, int categoryNum) {
        /* 설명. 1. 기존 사진 파일 및 DB 정보 삭제 */
        int postNum = post.getNum();
        List<PhotoEntity> old = photoRepository.findAllByPostNumAndPhotoCategoryNum(postNum, categoryNum);
        for (PhotoEntity photo : old) {
            File f = new File(photo.getPath(), photo.getName());
            if (f.exists() && !f.delete()) log.info("수정 중 사진 파일 삭제 실패: {}", f.getPath());
        }
        photoRepository.deleteAll(old);

        // ✅ uploadPath 대신 uploadRoot/subDir 계산으로 저장하도록 변경
        String subDir = (categoryNum == 1) ? DIR_POST : DIR_ITEM;
        savePhotos(postNum, subDir, categoryNum, newImageFiles);
    }

    private void saveNewPhotos(FashionPostEntity post, String uploadPath,
                               List<MultipartFile> imageFiles, int categoryNum) {
        int postNum = post.getNum();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        for (MultipartFile imageFile : imageFiles) {
            String originalFileName = imageFile.getOriginalFilename();
            String extension = "";
            if (originalFileName != null) {
                extension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }
            String savedFileName = UUID.randomUUID().toString() + extension;

            File targetFile = new File(uploadPath + File.separator + savedFileName);
            try {
                imageFile.transferTo(targetFile);
            } catch (IOException e) {
                throw new RuntimeException("새로운 파일 저장에 실패했습니다", e);
            }

            PhotoEntity newPhoto = new PhotoEntity();
            newPhoto.setName(savedFileName);
            newPhoto.setPath(uploadPath);
            newPhoto.setPostNum(postNum);
            newPhoto.setPhotoCategoryNum(categoryNum); // 해당 카테고리 넘버
            photoRepository.save(newPhoto);
        }
    }

    @Transactional
    public void deletePost(int postNum) {
        /* 설명. 1. 게시글 존재 여부 검사 */
        FashionPostEntity postToDelete = fashionPostRepository.findById(postNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글은 존재하지 않습니다."));

        /* 설명. 2. 해시태그 삭제 */
        deleteHashtags(postNum);

        /* 설명. 3. 아이템 삭제 */
        deleteItems(postNum);

        /* 설명. 4. 사진 삭제 */
        List<PhotoEntity> photosToDelete = photoRepository.findAllByPostNumAndPhotoCategoryNum(postNum, 1);
        photosToDelete.addAll(photoRepository.findAllByPostNumAndPhotoCategoryNum(postNum, 4));
        for (PhotoEntity photo : photosToDelete) {
            File file = new File(photo.getPath() + File.separator + photo.getName());
            if (file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.info("파일 삭제 중 이미지 파일 삭제에 실패했습니다");
                }
            }
        }
        photoRepository.deleteAll(photosToDelete);

        /* 설명. 5. 게시물 삭제 */
        fashionPostRepository.deleteById(postNum);
    }

    private void deleteHashtags(int postNum) {
        fashionHashRepository.deleteAllByFashionHashTagPK_PostNum(postNum);
    }

    private void deleteItems(int postNum) {
        fashionItemRepository.deleteAllByFashionPostItemPK_PostNum(postNum);
    }

    @Transactional
    public ReactionResponseDTO insertReact(int postNum, ReactionRequestDTO reaction) {
        if (reaction.getReactionType() == null ||
                !VALID_REACTION_TYPES.contains(reaction.getReactionType().toLowerCase())) {
            throw new IllegalArgumentException("좋아요/힘내요 요청이 아닙니다!");
        }
        String reactionType = reaction.getReactionType().toLowerCase();

        FashionPostEntity post = fashionPostRepository.findById(postNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        Optional<PostReactionEntity> oldReactionOpt = postReactionRepository.findByMemberNumAndPostNumAndPostCategoryNum(
                reaction.getMemberNum(), 1, postNum); // 카테고리 1(패션)으로 고정

        if (oldReactionOpt.isPresent()) {
            // --- 1. 기존 반응이 있는 경우 (변경 또는 취소) ---
            PostReactionEntity existingReaction = oldReactionOpt.get();
            String existingType = existingReaction.getReactionType().toLowerCase();

            if (existingType.equals(reactionType)) {
                // 1-1. 같은 반응을 눌렀을 때 (취소)
                postReactionRepository.delete(existingReaction);
                if (reactionType.equals("good")) {
                    post.setGood(post.getGood() - 1);
                } else {
                    post.setCheer(post.getCheer() - 1);
                }
            } else {
                // 1-2. 다른 반응을 눌렀을 때 (변경)
                // 기존 반응 카운트 감소
                if (existingType.equals("good")) {
                    post.setGood(post.getGood() - 1);
                } else {
                    post.setCheer(post.getCheer() - 1);
                }
                // 새 반응 카운트 증가
                if (reactionType.equals("good")) {
                    post.setGood(post.getGood() + 1);
                } else {
                    post.setCheer(post.getCheer() + 1);
                }
                // DB의 반응 타입 업데이트
                existingReaction.setReactionType(reaction.getReactionType());
                postReactionRepository.save(existingReaction);
            }
        } else {
            // --- 2. 기존 반응이 없는 경우 (신규) ---
            // (지적하신 중복 코드가 있던 위치)
            // 여기서만 새 엔티티를 생성합니다.
            PostReactionEntity newReaction = new PostReactionEntity();
            newReaction.setMemberNum(reaction.getMemberNum());
            newReaction.setReactionType(reaction.getReactionType());
            newReaction.setPostNum(postNum);
            newReaction.setPostCategoryNum(1); // 패션 게시물은 1

            postReactionRepository.save(newReaction);

            // 새 반응 카운트 증가
            if (reactionType.equals("good")) {
                post.setGood(post.getGood() + 1);
            } else {
                post.setCheer(post.getCheer() + 1);
            }
        }

        // 게시글의 최종 카운트를 DB에 저장 (트랜잭션 종료 시 자동 반영되지만 명시적 save)
        fashionPostRepository.save(post);

        // --- 3. 응답 반환 ---
        ReactionResponseDTO response = new ReactionResponseDTO();
        response.setPostNum(postNum);
        response.setReactionType(reactionType);
        response.setMemberNum(reaction.getMemberNum());
        response.setPostCategoryNum(1);

        return response;
    }
}
