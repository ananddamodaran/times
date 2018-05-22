package com.viginfotech.chennaitimes.util;


import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.viginfotech.chennaiTimesApi.ChennaiTimesApi;
import com.viginfotech.chennaitimes.Config;

import java.io.IOException;

/**
 * Created by anand on 12/25/15.
 */
public final class CloudEndpointBuilderHelper {
    private CloudEndpointBuilderHelper() {
    }

    /**
     * *
     *
     * @return chennaiTimes endpoints to the GAE backend.
     */
    /*public static ChennaiTimesApi getEndpoints() {

        // Create API handler

        ChennaiTimesApi.Builder builder = new ChennaiTimesApi.Builder(
                AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), getRequestInitializer())
                .setRootUrl(Config.ROOT_URL)
                .setGoogleClientRequestInitializer(
                        new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(
                                    final AbstractGoogleClientRequest<?>
                                            abstractGoogleClientRequest)
                                    throws IOException {
                                abstractGoogleClientRequest
                                        .setDisableGZipContent(true);
                            }
                        }
                );

        return builder.build();
    }*/

    /**
     * Returns appropriate HttpRequestInitializer depending whether the
     * application is configured to require users to be signed in or not.
     *
     * @return an appropriate HttpRequestInitializer.
     */
    /*static HttpRequestInitializer getRequestInitializer() {
        return new HttpRequestInitializer() {
            @Override
            public void initialize(final HttpRequest arg0) {
            }
        };
    }*/


}
