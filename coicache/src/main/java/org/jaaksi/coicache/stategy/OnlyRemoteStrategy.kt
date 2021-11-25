package org.jaaksi.coicache.stategy

import kotlinx.coroutines.flow.Flow
import org.jaaksi.coicache.model.CacheWrapper
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29..<br></br>
 *
 * 只请求网络，但数据依然会被缓存
 */
open class OnlyRemoteStrategy : IStrategy {
    override fun <T> execute(
        cacheKey: String,
        netSource: Flow<CacheWrapper<T?>>,
        type: Type
    ): Flow<CacheWrapper<T?>> {
        return netSource
    }
}