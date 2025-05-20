//package com.example.easyplan.domain.entity.Scrap;
//
//import com.example.easyplan.domain.entity.Review.Review;
//import com.example.easyplan.domain.entity.User.User;
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.RequiredArgsConstructor;
//import org.hibernate.annotations.CreationTimestamp;
//
//import java.time.LocalDateTime;
//
//@Entity
//@RequiredArgsConstructor
//@AllArgsConstructor
//@Getter
//public class Scrap {
//
//    @Id
//    @Column(name="scrap_id", updatable=false)
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name="user_id", nullable=false)
//    private User user;
//
//    @ManyToOne
//    @JoinColumn(name="review_id", nullable=false)
//    private Review review;
//
//    @Column(updatable=false)
//    @CreationTimestamp
//    private LocalDateTime createDate;
//
//    public Scrap(User user, Review review){
//        this.user = user;
//        this.review = review;
//    }
//
//    public void setUser(User user){
//        this.user = user;
//    }
//
//    public void setReview(Review review){
//        this.review=review;
//    }
//
//}
