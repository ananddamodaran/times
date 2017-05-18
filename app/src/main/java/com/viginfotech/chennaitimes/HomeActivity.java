package com.viginfotech.chennaitimes;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.appinvite.AppInviteInvitation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.viginfotech.chennaitimes.util.DisplayUtil;
import com.viginfotech.chennaitimes.util.PrefUtils;
import com.viginfotech.chennaitimes.util.ZoomOutPageTransformer;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.viginfotech.chennaitimes.Constants.CATEGORY_BUSINESS;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_CINEMA;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_HEADLINES;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_INDIA;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_SPORTS;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_TAMILNADU;
import static com.viginfotech.chennaitimes.Constants.CATEGORY_WORLD;


public class HomeActivity extends AppCompatActivity
        implements ViewPager.OnPageChangeListener,
        NavigationView.OnNavigationItemSelectedListener,
        GoogleApiClient.OnConnectionFailedListener {

    @Bind(R.id.container)
   ViewPager mViewPager;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    @Bind(R.id.nav_view)
    NavigationView navigationView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawer;
    public static final int REQUEST_INVITE = 2000;


    public static final int ITEMS_PER_AD = 8;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        ButterKnife.bind(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);



        if (mViewPager != null) setupViewPager(mViewPager);
        mViewPager.setOnPageChangeListener(this);
        mViewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        setTitle(getString(R.string.action_headlines));


    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    private void setupViewPager(ViewPager viewPager) {
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        for (int i = 0; i < 7; i++) {
            NewsFragment fragment = new NewsFragment();
            Bundle args = new Bundle();
            args.putInt("section_number", i);
            fragment.setArguments(args);
            mSectionsPagerAdapter.addFragment(fragment, DisplayUtil.getAppTitle(this, i));

        }
        viewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_INVITE) {

            if (resultCode == RESULT_OK) {
                String[] ids = AppInviteInvitation.getInvitationIds(resultCode, data);
            } else {

                Toast.makeText(this, getString(R.string.send_failed), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        item.setChecked(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);


        switch (item.getItemId()) {
            case R.id.action_headlines:
                mViewPager.setCurrentItem(CATEGORY_HEADLINES);
                return true;

            case R.id.action_tamilnadu:
                mViewPager.setCurrentItem(CATEGORY_TAMILNADU);
                return true;
            case R.id.action_india:
                mViewPager.setCurrentItem(CATEGORY_INDIA);
                return true;

            case R.id.action_world:
                mViewPager.setCurrentItem(CATEGORY_WORLD);
                return true;
            case R.id.action_business:
                mViewPager.setCurrentItem(CATEGORY_BUSINESS);
                return true;
            case R.id.action_sports:
                mViewPager.setCurrentItem(CATEGORY_SPORTS);
                return true;
            case R.id.action_cinema:
                mViewPager.setCurrentItem(CATEGORY_CINEMA);
                return true;
            case R.id.nav_share:

                onInviteClicked();
                item.setChecked(false);
                return true;
            default:
                mViewPager.setCurrentItem(0);
                return true;
        }


    }

    private void onInviteClicked() {
        Intent intent = new AppInviteInvitation.IntentBuilder(getString(R.string.invitation_title))
                .setMessage(getString(R.string.invitation_message))
                .setDeepLink(Uri.parse(getString(R.string.invitation_deep_link)))
                .setCustomImage(Uri.parse("android.resource://com.androidnanban.chennaitimes/drawable/play_store_j.jpg"))
                .setCallToActionText(getString(R.string.invitation_cta))
                .build();
        startActivityForResult(intent, REQUEST_INVITE);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setTitle(mSectionsPagerAdapter.getPageTitle(position));
        if (position > 0)
            PrefUtils.setLastSeenCategory(this, position);

        switch (position) {
            case CATEGORY_HEADLINES:
                navigationView.setCheckedItem(R.id.action_headlines);
                break;
            case CATEGORY_TAMILNADU:
                navigationView.setCheckedItem(R.id.action_tamilnadu);
               break;
            case CATEGORY_INDIA:
                navigationView.setCheckedItem(R.id.action_india);
             break;
            case CATEGORY_WORLD:
                navigationView.setCheckedItem(R.id.action_world);
               break;
            case CATEGORY_BUSINESS:
                navigationView.setCheckedItem(R.id.action_business);
               break;
            case CATEGORY_SPORTS:
                navigationView.setCheckedItem(R.id.action_sports);
               break;
            case CATEGORY_CINEMA:
                navigationView.setCheckedItem(R.id.action_cinema);
               break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (mViewPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {

            mViewPager.setCurrentItem(CATEGORY_HEADLINES);
            navigationView.setCheckedItem(R.id.action_headlines);


        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();
        private final List<String> mFragmentTitles = new ArrayList<>();


        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }


        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {

            return mFragments.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }


    }

}
