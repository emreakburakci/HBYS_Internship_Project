package com.example.application.data.service;

import com.example.application.data.entity.Personnel;
import com.example.application.data.statistics.PersonnelStatistics;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PersonnelStatisticsService {

    private PersonnelService service;

    public PersonnelStatisticsService(PersonnelService service){
        this.service = service;
    }

    public PersonnelStatistics getPersonnelStatistics(String personnelId){

        Personnel personnel = service.findById(personnelId);
        PersonnelStatistics ps = new PersonnelStatistics(personnel);

        return ps;
    }

    public List<PersonnelStatistics> getAllPersonnelStatistics() {
        List<Personnel> list = service.findAllPersonnel("");

        List<PersonnelStatistics> stats = new ArrayList<>();

        for(Personnel p : list){
            stats.add(new PersonnelStatistics(p));
        }
        return stats;
    }
}
