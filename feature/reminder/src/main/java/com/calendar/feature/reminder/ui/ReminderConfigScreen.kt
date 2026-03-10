package com.calendar.feature.reminder.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.calendar.core.domain.model.Reminder
import com.calendar.core.domain.model.ReminderRule
import java.time.format.DateTimeFormatter

/**
 * 提醒配置界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderConfigScreen(
    eventId: Long,
    eventTitle: String,
    eventStartTime: java.time.LocalDateTime,
    onSave: (List<ReminderRule>) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedRules by remember {
        mutableStateOf(
            listOf(
                ReminderRule.FIFTEEN_MINUTES,
                ReminderRule.ONE_HOUR
            )
        )
    }
    var customMinutes by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("设置提醒") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "为「$eventTitle」设置提醒",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // 预设提醒选项
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        ReminderOption(
                            label = "准时",
                            isSelected = ReminderRule.AT_TIME in selectedRules,
                            onToggle = {
                                selectedRules = toggleRule(selectedRules, ReminderRule.AT_TIME)
                            }
                        )
                    }
                    item {
                        ReminderOption(
                            label = "5分钟前",
                            isSelected = ReminderRule.FIVE_MINUTES in selectedRules,
                            onToggle = {
                                selectedRules = toggleRule(selectedRules, ReminderRule.FIVE_MINUTES)
                            }
                        )
                    }
                    item {
                        ReminderOption(
                            label = "15分钟前",
                            isSelected = ReminderRule.FIFTEEN_MINUTES in selectedRules,
                            onToggle = {
                                selectedRules = toggleRule(selectedRules, ReminderRule.FIFTEEN_MINUTES)
                            }
                        )
                    }
                    item {
                        ReminderOption(
                            label = "30分钟前",
                            isSelected = ReminderRule.THIRTY_MINUTES in selectedRules,
                            onToggle = {
                                selectedRules = toggleRule(selectedRules, ReminderRule.THIRTY_MINUTES)
                            }
                        )
                    }
                    item {
                        ReminderOption(
                            label = "1小时前",
                            isSelected = ReminderRule.ONE_HOUR in selectedRules,
                            onToggle = {
                                selectedRules = toggleRule(selectedRules, ReminderRule.ONE_HOUR)
                            }
                        )
                    }
                    item {
                        ReminderOption(
                            label = "1天前",
                            isSelected = ReminderRule.ONE_DAY in selectedRules,
                            onToggle = {
                                selectedRules = toggleRule(selectedRules, ReminderRule.ONE_DAY)
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(selectedRules) }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    )
}

@Composable
private fun ReminderOption(
    label: String,
    isSelected: Boolean,
    onToggle: () -> Unit
) {
    FilterChip(
        selected = isSelected,
        onClick = onToggle,
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    )
}

private fun toggleRule(rules: List<ReminderRule>, rule: ReminderRule): List<ReminderRule> {
    return if (rule in rules) {
        rules - rule
    } else {
        rules + rule
    }
}

/**
 * 提醒列表界面
 */
@Composable
fun ReminderListScreen(
    reminders: List<Reminder>,
    onCancelReminder: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    if (reminders.isEmpty()) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "暂无提醒",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reminders) { reminder ->
                ReminderItem(
                    reminder = reminder,
                    onCancel = { onCancelReminder(reminder.id) }
                )
            }
        }
    }
}

@Composable
private fun ReminderItem(
    reminder: Reminder,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = reminder.reminderTime.format(
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                    ),
                    style = MaterialTheme.typography.titleMedium
                )
                if (reminder.message != null) {
                    Text(
                        text = reminder.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            IconButton(onClick = onCancel) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "取消提醒"
                )
            }
        }
    }
}
