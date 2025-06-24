package com.example.dashy_platforms.domaine.service;

import com.example.dashy_platforms.domaine.model.InstagramMessageResponse;
import com.example.dashy_platforms.domaine.model.InstagramTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.Button_Template.InstagramButtonTemplateRequest;
import com.example.dashy_platforms.domaine.model.Template.QuickReplie.Quick_replies_Request;

public interface ITemplateService {

    InstagramMessageResponse sendGenericTemplate(String recipientId, InstagramTemplateRequest templateData);

    InstagramMessageResponse sendButtonTemplate(String recipientId, InstagramButtonTemplateRequest templateData);

    InstagramMessageResponse sendQuick_repliesTemplate(Quick_replies_Request templateData);

     InstagramTemplateRequest getTemplateDataByCode(String code);

}
