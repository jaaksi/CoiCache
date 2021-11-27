package org.jaaksi.coicache.demo.model

data class ApiResponse<T>(
    val errorCode: Int,
    val errorMsg: String? = null,
    val data: T? = null
)

