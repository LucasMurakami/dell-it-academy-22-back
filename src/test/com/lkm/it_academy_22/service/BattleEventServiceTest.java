package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.*;
import com.lkm.it_academy_22.model.BattleEventDTO;
import com.lkm.it_academy_22.repos.*;
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
public class BattleEventServiceTest {

  @Mock
  private BattleEventRepository battleEventRepository;

  @Mock
  private BattleRepository battleRepository;

  @Mock
  private TournamentStartupRepository tournamentStartupRepository;

  @Mock
  private TournamentRepository tournamentRepository;

  @Mock
  private EventTypeRepository eventTypeRepository;

  @InjectMocks
  private BattleEventService battleEventService;

  private Battle battle;
  private TournamentStartup tournamentStartup;
  private EventType eventType;
  private BattleEvent battleEvent;
  private BattleEventDTO battleEventDTO;

  @BeforeEach
  void setUp() {
    Tournament tournament = new Tournament();
    tournament.setId(1);
    tournament.setName("Test Tournament");

    tournamentStartup = new TournamentStartup();
    tournamentStartup.setId(1);
    tournamentStartup.setCurrentScore(70);
    tournamentStartup.setTournament(tournament);

    battle = new Battle();
    battle.setId(1);
    battle.setTournament(tournament);
    battle.setStartup1(tournamentStartup);
    battle.setBattleNumber(1);

    eventType = new EventType();
    eventType.setId(1);
    eventType.setName("Test Event");
    eventType.setScoreModifier(5);

    battleEvent = new BattleEvent();
    battleEvent.setId(1);
    battleEvent.setBattle(battle);
    battleEvent.setStartup(tournamentStartup);
    battleEvent.setEventType(eventType);
    battleEvent.setCreatedAt(OffsetDateTime.now());

    battleEventDTO = new BattleEventDTO();
    battleEventDTO.setId(1);
    battleEventDTO.setBattle(1);
    battleEventDTO.setStartup(1);
    battleEventDTO.setEventType(1);
  }

  @Test
  void findAll_ShouldReturnAllBattleEvents() {

    when(battleEventRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(battleEvent));

    List<BattleEventDTO> result = battleEventService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getBattle());
    assertEquals(1, result.get(0).getStartup());
    assertEquals(1, result.get(0).getEventType());
    verify(battleEventRepository).findAll(any(Sort.class));
  }

  @Test
  void findByBattleAndTournamentStartup_ShouldReturnMatchingEvents() {

    when(battleRepository.findById(1)).thenReturn(Optional.of(battle));
    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));
    when(battleEventRepository.findByBattleAndTournamentStartup(battle, tournamentStartup))
        .thenReturn(Arrays.asList(battleEvent));

    List<BattleEventDTO> result = battleEventService.findByBattleAndTournamentStartup(1, 1);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getBattle());
    assertEquals(1, result.get(0).getStartup());
    verify(battleRepository).findById(1);
    verify(tournamentStartupRepository).findById(1);
    verify(battleEventRepository).findByBattleAndTournamentStartup(battle, tournamentStartup);
  }

  @Test
  void findByTournamentStartup_ShouldReturnMatchingEvents() {

    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));
    when(battleEventRepository.findByStartup(tournamentStartup)).thenReturn(Arrays.asList(battleEvent));

    List<BattleEventDTO> result = battleEventService.findByTournamentStartup(1);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getBattle());
    assertEquals(1, result.get(0).getStartup());
    verify(tournamentStartupRepository).findById(1);
    verify(battleEventRepository).findByStartup(tournamentStartup);
  }

  @Test
  void get_WithValidId_ShouldReturnBattleEvent() {

    when(battleEventRepository.findById(1)).thenReturn(Optional.of(battleEvent));

    BattleEventDTO result = battleEventService.get(1);

    assertNotNull(result);
    assertEquals(1, result.getBattle());
    assertEquals(1, result.getStartup());
    assertEquals(1, result.getEventType());
    verify(battleEventRepository).findById(1);
  }

  @Test
  void get_WithInvalidId_ShouldThrowNotFoundException() {

    when(battleEventRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> battleEventService.get(99));
    verify(battleEventRepository).findById(99);
  }

  @Test
  void create_WithValidData_ShouldSaveAndReturnId() {

    when(battleRepository.findById(1)).thenReturn(Optional.of(battle));
    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));
    when(eventTypeRepository.findById(1)).thenReturn(Optional.of(eventType));
    when(battleEventRepository.save(any(BattleEvent.class))).thenReturn(battleEvent);

    Integer result = battleEventService.create(battleEventDTO);

    assertEquals(1, result);
    assertEquals(75, tournamentStartup.getCurrentScore()); // 70 + 5 (score modifier)
    verify(battleRepository).findById(1);
    verify(tournamentStartupRepository, times(2)).findById(1); // Ajustado para 2 chamadas
    verify(eventTypeRepository).findById(1);
    verify(battleEventRepository).save(any(BattleEvent.class));
    verify(tournamentStartupRepository).saveAndFlush(tournamentStartup);
  }

  @Test
  void update_WithValidId_ShouldUpdateBattleEvent() {

    when(battleEventRepository.findById(1)).thenReturn(Optional.of(battleEvent));
    when(battleRepository.findById(1)).thenReturn(Optional.of(battle));
    when(tournamentStartupRepository.findById(1)).thenReturn(Optional.of(tournamentStartup));
    when(eventTypeRepository.findById(1)).thenReturn(Optional.of(eventType));
    when(battleEventRepository.save(any(BattleEvent.class))).thenReturn(battleEvent);

    battleEventService.update(1, battleEventDTO);

    verify(battleEventRepository).findById(1);
    verify(battleRepository).findById(1);
    verify(tournamentStartupRepository).findById(1);
    verify(eventTypeRepository).findById(1);
    verify(battleEventRepository).save(battleEvent);
  }

  @Test
  void update_WithInvalidId_ShouldThrowNotFoundException() {

    when(battleEventRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> battleEventService.update(99, battleEventDTO));
    verify(battleEventRepository).findById(99);
    verify(battleEventRepository, never()).save(any(BattleEvent.class));
  }

  @Test
  void delete_ShouldCallRepository() {

    battleEventService.delete(1);

    verify(battleEventRepository).deleteById(1);
  }
}