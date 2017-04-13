package com.virtualassistant.LoggedIn;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.WindowManager;

import com.virtualassistant.LoggedIn.HomeFragment.HomeFragment;
import com.virtualassistant.LoggedIn.News.NewsFragment;
import com.virtualassistant.LoggedIn.Notification.NotificationFragment;
import com.virtualassistant.R;

public class MainActivity extends AppCompatActivity {


    private Fragment homeFragment, newsFragment, notificationFragment;
    private FragmentManager fragmentManager;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupView();

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);


        navigation.inflateMenu(R.menu.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                getSupportFragmentManager().beginTransaction().hide(homeFragment).commit();
                getSupportFragmentManager().beginTransaction().hide(newsFragment).commit();
                getSupportFragmentManager().beginTransaction().hide(notificationFragment).commit();

                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        getSupportFragmentManager().beginTransaction().show(homeFragment).commit();
                        break;
                    case R.id.navigation_dashboard:
                        getSupportFragmentManager().beginTransaction().show(newsFragment).commit();
                        break;
                    case R.id.navigation_notifications:
                        getSupportFragmentManager().beginTransaction().show(notificationFragment).commit();
                        break;
                }

                return true;
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.profile_picture) {
            Intent i = new Intent(this, ProfileActivity.class);
            startActivity(i);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupView() {

        getViewIds();

        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        homeFragment = new HomeFragment();
        newsFragment = new NewsFragment();
        notificationFragment = new NotificationFragment();

        fragmentManager = getSupportFragmentManager();
        addFragment(homeFragment);
        addFragment(newsFragment);
        addFragment(notificationFragment);

        getSupportFragmentManager().beginTransaction().show(homeFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(newsFragment).commit();
        getSupportFragmentManager().beginTransaction().hide(notificationFragment).commit();

    }

    private void getViewIds() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);

    }

    private void addFragment(Fragment fragment) {
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.content, fragment).commit();
    }
}
