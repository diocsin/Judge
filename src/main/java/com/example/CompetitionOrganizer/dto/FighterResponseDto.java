package com.example.CompetitionOrganizer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class FighterResponseDto {

    private List<Fighter> fighters;

    @NoArgsConstructor
    @Getter
    @Setter
    @AllArgsConstructor
    public static class Fighter {
        private Long id;
        private String name;
        private int age;
        private int power;
        private String beltColorFighter;
        private double dodgeChance;
        private int heilsFighters;

    }
}


