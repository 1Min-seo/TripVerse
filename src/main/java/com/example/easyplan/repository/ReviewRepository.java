package com.example.easyplan.repository;

import com.example.easyplan.domain.entity.Review.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    public List<Review> findReviewsByUserId(Long userId);

    public Review findByIdAndUserId(Long reviewId, Long userId);

    public List<Review> findAllById(Long reviewId);

    public Page<Review> findByUserId(Long userId, Pageable pageable);

}
