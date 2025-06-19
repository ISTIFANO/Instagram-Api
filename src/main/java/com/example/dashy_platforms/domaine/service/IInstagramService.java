package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.*;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachementResponse;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachmentDto;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachmentRequest;
import com.example.dashy_platforms.domaine.model.MessageMedia.MessageFileRequest;
import com.example.dashy_platforms.domaine.model.MessageText.InstagramMessageRequest;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies_Request;

import java.util.Set;

public interface IInstagramService {
    InstagramMessageResponse sendTextMessage(InstagramMessageRequest message);
    InstagramMessageResponse sendGenericTemplate(String recipientId, InstagramTemplateRequest templateData);
    public Set<UserListInfoResponse> listMessagedUsers();
        InstagramMessageResponse sendButtonTemplate(String recipientId, InstagramButtonTemplateRequest templateData);
    InstagramMessageResponse sendQuick_repliesTemplate(Quick_replies_Request templateData);
    AttachementResponse uploadAttachment(AttachmentDto attachmentRequest);
    void processPendingMessages();
    public InstagramMessageResponse sendImageMessage(AttachmentRequest messageRequest);
}
