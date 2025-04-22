package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.BattleDTO;
import com.lkm.it_academy_22.service.BattleService;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class BattleResourceTest {

  @Mock
  private BattleService battleService;

  @InjectMocks
  private BattleResource battleResource;

  private MockMvc mockMvc;
  private BattleDTO battleDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(battleResource).build();

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
  void getAllBattles_ShouldReturnAllBattles() {
    List<BattleDTO> battles = Arrays.asList(battleDTO);
    when(battleService.findAll()).thenReturn(battles);

    ResponseEntity<List<BattleDTO>> response = battleResource.getAllBattles();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(battles, response.getBody());
    verify(battleService).findAll();
  }

  @Test
  void getBattle_WithValidId_ShouldReturnBattle() {

    when(battleService.get(1)).thenReturn(battleDTO);

    ResponseEntity<BattleDTO> response = battleResource.getBattle(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(battleDTO, response.getBody());
    verify(battleService).get(1);
  }

  @Test
  void createBattle_ShouldCreateAndReturnId() {

    when(battleService.create(any(BattleDTO.class))).thenReturn(1);

    ResponseEntity<Integer> response = battleResource.createBattle(battleDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(battleService).create(battleDTO);
  }

  @Test
  void updateBattle_WithValidId_ShouldUpdateAndReturnId() {

    doNothing().when(battleService).update(eq(1), any(BattleDTO.class));

    ResponseEntity<Integer> response = battleResource.updateBattle(1, battleDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(battleService).update(1, battleDTO);
  }
}