package com.lkm.it_academy_22.rest;

import com.lkm.it_academy_22.model.BattleDTO;
import com.lkm.it_academy_22.service.BattleService;
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
@RequestMapping(value = "/api/battles", produces = MediaType.APPLICATION_JSON_VALUE)
public class BattleResource {

    private final BattleService battleService;

    public BattleResource(final BattleService battleService) {
        this.battleService = battleService;
    }

    @GetMapping
    public ResponseEntity<List<BattleDTO>> getAllBattles() {
        return ResponseEntity.ok(battleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BattleDTO> getBattle(@PathVariable(name = "id") final Integer id) {
        return ResponseEntity.ok(battleService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<Integer> createBattle(@RequestBody @Valid final BattleDTO battleDTO) {
        final Integer createdId = battleService.create(battleDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Integer> updateBattle(@PathVariable(name = "id") final Integer id,
            @RequestBody @Valid final BattleDTO battleDTO) {
        battleService.update(id, battleDTO);
        return ResponseEntity.ok(id);
    }

}
