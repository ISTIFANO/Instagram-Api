package com.example.dashy_platforms.infrastructure.database.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Table(name = "autoaction")
@Getter
@Setter
public class Autoaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "non_working_days", nullable = false)
    private String nonWorkingDays;

    @Column(name = "pause_start", nullable = false)
    private LocalTime pauseStart;

    @Column(name = "pause_end", nullable = false)
    private LocalTime pauseEnd;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;
}
