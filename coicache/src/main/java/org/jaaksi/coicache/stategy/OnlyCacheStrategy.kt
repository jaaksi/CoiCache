package org.jaaksi.coicache.stategy

import kotlinx.coroutines.flow.Flow
import org.jaaksi.coicache.model.CacheWrapper
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29..<br></br>
 *
 * 只加载缓存
 */
class OnlyCacheStrategy : BaseStrategy() {

    override fun <T> execute(
        cacheKey: String,
        netSource: Flow<CacheWrapper<T?>>,
        type: Type
    ): Flow<CacheWrapper<T?>> {
        return loadCache(cacheKey, type)
    }
}