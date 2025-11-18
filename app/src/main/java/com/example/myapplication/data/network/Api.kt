package com.example.myapplication.data.network

import androidx.annotation.Keep
import com.example.myapplication.data.network.request.SearchRequest
import com.example.myapplication.data.network.responses.BaseFranchiseResponse
import com.example.myapplication.data.network.responses.FullReleaseResponse
import com.example.myapplication.data.network.responses.ReleasesElementResponse
import com.example.myapplication.data.network.responses.base.BaseResponseWrapper
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

@Keep
interface Api {
    @GET("anime/catalog/releases")
    suspend fun getReleases(@Query("page") page: Int): BaseResponseWrapper<List<ReleasesElementResponse>>

    @GET("anime/releases/{id}")
    suspend fun getRelease(@Path("id") id: String): FullReleaseResponse

    @GET("anime/franchises/release/{id}?include=franchise_releases")
    suspend fun getFranchises(@Path("id")id: String): List<BaseFranchiseResponse>

    @GET("anime/franchises/release/{id}?include=franchise_releases")
    suspend fun getReleaseByGenres(@Path("id")id: String): List<BaseFranchiseResponse>

    //https://aniliberty.top/api/v1/app/search/releases?query=%D0%BC%D0%B0%D1%81%D1%82%D0%B5%D1%80
    @GET("app/search/release/")
    suspend fun getSearchReleases(@Query("query") query: String): List<BaseFranchiseResponse>
    @POST("anime/catalog/releases")
    suspend fun getSearchWithFilter(@Body request: SearchRequest): BaseResponseWrapper<List<ReleasesElementResponse>>

    /*
    {
  "page": 1,
  "limit": 15,
  "f": {
    "genres": [
      15,
      20
    ],
    "types": [
      "TV",
      "WEB"
    ],
    "seasons": [
      "winter",
      "autumn"
    ],
    "years": {
      "from_year": 2016,
      "to_year": 2020
    },
    "search": "Мастера меча",
    "sorting": "FRESH_AT_DESC",
    "age_ratings": [
      "R6_PLUS",
      "R12_PLUS"
    ],
    "publish_statuses": [
      "IS_ONGOING"
    ],
    "production_statuses": [
      "IS_IN_PRODUCTION"
    ]
  },
  "include": "id,type.description",
  "exclude": "season.value,description"
}*/
}