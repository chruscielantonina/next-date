package com.chrus.nextdate.service;

import com.chrus.nextdate.model.Appointment;
import com.chrus.nextdate.repository.AppointmentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
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

    public Appointment createAppointment(Appointment appointment) {
        if (appointment.getNextDate() == null) {
            if (appointment.getPreviousDate() != null && appointment.getTimePeriod() > 0 && appointment.getTimeUnit() != null) {
                LocalDate calculatedNextDate = switch (appointment.getTimeUnit()) {
                    case DAYS -> appointment.getPreviousDate().plusDays(appointment.getTimePeriod());
                    case WEEKS -> appointment.getPreviousDate().plusWeeks(appointment.getTimePeriod());
                    case MONTHS -> appointment.getPreviousDate().plusMonths(appointment.getTimePeriod());
                    case YEARS -> appointment.getPreviousDate().plusYears(appointment.getTimePeriod());
                };
                appointment.setNextDate(calculatedNextDate);
            }
        }
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(UUID id, Appointment appointmentDetails) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        appointment.setName(appointmentDetails.getName());
        appointment.setPreviousDate(appointmentDetails.getPreviousDate());
        appointment.setNextDate(appointmentDetails.getNextDate());
        appointment.setTimePeriod(appointmentDetails.getTimePeriod());
        appointment.setTimeUnit(appointmentDetails.getTimeUnit());
        appointment.setNotificationEnabled(appointmentDetails.getNotificationEnabled());
        appointment.setDaysBeforeNotification(appointmentDetails.getDaysBeforeNotification());

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointmentById(UUID id) {
        if (!appointmentRepository.existsById(id)) {
            throw new RuntimeException("Appointment not found with id: " + id);
        }
        appointmentRepository.deleteById(id);
    }
}