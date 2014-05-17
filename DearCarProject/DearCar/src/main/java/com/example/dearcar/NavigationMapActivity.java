package com.example.dearcar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dearcar.R;
//import com.google.android.gms.maps.*;

/**
 * Created by Yael on 6/23/13.
 */
public class NavigationMapActivity extends MapActivity{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.menu.main);
        //MapView mapView = (MapView) findViewById(R.id.mapview);
        //mapView.setBuiltInZoomControls(true);
    }
}
