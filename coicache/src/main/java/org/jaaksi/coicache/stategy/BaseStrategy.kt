package org.jaaksi.coicache.stategy

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.jaaksi.coicache.CoiCache
import org.jaaksi.coicache.model.CacheWrapper
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29.
 *
 * 缓存策略的base抽象类，提供 load data from cache and remote
 */
abstract class BaseStrategy : IStrategy {

    fun <T> loadCache(
        cacheKey: String, type: Type
    ): Flow<CacheWrapper<T?>> {
        return CoiCache.rxGetInner<T?>(cacheKey, type).map {
            CacheWrapper(true, it)
        }
    }
}