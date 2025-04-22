package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.BattleEvent;
import com.lkm.it_academy_22.domain.EventType;
import com.lkm.it_academy_22.model.EventTypeDTO;
import com.lkm.it_academy_22.repos.BattleEventRepository;
import com.lkm.it_academy_22.repos.EventTypeRepository;
import com.lkm.it_academy_22.util.ReferencedWarning;
import com.lkm.it_academy_22.util.exceptions.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EventTypeServiceTest {

  @Mock
  private EventTypeRepository eventTypeRepository;

  @Mock
  private BattleEventRepository battleEventRepository;

  @InjectMocks
  private EventTypeService eventTypeService;

  private EventType eventType;
  private EventTypeDTO eventTypeDTO;

  @BeforeEach
  void setUp() {
    eventType = new EventType();
    eventType.setId(1);
    eventType.setName("Pitch Convincente");
    eventType.setScoreModifier(5);

    eventTypeDTO = new EventTypeDTO();
    eventTypeDTO.setId(1);
    eventTypeDTO.setName("Pitch Convincente");
    eventTypeDTO.setScoreModifier(5);
  }

  @Test
  void findAll_ShouldReturnAllEventTypes() {

    when(eventTypeRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(eventType));

    List<EventTypeDTO> result = eventTypeService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Pitch Convincente", result.get(0).getName());
    assertEquals(5, result.get(0).getScoreModifier());
    verify(eventTypeRepository).findAll(any(Sort.class));
  }

  @Test
  void get_WithValidId_ShouldReturnEventType() {

    when(eventTypeRepository.findById(1)).thenReturn(Optional.of(eventType));

    EventTypeDTO result = eventTypeService.get(1);

    assertNotNull(result);
    assertEquals("Pitch Convincente", result.getName());
    assertEquals(5, result.getScoreModifier());
    verify(eventTypeRepository).findById(1);
  }

  @Test
  void get_WithInvalidId_ShouldThrowNotFoundException() {

    when(eventTypeRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> eventTypeService.get(99));
    verify(eventTypeRepository).findById(99);
  }

  @Test
  void create_ShouldSaveAndReturnId() {

    when(eventTypeRepository.save(any(EventType.class))).thenReturn(eventType);

    Integer result = eventTypeService.create(eventTypeDTO);

    assertEquals(1, result);
    verify(eventTypeRepository).save(any(EventType.class));
  }

  @Test
  void update_WithValidId_ShouldUpdateEventType() {

    when(eventTypeRepository.findById(1)).thenReturn(Optional.of(eventType));
    when(eventTypeRepository.save(any(EventType.class))).thenReturn(eventType);

    eventTypeDTO.setName("Pitch Atualizado");
    eventTypeDTO.setScoreModifier(10);

    eventTypeService.update(1, eventTypeDTO);

    assertEquals("Pitch Atualizado", eventType.getName());
    assertEquals(10, eventType.getScoreModifier());
    verify(eventTypeRepository).findById(1);
    verify(eventTypeRepository).save(eventType);
  }

  @Test
  void update_WithInvalidId_ShouldThrowNotFoundException() {

    when(eventTypeRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> eventTypeService.update(99, eventTypeDTO));
    verify(eventTypeRepository).findById(99);
    verify(eventTypeRepository, never()).save(any(EventType.class));
  }

  @Test
  void delete_ShouldCallRepository() {

    eventTypeService.delete(1);

    verify(eventTypeRepository).deleteById(1);
  }

  @Test
  void getReferencedWarning_WithBattleEventReference_ShouldReturnWarning() {

    when(eventTypeRepository.findById(1)).thenReturn(Optional.of(eventType));

    BattleEvent battleEvent = new BattleEvent();
    battleEvent.setId(1);
    battleEvent.setEventType(eventType);
    when(battleEventRepository.findFirstByEventType(eventType)).thenReturn(battleEvent);

    ReferencedWarning result = eventTypeService.getReferencedWarning(1);

    assertNotNull(result);
    assertEquals("eventType.battleEvent.eventType.referenced", result.getKey());
    assertEquals(1, result.getParams().get(0));
    verify(eventTypeRepository).findById(1);
    verify(battleEventRepository).findFirstByEventType(eventType);
  }

  @Test
  void getReferencedWarning_WithNoReferences_ShouldReturnNull() {

    when(eventTypeRepository.findById(1)).thenReturn(Optional.of(eventType));
    when(battleEventRepository.findFirstByEventType(eventType)).thenReturn(null);

    ReferencedWarning result = eventTypeService.getReferencedWarning(1);

    assertNull(result);
    verify(eventTypeRepository).findById(1);
    verify(battleEventRepository).findFirstByEventType(eventType);
  }
}