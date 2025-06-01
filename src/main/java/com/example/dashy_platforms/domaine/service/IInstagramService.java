package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.*;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies_Request;

import java.util.Set;

public interface IInstagramService {
    InstagramMessageResponse sendTextMessage(String recipientId, String message);
    InstagramMessageResponse sendGenericTemplate(String recipientId, InstagramTemplateRequest templateData);
    InstagramMessageResponse sendMessage(InstagramTemplateRequest request);
    public Set<UserListInfoResponse> listMessagedUsers();
        InstagramMessageResponse sendButtonTemplate(String recipientId, InstagramButtonTemplateRequest templateData);
    InstagramMessageResponse sendQuick_repliesTemplate(Quick_replies_Request templateData);

    void processPendingMessages();
}
