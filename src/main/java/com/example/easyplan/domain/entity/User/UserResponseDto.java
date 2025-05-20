package com.example.easyplan.domain.entity.User;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponseDto {
    private String email;
    private String name;
    private String profileImageUrl;
}
