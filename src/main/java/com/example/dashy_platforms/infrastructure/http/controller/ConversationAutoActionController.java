package com.example.dashy_platforms.infrastructure.http.controller;

import com.example.dashy_platforms.domaine.model.Autoaction.ConversationAutoActionDTO;
import com.example.dashy_platforms.domaine.model.Autoaction.ConversationAutoActionRequest;
import com.example.dashy_platforms.domaine.model.Autoaction.ConversationAutoActionResponse;
import com.example.dashy_platforms.domaine.model.Autoaction.MessageAutoActionDTO;
import com.example.dashy_platforms.infrastructure.database.entities.Company;
import com.example.dashy_platforms.infrastructure.database.entities.ConversationAutoAction;
import com.example.dashy_platforms.infrastructure.database.entities.MessageAutoAction;
import com.example.dashy_platforms.infrastructure.database.repositeries.AutoactionRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.CompanyRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.ConversationAutoActionRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.MessageAutoActionRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.hibernate.annotations.NotFound;
import org.hibernate.sql.exec.ExecutionException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.config.ConfigDataResourceNotFoundException;
import org.springframework.expression.ExpressionException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.lang.module.ResolutionException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/instagram")
public class ConversationAutoActionController {

    @Autowired
    private final ConversationAutoActionRepository conversationAutoActionRepository;
    @Autowired
    private final MessageAutoActionRepository messageAutoActionRepository;
    @Autowired
    private final CompanyRepository companyRepository;

  private ModelMapper modelMapper;
    public ConversationAutoActionController(ConversationAutoActionRepository conversationAutoActionRepository,MessageAutoActionRepository messageAutoActionRepository, CompanyRepository companyRepository) {
        this.conversationAutoActionRepository = conversationAutoActionRepository;
        this.messageAutoActionRepository = messageAutoActionRepository;
        this.companyRepository = companyRepository;
    }

    @PostMapping("/Auto")
    public ResponseEntity<ConversationAutoActionResponse> createAutoAction(
            @Valid @RequestBody ConversationAutoActionRequest request) {

        // Verify company exists
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new NullPointerException("Company not found with id: " + request.getCompanyId()));

        // Create main auto action
        ConversationAutoAction autoAction = new ConversationAutoAction();
        autoAction.setResponseMessage(request.getResponseMessage());
        autoAction.setMessageType(request.getMessageType());
        autoAction.setStatus(request.getStatus());
        autoAction.setPriority(request.getPriority());
        autoAction.setCompany(company);

        ConversationAutoAction savedAction = conversationAutoActionRepository.save(autoAction);

        // Create triggers
        List<MessageAutoAction> triggers = request.getTriggers().stream()
                .map(triggerRequest -> {
                    MessageAutoAction trigger = new MessageAutoAction();
                    trigger.setTriggerKeyword(triggerRequest.getTriggerKeyword());
                    trigger.setMatchType(triggerRequest.getMatchType());
                    trigger.setCaseSensitive(triggerRequest.getCaseSensitive());
                    trigger.setConversationAutoAction(savedAction);
                    return trigger;
                })
                .collect(Collectors.toList());

