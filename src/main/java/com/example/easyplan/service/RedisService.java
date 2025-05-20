//package com.example.easyplan.service;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//public class RedisService {
//    private final RedisTemplate<String, Object> redisTemplate;
//
//    //게시물 좋아요 수 증가
//    public void incrementPostLike(String postId) {
//        redisTemplate.opsForZSet().incrementScore("post:like", postId, 1);
//    }
//
//    //게시물 좋아요 감소
//    public void decrementPostLike(String postId) {
//        redisTemplate.opsForZSet().incrementScore("post:like", postId, -1);
//    }
//
//    //유저가 누른 좋아요 저장
//    public void addUserLike(Long userId, String postId) {
//        redisTemplate.opsForSet().add("user:like:" + userId, postId);
//    }
//
//    //유저가 누른 좋아요 삭제
//    public void removeUserLike(Long userId, String postId) {
//        redisTemplate.opsForSet().remove("user:like:" + userId, postId);
//    }
//
//    //유저가 좋아요 누른 게시물 목록
//    public Object getUserLike(Long userId) {
//        return redisTemplate.opsForSet().members("user:like:" + userId);
//    }
//
//    //좋아요 상위 게시물 조회 (Top N)
//}
