package com.calendar.feature.sync

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.calendar.core.data.repository.SyncRepositoryImpl
import com.calendar.core.data.repository.SyncState
import com.calendar.core.data.repository.SyncResult
import com.calendar.core.data.repository.SyncStatusInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SyncViewModel @Inject constructor(
    private val syncRepository: SyncRepositoryImpl
) : ViewModel() {

    private val _uiState = MutableStateFlow<SyncUiState>(SyncUiState.Idle)
    val uiState: StateFlow<SyncUiState> = _uiState

    private val _syncStatus = MutableStateFlow<SyncStatusInfo?>(null)
    val syncStatus: StateFlow<SyncStatusInfo?> = _syncStatus

    init {
        loadSyncStatus()
    }

    /**
     * 执行同步
     */
    fun sync() {
        viewModelScope.launch {
            _uiState.value = SyncUiState.Syncing
            
            syncRepository.sync()
                .onSuccess { result ->
                    if (result.success) {
                        _uiState.value = SyncUiState.Success(
                            message = "同步成功，共同步${result.syncedCount}个事件"
                        )
                    } else {
                        _uiState.value = SyncUiState.Conflict(
                            conflicts = result.conflicts,
                            message = result.message
                        )
                    }
                    loadSyncStatus()
                }
                .onFailure { error ->
                    _uiState.value = SyncUiState.Error(
                        message = error.message ?: "同步失败"
                    )
                }
        }
    }

    /**
     * 强制全量同步
     */
    fun forceFullSync() {
        viewModelScope.launch {
            _uiState.value = SyncUiState.Syncing
            
            syncRepository.forceFullSync()
                .onSuccess { result ->
                    _uiState.value = SyncUiState.Success(
                        message = "全量同步成功"
                    )
                    loadSyncStatus()
                }
                .onFailure { error ->
                    _uiState.value = SyncUiState.Error(
                        message = error.message ?: "全量同步失败"
                    )
                }
        }
    }

    /**
     * 加载同步状态
     */
    private fun loadSyncStatus() {
        viewModelScope.launch {
            syncRepository.getSyncStatus()
                .onSuccess { status ->
                    _syncStatus.value = status
                }
        }
    }

    /**
     * 重置状态
     */
    fun resetState() {
        _uiState.value = SyncUiState.Idle
    }
}

sealed class SyncUiState {
    object Idle : SyncUiState()
    object Syncing : SyncUiState()
    data class Success(val message: String) : SyncUiState()
    data class Conflict(
        val conflicts: List<com.calendar.core.domain.model.CalendarEvent>,
        val message: String
    ) : SyncUiState()
    data class Error(val message: String) : SyncUiState()
}
