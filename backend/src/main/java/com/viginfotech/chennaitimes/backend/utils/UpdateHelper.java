package com.viginfotech.chennaitimes.backend.utils;



import com.googlecode.objectify.Key;
import com.viginfotech.chennaitimes.backend.Config;
import com.viginfotech.chennaitimes.backend.model.Feed;
import com.viginfotech.chennaitimes.backend.tamil.BBCTamil;
import com.viginfotech.chennaitimes.backend.tamil.Dinamalar;
import com.viginfotech.chennaitimes.backend.tamil.Dinamani;
import com.viginfotech.chennaitimes.backend.tamil.Nakkheeran;
import com.viginfotech.chennaitimes.backend.tamil.UriFetch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_BUSINESS;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_CINEMA;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_HEADLINES;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_INDIA;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_SPORTS;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_TAMILNADU;
import static com.viginfotech.chennaitimes.backend.Constants.CATEGORY_WORLD;
import static com.viginfotech.chennaitimes.backend.Constants.SOURCE_DINAMANI;
import static com.viginfotech.chennaitimes.backend.service.OfyService.ofy;


/**
 * Created by anand on 1/26/16.
 */
public class UpdateHelper {
    public static List<Feed> updateNakkheeran() {
        System.out.println("nakkheeran");
        List<Feed> headLinesFeed = Nakkheeran.fetchNakkheeran(INSTANCE.getCATEGORY_HEADLINES(), Config.Nakkheeran.Companion.getNAKKHEERAN_HEADLINES());
        List<Feed> tamilNaduFeed = Nakkheeran.fetchNakkheeran(INSTANCE.getCATEGORY_TAMILNADU(), Config.Nakkheeran.Companion.getNAKKHEERAN_TAMILNADU());
        List<Feed> indiaFeed = Nakkheeran.fetchNakkheeran(INSTANCE.getCATEGORY_INDIA(), Config.Nakkheeran.Companion.getNAKKHEERAN_INDIA());
        List<Feed> worldFeed = Nakkheeran.fetchNakkheeran(INSTANCE.getCATEGORY_WORLD(), Config.Nakkheeran.Companion.getNAKKHEERAN_WORLD());
        List<Feed> sportFeed = Nakkheeran.fetchNakkheeran(INSTANCE.getCATEGORY_SPORTS(), Config.Nakkheeran.Companion.getNAKKHEERAN_SPORTS());

        List<Feed> allFeeds = new ArrayList<Feed>();
        if (headLinesFeed != null) {

            allFeeds.addAll(headLinesFeed);
        }
        if (tamilNaduFeed != null) {

            allFeeds.addAll(tamilNaduFeed);

        }
        if (indiaFeed != null) {

            allFeeds.addAll(indiaFeed);

        }
        if (worldFeed != null) {

            allFeeds.addAll(worldFeed);
        }
        if (sportFeed != null) {

            allFeeds.addAll(sportFeed);
        }
        return allFeeds;

    }

    public static List<Feed> updateOneIndia() {
        System.out.println("OneIndia");
        List<Feed> feedListtamil = UriFetch.fetchOneIndiaData(INSTANCE.getCATEGORY_TAMILNADU(), Config.OneIndia.Companion.getONEINDIA_TAMILNADU());
        List<Feed> feedListIndia = UriFetch.fetchOneIndiaData(INSTANCE.getCATEGORY_INDIA(), Config.OneIndia.Companion.getONEINDIA_INDIA());
        List<Feed> feedListWorld = UriFetch.fetchOneIndiaData(INSTANCE.getCATEGORY_WORLD(), Config.OneIndia.Companion.getONEINDIA_WORLD());
        List<Feed> feedListBusiness = UriFetch.fetchOneIndiaData(INSTANCE.getCATEGORY_BUSINESS(), Config.OneIndia.Companion.getONEINDIA_BUSINESS());

        List<Feed> allFeeds = new ArrayList<Feed>();
        if (feedListtamil != null) {
            System.out.println("tamil " + feedListtamil.size());


            allFeeds.addAll(feedListtamil);
        }
        if (feedListIndia != null) {
            System.out.println("india " + feedListIndia.size());


            allFeeds.addAll(feedListIndia);
        }
        if (feedListWorld != null) {
            System.out.println("world " + feedListWorld.size());


            allFeeds.addAll(feedListWorld);
        }
        if (feedListBusiness != null) {
            System.out.println("business " + feedListBusiness.size());


            allFeeds.addAll(feedListBusiness);
        }

        System.out.println("oneIndia " + allFeeds.size());
        return allFeeds;

    }

