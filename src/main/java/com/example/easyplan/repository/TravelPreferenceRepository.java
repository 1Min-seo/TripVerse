package com.example.easyplan.repository;

import com.example.easyplan.domain.entity.Preference.TravelPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TravelPreferenceRepository extends JpaRepository<TravelPreference, Long> {
}
