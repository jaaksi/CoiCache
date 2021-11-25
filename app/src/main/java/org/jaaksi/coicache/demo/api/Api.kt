package org.jaaksi.coicache.demo.api

import org.jaaksi.coicache.demo.model.ApiResponse
import org.jaaksi.coicache.demo.model.BannerBean
import retrofit2.http.GET

interface Api {
    @GET("banner/json")
    suspend fun getBanner(): ApiResponse<MutableList<BannerBean>>
}