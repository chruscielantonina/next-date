package com.chrus.nextdate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;


@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String name;
    private LocalDate previousDate;
    private LocalDate nextDate;
    private int timePeriod;
    @Enumerated(EnumType.STRING)
    private TimeUnit timeUnit;
    private Boolean notificationEnabled;
    private int daysBeforeNotification;

    public Appointment(String name, LocalDate previousDate, LocalDate nextDate, int timePeriod, TimeUnit timeUnit,Boolean notificationEnabled, int daysBeforeNotification) {
        this.name = name;
        this.previousDate = previousDate;
        this.nextDate = nextDate;
        this.timePeriod = timePeriod;
        this.timeUnit = timeUnit;
        this.notificationEnabled = notificationEnabled;
        this.daysBeforeNotification = daysBeforeNotification;
    }
}
