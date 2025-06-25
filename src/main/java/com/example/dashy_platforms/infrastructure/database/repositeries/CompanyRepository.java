package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByName(String name);

    // Méthode avec jointure explicite pour optimiser les requêtes
    @Query("SELECT c FROM Company c LEFT JOIN FETCH c.autoactions WHERE c.name = :name")
    Optional<Company> findByNameWithAutoactions(@Param("name") String name);

    // Méthode pour vérifier l'existence
    boolean existsByName(String name);

    // Méthode pour trouver par ID avec les relations chargées
    @Query("SELECT c FROM Company c LEFT JOIN FETCH c.autoactions WHERE c.id = :id")
    Optional<Company> findByIdWithAutoactions(@Param("id") Long id);
}