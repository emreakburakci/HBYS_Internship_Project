package com.example.application.data.presenter;

import java.util.List;

import org.springframework.stereotype.Component;

import com.example.application.data.entity.Personnel;
import com.example.application.data.service.PersonnelService;

@Component
public class PersonnelPresenter {
    
    PersonnelService personnelService;

    public PersonnelPresenter(PersonnelService personnelService){

        this.personnelService = personnelService;
        
    }



     public List<Personnel> findAllPersonnel(String stringFilter) {
        
            List<Personnel> personelList = personnelService.findAllPersonnel(stringFilter);

            return personelList;
        
    }

    public static String formatPhoneNumber(String phoneNumber) {

        if (phoneNumber.length() == 10) {
            return "(" + phoneNumber.substring(0, 3) + ")" +
                    phoneNumber.substring(3);
        } else {
            return phoneNumber; // Return the original phone number if it doesn't match the expected length
        }
    }
    public long countPersonnel() {
        return personnelService.countPersonnel();
    }

    public void deletePersonnel(Personnel personnel) {
        personnelService.deletePersonnel(personnel);
    }

    public void savePersonnel(Personnel personnel) {
        if (personnel == null) {
            System.err.println("Hasta is null. Are you sure you have connected your form to the application?");
            return;
        }
        personnelService.savePersonnel(personnel);
    }



    public Personnel findById(String id) {
       return personnelService.findById(id);

      
    }



    public Personnel saveAndFlush(Personnel personel) {
        return personnelService.saveAndFlush(personel);
    }




}
