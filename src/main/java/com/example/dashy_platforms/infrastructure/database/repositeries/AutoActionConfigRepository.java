package com.example.dashy_platforms.infrastructure.database.repositeries;

import com.example.dashy_platforms.infrastructure.database.entities.AutoActionConfigEntity;
import com.example.dashy_platforms.infrastructure.database.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AutoActionConfigRepository extends JpaRepository<AutoActionConfigEntity, Long> {
    List<AutoActionConfigEntity> findByCompany(Company company);
    List<AutoActionConfigEntity> findByCompany_Id(long id);

    AutoActionConfigEntity findByCompany_IdAndCompany_Id(long id, long companyId);

}
