package org.jaaksi.coicache

import android.content.Context
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import org.jaaksi.coicache.converter.GsonCacheConverter
import org.jaaksi.coicache.core.CacheCore
import org.jaaksi.coicache.core.LruDiskCache
import org.jaaksi.coicache.exception.NoCacheException
import org.jaaksi.coicache.model.CacheEntity
import org.jaaksi.coicache.type.CacheType
import java.io.File
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29.
 * 缓存类，支持普通操作及rx操作<br>
 * 支持设置缓存磁盘大小、缓存key、缓存时间、缓存存储的转换器、缓存目录、缓存Version<br>
 */
object CoiCache {

    const val TAG = "CoiCache"
    // 永久缓存虽然不会过期，但是由于最大缓存size的限制，可能会由于lru被移除，所以不推荐使用该库来作为重要信息存储
    const val NEVER_EXPIRE = -1L //缓存过期时间，默认永久缓存
    private const val MAX_CACHE_SIZE = 50 * 1024 * 1024L // 50MB

    //缓存的核心管理类
    private lateinit var cacheCore: CacheCore

    /**
     * 初始化，默认目录 filesDir/coicache，不依赖sd卡读写权限
     */
    fun initialize(context: Context) {
        initialize(File(context.filesDir, "coicache"))
    }

    /**
     * 初始化
     *
     * @param cacheDir       缓存目录
     * @param cacheConverter 缓存Converter
     * @param cacheVersion   缓存版本
     * @param maxCacheSize   缓存最大size
     */
    fun initialize(
        cacheDir: File,
        cacheConverter: GsonCacheConverter = GsonCacheConverter(Gson()),
        cacheVersion: Int = 1,
        maxCacheSize: Long = MAX_CACHE_SIZE

    ) {
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        cacheCore =
            CacheCore(LruDiskCache(cacheConverter, cacheDir, cacheVersion, maxCacheSize))
    }

    fun containsKey(key: String): Boolean {
        return cacheCore.containsKey(key)
    }

    /**
     * 获取缓存
     */
    fun <T> get(key: String, clazz: Class<T>): T? {
        return get(key, clazz as Type)
    }

    /**
     * 获取缓存
     */
    fun <T> get(key: String, cacheType: CacheType<T>): T? {
        return get(key, cacheType.type)
    }

    /**
     * 获取缓存
     */
    fun <T> get(key: String, type: Type): T? {
        return cacheCore.load<T>(type, key)?.data
    }

    // 内部使用
    internal fun <T> rxGetInner(key: String, type: Type): Flow<T> {
        return flow<T> {
            val value = get<T>(key, type)
            if (value != null) {
                emit(value)
            } else {
                throw NoCacheException()
            }
        }.flowOn(Dispatchers.IO)
    }

    /**
     * 保存
     *
     * @param duration 毫秒ms
     */
    fun <T> put(key: String, value: T, duration: Long = NEVER_EXPIRE): Boolean {
        check(duration == NEVER_EXPIRE || duration > 0)
        return cacheCore.save(key, CacheEntity(value, duration))
    }

    /**
     * 删除缓存
     */
    fun remove(key: String): Boolean {
        return cacheCore.remove(key)
    }

    /**
     * 清空缓存
     */
    fun clear(): Boolean {
        return cacheCore.clear()
    }
}