package com.chrus.nextdate.repository;

import com.chrus.nextdate.model.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<Appointment, UUID> {

    List<Appointment> findByUserIdOrderByNextDateAsc(UUID userId);

    Optional<Appointment> findByIdAndUserId(UUID id, UUID userId);
}