package com.lkm.it_academy_22.repos;

import com.lkm.it_academy_22.domain.Startup;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StartupRepository extends JpaRepository<Startup, Integer> {
}
