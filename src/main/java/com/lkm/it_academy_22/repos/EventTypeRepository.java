package com.lkm.it_academy_22.repos;

import com.lkm.it_academy_22.domain.EventType;
import org.springframework.data.jpa.repository.JpaRepository;


public interface EventTypeRepository extends JpaRepository<EventType, Integer> {
}
