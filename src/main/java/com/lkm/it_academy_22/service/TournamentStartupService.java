package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.Battle;
import com.lkm.it_academy_22.domain.BattleEvent;
import com.lkm.it_academy_22.domain.Startup;
import com.lkm.it_academy_22.domain.Tournament;
import com.lkm.it_academy_22.domain.TournamentStartup;
import com.lkm.it_academy_22.model.TournamentStartupDTO;
import com.lkm.it_academy_22.repos.BattleEventRepository;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


@Service
public class TournamentStartupService {

    private final TournamentStartupRepository tournamentStartupRepository;
    private final TournamentRepository tournamentRepository;
    private final StartupRepository startupRepository;
    private final BattleRepository battleRepository;
    private final BattleEventRepository battleEventRepository;

    public TournamentStartupService(final TournamentStartupRepository tournamentStartupRepository,
            final TournamentRepository tournamentRepository,
            final StartupRepository startupRepository, final BattleRepository battleRepository,
            final BattleEventRepository battleEventRepository) {
        this.tournamentStartupRepository = tournamentStartupRepository;
        this.tournamentRepository = tournamentRepository;
        this.startupRepository = startupRepository;
        this.battleRepository = battleRepository;
        this.battleEventRepository = battleEventRepository;
    }

    public List<TournamentStartupDTO> findAll() {
        final List<TournamentStartup> tournamentStartups = tournamentStartupRepository.findAll(Sort.by("id"));
        return tournamentStartups.stream()
                .map(tournamentStartup -> mapToDTO(tournamentStartup, new TournamentStartupDTO()))
                .toList();
    }

    public TournamentStartupDTO get(final Integer id) {
        return tournamentStartupRepository.findById(id)
                .map(tournamentStartup -> mapToDTO(tournamentStartup, new TournamentStartupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public List<TournamentStartupDTO> getTournamentStartupsByTournamentId(Integer tournamentId) {
        Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(NotFoundException::new);

        return tournament.getTournamentTournamentStartups().stream()
                .map(tournamentStartup -> mapToDTO(tournamentStartup, new TournamentStartupDTO()))
                .toList();
    }

    public Integer create(final TournamentStartupDTO tournamentStartupDTO) {
        final TournamentStartup tournamentStartup = new TournamentStartup();
        mapToEntity(tournamentStartupDTO, tournamentStartup);
        tournamentStartup.setCreatedAt(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC));
        return tournamentStartupRepository.save(tournamentStartup).getId();
    }

    public void update(final Integer id, final TournamentStartupDTO tournamentStartupDTO) {
        final TournamentStartup tournamentStartup = tournamentStartupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(tournamentStartupDTO, tournamentStartup);
        tournamentStartupRepository.save(tournamentStartup);
    }

    public void delete(final Integer id) {
        tournamentStartupRepository.deleteById(id);
    }

    private TournamentStartupDTO mapToDTO(final TournamentStartup tournamentStartup,
            final TournamentStartupDTO tournamentStartupDTO) {
        tournamentStartupDTO.setId(tournamentStartup.getId());
        tournamentStartupDTO.setCurrentScore(tournamentStartup.getCurrentScore());
        tournamentStartupDTO.setEliminated(tournamentStartup.getEliminated());
        tournamentStartupDTO.setTournament(tournamentStartup.getTournament() == null ? null : tournamentStartup.getTournament().getId());
        tournamentStartupDTO.setStartup(tournamentStartup.getStartup() == null ? null : tournamentStartup.getStartup().getId());
        return tournamentStartupDTO;
    }

    private TournamentStartup mapToEntity(final TournamentStartupDTO tournamentStartupDTO,
            final TournamentStartup tournamentStartup) {
        if(tournamentStartupDTO.getCurrentScore() != null)
            tournamentStartup.setCurrentScore(tournamentStartupDTO.getCurrentScore());
        tournamentStartup.setEliminated(tournamentStartupDTO.getEliminated());
        final Tournament tournament = tournamentStartupDTO.getTournament() == null ? null : tournamentRepository.findById(tournamentStartupDTO.getTournament())
                .orElseThrow(() -> new NotFoundException("tournament not found"));
        tournamentStartup.setTournament(tournament);
        final Startup startup = tournamentStartupDTO.getStartup() == null ? null : startupRepository.findById(tournamentStartupDTO.getStartup())
                .orElseThrow(() -> new NotFoundException("startup not found"));
        tournamentStartup.setStartup(startup);
        return tournamentStartup;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final TournamentStartup tournamentStartup = tournamentStartupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Battle startup1Battle = battleRepository.findFirstByStartup1(tournamentStartup);
        if (startup1Battle != null) {
            referencedWarning.setKey("tournamentStartup.battle.startup1.referenced");
            referencedWarning.addParam(startup1Battle.getId());
            return referencedWarning;
        }
        final Battle startup2Battle = battleRepository.findFirstByStartup2(tournamentStartup);
        if (startup2Battle != null) {
            referencedWarning.setKey("tournamentStartup.battle.startup2.referenced");
            referencedWarning.addParam(startup2Battle.getId());
            return referencedWarning;
        }
        final Battle winnerBattle = battleRepository.findFirstByWinner(tournamentStartup);
        if (winnerBattle != null) {
            referencedWarning.setKey("tournamentStartup.battle.winner.referenced");
            referencedWarning.addParam(winnerBattle.getId());
            return referencedWarning;
        }
        final BattleEvent startupBattleEvent = battleEventRepository.findFirstByStartup(tournamentStartup);
        if (startupBattleEvent != null) {
            referencedWarning.setKey("tournamentStartup.battleEvent.startup.referenced");
            referencedWarning.addParam(startupBattleEvent.getId());
            return referencedWarning;
        }
        return null;
    }

}
