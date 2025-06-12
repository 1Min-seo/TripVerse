package com.example.easyplan.domain.entity.Heart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HeartResponseDto {
    private Long userId;
    private Long reviewId;
    private Boolean isLiked;
    private int heartCount;

    public static HeartResponseDto from(Heart heart, boolean isLiked, int heartCount) {
        return new HeartResponseDto(
                heart.getUser().getId(),
                heart.getReview().getId(),
                isLiked,
                heartCount
        );
    }

}
