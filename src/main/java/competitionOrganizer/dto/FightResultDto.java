package competitionOrganizer.dto;

import competitionOrganizer.model.HitLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FightResultDto {
    private FighterResponseDto.Fighter attacker;
    private FighterResponseDto.Fighter victim;
    private HitLocation hitLocation;
    private int damage;
    private boolean hitSuccessful;
    private int victimHpAfter;
    private String logMessage;
}

