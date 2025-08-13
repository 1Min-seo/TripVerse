package com.example.easyplan.repository;

import com.example.easyplan.domain.entity.Preference.TravelPreference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TravelPreferenceRepository extends JpaRepository<TravelPreference, Long> {
    public List<TravelPreference> findAllByUserId(Long userId, Pageable pageable);
}
