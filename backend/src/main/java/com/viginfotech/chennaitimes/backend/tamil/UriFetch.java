package com.viginfotech.chennaitimes.backend.tamil;




import com.viginfotech.chennaitimes.backend.Constants;
import com.viginfotech.chennaitimes.backend.model.Feed;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by anand on 12/3/15.
 */
public class UriFetch {

    public static String fetchData(String uri) {

        BufferedReader reader = null;
        try {
            URL url = new URL(uri);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            StringBuilder builder = new StringBuilder();
            reader = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }
            return builder.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }
        }
    }

    public static List<Feed> fetchData(int categoryId, String uri) {
        Document doc;
        List<Feed> feedList = new ArrayList<>();
        try {
            doc = Jsoup.connect(uri).timeout(10000).get();

            Elements inboxLeft = doc.getElementsByClass("inn-box-left");
            if (inboxLeft != null&&inboxLeft.size()>0) {
                Elements inboxChildren = inboxLeft.get(0).getElementsByClass("inner-news-box-1");
                for (Element element : inboxChildren) {
                    Feed feed = new Feed();
                    Elements aTag = element.getElementsByTag("a");
                    String title = aTag.get(0).text();
                    String link = "http://www.dinakaran.com/" + aTag.get(0).attr("href");
                    String imgSrc = aTag.get(1).getElementsByTag("img").attr("src");
                    String description = element.getElementsByTag("p").text();
                    String pubDate = element.getElementsByTag("h5").text();
                    /*pubDate=pubDate.substring(0, pubDate.lastIndexOf(" "));
                    pubDate=pubDate.substring(0, pubDate.lastIndexOf(" "));
                    Date date=TimeUtils.parseDinakaranTimeStamp(pubDate);
                    date=(date==null)?new Date():new Date();
*/
                    feed.setTitle(title);

                    feed.setGuid(link);
                    feed.setSummary(description);
                    long now=System.currentTimeMillis();
                    feed.setPubDate(now);
                   // feed.setUpdate(now);

                    feed.setThumbnail(imgSrc);
                    feed.setCategoryId(categoryId);
                    feed.setSourceId(Constants.SOURCE_DINAKARAN);
                    feedList.add(feed);
                }
            }
            Elements inboxBottom = doc.getElementsByClass("box-left-normal");
            if (inboxBottom != null&&inboxBottom.size()>0) {
                Elements inboxChildren = inboxBottom.get(0).getElementsByClass("inner-news-box-1");
                for (Element element : inboxChildren) {
                    Feed feed = new Feed();
                    Elements aTag = element.getElementsByTag("a");
                    String title = aTag.get(0).text();
                    String link = "http://www.dinakaran.com/" + aTag.get(0).attr("href");
                    String imgSrc = aTag.get(1).getElementsByTag("img").attr("src");
                    String description = element.getElementsByTag("p").text();
                    String pubDate = element.getElementsByTag("h5").text();

                    feed.setTitle(title);
                    feed.setGuid(link);
                    feed.setSummary(description);
                    long now=System.currentTimeMillis();
                    feed.setPubDate(now);
                    feed.setThumbnail(imgSrc);
                    feed.setCategoryId(categoryId);
                    feed.setSourceId(Constants.SOURCE_DINAKARAN);
                    feedList.add(feed);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return feedList;
    }



    public static List<Feed> fetchOneIndiaData(int category, String uri) {
        Document doc;
        List<Feed> feedList=new ArrayList<>();
        try {
            doc = Jsoup.connect(uri).userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21").timeout(10000).get();
            Element element=doc.getElementById("collection-wrapper");
            if(element!=null) {
                Elements articles = element.getElementsByTag("article");


                for (Element article : articles) {
                    Feed feed = new Feed();

                    Elements heading=article.getElementsByClass("collection-heading");
                    if(heading!=null){

                        feed.setTitle(heading.get(0).text());
                    }

                    String articleImg = null;
                    Elements articleImgClass = article.getElementsByClass("article-img");
                    if (articleImgClass != null) {
                        feed.setThumbnail(
                                articleImgClass.get(0).getElementsByTag("img").get(0).attr("src"));
                    }



                    String link = article.getElementsByTag("a").get(1).attr("href");

                    feed.setGuid("http://tamil.oneindia.com" + link);
                    feed.setCategoryId(category);
                    feed.setSourceId(Constants.SOURCE_ONEINDIA);

                    long now=System.currentTimeMillis();

                    feed.setPubDate(now);

                    feedList.add(feed);
                }
            }

        } catch (IOException e) {

            e.printStackTrace();
        }
        return feedList;
    }

}
