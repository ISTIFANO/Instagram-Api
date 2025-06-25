package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.Autoaction;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface AutoactionRepository extends JpaRepository<Autoaction, Long> {

    @EntityGraph(attributePaths = {"company"})
    @Query("SELECT a FROM Autoaction a JOIN FETCH a.company LIMIT")
    Optional<Autoaction> findFirstWithCompany();

    @Query("SELECT a FROM Autoaction a JOIN FETCH a.company c WHERE c.name = :companyName")
    Optional<Autoaction> findByCompanyName(@Param("companyName") String companyName);



}