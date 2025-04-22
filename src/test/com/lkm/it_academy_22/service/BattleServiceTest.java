package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.*;
import com.lkm.it_academy_22.model.BattleDTO;
import com.lkm.it_academy_22.repos.*;
import com.lkm.it_academy_22.util.ReferencedWarning;
import com.lkm.it_academy_22.util.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BattleServiceTest {

  @Mock
  private BattleRepository battleRepository;

  @Mock
  private TournamentRepository tournamentRepository;

  @Mock
  private TournamentStartupRepository tournamentStartupRepository;

  @Mock
  private BattleEventRepository battleEventRepository;

  @InjectMocks
  private BattleService battleService;

  private Tournament tournament;
  private TournamentStartup startup1;
  private TournamentStartup startup2;
  private Battle battle;
  private BattleDTO battleDTO;

  @BeforeEach
  void setUp() {
    tournament = new Tournament();
    tournament.setId(1);
    tournament.setName("Test Tournament");
    tournament.setStatus("ACTIVE");

    startup1 = new TournamentStartup();
    startup1.setId(1);
    startup1.setCurrentScore(70);
    startup1.setEliminated(false);
    startup1.setTournament(tournament);

    startup2 = new TournamentStartup();
    startup2.setId(2);
    startup2.setCurrentScore(70);
    startup2.setEliminated(false);
    startup2.setTournament(tournament);

    battle = new Battle();
    battle.setId(1);
    battle.setTournament(tournament);
    battle.setStartup1(startup1);
    battle.setStartup2(startup2);
    battle.setBattleNumber(1);
    battle.setRoundNumber(1);
    battle.setCompleted(false);
    battle.setSharkFight(false);
    battle.setCreatedAt(OffsetDateTime.now());

    battleDTO = new BattleDTO();
    battleDTO.setTournament(1);
    battleDTO.setStartup1(1);
    battleDTO.setStartup2(2);
    battleDTO.setBattleNumber(1);
    battleDTO.setRoundNumber(1);
    battleDTO.setCompleted(false);
    battleDTO.setSharkFight(false);
  }

  @Test
  void findAll_ShouldReturnAllBattles() {

    when(battleRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(battle));

    List<BattleDTO> result = battleService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getBattleNumber());
    assertEquals(1, result.get(0).getRoundNumber());
    verify(battleRepository).findAll(any(Sort.class));
  }

  @Test
  void get_WithValidId_ShouldReturnBattle() {

    when(battleRepository.findById(1)).thenReturn(Optional.of(battle));

    BattleDTO result = battleService.get(1);

    assertNotNull(result);
    assertEquals(1, result.getBattleNumber());
    assertEquals(1, result.getRoundNumber());
    assertEquals(1, result.getTournament());
    assertEquals(1, result.getStartup1());
    assertEquals(2, result.getStartup2());
    verify(battleRepository).findById(1);
  }

  @Test
  void get_WithInvalidId_ShouldThrowNotFoundException() {

    when(battleRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> battleService.get(99));
    verify(battleRepository).findById(99);
  }

  @Test
  void create_ShouldSaveAndReturnId() {

    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));
    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(startup1));
    when(tournamentStartupRepository.findById(2)).thenReturn(Optional.of(startup2));
    when(battleRepository.save(any(Battle.class))).thenReturn(battle);

    Integer result = battleService.create(battleDTO);

    assertEquals(1, result);
    verify(tournamentRepository).findById(1);
    verify(tournamentStartupRepository).findById(1);
    verify(tournamentStartupRepository).findById(2);
    verify(battleRepository).save(any(Battle.class));
  }

  @Test
  void update_WithValidId_ShouldUpdateBattle() {

    when(battleRepository.findById(1)).thenReturn(Optional.of(battle));
    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));
    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(startup1));
    when(tournamentStartupRepository.findById(2)).thenReturn(Optional.of(startup2));
    when(battleRepository.save(any(Battle.class))).thenReturn(battle);

    battleDTO.setCompleted(true);

    battleService.update(1, battleDTO);

    assertTrue(battle.getCompleted());
    verify(battleRepository).findById(1);
    verify(tournamentRepository).findById(1);
    verify(tournamentStartupRepository).findById(1);
    verify(tournamentStartupRepository).findById(2);
    verify(battleRepository).save(battle);
  }

  @Test
  void update_WithInvalidId_ShouldThrowNotFoundException() {

    when(battleRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> battleService.update(99, battleDTO));
    verify(battleRepository).findById(99);
    verify(battleRepository, never()).save(any(Battle.class));
  }

  @Test
  void delete_ShouldCallRepository() {

    battleService.delete(1);

    verify(battleRepository).deleteById(1);
  }

  @Test
  void getReferencedWarning_WithBattleEventReference_ShouldReturnWarning() {

    when(battleRepository.findById(1)).thenReturn(Optional.of(battle));

    BattleEvent battleEvent = new BattleEvent();
    battleEvent.setId(1);
    when(battleEventRepository.findFirstByBattle(battle)).thenReturn(battleEvent);

    ReferencedWarning result = battleService.getReferencedWarning(1);

    assertNotNull(result);
    assertEquals("battle.battleEvent.battle.referenced", result.getKey());
    assertEquals(1, result.getParams().get(0));
    verify(battleRepository).findById(1);
    verify(battleEventRepository).findFirstByBattle(battle);
  }

  @Test
  void getReferencedWarning_WithNoReferences_ShouldReturnNull() {

    when(battleRepository.findById(1)).thenReturn(Optional.of(battle));
    when(battleEventRepository.findFirstByBattle(battle)).thenReturn(null);

    ReferencedWarning result = battleService.getReferencedWarning(1);

    assertNull(result);
    verify(battleRepository).findById(1);
    verify(battleEventRepository).findFirstByBattle(battle);
  }
}