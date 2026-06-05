package com.chrus.nextdate.controller;

import com.chrus.nextdate.model.Appointment;
import com.chrus.nextdate.service.AppointmentService;
import org.springframework.http.HttpStatus;
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
    public List<Appointment> findAppointmentsByUserId(@RequestHeader("X-User-Id") UUID userId) {
        return appointmentService.findAppointmentsByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Appointment createAppointment(@RequestBody Appointment appointment, @RequestHeader("X-User-Id") UUID userId) {
        return appointmentService.createAppointment(appointment, userId);
    }

    @PutMapping("/{id}")
    public Appointment updateAppointment(@PathVariable UUID id,
                                         @RequestBody Appointment appointment,
                                         @RequestHeader("X-User-Id") UUID userId) {
        return appointmentService.updateAppointment(id, appointment, userId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAppointmentById(@PathVariable UUID id, @RequestHeader("X-User-Id") UUID userId) {
        appointmentService.deleteAppointmentById(id, userId);
    }
}