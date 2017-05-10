package com.virtualassistant.LoggedIn.Settings;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.login.widget.LoginButton;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.virtualassistant.LoggedOut.SplashScreen;
import com.virtualassistant.R;
import com.virtualassistant.client.UserManager;
import com.virtualassistant.models.User;
import com.virtualassistant.util.PrefUtils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class SettingsFragment extends Fragment {

    RoundedImageView profilePicture;
    TextView nameTextView, emailTextView;
    Bitmap bitmap;
    User user;
    LoginButton loginButton;
    LinearLayout preferencesLayout;
    View rootView;
    Context context;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_settings, container, false);

        user = PrefUtils.getCurrentUser(context);
        getViewIDs();

        setUpViews();

        return rootView;
    }

    private void getViewIDs()
    {
        profilePicture = (RoundedImageView) rootView.findViewById(R.id.profile_picture);
        nameTextView = (TextView) rootView.findViewById(R.id.name);
        emailTextView = (TextView) rootView.findViewById(R.id.email);
        loginButton = (LoginButton) rootView.findViewById(R.id.login_button);
        preferencesLayout  = (LinearLayout) rootView.findViewById(R.id.preferences_Layout);

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
                Intent i = new Intent(context, PreferencesActivity.class);
                startActivity(i);

            }
        });

    }

    private void getProfilePic(){


        String url = "https://graph.facebook.com/" + user.getFacebookID() + "/picture?type=large";
        ImageLoader.getInstance().displayImage(url, profilePicture);
//        new AsyncTask<Void,Void,Void>(){
//            @Override
//            protected Void doInBackground(Void... params) {
//                URL imageURL = null;
//                try {
//                    imageURL = new URL("https://graph.facebook.com/" + user.getFacebookID() + "/picture?type=large");
//                } catch (MalformedURLException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    bitmap  = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                super.onPostExecute(aVoid);
//                profilePicture.setImageBitmap(bitmap);
//            }
//        }.execute();
    }


    private void logout(){
        UserManager.signOut(getActivity());

        Intent i= new Intent(getActivity(),SplashScreen.class);
        startActivity(i);
        getActivity().finish();
    }
    //endregion
}
