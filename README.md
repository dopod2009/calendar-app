# Android 日历应用

一款功能完善的 Android 原生日历应用，支持农历显示、节日提醒、事件管理等功能。

## 技术栈

- **Kotlin**: 1.9.21
- **最低 SDK**: API 23 (Android 6.0)
- **目标 SDK**: API 34 (Android 14)
- **架构**: MVVM + Clean Architecture
- **UI**: Jetpack Compose + Material Design 3
- **依赖注入**: Hilt
- **数据库**: Room

## 项目结构

```
CalendarApp/
├── app/                           # 应用模块
│   ├── src/main/
│   │   ├── java/com/calendar/app/
│   │   │   ├── CalendarApplication.kt
│   │   │   ├── MainActivity.kt
│   │   │   └── ui/theme/          # Material 3 主题
│   │   └── res/
│   └── build.gradle.kts
├── feature/                       # 功能模块
│   ├── calendar/                  # 日历视图模块
│   │   └── src/main/java/com/calendar/feature/calendar/
│   │       ├── CalendarScreen.kt
│   │       ├── components/        # UI 组件
│   │       │   ├── MonthCalendarView.kt
│   │       │   ├── WeekCalendarView.kt
│   │       │   ├── DayCalendarView.kt
│   │       │   ├── AnimatedCalendarPager.kt
│   │       │   └── CalendarPager.kt
│   │       └── util/
│   │           └── CalendarUtils.kt
│   └── event/                     # 事件管理模块
├── core/                          # 核心模块
│   ├── common/                    # 公共工具
│   │   └── src/main/java/com/calendar/core/common/
│   │       └── util/
│   │           └── ChineseCalendarHelper.kt  # 农历算法
│   ├── domain/                    # 业务模型
│   │   └── src/main/java/com/calendar/core/domain/
│   │       └── model/
│   │           ├── CalendarDay.kt
│   │           ├── CalendarEvent.kt
│   │           └── CalendarMonth.kt
│   └── data/                      # 数据层
└── build.gradle.kts
```

## 核心功能（Week 4-5 已完成）

### 1. 日历视图模块

#### 月视图 (MonthCalendarView)
- ✅ 月份日期网格展示
- ✅ 农历日期和节日显示
- ✅ 今天高亮标记
- ✅ 选中日期效果
- ✅ 事件指示器
- ✅ 非当前月份日期灰显
- ✅ 周末颜色区分

#### 周视图 (WeekCalendarView)
- ✅ 7天横向布局
- ✅ 快速日期切换
- ✅ 农历显示
- ✅ 事件指示器

#### 日视图 (DayCalendarView)
- ✅ 日期详细信息展示
- ✅ 农历、节气、节日信息
- ✅ 事件列表
- ✅ 添加事件按钮

### 2. 手势交互
- ✅ 左右滑动切换月份/周/日
- ✅ 手势拖拽反馈动画
- ✅ 弹性回弹效果

### 3. 视图切换动画
- ✅ 平滑过渡动画
- ✅ 视图类型选择器
- ✅ 动画同步效果

### 4. 农历节日显示
- ✅ 公历转农历算法
- ✅ 天干地支年份
- ✅ 传统节日（春节、中秋等）
- ✅ 公历节日（元旦、国庆等）
- ✅ 24节气计算
- ✅ 闰月处理

## 数据模型

### CalendarDay
```kotlin
data class CalendarDay(
    val date: LocalDate,              // 日期
    val isToday: Boolean,             // 是否今天
    val isSelected: Boolean,          // 是否选中
    val isCurrentMonth: Boolean,      // 是否当前月
    val lunarDate: String,            // 农历日期
    val lunarMonth: String,           // 农历月份
    val solarTerm: String,            // 节气
    val festival: String,             // 传统节日
    val gregorianFestival: String,    // 公历节日
    val hasEvent: Boolean,            // 是否有事件
    val eventCount: Int               // 事件数量
)
```

### CalendarEvent
```kotlin
data class CalendarEvent(
    val id: Long,
    val title: String,
    val startDate: LocalDate,
    val startTime: LocalTime?,
    val isAllDay: Boolean,
    val color: EventColor,
    val reminder: ReminderType,
    val repeatRule: RepeatRule
)
```

## 依赖项

### 核心库
- AndroidX Core: 1.12.0
- Lifecycle: 2.6.2
- Activity Compose: 1.8.2
- Navigation Compose: 2.7.6

### Compose
- Compose BOM: 2023.10.01
- Material 3
- Material Icons Extended

### DI & Database
- Hilt: 2.48
- Room: 2.6.1

### Coroutines
- Kotlinx Coroutines: 1.7.3

## 构建和运行

### 环境要求
- JDK 17+
- Android Studio Hedgehog | 2023.1.1 或更高版本
- Gradle 8.2

### 构建命令
```bash
# 清理项目
./gradlew clean

# 构建 Debug 版本
./gradlew assembleDebug

# 构建 Release 版本
./gradlew assembleRelease

# 运行测试
./gradlew test
```

## 下一步计划（Week 5-7）

### Week 5-6: 事件管理模块
- [ ] 事件创建/编辑界面
- [ ] 事件列表展示
- [ ] 事件搜索功能
- [ ] 事件分类管理
- [ ] Room 数据库实现
- [ ] 事件 Repository

### Week 6-7: 提醒功能模块
- [ ] 本地提醒实现
- [ ] 提醒时间配置
- [ ] 提醒通知管理
- [ ] 重复规则设置
- [ ] Android 13+ 权限适配

## 架构图

```
┌─────────────────────────────────────────────────────┐
│                    Presentation                      │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────┐ │
│  │  Calendar    │  │    Event     │  │  View     │ │
│  │  Screen      │  │   Screen     │  │ Components│ │
│  └──────────────┘  └──────────────┘  └───────────┘ │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│                      Domain                          │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────┐ │
│  │   Models     │  │   UseCases   │  │Repositories│ │
│  │  (Calendar)  │  │              │  │Interfaces │ │
│  └──────────────┘  └──────────────┘  └───────────┘ │
└─────────────────────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────┐
│                       Data                           │
│  ┌──────────────┐  ┌──────────────┐  ┌───────────┐ │
│  │   Room DB    │  │    Remote    │  │ Repository│ │
│  │  (Events)    │  │    API       │  │Implementation│
│  └──────────────┘  └──────────────┘  └───────────┘ │
└─────────────────────────────────────────────────────┘
```

## 代码规范

- 遵循 Kotlin 官方编码规范
- 使用 Material Design 3 设计系统
- Compose 函数命名规范
- 单元测试覆盖率 ≥ 70%

## 许可证

Copyright © 2026 Calendar App Team
