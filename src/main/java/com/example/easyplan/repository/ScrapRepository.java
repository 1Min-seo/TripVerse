//package com.example.easyplan.repository;
//
//import com.example.easyplan.domain.entity.Review.Review;
//import com.example.easyplan.domain.entity.Scrap.Scrap;
//import com.example.easyplan.domain.entity.User.User;
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
//
//import java.util.Optional;
//
//@Repository
//public interface ScrapRepository extends JpaRepository<Scrap, Long> {
//    Optional<Scrap> findByUserAndReview(User user, Review review);
//
//    Boolean existsByUserIdAndReviewId(Long userId, Long reviewId);
//}
