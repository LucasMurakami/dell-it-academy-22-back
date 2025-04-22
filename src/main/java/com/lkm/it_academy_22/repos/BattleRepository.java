package com.lkm.it_academy_22.repos;

import com.lkm.it_academy_22.domain.Battle;
import com.lkm.it_academy_22.domain.Tournament;
import com.lkm.it_academy_22.domain.TournamentStartup;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface BattleRepository extends JpaRepository<Battle, Integer> {

    Battle findFirstByTournament(Tournament tournament);

    Battle findFirstByStartup1(TournamentStartup tournamentStartup);

    Battle findFirstByStartup2(TournamentStartup tournamentStartup);

    Battle findFirstByWinner(TournamentStartup tournamentStartup);

    Battle findByBattleNumberAndTournamentId(int parentNumber, Integer tournamentId);
}
