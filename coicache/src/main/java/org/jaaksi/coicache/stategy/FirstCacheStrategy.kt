package org.jaaksi.coicache.stategy

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import org.jaaksi.coicache.model.CacheWrapper
import java.lang.reflect.Type

/**
 * Created by jaaksi on 2021/4/29.
 *
 * 先加载缓存，缓存不存在，再请求网络
 */
class FirstCacheStrategy : BaseStrategy() {

    override fun <T> execute(
        cacheKey: String,
        netSource: Flow<CacheWrapper<T?>>,
        type: Type
    ): Flow<CacheWrapper<T?>> {
        // 如果缓存为空，会抛出NoCacheException，且不会发射数据
        return loadCache<T>(cacheKey, type)
            .catch {
                // 这里不再判断是否是NoCacheException，读取缓存失败，就加载网络，保证数据展示
                emitAll(netSource)
            }
    }
}