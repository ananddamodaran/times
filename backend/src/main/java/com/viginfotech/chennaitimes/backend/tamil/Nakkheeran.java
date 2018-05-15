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
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_TAMILNADU;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_WORLD;
import static com.viginfotech.chennaitimes.backend.Constants.SOURCE_NAKKHEERAN;
import static com.viginfotech.chennaitimes.backend.service.OfyService.ofy;
import static com.viginfotech.chennaitimes.backend.utils.UpdateHelper.removeDuplicates;


/**
 * Created by anand on 1/22/16.
 */
public class Nakkheeran {

    public Nakkheeran(){}
    private static String getUri(int category){
        switch (category){
            case INSTANCE.getCATEGORY_HEADLINES():
                return Config.Nakkheeran.Companion.getNAKKHEERAN_HEADLINES();
            case INSTANCE.getCATEGORY_TAMILNADU():
                return Config.Nakkheeran.Companion.getNAKKHEERAN_TAMILNADU();
            case INSTANCE.getCATEGORY_INDIA():
                return Config.Nakkheeran.Companion.getNAKKHEERAN_INDIA();
            case INSTANCE.getCATEGORY_WORLD():
                return Config.Nakkheeran.Companion.getNAKKHEERAN_WORLD();
            case INSTANCE.getCATEGORY_SPORTS():
                return Config.Nakkheeran.Companion.getNAKKHEERAN_SPORTS();
            default:
                return "";
        }
    }
    public static List<Feed> queryNakkheeranNews(int category){
        List<Feed> feedList = null;
        switch (category){
            case INSTANCE.getCATEGORY_HEADLINES():
            case INSTANCE.getCATEGORY_TAMILNADU():
            case INSTANCE.getCATEGORY_INDIA():
            case INSTANCE.getCATEGORY_WORLD():
            case INSTANCE.getCATEGORY_SPORTS():


                    feedList = QueryUtils.queryCategorySortbyPubDate(INSTANCE.getSOURCE_NAKKHEERAN(), category);
                if (feedList.size() == 0) {
                    System.out.println("fetching from net nakkeran headlines");
                   feedList= fetchNakkheeran(category,getUri(category));
                    if (feedList != null) {

                        feedList = removeDuplicates(Constants.INSTANCE.getSOURCE_NAKKHEERAN(), Arrays.asList(category), feedList);
                        if(feedList.size()>0) {
                            ofy().save().entities(feedList).now();
                            feedList = QueryUtils.queryCategorySortbyPubDate(INSTANCE.getSOURCE_NAKKHEERAN(), category);
                        }else{
                            feedList=QueryUtils.queryLatest7Feeds(INSTANCE.getSOURCE_NAKKHEERAN(),category);
                        }
                    }

                }
                return feedList;

            default: return null;

        }

    }



    public static Feed getNakkheeranDetail(String guid){
        Document doc;
        try{
            doc= Jsoup.connect(guid).get();
            Element article=doc.getElementById("divCenter");
            String detailedTitle=null;
            Elements heading = article.getElementsByTag("b");
            if(heading!=null&&heading.size()>0){
               detailedTitle=heading.text().trim();

            }
            Elements spanElments=article.getElementsByTag("span");
            StringBuilder builder=new StringBuilder();
            String imgSrc=null;
            String detailedDescription;
            for(int i=1;i<spanElments.size()-4;i++){
                builder.append(spanElments.get(i).text());
                if(spanElments.get(i).childNodeSize()>1) {
                    Elements image=spanElments.get(i).getElementsByTag("img");
                    if(image.size()>0) {
                        imgSrc = "http://www.nakkheeran.in"+image.get(0).attr("src");
                    }
                }
            }
            detailedDescription=builder.toString();
            Key<Feed> key = Key.create(Feed.class, guid);
            Feed feed = ofy().load().key(key).now();
            if(detailedTitle!=null)feed.setDetailedTitle(detailedTitle);
            if(imgSrc!=null) {
                feed.setImage(imgSrc);
                if(feed.getThumbnail()==null) feed.setThumbnail(imgSrc);
            }
            feed.setDetailNews(detailedDescription);
            ofy().save().entity(feed).now();
            return feed;


        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }

    }

    public static List<Feed> fetchNakkheeran(int category, String uri){

            System.out.println("Fetching from net " + category);
        return  NakkheeranParser.parseFeed(UriFetch.
                    fetchData(uri), category);


    }
}
