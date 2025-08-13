package com.example.easyplan.service;

import com.example.easyplan.domain.entity.Preference.TravelPreference;
import com.example.easyplan.domain.entity.Preference.TravelPreferenceRequestDto;
import com.example.easyplan.domain.entity.Preference.TravelPreferenceResponseDto;
import com.example.easyplan.domain.entity.User.User;
import com.example.easyplan.repository.TravelPreferenceRepository;
import com.example.easyplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPreferenceService {
    private final TravelPreferenceRepository travelPreferenceRepository;
    private final UserRepository userRepository;
    private final GeminiService geminiService;

    @Transactional
    public TravelPreferenceResponseDto save(Long userId, TravelPreferenceRequestDto dto) {
        User user = getUserById(userId);

        TravelPreference travelPreference = TravelPreference.createPreference(
                user,
                dto.getTravelStyle(),
                dto.getTravelDuration(),
                dto.getTransportation(),
                dto.getBudget(),
                dto.getDestinationType(),
                dto.getCompanion(),
                dto.getPriority(),
                dto.getDailyPace()
        );

        String recommendation = getRecommendationWithCache(dto);
        travelPreference.saveRecommendation(recommendation);

        travelPreferenceRepository.save(travelPreference);
        return TravelPreferenceResponseDto.from(travelPreference);
    }

    @Cacheable(value = "travelRecommendation", key = "#dto.travelStyle + '_' + #dto.travelDuration + '_' + #dto.transportation + '_' + #dto.budget + '_' + #dto.destinationType + '_' + #dto.companion + '_' + #dto.priority + '_' + #dto.dailyPace")
    public String getRecommendationWithCache(TravelPreferenceRequestDto dto) {
        try {
            return geminiService.getTravelRecommendation(dto);
        } catch (Exception e) {
            throw new RuntimeException("AI 추천 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @Transactional
    public void saveAiRecommendation(Long preferenceId, String recommendation) {
        TravelPreference preference = travelPreferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 아이디 찾을 수 없음"));

        preference.saveRecommendation(recommendation);
        travelPreferenceRepository.save(preference);

    }

    public TravelPreferenceResponseDto getRecommendation(Long preferenceId) {
        TravelPreference preference = travelPreferenceRepository.findById(preferenceId)
                .orElseThrow(() -> new IllegalArgumentException("해당 preference Id를 찾을 수 없음"));
        return TravelPreferenceResponseDto.from(preference);
    }

    public List<TravelPreferenceResponseDto> getAllRecommendations(Long userId, int page) {
        User user = getUserById(userId);

        int pageSize = 6;
        Pageable pageable = PageRequest.of(page, pageSize, Sort.by("createdDate").descending());

        List<TravelPreference> recommendations = travelPreferenceRepository.findAllByUserId(userId, pageable);

        return recommendations
                .stream()
                .map(TravelPreferenceResponseDto::from)
                .collect(Collectors.toList());

    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
