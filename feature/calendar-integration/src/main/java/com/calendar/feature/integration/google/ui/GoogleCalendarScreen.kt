package com.calendar.feature.integration.google.ui

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calendar.feature.integration.google.*

/**
 * Google Calendar 集成界面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleCalendarScreen(
    viewModel: GoogleCalendarViewModel = viewModel(),
    onEventClick: (GoogleCalendarEvent) -> Unit = {},
    onImportComplete: (List<GoogleCalendarEvent>) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val calendars by viewModel.calendars.collectAsState()
    val events by viewModel.events.collectAsState()
    val syncProgress by viewModel.syncProgress.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    // Google Sign-In Launcher
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            viewModel.handleSignInResult(task)
        }
    }

    // 显示状态消息
    LaunchedEffect(uiState) {
        when (val state = uiState) {
            is GoogleCalendarUiState.Error -> {
                snackbarHostState.showSnackbar(state.message)
            }
            is GoogleCalendarUiState.ImportComplete -> {
                snackbarHostState.showSnackbar("成功导入 ${state.count} 个事件")
            }
            is GoogleCalendarUiState.ExportComplete -> {
                snackbarHostState.showSnackbar("成功导出 ${state.count} 个事件")
            }
            is GoogleCalendarUiState.SyncComplete -> {
                val result = state.result
                snackbarHostState.showSnackbar(
                    "同步完成: 上传 ${result.uploadedCount} 个, 下载 ${result.downloadedEvents.size} 个"
                )
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Google 日历集成") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            if (uiState is GoogleCalendarUiState.SignedIn || uiState is GoogleCalendarUiState.EventsLoaded) {
                FloatingActionButton(
                    onClick = {
                        // 刷新事件列表
                        viewModel.loadEvents()
                    }
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "刷新")
                }
            }
        }
    ) { paddingValues ->
        when (val state = uiState) {
            is GoogleCalendarUiState.Idle -> {
                // 未登录状态
                SignInPrompt(
                    onSignInClick = {
                        signInLauncher.launch(viewModel.getSignInIntent())
                    }
                )
            }

            is GoogleCalendarUiState.Loading -> {
                // 加载中
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("正在加载...")
                        syncProgress?.let { progress ->
                            if (progress.total > 0) {
                                LinearProgressIndicator(
                                    progress = { progress.current.toFloat() / progress.total },
                                    modifier = Modifier.fillMaxWidth(0.5f)
                                )
                                Text(progress.status)
                            }
                        }
                    }
                }
            }

            is GoogleCalendarUiState.SignedIn -> {
                // 已登录
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // 用户信息
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "已登录",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = state.email,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            IconButton(onClick = { viewModel.signOut() }) {
                                Icon(Icons.Default.Logout, contentDescription = "登出")
                            }
                        }
                    }

                    // 日历选择
                    if (calendars.isNotEmpty()) {
                        CalendarSelector(
                            calendars = calendars,
                            onCalendarSelected = { calendar ->
                                viewModel.loadEvents(calendar.id)
                            }
                        )
                    }

                    // 操作按钮
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.loadEvents() },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null)
                            Spacer(Modifier.width(8.dp))
                            Text("加载事件")
                        }
                    }

                    Spacer(Modifier.height(16.dp))
                }
            }

            is GoogleCalendarUiState.EventsLoaded -> {
                // 事件列表
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // 统计信息
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("已加载 ${state.count} 个事件")
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.importFromGoogle(
                                            onImportComplete = onImportComplete
                                        )
                                    }
                                ) {
                                    Icon(Icons.Default.ImportExport, contentDescription = null)
                                    Spacer(Modifier.width(4.dp))
                                    Text("导入")
                                }
                            }
                        }
                    }

                    // 事件列表
                    EventsList(
                        events = events,
                        onEventClick = onEventClick
                    )
                }
            }

            is GoogleCalendarUiState.Syncing -> {
                // 同步中
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator()
                        Text("正在同步...")
                        syncProgress?.let { progress ->
                            LinearProgressIndicator(
                                progress = { progress.current.toFloat() / progress.total.coerceAtLeast(1) },
                                modifier = Modifier.fillMaxWidth(0.5f)
                            )
                            Text(progress.status)
                        }
                    }
                }
            }

            else -> {}
        }
    }
}

/**
 * 登录提示
 */
@Composable
private fun SignInPrompt(onSignInClick: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "连接 Google 日历",
                style = MaterialTheme.typography.headlineMedium
            )
            Text(
                text = "同步您的 Google 日历事件",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Button(onClick = onSignInClick) {
                Icon(Icons.Default.Login, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("使用 Google 账号登录")
            }
        }
    }
}

/**
 * 日历选择器
 */
@Composable
private fun CalendarSelector(
    calendars: List<CalendarInfo>,
    onCalendarSelected: (CalendarInfo) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var selectedCalendar by remember { mutableStateOf<CalendarInfo?>(null) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        OutlinedTextField(
            value = selectedCalendar?.summary ?: "选择日历",
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            calendars.forEach { calendar ->
                DropdownMenuItem(
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            calendar.backgroundColor?.let { colorHex ->
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .padding(2.dp)
                                ) {
                                    // 使用颜色显示
                                }
                            }
                            Column {
                                Text(calendar.summary)
                                if (calendar.primary) {
                                    Text(
                                        "主日历",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    },
                    onClick = {
                        selectedCalendar = calendar
                        onCalendarSelected(calendar)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * 事件列表
 */
@Composable
private fun EventsList(
    events: List<GoogleCalendarEvent>,
    onEventClick: (GoogleCalendarEvent) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(events) { event ->
            EventCard(
                event = event,
                onClick = { onEventClick(event) }
            )
        }
    }
}

/**
 * 事件卡片
 */
@Composable
private fun EventCard(
    event: GoogleCalendarEvent,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = event.title,
                style = MaterialTheme.typography.titleMedium
            )
            if (event.description.isNotEmpty()) {
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }
            Spacer(Modifier.height(8.dp))
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = formatEventTime(event.startTime, event.endTime),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                if (event.location.isNotEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = event.location,
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

/**
 * 格式化事件时间
 */
private fun formatEventTime(startTime: Long, endTime: Long): String {
    val start = java.text.SimpleDateFormat("MM/dd HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(startTime))
    val end = java.text.SimpleDateFormat("HH:mm", java.util.Locale.getDefault())
        .format(java.util.Date(endTime))
    return "$start - $end"
}
