package fashionmanager.controller;

import fashionmanager.service.FashionPostService;
import fashionmanager.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/posts/fashion")
public class FashionPostController {
    private final FashionPostService fashionPostService;

    @Autowired
    public FashionPostController(FashionPostService fashionPostService) {
        this.fashionPostService = fashionPostService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<SelectAllFashionPostDTO>> getPostList() {
        List<SelectAllFashionPostDTO> response = fashionPostService.getPostList();
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPostListByPage(Criteria criteria) {
        List<SelectAllFashionPostDTO> list = fashionPostService.getPostListByPage(criteria);
        int total = fashionPostService.getTotal();

        PageDTO pageMaker = new PageDTO(criteria, total);
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("pageMaker", pageMaker);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{postNum}")
    public ResponseEntity<SelectDetailFashionPostDTO> getDetailPost(@PathVariable int postNum) {

        SelectDetailFashionPostDTO response = fashionPostService.getDetailPost(postNum);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<FashionRegistResponseDTO> registPost
            (@RequestPart("newPost") FashionRegistRequestDTO newPost,
             @RequestPart(value = "postImages", required = false) List<MultipartFile> postFiles,
             @RequestPart(value = "itemImages", required = false) List<MultipartFile> itemFiles) {
        // 사진은 Nullable하므로 required = false // postService 결정

        FashionRegistResponseDTO response = fashionPostService.registPost(newPost, postFiles, itemFiles); // method 작동

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{postNum}")
    public ResponseEntity<FashionModifyResponseDTO> modifyPost
            (@PathVariable int postNum,
             @RequestPart("modifyPost") FashionModifyRequestDTO modifyRequestDTO,
             @RequestPart(value = "postImages", required = false) List<MultipartFile> postFiles,
             @RequestPart(value = "itemImages", required = false) List<MultipartFile> itemFiles) {

        FashionModifyResponseDTO response = fashionPostService.modifyPost(postNum, modifyRequestDTO, postFiles, itemFiles);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/react/{postNum}")
    public ResponseEntity<ReactionResponseDTO> insertReaction(@PathVariable int postNum,
                        @RequestBody ReactionRequestDTO reactionRequestDTO){
        ReactionResponseDTO responseReaction = fashionPostService.insertReact(postNum, reactionRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseReaction);
    }

    @DeleteMapping("/{postNum}")
    public ResponseEntity<String> deletePost(@PathVariable int postNum) {

        fashionPostService.deletePost(postNum);

        return ResponseEntity.ok("게시글이 성공적으로 삭제됐습니다.");
    }

    @ExceptionHandler
    public ResponseEntity<String> handleException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
}
