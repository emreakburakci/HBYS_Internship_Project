package com.example.application.data.service;

import com.example.application.data.entity.Personel;
import com.example.application.data.repository.PersonelRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PersonelService {

    private final PersonelRepository personelRepository;


    public PersonelService(PersonelRepository personelRepository) {
        this.personelRepository = personelRepository;

    }

    public List<Personel> findAllPersonel(String stringFilter) {
        if (stringFilter == null || stringFilter.isEmpty()) {
            return personelRepository.findAll();
        } else {
            return personelRepository.search(stringFilter);
        }
    }

    public long countPersonel() {
        return personelRepository.count();
    }

    public void deletePersonel(Personel personel) {
        personelRepository.delete(personel);
    }

    public void savePersonel(Personel personel) {
        if (personel == null) {
            System.err.println("Hasta is null. Are you sure you have connected your form to the application?");
            return;
        }
        personelRepository.save(personel);
    }

    public Personel findById(String id) {
       return personelRepository.findById(Long.parseLong(id)).get();
    }

    public Personel saveAndFlush(Personel personel) {
        return personelRepository.saveAndFlush(personel);
    }

}
