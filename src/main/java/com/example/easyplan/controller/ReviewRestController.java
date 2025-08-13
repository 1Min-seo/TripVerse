package com.example.easyplan.controller;


import com.example.easyplan.domain.entity.Review.Review;
import com.example.easyplan.domain.entity.Review.ReviewRequestDto;
import com.example.easyplan.domain.entity.Review.ReviewResponseDto;
import com.example.easyplan.security.CustomOAuth2User;
import com.example.easyplan.service.ReviewService;
import com.example.easyplan.service.S3UploadService;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReviewRestController {
    private final ReviewService reviewService;
    private final S3UploadService s3UploadService;

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

    @PostMapping("/api/upload/image")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }

        try {
            String imageUrl = s3UploadService.uploadFile(file);
            log.info("S3 Upload Success: " + imageUrl);

            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            return ResponseEntity.ok(response);

        } catch (IOException e) {
            log.error("Image upload failed", e);
            return ResponseEntity.status(500).body(Map.of("error", "Image upload failed: " + e.getMessage()));
        }
    }

    private Long getUserId(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        return user.getUser().getId();
    }
}
