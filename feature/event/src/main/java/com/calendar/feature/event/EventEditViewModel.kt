package com.calendar.feature.event

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.domain.model.EventColor
import com.calendar.core.domain.model.ReminderType
import com.calendar.core.domain.model.RepeatRule
import com.calendar.core.domain.usecase.AddEventUseCase
import com.calendar.core.domain.usecase.DeleteEventUseCase
import com.calendar.core.domain.usecase.GetEventsByDateUseCase
import com.calendar.core.domain.usecase.UpdateEventUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * 事件编辑界面状态
 */
data class EventEditUiState(
    val id: Long = 0,
    val title: String = "",
    val description: String = "",
    val startDate: LocalDate = LocalDate.now(),
    val startTime: LocalTime? = LocalTime.now(),
    val endDate: LocalDate? = null,
    val endTime: LocalTime? = null,
    val isAllDay: Boolean = false,
    val location: String = "",
    val color: EventColor = EventColor.BLUE,
    val reminderType: ReminderType = ReminderType.NONE,
    val repeatRule: RepeatRule = RepeatRule.NONE,
    val isLoading: Boolean = false,
    val error: String? = null
) {
    val isValid: Boolean
        get() = title.isNotBlank()
}

/**
 * 事件编辑ViewModel
 */
@HiltViewModel
class EventEditViewModel @Inject constructor(
    private val addEventUseCase: AddEventUseCase,
    private val updateEventUseCase: UpdateEventUseCase,
    private val deleteEventUseCase: DeleteEventUseCase,
    private val getEventsByDateUseCase: GetEventsByDateUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(EventEditUiState())
    val uiState: StateFlow<EventEditUiState> = _uiState
    
    /**
     * 加载事件数据
     */
    fun loadEvent(eventId: Long) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // TODO: 从数据库加载事件
            _uiState.update { it.copy(isLoading = false) }
        }
    }
    
    /**
     * 用指定日期初始化
     */
    fun initializeWithDate(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
    }
    
    /**
     * 更新标题
     */
    fun updateTitle(title: String) {
        _uiState.update { it.copy(title = title) }
    }
    
    /**
     * 更新描述
     */
    fun updateDescription(description: String) {
        _uiState.update { it.copy(description = description) }
    }
    
    /**
     * 更新开始日期
     */
    fun updateStartDate(date: LocalDate) {
        _uiState.update { it.copy(startDate = date) }
    }
    
    /**
     * 更新开始时间
     */
    fun updateStartTime(time: LocalTime?) {
        _uiState.update { it.copy(startTime = time) }
    }
    
    /**
     * 更新结束日期
     */
    fun updateEndDate(date: LocalDate?) {
        _uiState.update { it.copy(endDate = date) }
    }
    
    /**
     * 更新结束时间
     */
    fun updateEndTime(time: LocalTime?) {
        _uiState.update { it.copy(endTime = time) }
    }
    
    /**
     * 更新是否全天
     */
    fun updateIsAllDay(isAllDay: Boolean) {
        _uiState.update { it.copy(
            isAllDay = isAllDay,
            startTime = if (isAllDay) null else LocalTime.now(),
            endTime = if (isAllDay) null else null
        )}
    }
    
    /**
     * 更新位置
     */
    fun updateLocation(location: String) {
        _uiState.update { it.copy(location = location) }
    }
    
    /**
     * 更新颜色
     */
    fun updateColor(color: EventColor) {
        _uiState.update { it.copy(color = color) }
    }
    
    /**
     * 更新提醒类型
     */
    fun updateReminderType(reminderType: ReminderType) {
        _uiState.update { it.copy(reminderType = reminderType) }
    }
    
    /**
     * 更新重复规则
     */
    fun updateRepeatRule(repeatRule: RepeatRule) {
        _uiState.update { it.copy(repeatRule = repeatRule) }
    }
    
    /**
     * 保存事件
     */
    fun saveEvent(onSuccess: () -> Unit) {
        viewModelScope.launch {
            val state = _uiState.value
            
            val event = CalendarEvent(
                id = state.id,
                title = state.title,
                description = state.description,
                startDate = state.startDate,
                startTime = state.startTime,
                endDate = state.endDate,
                endTime = state.endTime,
                isAllDay = state.isAllDay,
                location = state.location,
                color = state.color,
                reminder = state.reminderType,
                repeatRule = state.repeatRule,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )
            
            if (state.id == 0L) {
                addEventUseCase(event)
            } else {
                updateEventUseCase(event)
            }
            
            onSuccess()
        }
    }
    
    /**
     * 删除事件
     */
    fun deleteEvent(onSuccess: () -> Unit) {
        viewModelScope.launch {
            deleteEventUseCase(_uiState.value.id)
            onSuccess()
        }
    }
}
