package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.TournamentStartupDTO;
import com.lkm.it_academy_22.service.TournamentService;
import com.lkm.it_academy_22.service.TournamentStartupService;
import com.lkm.it_academy_22.util.ReferencedWarning;
import com.lkm.it_academy_22.util.exceptions.ReferencedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class TournamentStartupResourceTest {

  @Mock
  private TournamentStartupService tournamentStartupService;

  @Mock
  private TournamentService tournamentService;

  @InjectMocks
  private TournamentStartupResource tournamentStartupResource;

  private MockMvc mockMvc;
  private TournamentStartupDTO tournamentStartupDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(tournamentStartupResource).build();

    tournamentStartupDTO = new TournamentStartupDTO();
    tournamentStartupDTO.setId(1);
    tournamentStartupDTO.setTournament(1);
    tournamentStartupDTO.setStartup(1);
  }

  @Test
  void getAllTournamentStartups_ShouldReturnAllTournamentStartups() {

    List<TournamentStartupDTO> tournamentStartups = Arrays.asList(tournamentStartupDTO);
    when(tournamentStartupService.findAll()).thenReturn(tournamentStartups);

    ResponseEntity<List<TournamentStartupDTO>> response = tournamentStartupResource.getAllTournamentStartups();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tournamentStartups, response.getBody());
    verify(tournamentStartupService).findAll();
  }

  @Test
  void getTournamentStartup_WithValidId_ShouldReturnTournamentStartup() {

    when(tournamentStartupService.get(1)).thenReturn(tournamentStartupDTO);

    ResponseEntity<TournamentStartupDTO> response = tournamentStartupResource.getTournamentStartup(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tournamentStartupDTO, response.getBody());
    verify(tournamentStartupService).get(1);
  }

  @Test
  void getTournamentStartupsByTournamentId_WithValidId_ShouldReturnTournamentStartups() {

    List<TournamentStartupDTO> tournamentStartups = Arrays.asList(tournamentStartupDTO);
    when(tournamentService.existsById(1)).thenReturn(true);
    when(tournamentStartupService.getTournamentStartupsByTournamentId(1)).thenReturn(tournamentStartups);

    ResponseEntity<List<TournamentStartupDTO>> response = tournamentStartupResource
        .getTournamentStartupsByTournamentId(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(tournamentStartups, response.getBody());
    verify(tournamentService).existsById(1);
    verify(tournamentStartupService).getTournamentStartupsByTournamentId(1);
  }

  @Test
  void getTournamentStartupsByTournamentId_WithInvalidId_ShouldReturnNotFound() {

    when(tournamentService.existsById(99)).thenReturn(false);

    ResponseEntity<List<TournamentStartupDTO>> response = tournamentStartupResource
        .getTournamentStartupsByTournamentId(99);

    assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    verify(tournamentService).existsById(99);
    verify(tournamentStartupService, never()).getTournamentStartupsByTournamentId(anyInt());
  }

  @Test
  void createTournamentStartup_ShouldCreateAndReturnId() {

    when(tournamentStartupService.create(any(TournamentStartupDTO.class))).thenReturn(1);

    ResponseEntity<Integer> response = tournamentStartupResource.createTournamentStartup(tournamentStartupDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(tournamentStartupService).create(tournamentStartupDTO);
  }

  @Test
  void updateTournamentStartup_WithValidId_ShouldUpdateAndReturnId() {

    doNothing().when(tournamentStartupService).update(eq(1), any(TournamentStartupDTO.class));

    ResponseEntity<Integer> response = tournamentStartupResource.updateTournamentStartup(1, tournamentStartupDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(tournamentStartupService).update(1, tournamentStartupDTO);
  }

  @Test
  void deleteTournamentStartup_WithNoReferences_ShouldDelete() {

    when(tournamentStartupService.getReferencedWarning(1)).thenReturn(null);
    doNothing().when(tournamentStartupService).delete(1);

    ResponseEntity<Void> response = tournamentStartupResource.deleteTournamentStartup(1);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(tournamentStartupService).getReferencedWarning(1);
    verify(tournamentStartupService).delete(1);
  }
}