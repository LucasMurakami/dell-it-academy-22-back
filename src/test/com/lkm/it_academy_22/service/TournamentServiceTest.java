package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.Battle;
import com.lkm.it_academy_22.domain.Startup;
import com.lkm.it_academy_22.domain.Tournament;
import com.lkm.it_academy_22.domain.TournamentStartup;
import com.lkm.it_academy_22.model.BattleDTO;
import com.lkm.it_academy_22.model.TournamentDTO;
import com.lkm.it_academy_22.repos.BattleRepository;
import com.lkm.it_academy_22.repos.StartupRepository;
import com.lkm.it_academy_22.repos.TournamentRepository;
import com.lkm.it_academy_22.repos.TournamentStartupRepository;
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
public class TournamentServiceTest {

  @Mock
  private TournamentRepository tournamentRepository;

  @Mock
  private StartupRepository startupRepository;

  @Mock
  private TournamentStartupRepository tournamentStartupRepository;

  @Mock
  private BattleRepository battleRepository;

  @Mock
  private BattleService battleService;

  @InjectMocks
  private TournamentService tournamentService;

  private Tournament tournament;
  private TournamentDTO tournamentDTO;
  private Startup startup;
  private Battle battle;

  @BeforeEach
  void setUp() {
    startup = new Startup();
    startup.setId(1);
    startup.setName("Test Startup");

    tournament = new Tournament();
    tournament.setId(1);
    tournament.setName("Test Tournament");
    tournament.setStatus("ACTIVE");
    tournament.setChampion(startup);
    tournament.setCreatedAt(OffsetDateTime.now());

    tournamentDTO = new TournamentDTO();
    tournamentDTO.setName("Test Tournament");
    tournamentDTO.setStatus("ACTIVE");
    tournamentDTO.setChampion(1);

    battle = new Battle();
    battle.setId(1);
    battle.setTournament(tournament);
    battle.setBattleNumber(1);

    Set<Battle> battles = new HashSet<>();
    battles.add(battle);
    tournament.setTournamentBattles(battles);
  }

  @Test
  void findAll_ShouldReturnAllTournaments() {

    when(tournamentRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(tournament));

    List<TournamentDTO> result = tournamentService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Test Tournament", result.get(0).getName());
    assertEquals("ACTIVE", result.get(0).getStatus());
    verify(tournamentRepository).findAll(any(Sort.class));
  }

