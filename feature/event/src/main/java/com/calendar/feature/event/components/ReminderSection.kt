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
import com.calendar.core.domain.model.ReminderType

/**
 * 提醒设置区域
 */
@Composable
fun ReminderSection(
    reminderType: ReminderType,
    onReminderTypeChange: (ReminderType) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = true }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "提醒",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = getReminderText(reminderType),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
    
    // 提醒选项下拉菜单
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        ReminderType.values().forEach { type ->
            DropdownMenuItem(
                text = { Text(getReminderText(type)) },
                onClick = {
                    onReminderTypeChange(type)
                    expanded = false
                }
            )
        }
    }
}

/**
 * 获取提醒文本
 */
private fun getReminderText(type: ReminderType): String {
    return when (type) {
        ReminderType.NONE -> "无"
        ReminderType.AT_TIME -> "事件开始时"
        ReminderType.FIVE_MINUTES -> "提前5分钟"
        ReminderType.FIFTEEN_MINUTES -> "提前15分钟"
        ReminderType.THIRTY_MINUTES -> "提前30分钟"
        ReminderType.ONE_HOUR -> "提前1小时"
        ReminderType.TWO_HOURS -> "提前2小时"
        ReminderType.ONE_DAY -> "提前1天"
        ReminderType.TWO_DAYS -> "提前2天"
        ReminderType.ONE_WEEK -> "提前1周"
    }
}
