package com.example.easyplan.controller;

import com.example.easyplan.domain.entity.User.User;
import com.example.easyplan.domain.entity.User.UserResponseDto;
import com.example.easyplan.repository.UserRepository;
import com.example.easyplan.security.CustomOAuth2User;
import com.example.easyplan.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Slf4j
@RequiredArgsConstructor
public class LoginController {
//    private final UserRepository userRepository;
//    private final UserService userService;

//    public LoginController(UserRepository userRepository, UserService userService) {
//        this.userRepository = userRepository;
//        this.userService = userService;
//    }

    @GetMapping("/api/test")
    public ResponseEntity<String> test(Authentication authentication) {
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal();
        log.info("인증 객체: "+user);
        return ResponseEntity.ok(user.getUser().getName());
    }

    @PostMapping("/api/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null){
            new SecurityContextLogoutHandler().logout(request, response, authentication);
        }

        // accessToken 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("accessToken", null);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setHttpOnly(true);
        response.addCookie(accessTokenCookie);

        return ResponseEntity.ok("로그아웃 성공");
    }

    @GetMapping("/api/user/info")
    public ResponseEntity<UserResponseDto> getUserInfo(Authentication authentication) {
        CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();
        User user = oAuth2User.getUser();

        UserResponseDto response = UserResponseDto.builder()
                .name(user.getName())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .build();

        return ResponseEntity.ok(response);
    }
}