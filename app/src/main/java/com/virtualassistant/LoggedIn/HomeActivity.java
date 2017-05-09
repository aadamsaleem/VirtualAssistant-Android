package com.virtualassistant.LoggedIn;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.onesignal.OneSignal;
import com.virtualassistant.Constants;
import com.virtualassistant.LoggedIn.Chat.ChatFragment;
import com.virtualassistant.LoggedIn.News.NewsFragment;
import com.virtualassistant.LoggedIn.Settings.SettingsFragment;
import com.virtualassistant.LoggedIn.Weather.WeatherFragment;
import com.virtualassistant.R;
import com.virtualassistant.client.CompletionInterface;
import com.virtualassistant.client.EventManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {


    private SectionsPagerAdapter mSectionsPagerAdapter;

    private ViewPager mViewPager;
    private boolean firstStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initImageLoader();

        initOneSignal();

        SharedPreferences prefs = getSharedPreferences("VA", MODE_PRIVATE);
        firstStart = prefs.getBoolean("firstStart", true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.setTabTextColors(Color.parseColor("#3c775d"), Color.parseColor("#daefe7"));

        tabLayout.getTabAt(0).setIcon(R.drawable.icon_chat);
        tabLayout.getTabAt(1).setIcon(R.drawable.icon_news);
        tabLayout.getTabAt(2).setIcon(R.drawable.icon_weather);
        tabLayout.getTabAt(3).setIcon(R.drawable.icon_settings);

        if(firstStart){

            sendAllEvents();
        }

    }

    private void initOneSignal() {
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .init();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new ChatFragment();
                case 1:
                    return new NewsFragment();
                case 2:
                    return new WeatherFragment();
                case 3:
                    return new SettingsFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Chat";
                case 1:
                    return "News";
                case 2:
                    return "Weather";
                case 3:
                    return "Settings";
            }
            return null;
        }
    }

    private void initImageLoader(){
        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .resetViewBeforeLoading(true)
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(0))
                .build();

        ImageLoaderConfiguration imageLoaderConfiguration = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY - 1)
                .denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .defaultDisplayImageOptions(displayImageOptions).build();

        ImageLoader.getInstance().init(imageLoaderConfiguration);

    }

    public void sendAllEvents(){
        Cursor cursor = getApplicationContext().getContentResolver().query(Uri.parse("content://com.android.calendar/events"), new String[] { "calendar_id", "_id", "title", "description", "dtstart" }, null, null, null);
        cursor.moveToFirst();

        JSONObject requestJson = new JSONObject();
        try {
            requestJson.put("onsignal_playerId", Constants.ONE_SIGNAL_ID);

            Date date = new Date();

        JSONArray events = new JSONArray();
        for (int i = 0; i < cursor.getCount(); i++) {

            JSONObject eventJson = null;
            if (date.before(new Date(getDate(Long.parseLong(cursor.getString(4)))))) {
                eventJson = new JSONObject();

                eventJson.put("Calender Id", cursor.getString(0));
                eventJson.put("Event id", cursor.getString(1));
                eventJson.put("summary", cursor.getString(2));
                eventJson.put("description", cursor.getString(3));
                eventJson.put("start_date", getDate(Long.parseLong(cursor.getString(4))));


                events.put(eventJson);
            }
            cursor.moveToNext();

        }

        requestJson.put("onesignal_events",events);

            Log.e("aaaa", requestJson.toString());
            EventManager.sendAllEvents(this, requestJson, new CompletionInterface() {
                @Override
                public void onSuccess(JSONObject result) {

                    Log.e("bbbb",result.toString());
                }

                @Override
                public void onFailure() {

                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "MMM dd, yyyy h:mm:ss a Z");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }


}
