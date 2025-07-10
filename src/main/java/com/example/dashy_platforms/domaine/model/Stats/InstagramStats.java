package com.example.dashy_platforms.domaine.model.Stats;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstagramStats {
    private int totalcontact;
    private long received;
    private long failed;
    private long pending;
    private long sent;
    private  int activeusers;


}
