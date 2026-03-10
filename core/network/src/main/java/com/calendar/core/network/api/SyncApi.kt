package com.calendar.core.network.api

import com.calendar.core.network.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 数据同步API接口
 */
interface SyncApi {

    /**
     * 同步事件
     */
    @POST("sync/events")
    suspend fun syncEvents(
        @Body request: SyncRequest
    ): Response<ApiResponse<SyncResponse>>

    /**
     * 获取同步状态
     */
    @GET("sync/status")
    suspend fun getSyncStatus(): Response<ApiResponse<SyncStatusResponse>>
}
