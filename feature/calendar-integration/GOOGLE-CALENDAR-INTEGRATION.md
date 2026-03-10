# Google Calendar 集成指南

## 📋 功能概述

Google Calendar 集成模块提供完整的 Google 日历双向同步功能。

### 核心功能

- ✅ Google 账号授权登录
- ✅ 获取日历列表
- ✅ 加载事件列表
- ✅ 创建/更新/删除事件
- ✅ 导入 Google 事件到本地
- ✅ 导出本地事件到 Google
- ✅ 双向自动同步
- ✅ 冲突检测与解决

---

## 🚀 快速开始

### 1. 配置 Google Cloud 项目

#### 1.1 创建项目
1. 访问 [Google Cloud Console](https://console.cloud.google.com/)
2. 创建新项目或选择现有项目
3. 记录项目 ID

#### 1.2 启用 Google Calendar API
1. 进入 **API 和服务** → **库**
2. 搜索 **Google Calendar API**
3. 点击 **启用**

#### 1.3 创建 OAuth 2.0 凭据
1. 进入 **API 和服务** → **凭据**
2. 点击 **创建凭据** → **OAuth 客户端 ID**
3. 选择 **Android** 应用类型
4. 输入包名: `com.calendar.app`
5. 输入 SHA-1 证书指纹（调试证书）:
   ```bash
   keytool -exportcert -alias androiddebugkey -keystore ~/.android/debug.keystore -list -v
   ```
6. 记录生成的 **客户端 ID**

#### 1.4 配置同意屏幕
1. 进入 **API 和服务** → **OAuth 同意屏幕**
2. 选择 **外部** 用户类型
3. 填写应用信息:
   - 应用名称: Calendar App
   - 支持邮箱: your-email@example.com
   - 应用徽标: 上传应用图标
4. 添加作用域:
   - `https://www.googleapis.com/auth/calendar` (读写权限)
   - `https://www.googleapis.com/auth/calendar.readonly` (只读权限)

---

### 2. 添加依赖

在 `feature/calendar-integration/build.gradle.kts` 中添加:

```kotlin
dependencies {
    // Google Play Services
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    
    // Google API Client
    implementation("com.google.api-client:google-api-client-android:2.2.0")
    implementation("com.google.api-client:google-api-client-gson:2.2.0")
    
    // Google Calendar API
    implementation("com.google.apis:google-api-services-calendar:v3-rev20231123-2.0.0")
    
    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
}
```

---

### 3. 配置 AndroidManifest.xml

```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    
    <!-- 网络权限 -->
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    
    <application>
        <!-- Google Sign-In 配置 -->
        <meta-data
            android:name="com.google.android.gms.version"
            android:value="@integer/google_play_services_version" />
    </application>
</manifest>
```

---

### 4. 更新客户端 ID

在 `GoogleCalendarService.kt` 中更新客户端 ID:

```kotlin
companion object {
    private const val CLIENT_ID = "YOUR_CLIENT_ID.apps.googleusercontent.com"
}
```

---

## 📱 使用示例

### 1. 初始化并登录

```kotlin
val viewModel: GoogleCalendarViewModel = viewModel()

// 检查登录状态
if (!viewModel.checkSignInStatus()) {
    // 启动登录流程
    val signInIntent = viewModel.getSignInIntent()
    signInLauncher.launch(signInIntent)
}

// 处理登录结果
val signInLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.StartActivityForResult()
) { result ->
    if (result.resultCode == Activity.RESULT_OK) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        viewModel.handleSignInResult(task)
    }
}
```

---

### 2. 加载日历和事件

```kotlin
// 加载日历列表
viewModel.loadCalendars()

// 加载事件
viewModel.loadEvents(
    calendarId = "primary",  // 主日历
    startTime = startTime,
    endTime = endTime
)

// 观察状态
val events by viewModel.events.collectAsState()
```

---

### 3. 创建事件

```kotlin
val event = GoogleCalendarEvent(
    title = "团队会议",
    description = "每周例会",
    location = "会议室A",
    startTime = System.currentTimeMillis(),
    endTime = System.currentTimeMillis() + 3600000, // 1小时后
    reminders = listOf(
        EventReminder(type = "popup", minutesBefore = 15)
    )
)

viewModel.createEvent(event)
```

---

### 4. 更新事件

```kotlin
val updatedEvent = event.copy(
    title = "团队会议（改期）",
    startTime = newStartTime,
    endTime = newEndTime
)

viewModel.updateEvent(updatedEvent)
```

---

### 5. 删除事件

```kotlin
viewModel.deleteEvent(eventId = "event123")
```

---

### 6. 导入 Google 事件

```kotlin
viewModel.importFromGoogle(
    calendarId = "primary",
    startTime = startTime,
    endTime = endTime,
    onImportComplete = { googleEvents ->
        // 处理导入的事件
        googleEvents.forEach { event ->
            // 保存到本地数据库
            localRepository.insertEvent(event.toLocalEvent())
        }
    }
)
```

---

### 7. 导出本地事件到 Google

```kotlin
val localEvents = localRepository.getAllEvents()

viewModel.exportToGoogle(
    events = localEvents.map { it.toGoogleEvent() },
    calendarId = "primary"
)
```

---

### 8. 双向同步

```kotlin
val localEvents = localRepository.getAllEvents()
val lastSyncTime = preferences.getLastSyncTime()

viewModel.syncWithGoogle(
    calendarId = "primary",
    localEvents = localEvents,
    lastSyncTime = lastSyncTime,
    onSyncComplete = { syncResult ->
        // 处理同步结果
        println("上传: ${syncResult.uploadedCount} 个")
        println("下载: ${syncResult.downloadedEvents.size} 个")
        
        // 保存下载的事件
        syncResult.downloadedEvents.forEach { event ->
            localRepository.insertOrUpdateEvent(event.toLocalEvent())
        }
        
        // 更新同步时间
        preferences.setLastSyncTime(System.currentTimeMillis())
    }
)
```

---

## 🔄 同步策略

### 增量同步流程

```
1. 获取本地最后同步时间 (lastSyncTime)
2. 从 Google 获取 lastSyncTime 之后的所有事件
3. 比较本地和远程事件:
   - 本地新增 → 上传到 Google
   - 远程新增 → 下载到本地
   - 双方更新 → 根据 updatedAt 时间戳决定
   - 双方删除 → 标记删除
4. 更新 lastSyncTime
```

### 冲突解决

- **时间戳优先**: 比较 `updatedAt` 时间戳，新的覆盖旧的
- **用户选择**: 提供界面让用户选择保留哪个版本
- **版本标记**: 使用 `version` 字段追踪修改历史

---

## ⚠️ 注意事项

### 1. API 配额限制

Google Calendar API 有以下限制:
- **每秒请求数**: 100 QPS
- **每日请求数**: 1,000,000 次/天

建议:
- 使用批量操作减少 API 调用
- 实现请求限流
- 缓存已加载的事件

### 2. 权限处理

确保用户授予了日历权限:
```kotlin
if (ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_CALENDAR
    ) != PackageManager.PERMISSION_GRANTED
) {
    ActivityCompat.requestPermissions(
        activity,
        arrayOf(
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR
        ),
        REQUEST_CODE_CALENDAR_PERMISSION
    )
}
```

### 3. 网络状态

检查网络连接:
```kotlin
val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
val networkInfo = connectivityManager.activeNetworkInfo
if (networkInfo?.isConnected != true) {
    // 离线模式
}
```

### 4. 错误处理

常见错误:
- `401 Unauthorized`: Token 过期，需要重新登录
- `403 Forbidden`: 权限不足
- `404 Not Found`: 事件不存在
- `429 Too Many Requests`: 超过配额限制

---

## 🧪 测试

### 单元测试

```kotlin
@Test
fun `create event should return success`() = runTest {
    // Given
    val event = GoogleCalendarEvent(
        title = "Test Event",
        startTime = System.currentTimeMillis(),
        endTime = System.currentTimeMillis() + 3600000
    )
    
    // When
    val result = googleCalendarService.createEvent(event = event)
    
    // Then
    assertTrue(result.isSuccess)
    assertEquals("Test Event", result.getOrThrow().title)
}
```

### 集成测试

```kotlin
@Test
fun `sync should upload local events and download remote events`() = runTest {
    // Given
    val localEvents = listOf(/* ... */)
    val lastSyncTime = System.currentTimeMillis() - 86400000
    
    // When
    val result = googleCalendarService.syncWithGoogleCalendar(
        localEvents = localEvents,
        lastSyncTime = lastSyncTime
    )
    
    // Then
    assertTrue(result.isSuccess)
    result.getOrThrow().let { syncResult ->
        assertTrue(syncResult.uploadedCount >= 0)
        assertTrue(syncResult.downloadedEvents.isNotEmpty())
    }
}
```

---

## 📚 相关文档

- [Google Calendar API 官方文档](https://developers.google.com/calendar/api/guides/overview)
- [Google Sign-In for Android](https://developers.google.com/identity/sign-in/android/start)
- [OAuth 2.0 for Mobile & Desktop Apps](https://developers.google.com/identity/protocols/oauth2/native-app)

---

## ✅ 完成检查清单

集成前请确认:

- [ ] Google Cloud 项目已创建
- [ ] Google Calendar API 已启用
- [ ] OAuth 2.0 客户端 ID 已创建
- [ ] 客户端 ID 已配置到代码中
- [ ] 同意屏幕已配置
- [ ] 依赖已添加到 `build.gradle.kts`
- [ ] 权限已添加到 `AndroidManifest.xml`
- [ ] 测试登录流程正常
- [ ] 事件 CRUD 操作正常
- [ ] 同步功能正常

---

## 📞 支持

如遇问题，请检查:
1. Google Cloud Console 中的 API 使用情况
2. OAuth 同意屏幕配置
3. SHA-1 证书指纹是否正确
4. 网络连接状态
5. Logcat 中的错误日志

---

**集成完成后，用户可以无缝同步 Google 日历事件，享受多端数据一致体验！** 🎉
