package com.example.easyplan.domain.entity.Review;

import com.example.easyplan.domain.entity.BaseEntity;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewResponseDto {
    private Long id;
    private String title;
    private String content;
    private int rating;
    private List<String> imageUrls;
    private String userName;

    private LocalDateTime createdDate;


    //Review => ReviewResponseDto
    public static ReviewResponseDto from(Review review) {
        return new ReviewResponseDto(
                review.getId(),
                review.getTitle(),
                review.getContent(),
                review.getRating(),
                review.getImageUrls(),
                review.getUser().getName(),
                review.getCreatedDate()
        );
    }


}
