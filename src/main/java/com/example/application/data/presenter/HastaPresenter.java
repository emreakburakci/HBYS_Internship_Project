package com.example.application.data.presenter;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import com.example.application.data.entity.Hasta;
import com.example.application.data.entity.Personel;
import com.example.application.data.service.HastaService;

@Component
public class HastaPresenter {

    HastaService hastaService;

    public HastaPresenter(HastaService hastaService) {

        this.hastaService = hastaService;

    }

    public List<Hasta> findAllHasta(String stringFilter) {

        List<Hasta> hastaList = hastaService.findAllHasta(stringFilter);

         hastaList.forEach(h -> h.setTelefon(formatPhoneNumber(h.getTelefon())));


        return hastaList;
    }

    private String formatPhoneNumber(String phoneNumber) {

        if (phoneNumber.length() == 10) {
            return "(" + phoneNumber.substring(0, 3) + ")" +
                    phoneNumber.substring(3);
        } else {
            return phoneNumber; // Return the original phone number if it doesn't match the expected length
        }
    }

    public void flush(){
        hastaService.flush();
    }

    public Hasta saveAndFlush(Hasta h){
        return hastaService.saveAndFlush(h);
    }

    public Hasta findById(String id){
        return hastaService.findById(id);
    }

    public long countHasta() {
        return hastaService.countHasta();
    }

    public void deleteHasta(Hasta hasta) {
        hastaService.deleteHasta(hasta);
    }

    public void saveHasta(Hasta hasta) {
        if (hasta == null) {
            System.err.println("Hasta is null. Are you sure you have connected your form to the application?");
            return;
        }
        hastaService.saveHasta(hasta);
    }

    public Set<Personel> getRelatedPersonels(Hasta hasta) {

       Set<Personel> personelSet = hasta.getPersonelSet();
        personelSet.forEach(h -> h.setTelefon(formatPhoneNumber(h.getTelefon())));
       return  personelSet;

    }

    public static String removeParanthesisFromTel(String telefon) {
        telefon = telefon.substring(1,4) + telefon.substring(5);
        return telefon;


    }

}
