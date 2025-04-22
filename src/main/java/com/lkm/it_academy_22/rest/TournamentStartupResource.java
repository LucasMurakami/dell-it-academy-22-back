package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.TournamentStartupDTO;
import com.lkm.it_academy_22.service.TournamentService;
import com.lkm.it_academy_22.service.TournamentStartupService;
import com.lkm.it_academy_22.util.exceptions.ReferencedException;
import com.lkm.it_academy_22.util.ReferencedWarning;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping(value = "/api/tournament-startups", produces = MediaType.APPLICATION_JSON_VALUE)
public class TournamentStartupResource {

    private final TournamentStartupService tournamentStartupService;
    private final TournamentService tournamentService;

    public TournamentStartupResource(final TournamentStartupService tournamentStartupService, TournamentService tournamentService) {
        this.tournamentStartupService = tournamentStartupService;
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public ResponseEntity<List<TournamentStartupDTO>> getAllTournamentStartups() {
        return ResponseEntity.ok(tournamentStartupService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentStartupDTO> getTournamentStartup(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(tournamentStartupService.get(id));
    }

    @GetMapping("/{id}/startups")
    @Transactional(readOnly = true)
    public ResponseEntity<List<TournamentStartupDTO>> getTournamentStartupsByTournamentId(@PathVariable Integer id) {
        if (!tournamentService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<TournamentStartupDTO> startups = tournamentStartupService.getTournamentStartupsByTournamentId(id);
        return ResponseEntity.ok(startups);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createTournamentStartup(
            @RequestBody @Valid final TournamentStartupDTO tournamentStartupDTO) {
        final Integer createdId = tournamentStartupService.create(tournamentStartupDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateTournamentStartup(
            @PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final TournamentStartupDTO tournamentStartupDTO) {
        tournamentStartupService.update(id, tournamentStartupDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTournamentStartup(
            @PathVariable(name = "id") final Integer id) {
        final ReferencedWarning referencedWarning = tournamentStartupService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        tournamentStartupService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
