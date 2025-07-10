package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.infrastructure.database.entities.AutoActionConfigEntity;
import com.example.dashy_platforms.infrastructure.database.entities.Company;

import java.util.List;

public interface IAutoActionConfigService {
    /**
     * Retrieves all auto-action configurations for a specific company
     * @param company The company entity
     * @return List of auto-action configurations
     */
    List<AutoActionConfigEntity> getActionsByCompany(Company company);

    /**
     * Sends a text message using the configured auto-action message
     * @param recipientId The ID of the message recipient
     * @return Response containing message status and ID
     */
    InstagramMessageResponse sendTextMessage(String recipientId);
}
