# Android-后端联调测试计划

## 📋 测试概述

**测试目标**: 验证Android客户端与后端服务的接口联调，确保数据传输正确、异常处理完善  
**测试时间**: 2026年3月10日  
**测试负责人**: Android开发 + 后端开发 + 测试工程师  
**测试环境**: 开发环境（Local）

---

## 🔧 测试环境配置

### 后端环境

**服务器配置**:
- Spring Boot 3.2.3
- MySQL 8.0 (localhost:3306)
- Redis 7.x (localhost:6379)
- 端口: 8080
- Context Path: `/api`

**启动命令**:
```bash
cd backend
mvn clean install
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

**健康检查**:
```bash
curl http://localhost:8080/api/health
```

### Android环境

**配置文件**: `app/src/main/assets/config.properties`
- API地址: `http://10.0.2.2:8080/api/`
- WebSocket地址: `ws://10.0.2.2:8080/api/ws`

**运行方式**:
1. Android Studio连接模拟器
2. 启动应用
3. 查看Logcat日志

---

## 📝 测试用例清单

### 1. 用户认证模块（6个测试用例）

#### TC-INT-AUTH-001: 用户注册
**测试步骤**:
1. Android发送注册请求
   ```kotlin
   val request = RegisterRequest(
       email = "test@example.com",
       password = "Test123!",
       displayName = "Test User"
   )
   authApi.register(request)
   ```

2. 验证后端处理
   - 检查数据库users表
   - 密码已加密存储
   - 返回正确的userId

**预期结果**:
- ✅ HTTP 200
- ✅ 返回token和refreshToken
- ✅ 数据库新增用户记录

**验证SQL**:
```sql
SELECT * FROM users WHERE email = 'test@example.com';
```

---

#### TC-INT-AUTH-002: 用户登录
**测试步骤**:
```kotlin
val request = AuthRequest(
    email = "test@example.com",
    password = "Test123!"
)
val response = authApi.login(request)
```

**预期结果**:
- ✅ HTTP 200
- ✅ 返回accessToken（有效期24小时）
- ✅ 返回refreshToken（有效期7天）
- ✅ Token格式正确（JWT）

**验证方法**:
```bash
# 解析JWT Token
curl -H "Authorization: Bearer {token}" http://localhost:8080/api/auth/me
```

---

#### TC-INT-AUTH-003: Token刷新
**测试步骤**:
```kotlin
val response = authApi.refreshToken(refreshToken)
```

**预期结果**:
- ✅ 返回新的accessToken
- ✅ refreshToken保持不变

---

#### TC-INT-AUTH-004: 访问受保护接口（无Token）
**测试步骤**:
```kotlin
// 不携带Token访问接口
eventApi.getEvents()
```

**预期结果**:
- ✅ HTTP 401 Unauthorized
- ✅ 返回错误信息: "未授权访问"

---

#### TC-INT-AUTH-005: 访问受保护接口（有效Token）
**测试步骤**:
```kotlin
// 携带有效Token
val response = eventApi.getEvents("Bearer ${accessToken}")
```

**预期结果**:
- ✅ HTTP 200
- ✅ 返回事件列表

---

#### TC-INT-AUTH-006: Token过期处理
**测试步骤**:
1. 使用过期的Token访问接口
2. Android自动刷新Token

**预期结果**:
- ✅ 自动拦截401响应
- ✅ 自动调用refreshToken
- ✅ 使用新Token重试请求

---

### 2. 事件管理模块（8个测试用例）

#### TC-INT-EVENT-001: 创建事件
**测试步骤**:
```kotlin
val request = CreateEventRequest(
    title = "团队会议",
    description = "每周例会",
    startTime = System.currentTimeMillis(),
    endTime = System.currentTimeMillis() + 3600000,
    location = "会议室A",
    category = "WORK"
)
val response = eventApi.createEvent("Bearer ${token}", request)
```

**预期结果**:
- ✅ HTTP 201 Created
- ✅ 返回eventId
- ✅ 数据库新增事件记录

**验证SQL**:
```sql
SELECT * FROM events WHERE title = '团队会议';
```

---

#### TC-INT-EVENT-002: 获取事件详情
**测试步骤**:
```kotlin
val event = eventApi.getEvent("Bearer ${token}", eventId)
```

**预期结果**:
- ✅ HTTP 200
- ✅ 返回完整事件信息
- ✅ 包含创建者信息

---

#### TC-INT-EVENT-003: 更新事件
**测试步骤**:
```kotlin
val request = UpdateEventRequest(
    title = "团队会议（改期）",
    startTime = newTime
)
val response = eventApi.updateEvent("Bearer ${token}", eventId, request)
```

**预期结果**:
- ✅ HTTP 200
- ✅ 返回更新后的事件
- ✅ updatedAt时间戳更新

---

#### TC-INT-EVENT-004: 删除事件
**测试步骤**:
```kotlin
eventApi.deleteEvent("Bearer ${token}", eventId)
```

