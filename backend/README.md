# Android Calendar Backend Service

Backend service for Android Calendar Application built with Spring Boot 3.2.x

## 🚀 Features

- **User Authentication**: JWT-based authentication with registration, login, and token refresh
- **Event Management**: Full CRUD operations for calendar events
- **Data Synchronization**: Incremental sync with conflict resolution
- **Real-time Notifications**: WebSocket-based push notifications
- **Reminder System**: Scheduled reminder processing
- **Multi-device Support**: Device registration and management

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.2.3
- **Database**: MySQL 8.0
- **Cache**: Redis 7.x
- **Authentication**: JWT (jjwt 0.12.3)
- **WebSocket**: Spring WebSocket + STOMP
- **API Documentation**: SpringDoc OpenAPI 2.3.0
- **Build Tool**: Maven
- **Java Version**: 17

## 📋 Prerequisites

- JDK 17 or higher
- Maven 3.6+
- MySQL 8.0+
- Redis 7.x+

## 🔧 Configuration

### Database Setup

1. Create MySQL database:
```sql
CREATE DATABASE calendar_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. Update `application.yml` with your database credentials:
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/calendar_db
    username: your_username
    password: your_password
```

### Redis Setup

Update Redis configuration in `application.yml`:
```yaml
spring:
  redis:
    host: localhost
    port: 6379
    password: your_redis_password
```

### JWT Configuration

Update JWT secret in `application.yml`:
```yaml
jwt:
  secret: YourSuperSecretKeyForJWTTokenGeneration123456789
  expiration: 86400000 # 24 hours
  refresh-expiration: 604800000 # 7 days
```

## 🚀 Running the Application

### Build the project
```bash
cd backend
mvn clean install
```

### Run the application
```bash
mvn spring-boot:run
```

The application will start on `http://localhost:8080/api`

## 📚 API Documentation

Once the application is running, access:

- **Swagger UI**: http://localhost:8080/api/swagger-ui.html
- **OpenAPI Docs**: http://localhost:8080/api/v3/api-docs

## 🔌 WebSocket Endpoints

- **Endpoint**: `ws://localhost:8080/api/ws`
- **Protocol**: STOMP over WebSocket with SockJS

### Subscription Channels

- `/user/queue/events` - Event notifications (created/updated/deleted)
- `/user/queue/reminders` - Reminder notifications
- `/user/queue/sync` - Sync notifications

### Authentication

Include JWT token in WebSocket connection:
```javascript
const socket = new SockJS('/api/ws');
const stompClient = Stomp.over(socket);

stompClient.connect({
  'Authorization': 'Bearer ' + token
}, function(frame) {
  // Connected
});
```

## 🔄 Data Synchronization

### Sync Flow

1. Client sends last sync timestamp
2. Server returns all events modified since last sync
3. Client sends local changes
4. Server resolves conflicts (server-side wins by default)
5. Server returns sync timestamp for next sync

### Sync API

```http
POST /api/sync/events
Content-Type: application/json
Authorization: Bearer {token}

{
  "lastSyncTime": "2024-01-01T00:00:00",
  "events": [
    {
      "eventId": "uuid",
      "title": "My Event",
      "startTime": "2024-01-15T10:00:00",
      "endTime": "2024-01-15T11:00:00"
    }
  ]
}
```

## 📦 Project Structure

```
backend/
├── src/main/java/com/calendar/
│   ├── CalendarApplication.java        # Main application class
│   ├── config/                         # Configuration classes
│   │   ├── OpenApiConfig.java         # Swagger/OpenAPI config
│   │   ├── RedisConfig.java           # Redis cache config
│   │   ├── SecurityConfig.java        # Security config
│   │   ├── WebConfig.java             # Web MVC config
│   │   └── WebSocketConfig.java       # WebSocket config
│   ├── controller/                     # REST Controllers
│   │   ├── AuthController.java        # Authentication endpoints
│   │   ├── EventController.java       # Event management endpoints
│   │   ├── HealthController.java      # Health check
│   │   ├── SyncController.java        # Data sync endpoints
│   │   └── WebSocketController.java   # WebSocket handlers
│   ├── dto/                            # Data Transfer Objects
│   ├── mapper/                         # MapStruct mappers
│   ├── model/                          # JPA entities
│   ├── repository/                     # Spring Data repositories
│   ├── security/                       # Security components
│   ├── service/                        # Business logic
│   └── sync/                           # Synchronization logic
└── src/main/resources/
    ├── application.yml                 # Application configuration
    └── db/migration/                   # Flyway migrations
        └── V1__Create_initial_tables.sql
```

## 🧪 Testing

### Run all tests
```bash
mvn test
```

### Run with coverage
```bash
mvn test jacoco:report
```

## 🔒 Security

- All endpoints (except `/api/auth/**` and `/api/health`) require JWT authentication
- Passwords are hashed using BCrypt
- SQL injection protection via JPA parameterized queries
- CORS configured for cross-origin requests

## 📝 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `SERVER_PORT` | Server port | 8080 |
| `DB_URL` | Database URL | jdbc:mysql://localhost:3306/calendar_db |
| `DB_USERNAME` | Database username | root |
| `DB_PASSWORD` | Database password | root |
| `REDIS_HOST` | Redis host | localhost |
| `REDIS_PORT` | Redis port | 6379 |
| `JWT_SECRET` | JWT secret key | - |

## 🐳 Docker Support

Build Docker image:
```bash
docker build -t calendar-backend .
```

Run container:
```bash
docker run -p 8080:8080 calendar-backend
```

## 📊 Monitoring

- Health check endpoint: `/api/health`
- Metrics exposed via Spring Boot Actuator (if enabled)

## 🚦 Current Status

### ✅ Completed
- Spring Boot project structure
- User authentication (JWT)
- Event CRUD operations
- Data synchronization service
- WebSocket real-time push
- Database migrations (Flyway)
- API documentation (Swagger)
- Reminder processing service

### 🔄 In Progress
- Integration with Firebase Cloud Messaging (FCM)
- Google Calendar integration
- Performance optimization

### 📅 Planned
- Elasticsearch integration for full-text search
- RabbitMQ for async message processing
- Kubernetes deployment configurations

## 📞 API Endpoints Summary

### Authentication
- `POST /api/auth/register` - Register new user
- `POST /api/auth/login` - User login
- `POST /api/auth/refresh` - Refresh access token
- `GET /api/auth/me` - Get current user info

### Events
- `GET /api/events` - Get all events
- `GET /api/events/{id}` - Get event by ID
- `POST /api/events` - Create event
- `PUT /api/events/{id}` - Update event
- `DELETE /api/events/{id}` - Delete event
- `GET /api/events/range?start=&end=` - Get events by date range
- `GET /api/events/search?keyword=` - Search events
- `GET /api/events/category/{category}` - Get events by category

### Sync
- `POST /api/sync/events` - Sync events
- `GET /api/sync/status` - Get sync status

### Health
- `GET /api/health` - Health check

## 📄 License

Apache 2.0

## 👥 Team

Calendar Backend Team

---

**Last Updated**: 2026-03-10
**Version**: 1.0.0
