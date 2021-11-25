package org.jaaksi.coicache.model

import androidx.annotation.Keep

/**
 * 实际缓存的类，将传入的data包裹在此类下，用以设置缓存时长等
 */
@Keep
data class CacheEntity<T>(
    val data: T,
    /** 缓存有效的时间，以ms为单位  */
    val duration: Long
) {
    /** 缓存创建的时间  */
    val createTime: Long = System.currentTimeMillis()
}