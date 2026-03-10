package com.calendar.integration;

import com.calendar.dto.*;
import com.calendar.model.Event;
import com.calendar.model.User;
import com.calendar.repository.EventRepository;
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

import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 事件管理集成测试
 * 测试Android-后端事件CRUD接口联调
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class EventIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private String accessToken;
    private Long userId;

    @BeforeEach
    void setUp() throws Exception {
        eventRepository.deleteAll();
        userRepository.deleteAll();

        // 创建测试用户
        User user = new User();
        user.setEmail("event@example.com");
        user.setPassword(passwordEncoder.encode("Test123!"));
        user.setDisplayName("Event User");
        user = userRepository.save(user);
        userId = user.getId();

        // 获取访问Token
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("event@example.com");
        authRequest.setPassword("Test123!");

        MvcResult authResult = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(authRequest)))
                .andReturn();

        AuthResponse authResponse = objectMapper.readValue(
            authResult.getResponse().getContentAsString(),
            AuthResponse.class
        );
        accessToken = authResponse.getAccessToken();
    }

    /**
     * TC-INT-EVENT-001: 创建事件测试
     */
    @Test
    void testCreateEvent() throws Exception {
        CreateEventRequest request = new CreateEventRequest();
        request.setTitle("团队会议");
        request.setDescription("每周例会");
        request.setStartTime(System.currentTimeMillis() + 3600000);
        request.setEndTime(System.currentTimeMillis() + 7200000);
        request.setLocation("会议室A");
        request.setCategory("WORK");

        MvcResult result = mockMvc.perform(post("/events")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("团队会议"))
                .andExpect(jsonPath("$.category").value("WORK"))
                .andReturn();

        // 验证数据库
        Event savedEvent = eventRepository.findAll().get(0);
        assertNotNull(savedEvent);
        assertEquals("团队会议", savedEvent.getTitle());
        assertEquals(userId, savedEvent.getUserId());
        
        System.out.println("✅ TC-INT-EVENT-001: 创建事件测试通过");
    }

    /**
     * TC-INT-EVENT-002: 获取事件详情测试
     */
    @Test
    void testGetEventById() throws Exception {
        // 先创建事件
        Event event = new Event();
        event.setTitle("测试事件");
        event.setUserId(userId);
        event.setStartTime(LocalDateTime.now().plusHours(1).toInstant(ZoneOffset.UTC).toEpochMilli());
        event.setEndTime(LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.UTC).toEpochMilli());
        event = eventRepository.save(event);

        // 获取事件
        mockMvc.perform(get("/events/" + event.getId())
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(event.getId()))
                .andExpect(jsonPath("$.title").value("测试事件"));
        
        System.out.println("✅ TC-INT-EVENT-002: 获取事件详情测试通过");
    }

    /**
     * TC-INT-EVENT-003: 更新事件测试
     */
    @Test
    void testUpdateEvent() throws Exception {
        // 先创建事件
        Event event = new Event();
        event.setTitle("旧标题");
        event.setUserId(userId);
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        event = eventRepository.save(event);

        // 更新事件
        UpdateEventRequest request = new UpdateEventRequest();
        request.setTitle("新标题");
        request.setDescription("更新后的描述");

        mockMvc.perform(put("/events/" + event.getId())
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("新标题"))
                .andExpect(jsonPath("$.description").value("更新后的描述"));

        // 验证数据库
        Event updatedEvent = eventRepository.findById(event.getId()).orElse(null);
        assertNotNull(updatedEvent);
        assertEquals("新标题", updatedEvent.getTitle());
        
        System.out.println("✅ TC-INT-EVENT-003: 更新事件测试通过");
    }

    /**
     * TC-INT-EVENT-004: 删除事件测试
     */
    @Test
    void testDeleteEvent() throws Exception {
        // 先创建事件
        Event event = new Event();
        event.setTitle("待删除事件");
        event.setUserId(userId);
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        event = eventRepository.save(event);

        // 删除事件
        mockMvc.perform(delete("/events/" + event.getId())
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isNoContent());

        // 验证数据库
        assertFalse(eventRepository.existsById(event.getId()));
        
        System.out.println("✅ TC-INT-EVENT-004: 删除事件测试通过");
    }

    /**
     * TC-INT-EVENT-005: 获取事件列表（分页）测试
     */
    @Test
    void testGetEventsWithPagination() throws Exception {
        // 创建多个事件
        for (int i = 1; i <= 25; i++) {
            Event event = new Event();
            event.setTitle("事件 " + i);
            event.setUserId(userId);
            event.setStartTime(System.currentTimeMillis() + i * 3600000);
            event.setEndTime(System.currentTimeMillis() + (i + 1) * 3600000);
            eventRepository.save(event);
        }

        // 获取第一页
        mockMvc.perform(get("/events")
                .header("Authorization", "Bearer " + accessToken)
                .param("page", "0")
                .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(20))
                .andExpect(jsonPath("$.totalElements").value(25))
                .andExpect(jsonPath("$.totalPages").value(2));
        
        System.out.println("✅ TC-INT-EVENT-005: 分页查询测试通过");
    }

    /**
     * TC-INT-EVENT-006: 搜索事件测试
     */
    @Test
    void testSearchEvents() throws Exception {
        // 创建事件
        Event event1 = new Event();
        event1.setTitle("团队会议");
        event1.setDescription("每周例会");
        event1.setUserId(userId);
        event1.setStartTime(System.currentTimeMillis() + 3600000);
        event1.setEndTime(System.currentTimeMillis() + 7200000);
        eventRepository.save(event1);

        Event event2 = new Event();
        event2.setTitle("个人时间");
        event2.setDescription("读书学习");
        event2.setUserId(userId);
        event2.setStartTime(System.currentTimeMillis() + 7200000);
        event2.setEndTime(System.currentTimeMillis() + 10800000);
        eventRepository.save(event2);

        // 搜索"会议"
        mockMvc.perform(get("/events/search")
                .header("Authorization", "Bearer " + accessToken)
                .param("keyword", "会议"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].title").value("团队会议"));
        
        System.out.println("✅ TC-INT-EVENT-006: 搜索事件测试通过");
    }

    /**
     * TC-INT-EVENT-007: 按分类过滤测试
     */
    @Test
    void testGetEventsByCategory() throws Exception {
        // 创建不同分类的事件
        Event workEvent = new Event();
        workEvent.setTitle("工作事件");
        workEvent.setCategory("WORK");
        workEvent.setUserId(userId);
        workEvent.setStartTime(System.currentTimeMillis() + 3600000);
        workEvent.setEndTime(System.currentTimeMillis() + 7200000);
        eventRepository.save(workEvent);

        Event personalEvent = new Event();
        personalEvent.setTitle("个人事件");
        personalEvent.setCategory("PERSONAL");
        personalEvent.setUserId(userId);
        personalEvent.setStartTime(System.currentTimeMillis() + 7200000);
        personalEvent.setEndTime(System.currentTimeMillis() + 10800000);
        eventRepository.save(personalEvent);

        // 过滤WORK分类
        mockMvc.perform(get("/events")
                .header("Authorization", "Bearer " + accessToken)
                .param("category", "WORK"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.content[0].category").value("WORK"));
        
        System.out.println("✅ TC-INT-EVENT-007: 分类过滤测试通过");
    }

    /**
     * TC-INT-EVENT-008: 权限验证（访问他人事件）测试
     */
    @Test
    void testAccessOtherUserEvent() throws Exception {
        // 创建另一个用户
        User otherUser = new User();
        otherUser.setEmail("other@example.com");
        otherUser.setPassword(passwordEncoder.encode("Test123!"));
        otherUser.setDisplayName("Other User");
        otherUser = userRepository.save(otherUser);

        // 创建属于其他用户的事件
        Event otherEvent = new Event();
        otherEvent.setTitle("他人的事件");
        otherEvent.setUserId(otherUser.getId());
        otherEvent.setStartTime(System.currentTimeMillis() + 3600000);
        otherEvent.setEndTime(System.currentTimeMillis() + 7200000);
        otherEvent = eventRepository.save(otherEvent);

        // 尝试访问
        mockMvc.perform(get("/events/" + otherEvent.getId())
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isForbidden());
        
        System.out.println("✅ TC-INT-EVENT-008: 权限验证测试通过");
    }
}
