package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.Battle;
import com.lkm.it_academy_22.domain.Startup;
import com.lkm.it_academy_22.domain.Tournament;
import com.lkm.it_academy_22.domain.TournamentStartup;
import com.lkm.it_academy_22.model.BattleDTO;
import com.lkm.it_academy_22.model.TournamentDTO;
import com.lkm.it_academy_22.repos.BattleRepository;
import com.lkm.it_academy_22.repos.StartupRepository;
import com.lkm.it_academy_22.repos.TournamentRepository;
import com.lkm.it_academy_22.repos.TournamentStartupRepository;
import com.lkm.it_academy_22.util.exceptions.NotFoundException;
import com.lkm.it_academy_22.util.ReferencedWarning;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final StartupRepository startupRepository;
    private final TournamentStartupRepository tournamentStartupRepository;
    private final BattleRepository battleRepository;
    private final BattleService battleService;

    public TournamentService(final TournamentRepository tournamentRepository,
                             final StartupRepository startupRepository,
                             final TournamentStartupRepository tournamentStartupRepository,
                             final BattleRepository battleRepository, BattleService battleService) {
        this.tournamentRepository = tournamentRepository;
        this.startupRepository = startupRepository;
        this.tournamentStartupRepository = tournamentStartupRepository;
        this.battleRepository = battleRepository;
        this.battleService = battleService;
    }

    public List<TournamentDTO> findAll() {
        final List<Tournament> tournaments = tournamentRepository.findAll(Sort.by("id"));
        return tournaments.stream()
                .map(tournament -> mapToDTO(tournament, new TournamentDTO()))
                .toList();
    }

    public TournamentDTO get(final Integer id) {
        return tournamentRepository.findById(id)
                .map(tournament -> mapToDTO(tournament, new TournamentDTO()))
                .orElseThrow(NotFoundException::new);
    }

    @Transactional(readOnly = true)
    public List<BattleDTO> getBattlesByTournamentId(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(NotFoundException::new);

        return tournament.getTournamentBattles().stream()
                .map(battle -> battleService.mapToDTO(battle, new BattleDTO()))
                .collect(Collectors.toList());
    }

    public boolean existsById(Integer id) {
        return tournamentRepository.existsById(id);
    }

    public Integer create(final TournamentDTO tournamentDTO) {
        final Tournament tournament = new Tournament();
        mapToEntity(tournamentDTO, tournament);
        tournament.setCreatedAt(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC));
        return tournamentRepository.save(tournament).getId();
    }

    public void update(final Integer id, final TournamentDTO tournamentDTO) {
        final Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(tournamentDTO, tournament);
        tournamentRepository.save(tournament);
    }

    public void delete(final Integer id) {
        tournamentRepository.deleteById(id);
    }

    private TournamentDTO mapToDTO(final Tournament tournament, final TournamentDTO tournamentDTO) {
        tournamentDTO.setId(tournament.getId());
        tournamentDTO.setName(tournament.getName());
        tournamentDTO.setCreatedAt(tournament.getCreatedAt());
        tournamentDTO.setStatus(tournament.getStatus());
        tournamentDTO.setChampion(tournament.getChampion() == null ? null : tournament.getChampion().getId());
        return tournamentDTO;
    }

    private Tournament mapToEntity(final TournamentDTO tournamentDTO, final Tournament tournament) {
        tournament.setName(tournamentDTO.getName());
        tournament.setStatus(tournamentDTO.getStatus());
        final Startup champion = tournamentDTO.getChampion() == null ? null : startupRepository.findById(tournamentDTO.getChampion())
                .orElseThrow(() -> new NotFoundException("champion not found"));
        tournament.setChampion(champion);
        return tournament;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Tournament tournament = tournamentRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final TournamentStartup tournamentTournamentStartup = tournamentStartupRepository.findFirstByTournament(tournament);
        if (tournamentTournamentStartup != null) {
            referencedWarning.setKey("tournament.tournamentStartup.tournament.referenced");
            referencedWarning.addParam(tournamentTournamentStartup.getId());
            return referencedWarning;
        }
        final Battle tournamentBattle = battleRepository.findFirstByTournament(tournament);
        if (tournamentBattle != null) {
            referencedWarning.setKey("tournament.battle.tournament.referenced");
            referencedWarning.addParam(tournamentBattle.getId());
            return referencedWarning;
        }
        return null;
    }

}
