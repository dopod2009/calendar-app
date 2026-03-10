package com.calendar.feature.event.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * 日期时间设置区域
 */
@Composable
fun DateTimeSection(
    startDate: LocalDate,
    startTime: LocalTime?,
    endDate: LocalDate?,
    endTime: LocalTime?,
    isAllDay: Boolean,
    onStartDateChange: (LocalDate) -> Unit,
    onStartTimeChange: (LocalTime?) -> Unit,
    onEndDateChange: (LocalDate?) -> Unit,
    onEndTimeChange: (LocalTime?) -> Unit,
    onAllDayChange: (Boolean) -> Unit
) {
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        // 全天开关
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onAllDayChange(!isAllDay) }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "全天",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Switch(
                checked = isAllDay,
                onCheckedChange = onAllDayChange
            )
        }
        
        // 开始时间
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { 
                    if (isAllDay) showStartDatePicker = true 
                    else showStartDatePicker = true 
                }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "开始",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = if (isAllDay) {
                    startDate.format(DateTimeFormatter.ofPattern("M月d日"))
                } else {
                    "${startDate.format(DateTimeFormatter.ofPattern("M月d日"))} ${startTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""}"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
        
        // 结束时间（仅非全天事件显示）
        if (!isAllDay) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showEndDatePicker = true }
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(48.dp))
                Text(
                    text = "结束",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = endDate?.let { 
                        "${it.format(DateTimeFormatter.ofPattern("M月d日"))} ${endTime?.format(DateTimeFormatter.ofPattern("HH:mm")) ?: ""}"
                    } ?: "未设置",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
    
    // 日期选择器对话框
    if (showStartDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            onDateSelected = { date ->
                onStartDateChange(date)
                showStartDatePicker = false
            }
        )
    }
    
    if (showEndDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showEndDatePicker = false },
            onDateSelected = { date ->
                onEndDateChange(date)
                showEndDatePicker = false
            }
        )
    }
}

/**
 * 日期选择器对话框（简化版）
 */
@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (LocalDate) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("选择日期") },
        text = {
            Text("日期选择器功能待实现")
        },
        confirmButton = {
            TextButton(onClick = { onDateSelected(LocalDate.now()) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("取消")
            }
        }
    )
}
