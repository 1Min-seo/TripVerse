//package com.example.easyplan.domain.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.data.domain.AuditorAware;
//import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//
//import java.util.Optional;
//
//@Configuration
//@EnableJpaAuditing
//public class JpaConfig {
//
//    // AuditorAware Bean (로그인한 사용자 정보를 반환)
//    @Bean
//    public AuditorAware<String> auditorProvider() {
//        return () -> {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//            if (authentication != null && authentication.isAuthenticated()) {
//                return Optional.ofNullable(authentication.getName());
//            }
//
//            return Optional.of("anonymousUser");
//        };
//    }
//}
