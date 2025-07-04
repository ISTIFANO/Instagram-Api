package com.example.dashy_platforms.infrastructure.http.controller;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import com.example.dashy_platforms.domaine.model.Autoaction.AutoActionConfigRequest;
import com.example.dashy_platforms.infrastructure.database.entities.AutoActionConfigEntity;
import com.example.dashy_platforms.infrastructure.database.entities.Company;
import com.example.dashy_platforms.infrastructure.database.repositeries.AutoActionConfigRepository;
import com.example.dashy_platforms.infrastructure.database.repositeries.CompanyRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auto-replies")
public class AutoActionConfigController {

    private final AutoActionConfigRepository autoActionConfigRepository;
    private final CompanyRepository companyRepository;

    public AutoActionConfigController(
            AutoActionConfigRepository autoActionConfigRepository,
            CompanyRepository companyRepository
    ) {
        this.autoActionConfigRepository = autoActionConfigRepository;
        this.companyRepository = companyRepository;
    }

    @Operation(summary = "Créer une configuration d'auto-réponse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration créée avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content)
    })
    @PostMapping("/messageconfig")
    public ResponseEntity<?> createAutoReply(@RequestBody AutoActionConfigRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        AutoActionConfigEntity config = new AutoActionConfigEntity();
        config.setMessage(request.getMessage());
        config.setMessageType(request.getMessageType());
        config.setCompany(company);

        autoActionConfigRepository.save(config);

        return ResponseEntity.ok("Auto-reply configuration saved successfully.");
    }

    @Operation(summary = "Liste toutes les configurations d'une entreprise")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Liste retournée avec succès"),
            @ApiResponse(responseCode = "404", description = "Entreprise non trouvée", content = @Content)
    })
    @GetMapping("/company/{companyId}")
    public ResponseEntity<?> getAutoRepliesByCompany(@PathVariable Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new RuntimeException("Company not found"));

        var autoReplies = autoActionConfigRepository.findByCompany(company);

        return ResponseEntity.ok(autoReplies);
    }

    @Operation(summary = "Mettre à jour une configuration d'auto-réponse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Configuration mise à jour avec succès"),
            @ApiResponse(responseCode = "404", description = "Configuration ou entreprise non trouvée", content = @Content),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content)
    })
    @PutMapping("/message/{id}")
    public ResponseEntity<?> updateAutoReply(
            @PathVariable Long id,
            @Valid @RequestBody AutoActionConfigRequest request) {

        AutoActionConfigEntity config = autoActionConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Auto-reply config not found"));

        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new RuntimeException("Company not found"));

        config.setMessage(request.getMessage());
        config.setMessageType(request.getMessageType());
        config.setCompany(company);

        autoActionConfigRepository.save(config);

        return ResponseEntity.ok("Auto-reply configuration updated successfully.");
    }
}
