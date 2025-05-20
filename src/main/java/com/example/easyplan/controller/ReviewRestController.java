package com.example.easyplan.controller;


import com.example.easyplan.domain.entity.Review.Review;
import com.example.easyplan.domain.entity.Review.ReviewRequestDto;
import com.example.easyplan.domain.entity.Review.ReviewResponseDto;
import com.example.easyplan.security.CustomOAuth2User;
import com.example.easyplan.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewRestController {
    private final ReviewService reviewService;

    @GetMapping("/api/mypage/reviews")
    public ResponseEntity<Page<ReviewResponseDto>> getMyReviews
            (@RequestParam(value = "page", defaultValue = "0") int page){
        log.info("mypageReviews");
        Long userId = getUserId();

        Pageable pageable = PageRequest.of(page, 6, Sort.by(Sort.Direction.DESC, "createdDate"));
        Page<ReviewResponseDto> reviews = reviewService.getReviews(userId, pageable);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/api/reviews/new")
    public ResponseEntity<Long> createReview(@RequestBody ReviewRequestDto reviewRequestDto) {
        Long userId = getUserId();
        Long newReviewId = reviewService.saveReview(reviewRequestDto, userId);
        return ResponseEntity.ok(newReviewId);
    }

    @GetMapping("/api/reviews/{reviewId}")
    public ResponseEntity<ReviewResponseDto> getReviewDetails(@PathVariable Long reviewId){
        Review getReview = reviewService.getReviewById(reviewId);
        ReviewResponseDto responseDto = ReviewResponseDto.from(getReview);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/api/reviews/edit/{reviewId}")
    public ResponseEntity<Void> editReview(@PathVariable Long reviewId, @RequestBody ReviewRequestDto reviewRequestDto){
        Long userId = getUserId();
        reviewService.updateReview(reviewRequestDto, userId, reviewId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/api/reviews/delete/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId){
        Long userId = getUserId();
        reviewService.deleteReview(userId, reviewId);
        return ResponseEntity.ok().build();
    }

    private Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        return user.getUser().getId();
    }
}
