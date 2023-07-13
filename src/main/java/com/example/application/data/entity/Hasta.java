package com.example.application.data.entity;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;

@Entity
public class Hasta {

    @Id
    @Pattern(regexp = "^[1-9]{1}[0-9]{9}[02468]{1}$", message = "Geçerli bir TC Kimlik Numarası giriniz!")
    private String TCNO;

    @NotEmpty
    private String isim = "";

    @NotEmpty
    private String soyisim = "";

    @NotEmpty
    private String cinsiyet;

    
    @Pattern(regexp="^[1-9][0-9]{9}$"
            , message="Lütfen 5XXXXXXXXX formatında giriniz!")
    private String telefon;

    @Email
    @NotEmpty
    private String email = "";

    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinTable(name = "hasta_personel",
    joinColumns = @JoinColumn(name ="TCNO"),
    inverseJoinColumns = @JoinColumn(name ="personelId"))
    private Set<Personel> personelSet;

    @CreationTimestamp
    private Instant createdOn;

    @UpdateTimestamp
    private Instant lastUpdatedOn;

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



    public String getTCNO() {
        return TCNO;
    }


    public void setTCNO(String tCNO) {
        TCNO = tCNO;
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


    public String getCinsiyet() {
        return cinsiyet;
    }


    public void setCinsiyet(String cinsiyet) {
        this.cinsiyet = cinsiyet;
    }


    public String getTelefon() {
        return telefon;
    }


    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }


    public String getEmail() {
        return email;
    }


    public void setEmail(String email) {
        this.email = email;
    }


    public Set<Personel> getPersonelSet() {
        return personelSet;
    }


    public void setPersonelSet(Set<Personel> personelSet) {
        this.personelSet = personelSet;
    }

@Override
public boolean equals(Object o){

    if(o instanceof Hasta){
        if(((Hasta)o).getTCNO().equals(this.getTCNO())){
            return true;
        }
    }

    return false;
}
    
@Override
public int hashCode(){

    return Integer.parseInt(getTCNO().substring(0,7));
    
}


}
