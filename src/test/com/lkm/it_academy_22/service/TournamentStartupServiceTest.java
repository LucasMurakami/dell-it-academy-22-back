package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.*;
import com.lkm.it_academy_22.model.TournamentStartupDTO;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TournamentStartupServiceTest {

  @Mock
  private TournamentStartupRepository tournamentStartupRepository;

  @Mock
  private TournamentRepository tournamentRepository;

  @Mock
  private StartupRepository startupRepository;

  @Mock
  private BattleRepository battleRepository;

  @Mock
  private BattleEventRepository battleEventRepository;

  @InjectMocks
  private TournamentStartupService tournamentStartupService;

  private TournamentStartup tournamentStartup;
  private TournamentStartupDTO tournamentStartupDTO;
  private Tournament tournament;
  private Startup startup;

  @BeforeEach
  void setUp() {
    startup = new Startup();
    startup.setId(1);
    startup.setName("Test Startup");

    tournament = new Tournament();
    tournament.setId(1);
    tournament.setName("Test Tournament");

    tournamentStartup = new TournamentStartup();
    tournamentStartup.setId(1);
    tournamentStartup.setCurrentScore(70);
    tournamentStartup.setEliminated(false);
    tournamentStartup.setTournament(tournament);
    tournamentStartup.setStartup(startup);
    tournamentStartup.setCreatedAt(OffsetDateTime.now());

    tournamentStartupDTO = new TournamentStartupDTO();
    tournamentStartupDTO.setCurrentScore(70);
    tournamentStartupDTO.setEliminated(false);
    tournamentStartupDTO.setTournament(1);
    tournamentStartupDTO.setStartup(1);

    Set<TournamentStartup> startups = new HashSet<>();
    startups.add(tournamentStartup);
    tournament.setTournamentTournamentStartups(startups);
  }

  @Test
  void findAll_ShouldReturnAllTournamentStartups() {

    when(tournamentStartupRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(tournamentStartup));

    List<TournamentStartupDTO> result = tournamentStartupService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(70, result.get(0).getCurrentScore());
    assertFalse(result.get(0).getEliminated());
    assertEquals(1, result.get(0).getTournament());
    assertEquals(1, result.get(0).getStartup());
    verify(tournamentStartupRepository).findAll(any(Sort.class));
  }

  @Test
  void get_WithValidId_ShouldReturnTournamentStartup() {

    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));

    TournamentStartupDTO result = tournamentStartupService.get(1);

    assertNotNull(result);
    assertEquals(70, result.getCurrentScore());
    assertFalse(result.getEliminated());
    assertEquals(1, result.getTournament());
    assertEquals(1, result.getStartup());
    verify(tournamentStartupRepository).findById(1);
  }

  @Test
  void get_WithInvalidId_ShouldThrowNotFoundException() {

    when(tournamentStartupRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> tournamentStartupService.get(99));
    verify(tournamentStartupRepository).findById(99);
  }

  @Test
  void getTournamentStartupsByTournamentId_ShouldReturnStartups() {

    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));

    List<TournamentStartupDTO> result = tournamentStartupService.getTournamentStartupsByTournamentId(1);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(70, result.get(0).getCurrentScore());
    assertFalse(result.get(0).getEliminated());
    verify(tournamentRepository).findById(1);
  }

  @Test
  void getTournamentStartupsByTournamentId_WithInvalidId_ShouldThrowNotFoundException() {

    when(tournamentRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> tournamentStartupService.getTournamentStartupsByTournamentId(99));
    verify(tournamentRepository).findById(99);
  }

  @Test
  void create_ShouldSaveAndReturnId() {

    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));
    when(startupRepository.findById(1)).thenReturn(Optional.of(startup));
    when(tournamentStartupRepository.save(any(TournamentStartup.class))).thenReturn(tournamentStartup);

    Integer result = tournamentStartupService.create(tournamentStartupDTO);

    assertEquals(1, result);
    verify(tournamentRepository).findById(1);
    verify(startupRepository).findById(1);
    verify(tournamentStartupRepository).save(any(TournamentStartup.class));
  }

  @Test
  void update_WithValidId_ShouldUpdateTournamentStartup() {

    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));
    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));
    when(startupRepository.findById(1)).thenReturn(Optional.of(startup));
    when(tournamentStartupRepository.save(any(TournamentStartup.class))).thenReturn(tournamentStartup);

    tournamentStartupDTO.setCurrentScore(80);
    tournamentStartupDTO.setEliminated(true);

    tournamentStartupService.update(1, tournamentStartupDTO);

    assertEquals(80, tournamentStartup.getCurrentScore());
    assertTrue(tournamentStartup.getEliminated());
    verify(tournamentStartupRepository).findById(1);
    verify(tournamentRepository).findById(1);
    verify(startupRepository).findById(1);
    verify(tournamentStartupRepository).save(tournamentStartup);
  }

  @Test
  void update_WithInvalidId_ShouldThrowNotFoundException() {

    when(tournamentStartupRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> tournamentStartupService.update(99, tournamentStartupDTO));
    verify(tournamentStartupRepository).findById(99);
    verify(tournamentStartupRepository, never()).save(any(TournamentStartup.class));
  }

  @Test
  void delete_ShouldCallRepository() {

    tournamentStartupService.delete(1);

    verify(tournamentStartupRepository).deleteById(1);
  }

  @Test
  void getReferencedWarning_WithBattleStartup1Reference_ShouldReturnWarning() {

    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));

    Battle battle = new Battle();
    battle.setId(1);
    when(battleRepository.findFirstByStartup1(tournamentStartup)).thenReturn(battle);

    ReferencedWarning result = tournamentStartupService.getReferencedWarning(1);

    assertNotNull(result);
    assertEquals("tournamentStartup.battle.startup1.referenced", result.getKey());
    assertEquals(1, result.getParams().get(0));
    verify(tournamentStartupRepository).findById(1);
    verify(battleRepository).findFirstByStartup1(tournamentStartup);
  }

  @Test
  void getReferencedWarning_WithBattleStartup2Reference_ShouldReturnWarning() {

    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));
    when(battleRepository.findFirstByStartup1(tournamentStartup)).thenReturn(null);

    Battle battle = new Battle();
    battle.setId(1);
    when(battleRepository.findFirstByStartup2(tournamentStartup)).thenReturn(battle);

    ReferencedWarning result = tournamentStartupService.getReferencedWarning(1);

    assertNotNull(result);
    assertEquals("tournamentStartup.battle.startup2.referenced", result.getKey());
    assertEquals(1, result.getParams().get(0));
    verify(tournamentStartupRepository).findById(1);
    verify(battleRepository).findFirstByStartup1(tournamentStartup);
    verify(battleRepository).findFirstByStartup2(tournamentStartup);
  }

  @Test
  void getReferencedWarning_WithBattleWinnerReference_ShouldReturnWarning() {

    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));
    when(battleRepository.findFirstByStartup1(tournamentStartup)).thenReturn(null);
    when(battleRepository.findFirstByStartup2(tournamentStartup)).thenReturn(null);

    Battle battle = new Battle();
    battle.setId(1);
    when(battleRepository.findFirstByWinner(tournamentStartup)).thenReturn(battle);

    ReferencedWarning result = tournamentStartupService.getReferencedWarning(1);

    assertNotNull(result);
    assertEquals("tournamentStartup.battle.winner.referenced", result.getKey());
    assertEquals(1, result.getParams().get(0));
    verify(tournamentStartupRepository).findById(1);
    verify(battleRepository).findFirstByStartup1(tournamentStartup);
    verify(battleRepository).findFirstByStartup2(tournamentStartup);
    verify(battleRepository).findFirstByWinner(tournamentStartup);
  }

  @Test
  void getReferencedWarning_WithBattleEventReference_ShouldReturnWarning() {

    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));
    when(battleRepository.findFirstByStartup1(tournamentStartup)).thenReturn(null);
    when(battleRepository.findFirstByStartup2(tournamentStartup)).thenReturn(null);
    when(battleRepository.findFirstByWinner(tournamentStartup)).thenReturn(null);

    BattleEvent battleEvent = new BattleEvent();
    battleEvent.setId(1);
    when(battleEventRepository.findFirstByStartup(tournamentStartup)).thenReturn(battleEvent);

    ReferencedWarning result = tournamentStartupService.getReferencedWarning(1);

    assertNotNull(result);
    assertEquals("tournamentStartup.battleEvent.startup.referenced", result.getKey());
    assertEquals(1, result.getParams().get(0));
    verify(tournamentStartupRepository).findById(1);
    verify(battleRepository).findFirstByStartup1(tournamentStartup);
    verify(battleRepository).findFirstByStartup2(tournamentStartup);
    verify(battleRepository).findFirstByWinner(tournamentStartup);
    verify(battleEventRepository).findFirstByStartup(tournamentStartup);
  }

  @Test
  void getReferencedWarning_WithNoReferences_ShouldReturnNull() {

    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));
    when(battleRepository.findFirstByStartup1(tournamentStartup)).thenReturn(null);
    when(battleRepository.findFirstByStartup2(tournamentStartup)).thenReturn(null);
    when(battleRepository.findFirstByWinner(tournamentStartup)).thenReturn(null);
    when(battleEventRepository.findFirstByStartup(tournamentStartup)).thenReturn(null);

    ReferencedWarning result = tournamentStartupService.getReferencedWarning(1);

    assertNull(result);
    verify(tournamentStartupRepository).findById(1);
    verify(battleRepository).findFirstByStartup1(tournamentStartup);
    verify(battleRepository).findFirstByStartup2(tournamentStartup);
    verify(battleRepository).findFirstByWinner(tournamentStartup);
    verify(battleEventRepository).findFirstByStartup(tournamentStartup);
  }
}