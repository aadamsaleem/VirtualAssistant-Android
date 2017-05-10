package com.virtualassistant.LoggedOut;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.virtualassistant.LoggedIn.HomeActivity;
import com.virtualassistant.R;
import com.virtualassistant.models.User;
import com.virtualassistant.util.PrefUtils;
import com.yqritc.scalablevideoview.ScalableType;
import com.yqritc.scalablevideoview.ScalableVideoView;

import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SplashScreen extends AppCompatActivity {

    LoginButton loginButton;
    CallbackManager callbackManager;
    User user;

    //region Facebook CallBack
    private FacebookCallback<LoginResult> mCallBack = new FacebookCallback<LoginResult>() {
        @Override
        public void onSuccess(LoginResult loginResult) {

            GraphRequest request = GraphRequest.newMeRequest(
                    loginResult.getAccessToken(),
                    new GraphRequest.GraphJSONObjectCallback() {
                        @Override
                        public void onCompleted(
                                JSONObject object,
                                final GraphResponse response) {

                            try {
                                user = new User();
                                user.setFacebookID(object.getString("id"));
                                user.setEmail(object.getString("email"));
                                user.setName(object.getString("name"));
                                user.setGender(object.getString("gender"));


//                                final ProgressDialog progressDialog = new ProgressDialog(SplashScreen.this, R.style.AppTheme);
//                                progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//                                progressDialog.setCancelable(false);
//                                progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
//                                progressDialog.show();

//                                UserManager.signup(user, getApplicationContext(), new CompletionInterface() {
//                                    @Override
//                                    public void onSuccess(JSONObject result) {
//
//                                        String status = null;
//                                        try {
//                                            String token = result.getString("user_token");
//                                            status = result.getString("status");
//                                            user.setToken(token);
                                            PrefUtils.setCurrentUser(user, SplashScreen.this);
//
//                                        } catch (JSONException e) {
//                                            e.printStackTrace();
//                                        }
//                                        Toast.makeText(SplashScreen.this, "Welcome " + user.getName(), Toast.LENGTH_LONG).show();
//                                        if(status.equals("101"))
//                                        {
//                                            Intent intent = new Intent(SplashScreen.this, PreferencesActivity.class);
//                                            intent.putExtra("newUser", true);
//                                            startActivity(intent);
//                                        }
//                                        else{
                                            Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
                                            startActivity(intent);
                                            finish();
//                                        }
//
//                                        progressDialog.dismiss();
//                                        finish();
//
//                                    }
//
//                                    @Override
//                                    public void onFailure() {
//                                        Toast.makeText(SplashScreen.this, "Something went wrong!", Toast.LENGTH_LONG).show();
//                                        progressDialog.dismiss();
//                                        try {
//                                            UserManager.signOut(SplashScreen.this);
//                                        } catch (Exception e) {
//                                            e.printStackTrace();
//                                        }
//
//                                    }
//                                });


                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }

                    });

            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,email,gender, birthday");
            request.setParameters(parameters);
            request.executeAsync();
        }

        @Override
        public void onCancel() {
        }

        @Override
        public void onError(FacebookException error) {

        }

    };
    //endregion

    //region Override Methods
    @Override
    public void onCreate(Bundle savedInstanceState) {

        initFacebook();

        super.onCreate(savedInstanceState);

        printKeyHash();
        if (PrefUtils.getCurrentUser(SplashScreen.this) != null) {
            Intent homeIntent = new Intent(SplashScreen.this, HomeActivity.class);
            startActivity(homeIntent);
            finish();
        }

        setContentView(R.layout.activity_splash_screen);

        getViewIDs();

    }

    @Override
    protected void onResume() {
        super.onResume();

        callbackManager = CallbackManager.Factory.create();

        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile", "email", "user_friends");
        loginButton.registerCallback(callbackManager, mCallBack);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
    //endregion

    //region Private Methods
    private void getViewIDs() {

        loginButton = (LoginButton) findViewById(R.id.login_button);

    }

    private void initFacebook() {

        FacebookSdk.sdkInitialize(getApplicationContext());

    }

    private void printKeyHash() {
        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo("com.virtualassistant", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("KeyHash:", e.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("KeyHash:", e.toString());
        }
    }
    //endregion
}


