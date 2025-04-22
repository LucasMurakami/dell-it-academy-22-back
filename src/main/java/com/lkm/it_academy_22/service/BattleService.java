package com.lkm.it_academy_22.service;

import com.lkm.it_academy_22.domain.Battle;
import com.lkm.it_academy_22.domain.BattleEvent;
import com.lkm.it_academy_22.domain.Tournament;
import com.lkm.it_academy_22.domain.TournamentStartup;
import com.lkm.it_academy_22.model.BattleDTO;
import com.lkm.it_academy_22.repos.BattleEventRepository;
import com.lkm.it_academy_22.repos.BattleRepository;
import com.lkm.it_academy_22.repos.TournamentRepository;
import com.lkm.it_academy_22.repos.TournamentStartupRepository;
import com.lkm.it_academy_22.util.exceptions.NotFoundException;
import com.lkm.it_academy_22.util.ReferencedWarning;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class BattleService {

    private final BattleRepository battleRepository;
    private final TournamentRepository tournamentRepository;
    private final TournamentStartupRepository tournamentStartupRepository;
    private final BattleEventRepository battleEventRepository;

    public BattleService(final BattleRepository battleRepository,
            final TournamentRepository tournamentRepository,
            final TournamentStartupRepository tournamentStartupRepository,
            final BattleEventRepository battleEventRepository) {
        this.battleRepository = battleRepository;
        this.tournamentRepository = tournamentRepository;
        this.tournamentStartupRepository = tournamentStartupRepository;
        this.battleEventRepository = battleEventRepository;
    }

    public List<BattleDTO> findAll() {
        final List<Battle> battles = battleRepository.findAll(Sort.by("id"));
        return battles.stream()
                .map(battle -> mapToDTO(battle, new BattleDTO()))
                .toList();
    }

    public BattleDTO get(final Integer id) {
        return battleRepository.findById(id)
                .map(battle -> mapToDTO(battle, new BattleDTO()))
                .orElseThrow(NotFoundException::new);
    }

    public Integer create(final BattleDTO battleDTO) {
        final Battle battle = new Battle();
        mapToEntity(battleDTO, battle);
        battle.setCreatedAt(OffsetDateTime.of(LocalDateTime.now(), ZoneOffset.UTC));
        return battleRepository.save(battle).getId();
    }

    @Transactional
    public void update(final Integer id, final BattleDTO battleDTO) {
        final Battle battle = battleRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("battle not found"));

        mapToEntity(battleDTO, battle);
        battleRepository.save(battle);

        Integer winnerId = battleDTO.getWinner();
        Integer startup1Id = battleDTO.getStartup1();
        Integer startup2Id = battleDTO.getStartup2();

        boolean bothStartupsPresent = startup1Id != null && startup2Id != null;

        if (bothStartupsPresent) {
            Integer loserId = null;

            if (winnerId != null) {
                if (Objects.equals(winnerId, startup1Id)) {
                    loserId = startup2Id;
                } else if (Objects.equals(winnerId, startup2Id)) {
                    loserId = startup1Id;
                } else {
                    throw new IllegalArgumentException("Winner ID does not match either of the startups in the battle.");
                }
            }

            processWinnerAdvancement(battle, winnerId, loserId);
        }
    }

    private void processWinnerAdvancement(Battle currentBattle, Integer winnerId, Integer loserId) {
        Integer tournamentId = currentBattle.getTournament().getId();

        TournamentStartup startup1 = currentBattle.getStartup1();
        TournamentStartup startup2 = currentBattle.getStartup2();

        if (startup1 == null || startup2 == null) {
            throw new IllegalStateException("Battle must have both startups defined.");
        }

        if (winnerId == null && Boolean.TRUE.equals(currentBattle.getSharkFight())) {

            if (!startup1.getCurrentScore().equals(startup2.getCurrentScore())) {
                throw new IllegalStateException("Shark Fight was expected, but scores are not equal.");
            }

            boolean giveToStartup1 = ThreadLocalRandom.current().nextBoolean();

            TournamentStartup winnerStartup = giveToStartup1 ? startup1 : startup2;
            TournamentStartup loserStartup = giveToStartup1 ? startup2 : startup1;

            winnerStartup.setCurrentScore(winnerStartup.getCurrentScore() + 2);

            winnerId = winnerStartup.getId();
            loserId = loserStartup.getId();

            currentBattle.setWinner(winnerStartup);
            battleRepository.save(currentBattle);
        }

        if (winnerId == null) {
            return;
        }

        TournamentStartup winnerStartup = tournamentStartupRepository.findById(winnerId)
                .orElseThrow(() -> new NotFoundException("Winner startup not found"));
        TournamentStartup loserStartup = tournamentStartupRepository.findById(loserId)
                .orElseThrow(() -> new NotFoundException("Loser startup not found"));

        int totalBattles = getTotalBattles(tournamentId);
        int currentBattleNumber = currentBattle.getBattleNumber();

        boolean isFinalBattle = (currentBattleNumber == totalBattles);

        if (!isFinalBattle) {
            int parentNumber = getParentBattleNumber(currentBattleNumber, totalBattles);

            Battle parentBattle = battleRepository.findByBattleNumberAndTournamentId(parentNumber, tournamentId);

            if (parentBattle == null) {
                throw new NotFoundException("Parent battle not found for battle number: " + currentBattleNumber);
            }

            if (currentBattleNumber % 2 == 1) {
                parentBattle.setStartup1(winnerStartup);
            } else {
                parentBattle.setStartup2(winnerStartup);
            }

            battleRepository.save(parentBattle);
        }

        final Tournament tournament = tournamentRepository.findById(tournamentId)
                .orElseThrow(() -> new NotFoundException("tournament not found"));

        tournament.setChampion(winnerStartup.getStartup());
        tournament.setStatus("COMPLETED");
        tournamentRepository.save(tournament);

        loserStartup.setEliminated(true);
        winnerStartup.setCurrentScore(winnerStartup.getCurrentScore() + 30);
        tournamentStartupRepository.saveAll(List.of(winnerStartup, loserStartup));
    }


    private int getParentBattleNumber(int battleNumber, int totalBattles) {
        int numberOfStartups = totalBattles + 1;

        int battleCountInRound = numberOfStartups / 2;
        int firstBattleInRound = 1;

        while (battleNumber > firstBattleInRound + battleCountInRound - 1) {
            firstBattleInRound += battleCountInRound;
            battleCountInRound /= 2;
        }

        if (battleNumber == totalBattles) {
            return battleNumber;
        }

        int offsetInCurrentRound = battleNumber - firstBattleInRound;

        int firstBattleInNextRound = firstBattleInRound + battleCountInRound;

        return firstBattleInNextRound + offsetInCurrentRound / 2;
    }

    private int getTotalBattles(Integer tournamentId) {
        long startupCount = tournamentStartupRepository.countByTournamentId(tournamentId);

        return (int) startupCount - 1;
    }

    public void delete(final Integer id) {
        battleRepository.deleteById(id);
    }

    public BattleDTO mapToDTO(final Battle battle, final BattleDTO battleDTO) {
        battleDTO.setId(battle.getId());
        battleDTO.setBattleNumber(battle.getBattleNumber());
        battleDTO.setRoundNumber(battle.getRoundNumber());
        battleDTO.setSharkFight(battle.getSharkFight());
        battleDTO.setCompleted(battle.getCompleted());
        battleDTO.setCreatedAt(battle.getCreatedAt());
        battleDTO.setTournament(battle.getTournament() == null ? null : battle.getTournament().getId());
        battleDTO.setStartup1(battle.getStartup1() == null ? null : battle.getStartup1().getId());
        battleDTO.setStartup2(battle.getStartup2() == null ? null : battle.getStartup2().getId());
        battleDTO.setWinner(battle.getWinner() == null ? null : battle.getWinner().getId());
        return battleDTO;
    }

    protected Battle mapToEntity(final BattleDTO battleDTO, final Battle battle) {
        battle.setBattleNumber(battleDTO.getBattleNumber());
        battle.setRoundNumber(battleDTO.getRoundNumber());
        battle.setSharkFight(battleDTO.getSharkFight());
        battle.setCompleted(battleDTO.getCompleted());
        final Tournament tournament = battleDTO.getTournament() == null ? null : tournamentRepository.findById(battleDTO.getTournament())
                .orElseThrow(() -> new NotFoundException("tournament not found"));
        battle.setTournament(tournament);
        final TournamentStartup startup1 = battleDTO.getStartup1() == null ? null : tournamentStartupRepository.findById(battleDTO.getStartup1())
                .orElseThrow(() -> new NotFoundException("startup1 not found"));
        battle.setStartup1(startup1);
        final TournamentStartup startup2 = battleDTO.getStartup2() == null ? null : tournamentStartupRepository.findById(battleDTO.getStartup2())
                .orElseThrow(() -> new NotFoundException("startup2 not found"));
        battle.setStartup2(startup2);
        final TournamentStartup winner = battleDTO.getWinner() == null ? null : tournamentStartupRepository.findById(battleDTO.getWinner())
                .orElseThrow(() -> new NotFoundException("winner not found"));
        battle.setWinner(winner);
        return battle;
    }

    public ReferencedWarning getReferencedWarning(final Integer id) {
        final ReferencedWarning referencedWarning = new ReferencedWarning();
        final Battle battle = battleRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        final BattleEvent battleBattleEvent = battleEventRepository.findFirstByBattle(battle);
        if (battleBattleEvent != null) {
            referencedWarning.setKey("battle.battleEvent.battle.referenced");
            referencedWarning.addParam(battleBattleEvent.getId());
            return referencedWarning;
        }
        return null;
    }

}
