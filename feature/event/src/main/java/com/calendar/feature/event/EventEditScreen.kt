package com.calendar.feature.event

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.calendar.core.domain.model.CalendarEvent
import com.calendar.core.domain.model.EventColor
import com.calendar.core.domain.model.ReminderType
import com.calendar.core.domain.model.RepeatRule
import com.calendar.feature.event.components.*
import java.time.LocalDate
import java.time.LocalTime

/**
 * 事件编辑界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventEditScreen(
    eventId: Long? = null,
    initialDate: LocalDate? = null,
    onSaveSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: EventEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // 初始化数据
    LaunchedEffect(eventId, initialDate) {
        if (eventId != null) {
            viewModel.loadEvent(eventId)
        } else if (initialDate != null) {
            viewModel.initializeWithDate(initialDate)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(if (eventId == null) "新建事件" else "编辑事件") 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "关闭")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.saveEvent {
                                onSaveSuccess()
                            }
                        },
                        enabled = uiState.isValid
                    ) {
                        Text("保存")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            // 标题输入
            TitleInput(
                title = uiState.title,
                onTitleChange = { viewModel.updateTitle(it) }
            )
            
            Divider(modifier = Modifier.padding(start = 72.dp))
            
            // 时间设置
            DateTimeSection(
                startDate = uiState.startDate,
                startTime = uiState.startTime,
                endDate = uiState.endDate,
                endTime = uiState.endTime,
                isAllDay = uiState.isAllDay,
                onStartDateChange = { viewModel.updateStartDate(it) },
                onStartTimeChange = { viewModel.updateStartTime(it) },
                onEndDateChange = { viewModel.updateEndDate(it) },
                onEndTimeChange = { viewModel.updateEndTime(it) },
                onAllDayChange = { viewModel.updateIsAllDay(it) }
            )
            
            Divider(modifier = Modifier.padding(start = 72.dp))
            
            // 提醒设置
            ReminderSection(
                reminderType = uiState.reminderType,
                onReminderTypeChange = { viewModel.updateReminderType(it) }
            )
            
            Divider(modifier = Modifier.padding(start = 72.dp))
            
            // 重复设置
            RepeatSection(
                repeatRule = uiState.repeatRule,
                onRepeatRuleChange = { viewModel.updateRepeatRule(it) }
            )
            
            Divider(modifier = Modifier.padding(start = 72.dp))
            
            // 位置设置
            LocationSection(
                location = uiState.location,
                onLocationChange = { viewModel.updateLocation(it) }
            )
            
            Divider(modifier = Modifier.padding(start = 72.dp))
            
            // 颜色设置
            ColorSection(
                selectedColor = uiState.color,
                onColorChange = { viewModel.updateColor(it) }
            )
            
            Divider(modifier = Modifier.padding(start = 72.dp))
            
            // 描述输入
            DescriptionSection(
                description = uiState.description,
                onDescriptionChange = { viewModel.updateDescription(it) }
            )
            
            // 删除按钮（仅编辑模式）
            if (eventId != null) {
                Spacer(modifier = Modifier.height(16.dp))
                DeleteButton(onClick = { viewModel.deleteEvent(onSaveSuccess) })
            }
        }
    }
}

/**
 * 标题输入
 */
@Composable
private fun TitleInput(
    title: String,
    onTitleChange: (String) -> Unit
) {
    OutlinedTextField(
        value = title,
        onValueChange = onTitleChange,
        label = { Text("标题") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        leadingIcon = {
            Icon(Icons.Default.Edit, contentDescription = null)
        },
        singleLine = true
    )
}

/**
 * 删除按钮
 */
@Composable
private fun DeleteButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = MaterialTheme.colorScheme.error
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(Icons.Default.Delete, contentDescription = "删除")
        Spacer(modifier = Modifier.width(8.dp))
        Text("删除事件")
    }
}
