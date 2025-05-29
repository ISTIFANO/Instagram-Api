package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.*;

public interface IInstagramService {
    InstagramMessageResponse sendTextMessage(String recipientId, String message);
    InstagramMessageResponse sendGenericTemplate(String recipientId, InstagramTemplateRequest templateData);
    InstagramMessageResponse sendMessage(InstagramTemplateRequest request);
    void processPendingMessages();
}
