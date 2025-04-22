package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.Startup;
import com.lkm.it_academy_22.domain.Tournament;
import com.lkm.it_academy_22.domain.TournamentStartup;
import com.lkm.it_academy_22.model.StartupDTO;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StartupServiceTest {

  @Mock
  private StartupRepository startupRepository;

  @Mock
  private TournamentRepository tournamentRepository;

  @Mock
  private TournamentStartupRepository tournamentStartupRepository;

  @InjectMocks
  private StartupService startupService;

  private Startup startup;
  private StartupDTO startupDTO;

  @BeforeEach
  void setUp() {
    startup = new Startup();
    startup.setId(1);
    startup.setName("Test Startup");
    startup.setSlogan("Test Slogan");
    startup.setFoundedYear(2020);
    startup.setDescription("Test Description");
    startup.setCreatedAt(OffsetDateTime.now());

    startupDTO = new StartupDTO();
    startupDTO.setName("Test Startup");
    startupDTO.setSlogan("Test Slogan");
    startupDTO.setFoundedYear(2020);
    startupDTO.setDescription("Test Description");
  }

  @Test
  void findAll_ShouldReturnAllStartups() {

    when(startupRepository.findAll(any(Sort.class))).thenReturn(Arrays.asList(startup));

    List<StartupDTO> result = startupService.findAll();

    assertNotNull(result);
    assertEquals(1, result.size());
    assertEquals("Test Startup", result.get(0).getName());
    assertEquals("Test Slogan", result.get(0).getSlogan());
    verify(startupRepository).findAll(any(Sort.class));
  }

  @Test
  void get_WithValidId_ShouldReturnStartup() {

    when(startupRepository.findById(1)).thenReturn(Optional.of(startup));

    StartupDTO result = startupService.get(1);

    assertNotNull(result);
    assertEquals("Test Startup", result.getName());
    assertEquals("Test Slogan", result.getSlogan());
    verify(startupRepository).findById(1);
  }

  @Test
  void get_WithInvalidId_ShouldThrowNotFoundException() {

    when(startupRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> startupService.get(99));
    verify(startupRepository).findById(99);
  }

  @Test
  void create_ShouldSaveAndReturnId() {

    when(startupRepository.save(any(Startup.class))).thenReturn(startup);

    Integer result = startupService.create(startupDTO);

    assertEquals(1, result);
    verify(startupRepository).save(any(Startup.class));
  }

  @Test
  void update_WithValidId_ShouldUpdateStartup() {

    when(startupRepository.findById(1)).thenReturn(Optional.of(startup));
    when(startupRepository.save(any(Startup.class))).thenReturn(startup);

    startupDTO.setName("Updated Name");

    startupService.update(1, startupDTO);

    assertEquals("Updated Name", startup.getName());
    verify(startupRepository).findById(1);
    verify(startupRepository).save(startup);
  }

  @Test
  void update_WithInvalidId_ShouldThrowNotFoundException() {

    when(startupRepository.findById(99)).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> startupService.update(99, startupDTO));
    verify(startupRepository).findById(99);
    verify(startupRepository, never()).save(any(Startup.class));
  }

  @Test
  void delete_ShouldCallRepository() {

    startupService.delete(1);

    verify(startupRepository).deleteById(1);
  }

  @Test
  void getReferencedWarning_WithNoReferences_ShouldReturnNull() {

    when(startupRepository.findById(1)).thenReturn(Optional.of(startup));
    when(tournamentRepository.findFirstByChampion(startup)).thenReturn(null);
    when(tournamentStartupRepository.findFirstByStartup(startup)).thenReturn(null);

    ReferencedWarning result = startupService.getReferencedWarning(1);

    assertNull(result);
    verify(startupRepository).findById(1);
    verify(tournamentRepository).findFirstByChampion(startup);
    verify(tournamentStartupRepository).findFirstByStartup(startup);
  }

  @Test
  void getReferencedWarning_WithTournamentReference_ShouldReturnWarning() {

    when(startupRepository.findById(1)).thenReturn(Optional.of(startup));

    Tournament tournament = new Tournament();
    tournament.setId(1);
    when(tournamentRepository.findFirstByChampion(startup)).thenReturn(tournament);

    ReferencedWarning result = startupService.getReferencedWarning(1);

    assertNotNull(result);
    assertEquals("startup.tournament.champion.referenced", result.getKey());
    assertEquals(1, result.getParams().get(0));
    verify(startupRepository).findById(1);
    verify(tournamentRepository).findFirstByChampion(startup);
  }

  @Test
  void getReferencedWarning_WithTournamentStartupReference_ShouldReturnWarning() {

    when(startupRepository.findById(1)).thenReturn(Optional.of(startup));
    when(tournamentRepository.findFirstByChampion(startup)).thenReturn(null);

    TournamentStartup tournamentStartup = new TournamentStartup();
    tournamentStartup.setId(1);
    when(tournamentStartupRepository.findFirstByStartup(startup)).thenReturn(tournamentStartup);

    ReferencedWarning result = startupService.getReferencedWarning(1);

    assertNotNull(result);
    assertEquals("startup.tournamentStartup.startup.referenced", result.getKey());
    assertEquals(1, result.getParams().get(0));
    verify(startupRepository).findById(1);
    verify(tournamentRepository).findFirstByChampion(startup);
    verify(tournamentStartupRepository).findFirstByStartup(startup);
  }
}