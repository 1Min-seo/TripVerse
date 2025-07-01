package com.example.easyplan.service;

import com.example.easyplan.domain.entity.Preference.TravelPreferenceRequestDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    @Value("${gcp.service-account-key-path}")
    private String serviceAccountPath;

    @Value("${gcp.gemini-api-url}")
    private String geminiApiUrl;

    private final ObjectMapper objectMapper;

    public String getTravelRecommendation(TravelPreferenceRequestDto dto) throws IOException {
        try {
            String prompt = buildPrompt(dto);
            String accessToken = getAccessToken();

            Map<String, Object> requestBody = Map.of(
                    "contents", List.of(
                            Map.of(
                                    "role", "user",
                                    "parts", List.of(
                                            Map.of("text", prompt)
                                    )
                            )
                    ),
                    "generationConfig", Map.of(
                            "temperature", 0.7,
                            "topP", 0.8,
                            "topK", 40,
                            "maxOutputTokens", 8192
                    ),
                    "safetySettings", List.of(
                            Map.of("category", "HARM_CATEGORY_HARASSMENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                            Map.of("category", "HARM_CATEGORY_HATE_SPEECH", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                            Map.of("category", "HARM_CATEGORY_SEXUALLY_EXPLICIT", "threshold", "BLOCK_MEDIUM_AND_ABOVE"),
                            Map.of("category", "HARM_CATEGORY_DANGEROUS_CONTENT", "threshold", "BLOCK_MEDIUM_AND_ABOVE")
                    )
            );

            log.info("요청 URL: {}", geminiApiUrl);
            log.info("요청 본문: {}", objectMapper.writeValueAsString(requestBody));

            WebClient webClient = WebClient.builder()
                    .baseUrl(geminiApiUrl)
                    .defaultHeader("Authorization", "Bearer " + accessToken)
                    .defaultHeader("Content-Type", "application/json")
                    .build();

            String response = webClient.post()
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            log.info("응답: {}", response);
            return parseResponse(response);

        } catch (WebClientResponseException e) {
            log.error("Gemini API 호출 실패 - 상태 코드: {}, 응답 본문: {}",
                    e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("AI 서비스 호출 실패: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생", e);
            throw new RuntimeException("AI 서비스 처리 중 오류 발생: " + e.getMessage(), e);
        }
    }

    private String parseResponse(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);

            if (rootNode.has("candidates") && rootNode.get("candidates").isArray()
                    && rootNode.get("candidates").size() > 0) {

                JsonNode candidate = rootNode.get("candidates").get(0);
                if (candidate.has("content") && candidate.get("content").has("parts")
                        && candidate.get("content").get("parts").isArray()
                        && candidate.get("content").get("parts").size() > 0) {

                    JsonNode textNode = candidate.get("content").get("parts").get(0);
                    if (textNode.has("text")) {
                        return textNode.get("text").asText();
                    }
                }
            }

            // 오류 응답 확인
            if (rootNode.has("error")) {
                JsonNode error = rootNode.get("error");
                String errorMessage = error.has("message") ? error.get("message").asText() : "알 수 없는 오류";
                throw new RuntimeException("Gemini API 오류: " + errorMessage);
            }

            return "AI 응답 파싱 실패: " + response;

        } catch (Exception e) {
            log.error("AI 응답 파싱 중 오류 발생: {}", e.getMessage());
            return "AI 응답 파싱 오류: " + response;
        }
    }

    private String buildPrompt(TravelPreferenceRequestDto dto) {
        String prompt = String.format(
                """
                당신은 최고의 여행 플래너입니다. 사용자가 제공한 선호도에 맞춰 다음 형식으로 상세한 여행 계획을 생성해 주세요:

                [추천 여행지 이름] 여행 계획 ([여행 기간] [사용자 여행 스타일] 여행)
                
                --- 여행 개요 ---
                - 주요 테마: [사용자 선호도 기반]
                - 추천 교통수단: [사용자 선호도 기반]
                - 하루 예산: [사용자 예산 기반]

                --- 일차별 상세 일정 ---
                1일차: [주요 활동/지역]
                - 오전: [장소, 추천 활동, 예상 소요 시간, 간단한 설명]
                - 점심: [추천 음식 또는 식당 유형, 간단한 설명]
                - 오후: [장소, 추천 활동, 예상 소요 시간, 간단한 설명]
                - 저녁: [추천 음식 또는 식당 유형, 간단한 설명]
                - 숙소 추천: [숙소 유형, 간단한 설명]

                --- 추가 팁 ---
                - [여행 팁 1]
                - [여행 팁 2]

                모든 내용은 한국어로 작성해 주세요.

                --- 사용자 선호도 데이터 ---
                여행 스타일: %s
                여행 기간: %s
                이동 수단: %s
                1일 예상 예산: %s
                여행지 유형: %s
                동반자: %s
                가장 중요하게 생각하는 것: %s
                하루 일정 강도: %s
                """,
                sanitizeInput(dto.getTravelStyle()),
                sanitizeInput(dto.getTravelDuration()),
                sanitizeInput(dto.getTransportation()),
                sanitizeInput(dto.getBudget()),
                sanitizeInput(dto.getDestinationType()),
                sanitizeInput(dto.getCompanion()),
                sanitizeInput(dto.getPriority()),
                sanitizeInput(dto.getDailyPace())
        );

        if (prompt.length() > 30000) {
            prompt = prompt.substring(0, 30000) + "...";
        }

        return prompt;
    }

    private String sanitizeInput(String input) {
        if (input == null) {
            return "정보 없음";
        }

        return input.replaceAll("[\\r\\n\\t]", " ")
                .replaceAll("\\s+", " ")
                .trim()
                .substring(0, Math.min(input.length(), 100));
    }

    private String getAccessToken() throws IOException {
        try (InputStream inputStream = new ClassPathResource("service-account.json").getInputStream()) {
            GoogleCredentials credentials = GoogleCredentials
                    .fromStream(inputStream)
                    .createScoped(List.of("https://www.googleapis.com/auth/cloud-platform"));

            credentials.refreshIfExpired();
            String token = credentials.getAccessToken().getTokenValue();

            if (token == null || token.isEmpty()) {
                throw new RuntimeException("액세스 토큰을 가져올 수 없습니다.");
            }

            return token;
        } catch (IOException e) {
            log.error("서비스 계정 키 파일을 읽는 중 오류 발생", e);
            throw new IOException("Google 인증 실패: " + e.getMessage(), e);
        }
    }
}