**预期结果**:
- ✅ HTTP 204 No Content
- ✅ 数据库记录已删除

---

#### TC-INT-EVENT-005: 获取事件列表（分页）
**测试步骤**:
```kotlin
val response = eventApi.getEvents(
    "Bearer ${token}",
    page = 0,
    size = 20
)
```

**预期结果**:
- ✅ HTTP 200
- ✅ 返回事件列表
- ✅ 包含分页信息（totalElements, totalPages）

---

#### TC-INT-EVENT-006: 搜索事件
**测试步骤**:
```kotlin
val response = eventApi.searchEvents(
    "Bearer ${token}",
    keyword = "会议",
    startTime = startTime,
    endTime = endTime
)
```

**预期结果**:
- ✅ HTTP 200
- ✅ 返回匹配的事件列表

---

#### TC-INT-EVENT-007: 按分类过滤
**测试步骤**:
```kotlin
val response = eventApi.getEventsByCategory(
    "Bearer ${token}",
    category = "WORK"
)
```

**预期结果**:
- ✅ HTTP 200
- ✅ 只返回WORK类别的事件

---

#### TC-INT-EVENT-008: 权限验证（访问他人事件）
**测试步骤**:
1. 用户A创建事件
2. 用户B尝试访问用户A的事件

**预期结果**:
- ✅ HTTP 403 Forbidden
- ✅ 错误信息: "无权访问此事件"

---

### 3. 数据同步模块（6个测试用例）

#### TC-INT-SYNC-001: 增量同步（首次同步）
**测试步骤**:
```kotlin
val request = SyncRequest(
    lastSyncTime = 0, // 首次同步
    events = emptyList()
)
val response = syncApi.sync("Bearer ${token}", request)
```

**预期结果**:
- ✅ HTTP 200
- ✅ 返回所有服务端事件
- ✅ syncTime更新

---

#### TC-INT-SYNC-002: 增量同步（后续同步）
**测试步骤**:
```kotlin
val request = SyncRequest(
    lastSyncTime = lastSyncTimestamp,
    events = localModifiedEvents
)
val response = syncApi.sync("Bearer ${token}", request)
```

**预期结果**:
- ✅ HTTP 200
- ✅ 返回lastSyncTime之后修改的事件
- ✅ 处理本地修改的事件

---

#### TC-INT-SYNC-003: 冲突检测（双方都修改）
**测试步骤**:
1. 用户在设备A修改事件
2. 用户在设备B也修改同一事件
3. 设备B发起同步

**预期结果**:
- ✅ 检测到冲突
- ✅ 根据updatedAt时间戳解决
- ✅ 或提示用户选择

---

#### TC-INT-SYNC-004: 离线数据同步
**测试步骤**:
1. 断开网络
2. 在本地创建/修改事件
3. 恢复网络
4. 触发同步

**预期结果**:
- ✅ 离线修改保存到本地
- ✅ 网络恢复后自动同步
- ✅ 数据一致

---

#### TC-INT-SYNC-005: 同步状态查询
**测试步骤**:
```kotlin
val status = syncApi.getSyncStatus("Bearer ${token}")
```

**预期结果**:
- ✅ HTTP 200
- ✅ 返回lastSyncTime
- ✅ 返回pendingChanges数量

---

#### TC-INT-SYNC-006: 批量同步
**测试步骤**:
```kotlin
val request = SyncRequest(
    lastSyncTime = lastSyncTime,
    events = bulkEvents // 批量100个事件
)
val response = syncApi.sync("Bearer ${token}", request)
```

**预期结果**:
- ✅ HTTP 200
- ✅ 所有事件处理成功
- ✅ 响应时间 < 5秒

---

### 4. WebSocket模块（4个测试用例）

#### TC-INT-WS-001: 建立连接
**测试步骤**:
```kotlin
val client = Stomp.over(
    Stomp.ConnectionProvider.OKHTTP,
    "ws://10.0.2.2:8080/api/ws"
)
client.connect("Authorization" to "Bearer ${token}")
```

**预期结果**:
- ✅ 连接成功
- ✅ 收到CONNECTED帧

---

#### TC-INT-WS-002: 订阅事件通知
**测试步骤**:
```kotlin
client.subscribe("/user/queue/events") { message ->
    // 处理消息
}
```

**预期结果**:
- ✅ 订阅成功
- ✅ 收到订阅确认

---

#### TC-INT-WS-003: 接收实时通知
**测试步骤**:
1. 设备A订阅事件通知
2. 设备B创建/修改事件
3. 验证设备A收到通知

**预期结果**:
- ✅ 设备A收到实时推送
- ✅ 消息格式正确
- ✅ 延迟 < 1秒

---

#### TC-INT-WS-004: 断线重连
**测试步骤**:
1. 建立WebSocket连接
2. 断开网络
3. 恢复网络
4. 验证自动重连

**预期结果**:
- ✅ 自动检测断线
- ✅ 自动重连
- ✅ 重新订阅成功

---

### 5. FCM推送模块（3个测试用例）

