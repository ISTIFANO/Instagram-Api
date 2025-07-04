package com.example.dashy_platforms.domaine.service;

import org.springframework.scheduling.annotation.Scheduled;

public interface IScheduledMessageExecutorService {
    /**
     * Exécute les messages planifiés dont l'heure d'exécution est atteinte.
     * Cette méthode est exécutée périodiquement toutes les minutes (60000 ms).
     * Elle gère différents types de messages (TEXT, MEDIA, TEMPLATE, etc.)
     * et met à jour leur statut après exécution.
     */
    @Scheduled(fixedRate = 60000)
    void executeScheduledMessages();
}
