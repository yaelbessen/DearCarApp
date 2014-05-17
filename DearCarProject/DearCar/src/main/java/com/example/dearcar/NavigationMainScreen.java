package com.example.dearcar;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.content.Intent;

import java.util.ArrayList;

/**
 * Created by Yael on 6/22/13.
 */
public class NavigationMainScreen {
    public static class NavigationFragment extends ListFragment {

        static LinearLayout mapScreen;
        static LinearLayout mainScreen;
        ImageView mapView;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.navigation_main_screen, container, false);

            // storing string resources into Array
            String[] recent_destinations = getResources().getStringArray(R.array.Recent_Destination);
            String[] favorites_destinations = getResources().getStringArray(R.array.Favorites_Destination);

            ArrayList<Item> items = new ArrayList<Item>();

            items.add(new ItemSection("Recent", "#103909" ));
            for(int i=0; i<recent_destinations.length; i++){
                items.add(new NavigationMainScreenEntry(recent_destinations[i]));
            }
            items.add(new ItemSection("Favorites", "#38005c"));
            for(int i=0; i<favorites_destinations.length; i++){
                items.add(new NavigationMainScreenEntry(favorites_destinations[i]));
            }

            NavigationListAdapter adapter = new NavigationListAdapter(getActivity(), items);
            setListAdapter(adapter);

            mapScreen =(LinearLayout) rootView.findViewById(R.id.map_layout);
            mapScreen.setVisibility(View.INVISIBLE);

            mainScreen=(LinearLayout) rootView.findViewById(R.id.navigation_main);

            mapView =(ImageView) rootView.findViewById(R.id.map);
            mapView.setImageBitmap(Utils.roundImageCorners(getResources(), R.drawable.waze1, 20));

            final ImageButton button = (ImageButton)rootView.findViewById(R.id.new_dest);
            button.setOnTouchListener(new MicOnClick.SearchListener(button,
            new MicOnClick.MicAction() {
                @Override
                public void onMicPressed() {
                    Intent myIntent = new Intent(getActivity(), NavigationFindDestActivity.class);
                    startActivity(myIntent);
                }
            }));
            final Button cancel = (Button)rootView.findViewById(R.id.map_cancel);
            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mapScreen.setVisibility(View.INVISIBLE);
                    mainScreen.setVisibility(View.VISIBLE);
                }
            });
            return rootView;
        }

       @Override
        public void onListItemClick(ListView lst, View view, int position, long id) {
            super.onListItemClick(lst, view, position, id);
           mapScreen.setVisibility(View.VISIBLE);
           mainScreen.setVisibility(View.INVISIBLE);
        }
    }

    public static class NavigationListAdapter extends ArrayAdapter<Item> {

        private Context context;
        private ArrayList<Item> items;
        private LayoutInflater vi;

        public NavigationListAdapter(Context context,ArrayList<Item> items) {
            super(context,0, items);
            this.context = context;
            this.items = items;
            vi = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            final Item i = items.get(position);
            if (i != null) {
                if(i.isSection()){
                    ItemSection si = (ItemSection)i;
                    v = vi.inflate(R.layout.section, null);

                    v.setOnClickListener(null);
                    v.setOnLongClickListener(null);
                    v.setLongClickable(false);

                    final TextView sectionView = (TextView) v.findViewById(R.id.contact_section);
                    sectionView.setText(si.title);
                    sectionView.setTextColor(Color.parseColor(si.color));
                }else{
                    NavigationMainScreenEntry ei = (NavigationMainScreenEntry)i;
                    v = vi.inflate(R.layout.navigation_main_screen_logs, null);

                    TextView destination = (TextView)v.findViewById(android.R.id.text1);

                    if (ei!=null && destination!=null ){
                        destination.setText(ei.destination);
                    }
                    if(position % 2 == 1){
                        v.setBackgroundResource(R.drawable.bg_row2);
                    }else{
                        v.setBackgroundResource(R.drawable.bg_row);
                    }
                }
            }
            return v;
        }
    }
}
