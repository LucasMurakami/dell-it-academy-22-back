package com.lkm.it_academy_22.repos;

import com.lkm.it_academy_22.domain.Startup;
import com.lkm.it_academy_22.domain.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TournamentRepository extends JpaRepository<Tournament, Integer> {

    Tournament findFirstByChampion(Startup startup);

}
