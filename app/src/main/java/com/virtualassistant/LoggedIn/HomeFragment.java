package com.virtualassistant.LoggedIn;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.virtualassistant.R;

/**
 * Created by aadam on 11/4/2017.
 */

public class HomeFragment extends android.support.v4.app.Fragment {
    public HomeFragment(){};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

}
