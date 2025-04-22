package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.BattleDTO;
import com.lkm.it_academy_22.model.TournamentDTO;
import com.lkm.it_academy_22.service.TournamentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TournamentResourceTest {

  @Mock
  private TournamentService tournamentService;

  @InjectMocks
  private TournamentResource tournamentResource;

  private MockMvc mockMvc;
  private TournamentDTO tournamentDTO;
  private BattleDTO battleDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(tournamentResource).build();

    tournamentDTO = new TournamentDTO();
    tournamentDTO.setId(1);
    tournamentDTO.setName("Tournament Test");

    battleDTO = new BattleDTO();
    battleDTO.setId(1);
    battleDTO.setBattleNumber(1);
    battleDTO.setRoundNumber(1);
    battleDTO.setSharkFight(false);
    battleDTO.setCompleted(false);
    battleDTO.setTournament(1);
    battleDTO.setStartup1(1);
    battleDTO.setStartup2(2);
  }

  @Test
  void getAllTournaments_ShouldReturnAllTournaments() {

    List<TournamentDTO> tournaments = Arrays.asList(tournamentDTO);
    when(tournamentService.findAll()).thenReturn(tournaments);

    ResponseEntity<List<TournamentDTO>> response = tournamentResource.getAllTournaments();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tournaments, response.getBody());
    verify(tournamentService).findAll();
  }

  @Test
  void getTournament_WithValidId_ShouldReturnTournament() {

    when(tournamentService.get(1)).thenReturn(tournamentDTO);

    ResponseEntity<TournamentDTO> response = tournamentResource.getTournament(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tournamentDTO, response.getBody());
    verify(tournamentService).get(1);
  }

  @Test
  void getTournamentBattles_WithValidId_ShouldReturnBattles() {

    List<BattleDTO> battles = Arrays.asList(battleDTO);
    when(tournamentService.existsById(1)).thenReturn(true);
    when(tournamentService.getBattlesByTournamentId(1)).thenReturn(battles);

    ResponseEntity<List<BattleDTO>> response = tournamentResource.getTournamentBattles(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(battles, response.getBody());
    verify(tournamentService).existsById(1);
    verify(tournamentService).getBattlesByTournamentId(1);
  }

  @Test
  void getTournamentBattles_WithInvalidId_ShouldReturnNotFound() {

    when(tournamentService.existsById(99)).thenReturn(false);

    ResponseEntity<List<BattleDTO>> response = tournamentResource.getTournamentBattles(99);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(tournamentService).existsById(99);
    verify(tournamentService, never()).getBattlesByTournamentId(anyInt());
  }

  @Test
  void createTournament_ShouldCreateAndReturnId() {

    when(tournamentService.create(any(TournamentDTO.class))).thenReturn(1);

    ResponseEntity<Integer> response = tournamentResource.createTournament(tournamentDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(tournamentService).create(tournamentDTO);
  }

  @Test
  void updateTournament_WithValidId_ShouldUpdateAndReturnId() {

    doNothing().when(tournamentService).update(eq(1), any(TournamentDTO.class));

    ResponseEntity<Integer> response = tournamentResource.updateTournament(1, tournamentDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(tournamentService).update(1, tournamentDTO);
  }

  @Test
  void deleteTournament_WithNoReferences_ShouldDelete() {

    when(tournamentService.getReferencedWarning(1)).thenReturn(null);
    doNothing().when(tournamentService).delete(1);

    ResponseEntity<Void> response = tournamentResource.deleteTournament(1);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(tournamentService).getReferencedWarning(1);
    verify(tournamentService).delete(1);
  }
}