package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.*;
import com.lkm.it_academy_22.model.BattleEventDTO;
import com.lkm.it_academy_22.repos.*;
import com.lkm.it_academy_22.util.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class BattleEventService {

    private final BattleEventRepository battleEventRepository;
    private final BattleRepository battleRepository;
    private final TournamentStartupRepository tournamentStartupRepository;
    private final EventTypeRepository eventTypeRepository;

    public BattleEventService(final BattleEventRepository battleEventRepository,
            final BattleRepository battleRepository,
            final TournamentStartupRepository tournamentStartupRepository,
            final TournamentRepository tournament,
            final EventTypeRepository eventTypeRepository
            ) {
        this.battleEventRepository = battleEventRepository;
        this.battleRepository = battleRepository;
        this.tournamentStartupRepository = tournamentStartupRepository;
        this.eventTypeRepository = eventTypeRepository;
    }

    public List<BattleEventDTO> findAll() {
        final List<BattleEvent> battleEvents = battleEventRepository.findAll(Sort.by("id"));
        return battleEvents.stream()
                .map(battleEvent -> mapToDTO(battleEvent, new BattleEventDTO()))
                .toList();
    }

    public List<BattleEventDTO> findByBattleAndTournamentStartup(final Integer battleId, final Integer startupId) {
        Battle battle = battleRepository.findById(battleId)
                .orElseThrow(() -> new NotFoundException("Battle not found with ID: " + battleId));

        TournamentStartup startup = tournamentStartupRepository.findById(startupId)
                .orElseThrow(() -> new NotFoundException("Tournament startup not found with ID: " + startupId));

        List<BattleEvent> battleEvents = battleEventRepository.findByBattleAndTournamentStartup(battle, startup);

        return battleEvents.stream()
                .map(battleEvent -> mapToDTO(battleEvent, new BattleEventDTO()))
                .toList();
    }

    public List<BattleEventDTO> findByTournamentStartup(final Integer tournamentStartupId) {
        TournamentStartup tournamentStartup = tournamentStartupRepository.findById(tournamentStartupId)
                .orElseThrow(() -> new NotFoundException("Tournament startup not found with ID: " + tournamentStartupId));

        List<BattleEvent> battleEvents = battleEventRepository.findByStartup(tournamentStartup);

        return battleEvents.stream()
                .map(battleEvent -> mapToDTO(battleEvent, new BattleEventDTO()))
                .toList();
    }

    public BattleEventDTO get(final Integer id) {
        return battleEventRepository.findById(id)
                .map(battleEvent -> mapToDTO(battleEvent, new BattleEventDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Transactional
    public Integer create(final BattleEventDTO battleEventDTO) {
        if (battleEventDTO.getStartup() == null) {
            throw new IllegalArgumentException("Startup cannot be null");
        }

        if (battleEventDTO.getBattle() == null) {
            throw new IllegalArgumentException("Battle cannot be null");
        }

        if (battleEventDTO.getEventType() == null) {
            throw new IllegalArgumentException("EventType cannot be null");
        }

        final BattleEvent battleEvent = new BattleEvent();
        mapToEntity(battleEventDTO, battleEvent);
        battleEvent.setCreatedAt(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC));

        BattleEvent savedEvent = battleEventRepository.save(battleEvent);
        Integer eventId = savedEvent.getId();

        if (battleEvent.getStartup() != null && battleEvent.getEventType() != null) {
            Integer startupId = battleEvent.getStartup().getId();

            TournamentStartup startup = tournamentStartupRepository.findById(startupId)
                    .orElseThrow(() -> new RuntimeException("TournamentStartup not found with ID: " + startupId));

            Integer scoreModifier = battleEvent.getEventType().getScoreModifier();

            if (scoreModifier != null) {
                Integer currentScore = startup.getCurrentScore();
                if (currentScore == null) {
                    currentScore = 0;
                }

                Integer newScore = currentScore + scoreModifier;
                startup.setCurrentScore(newScore);
                tournamentStartupRepository.saveAndFlush(startup);
            }
        }

        return eventId;
    }

    public void update(final Integer id, final BattleEventDTO battleEventDTO) {
        final BattleEvent battleEvent = battleEventRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(battleEventDTO, battleEvent);
        battleEventRepository.save(battleEvent);
    }

    public void delete(final Integer id) {
        battleEventRepository.deleteById(id);
    }

    private BattleEventDTO mapToDTO(final BattleEvent battleEvent,
            final BattleEventDTO battleEventDTO) {
        battleEventDTO.setId(battleEvent.getId());
        battleEventDTO.setCreatedAt(battleEvent.getCreatedAt());
        battleEventDTO.setBattle(battleEvent.getBattle() == null ? null : battleEvent.getBattle().getId());
        battleEventDTO.setStartup(battleEvent.getStartup() == null ? null : battleEvent.getStartup().getId());
        battleEventDTO.setEventType(battleEvent.getEventType() == null ? null : battleEvent.getEventType().getId());
        return battleEventDTO;
    }

    private BattleEvent mapToEntity(final BattleEventDTO battleEventDTO,
            final BattleEvent battleEvent) {
        final Battle battle = battleEventDTO.getBattle() == null ? null : battleRepository.findById(battleEventDTO.getBattle())
                .orElseThrow(() -> new NotFoundException("battle not found"));
        battleEvent.setBattle(battle);
        final TournamentStartup startup = battleEventDTO.getStartup() == null ? null : tournamentStartupRepository.findById(battleEventDTO.getStartup())
                .orElseThrow(() -> new NotFoundException("startup not found"));
        battleEvent.setStartup(startup);
        final EventType eventType = battleEventDTO.getEventType() == null ? null : eventTypeRepository.findById(battleEventDTO.getEventType())
                .orElseThrow(() -> new NotFoundException("eventType not found"));
        battleEvent.setEventType(eventType);
        return battleEvent;
    }

}
