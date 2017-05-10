package com.virtualassistant.LoggedIn;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;
import com.onesignal.OneSignal.NotificationOpenedHandler;
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
    private static String playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initImageLoader();

        initOneSignal();


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

        EventManager.sendAllEvents(this, getPlayerId(), new CompletionInterface() {
            @Override
            public void onSuccess(JSONObject result) {

                Log.e("bbbb", result.toString());
            }

            @Override
            public void onFailure() {

            }
        });

    }


    //region Private Methods
    private void initOneSignal() {
        OneSignal.startInit(this)
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .unsubscribeWhenNotificationsAreDisabled(true)
                .setNotificationOpenedHandler(new EventNotificationOpenedHandler())
                .init();
    }

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

    private String getPlayerId() {
        OneSignal.idsAvailable(new OneSignal.IdsAvailableHandler() {
            @Override
            public void idsAvailable(String userId, String registrationId) {
                playerId = userId;
            }
        });

        SharedPreferences.Editor editor = getSharedPreferences("VA", MODE_PRIVATE).edit();
        editor.putString("playerId", playerId);
        editor.commit();
        return playerId;
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

    // This fires when a notification is opened by tapping on it or one is received while the app is running.
    private class EventNotificationOpenedHandler implements NotificationOpenedHandler {
        @Override
        public void notificationOpened(OSNotificationOpenResult result) {
            OSNotificationAction.ActionType actionType = result.action.type;
            JSONObject data = result.notification.payload.additionalData;
            String type, value;

            if (data != null) {
                type = data.optString("type", null);
                value = data.optString("value", null);
                switch (type) {
                    case "phonenumber":
                        phonecall(value);
                        break;
                    case "skype":
                        skype(value);
                        break;
                    case "hangout":
                        hangout(value);
                        break;
                }
                if (type != null)
                    Log.i("OneSignalExample", "customkey set with value: " + type);
            }

            if (actionType == OSNotificationAction.ActionType.ActionTaken)
                Log.i("OneSignalExample", "Button pressed with id: " + result.action.actionID);


        }

        private void phonecall(String number) {
            Intent intent = new Intent(Intent.ACTION_CALL);

            intent.setData(Uri.parse("tel:" + number));
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivity(intent);
        }

        private void skype(String link) {
            try {

                Intent sky = new Intent("android.intent.action.VIEW");
                sky.setData(Uri.parse("skype:" + link + "?call&video=true"));
                startActivity(sky);
            } catch (ActivityNotFoundException e) {
                Log.e("SKYPE CALL", "Skype failed", e);
                Toast.makeText(getApplicationContext(), "Unable to place Skype Call", Toast.LENGTH_LONG).show();
            }

        }

        private void hangout(String link) {
            try {
                Intent hangout = new Intent("android.intent.action.VIEW");
                hangout.setData(Uri.parse(link));
                startActivity(hangout);
            } catch (ActivityNotFoundException e) {
                Log.e("SKYPE CALL", "Skype failed", e);
                Toast.makeText(getApplicationContext(), "Unable to place Hangout Call", Toast.LENGTH_LONG).show();
            }

        }

    }

}
