package com.calendar.integration;

import com.calendar.dto.AuthRequest;
import com.calendar.dto.AuthResponse;
import com.calendar.dto.RegisterRequest;
import com.calendar.model.User;
import com.calendar.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 用户认证集成测试
 * 测试Android-后端用户认证接口联调
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    /**
     * TC-INT-AUTH-001: 用户注册测试
     */
    @Test
    void testUserRegistration() throws Exception {
        // 准备注册数据
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@example.com");
        request.setPassword("Test123!");
        request.setDisplayName("Test User");

        // 发送注册请求
        MvcResult result = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.email").value("test@example.com"))
                .andReturn();

        // 验证数据库记录
        User savedUser = userRepository.findByEmail("test@example.com").orElse(null);
        assertNotNull(savedUser);
        assertEquals("Test User", savedUser.getDisplayName());
        assertTrue(passwordEncoder.matches("Test123!", savedUser.getPassword()));
        
        System.out.println("✅ TC-INT-AUTH-001: 用户注册测试通过");
    }

    /**
     * TC-INT-AUTH-002: 用户登录测试
     */
    @Test
    void testUserLogin() throws Exception {
        // 先注册用户
        User user = new User();
        user.setEmail("login@example.com");
        user.setPassword(passwordEncoder.encode("Test123!"));
        user.setDisplayName("Login User");
        userRepository.save(user);

        // 准备登录数据
        AuthRequest request = new AuthRequest();
        request.setEmail("login@example.com");
        request.setPassword("Test123!");

        // 发送登录请求
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.user.email").value("login@example.com"));
        
        System.out.println("✅ TC-INT-AUTH-002: 用户登录测试通过");
    }

    /**
     * TC-INT-AUTH-003: Token刷新测试
     */
    @Test
    void testTokenRefresh() throws Exception {
        // 注册并登录
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("refresh@example.com");
        registerRequest.setPassword("Test123!");
        registerRequest.setDisplayName("Refresh User");

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
            registerResult.getResponse().getContentAsString(),
            AuthResponse.class
        );

        // 刷新Token
        mockMvc.perform(post("/auth/refresh")
                .header("Authorization", "Bearer " + authResponse.getRefreshToken()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
        
        System.out.println("✅ TC-INT-AUTH-003: Token刷新测试通过");
    }

    /**
     * TC-INT-AUTH-004: 访问受保护接口（无Token）
     */
    @Test
    void testAccessProtectedEndpointWithoutToken() throws Exception {
        mockMvc.perform(get("/events"))
                .andExpect(status().isUnauthorized());
        
        System.out.println("✅ TC-INT-AUTH-004: 无Token访问测试通过");
    }

    /**
     * TC-INT-AUTH-005: 访问受保护接口（有效Token）
     */
    @Test
    void testAccessProtectedEndpointWithValidToken() throws Exception {
        // 注册并获取Token
        RegisterRequest registerRequest = new RegisterRequest();
        registerRequest.setEmail("protected@example.com");
        registerRequest.setPassword("Test123!");
        registerRequest.setDisplayName("Protected User");

        MvcResult registerResult = mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registerRequest)))
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
            registerResult.getResponse().getContentAsString(),
            AuthResponse.class
        );

        // 使用Token访问受保护接口
        mockMvc.perform(get("/events")
                .header("Authorization", "Bearer " + authResponse.getAccessToken()))
                .andExpect(status().isOk());
        
        System.out.println("✅ TC-INT-AUTH-005: 有效Token访问测试通过");
    }

    /**
     * TC-INT-AUTH-006: 登录失败（密码错误）
     */
    @Test
    void testLoginFailure() throws Exception {
        // 注册用户
        User user = new User();
        user.setEmail("fail@example.com");
        user.setPassword(passwordEncoder.encode("CorrectPassword123!"));
        user.setDisplayName("Fail User");
        userRepository.save(user);

        // 使用错误密码登录
        AuthRequest request = new AuthRequest();
        request.setEmail("fail@example.com");
        request.setPassword("WrongPassword123!");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
        
        System.out.println("✅ TC-INT-AUTH-006: 登录失败测试通过");
    }
}
