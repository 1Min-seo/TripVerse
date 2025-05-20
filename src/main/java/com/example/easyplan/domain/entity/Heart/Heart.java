package com.example.easyplan.domain.entity.Heart;

import com.example.easyplan.domain.entity.BaseEntity;
import com.example.easyplan.domain.entity.Review.Review;
import com.example.easyplan.domain.entity.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;


@Entity
@RequiredArgsConstructor
@Getter @Setter
public class Heart extends BaseEntity {
    @Id @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    public Heart(User user, Review review) {
        this.user = user;
        this.review = review;
    }


}
