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

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 数据同步集成测试
 * 测试Android-后端数据同步接口联调
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SyncIntegrationTest {

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
        user.setEmail("sync@example.com");
        user.setPassword(passwordEncoder.encode("Test123!"));
        user.setDisplayName("Sync User");
        user = userRepository.save(user);
        userId = user.getId();

        // 获取访问Token
        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("sync@example.com");
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
     * TC-INT-SYNC-001: 增量同步（首次同步）测试
     */
    @Test
    void testFirstTimeSync() throws Exception {
        // 服务端有3个事件
        for (int i = 1; i <= 3; i++) {
            Event event = new Event();
            event.setTitle("服务端事件 " + i);
            event.setUserId(userId);
            event.setStartTime(System.currentTimeMillis() + i * 3600000);
            event.setEndTime(System.currentTimeMillis() + (i + 1) * 3600000);
            eventRepository.save(event);
        }

        // 客户端首次同步（lastSyncTime = 0）
        SyncRequest request = new SyncRequest();
        request.setLastSyncTime(0L);
        request.setEvents(new ArrayList<>());

        mockMvc.perform(post("/sync")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events.length()").value(3))
                .andExpect(jsonPath("$.syncTime").exists());
        
        System.out.println("✅ TC-INT-SYNC-001: 首次同步测试通过");
    }

    /**
     * TC-INT-SYNC-002: 增量同步（后续同步）测试
     */
    @Test
    void testIncrementalSync() throws Exception {
        // 创建2个旧事件
        long oldTime = System.currentTimeMillis() - 10000;
        for (int i = 1; i <= 2; i++) {
            Event event = new Event();
            event.setTitle("旧事件 " + i);
            event.setUserId(userId);
            event.setStartTime(oldTime + i * 3600000);
            event.setEndTime(oldTime + (i + 1) * 3600000);
            event.setUpdatedAt(oldTime);
            eventRepository.save(event);
        }

        // 创建1个新事件
        Event newEvent = new Event();
        newEvent.setTitle("新事件");
        newEvent.setUserId(userId);
        newEvent.setStartTime(System.currentTimeMillis() + 3600000);
        newEvent.setEndTime(System.currentTimeMillis() + 7200000);
        newEvent = eventRepository.save(newEvent);

        // 客户端同步（lastSyncTime = oldTime）
        SyncRequest request = new SyncRequest();
        request.setLastSyncTime(oldTime + 1000);
        request.setEvents(new ArrayList<>());

        mockMvc.perform(post("/sync")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.events.length()").value(1))
                .andExpect(jsonPath("$.events[0].title").value("新事件"));
        
        System.out.println("✅ TC-INT-SYNC-002: 增量同步测试通过");
    }

    /**
     * TC-INT-SYNC-003: 冲突检测（双方都修改）测试
     */
    @Test
    void testConflictDetection() throws Exception {
        // 服务端事件
        Event serverEvent = new Event();
        serverEvent.setTitle("服务端标题");
        serverEvent.setDescription("服务端描述");
        serverEvent.setUserId(userId);
        serverEvent.setStartTime(System.currentTimeMillis() + 3600000);
        serverEvent.setEndTime(System.currentTimeMillis() + 7200000);
        serverEvent.setUpdatedAt(System.currentTimeMillis());
        serverEvent = eventRepository.save(serverEvent);

        // 客户端修改了同一事件（基于旧版本）
        EventDTO clientEvent = new EventDTO();
        clientEvent.setId(serverEvent.getId());
        clientEvent.setTitle("客户端标题");
        clientEvent.setDescription("客户端描述");
        clientEvent.setStartTime(serverEvent.getStartTime());
        clientEvent.setEndTime(serverEvent.getEndTime());
        clientEvent.setUpdatedAt(serverEvent.getUpdatedAt() - 10000); // 旧版本

        SyncRequest request = new SyncRequest();
        request.setLastSyncTime(0L);
        request.setEvents(List.of(clientEvent));

        // 同步应该使用服务端版本（或提示冲突）
        mockMvc.perform(post("/sync")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
        
        // 验证服务端版本保留
        Event updatedEvent = eventRepository.findById(serverEvent.getId()).orElse(null);
        assertNotNull(updatedEvent);
        assertEquals("服务端标题", updatedEvent.getTitle());
        
        System.out.println("✅ TC-INT-SYNC-003: 冲突检测测试通过");
    }

    /**
     * TC-INT-SYNC-004: 离线数据同步测试
     */
    @Test
    void testOfflineDataSync() throws Exception {
        // 客户端离线创建了2个事件
        List<EventDTO> offlineEvents = new ArrayList<>();
        for (int i = 1; i <= 2; i++) {
            EventDTO event = new EventDTO();
            event.setTitle("离线事件 " + i);
            event.setStartTime(System.currentTimeMillis() + i * 3600000);
            event.setEndTime(System.currentTimeMillis() + (i + 1) * 3600000);
            event.setCategory("PERSONAL");
            offlineEvents.add(event);
        }

        SyncRequest request = new SyncRequest();
        request.setLastSyncTime(0L);
        request.setEvents(offlineEvents);

        // 同步到服务端
        mockMvc.perform(post("/sync")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadedCount").value(2));

        // 验证服务端已保存
        List<Event> savedEvents = eventRepository.findByUserId(userId);
        assertEquals(2, savedEvents.size());
        
        System.out.println("✅ TC-INT-SYNC-004: 离线同步测试通过");
    }

    /**
     * TC-INT-SYNC-005: 同步状态查询测试
     */
    @Test
    void testGetSyncStatus() throws Exception {
        // 创建事件
        Event event = new Event();
        event.setTitle("测试事件");
        event.setUserId(userId);
        event.setStartTime(System.currentTimeMillis() + 3600000);
        event.setEndTime(System.currentTimeMillis() + 7200000);
        eventRepository.save(event);

        // 查询同步状态
        mockMvc.perform(get("/sync/status")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").value(1))
                .andExpect(jsonPath("$.lastSyncTime").exists());
        
        System.out.println("✅ TC-INT-SYNC-005: 同步状态查询测试通过");
    }

    /**
     * TC-INT-SYNC-006: 批量同步测试
     */
    @Test
    void testBulkSync() throws Exception {
        // 创建100个事件
        List<EventDTO> bulkEvents = new ArrayList<>();
        for (int i = 1; i <= 100; i++) {
            EventDTO event = new EventDTO();
            event.setTitle("批量事件 " + i);
            event.setStartTime(System.currentTimeMillis() + i * 3600000);
            event.setEndTime(System.currentTimeMillis() + (i + 1) * 3600000);
            bulkEvents.add(event);
        }

        SyncRequest request = new SyncRequest();
        request.setLastSyncTime(0L);
        request.setEvents(bulkEvents);

        // 同步到服务端
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(post("/sync")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadedCount").value(100));

        long duration = System.currentTimeMillis() - startTime;
        assertTrue(duration < 5000, "批量同步应在5秒内完成");
        
        System.out.println("✅ TC-INT-SYNC-006: 批量同步测试通过（耗时: " + duration + "ms）");
    }
}
