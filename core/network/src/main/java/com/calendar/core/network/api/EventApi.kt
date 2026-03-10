package com.calendar.core.network.api

import com.calendar.core.network.dto.*
import retrofit2.Response
import retrofit2.http.*

/**
 * 事件API接口
 */
interface EventApi {

    /**
     * 创建事件
     */
    @POST("events")
    suspend fun createEvent(
        @Body request: CreateEventRequest
    ): Response<ApiResponse<EventDTO>>

    /**
     * 更新事件
     */
    @PUT("events/{eventId}")
    suspend fun updateEvent(
        @Path("eventId") eventId: Long,
        @Body request: UpdateEventRequest
    ): Response<ApiResponse<EventDTO>>

    /**
     * 删除事件
     */
    @DELETE("events/{eventId}")
    suspend fun deleteEvent(
        @Path("eventId") eventId: Long
    ): Response<ApiResponse<Unit>>

    /**
     * 获取事件详情
     */
    @GET("events/{eventId}")
    suspend fun getEvent(
        @Path("eventId") eventId: Long
    ): Response<ApiResponse<EventDTO>>

    /**
     * 获取所有事件
     */
    @GET("events")
    suspend fun getAllEvents(): Response<ApiResponse<List<EventDTO>>>

    /**
     * 根据日期范围获取事件
     */
    @GET("events/range")
    suspend fun getEventsByDateRange(
        @Query("start") start: String,
        @Query("end") end: String
    ): Response<ApiResponse<List<EventDTO>>>

    /**
     * 分页获取事件
     */
    @GET("events/page")
    suspend fun getEventsPage(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<PageResponse<EventDTO>>>

    /**
     * 搜索事件
     */
    @GET("events/search")
    suspend fun searchEvents(
        @Query("keyword") keyword: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Response<ApiResponse<PageResponse<EventDTO>>>

    /**
     * 根据分类获取事件
     */
    @GET("events/category/{category}")
    suspend fun getEventsByCategory(
        @Path("category") category: String
    ): Response<ApiResponse<List<EventDTO>>>
}
