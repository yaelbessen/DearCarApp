package com.example.dearcar;

import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import com.example.dearcar.NavigationMainScreen.NavigationListAdapter;

import java.util.ArrayList;


public class NavigationFindDestActivity extends FragmentActivity{

    public static final int VOICE_RECOGNITION_REQUEST_CODE = 1234;
    private ListView results;
    private ImageButton retry;
    private VoiceSearchDialog searchDestination;
    private LinearLayout findDestination;
    private LinearLayout mapScreen;
    ImageView map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.navigation_find_dest_screen);

        findDestination = (LinearLayout) findViewById(R.id.navigation_find_dest_layout);
        mapScreen = (LinearLayout) findViewById(R.id.map_layout);
        mapScreen.setVisibility(View.GONE);

        map=(ImageView) findViewById(R.id.map);

        final ImageButton cancel = (ImageButton)findViewById(R.id.cancel_button);
        cancel.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                    finish();
                }
                return true;
            }
        });

        final Button mapGoBack = (Button) (Button) findViewById(R.id.map_cancel);
        mapGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        retry = (ImageButton)findViewById(R.id.retry_button);

        results = (ListView) findViewById(R.id.navigation_result_list);
        results.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                findDestination.setVisibility(View.GONE);
                mapScreen.setVisibility(View.VISIBLE);
                map.setImageResource(R.drawable.waze1);
            }
        });

        String[] results_destinations = getResources().getStringArray(R.array.Results_Destination);
        ArrayList<Item> items = new ArrayList<Item>();
        for(String dest : results_destinations){
            items.add(new NavigationMainScreenEntry(dest));
        }
//        results.setAdapter(new NavigationResultsListAdapter(this, items));
        results.setAdapter(new NavigationListAdapter(this, items));

        searchDestination =
                new VoiceSearchDialog(this, 3, 3).disableResult()
                        .setSearchMessage("Say an address...").setOnOK(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        new android.os.Handler(Looper.getMainLooper()).post(new Runnable() {
                            public void run() {
                                results.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                }).setSound("nav.mp3")
                  .setOnCancel(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        finish();
                    }
                });

        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                results.setVisibility(View.GONE);
                searchDestination.show();
            }
        });

        results.setVisibility(View.GONE);
        searchDestination.show();
    }

    static class NavigationResultsListAdapter extends ArrayAdapter<Item>{
        NavigationListAdapter auxAdapter;

        public NavigationResultsListAdapter(Context context, ArrayList<Item> items){
            super(context,0,items);
            auxAdapter = new NavigationListAdapter(context, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = auxAdapter.getView(position, convertView, parent);
            view.setBackgroundResource(R.drawable.mesh_button_pressed);
            return view;
        }
    }
}