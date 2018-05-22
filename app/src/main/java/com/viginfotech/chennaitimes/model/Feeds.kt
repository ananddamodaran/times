package com.viginfotech.chennaitimes.model

import com.google.gson.annotations.SerializedName


data class Feeds(
        @SerializedName("items")
        val  items : List<Feed>)