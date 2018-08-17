package com.viginfotech.chennaitimes.ui


import android.app.Activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import com.crashlytics.android.Crashlytics
import com.firebase.jobdispatcher.*
import com.google.android.gms.appinvite.AppInviteInvitation
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.firebase.analytics.FirebaseAnalytics
import com.viginfotech.chennaitimes.Config
import com.viginfotech.chennaitimes.Constants.CATEGORY_BUSINESS
import com.viginfotech.chennaitimes.Constants.CATEGORY_CINEMA
import com.viginfotech.chennaitimes.Constants.CATEGORY_HEADLINES
import com.viginfotech.chennaitimes.Constants.CATEGORY_INDIA
import com.viginfotech.chennaitimes.Constants.CATEGORY_MEME
import com.viginfotech.chennaitimes.Constants.CATEGORY_SPORTS
import com.viginfotech.chennaitimes.Constants.CATEGORY_TAMILNADU
import com.viginfotech.chennaitimes.Constants.CATEGORY_WORLD
import com.viginfotech.chennaitimes.R
import com.viginfotech.chennaitimes.R.id.*
import com.viginfotech.chennaitimes.firebase.SyncSheduleService
import com.viginfotech.chennaitimes.util.DisplayUtil
import com.viginfotech.chennaitimes.util.PrefUtils
import com.viginfotech.chennaitimes.util.ZoomOutPageTransformer
import io.fabric.sdk.android.Fabric
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.app_bar_home.*
import java.util.*


class HomeActivity : AppCompatActivity(),
        ViewPager.OnPageChangeListener,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_home)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        val mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        val dispatcher = FirebaseJobDispatcher(
                GooglePlayDriver(this@HomeActivity)
        )
        dispatcher.mustSchedule(
                dispatcher.newJobBuilder()
                        .setService(SyncSheduleService::class.java)
                        .setTag(Config.SYNCSCHEDULE_TAG)
                        .setRecurring(true)
                        .setTrigger(Trigger.executionWindow(5, 60 * 10))//every 10 mins
                        .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                        .setLifetime(Lifetime.FOREVER)
                        .build()
        )
        dispatcher.mustSchedule(
                dispatcher.newJobBuilder()
                        .setService(SyncSheduleService::class.java)
                        .setTag(Config.TRUNCATE_TAG)
                        .setRecurring(true)
                        .setTrigger(Trigger.executionWindow(5, 60 * 60 * 24)) //every 24 hour
                        .setRetryStrategy(RetryStrategy.DEFAULT_EXPONENTIAL)
                        .setLifetime(Lifetime.FOREVER)
                        .build()

        )




        setSupportActionBar(toolbar)
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout!!.setDrawerListener(toggle)
        toggle.syncState()

        nav_view!!.setNavigationItemSelectedListener(this)



        if (container != null) setupViewPager(container)
        container!!.setOnPageChangeListener(this)
        container!!.setPageTransformer(true, ZoomOutPageTransformer())
        title = getString(R.string.action_headlines)


    }

    override fun onRestart() {
        super.onRestart()

    }

    private lateinit var mSectionsPagerAdapter: SectionsPagerAdapter

    private fun setupViewPager(viewPager: ViewPager) {
        mSectionsPagerAdapter = SectionsPagerAdapter(supportFragmentManager)
        for (i in 0..6) {
            val fragment = NewsFragment()
            val args = Bundle()
            args.putInt("section_number", i)
            fragment.arguments = args
            mSectionsPagerAdapter!!.addFragment(fragment, DisplayUtil.getAppTitle(this, i))

        }
        viewPager.adapter = mSectionsPagerAdapter
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_INVITE) {

            if (resultCode == Activity.RESULT_OK) {
                val ids = AppInviteInvitation.getInvitationIds(resultCode, data)
            } else {

                Toast.makeText(this, getString(R.string.send_failed), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.home, menu)
        return true
    }


    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        item.isChecked = true

        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        drawer.closeDrawer(GravityCompat.START)


        when (item.itemId) {
            R.id.action_headlines -> {
                container!!.currentItem = CATEGORY_HEADLINES
                return true
            }

            R.id.action_tamilnadu -> {
                container!!.currentItem = CATEGORY_TAMILNADU
                return true
            }
            R.id.action_india -> {
                container!!.currentItem = CATEGORY_INDIA
                return true
            }

            R.id.action_world -> {
                container!!.currentItem = CATEGORY_WORLD
                return true
            }
            R.id.action_business -> {
                container!!.currentItem = CATEGORY_BUSINESS
                return true
            }
            R.id.action_sports -> {
                container!!.currentItem = CATEGORY_SPORTS
                return true
            }
            R.id.action_cinema -> {
                container!!.currentItem = CATEGORY_CINEMA
                return true
            }
            R.id.action_meme -> {
                container!!.currentItem= CATEGORY_MEME
                return true
            }
            R.id.nav_share -> {

                onInviteClicked()
                item.isChecked = false
                return true
            }
            else -> {
                container!!.currentItem = 0
                return true
            }
        }


    }

    private fun onInviteClicked() {
        val intent = AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCustomImage(Uri.parse("android.resource://com.androidnanban.chennaitimes/drawable/play_store_j.jpg"))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build()
        startActivityForResult(intent, REQUEST_INVITE)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        title = mSectionsPagerAdapter!!.getPageTitle(position)
        if (position > 0)
            PrefUtils.setLastSeenCategory(this, position)

        when (position) {
            CATEGORY_HEADLINES -> nav_view!!.setCheckedItem(R.id.action_headlines)
            CATEGORY_TAMILNADU -> nav_view!!.setCheckedItem(R.id.action_tamilnadu)
            CATEGORY_INDIA -> nav_view!!.setCheckedItem(R.id.action_india)
            CATEGORY_WORLD -> nav_view!!.setCheckedItem(R.id.action_world)
            CATEGORY_BUSINESS-> nav_view!!.setCheckedItem(R.id.action_business)
            CATEGORY_SPORTS -> nav_view!!.setCheckedItem(R.id.action_sports)
            CATEGORY_CINEMA-> nav_view!!.setCheckedItem(R.id.action_cinema)
            CATEGORY_MEME-> nav_view!!.setCheckedItem(R.id.action_meme)
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    override fun onBackPressed() {
        val drawer = findViewById<View>(R.id.drawer_layout) as DrawerLayout
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else if (container!!.currentItem == 0) {
            super.onBackPressed()
        } else {

            container!!.currentItem = CATEGORY_HEADLINES
            nav_view!!.setCheckedItem(R.id.action_headlines)


        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {

    }

    inner class SectionsPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        private val mFragments = ArrayList<Fragment>()
        private val mFragmentTitles = ArrayList<String>()

        fun addFragment(fragment: Fragment, title: String?) {
            mFragments.add(fragment)
            mFragmentTitles.add(title!!)
        }


        override fun getItem(position: Int): Fragment {
            return mFragments[position]
        }

        override fun getCount(): Int {

            return mFragments.size
        }


        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitles[position]
        }


    }

    companion object {
        val REQUEST_INVITE = 2000
        val ITEMS_PER_AD = 8
    }

}
