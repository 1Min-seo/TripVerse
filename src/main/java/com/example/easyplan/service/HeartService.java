package com.example.easyplan.service;

import com.example.easyplan.config.RedisKeyUtil;
import com.example.easyplan.domain.entity.Heart.Heart;
import com.example.easyplan.domain.entity.Heart.HeartResponseDto;
import com.example.easyplan.domain.entity.Review.Review;
import com.example.easyplan.domain.entity.Review.ReviewResponseDto;
import com.example.easyplan.domain.entity.User.User;
import com.example.easyplan.repository.HeartRepository;
import com.example.easyplan.repository.ReviewRepository;
import com.example.easyplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
@EnableCaching(proxyTargetClass = true)
public class HeartService {

    private final HeartRepository heartRepository;
    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    public HeartResponseDto getHeartStatus(Long userId, Long reviewId) {
        boolean isLiked = heartRepository.existsByUserIdAndReviewId(userId, reviewId);
        int heartCount = heartRepository.countByReviewId(reviewId);
        return new HeartResponseDto(reviewId, isLiked, heartCount);
    }

    // 좋아요 수 가져오기 (리뷰 기준 캐싱)
    @Cacheable(cacheNames = "likeCount", key = "'review:like:' + #reviewId", cacheManager = "likeCacheManager")
    public Integer getLikedCount(Long reviewId) {
        return heartRepository.countByReviewId(reviewId);
    }

    // 좋아요 수 TOP N 리뷰 가져오기
    @Cacheable(cacheNames = "topLikedReviews", key = "'top:' + #limit", cacheManager = "likeCacheManager")
    public List<ReviewResponseDto> getTopLikedReviews(int limit) {
        List<Review> reviews = heartRepository.findTopLikedReviews(PageRequest.of(0, limit));
        return reviews.stream()
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());
    }

    //좋아요 누른 게시글 가져오기
    public List<ReviewResponseDto> getLikedReviews(Long userId, int page) {
        String redisKey = "heart:user:" + userId + ":zset";
        int pageSize = 6;
        int start = page * pageSize;
        int end = start + pageSize - 1;

        System.out.println("0");
        //최근에 좋아요 누른게 앞으로 오게 정렬
        Set<Object> likedReviewObjects = redisTemplate.opsForZSet()
                .reverseRange(redisKey, start, end);

        System.out.println("1");

        if(likedReviewObjects.isEmpty() || likedReviewObjects == null) {
            return Collections.emptyList();
        }
        System.out.println("2");
        List<Long> reviewIds = likedReviewObjects.stream()
                .map(Object::toString)
                .map(Long::parseLong)
                .collect(Collectors.toList());

        System.out.println("reviewIds의 갯수: "+ reviewIds.size());
        List<Review> reviews = reviewRepository.findAllById(reviewIds);
        Map<Long, Review> reviewMap = reviews.stream()
                .collect(Collectors.toMap(Review::getId, r -> r));

        List<ReviewResponseDto> result = reviewIds.stream()
                .map(reviewMap::get)
                .map(ReviewResponseDto::from)
                .collect(Collectors.toList());

        return result;
    }


    @Transactional
    @CacheEvict(cacheNames = "likeCount", key = "'heart:count:' + #reviewId", cacheManager = "likeCacheManager")
    public HeartResponseDto toggleHeart(Long userId, Long reviewId) {
        User user = getUserById(userId);
        Review review = getReviewById(reviewId);
        boolean isLiked;

        Optional<Heart> existingHeart = heartRepository.findByUserAndReview(user, review);

        String redisCountKey = RedisKeyUtil.heartCount(reviewId);
        String redisUserKey = RedisKeyUtil.userHeartHash(userId);
        String redisZsetKey = RedisKeyUtil.userHeartZSet(userId);
        String reviewField = RedisKeyUtil.reviewFieldForZSet(reviewId);

        if (existingHeart.isPresent()) {
            heartRepository.delete(existingHeart.get());

            redisTemplate.opsForHash().delete(redisUserKey, reviewField);
            redisTemplate.opsForZSet().remove(redisZsetKey, reviewId);
            redisTemplate.opsForValue().decrement(redisCountKey);

            isLiked = false;
            log.info("좋아요 취소 완료");
        } else {
            Heart heart = new Heart(user, review);
            heartRepository.save(heart);

            redisTemplate.opsForHash().put(redisUserKey, reviewField, true);
            redisTemplate.opsForZSet().add(redisZsetKey, reviewId, System.currentTimeMillis());
            redisTemplate.opsForValue().increment(redisCountKey);

            isLiked = true;
            log.info("좋아요 등록 완료");
        }

        // 캐시 존재 여부 확인 후 없으면 DB에서 조회
        Integer likeCount = (Integer) redisTemplate.opsForValue().get(redisCountKey);
        if (likeCount == null) {
            likeCount = heartRepository.countByReviewId(reviewId);
            redisTemplate.opsForValue().set(redisCountKey, likeCount, Duration.ofHours(1));
        }

        return new HeartResponseDto(reviewId, isLiked, (int)likeCount);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private Review getReviewById(Long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new IllegalArgumentException("Review not found"));
    }
}