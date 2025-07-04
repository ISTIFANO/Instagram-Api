package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.service.IUserInstagram;
import com.example.dashy_platforms.infrastructure.database.entities.Company;
import com.example.dashy_platforms.infrastructure.database.entities.InstagramUserEntity;
import com.example.dashy_platforms.infrastructure.database.repositeries.CompanyRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.InstagramUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserInstagram implements IUserInstagram {

    @Autowired
    private InstagramUserRepository instagramUserRepository;
 @Autowired
 private CompanyService companyService;

 @Override
    public  Boolean userExists(String instagramUserId) {
        return instagramUserRepository.existsByInstagramUserId(instagramUserId);
    }

    @Override
     public void saveInstagramUserIfNotExists(String instagramUserId) {
        if (!instagramUserRepository.existsByInstagramUserId(instagramUserId)) {
            InstagramUserEntity user = new InstagramUserEntity();
            user.setInstagramUserId(instagramUserId);
            user.setStatus(InstagramUserEntity.InstagramUserStatus.ACTIVE);
            Company company = companyService.getCompanyByname("DASHY");
            if (company == null) {
                throw new RuntimeException("❌ Company not found. Cannot save message.");
            }

            user.setCompany(company);
            instagramUserRepository.save(user);
            System.out.println("🆕 New Instagram user saved: " + instagramUserId);
        }
    }


}
