package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.StartupDTO;
import com.lkm.it_academy_22.service.StartupService;
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
@RequestMapping(value = "/api/startups", produces = MediaType.APPLICATION_JSON_VALUE)
public class StartupResource {

    private final StartupService startupService;

    public StartupResource(final StartupService startupService) {
        this.startupService = startupService;
    }

    @GetMapping
    public ResponseEntity<List<StartupDTO>> getAllStartups() {
        return ResponseEntity.ok(startupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<StartupDTO> getStartup(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(startupService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createStartup(@RequestBody @Valid final StartupDTO startupDTO) {
        final Integer createdId = startupService.create(startupDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateStartup(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final StartupDTO startupDTO) {
        startupService.update(id, startupDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteStartup(@PathVariable(name = "id") final Integer id) {
        final ReferencedWarning referencedWarning = startupService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        startupService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
