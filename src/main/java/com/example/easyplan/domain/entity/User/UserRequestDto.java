package com.example.easyplan.domain.entity.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDto {
    private String email;
    private String name;
    private String profileImageUrl;
}
