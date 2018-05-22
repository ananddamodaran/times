package com.viginfotech.chennaitimes.model

data class Feed(
        val title:String,
        val detailedTitle: String? = null,
        val summary:String,
        val detailNews: String? = null,
        val pubDate:Long,
        val guid:String,
        val thumbnail:String,
        val image: String? = null,
        val categoryId:Int,
        val sourceId:Int

)