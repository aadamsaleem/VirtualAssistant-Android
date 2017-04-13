package com.virtualassistant.LoggedIn;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.virtualassistant.LoggedOut.SplashScreen;
import com.virtualassistant.R;
import com.virtualassistant.client.UserManager;
import com.virtualassistant.models.User;
import com.virtualassistant.util.PrefUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ProfileActivity extends AppCompatActivity {

    RoundedImageView profilePicture;
    TextView nameTextView, emailTextView;
    Bitmap bitmap;
    User user;
    LoginButton loginButton;
    LinearLayout preferencesLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = PrefUtils.getCurrentUser(getApplicationContext());
        getViewIDs();

        setUpViews();

    }

    private void getViewIDs()
    {
        profilePicture = (RoundedImageView) findViewById(R.id.profile_picture);
        nameTextView = (TextView) findViewById(R.id.name);
        emailTextView = (TextView) findViewById(R.id.email);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        preferencesLayout  = (LinearLayout) findViewById(R.id.preferences_Layout);

    }

    private void setUpViews(){
        getProfilePic();

        nameTextView.setText(user.getName());
        emailTextView.setText(user.getEmail());

        profilePicture.setScaleType(ImageView.ScaleType.CENTER_CROP);
        profilePicture.setOval(true);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();

            }
        });

        preferencesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(mContext, PreferencesActivity.class);
//                startActivity(i);

            }
        });

    }

    private void getProfilePic(){
        new AsyncTask<Void,Void,Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                URL imageURL = null;
                try {
                    imageURL = new URL("https://graph.facebook.com/" + user.getFacebookID() + "/picture?type=large");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    bitmap  = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                profilePicture.setImageBitmap(bitmap);
            }
        }.execute();
    }


    private void logout(){
        UserManager.signOut(this);

        Intent i= new Intent(this,SplashScreen.class);
        startActivity(i);
        finish();
    }
    //endregion
}
