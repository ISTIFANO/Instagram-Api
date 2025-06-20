package com.example.dashy_platforms.infrastructure.database.service;

import com.example.dashy_platforms.infrastructure.database.entities.IScheduledMessageExecutorService;
import com.example.dashy_platforms.infrastructure.database.repositeries.ScheduledMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ScheduledMessageExecutorService implements  IScheduledMessageExecutorService{
    private ScheduledMessageRepository scheduledMessageRepository;
    private IScheduledMessageExecutorService iScheduledMessageExecutorService;
    private  InstagramService instagramService;

    public ScheduledMessageExecutorService() {}
public ScheduledMessageExecutorService(ScheduledMessageRepository repo ,IScheduledMessageExecutorService iScheduledMessageExecutorService ,InstagramService instagramService) {
        this.scheduledMessageRepository = repo;
        this.iScheduledMessageExecutorService = iScheduledMessageExecutorService;
        this.instagramService = instagramService;
}
}
