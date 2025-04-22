package com.lkm.it_academy_22.repos;

import com.lkm.it_academy_22.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BattleEventRepository extends JpaRepository<BattleEvent, Integer> {

    BattleEvent findFirstByBattle(Battle battle);

    BattleEvent findFirstByStartup(TournamentStartup tournamentStartup);

    BattleEvent findFirstByEventType(EventType eventType);

    @Query("SELECT be FROM BattleEvent be WHERE be.battle = :battle AND be.startup = :startup")
    List<BattleEvent> findByBattleAndTournamentStartup(
            @Param("battle") Battle battle,
            @Param("startup") TournamentStartup startup
    );

    List<BattleEvent> findByStartup(TournamentStartup tournamentStartup);
}
