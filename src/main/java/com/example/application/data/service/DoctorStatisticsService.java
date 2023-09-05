package com.example.application.data.service;

import com.example.application.data.entity.Doctor;
import com.example.application.data.statistics.DoctorStatistics;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DoctorStatisticsService {

    private DoctorService service;

    public DoctorStatisticsService(DoctorService service){
        this.service = service;
    }

    public DoctorStatistics getPersonnelStatistics(String personnelId){

        Doctor personnel = service.findById(personnelId);
        DoctorStatistics ps = new DoctorStatistics(personnel);

        return ps;
    }

    public List<DoctorStatistics> getAllPersonnelStatistics() {
        List<Doctor> list = service.findAllPersonnel("");

        List<DoctorStatistics> stats = new ArrayList<>();

        for(Doctor p : list){
            stats.add(new DoctorStatistics(p));
        }
        return stats;
    }
}
