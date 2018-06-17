package com.viginfotech.chennaitimes.service

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChennaiTimeAPIBuilder {
     companion object {
        const val CHENNAI_TIMES_BASE_URL="https://chennaitimes-prod.appspot.com/"
        val chennaiTimesServiceBuilder = Retrofit.Builder()
                .baseUrl(CHENNAI_TIMES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        val retrofitChennaiTimesAPI= chennaiTimesServiceBuilder.build()

        @JvmStatic
        fun buildChennaiTimesService()=retrofitChennaiTimesAPI.create(ChennaiTimesService::class.java)
    }
}