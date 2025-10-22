package fashionmanager.develop.service;

import fashionmanager.dto.SelectAllFashionPostDTO;
import fashionmanager.service.FashionPostService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class FashionPostServiceTest {

    @Autowired
    private FashionPostService fashionPostService;

    @DisplayName("패션 게시판 조회 테스트")
    @Test
    void testSelectFashionPost() {
        Assertions.assertDoesNotThrow(
                () -> {
                    List<SelectAllFashionPostDTO> fashionPosts = fashionPostService.getPostList();
                    fashionPosts.forEach(System.out::println);
                }
        );
    }

}