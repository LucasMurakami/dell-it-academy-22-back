package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.BattleDTO;
import com.lkm.it_academy_22.model.TournamentDTO;
import com.lkm.it_academy_22.service.TournamentService;
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
@RequestMapping(value = "/api/tournaments", produces = MediaType.APPLICATION_JSON_VALUE)
public class TournamentResource {

    private final TournamentService tournamentService;

    public TournamentResource(final TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public ResponseEntity<List<TournamentDTO>> getAllTournaments() {
        return ResponseEntity.ok(tournamentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TournamentDTO> getTournament(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(tournamentService.get(id));
    }

    @GetMapping("/{id}/battles")
    public ResponseEntity<List<BattleDTO>> getTournamentBattles(@PathVariable Integer id) {
        if (!tournamentService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        List<BattleDTO> battles = tournamentService.getBattlesByTournamentId(id);
        return ResponseEntity.ok(battles);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createTournament(
            @RequestBody @Valid final TournamentDTO tournamentDTO) {
        final Integer createdId = tournamentService.create(tournamentDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateTournament(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final TournamentDTO tournamentDTO) {
        tournamentService.update(id, tournamentDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteTournament(@PathVariable(name = "id") final Integer id) {
        final ReferencedWarning referencedWarning = tournamentService.getReferencedWarning(id);
        if (referencedWarning != null) {
            throw new ReferencedException(referencedWarning);
        }
        tournamentService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
