package com.example.application.data.service;

import com.example.application.data.entity.Hasta;
import com.example.application.data.repository.HastaRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class HastaService {

    private final HastaRepository hastaRepository;


    public HastaService(HastaRepository hastaRepository) {
        this.hastaRepository = hastaRepository;
        System.out.println("HASTA REPOSİTORY NESNE "+ hastaRepository.getClass());

    }

    public void flush(){
        hastaRepository.flush();
    }

    public Hasta saveAndFlush(Hasta h){
        return hastaRepository.saveAndFlush(h);
    }
    public Hasta findById(String id){

       return  hastaRepository.findById(id).get();

    }

    public List<Hasta> findAllHasta(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return hastaRepository.findAll();
        } else {
            return hastaRepository.search(stringFilter);
        }
    }

    public long countHasta() {
        return hastaRepository.count();
    }

    public void deleteHasta(Hasta hasta) {
        hastaRepository.delete(hasta);
    }

    public void saveHasta(Hasta hasta) {
        if (hasta == null) {
            System.err.println("Hasta is null. Are you sure you have connected your form to the application?");
            return;
        }
        hastaRepository.save(hasta);
    }

}
