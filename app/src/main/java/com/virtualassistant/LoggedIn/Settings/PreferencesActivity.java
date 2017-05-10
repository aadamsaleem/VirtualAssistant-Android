package com.virtualassistant.LoggedIn.Settings;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;

import com.virtualassistant.R;

public class PreferencesActivity extends AppCompatActivity {


    Switch morningWishSwitch, notificationCallSwitch;
    Button saveButton;

    boolean morningWish, notificationCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        SharedPreferences prefs = getSharedPreferences("VA", MODE_PRIVATE);
        morningWish= prefs.getBoolean("morningWishSwitch", true);
        notificationCall = prefs.getBoolean("notificationCallSwitch", true);

        setupView();
    }

    private void setupView(){

        getViewIds();

        morningWishSwitch.setChecked(morningWish);
        notificationCallSwitch.setChecked(notificationCall);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                morningWish = morningWishSwitch.isChecked();
                notificationCall = notificationCallSwitch.isChecked();

                SharedPreferences.Editor editor = getSharedPreferences("VA", MODE_PRIVATE).edit();
                editor.putBoolean("morningWishSwitch", morningWish);
                editor.putBoolean("notificationCallSwitch", notificationCall);
                editor.commit();

                finish();
            }
        });


    }

    private void getViewIds(){
        morningWishSwitch = (Switch) findViewById(R.id.morningSwitch);
        notificationCallSwitch = (Switch) findViewById(R.id.notification_switch);
        saveButton = (Button) findViewById(R.id.save_button);
    }

}
