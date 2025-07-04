package com.example.dashy_platforms.infrastructure.database.service;


import com.example.dashy_platforms.domaine.service.IMessageAutoAction;
import com.example.dashy_platforms.infrastructure.database.entities.ConversationAutoAction;
import com.example.dashy_platforms.infrastructure.database.entities.MessageAutoAction;
import com.example.dashy_platforms.infrastructure.database.repositeries.MessageAutoActionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class MessageAutoActionService {
 @Autowired
 private MessageAutoActionRepository messageAutoActionRepository;




}