    public static List<Feed> updateDinakaran() {
        System.out.println("dinakaran");
        List<Feed> feedListtamil = UriFetch.fetchDinakaranData(INSTANCE.getCATEGORY_TAMILNADU(), Config.Dinakaran.Companion.getDINAKARAN_TAMILNADU_URI());
        List<Feed> feedListIndia = UriFetch.fetchDinakaranData(INSTANCE.getCATEGORY_INDIA(), Config.Dinakaran.Companion.getDINAKARAN_INDIA_URI());
        List<Feed> feedListWorld = UriFetch.fetchDinakaranData(INSTANCE.getCATEGORY_WORLD(), Config.Dinakaran.Companion.getDINAKARAN_WORLD_URI());
        List<Feed> feedListBusiness = UriFetch.fetchDinakaranData(INSTANCE.getCATEGORY_BUSINESS(), Config.Dinakaran.Companion.getDINAKARAN_BUSINESS_URI());
        List<Feed> feedListSports = UriFetch.fetchDinakaranData(INSTANCE.getCATEGORY_SPORTS(), Config.Dinakaran.Companion.getDINAKARAN_SPORTS_URI());

        List<Feed> allFeeds = new ArrayList<Feed>();
        if (feedListtamil != null) {
            System.out.println("tamilnadu " + feedListtamil.size());

            allFeeds.addAll(feedListtamil);

        }
        if (feedListIndia != null) {
            System.out.println("india " + feedListIndia.size());

            allFeeds.addAll(feedListIndia);
        }
        if (feedListWorld != null) {
            System.out.println("world " + feedListWorld.size());

            allFeeds.addAll(feedListWorld);
        }
        if (feedListBusiness != null) {
            System.out.println("business " + feedListtamil.size());

            allFeeds.addAll(feedListBusiness);
        }
        if (feedListSports != null) {
            System.out.println("sports " + feedListtamil.size());

            allFeeds.addAll(feedListSports);
        }
        return allFeeds;




    }

    public static List<Feed> updateBBCTamil() {
        System.out.println("bbctamil");
        List<Feed> indiaFeed = BBCTamil.fetchBBCNews(INSTANCE.getCATEGORY_INDIA());
        List<Feed> worldFeed = BBCTamil.fetchBBCNews(INSTANCE.getCATEGORY_WORLD());

        List<Feed> allFeeds = new ArrayList<Feed>();

        if (indiaFeed != null) {
            System.out.println("india " + indiaFeed.size());

            allFeeds.addAll(indiaFeed);
        }
        if (worldFeed != null) {
            System.out.println("world " + worldFeed.size());


            allFeeds.addAll(worldFeed);
        }

        System.out.println("bbcTamil " + allFeeds.size());
        return allFeeds;


    }

    private static Feed addNewFeed(int source, int category) {
        Feed feed = new Feed();
        feed.setTitle("NewFeed" + Math.random());
        feed.setSummary("summary" + Math.random());
        feed.setSourceId(INSTANCE.getSOURCE_DINAMANI());
        feed.setCategoryId(0);
        feed.setGuid("http://www.google.com" + Math.random());
        feed.setPubDate(System.currentTimeMillis());
        ofy().save().entity(feed).now();
        return feed;
    }

    public static List<Feed> updateDinamani() {
        System.out.println("dinamani");
        List<Feed> headLinesFeed = Dinamani.fetchDinamaniNews(INSTANCE.getCATEGORY_HEADLINES());
        List<Feed> tamilNaduFeed = Dinamani.fetchDinamaniNews(INSTANCE.getCATEGORY_TAMILNADU());
        List<Feed> indiaFeed = Dinamani.fetchDinamaniNews(INSTANCE.getCATEGORY_INDIA());
        List<Feed> worldFeed = Dinamani.fetchDinamaniNews(INSTANCE.getCATEGORY_WORLD());
        List<Feed> businessFeed = Dinamani.fetchDinamaniNews(INSTANCE.getCATEGORY_BUSINESS());
        List<Feed> cinemaFeed = Dinamani.fetchDinamaniNews(INSTANCE.getCATEGORY_CINEMA());

        List<Feed> allFeeds = new ArrayList<Feed>();
        if (headLinesFeed != null) {


            allFeeds.addAll(headLinesFeed);
        }
        if (tamilNaduFeed != null) {
            System.out.println("tamilnadu " + tamilNaduFeed.size());

            allFeeds.addAll(tamilNaduFeed);
        }
        if (indiaFeed != null) {
            System.out.println("india " + indiaFeed.size());


            allFeeds.addAll(indiaFeed);
        }
        if (worldFeed != null) {
            System.out.println("world " + worldFeed.size());

            allFeeds.addAll(worldFeed);
        }
        if (businessFeed != null) {
            System.out.println("business " + businessFeed.size());

            allFeeds.addAll(businessFeed);
        }
        if (cinemaFeed != null) {
            System.out.println("cinema " + cinemaFeed.size());

            allFeeds.addAll(cinemaFeed);
        }
        System.out.println("dinamani " + allFeeds.size());
        return allFeeds;
    }