        messageAutoActionRepository.saveAll(triggers);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new ConversationAutoActionResponse(
                        savedAction.getId(),
                        "Auto-reply action created successfully",
                        LocalDateTime.now()));
    }
    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<ConversationAutoActionDTO>> getAutoActionsByCompany(
            @PathVariable Long companyId,
            @RequestParam(required = false) ConversationAutoAction.ActionStatus status) {
ModelMapper modelMapper = new ModelMapper();
        List<ConversationAutoAction> autoActions = status != null ?
                conversationAutoActionRepository.findByCompanyIdAndStatus(companyId, status) :
                conversationAutoActionRepository.findByCompanyId(companyId);

        List<ConversationAutoActionDTO> response = autoActions.stream()
                .map(action -> {
                    ConversationAutoActionDTO dto = modelMapper.map(action, ConversationAutoActionDTO.class);
                    List<MessageAutoAction> triggers = messageAutoActionRepository
                            .findByConversationAutoActionId(action.getId());
                    dto.setTriggers(triggers.stream()
                            .map(trigger -> modelMapper.map(trigger, MessageAutoActionDTO.class))
                            .collect(Collectors.toList()));
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }
    @PatchMapping("/Conversation")
    public ResponseEntity<ConversationAutoActionResponse> updateStatus(
            @RequestBody Map<String, Object> requestBody) {

        try {
            Long id = Long.valueOf(requestBody.get("id").toString());
            ConversationAutoAction.ActionStatus status = ConversationAutoAction.ActionStatus.valueOf(
                    requestBody.get("status").toString()
            );

            ConversationAutoAction autoAction = conversationAutoActionRepository.findById(id)
                    .orElseThrow(() -> new ExecutionException("Auto-action not found with id: " + id));

            autoAction.setStatus(status);
            conversationAutoActionRepository.save(autoAction);

            return ResponseEntity.ok(new ConversationAutoActionResponse(
                    autoAction.getId(),
                    "Status updated successfully",
                    LocalDateTime.now()));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ConversationAutoActionResponse(
                    null,
                    "Invalid status value. Allowed values: ACTIVE, INACTIVE, PAUSED",
                    LocalDateTime.now()));
        } catch (NullPointerException e) {
            return ResponseEntity.badRequest().body(new ConversationAutoActionResponse(
                    null,
                    "Both id and status are required in request body",
                    LocalDateTime.now()));
        }
    }
    @PutMapping("/ConversationAutoAction/{id}")
    @Transactional
    public ResponseEntity<ConversationAutoActionResponse> updateAutoAction(
            @PathVariable Long id,
            @Valid @RequestBody ConversationAutoActionRequest request) {

        ConversationAutoAction existingAction = conversationAutoActionRepository.findById(id)
                .orElseThrow(() -> new ResolutionException("Auto-action not found with id: " + id));

        // Update main action
        existingAction.setResponseMessage(request.getResponseMessage());
        existingAction.setMessageType(request.getMessageType());
        existingAction.setStatus(request.getStatus());
        existingAction.setPriority(request.getPriority());

        ConversationAutoAction updatedAction = conversationAutoActionRepository.save(existingAction);

        // Delete existing triggers
        messageAutoActionRepository.deleteByConversationAutoActionId(id);

        // Create new triggers
        List<MessageAutoAction> triggers = request.getTriggers().stream()
                .map(triggerRequest -> {
                    MessageAutoAction trigger = new MessageAutoAction();
                    trigger.setTriggerKeyword(triggerRequest.getTriggerKeyword());
                    trigger.setMatchType(triggerRequest.getMatchType());
                    trigger.setCaseSensitive(triggerRequest.getCaseSensitive());
                    trigger.setConversationAutoAction(updatedAction);
                    return trigger;
                })
                .collect(Collectors.toList());

        messageAutoActionRepository.saveAll(triggers);

        return ResponseEntity.ok(new ConversationAutoActionResponse(
                updatedAction.getId(),
                "Auto-reply action updated successfully",
                LocalDateTime.now()));
    }

    @DeleteMapping("/ConversationAutoAction/{id}")
    @Transactional
    public ResponseEntity<Void> deleteAutoAction(@PathVariable Long id) {
        System.out.println(id);
        ConversationAutoAction autoAction = conversationAutoActionRepository.findById(id)
                .orElseThrow(() -> new ExecutionException("Auto-action not found with id: " + id));
        messageAutoActionRepository.deleteByConversationAutoActionId(id);

        conversationAutoActionRepository.delete(autoAction);

        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(ConfigDataResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(Exception ex) {

        return new ResponseEntity<>("delete", HttpStatus.NOT_FOUND);
    }
}
