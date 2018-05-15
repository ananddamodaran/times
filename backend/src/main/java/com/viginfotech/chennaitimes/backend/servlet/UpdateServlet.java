package com.viginfotech.chennaitimes.backend.servlet;




import com.viginfotech.chennaitimes.backend.Constants;
import com.viginfotech.chennaitimes.backend.model.Feed;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.viginfotech.chennaitimes.backend.service.OfyService.ofy;
import static com.viginfotech.chennaitimes.backend.utils.UpdateHelper.removeDuplicates;
import static com.viginfotech.chennaitimes.backend.utils.UpdateHelper.updateBBCTamil;
import static com.viginfotech.chennaitimes.backend.utils.UpdateHelper.updateDinakaran;
import static com.viginfotech.chennaitimes.backend.utils.UpdateHelper.updateDinamalar;
import static com.viginfotech.chennaitimes.backend.utils.UpdateHelper.updateDinamani;
import static com.viginfotech.chennaitimes.backend.utils.UpdateHelper.updateNakkheeran;
import static com.viginfotech.chennaitimes.backend.utils.UpdateHelper.updateOneIndia;


/**
 * Created by anand on 12/21/15.
 */
public class UpdateServlet extends HttpServlet {
    private static final Logger logger = Logger.getLogger(UpdateServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        logger.info("updating by cron");
        List<Feed> dinakaranFeeds = updateDinakaran();
        List<Feed> dinamalarFeeds = updateDinamalar();
        List<Feed> bbcTamilFeeds = updateBBCTamil();
        List<Feed> dinamaniFeeds = updateDinamani();
        List<Feed> oneIndiaFeeds = updateOneIndia();
        List<Feed> nakkheeranFeeds = updateNakkheeran();

        List<Feed> allFeeds = new ArrayList<>();


        List<Integer> dinakaranCategory = Arrays.asList(
                Constants.INSTANCE.getCATEGORY_TAMILNADU(),
                Constants.INSTANCE.getCATEGORY_INDIA(),
                Constants.INSTANCE.getCATEGORY_WORLD(),
                Constants.INSTANCE.getCATEGORY_BUSINESS(),
                Constants.INSTANCE.getCATEGORY_SPORTS());
        List<Integer> dinamalarCategory = Arrays.asList(
                Constants.INSTANCE.getCATEGORY_HEADLINES(),
                Constants.INSTANCE.getCATEGORY_TAMILNADU(),
                Constants.INSTANCE.getCATEGORY_WORLD(),
                Constants.INSTANCE.getCATEGORY_BUSINESS(),
                Constants.INSTANCE.getCATEGORY_CINEMA());
        List<Integer> bbcCategory = Arrays.asList(
                Constants.INSTANCE.getCATEGORY_INDIA(),
                Constants.INSTANCE.getCATEGORY_WORLD()
        );
        List<Integer> dinamanaiCategory = Arrays.asList(
                Constants.INSTANCE.getCATEGORY_HEADLINES(),
                Constants.INSTANCE.getCATEGORY_TAMILNADU(),
                Constants.INSTANCE.getCATEGORY_INDIA(),
                Constants.INSTANCE.getCATEGORY_WORLD(),
                Constants.INSTANCE.getCATEGORY_BUSINESS(),
                Constants.INSTANCE.getCATEGORY_SPORTS(),
                Constants.INSTANCE.getCATEGORY_CINEMA()
        );

        List<Integer> oneIndiaCategory = Arrays.asList(
                Constants.INSTANCE.getCATEGORY_TAMILNADU(),
                Constants.INSTANCE.getCATEGORY_INDIA(),
                Constants.INSTANCE.getCATEGORY_WORLD(),
                Constants.INSTANCE.getCATEGORY_BUSINESS()
        );

        List<Integer> nakkheeranCategory = Arrays.asList(Constants.INSTANCE.getCATEGORY_HEADLINES(),
                Constants.INSTANCE.getCATEGORY_TAMILNADU(),
                Constants.INSTANCE.getCATEGORY_INDIA(),
                Constants.INSTANCE.getCATEGORY_WORLD(),
                Constants.INSTANCE.getCATEGORY_SPORTS());
        dinakaranFeeds = removeDuplicates(Constants.INSTANCE.getSOURCE_DINAKARAN(), dinakaranCategory, dinakaranFeeds);
        dinamalarFeeds = removeDuplicates(Constants.INSTANCE.getSOURCE_DINAMALAR(), dinamalarCategory, dinamalarFeeds);
        bbcTamilFeeds = removeDuplicates(Constants.INSTANCE.getSOURCE_BBCTAMIL(), bbcCategory, bbcTamilFeeds);
        dinamaniFeeds = removeDuplicates(Constants.INSTANCE.getSOURCE_DINAMANI(), dinamanaiCategory, dinamaniFeeds);
        oneIndiaFeeds = removeDuplicates(Constants.INSTANCE.getSOURCE_ONEINDIA(), oneIndiaCategory, oneIndiaFeeds);
        nakkheeranFeeds = removeDuplicates(Constants.INSTANCE.getSOURCE_NAKKHEERAN(), nakkheeranCategory, nakkheeranFeeds);

        allFeeds.addAll(dinakaranFeeds);
        allFeeds.addAll(dinamaniFeeds);
        allFeeds.addAll(dinamalarFeeds);
        allFeeds.addAll(oneIndiaFeeds);
        allFeeds.addAll(nakkheeranFeeds);
        allFeeds.addAll(bbcTamilFeeds);

        ofy().save().entities(allFeeds).now();


        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);






    }


}
