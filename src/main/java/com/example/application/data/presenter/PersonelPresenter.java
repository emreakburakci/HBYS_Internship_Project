package com.example.application.data.presenter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.application.data.entity.Personel;
import com.example.application.data.service.PersonelService;

@Component
public class PersonelPresenter {
    
    PersonelService personelService;

    public PersonelPresenter(PersonelService personelService){

        this.personelService = personelService;
        
    }



     public List<Personel> findAllPersonel(String stringFilter) {
        
            List<Personel> personelList = personelService.findAllPersonel(stringFilter);

            personelList.forEach(p -> p.setTelefon(formatPhoneNumber(p.getTelefon())));

            return personelList;
        
    }

    private String formatPhoneNumber(String phoneNumber) {

        if (phoneNumber.length() == 10) {
            return "(" + phoneNumber.substring(0, 3) + ")" +
                    phoneNumber.substring(3);
        } else {
            return phoneNumber; // Return the original phone number if it doesn't match the expected length
        }
    }
    public long countPersonel() {
        return personelService.countPersonel();
    }

    public void deletePersonel(Personel personel) {
        personelService.deletePersonel(personel);
    }

    public void savePersonel(Personel personel) {
        if (personel == null) {
            System.err.println("Hasta is null. Are you sure you have connected your form to the application?");
            return;
        }
        personelService.savePersonel(personel);
    }



    public Personel findById(String id) {
       return personelService.findById(id);

      
    }



    public Personel saveAndFlush(Personel personel) {
        return personelService.saveAndFlush(personel);
    }



    public static String removeParanthesisFromTel(String telefon) {
        telefon = telefon.substring(1,4) + telefon.substring(5);
        return telefon;
    }
}
