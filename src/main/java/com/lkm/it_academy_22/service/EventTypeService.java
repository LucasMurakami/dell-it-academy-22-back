package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.BattleEvent;
import com.lkm.it_academy_22.domain.EventType;
import com.lkm.it_academy_22.model.EventTypeDTO;
import com.lkm.it_academy_22.repos.BattleEventRepository;
import com.lkm.it_academy_22.repos.EventTypeRepository;
import com.lkm.it_academy_22.util.exceptions.NotFoundException;
import com.lkm.it_academy_22.util.ReferencedWarning;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class EventTypeService {

    private final EventTypeRepository eventTypeRepository;
    private final BattleEventRepository battleEventRepository;

    public EventTypeService(final EventTypeRepository eventTypeRepository,
            final BattleEventRepository battleEventRepository) {
        this.eventTypeRepository = eventTypeRepository;
        this.battleEventRepository = battleEventRepository;
    }

    public List<EventTypeDTO> findAll() {
        final List<EventType> eventTypes = eventTypeRepository.findAll(Sort.by("id"));
        return eventTypes.stream()
                .map(eventType -> mapToDTO(eventType, new EventTypeDTO()))
                .toList();
    }

    public EventTypeDTO get(final Integer id) {
        return eventTypeRepository.findById(id)
                .map(eventType -> mapToDTO(eventType, new EventTypeDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final EventTypeDTO eventTypeDTO) {
        final EventType eventType = new EventType();
        mapToEntity(eventTypeDTO, eventType);
        return eventTypeRepository.save(eventType).getId();
    }

    public void update(final Integer id, final EventTypeDTO eventTypeDTO) {
        final EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(eventTypeDTO, eventType);
        eventTypeRepository.save(eventType);
    }

    public void delete(final Integer id) {
        eventTypeRepository.deleteById(id);
    }

    private EventTypeDTO mapToDTO(final EventType eventType, final EventTypeDTO eventTypeDTO) {
        eventTypeDTO.setId(eventType.getId());
        eventTypeDTO.setName(eventType.getName());
        eventTypeDTO.setScoreModifier(eventType.getScoreModifier());
        return eventTypeDTO;
    }

    private EventType mapToEntity(final EventTypeDTO eventTypeDTO, final EventType eventType) {
        eventType.setName(eventTypeDTO.getName());
        eventType.setScoreModifier(eventTypeDTO.getScoreModifier());
        return eventType;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final EventType eventType = eventTypeRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final BattleEvent eventTypeBattleEvent = battleEventRepository.findFirstByEventType(eventType);
        if (eventTypeBattleEvent != null) {
            referencedWarning.setKey("eventType.battleEvent.eventType.referenced");
            referencedWarning.addParam(eventTypeBattleEvent.getId());
            return referencedWarning;
        }
        return null;
    }

}
