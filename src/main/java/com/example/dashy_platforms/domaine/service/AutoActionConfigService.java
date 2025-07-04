package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.domaine.model.MessageText.InstagramMessageRequest;
import com.example.dashy_platforms.infrastructure.database.entities.AutoActionConfigEntity;
import com.example.dashy_platforms.infrastructure.database.entities.Company;

import java.util.List;

public interface AutoActionConfigService {
    List<AutoActionConfigEntity> getActionsByCompany(Company company);
    InstagramMessageResponse sendTextMessage(String message);


}
