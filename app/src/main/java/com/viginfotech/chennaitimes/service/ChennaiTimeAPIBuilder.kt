package com.viginfotech.chennaitimes.service

import com.viginfotech.chennaitimes.BuildConfig
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ChennaiTimeAPIBuilder {
    companion object {
        private val CHENNAI_TIMES_BASE_URL =
                if (BuildConfig.DEBUG) BuildConfig.BACKEND_STAGING
                else BuildConfig.BACKEND_PROD


        private val chennaiTimesServiceBuilder = Retrofit.Builder()
                .baseUrl(CHENNAI_TIMES_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        private val retrofitChennaiTimesAPI = chennaiTimesServiceBuilder.build()!!

        fun buildChennaiTimesService() = retrofitChennaiTimesAPI.create(ChennaiTimesService::class.java)!!
    }
}