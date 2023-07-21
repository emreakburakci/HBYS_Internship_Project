package com.example.application.data.statistics;

import com.example.application.data.entity.Patience;
import com.example.application.data.entity.Personnel;

import java.util.Set;

public class PersonnelStatistics {

    private Long personnelId;
    private String fullName;
    private long patienceCount;
    private long maleGenderPatienceCount;
    private long femaleGenderPatienceCount;
    private long otherGenderPatienceCount;

    public PersonnelStatistics(Personnel personnel){
        Set<Patience> patiences = personnel.getPatienceSet();

        setFullName(personnel.getName() + " " + personnel.getLastName());
        setPatienceCount(patiences.stream().count());
        setPersonnelId(personnel.getPersonnelId());
        setOtherGenderPatienceCount(patiences.stream().filter(p -> p.getGender().equals("Diğer")).count());
        setMaleGenderPatienceCount(patiences.stream().filter(p -> p.getGender().equals("Erkek")).count());
        setFemaleGenderPatienceCount(patiences.stream().filter(p -> p.getGender().equals("Kadın")).count());
    }

    public Long getPersonnelId() {
        return personnelId;
    }

    public void setPersonnelId(Long personnelId) {
        this.personnelId = personnelId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public long getPatienceCount() {
        return patienceCount;
    }

    public void setPatienceCount(long patienceCount) {
        this.patienceCount = patienceCount;
    }

    public long getMaleGenderPatienceCount() {
        return maleGenderPatienceCount;
    }

    public void setMaleGenderPatienceCount(long maleGenderPatienceCount) {
        this.maleGenderPatienceCount = maleGenderPatienceCount;
    }

    public long getFemaleGenderPatienceCount() {
        return femaleGenderPatienceCount;
    }

    public void setFemaleGenderPatienceCount(long femaleGenderPatienceCount) {
        this.femaleGenderPatienceCount = femaleGenderPatienceCount;
    }

    public long getOtherGenderPatienceCount() {
        return otherGenderPatienceCount;
    }

    public void setOtherGenderPatienceCount(long otherGenderPatienceCount) {
        this.otherGenderPatienceCount = otherGenderPatienceCount;
    }
}
