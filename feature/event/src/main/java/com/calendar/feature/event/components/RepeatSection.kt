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
import com.calendar.core.domain.model.RepeatRule

/**
 * 重复设置区域
 */
@Composable
fun RepeatSection(
    repeatRule: RepeatRule,
    onRepeatRuleChange: (RepeatRule) -> Unit
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
                imageVector = Icons.Default.Repeat,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(24.dp))
            Text(
                text = "重复",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = getRepeatText(repeatRule),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }
    }
    
    // 重复选项下拉菜单
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = { expanded = false }
    ) {
        RepeatRule.values().forEach { rule ->
            DropdownMenuItem(
                text = { Text(getRepeatText(rule)) },
                onClick = {
                    onRepeatRuleChange(rule)
                    expanded = false
                }
            )
        }
    }
}

/**
 * 获取重复文本
 */
private fun getRepeatText(rule: RepeatRule): String {
    return when (rule) {
        RepeatRule.NONE -> "不重复"
        RepeatRule.DAILY -> "每天"
        RepeatRule.WEEKLY -> "每周"
        RepeatRule.MONTHLY -> "每月"
        RepeatRule.YEARLY -> "每年"
        RepeatRule.CUSTOM -> "自定义"
    }
}
