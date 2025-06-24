package com.example.dashy_platforms.domaine.model.ScheduleMessage;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageContent {

    @JsonProperty("code")
    @Nullable
    private String code;


    @Nullable
    @JsonProperty("text")
    private String text;
}
