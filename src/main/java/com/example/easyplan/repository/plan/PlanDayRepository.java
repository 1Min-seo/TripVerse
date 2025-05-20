package com.example.easyplan.repository.plan;

import com.example.easyplan.domain.entity.Plan.Plan;
import com.example.easyplan.domain.entity.Plan.PlanDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlanDayRepository extends JpaRepository<PlanDay, Long> {
    List<PlanDay> findByPlan(Plan plan);
}
