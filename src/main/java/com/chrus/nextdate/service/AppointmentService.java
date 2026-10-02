package com.chrus.nextdate.service;

import com.chrus.nextdate.model.Appointment;
import com.chrus.nextdate.model.User;
import com.chrus.nextdate.repository.AppointmentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;

    public AppointmentService(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    public List<Appointment> findAllForUser(User user) {
        return appointmentRepository.findByUserIdOrderByNextDateAsc(user.getId());
    }

    public Appointment createAppointment(Appointment appointment, User user) {
        // The client must not choose the id, otherwise save() would overwrite someone else's appointment
        appointment.setId(null);
        appointment.setUser(user);
        applyNextDate(appointment);
        return appointmentRepository.save(appointment);
    }

    public Appointment updateAppointment(UUID id, Appointment details, User user) {
        Appointment appointment = findOwned(id, user);

        appointment.setName(details.getName());
        appointment.setPreviousDate(details.getPreviousDate());
        appointment.setTimePeriod(details.getTimePeriod());
        appointment.setTimeUnit(details.getTimeUnit());
        appointment.setNotificationEnabled(details.getNotificationEnabled());
        appointment.setDaysBeforeNotification(details.getDaysBeforeNotification());
        appointment.setNextDate(details.getNextDate());

        applyNextDate(appointment);
        return appointmentRepository.save(appointment);
    }

    public void deleteAppointmentById(UUID id, User user) {
        appointmentRepository.delete(findOwned(id, user));
    }

    // Returns the appointment only if it belongs to the given user, otherwise responds with 404
    private Appointment findOwned(UUID id, User user) {
        return appointmentRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Appointment not found"));
    }

    // Next date = previous date + period (the server is the source of truth)
    private void applyNextDate(Appointment appointment) {
        LocalDate previous = appointment.getPreviousDate();
        int period = appointment.getTimePeriod();

        if (previous != null && period > 0 && appointment.getTimeUnit() != null) {
            LocalDate next = switch (appointment.getTimeUnit()) {
                case DAYS -> previous.plusDays(period);
                case WEEKS -> previous.plusWeeks(period);
                case MONTHS -> previous.plusMonths(period);
                case YEARS -> previous.plusYears(period);
            };
            appointment.setNextDate(next);
        }
    }
}