package com.example.easyplan.config;

public class RedisKeyUtil {

    public static String heartCount(Long reviewId) {
        return "heart:count:" + reviewId;
    }

    public static String userHeartHash(Long userId) {
        return "heart:user:" + userId + ":hash";
    }

    public static String userHeartZSet(Long userId) {
        return "heart:user:" + userId + ":zset";
    }

    public static String reviewFieldForZSet(Long reviewId) {
        return "review:" + reviewId + ":zset";
    }

}
