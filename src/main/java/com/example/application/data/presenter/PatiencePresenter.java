package com.example.application.data.presenter;

import java.util.List;
import java.util.Set;

import com.example.application.data.entity.Doctor;
import org.springframework.stereotype.Component;
import com.example.application.data.entity.Patience;
import com.example.application.data.service.PatienceService;

@Component
public class PatiencePresenter {

    PatienceService patienceService;

    public PatiencePresenter(PatienceService patienceService) {

        this.patienceService = patienceService;

    }

    public static String translateGender(String gender) {
        String result = gender;
        if(gender.equals("Male")){
            result = "Erkek";
        }else if(gender.equals("Female")){
            result = "Kadın";
        }else if(gender.equals("Other")){
            result = "Diğer";
        }

        return result;
    }

    public List<Patience> findAllPatience(String stringFilter) {

        List<Patience> patienceList = patienceService.findAllPatience(stringFilter);

        return patienceList;
    }

    public static String formatPhoneNumber(String phoneNumber) {

        if (phoneNumber.length() == 10) {
            return "(" + phoneNumber.substring(0, 3) + ")" +
                    phoneNumber.substring(3);
        } else {
            return phoneNumber; // Return the original phone number if it doesn't match the expected length
        }
    }

    public void flush(){
        patienceService.flush();
    }

    public Patience saveAndFlush(Patience h){
        return patienceService.saveAndFlush(h);
    }

    public Patience findById(String id){

        Patience patience = patienceService.findById(id);

        return patience;
    }

    public long countpatience() {
        return patienceService.countPatience();
    }

    public void deletePatience(Patience patience) {
        patienceService.deletePatience(patience);
    }

    public void savePatience(Patience patience) {
        if (patience == null) {
            System.err.println("patience is null. Are you sure you have connected your form to the application?");
            return;
        }
        patienceService.savePatience(patience);
    }

    public Set<Doctor> getRelatedPersonnels(Patience patience) {


       return  patience.getPersonnelSet();

    }

    public long getPatienceCount(){
        return patienceService.getPatienceCount();
    }
}