  @Test
  void get_WithValidId_ShouldReturnTournament() {

    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));

    TournamentDTO result = tournamentService.get(1);

    assertNotNull(result);
    assertEquals("Test Tournament", result.getName());
    assertEquals("ACTIVE", result.getStatus());
    assertEquals(1, result.getChampion());
    verify(tournamentRepository).findById(1);
  }

  @Test
  void get_WithInvalidId_ShouldThrowNotFoundException() {

    when(tournamentRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> tournamentService.get(99));
    verify(tournamentRepository).findById(99);
  }

  @Test
  void getBattlesByTournamentId_WithValidId_ShouldReturnBattles() {

    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));
    when(battleService.mapToDTO(eq(battle), any(BattleDTO.class))).thenAnswer(invocation -> {
      BattleDTO dto = invocation.getArgument(1);
      dto.setId(battle.getId());
      dto.setBattleNumber(battle.getBattleNumber());
      return dto;
    });

    List<BattleDTO> result = tournamentService.getBattlesByTournamentId(1);

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals(1, result.get(0).getId());
    assertEquals(1, result.get(0).getBattleNumber());
    verify(tournamentRepository).findById(1);
  }

  @Test
  void getBattlesByTournamentId_WithInvalidId_ShouldThrowNotFoundException() {

    when(tournamentRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> tournamentService.getBattlesByTournamentId(99));
    verify(tournamentRepository).findById(99);
  }

  @Test
  void existsById_WithExistingId_ShouldReturnTrue() {

    when(tournamentRepository.existsById(1)).thenReturn(true);

    boolean result = tournamentService.existsById(1);

    assertTrue(result);
    verify(tournamentRepository).existsById(1);
  }

  @Test
  void existsById_WithNonExistingId_ShouldReturnFalse() {

    when(tournamentRepository.existsById(99)).thenReturn(false);

    boolean result = tournamentService.existsById(99);

    assertFalse(result);
    verify(tournamentRepository).existsById(99);
  }

  @Test
  void create_ShouldSaveAndReturnId() {

    when(startupRepository.findById(1)).thenReturn(Optional.of(startup));
    when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

    Integer result = tournamentService.create(tournamentDTO);

    assertEquals(1, result);
    verify(startupRepository).findById(1);
    verify(tournamentRepository).save(any(Tournament.class));
  }

  @Test
  void update_WithValidId_ShouldUpdateTournament() {

    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));
    when(startupRepository.findById(1)).thenReturn(Optional.of(startup));
    when(tournamentRepository.save(any(Tournament.class))).thenReturn(tournament);

    tournamentDTO.setName("Updated Name");

    tournamentService.update(1, tournamentDTO);

    assertEquals("Updated Name", tournament.getName());
    verify(tournamentRepository).findById(1);
    verify(startupRepository).findById(1);
    verify(tournamentRepository).save(tournament);
  }

  @Test
  void update_WithInvalidId_ShouldThrowNotFoundException() {

    when(tournamentRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> tournamentService.update(99, tournamentDTO));
    verify(tournamentRepository).findById(99);
    verify(tournamentRepository, never()).save(any(Tournament.class));
  }

  @Test
  void delete_ShouldCallRepository() {

    tournamentService.delete(1);

    verify(tournamentRepository).deleteById(1);
  }

  @Test
  void getReferencedWarning_WithTournamentStartupReference_ShouldReturnWarning() {

    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));

    TournamentStartup tournamentStartup = new TournamentStartup();
    tournamentStartup.setId(1);
    when(tournamentStartupRepository.findFirstByTournament(tournament)).thenReturn(tournamentStartup);

    ReferencedWarning result = tournamentService.getReferencedWarning(1);

    assertNotNull(result);
    assertEquals("tournament.tournamentStartup.tournament.referenced", result.getKey());
    assertEquals(1, result.getParams().get(0));
    verify(tournamentRepository).findById(1);
    verify(tournamentStartupRepository).findFirstByTournament(tournament);
    verify(battleRepository, never()).findFirstByTournament(tournament);
  }

  @Test
  void getReferencedWarning_WithBattleReference_ShouldReturnWarning() {

    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));
    when(tournamentStartupRepository.findFirstByTournament(tournament)).thenReturn(null);

    Battle battleRef = new Battle();
    battleRef.setId(1);
    when(battleRepository.findFirstByTournament(tournament)).thenReturn(battleRef);

    ReferencedWarning result = tournamentService.getReferencedWarning(1);

    assertNotNull(result);
    assertEquals("tournament.battle.tournament.referenced", result.getKey());
    assertEquals(1, result.getParams().get(0));
    verify(tournamentRepository).findById(1);
    verify(tournamentStartupRepository).findFirstByTournament(tournament);
    verify(battleRepository).findFirstByTournament(tournament);
  }

  @Test
  void getReferencedWarning_WithNoReferences_ShouldReturnNull() {

    when(tournamentRepository.findById(1)).thenReturn(Optional.of(tournament));
    when(tournamentStartupRepository.findFirstByTournament(tournament)).thenReturn(null);
    when(battleRepository.findFirstByTournament(tournament)).thenReturn(null);

    ReferencedWarning result = tournamentService.getReferencedWarning(1);

    assertNull(result);
    verify(tournamentRepository).findById(1);
    verify(tournamentStartupRepository).findFirstByTournament(tournament);
    verify(battleRepository).findFirstByTournament(tournament);
  }
}