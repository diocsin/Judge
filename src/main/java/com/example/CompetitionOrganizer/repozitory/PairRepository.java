package com.example.CompetitionOrganizer.repozitory;

import com.example.CompetitionOrganizer.model.Pair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;



@Repository
public interface PairRepository extends JpaRepository<Pair,Long> {


}
