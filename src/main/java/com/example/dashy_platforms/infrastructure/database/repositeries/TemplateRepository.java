package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.TemplateInstagram;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TemplateRepository extends JpaRepository<TemplateInstagram, Long> {
    boolean existsByCode(String code);
    Optional<TemplateInstagram> findById(Long id);
    @Query("SELECT t FROM TemplateInstagram t WHERE t.code = :code")
    Optional<TemplateInstagram> findByCode(@Param("code") String code);




}
