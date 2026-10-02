package com.chrus.nextdate.controller;

import com.chrus.nextdate.model.Appointment;
import com.chrus.nextdate.model.User;
import com.chrus.nextdate.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {
    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @GetMapping
    public List<Appointment> findAllAppointments(@AuthenticationPrincipal User user) {
        return appointmentService.findAllForUser(user);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment createAppointment(@Valid @RequestBody Appointment appointment,
                                         @AuthenticationPrincipal User user) {
        return appointmentService.createAppointment(appointment, user);
    }

    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable UUID id,
                                         @Valid @RequestBody Appointment appointment,
                                         @AuthenticationPrincipal User user) {
        return appointmentService.updateAppointment(id, appointment, user);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointmentById(@PathVariable UUID id,
                                      @AuthenticationPrincipal User user) {
        appointmentService.deleteAppointmentById(id, user);
    }
}