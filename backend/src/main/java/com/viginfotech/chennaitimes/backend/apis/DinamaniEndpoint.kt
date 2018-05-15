package com.viginfotech.chennaitimes.backend.apis


import com.google.api.server.spi.config.Api
import com.google.api.server.spi.config.ApiMethod
import com.google.api.server.spi.config.ApiNamespace
import com.viginfotech.chennaitimes.backend.Constants
import com.viginfotech.chennaitimes.backend.model.Feed
import com.viginfotech.chennaitimes.backend.tamil.Dinamani

import javax.inject.Named

/**
 * Created by anand on 1/21/16.
 */
@Api(name = "chennaiTimesApi", version = "v1",
        namespace = ApiNamespace(ownerDomain = Constants.API_OWNER,
                ownerName = Constants.API_OWNER,
                packagePath = Constants.API_PACKAGE_PATH))
class DinamaniEndpoint {

    @ApiMethod(name = "getDinamaniFeeds", path = "dinamani")
    fun getDinamaniFeeds(@Named("category") category: Int): List<Feed>? {
        return Dinamani.queryDinamaniNews(category)
    }

    @ApiMethod(name = "getDinamaniDetail", path = "dinamani/detail")
    fun getDinamaniDetail(@Named("guid") guid: String): Feed? {
        return Dinamani.getDinamaniDetail(guid)
    }
}
