package fashionmanager.service;


import fashionmanager.entity.PhotoEntity;
import fashionmanager.entity.PostReactionEntity;
import fashionmanager.entity.ReviewPostEntity;
import fashionmanager.entity.ReviewPostItemEntity;
import fashionmanager.entity.pk.ReviewPostItemPK;
import fashionmanager.mapper.ReviewPostMapper;
import fashionmanager.repository.PhotoRepository;
import fashionmanager.repository.PostReactionRepository;
import fashionmanager.repository.ReviewItemRepository;
import fashionmanager.repository.ReviewPostRepository;
import fashionmanager.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ReviewPostService {
    private final ReviewPostRepository reviewPostRepository;
    private final ReviewItemRepository reviewItemRepository;
    private final PhotoRepository photoRepository;
    private final ReviewPostMapper reviewPostMapper;
    private final PostReactionRepository postReactionRepository;
    private static final Set<String> VALID_REACTION_TYPES = Set.of("good", "cheer");
    private String postUploadPath = "C:\\uploadFiles\\review";
    private String reviewItemUploadPath = "C:\\uploadFiles\\review_items";

    @Autowired
    public ReviewPostService(ReviewPostRepository reviewPostRepository,
                             ReviewItemRepository reviewItemRepository, PhotoRepository photoRepository, ReviewPostMapper reviewPostMapper, PostReactionRepository postReactionRepository) {
        this.reviewPostRepository = reviewPostRepository;
        this.reviewItemRepository = reviewItemRepository;
        this.photoRepository = photoRepository;
        this.reviewPostMapper = reviewPostMapper;
        this.postReactionRepository = postReactionRepository;
    }

    public List<SelectAllReviewPostDTO> getPostList() {
        return reviewPostMapper.findAll();
    }

    public SelectDetailReviewPostDTO getDetailPost(int postNum) {
        SelectDetailReviewPostDTO postDetail = reviewPostMapper.findById(postNum);
        if (postDetail == null) {
            throw new IllegalArgumentException("해당 게시글을 찾을 수 없습니다.");
        }
        if(postDetail.getPhotos() != null) {
            for (PhotoDTO photo : postDetail.getPhotos()) {
                String subPath = "";
                if (photo.getPhotoCategoryNum() == 2) {
                    subPath = "review/";
                } else if (photo.getPhotoCategoryNum() == 5) {
                    subPath = "review_items/";
                }
                String imageUrl = "/images/" + extractFolderName(photo.getPath()) + "/" + photo.getName();
                photo.setImageUrl(imageUrl);
            }
        }
        return postDetail;
    }
    private String extractFolderName(String path) {
        if(path == null || path.isEmpty()) {
            return "";
        }
        return new File(path).getName();
    }


    @Transactional
    public ReviewRegistResponseDTO registPost(ReviewRegistRequestDTO newPost, List<MultipartFile> postFiles,
                                              List<MultipartFile> itemFiles) {
        /* 설명. 1. fashion_post table에 게시글 등록 */
        ReviewPostEntity reviewPostEntity = changeToRegistPost(newPost);
        ReviewPostEntity registReviewPost = reviewPostRepository.save(reviewPostEntity);
        int postNum = registReviewPost.getNum();

        /* 설명. 2. review_item table에 아이템 등록 */
        for (Integer itemNums : newPost.getItems()) {
            ReviewPostItemPK reviewPostItemPK = new ReviewPostItemPK(postNum, itemNums);
            ReviewPostItemEntity reviewPostItemEntity = new ReviewPostItemEntity(reviewPostItemPK);
            reviewItemRepository.save(reviewPostItemEntity); // 반복문 안에서 매번 저장
        }

        /* 설명. 3. photo table에 사진 등록, 사진 카테고리 번호 1 = 패션 게시물 */
        if (postFiles != null && !postFiles.isEmpty()) {
            File uploadDir = new File(postUploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // 경로에 해당하는 폴더가 없으면 생성해줌
            }
            for (MultipartFile imageFile : postFiles) {
                String originalFileName = imageFile.getOriginalFilename();
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String savedFileName = UUID.randomUUID().toString() + extension;

                File targetFile = new File(postUploadPath + File.separator + savedFileName);
                try {
                    imageFile.transferTo(targetFile);
                } catch (IOException e) {
                    throw new RuntimeException("파일 저장에 실패했습니다", e);
                }
                PhotoEntity photoEntity = new PhotoEntity();
                photoEntity.setName(savedFileName); // 고유한 이름으로 저장
                photoEntity.setPath(postUploadPath);
                photoEntity.setPostNum(postNum);    // postNum과 CategoryNum 지정
                photoEntity.setPhotoCategoryNum(2); // 후기 게시물 사진은 2
                photoRepository.save(photoEntity);
            }
        }
        /* 설명. 3-2. 패션 아이템 사진 등록, 사진 카테고리 번호 4 = 패션 아이템 */
        if (itemFiles != null && !itemFiles.isEmpty()) {
            File uploadDir = new File(reviewItemUploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs(); // 경로에 해당하는 폴더가 없으면 생성해줌
            }
            for (MultipartFile imageFile : itemFiles) {
                String originalFileName = imageFile.getOriginalFilename();
                String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
                String savedFileName = UUID.randomUUID().toString() + extension;

                File targetFile = new File(reviewItemUploadPath + File.separator + savedFileName);
                try {
                    imageFile.transferTo(targetFile);
                } catch (IOException e) {
                    throw new RuntimeException("파일 저장에 실패했습니다", e);
                }
                PhotoEntity photoEntity = new PhotoEntity();
                photoEntity.setName(savedFileName); // 고유한 이름으로 저장
                photoEntity.setPath(reviewItemUploadPath);
                photoEntity.setPostNum(postNum);    // postNum과 CategoryNum 저장
                photoEntity.setPhotoCategoryNum(5); // 후기 아이템 사진은 5
                photoRepository.save(photoEntity);
            }
        }

        ReviewRegistResponseDTO response = new ReviewRegistResponseDTO();
        response.setNum(postNum);
        response.setTitle(registReviewPost.getTitle());
        response.setContent(registReviewPost.getContent());
        response.setItems(newPost.getItems());
        response.setMemberNum(registReviewPost.getMemberNum());
        return response;
    }

    private ReviewPostEntity changeToRegistPost(ReviewRegistRequestDTO newPost) {
        ReviewPostEntity reviewPostEntity = new ReviewPostEntity();
        reviewPostEntity.setTitle(newPost.getTitle());
        reviewPostEntity.setContent(newPost.getContent());
        reviewPostEntity.setGood(0);
        reviewPostEntity.setCheer(0);
        reviewPostEntity.setMemberNum(newPost.getMemberNum());
        reviewPostEntity.setReviewCategoryNum(newPost.getReviewCategoryNum());
        return reviewPostEntity;
    }


    @Transactional
    public ReviewModifyResponseDTO modifyPost(int postNum, ReviewModifyRequestDTO updatePost,
                                        List<MultipartFile> postFiles, List<MultipartFile> itemFiles) {
        ReviewPostEntity reviewPostEntity = reviewPostRepository.findById(postNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다. id=" + postNum));

        reviewPostEntity.setTitle(updatePost.getTitle());
        reviewPostEntity.setContent(updatePost.getContent());
        reviewPostEntity.setReviewCategoryNum(updatePost.getReviewCategoryNum());

        List<Integer> updateItems = updateItems(postNum, updatePost.getItems());

        updatePhotos(reviewPostEntity,this.postUploadPath, postFiles, 2);
        updatePhotos(reviewPostEntity,this.reviewItemUploadPath, itemFiles, 5);

        ReviewModifyResponseDTO response = new ReviewModifyResponseDTO();
        response.setNum(postNum);
        response.setTitle(reviewPostEntity.getTitle());
        response.setContent(reviewPostEntity.getContent());
        response.setMemberNum(reviewPostEntity.getMemberNum());
        response.setItems(updateItems);
        response.setReviewCategoryNum(reviewPostEntity.getReviewCategoryNum());
        return response;
    }

    private void updatePhotos(ReviewPostEntity post, String uploadPath, List<MultipartFile> newImageFiles, int categoryNum) {
        int postNum = post.getNum();
        List<PhotoEntity> photosToUpdate = photoRepository.findAllByPostNumAndPhotoCategoryNum(postNum, categoryNum);
        for (PhotoEntity photo : photosToUpdate) {
            File fileToDelete = new File(photo.getPath() + File.separator + photo.getName());
            boolean deleted = fileToDelete.delete();
            if (!deleted) {
                log.info("수정 중 사진 파일 삭제에 실패했습니다. {}", fileToDelete.getPath());  // 삭제 실패했다면 log로 남김
            }
        }
        photoRepository.deleteAll(photosToUpdate);

        if (newImageFiles != null && !newImageFiles.isEmpty()) {
            saveNewPhotos(post, uploadPath, newImageFiles, categoryNum);
        }
    }

    private void saveNewPhotos(ReviewPostEntity post, String uploadPath,
                               List<MultipartFile> imageFiles, int categoryNum) {
        int postNum = post.getNum();
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) { uploadDir.mkdirs(); }
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

    private List<Integer> updateItems(int postNum, List<Integer> newItemId) {
        List<Integer> currentItemsIds = reviewItemRepository.findAllByReviewPostItemPK_PostNum(postNum)
                .stream()
                .map(item -> item.getReviewPostItemPK().getItemNum())
                .collect(Collectors.toList());

        List<Integer> itemsToRemove = currentItemsIds.stream()
                .filter(id -> !newItemId.contains(id))
                .collect(Collectors.toList());
        if(!itemsToRemove.isEmpty()) {
            reviewItemRepository.deleteAllByReviewPostItemPK_PostNumAndReviewPostItemPK_ItemNumIn(postNum, itemsToRemove);
        }

        List<Integer> itemsToAdd = newItemId.stream()
                .filter(id -> !currentItemsIds.contains(id))
                .collect(Collectors.toList());
        for (Integer itemNum : itemsToAdd) {
            ReviewPostItemPK pk = new ReviewPostItemPK(postNum, itemNum);
            reviewItemRepository.save(new ReviewPostItemEntity(pk));
        }
        return newItemId;
    }

    @Transactional
    public void deletePost(int postNum) {
        ReviewPostEntity reviewToDelete = reviewPostRepository.findById(postNum)
                .orElseThrow(() ->new IllegalArgumentException("해당 게시글은 존재하지 않습니다."));

        deleteItems(postNum);

        List<PhotoEntity> photosToDelete = photoRepository.findAllByPostNumAndPhotoCategoryNum(postNum, 2);
        photosToDelete.addAll(photoRepository.findAllByPostNumAndPhotoCategoryNum(postNum, 5));
        for (PhotoEntity photo : photosToDelete) {
            File file = new File(photo.getPath() + File.separator + photo.getName());
            if(file.exists()) {
                boolean deleted = file.delete();
                if (!deleted) {
                    log.info("파일 삭제 중 이미지 파일 삭제에 실패했습니다");
                }
            }
        }
        photoRepository.deleteAll(photosToDelete);

        reviewPostRepository.deleteById(postNum);
    }

    private void deleteItems(int postNum) {
        reviewItemRepository.deleteAllByReviewPostItemPK_PostNum(postNum);
    }

    @Transactional
    public ReactionResponseDTO insertReact(int postNum, ReactionRequestDTO reaction) {
        if (reaction.getReactionType() == null ||
                !VALID_REACTION_TYPES.contains(reaction.getReactionType().toLowerCase())) {
            throw new IllegalArgumentException("좋아요/힘내요 요청이 아닙니다!");
        }
        String reactionType = reaction.getReactionType().toLowerCase();
        ReactionResponseDTO response = new ReactionResponseDTO();


        ReviewPostEntity post = reviewPostRepository.findById(postNum)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글을 찾을 수 없습니다."));

        PostReactionEntity reactionEntity = new PostReactionEntity();
        reactionEntity.setMemberNum(reaction.getMemberNum());
        reactionEntity.setReactionType(reaction.getReactionType());
        reactionEntity.setPostNum(postNum);
        reactionEntity.setPostCategoryNum(2);  // 후기 게시물은 2

        Optional<PostReactionEntity> oldReaction = postReactionRepository.findByMemberNumAndPostNumAndPostCategoryNum
                (reaction.getMemberNum(), 2, postNum);

        if (oldReaction.isPresent()) {
            PostReactionEntity existingReaction = oldReaction.get();
            if (existingReaction.getReactionType().equalsIgnoreCase(reactionEntity.getReactionType())) {
                postReactionRepository.delete(existingReaction);

                switch (reactionType) {
                    case "good":
                        post.setGood(post.getGood() - 1);
                        break;
                    case "cheer":
                        post.setCheer(post.getCheer() - 1);
                        break;
                }
            } else {
                PostReactionEntity newReaction = postReactionRepository.save(reactionEntity);
                switch (reactionType) {
                    case "good":
                        post.setGood(post.getGood() + 1);
                        break;
                    case "cheer":
                        post.setCheer(post.getCheer() + 1);
                        break;
                }
            }
            PostReactionEntity newReaction = postReactionRepository.save(reactionEntity);

            ReviewPostEntity updateReactionPost = reviewPostRepository.save(post);
            response.setPostNum(postNum);
            response.setReactionType(reactionType);
            response.setMemberNum(reaction.getMemberNum());
            response.setPostCategoryNum(2);
        } else {
            PostReactionEntity newReaction = new PostReactionEntity();
            newReaction.setMemberNum(reaction.getMemberNum());
            newReaction.setReactionType(reaction.getReactionType());
            newReaction.setPostNum(postNum);
            newReaction.setPostCategoryNum(2);  // 후기 게시물은 2

            PostReactionEntity finalReactionEntity = postReactionRepository.save(newReaction);
            if (reactionType.equals("good")) post.setGood(post.getGood() + 1);
            else post.setCheer(post.getCheer() + 1);

            response.setPostNum(postNum);
            response.setReactionType(reactionType);
            response.setMemberNum(reaction.getMemberNum());
            response.setPostCategoryNum(2);

        }
        return response;
    }

    public List<SelectAllReviewPostDTO> getPostListByPage(Criteria criteria) {
        log.info("Criteria 설정만큼 List 갖고 오기: " + criteria);
        return reviewPostMapper.getListWithPaging(criteria);
    }

    public int getTotal() {
        log.info("get total count");
        return reviewPostMapper.getTotalCount();
    }
}
