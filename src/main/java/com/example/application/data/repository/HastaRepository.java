package com.example.application.data.repository;

import com.example.application.data.entity.Hasta;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface HastaRepository extends JpaRepository<Hasta, String> {

    @Query("select h from Hasta h " +
        "where lower(h.isim) like lower(concat('%', :searchTerm, '%')) " +
        "or lower(h.soyisim) like lower(concat('%', :searchTerm, '%'))")
    List<Hasta> search(@Param("searchTerm") String searchTerm);
}
