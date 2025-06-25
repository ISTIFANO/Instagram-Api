package com.example.dashy_platforms.domaine.model.Autoaction;

import lombok.Data;
import java.util.List;

@Data
public class AutoactionConfigDTO {
    private String companyName;
    private List<String> nonWorkingDays;
    private String pauseStart;
    private String pauseEnd;
    private String workStartTime;
    private String workEndTime;
}