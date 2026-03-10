package com.calendar.core.common.optimization

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.LruCache
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.security.MessageDigest

/**
 * 图片缓存管理器
 */
object ImageCacheManager {

    // 内存缓存 (10MB)
    private val memoryCache: LruCache<String, Bitmap> by lazy {
        val maxMemory = (Runtime.getRuntime().maxMemory() / 1024).toInt()
        val cacheSize = maxMemory / 8 // 使用1/8的可用内存
        object : LruCache<String, Bitmap>(cacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                return bitmap.byteCount / 1024
            }
        }
    }

    // 磁盘缓存目录
    private var diskCacheDir: File? = null
    private const val DISK_CACHE_SIZE = 50L * 1024 * 1024 // 50MB

    /**
     * 初始化磁盘缓存
     */
    fun initDiskCache(cacheDir: File) {
        diskCacheDir = File(cacheDir, "image_cache").apply {
            if (!exists()) {
                mkdirs()
            }
        }
    }

    /**
     * 从缓存获取图片
     */
    fun getBitmapFromCache(key: String): Bitmap? {
        // 先从内存缓存获取
        memoryCache.get(key)?.let { return it }

        // 再从磁盘缓存获取
        return getBitmapFromDiskCache(key)?.also {
            memoryCache.put(key, it)
        }
    }

    /**
     * 添加图片到缓存
     */
    fun addBitmapToCache(key: String, bitmap: Bitmap) {
        // 添加到内存缓存
        memoryCache.put(key, bitmap)

        // 添加到磁盘缓存
        addBitmapToDiskCache(key, bitmap)
    }

    /**
     * 从磁盘缓存获取图片
     */
    private fun getBitmapFromDiskCache(key: String): Bitmap? {
        val diskDir = diskCacheDir ?: return null
        val file = File(diskDir, hashKeyForDisk(key))
        
        return try {
            FileInputStream(file).use { fis ->
                BitmapFactory.decodeStream(fis)
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 添加图片到磁盘缓存
     */
    private fun addBitmapToDiskCache(key: String, bitmap: Bitmap) {
        val diskDir = diskCacheDir ?: return
        val file = File(diskDir, hashKeyForDisk(key))

        try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
            }
        } catch (e: Exception) {
            // 忽略错误
        }
    }

    /**
     * 清除内存缓存
     */
    fun clearMemoryCache() {
        memoryCache.evictAll()
    }

    /**
     * 清除磁盘缓存
     */
    fun clearDiskCache() {
        diskCacheDir?.deleteRecursively()
    }

    /**
     * 获取缓存大小
     */
    fun getCacheSize(): Long {
        val diskDir = diskCacheDir ?: return 0
        return diskDir.walkTopDown()
            .filter { it.isFile }
            .map { it.length() }
            .sum()
    }

    /**
     * 生成磁盘缓存key
     */
    private fun hashKeyForDisk(key: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            md.update(key.toByteArray())
            md.digest().joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            key.hashCode().toString()
        }
    }

    /**
     * 加载图片（带缓存）
     */
    suspend fun loadBitmap(
        filePath: String,
        maxWidth: Int = 1024,
        maxHeight: Int = 1024
    ): Bitmap? = withContext(Dispatchers.IO) {
        // 检查缓存
        getBitmapFromCache(filePath)?.let { return@withContext it }

        // 从文件加载
        try {
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            BitmapFactory.decodeFile(filePath, options)

            // 计算采样率
            options.inSampleSize = calculateInSampleSize(options, maxWidth, maxHeight)
            options.inJustDecodeBounds = false

            val bitmap = BitmapFactory.decodeFile(filePath, options)
            bitmap?.let { addBitmapToCache(filePath, it) }
            bitmap
        } catch (e: Exception) {
            null
        }
    }

    /**
     * 计算采样率
     */
    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {
        val (width: Int, height: Int) = options.outWidth to options.outHeight
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight = height / 2
            val halfWidth = width / 2

            while (halfHeight / inSampleSize >= reqHeight && 
                   halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}
