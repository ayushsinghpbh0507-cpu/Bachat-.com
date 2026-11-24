package com.bachatcom.app

import retrofit2.http.GET
import retrofit2.http.Query

data class StoreResult(
    val name: String,
    val price: Int?,
    val rating: String?,
    val product_url: String,
    val image: String?,
    val live_data: Boolean
)
data class ProductResponse(
    val product_name: String,
    val flipkart_search: String,
    val myntra_search: String,
    val ajio_search: String,
    val other_search: String,
    val image: String?,
    val price: Int?,
    val rating: String?,
    val stores: List<StoreResult>,
    val message: String
)

interface ApiService {

    @GET("search")
    suspend fun searchProduct(
        @Query("q") productName: String
    ): ProductResponse
}