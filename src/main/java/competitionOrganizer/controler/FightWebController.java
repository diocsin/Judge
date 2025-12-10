package competitionOrganizer.controler;

import competitionOrganizer.dto.FighterResponseDto;
import competitionOrganizer.dto.FightResultDto;
import competitionOrganizer.dto.FightStateDto;
import competitionOrganizer.dto.PairDto;
import competitionOrganizer.model.HitLocation;
import competitionOrganizer.service.PairService;
import competitionOrganizer.service.TournamentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/")
public class FightWebController {

    private final PairService pairService;
    private final TournamentService tournamentService;

    public FightWebController(PairService pairService, TournamentService tournamentService) {
        this.pairService = pairService;
        this.tournamentService = tournamentService;
    }

    @GetMapping
    public String showFighterSelection(Model model) {
        try {
            List<FighterResponseDto.Fighter> fighters = pairService.getAllParticipantAndRunTournament();
            model.addAttribute("fighters", fighters);
            return "fighter-selection";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при загрузке бойцов: " + e.getMessage());
            return "fighter-selection";
        }
    }

    @PostMapping("/start-fight")
    public String startFight(@RequestParam Long firstId, @RequestParam Long secondId, 
                            HttpSession session, Model model) {
        try {
            List<FighterResponseDto.Fighter> fighters = pairService.createPair(firstId, secondId);
            
            // Сохраняем бойцов в сессии (они остаются на своих местах)
            session.setAttribute("fighter1", fighters.get(0));
            session.setAttribute("fighter2", fighters.get(1));
            
            // Инициализируем лог боя
            List<String> fightLog = new ArrayList<>();
            fightLog.add("Бой начался! " + fighters.get(0).getName() + " против " + fighters.get(1).getName());
            session.setAttribute("fightLog", fightLog);
            session.setAttribute("fightFinished", false);
            // Первым бьет fighter1
            session.setAttribute("isFighter1Turn", true);
            
            return "redirect:/fight";
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при начале боя: " + e.getMessage());
            return "redirect:/";
        }
    }

    @GetMapping("/fight")
    public String showFight(HttpSession session, Model model) {
        FighterResponseDto.Fighter fighter1 = (FighterResponseDto.Fighter) session.getAttribute("fighter1");
        FighterResponseDto.Fighter fighter2 = (FighterResponseDto.Fighter) session.getAttribute("fighter2");
        List<String> fightLog = (List<String>) session.getAttribute("fightLog");
        Boolean fightFinished = (Boolean) session.getAttribute("fightFinished");
        Boolean isFighter1Turn = (Boolean) session.getAttribute("isFighter1Turn");
        
        if (fighter1 == null || fighter2 == null) {
            return "redirect:/";
        }
        
        if (isFighter1Turn == null) {
            isFighter1Turn = true;
        }
        
        FightStateDto fightState = new FightStateDto();
        fightState.setFighter1(fighter1);
        fightState.setFighter2(fighter2);
        fightState.setFightLog(fightLog != null ? fightLog : new ArrayList<>());
        fightState.setFightFinished(fightFinished != null ? fightFinished : false);
        
        FighterResponseDto.Fighter winner = (FighterResponseDto.Fighter) session.getAttribute("winner");
        if (winner != null) {
            fightState.setWinner(winner);
        }
        
        model.addAttribute("fightState", fightState);
        model.addAttribute("isFighter1Turn", isFighter1Turn);
        
        return "fight";
    }

    @PostMapping("/fight/attack")
    public String attack(@RequestParam HitLocation hitLocation, HttpSession session, Model model) {
        FighterResponseDto.Fighter fighter1 = (FighterResponseDto.Fighter) session.getAttribute("fighter1");
        FighterResponseDto.Fighter fighter2 = (FighterResponseDto.Fighter) session.getAttribute("fighter2");
        List<String> fightLog = (List<String>) session.getAttribute("fightLog");
        Boolean fightFinished = (Boolean) session.getAttribute("fightFinished");
        Boolean isFighter1Turn = (Boolean) session.getAttribute("isFighter1Turn");
        
        if (fighter1 == null || fighter2 == null || (fightFinished != null && fightFinished)) {
            return "redirect:/";
        }
        
        if (fightLog == null) {
            fightLog = new ArrayList<>();
        }
        
        if (isFighter1Turn == null) {
            isFighter1Turn = true; // По умолчанию первый бьет
        }
        
        // Определяем атакующего и жертву
        FighterResponseDto.Fighter attacker = isFighter1Turn ? fighter1 : fighter2;
        FighterResponseDto.Fighter victim = isFighter1Turn ? fighter2 : fighter1;
        
        // Выполняем удар
        FightResultDto result = tournamentService.fight2WithResult(attacker, victim, hitLocation);
        fightLog.add(result.getLogMessage());
        
        // Обновляем HP жертвы
        if (isFighter1Turn) {
            // fighter1 бил fighter2, обновляем fighter2
            session.setAttribute("fighter2", result.getVictim());
        } else {
            // fighter2 бил fighter1, обновляем fighter1
            session.setAttribute("fighter1", result.getVictim());
        }
        
        // Проверяем, закончен ли бой
        if (result.getVictimHpAfter() <= 0) {
            fightLog.add("Победил " + attacker.getName() + "!");
            session.setAttribute("fightFinished", true);
            session.setAttribute("winner", attacker);
        } else {
            // Меняем очередь удара (но не меняем местами бойцов)
            session.setAttribute("isFighter1Turn", !isFighter1Turn);
        }
        
        session.setAttribute("fightLog", fightLog);
        
        return "redirect:/fight";
    }
}

