package com.example.CompetitionOrganizer.controler;

import com.example.CompetitionOrganizer.model.Pair;
import com.example.CompetitionOrganizer.service.PairService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pairs")
public class PairController {

    private final PairService pairService;

    public PairController(PairService pairService) {
        this.pairService = pairService;
    }

    @GetMapping("getAll")
    public List<Pair> getAll(){
       return pairService.getAll();
    }
}
