package com.example.application.data.entity;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
public class Personel {

    @Id
    private long personelId;
    
    @NotEmpty
    private String isim = "";

    @NotEmpty
    private String soyisim = "";

    
     @Pattern(regexp="^[1-9][0-9]{9}$"
            , message="Lütfen 5XXXXXXXXX formatında giriniz!")
    private String telefon;

    @ManyToMany(mappedBy = "personelSet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Hasta> hastaSet;

    public long getPersonelId() {
        return personelId;
    }

    public void setPersonelId(long personelId) {
        this.personelId = personelId;
    }

    public String getIsim() {
        return isim;
    }

    public void setIsim(String isim) {
        this.isim = isim;
    }

    public String getSoyisim() {
        return soyisim;
    }

    public void setSoyisim(String soyisim) {
        this.soyisim = soyisim;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public Set<Hasta> getHastaSet() {
        return hastaSet;
    }

    public void setHastaSet(Set<Hasta> hastaSet) {
        this.hastaSet = hastaSet;
    }

    @Override
public boolean equals(Object o){

    if(o instanceof Personel){
        if(((Personel)o).getPersonelId() == (this.getPersonelId())){
            return true;
        }
    }

    return false;
}
    
@Override
public int hashCode(){

    String str = Long.toString(getPersonelId());
    return Integer.parseInt(str);
    
}


    
}
