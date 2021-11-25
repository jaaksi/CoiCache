package org.jaaksi.coicache.model

/**
 * 缓存对象，可区分是否来自缓存
 */
data class CacheWrapper<T>(
    /** 是否来自缓存  */
    val isFromCache: Boolean,
    val data: T
) {
    /** 用来记录，是否缓存数据（无效数据不缓存）  */
    var cacheable = false
}