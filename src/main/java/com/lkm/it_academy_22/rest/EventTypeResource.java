package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.EventTypeDTO;
import com.lkm.it_academy_22.service.EventTypeService;
import com.lkm.it_academy_22.util.exceptions.ReferencedException;
import com.lkm.it_academy_22.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/event-types", produces = MediaType.APPLICATION_JSON_VALUE)
public class EventTypeResource {

    private final EventTypeService eventTypeService;

    public EventTypeResource(final EventTypeService eventTypeService) {
        this.eventTypeService = eventTypeService;
    }

    @GetMapping
    public ResponseEntity<List<EventTypeDTO>> getAllEventTypes() {
        return ResponseEntity.ok(eventTypeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventTypeDTO> getEventType(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(eventTypeService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createEventType(
            @RequestBody @Valid final EventTypeDTO eventTypeDTO) {
        final Integer createdId = eventTypeService.create(eventTypeDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateEventType(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final EventTypeDTO eventTypeDTO) {
        eventTypeService.update(id, eventTypeDTO);
        return ResponseEntity.ok(id);
    }

}
