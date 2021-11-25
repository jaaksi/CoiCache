package org.jaaksi.coicache.stategy

import kotlinx.coroutines.flow.Flow
import org.jaaksi.coicache.model.CacheWrapper
import java.lang.reflect.Type

/**
 * 缓存策略的接口
 */
interface IStrategy {
    /**
     * 根据缓存策略处理，返回对应的Flow
     *
     * @param cacheKey  缓存的key
     * @param netSource 网络请求对象
     */
    fun <T> execute(
        cacheKey: String,
        netSource: Flow<CacheWrapper<T?>>,
        type: Type
    ): Flow<CacheWrapper<T?>>
}