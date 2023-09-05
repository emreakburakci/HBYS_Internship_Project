package com.example.application.data.service;

import com.example.application.data.entity.Doctor;
import com.example.application.data.repository.DoctorRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DoctorService {

    private final DoctorRepository personnelRepository;


    public DoctorService(DoctorRepository personnelRepository) {
        this.personnelRepository = personnelRepository;

    }

    public List<Doctor> findAllPersonnel(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return personnelRepository.findAll();
        } else {
            return personnelRepository.search(stringFilter);
        }
    }

    public long countPersonnel() {
        return personnelRepository.count();
    }

    public void deletePersonnel(Doctor personnel) {
        personnelRepository.delete(personnel);
    }

    public void savePersonnel(Doctor personnel) {
        if (personnel == null) {
            System.err.println("Hasta is null. Are you sure you have connected your form to the application?");
            return;
        }
        personnelRepository.save(personnel);
    }

    public Doctor findById(String id) {
       return personnelRepository.findById(Long.parseLong(id)).get();
    }

    public Doctor saveAndFlush(Doctor personnel) {
        return personnelRepository.saveAndFlush(personnel);
    }

}
