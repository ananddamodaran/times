package com.viginfotech.chennaitimes.backend.tamil;



import com.googlecode.objectify.Key;
import com.viginfotech.chennaitimes.backend.Config;
import com.viginfotech.chennaitimes.backend.Constants;
import com.viginfotech.chennaitimes.backend.model.Feed;
import com.viginfotech.chennaitimes.backend.utils.QueryUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.Arrays;
import java.util.List;

import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_HEADLINES;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_INDIA;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_SPORTS;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_WORLD;
import static com.viginfotech.chennaitimes.backend.Constants.SOURCE_BBCTAMIL;
import static com.viginfotech.chennaitimes.backend.service.OfyService.ofy;
import static com.viginfotech.chennaitimes.backend.utils.UpdateHelper.removeDuplicates;


/**
 * Created by anand on 1/20/16.
 */
public class BBCTamil {
    public BBCTamil() {
    }
    private static String getUri(int category){
        switch (category){
            case INSTANCE.getCATEGORY_HEADLINES():
                return Config.BBCTamil.Companion.getBBCTAMIL_HEADLINES();
            case INSTANCE.getCATEGORY_INDIA():
                return Config.BBCTamil.Companion.getBBCTAMIL_INDIA();
            case INSTANCE.getCATEGORY_WORLD():
                return Config.BBCTamil.Companion.getBBCTAMIL_WORLD();
            case INSTANCE.getCATEGORY_SPORTS():
                return Config.BBCTamil.Companion.getBBCTAMIL_SPORTS();
            default:
                return "";
        }
    }

    public static List<Feed> queryBBCNews(int category) {
        List<Feed> feedList = null;
        switch (category) {
            case INSTANCE.getCATEGORY_HEADLINES():
            case INSTANCE.getCATEGORY_INDIA():
            case INSTANCE.getCATEGORY_WORLD():
            case INSTANCE.getCATEGORY_SPORTS():

                feedList = QueryUtils.queryCategorySortbyPubDate(INSTANCE.getSOURCE_BBCTAMIL(), category);
                if (feedList.size() == 0) {
                    System.out.println("Fetching from net " + category+" BBC");
                    feedList = BBCTamilParser.parseFeed(UriFetch.
                            fetchData(getUri(category)), category);
                    if (feedList != null) {
                        feedList = removeDuplicates(Constants.INSTANCE.getSOURCE_BBCTAMIL(), Arrays.asList(category), feedList);
                        System.out.println("filtered size oneindia" + feedList.size());
                        if(feedList.size()>0) {
                            ofy().save().entities(feedList).now();
                            feedList = QueryUtils.queryCategorySortbyPubDate(INSTANCE.getSOURCE_BBCTAMIL(), category);
                        }else{
                            feedList=QueryUtils.queryLatest7Feeds(INSTANCE.getSOURCE_BBCTAMIL(),category);
                        }

                    }
                }
                return feedList;


            default:
                return null;
        }


    }
    public static List<Feed> fetchBBCNews(int category) {
        List<Feed> feedList = null;
        switch (category) {
            case INSTANCE.getCATEGORY_HEADLINES():
                    System.out.println("Fetching from net " + INSTANCE.getCATEGORY_HEADLINES());
                    feedList = BBCTamilParser.parseFeed(UriFetch.
                            fetchData(Config.BBCTamil.Companion.getBBCTAMIL_HEADLINES()), INSTANCE.getCATEGORY_HEADLINES());
                return feedList;

            case INSTANCE.getCATEGORY_INDIA():
                    System.out.println("Fetching from net " + INSTANCE.getCATEGORY_INDIA());
                    feedList = BBCTamilParser.parseFeed(UriFetch.fetchData(Config.BBCTamil.Companion.getBBCTAMIL_INDIA()), category);
                return feedList;

            case INSTANCE.getCATEGORY_WORLD():
                    System.out.println("Fetching from net " + INSTANCE.getCATEGORY_WORLD());
                    feedList = BBCTamilParser.parseFeed(UriFetch.fetchData(Config.BBCTamil.Companion.getBBCTAMIL_WORLD()), category);
                return feedList;

            case INSTANCE.getCATEGORY_SPORTS():
                    System.out.println("Fetching from net " + INSTANCE.getCATEGORY_SPORTS());
                    feedList = BBCTamilParser.parseFeed(UriFetch.fetchData(Config.BBCTamil.Companion.getBBCTAMIL_SPORTS()),
                            category);
                return feedList;


            default:
                return null;
        }


    }

    public static Feed getDetail(String guid) {
        try {
            Document doc;
            doc = Jsoup.connect(guid).get();
            Elements story_board = doc.getElementsByClass("story-body");
            String detailDescription = null;
            String detailedTitle=null;
            String imgSrc = null;
            if(story_board!=null&&story_board.size()>0){
                detailedTitle=story_board.get(0).getElementsByClass("story-body__h1").get(0).text().trim();
            }
            Elements story_body = doc.getElementsByClass("map-body");
            if (story_body != null && story_body.size() > 0) {
                detailDescription = getStory(story_body);

            } else {
                story_body = doc.getElementsByClass("story-body__inner");
                if (story_body != null && story_body.size() > 0) {
                    Elements img = story_body.get(0).getElementsByTag("img");

                    if (img != null && img.size() > 0) {
                        imgSrc = img.get(0).attr("src");
                    }


                    detailDescription = getStory(story_body);
                }
            }
            Key<Feed> key = Key.create(Feed.class, guid);
            Feed feed = ofy().load().key(key).now();
            if(detailedTitle!=null)feed.setDetailedTitle(detailedTitle);
            if(imgSrc!=null) {
                feed.setImage(imgSrc);
                if(feed.getThumbnail()==null) feed.setThumbnail(imgSrc);
            }
            feed.setDetailNews(detailDescription);
            ofy().save().entity(feed).now();
            return feed;
        } catch (Exception ex) {

            ex.printStackTrace();
            return null;
        }

    }

    private static String getStory(Elements body) {
        if (body.size() > 0) {
            Elements detailText = body.get(0).getElementsByTag("p");
            if (detailText != null) {
                StringBuilder builder = new StringBuilder();
                for (Element p : detailText) {

                    builder.append(p.text());
                    builder.append("\n");
                }
                return builder.toString();
            }
        }
        return null;

    }
}




