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
public class Personnel {

    @Id
    @Range(min = 1, message = "Personel Id 0 yada negatif olamaz!")
    @NotNull
    private Long personnelId;

    @NotEmpty
    private String name = "";

    @NotEmpty
    private String lastName = "";

    @Pattern(regexp = "^[1-9][0-9]{9}$", message = "Lütfen 5XXXXXXXXX formatında giriniz!")
    private String phone;

    @ManyToMany(mappedBy = "personnelSet", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private Set<Patience> patienceSet;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Set<Patience> getPatienceSet() {
        return patienceSet;
    }

    public void setPatienceSet(Set<Patience> patienceSet) {
        this.patienceSet = patienceSet;
    }

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

    public Long getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(Long personelId) {
        this.personnelId = personelId;
    }

    @Override
    public boolean equals(Object o) {

        if (o instanceof Personnel) {
            if (((Personnel) o).getPersonnelId() == (this.getPersonnelId())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public int hashCode() {

        String str = Long.toString(getPersonnelId());
        return Integer.parseInt(str);

    }

}
