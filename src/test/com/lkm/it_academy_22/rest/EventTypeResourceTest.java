package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.EventTypeDTO;
import com.lkm.it_academy_22.service.EventTypeService;
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

public class EventTypeResourceTest {

  @Mock
  private EventTypeService eventTypeService;

  @InjectMocks
  private EventTypeResource eventTypeResource;

  private MockMvc mockMvc;
  private EventTypeDTO eventTypeDTO;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mockMvc = MockMvcBuilders.standaloneSetup(eventTypeResource).build();

    eventTypeDTO = new EventTypeDTO();
    eventTypeDTO.setId(1);
    eventTypeDTO.setName("Pitch Convincente");
    eventTypeDTO.setScoreModifier(5);
  }

  @Test
  void getAllEventTypes_ShouldReturnAllEventTypes() {

    List<EventTypeDTO> eventTypes = Arrays.asList(eventTypeDTO);
    when(eventTypeService.findAll()).thenReturn(eventTypes);

    ResponseEntity<List<EventTypeDTO>> response = eventTypeResource.getAllEventTypes();

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(eventTypes, response.getBody());
    verify(eventTypeService).findAll();
  }

  @Test
  void getEventType_WithValidId_ShouldReturnEventType() {

    when(eventTypeService.get(1)).thenReturn(eventTypeDTO);

    ResponseEntity<EventTypeDTO> response = eventTypeResource.getEventType(1);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(eventTypeDTO, response.getBody());
    verify(eventTypeService).get(1);
  }

  @Test
  void createEventType_ShouldCreateAndReturnId() {

    when(eventTypeService.create(any(EventTypeDTO.class))).thenReturn(1);

    ResponseEntity<Integer> response = eventTypeResource.createEventType(eventTypeDTO);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(eventTypeService).create(eventTypeDTO);
  }

  @Test
  void updateEventType_WithValidId_ShouldUpdateAndReturnId() {

    doNothing().when(eventTypeService).update(eq(1), any(EventTypeDTO.class));

    ResponseEntity<Integer> response = eventTypeResource.updateEventType(1, eventTypeDTO);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(1, response.getBody());
    verify(eventTypeService).update(1, eventTypeDTO);
  }
}