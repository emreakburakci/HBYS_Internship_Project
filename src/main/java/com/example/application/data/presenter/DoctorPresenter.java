package com.example.application.data.presenter;

import java.util.List;

import com.example.application.data.entity.Doctor;
import com.example.application.data.service.DoctorService;
import org.springframework.stereotype.Component;


@Component
public class DoctorPresenter {

    DoctorService personnelService;

    public DoctorPresenter(DoctorService personnelService){

        this.personnelService = personnelService;
        
    }



     public List<Doctor> findAllPersonnel(String stringFilter) {
        
            List<Doctor> personelList = personnelService.findAllPersonnel(stringFilter);

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

    public void deletePersonnel(Doctor personnel) {
        personnelService.deletePersonnel(personnel);
    }

    public void savePersonnel(Doctor personnel) {
        if (personnel == null) {
            System.err.println("Hasta is null. Are you sure you have connected your form to the application?");
            return;
        }
        personnelService.savePersonnel(personnel);
    }



    public Doctor findById(String id) {
       return personnelService.findById(id);

      
    }



    public Doctor saveAndFlush(Doctor personel) {
        return personnelService.saveAndFlush(personel);
    }




}