#### TC-INT-FCM-001: 注册设备Token
**测试步骤**:
```kotlin
val request = DeviceRequest(
    deviceId = "device-123",
    fcmToken = "fcm-token-xxx"
)
authApi.registerDevice("Bearer ${token}", request)
```

**预期结果**:
- ✅ HTTP 200
- ✅ 设备Token保存到数据库

---

#### TC-INT-FCM-002: 发送推送通知
**测试步骤**:
1. 创建事件并设置提醒
2. 到达提醒时间
3. 验证收到FCM推送

**预期结果**:
- ✅ 收到推送通知
- ✅ 通知内容正确
- ✅ 点击跳转正确

---

#### TC-INT-FCM-003: 推送失败处理
**测试步骤**:
1. 使用无效的FCM Token
2. 发送推送
3. 验证错误处理

**预期结果**:
- ✅ 记录推送失败日志
- ✅ 不影响系统运行

---

### 6. Google Calendar集成（3个测试用例）

#### TC-INT-GCAL-001: OAuth授权
**测试步骤**:
```kotlin
val signInIntent = googleCalendarViewModel.getSignInIntent()
signInLauncher.launch(signInIntent)
```

**预期结果**:
- ✅ 打开Google登录页面
- ✅ 授权成功
- ✅ 获取访问Token

---

#### TC-INT-GCAL-002: 导入Google事件
**测试步骤**:
```kotlin
googleCalendarViewModel.importFromGoogle(
    calendarId = "primary",
    startTime = startTime,
    endTime = endTime,
    onImportComplete = { events ->
        // 处理导入的事件
    }
)
```

**预期结果**:
- ✅ 成功获取Google事件
- ✅ 事件保存到本地数据库

---

#### TC-INT-GCAL-003: 双向同步
**测试步骤**:
```kotlin
googleCalendarViewModel.syncWithGoogle(
    calendarId = "primary",
    localEvents = localEvents,
    lastSyncTime = lastSyncTime,
    onSyncComplete = { syncResult ->
        // 处理同步结果
    }
)
```

**预期结果**:
- ✅ 上传本地新增事件
- ✅ 下载Google新增事件
- ✅ 冲突处理正确

---

## 🧪 测试执行流程

### 第一步：环境准备
1. 启动MySQL数据库
   ```bash
   mysql.server start
   mysql -u root -p
   CREATE DATABASE calendar_dev;
   CREATE USER 'calendar_dev'@'localhost' IDENTIFIED BY 'dev_password_123';
   GRANT ALL PRIVILEGES ON calendar_dev.* TO 'calendar_dev'@'localhost';
   ```

2. 启动Redis
   ```bash
   redis-server
   ```

3. 启动后端服务
   ```bash
   cd backend
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

4. 验证服务启动
   ```bash
   curl http://localhost:8080/api/health
   ```

### 第二步：执行测试

#### 1. 用户认证测试（30分钟）
- 执行TC-INT-AUTH-001至006
- 验证Token生成、刷新、过期处理

#### 2. 事件管理测试（45分钟）
- 执行TC-INT-EVENT-001至008
- 验证CRUD操作和权限控制

#### 3. 数据同步测试（60分钟）
- 执行TC-INT-SYNC-001至006
- 验证增量同步和冲突处理

#### 4. WebSocket测试（30分钟）
- 执行TC-INT-WS-001至004
- 验证实时推送和断线重连

#### 5. FCM推送测试（30分钟）
- 执行TC-INT-FCM-001至003
- 验证推送功能（Mock模式）

#### 6. Google Calendar测试（45分钟）
- 执行TC-INT-GCAL-001至003
- 验证OAuth授权和数据同步

### 第三步：问题记录

**发现问题时**:
1. 记录问题详情（步骤、预期、实际）
2. 截图/日志保存
3. 标注严重级别（P0/P1/P2）
4. 立即修复或记录到待办

---

## 📊 测试报告模板

### 测试结果汇总

| 模块 | 用例数 | 通过数 | 失败数 | 通过率 |
|------|--------|--------|--------|--------|
| 用户认证 | 6 | - | - | -% |
| 事件管理 | 8 | - | - | -% |
| 数据同步 | 6 | - | - | -% |
| WebSocket | 4 | - | - | -% |
| FCM推送 | 3 | - | - | -% |
| Google Calendar | 3 | - | - | -% |
| **总计** | **30** | **-** | **-** | **-%** |

### 缺陷列表

| ID | 模块 | 描述 | 严重级别 | 状态 |
|----|------|------|---------|------|
| - | - | - | - | - |

---

## ✅ 测试完成标准

- [ ] 所有30个测试用例执行完成
- [ ] 通过率 ≥ 90%
- [ ] 无P0级别缺陷
- [ ] P1级别缺陷 ≤ 3个
- [ ] 所有发现的问题已修复或有workaround

---

**测试负责人**: _____________  
**测试日期**: 2026年3月10日  
**报告生成时间**: 测试完成后
