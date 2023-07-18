package com.example.application.data.service;

import com.example.application.data.entity.Personnel;
import com.example.application.data.repository.PersonnelRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonnelService {

    private final PersonnelRepository personnelRepository;


    public PersonnelService(PersonnelRepository personnelRepository) {
        this.personnelRepository = personnelRepository;

    }

    public List<Personnel> findAllPersonnel(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return personnelRepository.findAll();
        } else {
            return personnelRepository.search(stringFilter);
        }
    }

    public long countPersonnel() {
        return personnelRepository.count();
    }

    public void deletePersonnel(Personnel personnel) {
        personnelRepository.delete(personnel);
    }

    public void savePersonnel(Personnel personnel) {
        if (personnel == null) {
            System.err.println("Hasta is null. Are you sure you have connected your form to the application?");
            return;
        }
        personnelRepository.save(personnel);
    }

    public Personnel findById(String id) {
       return personnelRepository.findById(Long.parseLong(id)).get();
    }

    public Personnel saveAndFlush(Personnel personnel) {
        return personnelRepository.saveAndFlush(personnel);
    }

}
