package com.example.application.data.repository;

import com.example.application.data.entity.Personel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PersonelRepository extends JpaRepository<Personel, Long> {

    @Query("select p from Personel p " +
        "where lower(p.isim) like lower(concat('%', :searchTerm, '%')) " +
        "or lower(p.soyisim) like lower(concat('%', :searchTerm, '%'))")
    List<Personel> search(@Param("searchTerm") String searchTerm);
}
