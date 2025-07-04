package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.domaine.service.IMessage;
import com.example.dashy_platforms.infrastructure.database.entities.*;
import com.example.dashy_platforms.infrastructure.database.repositeries.InstagramUserRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service

public class MessageServiceImp implements IMessage {
    @Autowired
   private CompanyService companyService;
    @Autowired
    private InstagramUserRepository instagramUserRepository;
    @Autowired
    private MessageRepository messageRepository;



@Override
    public void markMessageAsSeen(String mid) {
        Optional<MessageEntity> optionalMessage = messageRepository.findByMessageId(mid);
        if (optionalMessage.isPresent()) {
            MessageEntity message = optionalMessage.get();
            message.setStatus("SEEN");
            messageRepository.save(message);
            System.out.println("✅ Message marked as SEEN: " + mid);
        } else {
            System.out.println("⚠️ Message with ID " + mid + " not found.");
        }
    }

    @Override
    public void saveIncomingMessage(String senderId,String recipientId, String content, String type, LocalDateTime sentAt ,String mid) {
        MessageEntity message = new MessageEntity();
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setMessageContent(content);
        message.setMessageType(type);
        message.setStatus("RECEIVED");
        message.setMessageId(mid);
        message.setSentAt(sentAt);
        message.setCreatedAt(LocalDateTime.now());

        Company company = companyService.getCompanyByname("DASHY");
        if (company == null) {
            throw new RuntimeException("❌ Company not found. Cannot save message.");
        }

        message.setCompany(company);
        messageRepository.save(message);
        System.out.println("💾 Message saved from " + senderId);
    }
    @Override
    public void updateMessageStatus(String messageId, String status) {
        Optional<MessageEntity> optional = messageRepository.findByMessageId(messageId);
        if (optional.isPresent()) {
            MessageEntity message = optional.get();
            message.setStatus(status);
            messageRepository.save(message);
        }
    }

    public void addReactionToMessage(String mid, String reaction) {
        Optional<MessageEntity> optionalMessage = messageRepository.findByMessageId(mid);
        if (optionalMessage.isPresent()) {
            MessageEntity msg = optionalMessage.get();
            msg.setReaction(reaction);
            messageRepository.save(msg);
            System.out.println("✅ Reaction saved: " + reaction);
        } else {
            System.out.println("⚠️ Message not found for reaction (mid: " + mid + ")");
        }
    }

}