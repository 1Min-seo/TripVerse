package com.example.easyplan.service;

import com.example.easyplan.domain.entity.Review.Review;
import com.example.easyplan.domain.entity.Review.ReviewRequestDto;
import com.example.easyplan.domain.entity.Review.ReviewResponseDto;
import com.example.easyplan.domain.entity.User.User;
import com.example.easyplan.repository.ReviewRepository;
import com.example.easyplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
public class ReviewService {
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final S3UploadService s3UploadService;

    @Transactional
    public Long saveReview(ReviewRequestDto dto, Long userId) {
        try {
            List<String> fileUrls = dto.getPhotos() != null ? dto.getPhotos() : new ArrayList<>();
            log.info("이미지 url: "+ fileUrls);

            User user = getUserById(userId);
            Review review = Review.createReview(user, dto.getTitle(), dto.getContent(), dto.getRating(), fileUrls);
            reviewRepository.save(review);

            return review.getId();
        } catch (Exception e) {
            log.error("Error during review saving: {}", e.getMessage());
            // 업로드한 파일들 삭제
            //s3UploadService.deleteFileFromUrls(fileUrls); // 업로드된 URL 리스트를 전달
        }
        return userId;
    }

    @Transactional
    public void updateReview(ReviewRequestDto dto, Long userId, Long reviewId) {
        User user = getUserById(userId);
        Review review = getReviewById(reviewId);
        validateReviewOwner(user, review);

        review.updateReview(dto.getTitle(), dto.getContent(), dto.getRating(), dto.getPhotos());
    }

    @Transactional
    public void deleteReview(Long userId, Long reviewId) {
        User user = getUserById(userId);
        Review review = getReviewById(reviewId);
        validateReviewOwner(user, review);

        reviewRepository.delete(review);
        log.info("Review delete: userId={}, reviewId{}", userId, reviewId);
    }

    //View all reviews of a particular user
    public Page<ReviewResponseDto> getReviews(Long userId, Pageable pageable) {
        getUserById(userId);

        return reviewRepository.findByUserId(userId, pageable)
                .map(ReviewResponseDto::from);

    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
    }

    //validate owner review
    private void validateReviewOwner(User user, Review review) {
        if(!user.getId().equals(review.getUser().getId())) {
            throw new IllegalArgumentException("Review owner is not the user");
        }
    }

}
