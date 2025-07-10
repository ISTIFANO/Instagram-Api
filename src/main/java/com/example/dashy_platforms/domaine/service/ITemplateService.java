package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.BroadcastMessage.InstagramMessageR;
import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.domaine.model.InstagramTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies_Request;

import java.util.Map;

/**
 * Service interface for managing Instagram message templates and their delivery.
 */
public interface ITemplateService {

    /**
     * Sends a generic template message to a specific recipient
     */
    InstagramMessageResponse sendGenericTemplate(String recipientId, InstagramTemplateRequest templateData);

    /**
     * Sends a button template message to a specific recipient
     */
    InstagramMessageResponse sendButtonTemplate(String recipientId, InstagramButtonTemplateRequest templateData);

    /**
     * Sends a quick replies template message
     */
    InstagramMessageResponse sendQuick_repliesTemplate(Quick_replies_Request templateData);

    /**
     * Retrieves template data by its unique code
     */
    InstagramTemplateRequest getTemplateDataByCode(String code);

    /**
     * Retrieves quick replies template by its unique code
     */
    InstagramMessageR getQuick_replies(String code);

    /**
     * Sends a button template to all active users
     * @return Map of user IDs to send status (true = successful)
     */
    Map<String, Boolean> sendButtonTemplateToAllActiveUsers(InstagramButtonTemplateRequest templateRequest);

    /**
     * Retrieves button template by its unique code
     */
    InstagramButtonTemplateRequest getTemplatebutton(String code);
}