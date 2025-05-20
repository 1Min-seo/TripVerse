package com.example.easyplan.service;

import com.example.easyplan.domain.entity.Heart.Heart;
import com.example.easyplan.domain.entity.Review.Review;
import com.example.easyplan.domain.entity.Review.ReviewResponseDto;
import com.example.easyplan.domain.entity.User.User;
import com.example.easyplan.repository.HeartRepository;
import com.example.easyplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class HeartService {

    private final HeartRepository heartRepository;
    private Pageable pageable;
    private final UserRepository userRepository;

    //좋아요 수 가져오기
    @Cacheable(cacheNames = "likeCount", key = "'review:like:' + #reviewId", cacheManager = "likeCacheManager")
    public int getLikeCount(Long reviewId) {
        int heartCount = heartRepository.countByReviewId(reviewId);
        return heartCount;
    }

    //좋아요 TOP N개 가져오기
    @Cacheable(cacheNames = "topLikedReviews", key = "'top:' + #limit", cacheManager = "likeCacheManager")
    public List<ReviewResponseDto> getTopLikedReviews(int limit) {
        List<Review> reviews = heartRepository.findTopLikedReviews(PageRequest.of(0, limit));
        return reviews.stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    //내가 좋아요 누른 게시글 가져오기
    public List<ReviewResponseDto> getLikedReviews(Long userId) {
        User user = getUserById(userId);
        List<Heart> hearts = heartRepository.findByUser(user);
        return hearts.stream()
                .map(Heart::getReview)
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

}
