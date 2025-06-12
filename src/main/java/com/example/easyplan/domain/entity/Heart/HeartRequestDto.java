package com.example.easyplan.domain.entity.Heart;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HeartRequestDto {
    private Long userId;
    private Long reviewId;
    private boolean isLiked;
    private int likeCount;
}
