package com.example.easyplan.service;

import com.example.easyplan.domain.entity.Preference.TravelPreference;
import com.example.easyplan.domain.entity.Preference.TravelPreferenceRequestDto;
import com.example.easyplan.domain.entity.Preference.TravelPreferenceResponseDto;
import com.example.easyplan.domain.entity.User.User;
import com.example.easyplan.repository.TravelPreferenceRepository;
import com.example.easyplan.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TravelPreferenceService {
    private final TravelPreferenceRepository travelPreferenceRepository;
    private final UserRepository userRepository;

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

        travelPreferenceRepository.save(travelPreference);
        return TravelPreferenceResponseDto.from(travelPreference);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
}
