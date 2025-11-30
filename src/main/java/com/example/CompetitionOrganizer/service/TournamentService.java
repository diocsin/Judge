package com.example.CompetitionOrganizer.service;

import com.example.CompetitionOrganizer.dto.FighterResponseDto;
import com.example.CompetitionOrganizer.model.HitLocation;
import com.example.CompetitionOrganizer.model.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class TournamentService {

    private final PairService pairService;
    private final LocationService locationService;
    private final Random random = new Random();

    @Autowired
    public TournamentService(PairService pairService, LocationService locationService) {
        this.pairService = pairService;
        this.locationService = locationService;
    }

    public FighterResponseDto.Fighter organizerCompetition() {

        List<FighterResponseDto.Fighter> allParticipantAndRunTournament = pairService.getAllParticipantAndRunTournament();
        Collections.shuffle(allParticipantAndRunTournament);
        FighterResponseDto.Fighter fighter = createFight(allParticipantAndRunTournament);
        return fighter;
    }


    public FighterResponseDto.Fighter createFight(List<FighterResponseDto.Fighter> fighterList) {

        List<FighterResponseDto.Fighter> fighters = new ArrayList<>(fighterList);
        List<FighterResponseDto.Fighter> nextRound = new ArrayList<>();

        for (int i = 0; i < fighters.size(); i += 2) {
            if (fighters.size() > 1) {
                FighterResponseDto.Fighter fighterFirst = fighters.get(i);
                FighterResponseDto.Fighter fighterSecond = (i + 1 < fighters.size()) ? fighters.get(i + 1) : null;
                if (fighterSecond == null) {
                    // если нечётное число участников, 1 автоматически проходит
                    nextRound.add(fighterFirst);
                } else {
                    FighterResponseDto.Fighter winner = fight(fighterFirst, fighterSecond);
                    savePairInBaz(fighterFirst, fighterSecond, winner);
                    nextRound.add(winner);
                }
            }
        }
        if (nextRound.size() == 1) {
            // Это последний победитель
            return nextRound.get(0);
        } else {
            fighters = nextRound;
            return createFight(fighters);
        }
    }

    public void savePairInBaz(FighterResponseDto.Fighter fighter, FighterResponseDto.Fighter
            fighter1, FighterResponseDto.Fighter winner) {
        Pair pair = Pair.builder()
                .participantIdFirst(fighter.getId())
                .participantIdSecond(fighter1.getId())
                .winnerId(winner.getId())
                .build();
        pairService.savePair(pair);
    }


    public FighterResponseDto.Fighter fight(FighterResponseDto.Fighter first, FighterResponseDto.Fighter second) {

        Random random = new Random();
        FighterResponseDto.Fighter winner = null;

        while (true) {
            int powerFirst = random.nextInt(first.getPower() + 10);
            int powerSecond = random.nextInt(second.getPower() + 10);
            double dodgeChanceFirst = random.nextDouble(first.getDodgeChance() + 1);
            double dodgeChanceSecond = random.nextDouble(second.getDodgeChance() + 1);

            if (dodgeChanceFirst < first.getDodgeChance()) {
                int i = first.getHeilsFighters() - powerSecond;
                first.setHeilsFighters(i);
                System.out.println(first.getName() + " пропустил урон " + powerSecond + " осталоссь HP " + i);
                if (first.getHeilsFighters() <= 0) {
                    winner = second;
                    break;
                }
            } else {
                System.out.println(second.getName() + " промахнулся ");
            }

            if (dodgeChanceSecond < second.getDodgeChance()) {
                int i = second.getHeilsFighters() - powerFirst;
                second.setHeilsFighters(i);
                System.out.println(second.getName() + " пропустил урон " + powerFirst + " осталоссь HP " + i);
                if (second.getHeilsFighters() <= 0) {
                    winner = first;
                    break;
                }
            } else {
                System.out.println(first.getName() + " промахнулся ");
            }
        }
        return winner;
    }


    private boolean isAlive(FighterResponseDto.Fighter fighter) {
        if (fighter.getHeilsFighters() > 0) {
            return true;
        } else {
            return false;
        }
    }

    private int hitDamage(FighterResponseDto.Fighter fighter, HitLocation hitLocation) {
        int fighterPower = fighter.getPower();
        double damageMultiplier = hitLocation.getDamageMultiplier();
        return (int) (fighterPower * damageMultiplier);
    }

    private boolean dodgeChance(FighterResponseDto.Fighter fighter) {
        double randomDodgeChance = random.nextDouble(0, 2.0);
        if (randomDodgeChance < fighter.getDodgeChance()) {
            return true;
        }
        return false;
    }

    public FighterResponseDto.Fighter createFight2(FighterResponseDto.Fighter first, FighterResponseDto.Fighter second, HitLocation hitLocation) {
        FighterResponseDto.Fighter winner = null;
        while (isAlive(first) && isAlive(second)) {
            fight2(first, second, hitLocation);
            fight2(second, first, hitLocation);
            if (isAlive(first) == false) {
                winner = second;
            } else if (isAlive(second) == false) {
                winner = first;
            }
        }
        return winner;
    }


    public void fight2(FighterResponseDto.Fighter first, FighterResponseDto.Fighter second, HitLocation hitLocation) {

        if (isAlive(first)) {
            if (dodgeChance(first) == true) {
                int damage = hitDamage(second, hitLocation);
                int heils = first.getHeilsFighters() - damage;
                first.setHeilsFighters(heils);
            } else if (isAlive(first) == false) {
                System.out.println(first.getName() + " Бить не может мертв");
            }
        } else {
            System.out.println(second.getName() + " ударив в " + hitLocation.getDisplayName() + "промахнулся");
        }

    }

}



