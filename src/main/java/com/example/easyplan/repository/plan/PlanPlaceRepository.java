package com.example.easyplan.repository.plan;

import com.example.easyplan.domain.entity.Plan.PlanDay;
import com.example.easyplan.domain.entity.Plan.PlanPlace;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanPlaceRepository extends JpaRepository<PlanPlace, Long> {
    List<PlanPlace> findByPlanDay(PlanDay planDay);
}
