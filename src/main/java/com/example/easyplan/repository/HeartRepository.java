package com.example.easyplan.repository;

import com.example.easyplan.domain.entity.Heart.Heart;
import com.example.easyplan.domain.entity.Review.Review;
import com.example.easyplan.domain.entity.User.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HeartRepository extends JpaRepository<Heart, Long> {
    int countByReviewId(Long reviewId);

    @Query("SELECT h.review FROM Heart h GROUP BY h.review ORDER BY COUNT(h.id) DESC")
    List<Review> findTopLikedReviews(Pageable pageable);

    List<Heart> findByUser(User user);
}
