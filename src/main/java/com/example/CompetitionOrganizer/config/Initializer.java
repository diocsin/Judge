package com.example.CompetitionOrganizer.config;

import com.example.CompetitionOrganizer.model.Location;
import com.example.CompetitionOrganizer.service.LocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class Initializer implements CommandLineRunner {

    private final LocationService locationService;

    @Autowired
    public Initializer(LocationService locationService) {
        this.locationService = locationService;
    }


    @Override
    public void run(String... args) throws Exception {
        Location location =new Location();
        location.setName("Витебск");
        locationService.createLocation(location);

    }
}
