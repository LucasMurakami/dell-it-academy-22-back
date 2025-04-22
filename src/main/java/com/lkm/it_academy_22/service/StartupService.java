package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.Startup;
import com.lkm.it_academy_22.domain.Tournament;
import com.lkm.it_academy_22.domain.TournamentStartup;
import com.lkm.it_academy_22.model.StartupDTO;
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
public class StartupService {

    private final StartupRepository startupRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentStartupRepository tournamentStartupRepository;

    public StartupService(final StartupRepository startupRepository,
            final TournamentRepository tournamentRepository,
            final TournamentStartupRepository tournamentStartupRepository) {
        this.startupRepository = startupRepository;
        this.tournamentRepository = tournamentRepository;
        this.tournamentStartupRepository = tournamentStartupRepository;
    }

    public List<StartupDTO> findAll() {
        final List<Startup> startups = startupRepository.findAll(Sort.by("id"));
        return startups.stream()
                .map(startup -> mapToDTO(startup, new StartupDTO()))
                .toList();
    }

    public StartupDTO get(final Integer id) {
        return startupRepository.findById(id)
                .map(startup -> mapToDTO(startup, new StartupDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final StartupDTO startupDTO) {
        final Startup startup = new Startup();
        mapToEntity(startupDTO, startup);
        startup.setCreatedAt(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC));
        return startupRepository.save(startup).getId();
    }

    public void update(final Integer id, final StartupDTO startupDTO) {
        final Startup startup = startupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        mapToEntity(startupDTO, startup);
        startupRepository.save(startup);
    }

    public void delete(final Integer id) {
        startupRepository.deleteById(id);
    }

    private StartupDTO mapToDTO(final Startup startup, final StartupDTO startupDTO) {
        startupDTO.setId(startup.getId());
        startupDTO.setName(startup.getName());
        startupDTO.setSlogan(startup.getSlogan());
        startupDTO.setFoundedYear(startup.getFoundedYear());
        startupDTO.setDescription(startup.getDescription());
        startupDTO.setCreatedAt(startup.getCreatedAt());
        return startupDTO;
    }

    private Startup mapToEntity(final StartupDTO startupDTO, final Startup startup) {
        startup.setName(startupDTO.getName());
        startup.setSlogan(startupDTO.getSlogan());
        startup.setFoundedYear(startupDTO.getFoundedYear());
        startup.setDescription(startupDTO.getDescription());
        return startup;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Startup startup = startupRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final Tournament championTournament = tournamentRepository.findFirstByChampion(startup);
        if (championTournament != null) {
            referencedWarning.setKey("startup.tournament.champion.referenced");
            referencedWarning.addParam(championTournament.getId());
            return referencedWarning;
        }
        final TournamentStartup startupTournamentStartup = tournamentStartupRepository.findFirstByStartup(startup);
        if (startupTournamentStartup != null) {
            referencedWarning.setKey("startup.tournamentStartup.startup.referenced");
            referencedWarning.addParam(startupTournamentStartup.getId());
            return referencedWarning;
        }
        return null;
    }

}
