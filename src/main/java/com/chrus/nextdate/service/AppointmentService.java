package com.chrus.nextdate.service;

import com.chrus.nextdate.model.Appointment;
import com.chrus.nextdate.model.User;
import com.chrus.nextdate.repository.AppointmentRepository;
import com.chrus.nextdate.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private UserRepository userRepository;

    public AppointmentService(AppointmentRepository appointmentRepository, UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.userRepository = userRepository;
    }

    public List<Appointment> findAppointmentsByUserId(UUID userId) {
        return appointmentRepository.findByUserId(userId);
    }


    public Appointment createAppointment(Appointment appointment, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        appointment.setUser(user);

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

    public Appointment updateAppointment(UUID id, Appointment appointmentDetails, UUID userId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        if (!appointment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You cannot modify someone else's appointment!");
        }

        appointment.setName(appointmentDetails.getName());
        appointment.setPreviousDate(appointmentDetails.getPreviousDate());
        appointment.setNextDate(appointmentDetails.getNextDate());
        appointment.setTimePeriod(appointmentDetails.getTimePeriod());
        appointment.setTimeUnit(appointmentDetails.getTimeUnit());
        appointment.setNotificationEnabled(appointmentDetails.getNotificationEnabled());
        appointment.setDaysBeforeNotification(appointmentDetails.getDaysBeforeNotification());

        return appointmentRepository.save(appointment);
    }

    public void deleteAppointmentById(UUID id, UUID userId) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        if (!appointment.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized: You cannot delete someone else's appointment!");
        }

        appointmentRepository.deleteById(id);
    }
}
