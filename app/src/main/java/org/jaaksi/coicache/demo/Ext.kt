package org.jaaksi.coicache.demo

import org.jaaksi.coicache.demo.model.ApiResponse
import org.jaaksi.coicache.demo.net.ApiClient

fun <T> Class<T>.create(): T {
    return ApiClient.create(this)
}

fun <T> ApiResponse<T>?.isSuccess(): Boolean {
    return this?.errorCode == 0
}

fun <T> ApiResponse<T>?.hasData(): Boolean {
    return isSuccess() && this?.data != null
}