package com.example.easyplan.controller;

import com.example.easyplan.domain.entity.Heart.HeartRequestDto;
import com.example.easyplan.domain.entity.Heart.HeartResponseDto;
import com.example.easyplan.domain.entity.Review.ReviewResponseDto;
import com.example.easyplan.security.CustomOAuth2User;
import com.example.easyplan.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class HeartRestController {
    private final HeartService heartService;

    @GetMapping("/hearts")
    public ResponseEntity<HeartResponseDto> getHeartStatus(@RequestParam Long reviewId) {
        Long userId = getUserId();
        HeartResponseDto responseDto = heartService.getHeartStatus(userId, reviewId);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/hearts/toggle")
    public ResponseEntity<HeartResponseDto> getHeartStatus(@RequestBody HeartRequestDto heartRequestDto) {
        Long userId = getUserId();
        HeartResponseDto responseDto = heartService.toggleHeart(userId, heartRequestDto.getReviewId());

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/reviews/likes/top")
    public ResponseEntity<List<ReviewResponseDto>> getTopLikedReviews(@RequestParam(defaultValue = "6") int limit) {
        List<ReviewResponseDto> topReviews = heartService.getTopLikedReviews(limit);

        return ResponseEntity.ok(topReviews);
    }

    @GetMapping("/mypage/likes")
    public ResponseEntity<List<ReviewResponseDto>> getLikedReviews(
            @RequestParam(value = "page", defaultValue = "0") int page) {

        System.out.println("내가 좋아요 누른 리뷰 요청 들어옴");
        Long userId = getUserId();
        List<ReviewResponseDto> likedReviews = heartService.getLikedReviews(userId, page);
        System.out.println("좋아요 누른 개수 controller: " + likedReviews.size());
        for(ReviewResponseDto review : likedReviews) {
            System.out.println(review.getTitle());
        }
        return ResponseEntity.ok(likedReviews);
    }

    @GetMapping("/reviews/{reviewId}/like-count")
    public ResponseEntity<Integer> getLikedCount(@PathVariable Long reviewId) {
        Integer count = heartService.getLikedCount(reviewId);
        return ResponseEntity.ok(count);
    }

    private Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        return user.getUser().getId();
    }
}
