package com.example.CompetitionOrganizer.model;

import lombok.Getter;

@Getter
public enum HitLocation {
    HEAD("Голова",1.5),
    STOMACH("Туловище",1.0),
    LEGS("Ноги",0.7);

    private final String displayName;
    private final double damageMultiplier;

    HitLocation(String displayName, double damageMultiplier) {
        this.displayName = displayName;
        this.damageMultiplier = damageMultiplier;
    }
}
