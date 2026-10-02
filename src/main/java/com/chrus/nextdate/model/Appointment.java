package com.chrus.nextdate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

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

    @NotBlank(message = "Appointment name is required")
    @Size(max = 255, message = "Appointment name is too long")
    private String name;

    @NotNull(message = "Appointment date is required")
    private LocalDate previousDate;

    private LocalDate nextDate;

    @Min(value = 1, message = "Period must be at least 1")
    private int timePeriod;

    @NotNull(message = "Time unit is required")
    @Enumerated(EnumType.STRING)
    private TimeUnit timeUnit;

    private Boolean notificationEnabled;

    private int daysBeforeNotification;

    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    @JsonIgnore
    private User user;

    public Appointment(String name, LocalDate previousDate, LocalDate nextDate, int timePeriod, TimeUnit timeUnit, Boolean notificationEnabled, int daysBeforeNotification) {
        this.name = name;
        this.previousDate = previousDate;
        this.nextDate = nextDate;
        this.timePeriod = timePeriod;
        this.timeUnit = timeUnit;
        this.notificationEnabled = notificationEnabled;
        this.daysBeforeNotification = daysBeforeNotification;
    }
}