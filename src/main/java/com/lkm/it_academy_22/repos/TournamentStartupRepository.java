package com.lkm.it_academy_22.repos;

import com.lkm.it_academy_22.domain.Startup;
import com.lkm.it_academy_22.domain.Tournament;
import com.lkm.it_academy_22.domain.TournamentStartup;
import org.springframework.data.jpa.repository.JpaRepository;


public interface TournamentStartupRepository extends JpaRepository<TournamentStartup, Integer> {

    TournamentStartup findFirstByTournament(Tournament tournament);

    TournamentStartup findFirstByStartup(Startup startup);

    long countByTournamentId(Integer tournamentId);
}
