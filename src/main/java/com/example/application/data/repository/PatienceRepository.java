package com.example.application.data.repository;

import com.example.application.data.entity.Patience;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PatienceRepository extends JpaRepository<Patience, String> {

    @Query("select h from Patience h " +
        "where lower(h.name) like lower(concat('%', :searchTerm, '%')) " +
        "or lower(h.lastName) like lower(concat('%', :searchTerm, '%'))")
    List<Patience> search(@Param("searchTerm") String searchTerm);

    @Query(value = "SELECT COUNT(*) FROM patience", nativeQuery = true)
    long getPatienceCount();


}
