package com.chrus.nextdate.service;

import com.chrus.nextdate.model.Appointment;
import com.chrus.nextdate.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> findAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Optional<Appointment> findAppointmentById(UUID id) {
        return appointmentRepository.findById(id);
    }

    public Appointment createAppointment(Appointment appointment) {
        if (appointment.getPreviousDate() != null && appointment.getTimePeriod() > 0 && appointment.getTimeUnit() != null) {

            LocalDate calculatedNextDate = switch (appointment.getTimeUnit()) {
                case DAYS -> appointment.getPreviousDate().plusDays(appointment.getTimePeriod());
                case WEEKS -> appointment.getPreviousDate().plusWeeks(appointment.getTimePeriod());
                case MONTHS -> appointment.getPreviousDate().plusMonths(appointment.getTimePeriod());
                case YEARS -> appointment.getPreviousDate().plusYears(appointment.getTimePeriod());
            };

            appointment.setNextDate(calculatedNextDate);
        }

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointmentById(UUID id) {
        appointmentRepository.deleteById(id);
    }
}
