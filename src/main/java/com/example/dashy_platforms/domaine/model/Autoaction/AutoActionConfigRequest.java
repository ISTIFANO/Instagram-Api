package com.example.dashy_platforms.domaine.model.Autoaction;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoActionConfigRequest {

    @NotBlank
    private String message;

    @NotNull
    private String messageType;

    @NotNull
    private Long companyId;
}