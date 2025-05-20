package com.example.easyplan.security;

import com.example.easyplan.domain.entity.User.User;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collections;
import java.util.Map;

public class CustomOAuth2User extends DefaultOAuth2User {

    private final User user;

    public CustomOAuth2User(User user, Map<String, Object> attributes) {
        super(
                Collections.singleton(new SimpleGrantedAuthority(user.getRole().toString())),
                attributes,
                "name"
        );
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}