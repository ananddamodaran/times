package com.viginfotech.chennaitimes.backend.tamil;


import com.googlecode.objectify.Key;
import com.viginfotech.chennaitimes.backend.Config;
import com.viginfotech.chennaitimes.backend.Constants;
import com.viginfotech.chennaitimes.backend.model.Feed;
import com.viginfotech.chennaitimes.backend.utils.QueryUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;

import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_BUSINESS;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_INDIA;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_SPORTS;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_TAMILNADU;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_WORLD;
import static com.viginfotech.chennaitimes.backend.Constants.SOURCE_DINAKARAN;
import static com.viginfotech.chennaitimes.backend.service.OfyService.ofy;
import static com.viginfotech.chennaitimes.backend.utils.UpdateHelper.removeDuplicates;


/**
 * Created by anand on 12/6/15.
 */
public class Dinakaran {

    public Dinakaran() {
    }

    private static String getUri(int category) {
        switch (category) {
            case INSTANCE.getCATEGORY_TAMILNADU():
                return Config.Dinakaran.Companion.getDINAKARAN_TAMILNADU_URI();
            case INSTANCE.getCATEGORY_INDIA():
                return Config.Dinakaran.Companion.getDINAKARAN_INDIA_URI();
            case INSTANCE.getCATEGORY_WORLD():
                return Config.Dinakaran.Companion.getDINAKARAN_WORLD_URI();
            case INSTANCE.getCATEGORY_BUSINESS():
                return Config.Dinakaran.Companion.getDINAKARAN_BUSINESS_URI();
            case INSTANCE.getCATEGORY_SPORTS():
                return Config.Dinakaran.Companion.getDINAKARAN_SPORTS_URI();
            default:
                return "";
        }
    }

    public static List<Feed> queryDinakaranNews(int category) {
        List<Feed> feedList = null;
        switch (category) {
            case INSTANCE.getCATEGORY_TAMILNADU():
            case INSTANCE.getCATEGORY_INDIA():
            case INSTANCE.getCATEGORY_WORLD():
            case INSTANCE.getCATEGORY_BUSINESS():
            case INSTANCE.getCATEGORY_SPORTS():

                feedList = QueryUtils.queryCategorySortbyPubDate(INSTANCE.getSOURCE_DINAKARAN(), category);
                if (feedList.size() == 0) {
                    System.out.println("Fetching from net " + category);
                    feedList = UriFetch.fetchDinakaranData(category, getUri(category));

                    if (feedList != null) {

                        feedList = removeDuplicates(Constants.INSTANCE.getSOURCE_DINAKARAN(), Arrays.asList(category), feedList);
                        System.out.println("filtered size dinakaran" + feedList.size());
                        if (feedList.size() > 0) {
                            ofy().save().entities(feedList).now();
                            feedList = QueryUtils.queryCategorySortbyPubDate(INSTANCE.getSOURCE_DINAKARAN(), category);
                        } else {
                            feedList = QueryUtils.queryLatest7Feeds(INSTANCE.getSOURCE_DINAKARAN(), category);

                        }

                    }

                }

                return feedList;
            default:
                return null;
        }
    }


    public static Feed getDinakaranDetail(String guid) {

        try {
            Document doc;
            doc = Jsoup.connect(guid).get();
            Elements main_news = doc.getElementsByClass("main-news");
            String detailedTitle = null;
            String imgSrc = null;
            String detailDescription = null;
            if (main_news != null && main_news.size() > 0) {
                detailedTitle = main_news.get(0).getElementsByTag("h1").text();
                imgSrc = main_news.get(0).getElementsByTag("img").attr("src");
                detailDescription = main_news.get(0).getElementsByTag("p").text();


            }
            Elements leftcolumn = doc.getElementsByClass("leftcolumn");
            if (leftcolumn != null && leftcolumn.size() > 0) {
                detailedTitle = leftcolumn.get(0).getElementsByTag("h1").text();
                imgSrc = leftcolumn.get(0).getElementsByTag("img").get(1).attr("src");
                detailDescription = leftcolumn.get(0).getElementsByTag("p").text();


            }
            Key<Feed> key = Key.create(Feed.class, guid);
            Feed feed = ofy().load().key(key).now();
            if (detailedTitle != null) feed.setDetailedTitle(detailedTitle);
            if (imgSrc != null) {
                feed.setImage(imgSrc);
                if (feed.getThumbnail() == null) feed.setThumbnail(imgSrc);

            }
            feed.setDetailNews(detailDescription);
            ofy().save().entity(feed).now();

            return feed;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

}
