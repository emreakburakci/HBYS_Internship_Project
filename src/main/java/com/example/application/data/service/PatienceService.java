package com.example.application.data.service;

import com.example.application.data.entity.Patience;
import com.example.application.data.repository.PatienceRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PatienceService {

    private final PatienceRepository patienceRepository;


    public PatienceService(PatienceRepository patienceRepository) {
        this.patienceRepository = patienceRepository;

    }

    public void flush(){
        patienceRepository.flush();
    }

    public Patience saveAndFlush(Patience patience){
        return patienceRepository.saveAndFlush(patience);
    }
    public Patience findById(String id){

       return  patienceRepository.findById(id).get();

    }

    public List<Patience> findAllPatience(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return patienceRepository.findAll();
        } else {
            return patienceRepository.search(stringFilter);
        }
    }

    public long countPatience() {
        return patienceRepository.count();
    }

    public void deletePatience(Patience patience) {
        patienceRepository.delete(patience);
    }

    public void savePatience(Patience patience) {
        if (patience == null) {
            System.err.println("Hasta is null. Are you sure you have connected your form to the application?");
            return;
        }
        patienceRepository.save(patience);
    }

}
