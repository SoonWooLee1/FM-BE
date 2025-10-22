package fashionmanager.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import fashionmanager.dto.BadgeAssignRequest;
import fashionmanager.dto.UserDTO;
import fashionmanager.service.BadgeService;
import fashionmanager.service.UserService;
import fashionmanager.repository.BadgeRepository;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/menu")
public class BadgeController {

    private final BadgeService badgeService;
    private final UserService userService; // 유저 조회용
    private final BadgeRepository badgeRepository;

    public BadgeController(BadgeService badgeService,
                           UserService userService,
                           BadgeRepository badgeRepository) {
        this.badgeService = badgeService;
        this.userService = userService;
        this.badgeRepository = badgeRepository;
    }


    // 회원에게 칭호 등록

    @PostMapping("/badges/assign")
    public ResponseEntity<UserDTO> assignBadge(@RequestBody BadgeAssignRequest request) {
        UserDTO updatedUser = badgeService.assignBadge(request.getUserNum(), request.getBadgeNum());
        return ResponseEntity.ok(updatedUser);
    }



    // 회원에게서 칭호 삭제

    @DeleteMapping("/badges/remove")
    public ResponseEntity<Map<String, String>>  removeBadge(@RequestBody BadgeAssignRequest request) {
        badgeService.removeBadge(request.getUserNum(), request.getBadgeNum());

        Map<String, String> response = new HashMap<>();
        response.put("message", "뱃지가 삭제되었습니다.");


        return ResponseEntity.ok(response);
    }
}