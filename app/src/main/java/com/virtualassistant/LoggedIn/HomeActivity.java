package com.virtualassistant.LoggedIn;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.virtualassistant.LoggedIn.Chat.ChatFragment;
import com.virtualassistant.LoggedIn.News.NewsFragment;
import com.virtualassistant.LoggedIn.Settings.SettingsFragment;
import com.virtualassistant.LoggedIn.Weather.WeatherFragment;
import com.virtualassistant.R;
import com.virtualassistant.client.CallLogManager;
import com.virtualassistant.interfaces.CompletionInterface;
import com.virtualassistant.client.EventManager;
import com.virtualassistant.receiver.AlarmReceiver;

import org.json.JSONObject;

import java.util.Calendar;

public class HomeActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, TextToSpeech.OnUtteranceCompletedListener {


    private static final int PERMISSIONS_REQUEST_READ_CALENDER = 1;
    private static String playerId;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    private TextToSpeech tts = null;
    private String msg = "";
    private boolean firstLaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences prefs = getSharedPreferences("VA", MODE_PRIVATE);
        firstLaunch = prefs.getBoolean("firstLaunch", true);
        playerId = prefs.getString("playerId",null);
        Constants.userID = playerId;

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        Intent startingIntent = this.getIntent();
        msg = startingIntent.getStringExtra("MESSAGE");
        tts = new TextToSpeech(this, this);

        initImageLoader();

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

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CALENDAR, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                    PERMISSIONS_REQUEST_READ_CALENDER);
        } else {
            EventManager.sendAllEvents(this, playerId, new CompletionInterface() {
                @Override
                public void onSuccess(JSONObject result) {

                }

                @Override
                public void onFailure() {

                }
            });
        }

        if (firstLaunch) {
            setMorningAlarm();
            firstLaunch = false;
            SharedPreferences.Editor editor = getSharedPreferences("VA", MODE_PRIVATE).edit();
            editor.putBoolean("firstLaunch", firstLaunch);
            editor.commit();

            CallLogManager.sendAllLog(getApplicationContext(), playerId);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_READ_CALENDER: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    EventManager.sendAllEvents(this, playerId, new CompletionInterface() {
                        @Override
                        public void onSuccess(JSONObject result) {


                        }

                        @Override
                        public void onFailure() {

                        }
                    });

                }

                return;
            }
        }
    }

    @Override
    protected void onDestroy() {

        if(tts != null) {

            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
    //region Private Methods


    private void initImageLoader() {
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



    private void setMorningAlarm() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 00);
        calendar.set(Calendar.SECOND, 00);
        Intent myIntent = new Intent(HomeActivity.this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(HomeActivity.this, 0, myIntent, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);

    }

    @Override
    public void onInit(int status) {
        tts.speak(msg, TextToSpeech.QUEUE_FLUSH, null);

    }

    @Override
    public void onUtteranceCompleted(String utteranceId) {
        tts.shutdown();
        tts = null;
        finish();

    }
    //endregion

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

}
