package com.example.application.data.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import java.time.Instant;
import java.util.Set;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Entity
public class Personel {

    @Id
    @Range(min = 1, message = "Personel Id 0 yada negatif olamaz!")
    @NotNull
    private Long personelId;
    
    @NotEmpty
    private String isim = "";

    @NotEmpty
    private String soyisim = "";

     @Pattern(regexp="^[1-9][0-9]{9}$"
            , message="Lütfen 5XXXXXXXXX formatında giriniz!")
    private String telefon;

    @ManyToMany(mappedBy = "personelSet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Hasta> hastaSet;

    @UpdateTimestamp
    private Instant lastUpdatedOn;
    @CreationTimestamp
    private Instant createdOn;

    private String updatedUserId;

    private String createdUserId;

    public String getUpdatedUserId() {
        return updatedUserId;
    }

    public void setUpdatedUserId(String updatedUserId) {
        this.updatedUserId = updatedUserId;
    }

    public String getCreatedUserId() {
        return createdUserId;
    }

    public void setCreatedUserId(String createdUserId) {
        this.createdUserId = createdUserId;
    }

    public Instant getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(Instant createdOn) {

        this.createdOn = createdOn;
    }

    public Instant getLastUpdatedOn() {
        return lastUpdatedOn;
    }

    public void setLastUpdatedOn(Instant lastUpdatedOn) {
        this.lastUpdatedOn = lastUpdatedOn;
    }

    public Long getPersonelId() {
        return personelId;
    }

    public void setPersonelId(Long personelId) {
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
