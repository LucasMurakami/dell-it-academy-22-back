package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.BattleEventDTO;
import com.lkm.it_academy_22.service.BattleEventService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BattleEventResourceTest {

  @Mock
  private BattleEventService battleEventService;

  @InjectMocks
  private BattleEventResource battleEventResource;

  private MockMvc mockMvc;
  private BattleEventDTO battleEventDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(battleEventResource).build();

    battleEventDTO = new BattleEventDTO();
    battleEventDTO.setId(1);
    battleEventDTO.setBattle(1);
    battleEventDTO.setStartup(1);
    battleEventDTO.setEventType(1);
  }

  @Test
  void getAllBattleEvents_ShouldReturnAllBattleEvents() {
    List<BattleEventDTO> battleEvents = Arrays.asList(battleEventDTO);
    when(battleEventService.findAll()).thenReturn(battleEvents);

    ResponseEntity<List<BattleEventDTO>> response = battleEventResource.getAllBattleEvents();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(battleEvents, response.getBody());
    verify(battleEventService).findAll();
  }

  @Test
  void getBattleEvent_WithValidId_ShouldReturnBattleEvent() {
    when(battleEventService.get(1)).thenReturn(battleEventDTO);

    ResponseEntity<BattleEventDTO> response = battleEventResource.getBattleEvent(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(battleEventDTO, response.getBody());
    verify(battleEventService).get(1);
  }

  @Test
  void getBattleEventsByBattleAndStartup_ShouldReturnBattleEvents() {
    List<BattleEventDTO> battleEvents = Arrays.asList(battleEventDTO);
    when(battleEventService.findByBattleAndTournamentStartup(1, 1)).thenReturn(battleEvents);

    ResponseEntity<List<BattleEventDTO>> response = battleEventResource.getBattleEventsByBattleAndStartup(1, 1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(battleEvents, response.getBody());
    verify(battleEventService).findByBattleAndTournamentStartup(1, 1);
  }

  @Test
  void findByTournamentStartup_ShouldReturnBattleEvents() {
    List<BattleEventDTO> battleEvents = Arrays.asList(battleEventDTO);
    when(battleEventService.findByTournamentStartup(1)).thenReturn(battleEvents);

    ResponseEntity<List<BattleEventDTO>> response = battleEventResource.findByTournamentStartup(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(battleEvents, response.getBody());
    verify(battleEventService).findByTournamentStartup(1);
  }

  @Test
  void createBattleEvent_ShouldCreateAndReturnId() {
    when(battleEventService.create(any(BattleEventDTO.class))).thenReturn(1);

    ResponseEntity<Integer> response = battleEventResource.createBattleEvent(battleEventDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(battleEventService).create(battleEventDTO);
  }

  @Test
  void updateBattleEvent_WithValidId_ShouldUpdateAndReturnId() {
    doNothing().when(battleEventService).update(eq(1), any(BattleEventDTO.class));

    ResponseEntity<Integer> response = battleEventResource.updateBattleEvent(1, battleEventDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(battleEventService).update(1, battleEventDTO);
  }
}