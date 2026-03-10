package com.calendar.service;

import com.calendar.dto.*;
import com.calendar.mapper.UserMapper;
import com.calendar.model.Device;
import com.calendar.model.User;
import com.calendar.repository.DeviceRepository;
import com.calendar.repository.UserRepository;
import com.calendar.security.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final DeviceRepository deviceRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserMapper userMapper;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.info("Registering new user with email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = User.builder()
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .username(request.getUsername())
            .phone(request.getPhone())
            .gender(request.getGender())
            .active(true)
            .status(User.UserStatus.ACTIVE)
            .timezone("Asia/Shanghai")
            .build();

        user = userRepository.save(user);

        if (request.getDeviceId() != null) {
            registerDevice(user, request.getDeviceId(), request.getPlatform());
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .user(userMapper.toDTO(user))
            .build();
    }

    @Transactional
    public AuthResponse login(AuthRequest request) {
        log.info("User login attempt with email: {}", request.getEmail());

        User user = userRepository.findByEmailAndActiveTrue(request.getEmail())
            .orElseThrow(() -> new RuntimeException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        if (user.getStatus() != User.UserStatus.ACTIVE) {
            throw new RuntimeException("Account is not active");
        }

        if (request.getDeviceId() != null) {
            registerDevice(user, request.getDeviceId(), request.getPlatform());
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
            .token(token)
            .refreshToken(refreshToken)
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .user(userMapper.toDTO(user))
            .build();
    }

    @Transactional
    public AuthResponse refreshToken(String refreshToken) {
        log.info("Refreshing token");

        if (!jwtService.validateToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        Long userId = jwtService.extractUserId(refreshToken);
        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        String newToken = jwtService.generateToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return AuthResponse.builder()
            .token(newToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(86400000L)
            .user(userMapper.toDTO(user))
            .build();
    }

    private void registerDevice(User user, String deviceId, String platform) {
        Optional<Device> existingDevice = deviceRepository.findByDeviceId(deviceId);
        
        if (existingDevice.isPresent()) {
            Device device = existingDevice.get();
            device.setLastUsedAt(LocalDateTime.now());
            device.setPlatform(platform);
            deviceRepository.save(device);
        } else {
            Device device = Device.builder()
                .user(user)
                .deviceId(deviceId)
                .platform(platform)
                .active(true)
                .lastUsedAt(LocalDateTime.now())
                .build();
            deviceRepository.save(device);
        }
    }

    public UserDTO getCurrentUser(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        return userMapper.toDTO(user);
    }
}
