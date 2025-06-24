package com.example.dashy_platforms.domaine.helper;

import com.example.dashy_platforms.infrastructure.database.repositeries.TemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

public class CodeGenerator {
    @Autowired
    private TemplateRepository templateRepository;

    public static String generateTemplateCode() {
        return "TEMPLATE-" + UUID.randomUUID().toString();
    }
}
