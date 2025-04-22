package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.BattleEventDTO;
import com.lkm.it_academy_22.service.BattleEventService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(value = "/api/battle-events", produces = MediaType.APPLICATION_JSON_VALUE)
public class BattleEventResource {

    private final BattleEventService battleEventService;

    public BattleEventResource(final BattleEventService battleEventService) {
        this.battleEventService = battleEventService;
    }

    @GetMapping
    public ResponseEntity<List<BattleEventDTO>> getAllBattleEvents() {
        return ResponseEntity.ok(battleEventService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BattleEventDTO> getBattleEvent(
            @PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(battleEventService.get(id));
    }

    @GetMapping("/by-battle-tournament-startup")
    public ResponseEntity<List<BattleEventDTO>> getBattleEventsByBattleAndStartup(
            @RequestParam Integer battleId,
            @RequestParam Integer startupId) {
        List<BattleEventDTO> battleEvents = battleEventService.findByBattleAndTournamentStartup(battleId, startupId);
        return ResponseEntity.ok(battleEvents);
    }

    @GetMapping("/tournament-startup/{tournamentStartupId}")
    @ApiResponse(responseCode = "200")
    public ResponseEntity<List<BattleEventDTO>> findByTournamentStartup(
            @PathVariable final Integer tournamentStartupId) {
        return ResponseEntity.ok(battleEventService.findByTournamentStartup(tournamentStartupId));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createBattleEvent(
            @RequestBody @Valid final BattleEventDTO battleEventDTO) {
        final Integer createdId = battleEventService.create(battleEventDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateBattleEvent(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final BattleEventDTO battleEventDTO) {
        battleEventService.update(id, battleEventDTO);
        return ResponseEntity.ok(id);
    }

}
