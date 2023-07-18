package com.example.application.data.repository;

import com.example.application.data.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    @Query("select p from Personnel p " +
        "where lower(p.name) like lower(concat('%', :searchTerm, '%')) " +
        "or lower(p.lastName) like lower(concat('%', :searchTerm, '%'))")
    List<Personnel> search(@Param("searchTerm") String searchTerm);
}
