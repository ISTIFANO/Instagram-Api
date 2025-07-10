package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.*;
import com.example.dashy_platforms.domaine.model.BroadcastMessage.*;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachmentDto;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachementResponse;
import com.example.dashy_platforms.domaine.model.MediaAttachment.AttachmentRequest;
import com.example.dashy_platforms.domaine.model.BroadcastMessage.Message;
import com.example.dashy_platforms.domaine.model.MessageMedia.MessageFileRequest;
import com.example.dashy_platforms.domaine.model.MessageSticker.InstagramStickerRequest;
import com.example.dashy_platforms.domaine.model.MessageText.InstagramMessageRequest;
import com.example.dashy_platforms.domaine.model.Reaction.ReactionContainer;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies_Request;
import com.example.dashy_platforms.infrastructure.database.entities.MessageEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface IInstagramService {

    // Basic Message Operations
    InstagramMessageResponse sendTextMessage(InstagramMessageRequest messageRequest);
    InstagramMessageResponse sendGenericTemplate(String recipientId, InstagramTemplateRequest templateData);
    InstagramMessageResponse sendButtonTemplate(String recipientId, InstagramButtonTemplateRequest templateRequest);
    InstagramMessageResponse sendQuick_repliesTemplate(Quick_replies_Request quickReplies);
    InstagramMessageResponse sendSticker(InstagramStickerRequest request);
    InstagramMessageResponse sendReaction(ReactionContainer request);

    // Media Operations
    InstagramMessageResponse sendImageMessage(AttachmentRequest messageRequest);
    AttachementResponse uploadAttachment(AttachmentDto attachmentRequest);
    MessageFileRequest UploadFile(AttachementResponse attachmentResponse, Recipient recipient);
    String uploadMediaAndGetAttachmentId(MultipartFile file) throws Exception;
    InstagramMessageResponse sendMediaByAttachmentId(String recipientId, String attachmentId, String mediaType);

    // Conversation Management
    List<String> getActiveConversations();
    List<Message> getConversationMessages(String conversationId);
    Set<String> getActiveUsers();

    // Bulk Messaging Operations
    Map<String, Boolean> sendTextToAllActiveUsers(String messageText);
    Map<String, Boolean> sendMediaToAllActiveUsers(String attachmentId, String mediaType);
    Map<String, Boolean> sendTemplateToAllActiveUsers(MessageTemplate template);
    Map<String, Boolean> sendCustomMessageToAllActiveUsers(InstagramMessageR request);

    // Message Processing
    @Scheduled(fixedRate = 60000)
    void processPendingMessages();

    // User Management
    Set<UserListInfoResponse> listMessagedUsers();

    // Message Statistics
    Map<String, Long> getMessageStatsByCompanyAndDate(Long companyId, LocalDate date);
    Map<String, Long> getMessageStatusStats(Long companyId);

    // Utility Methods
    MessageEntity getVideoMessageByContent(String messageContent) throws Exception;
}