package com.virtualassistant.LoggedIn;

import android.Manifest;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.virtualassistant.LoggedIn.HomeFragment.ChatMessageAdapter;
import com.virtualassistant.LoggedIn.HomeFragment.HomeFragment;
import com.virtualassistant.R;
import com.virtualassistant.models.ChatMessage;

import org.alicebot.ab.AIMLProcessor;
import org.alicebot.ab.Bot;
import org.alicebot.ab.Chat;
import org.alicebot.ab.Graphmaster;
import org.alicebot.ab.MagicBooleans;
import org.alicebot.ab.MagicStrings;
import org.alicebot.ab.PCAIMLProcessorExtension;
import org.alicebot.ab.Timer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

public class MainActivity extends AppCompatActivity {


    private Fragment fragment;
    private FragmentManager fragmentManager;
    private BottomNavigationView navigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupView();

        setupCA();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);


        navigation.inflateMenu(R.menu.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.navigation_dashboard:
                        fragment = new DashboardFragment();
                        break;
                    case R.id.navigation_notifications:
                        fragment = new NotificationFragment();
                        break;
                }

                addFragment(fragment);

                return true;
            }
        });



    }

    private void setupView() {

        getViewIds();

        fragmentManager = getSupportFragmentManager();
        addFragment(new HomeFragment());

    }

    private void getViewIds() {
        navigation = (BottomNavigationView) findViewById(R.id.navigation);

    }

    private void addFragment(Fragment fragment) {
        final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.content, fragment).commit();
    }

    private void setupCA(){
//        try {// Load CAs from an InputStream
//// (could be from a resource or ByteArrayInputStream or ...)
//            CertificateFactory cf = CertificateFactory.getInstance("X.509");
//// From https://www.washington.edu/itconnect/security/ca/load-der.crt
//            InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
//            Certificate ca;
//            try {
//                ca = cf.generateCertificate(caInput);
//                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
//            } finally {
//                caInput.close();
//            }
//
//// Create a KeyStore containing our trusted CAs
//            String keyStoreType = KeyStore.getDefaultType();
//            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
//            keyStore.load(null, null);
//            keyStore.setCertificateEntry("ca", ca);
//
//// Create a TrustManager that trusts the CAs in our KeyStore
//            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
//            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
//            tmf.init(keyStore);
//
//// Create an SSLContext that uses our TrustManager
//            SSLContext context = SSLContext.getInstance("TLS");
//            context.init(null, tmf.getTrustManagers(), null);
//// Tell the okhttp to use a SocketFactory from our SSLContext
//            OkHttpClient client = new OkHttpClient.Builder().sslSocketFactory(context.getSocketFactory()).build();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }
}
