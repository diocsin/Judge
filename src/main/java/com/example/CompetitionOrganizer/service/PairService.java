package com.example.CompetitionOrganizer.service;


import com.example.CompetitionOrganizer.dto.FighterResponseDto;
import com.example.CompetitionOrganizer.model.Pair;
import com.example.CompetitionOrganizer.repozitory.PairRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PairService {

    @Value(value = "${fightClub.api.base-url}")
    String REMOTE_FIGHTER_API_URL;

    private final RestTemplate restTemplate;
    private final PairRepository pairRepository;

    @Autowired
    public PairService(RestTemplate restTemplate, PairRepository pairRepository) {
        this.restTemplate = restTemplate;
        this.pairRepository = pairRepository;
    }

    public List<FighterResponseDto.Fighter> getAllParticipantAndRunTournament() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<String>("param", headers);
        try {
            ResponseEntity<FighterResponseDto> exchange = restTemplate.exchange(
                    REMOTE_FIGHTER_API_URL + "/api/fighter/getAll", HttpMethod.GET, entity,
                    FighterResponseDto.class);

            FighterResponseDto body = exchange.getBody();
            List<FighterResponseDto.Fighter> fighters = body.getFighters();

            return fighters;

        } catch (Exception e) {
            throw new RuntimeException("Ошибка при получении учасников с сервера");
        }
    }

    public List<FighterResponseDto.Fighter> createPair(Long firstId, Long secondId) {
        List<FighterResponseDto.Fighter> fighters = new ArrayList<>(2);

        List<FighterResponseDto.Fighter> fighterList = getAllParticipantAndRunTournament();

        FighterResponseDto.Fighter first = fighterList.stream()
                .filter(fighter -> fighter.getId().equals(firstId))
                .findFirst().orElse(null);

        FighterResponseDto.Fighter second = fighterList.stream()
                .filter(fighter -> fighter.getId().equals(secondId))
                .findFirst().orElse(null);
        fighters.add(first);
        fighters.add(second);
        return fighters;
    }

    public void savePair(Pair pair) {
        pairRepository.save(pair);
    }

    public List<Pair> getAll() {
        return pairRepository.findAll();
    }

}
