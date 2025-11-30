package com.example.CompetitionOrganizer.model;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "pair")
@Entity
public class Pair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "participantIdFirst")
    private Long participantIdFirst;

    @Column(name = "participantIdSecond")
    private Long participantIdSecond;

    @Column(name = "winnerId")
    private Long winnerId;

}
