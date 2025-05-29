package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.GenericTemplateData;
import com.example.dashy_platforms.domaine.model.InstagramMessageRequest;
import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;

public interface IInstagramService {
    InstagramMessageResponse sendTextMessage(String recipientId, String message);
    InstagramMessageResponse sendGenericTemplate(String recipientId, GenericTemplateData templateData);
    InstagramMessageResponse sendMessage(InstagramMessageRequest request);
    void processPendingMessages();
}
