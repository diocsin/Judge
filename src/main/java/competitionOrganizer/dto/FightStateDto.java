package competitionOrganizer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FightStateDto {
    private FighterResponseDto.Fighter fighter1;
    private FighterResponseDto.Fighter fighter2;
    private List<String> fightLog;
    private boolean fightFinished;
    private FighterResponseDto.Fighter winner;
}

