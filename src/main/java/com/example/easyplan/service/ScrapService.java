//package com.example.easyplan.service;
//
//import com.example.easyplan.domain.entity.Review.Review;
//import com.example.easyplan.domain.entity.Scrap.Scrap;
//import com.example.easyplan.domain.entity.User.User;
//import com.example.easyplan.repository.ReviewRepository;
//import com.example.easyplan.repository.ScrapRepository;
//import com.example.easyplan.repository.UserRepository;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.PageImpl;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.time.Duration;
//import java.util.List;
//import java.util.Set;
//import java.util.stream.Collectors;
//
//@Service
//@RequiredArgsConstructor
//@Transactional(readOnly = true)
//@Slf4j
//public class ScrapService {
//    private final UserRepository userRepository;
//    private final ReviewRepository reviewRepository;
//    private final ScrapRepository scrapRepository;
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    private static final String SCRAP_KEY_PREFIX= "user:scrap:";
//
//    public String generateKey(Long userId){
//        return SCRAP_KEY_PREFIX + userId.toString();
//    }
//
//    @Transactional
//    public void addScrap(Long userId, Long reviewId){
//        String userScrapKey=generateKey(userId);
//
//        User user=userRepository.findById(userId)
//                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
//
//        Review review= reviewRepository.findById(reviewId)
//                .orElseThrow(()-> new IllegalArgumentException("리뷰를 찾을 수 없습니다."));
//
//
//        Boolean alreadyScrapped=redisTemplate.opsForZSet().score(userScrapKey, reviewId) !=null;
//
//        if(alreadyScrapped){
//            Scrap existingScrap=scrapRepository.findByUserAndReview(user, review)
//                            .orElseThrow(()->new IllegalStateException("삭제할 스크랩을 찾을 수 없습니다."));
//
//            scrapRepository.delete(existingScrap);
//            redisTemplate.opsForZSet().remove(userScrapKey, reviewId);
//            log.info("사용자 {}가 리뷰 {}를 스크랩 실패", userId, reviewId);
//        }else{
//            Scrap scrap=new Scrap(user, review);
//            scrapRepository.save(scrap);
//            redisTemplate.opsForZSet().add(userScrapKey, reviewId, System.currentTimeMillis());
//
//            log.info("사용자 {}가 리뷰 {}를 스크랩", userId, reviewId);
//        }
//    }
//
//    public Boolean isScrapped(Long userId, Long reviewId){
//        String redisKey=generateKey(userId);
//
//        Double score=redisTemplate.opsForZSet().score(redisKey, reviewId);
//        if(score!=null){
//            return true;
//        }
//
//        Boolean existsInDB=scrapRepository.existsByUserIdAndReviewId(userId, reviewId);
//
//        if(existsInDB){
//            redisTemplate.opsForZSet().add(redisKey, reviewId, System.currentTimeMillis());
//            redisTemplate.expire(redisKey, Duration.ofDays(7));
//        }
//
//        return existsInDB;
//    }
//
//    public Page<Long> getScrappedReviews(Long userId, Pageable pageable){
//        String userScrapKey=generateKey(userId);
//        Set<Object> likedReviewIds=redisTemplate.opsForZSet().reverseRange(userScrapKey,
//                pageable.getOffset(), pageable.getOffset()+ pageable.getPageSize()-1);
//
//        if(likedReviewIds==null || likedReviewIds.isEmpty()){
//            return Page.empty(pageable);
//        }
//
//        List<Long> pagedScrappedReviewIds= likedReviewIds.stream()
//                .map(id->Long.valueOf(id.toString()))
//                .collect(Collectors.toList());
//
//
//        return new PageImpl<>(pagedScrappedReviewIds, pageable, likedReviewIds.size());
//    }
//
//}
