package org.jaaksi.coicache

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jaaksi.coicache.model.CacheWrapper
import org.jaaksi.coicache.stategy.CacheStrategy
import org.jaaksi.coicache.stategy.IStrategy
import org.jaaksi.coicache.stategy.NoStrategy
import org.jaaksi.coicache.type.CacheType
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29.
 * 用于发送网络请求
 */
class RequestApi<T> constructor(private val api: Flow<T?>) {
    private val rxCache = CoiCache
    private var cacheTime: Long = CoiCache.NEVER_EXPIRE
    private var cacheStrategy: IStrategy = CacheStrategy.CACHE_AND_REMOTE

    private lateinit var cacheKey: String
    private var cacheableChecker: ((data: T) -> Boolean)? = null

    /**
     * 设置缓存key
     */
    fun cacheKey(cacheKey: String): RequestApi<T> {
        this.cacheKey = cacheKey
        return this
    }

    /**
     *
     * 校验response的有效性，数据有效才进行缓存
     */
    fun cacheable(block: (data: T) -> Boolean): RequestApi<T> {
        this.cacheableChecker = block
        return this
    }


    /**
     * 设置缓存时间
     */
    fun cacheTime(cacheTime: Long): RequestApi<T> {
        check(cacheTime == CoiCache.NEVER_EXPIRE || cacheTime > 0)
        this.cacheTime = cacheTime
        return this
    }

    /**
     * 设置缓存策略
     */
    fun cacheStrategy(iStrategy: IStrategy): RequestApi<T> {
        cacheStrategy = iStrategy
        return this
    }

//    fun buildCache(type: Type): Flow<T?> {
//        return doBuildCache(type)
//    }

    /**
     * buildCache
     */
    fun buildCache(cacheType: CacheType<T>): Flow<T?> {
        return doBuildCache(cacheType.type)
    }

    private fun doBuildCache(type: Type): Flow<T?> {
        return doBuildCacheWithCacheResult(type).map {
            it.data
        }
    }

//    fun buildCacheWithCacheResult(type: Type): Flow<CacheResult<T?>> {
//        return doBuildCacheWithCacheResult(type)
//    }

    /**
     * buildCacheWithCacheResult
     */
    fun buildCacheWithCacheResult(cacheType: CacheType<T>): Flow<CacheWrapper<T?>> {
        return doBuildCacheWithCacheResult(cacheType.type)
    }

    // 根据不同的策略处理
    private fun doBuildCacheWithCacheResult(type: Type): Flow<CacheWrapper<T?>> {
        return api.map {
            val result = CacheWrapper(false, it)
            if (cacheStrategy !is NoStrategy) { // 如果缓存策略是不缓存
                //  这里根据业务情况处理cacheable == null的情况，提供默认的判断，如result.errno==0&&result.data!=null
                if (it != null && cacheableChecker?.invoke(it) != false) {
                    result.cacheable = true
                    writeCache(it)
                }
            }
            result
        }.let {
            cacheStrategy.execute(cacheKey, it, type)
        }
    }

    private fun writeCache(t: T) {
        GlobalScope.launch {
            rxCache.put(cacheKey, t)
        }
    }
}