# Firebase Cloud Messaging (FCM) 集成指南

## 1. 添加依赖

在 `pom.xml` 中添加：

```xml
<dependency>
    <groupId>com.google.firebase</groupId>
    <artifactId>firebase-admin</artifactId>
    <version>9.1.1</version>
</dependency>
```

## 2. Firebase配置

### 2.1 获取Firebase项目配置

1. 访问 [Firebase Console](https://console.firebase.google.com/)
2. 创建或选择项目
3. 进入项目设置 > 服务账号
4. 点击"生成新的私钥"下载 `serviceAccountKey.json`

### 2.2 配置Spring Boot

将 `serviceAccountKey.json` 放在 `src/main/resources/` 目录下

在 `application.yml` 中添加：

```yaml
firebase:
  config:
    path: classpath:serviceAccountKey.json
  database:
    url: https://your-project-id.firebaseio.com
```

## 3. 完整集成示例

### 3.1 Firebase配置类

```java
@Configuration
public class FirebaseConfig {
    
    @Value("${firebase.config.path}")
    private String firebaseConfigPath;
    
    @Value("${firebase.database.url}")
    private String databaseUrl;
    
    @Bean
    public FirebaseApp initializeFirebase() throws IOException {
        InputStream serviceAccount = 
            new ClassPathResource("serviceAccountKey.json").getInputStream();
        
        FirebaseOptions options = FirebaseOptions.builder()
            .setCredentials(GoogleCredentials.fromStream(serviceAccount))
            .setDatabaseUrl(databaseUrl)
            .build();
        
        return FirebaseApp.initializeApp(options);
    }
    
    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
```

### 3.2 完整的推送服务

```java
@Slf4j
@Service
@RequiredArgsConstructor
public class FirebasePushService {
    
    private final FirebaseMessaging firebaseMessaging;
    
    public void sendPushNotification(String token, String title, String body) {
        Message message = Message.builder()
            .setToken(token)
            .setNotification(Notification.builder()
                .setTitle(title)
                .setBody(body)
                .build())
            .setAndroidConfig(AndroidConfig.builder()
                .setPriority(AndroidConfig.Priority.HIGH)
                .setNotification(AndroidNotification.builder()
                    .setChannelId("calendar_reminder")
                    .setIcon("ic_notification")
                    .build())
                .build())
            .putData("click_action", "OPEN_EVENT")
            .build();
        
        try {
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent message: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send FCM message", e);
            throw new RuntimeException("Push notification failed", e);
        }
    }
}
```

## 4. Android端集成

### 4.1 添加依赖

在 app `build.gradle.kts` 中：

```kotlin
dependencies {
    implementation("com.google.firebase:firebase-messaging-ktx:23.3.1")
}
```

### 4.2 创建FirebaseMessagingService

```kotlin
class CalendarMessagingService : FirebaseMessagingService() {
    
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        
        // 处理接收到的消息
        remoteMessage.notification?.let { notification ->
            showNotification(
                title = notification.title ?: "日历提醒",
                message = notification.body ?: "",
                data = remoteMessage.data
            )
        }
    }
    
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        
        // 将新token发送到服务器
        lifecycleScope.launch {
            updatePushToken(token)
        }
    }
    
    private fun showNotification(title: String, message: String, data: Map<String, String>) {
        // 使用前面创建的ReminderNotificationManager显示通知
        val notificationManager = ReminderNotificationManager(this)
        notificationManager.showNotification(
            reminderId = System.currentTimeMillis(),
            eventId = data["eventId"]?.toLong() ?: 0L,
            title = title,
            message = message
        )
    }
}
```

### 4.3 在AndroidManifest.xml中注册

```xml
<service
    android:name=".CalendarMessagingService"
    android:exported="false">
    <intent-filter>
        <action android:name="com.google.firebase.MESSAGING_EVENT" />
    </intent-filter>
</service>
```

## 5. 测试推送

### 5.1 使用Firebase Console测试

1. 打开Firebase Console
2. 选择项目 > Cloud Messaging
3. 点击"Send your first message"
4. 输入标题和正文
5. 选择目标应用
6. 点击"Send message"

### 5.2 使用API测试

```bash
curl -X POST https://fcm.googleapis.com/fcm/send \
  -H "Authorization: key=YOUR_SERVER_KEY" \
  -H "Content-Type: application/json" \
  -d '{
    "to": "DEVICE_TOKEN",
    "notification": {
      "title": "日历提醒",
      "body": "您有一个即将开始的事件"
    },
    "data": {
      "eventId": "123",
      "type": "reminder"
    }
  }'
```

## 6. 注意事项

1. **安全性**：不要将 `serviceAccountKey.json` 提交到Git
2. **Token管理**：定期清理无效的设备Token
3. **消息限制**：FCM有消息大小和频率限制
4. **测试环境**：建议先在Firebase测试环境验证
5. **错误处理**：实现完善的错误重试机制

## 7. 当前状态

✅ 推送服务架构已搭建  
⏳ 需要Firebase账号和配置文件  
⏳ 需要Android端集成FCM SDK  
⏳ 需要实际测试推送功能  

## 8. 下一步

1. 创建Firebase项目并获取配置文件
2. 完善FirebaseConfig.java配置
3. 在Android端集成FCM
4. 进行端到端推送测试
