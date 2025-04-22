package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.StartupDTO;
import com.lkm.it_academy_22.service.StartupService;
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

public class StartupResourceTest {

  @Mock
  private StartupService startupService;

  @InjectMocks
  private StartupResource startupResource;

  private MockMvc mockMvc;
  private StartupDTO startupDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(startupResource).build();

    startupDTO = new StartupDTO();
    startupDTO.setId(1);
    startupDTO.setName("Test Startup");
    startupDTO.setDescription("A test startup for unit tests");
  }

  @Test
  void getAllStartups_ShouldReturnAllStartups() {

    List<StartupDTO> startups = Arrays.asList(startupDTO);
    when(startupService.findAll()).thenReturn(startups);

    ResponseEntity<List<StartupDTO>> response = startupResource.getAllStartups();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(startups, response.getBody());
    verify(startupService).findAll();
  }

  @Test
  void getStartup_WithValidId_ShouldReturnStartup() {

    when(startupService.get(1)).thenReturn(startupDTO);

    ResponseEntity<StartupDTO> response = startupResource.getStartup(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(startupDTO, response.getBody());
    verify(startupService).get(1);
  }

  @Test
  void createStartup_ShouldCreateAndReturnId() {

    when(startupService.create(any(StartupDTO.class))).thenReturn(1);

    ResponseEntity<Integer> response = startupResource.createStartup(startupDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(startupService).create(startupDTO);
  }

  @Test
  void updateStartup_WithValidId_ShouldUpdateAndReturnId() {

    doNothing().when(startupService).update(eq(1), any(StartupDTO.class));

    ResponseEntity<Integer> response = startupResource.updateStartup(1, startupDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(startupService).update(1, startupDTO);
  }

  @Test
  void deleteStartup_WithNoReferences_ShouldDelete() {

    when(startupService.getReferencedWarning(1)).thenReturn(null);
    doNothing().when(startupService).delete(1);

    ResponseEntity<Void> response = startupResource.deleteStartup(1);

    assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    verify(startupService).getReferencedWarning(1);
    verify(startupService).delete(1);
  }

}