    public static List<Feed> updateDinamalar() {
        System.out.println("dinamalar");
        List<Feed> headLinesFeed = Dinamalar.fetchDinamalarNews(INSTANCE.getCATEGORY_HEADLINES());
        List<Feed> tamilNaduFeed = Dinamalar.fetchDinamalarNews(INSTANCE.getCATEGORY_TAMILNADU());
        List<Feed> worldFeed = Dinamalar.fetchDinamalarNews(INSTANCE.getCATEGORY_WORLD());
        List<Feed> businessFeed = Dinamalar.fetchDinamalarNews(INSTANCE.getCATEGORY_BUSINESS());
        List<Feed> cinemaFeed = Dinamalar.fetchDinamalarNews(INSTANCE.getCATEGORY_CINEMA());

        List<Feed> allFeeds = new ArrayList<Feed>();
        if (headLinesFeed != null) {
            System.out.println("headlines " + headLinesFeed.size());

            allFeeds.addAll(headLinesFeed);

        }
        if (tamilNaduFeed != null) {
            System.out.println("tamilnadu " + tamilNaduFeed.size());

            allFeeds.addAll(tamilNaduFeed);
        }
        if (worldFeed != null) {
            System.out.println("worldFeed " + worldFeed.size());

            allFeeds.addAll(worldFeed);
        }
        if (businessFeed != null) {
            System.out.println("business " + businessFeed.size());

            allFeeds.addAll(businessFeed);
        }
        if (cinemaFeed != null) {
            System.out.println("cinema " + cinemaFeed.size());

            allFeeds.addAll(cinemaFeed);
        }


        System.out.println("dinamalar " + allFeeds.size());

        return allFeeds;


    }


    public static List<Feed> removeDuplicates(int source, List<Integer> categoryList, List<Feed> freshFeeds) {
        List<Feed> allfeeds=new ArrayList<>();
        List<Feed> reverse=new ArrayList<>();

        System.out.println("before filter"+source+ " "+freshFeeds.size());
        System.out.println();
        System.out.println(freshFeeds);
        Collections.sort(freshFeeds,Collections.reverseOrder(new Comparator<Feed>() {
            @Override
            public int compare(Feed lhs, Feed rhs) {
                return lhs.getPubDate().compareTo(rhs.getPubDate());
            }
        }));
        System.out.println();
        System.out.println(freshFeeds);
        for (int category : categoryList) {
            List<Feed> thisCategoryFeeds = predicate.getTlistforThisCatagory(freshFeeds, category);
            Collections.sort(thisCategoryFeeds,Collections.reverseOrder(new Comparator<Feed>() {
                @Override
                public int compare(Feed lhs, Feed rhs) {
                    return lhs.getPubDate().compareTo(rhs.getPubDate());
                }
            }));
            for (Feed f:thisCategoryFeeds){


                Key<Feed> key = Key.create(Feed.class, f.getGuid());
                Feed feed = ofy().load().key(key).now();
                if(feed!=null){
                    System.out.println("exists"+f);
                    break;

                }else{
                    System.out.println("not exists so add"+f);
                    allfeeds.add(f);
                }
            }

        }
        return allfeeds;
    }
    static FPredicate<Feed> predicate = new FPredicate<Feed>() {
        @Override
        public boolean test(Feed feed,Feed lastFeedInDB) {
            return feed.getSourceId()==lastFeedInDB.getSourceId()&&
                    feed.getCategoryId()==lastFeedInDB.getCategoryId()&&
                    feed.getPubDate() > lastFeedInDB.getPubDate()&&
                    !feed.getGuid().equals(lastFeedInDB.getGuid());
        }

        @Override
        public List<Feed> getTlistforThisCatagory(List<Feed> tlist, int category) {
           List<Feed> feedList=new ArrayList<>();

            for(Feed feed:tlist){
                if(feed.getCategoryId()==category){
                    feedList.add(feed);
                }
            }
            return feedList;

        }
    };




    private static <T> List<T> filter(List<Feed> lastFeedsInDB,List<T> target) {


        List<T> result = new ArrayList<T>();
        System.out.println("before removing "+target.size());
        if(target.removeAll(lastFeedsInDB)){
            System.out.println("true"+target.size());
        }
        System.out.println("after removing "+target.size());
        return target;

    }


}
