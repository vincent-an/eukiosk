package com.example.Kiosk.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "Call_log")
@Getter
@Setter
@NoArgsConstructor
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "call_id")
    private Long callId;

    private Boolean isChecked;

    @Column(name = "callTime")
    private LocalDateTime callTime;
}
