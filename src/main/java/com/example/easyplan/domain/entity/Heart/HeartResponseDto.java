package com.example.easyplan.domain.entity.Heart;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class HeartResponseDto {
    //private Long userId;
    private Long reviewId;
    private Boolean isLiked;
    private int heartCount;

    public HeartResponseDto (Long reviewId, boolean isLiked, int heartCount) {
        this.reviewId = reviewId;
        this.isLiked = isLiked;
        this.heartCount = heartCount;
    }

}
