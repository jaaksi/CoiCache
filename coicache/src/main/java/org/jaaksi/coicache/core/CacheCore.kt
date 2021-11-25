package org.jaaksi.coicache.core

import org.jaaksi.coicache.CoiCache
import org.jaaksi.coicache.model.CacheEntity
import org.jaaksi.coicache.util.Utils
import java.lang.reflect.Type

/**
 *
 * 描述：缓存核心管理类
 *
 *
 * 1.采用LruDiskCache<br></br>
 * 2.对Key进行MD5加密<br></br>
 *
 */
class CacheCore(private val disk: LruDiskCache) {

    /**
     * 读取
     */
    @Synchronized
    fun <T> load(type: Type, key: String): CacheEntity<T>? {
        val cacheKey = encryptKey(key)
        val result: CacheEntity<T>? = disk.load(type, cacheKey)
        if (result != null) {
            if (result.duration == CoiCache.NEVER_EXPIRE
                || result.createTime + result.duration > System.currentTimeMillis()
            ) {
                // 未过期
                return result
            }

            // 如果已过期，则删除
            disk.remove(cacheKey)
        }
        return null
    }

    /**
     * 保存
     */
    @Synchronized
    fun <T> save(key: String, value: T): Boolean {
        return disk.save(encryptKey(key), value)
    }

    /**
     * 是否包含
     */
    @Synchronized
    fun containsKey(key: String): Boolean {
        return disk.containsKey(encryptKey(key))
    }

    /**
     * 删除缓存
     */
    @Synchronized
    fun remove(key: String): Boolean {
        return disk.remove(encryptKey(key))
    }

    /**
     * 清空缓存
     */
    @Synchronized
    fun clear(): Boolean {
        return disk.clear()
    }

    // encryptKey
    private fun encryptKey(key: String): String = Utils.md5(key)
}