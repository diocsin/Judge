package com.example.CompetitionOrganizer.repozitory;

import com.example.CompetitionOrganizer.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

}
