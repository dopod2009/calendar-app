package com.calendar.repository;

import com.calendar.model.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {

    Optional<Device> findByDeviceId(String deviceId);

    List<Device> findByUserIdAndActiveTrue(Long userId);

    Optional<Device> findByDeviceIdAndUserId(String deviceId, Long userId);

    boolean existsByDeviceId(String deviceId);

    List<Device> findByUserId(Long userId);
}
