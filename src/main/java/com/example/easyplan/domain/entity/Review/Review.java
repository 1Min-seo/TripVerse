package com.example.easyplan.domain.entity.Review;

import com.example.easyplan.domain.entity.BaseEntity;
import com.example.easyplan.domain.entity.Heart.Heart;
import com.example.easyplan.domain.entity.User.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;


import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@Getter
public class Review extends BaseEntity {
    @Id
    @Column(name = "review_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ElementCollection
    @CollectionTable(name = "review_images", joinColumns = @JoinColumn(name = "review_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column(nullable = false)
    private int rating;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "review")
    private List<Heart> hearts = new ArrayList<>();

    private Review(User user, String title, String content, int rating, List<String> imageUrls) {
        this.user = user;
        this.title = title;
        this.content = content;
        this.rating = rating;
        this.imageUrls = imageUrls;
    }

    public static Review createReview(User user, String title, String content, int rating, List<String> imageUrls) {
        validateTitle(title);
        validateContent(content);
        validateRating(rating);

        return new Review(user, title, content, rating, imageUrls);
    }

    public void updateReview(String title, String content, int rating, List<String> imageUrls) {
        validateTitle(title);
        validateContent(content);
        validateRating(rating);

        this.title = title;
        this.content = content;
        this.rating = rating;
        this.imageUrls = imageUrls != null ? imageUrls : new ArrayList<>();
    }

    //validate
    private static void validateTitle(String title){
        if(title == null || title.isEmpty()){
            throw new IllegalArgumentException("Please enter the title");
        }
    }
    private static void validateContent(String content){
        if(content == null || content.isEmpty()){
            throw new IllegalArgumentException("Please enter the content");
        }
    }
    private static void validateRating(int rating){
        if(rating < 0 || rating > 5){
            throw new IllegalArgumentException("Please enter a valid rating");
        }
    }

}
