package competitionOrganizer.service;

import competitionOrganizer.dto.FighterResponseDto;
import competitionOrganizer.dto.FightResultDto;
import competitionOrganizer.model.HitLocation;
import competitionOrganizer.model.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Service
public class TournamentService {

    private final PairService pairService;
    private final Random random = new Random();

    @Autowired
    public TournamentService(PairService pairService) {
        this.pairService = pairService;
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

    private int hitDamage(FighterResponseDto.Fighter fighter, HitLocation hitLocation) {
        int fighterPower = fighter.getPower();
        double damageMultiplier = hitLocation.getDamageMultiplier();
        return (int) (fighterPower * damageMultiplier);
    }

    private boolean dodgeChance(FighterResponseDto.Fighter fighter) {
        double randomDodgeChance = random.nextDouble(1.2, 3.0);
        if (randomDodgeChance < fighter.getDodgeChance()) {
            return true;
        }
        return false;
    }

    public FighterResponseDto.Fighter createFight2(FighterResponseDto.Fighter first, FighterResponseDto.Fighter second, HitLocation hitLocation) {
        return fight2(first, second, hitLocation);
    }

    public FighterResponseDto.Fighter fight2(FighterResponseDto.Fighter first, FighterResponseDto.Fighter second, HitLocation hitLocation) {
        System.out.println("Бьет: " + first.getName());
        if (dodgeChance(second) == true) {
            int damage = hitDamage(first, hitLocation);
            int heils = second.getHeilsFighters() - damage;
            second.setHeilsFighters(heils);
            System.out.println(second.getName() + " пропустил удар в " + hitLocation.getDisplayName() + " осталось HP " + second.getHeilsFighters());
        } else {
            System.out.println(first.getName() + " ударив в " + hitLocation.getDisplayName() + " промахнулся");
        }
        return second;
    }

    public FightResultDto fight2WithResult(FighterResponseDto.Fighter attacker, FighterResponseDto.Fighter victim, HitLocation hitLocation) {
        int damage = hitDamage(attacker, hitLocation);
        boolean hitSuccessful = dodgeChance(victim);
        String logMessage;
        
        if (hitSuccessful) {
            int newHp = victim.getHeilsFighters() - damage;
            victim.setHeilsFighters(newHp);
            logMessage = victim.getName() + " пропустил удар в " + hitLocation.getDisplayName() + 
                        ", нанесено " + damage + " урона. У " + victim.getName() + " осталось " + newHp + " HP";
        } else {
            logMessage = attacker.getName() + " ударив в " + hitLocation.getDisplayName() + " промахнулся";
        }
        
        return new FightResultDto(
            attacker,
            victim,
            hitLocation,
            damage,
            hitSuccessful,
            victim.getHeilsFighters(),
            logMessage
        );
    }

}



