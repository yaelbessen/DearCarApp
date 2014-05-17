package com.example.dearcar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


import android.bluetooth.BluetoothAdapter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {

    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link android.support.v4.app.FragmentPagerAdapter} derivative, which
     * will keep every loaded fragment in memory. If this becomes too memory
     * intensive, it may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    MyPagerAdapter pagerAdapter;

    /**
     * The {@link android.support.v4.view.ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;
    private boolean activitySwitchFlag = false;
    static public BluetoothAdapter mBluetoothAdapter;
    static public MediaPlayer mainPlayer;
    static public ImageView night;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_scroller);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);

        // Create the adapter that will return a fragment for each primary screen
        pagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), 2);
        mViewPager.setAdapter((FragmentPagerAdapter)pagerAdapter);
        mViewPager.setOffscreenPageLimit(7);
        mViewPager.setCurrentItem(2 + pagerAdapter.getSize()*100,false);

        //Night mode on/off according to hour:
        night = (ImageView) findViewById(R.id.night_mode);
        if(isNight()){
            night.setVisibility(View.VISIBLE);
        }else{
            night.setVisibility(View.GONE);
        }

        mainPlayer = new MediaPlayer();

        //Turn on bluetooth
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!mBluetoothAdapter.isEnabled()) {
            Toast.makeText(this, "Bluetooth turned on.", Toast.LENGTH_SHORT).show();
            Utils.playMediaFile(getAssets(), "bluetooth_on.mp3");
            mBluetoothAdapter.enable();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if(keyCode == KeyEvent.KEYCODE_HOME)
        {
            activitySwitchFlag = true;
            // activity switch stuff..
            return true;
        }
        return false;
    }

    void shutdown(){
        mainPlayer.stop();
        if(mBluetoothAdapter.isEnabled()){
            Toast.makeText(this, "Bluetooth turned off.", Toast.LENGTH_SHORT).show();
            Utils.playMediaFile(getAssets(), "bluetooth_off.mp3");
            mBluetoothAdapter.disable();
        }
        finish();
    }

    private boolean isNight(){
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        return hour > 18 || hour < 6;
    }

    @Override
    public void onBackPressed() { //disable back button in this activity
        return;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        return true;
    }

    private float getBatteryLevel(){
        //TODO: Implement later if you have the power and will...

        return (float)0.5;
    }
    
    /**
     * A {@link android.support.v4.app.FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter implements MyPagerAdapter{

        private List<Fragment> screens = new ArrayList<Fragment>();

        public SectionsPagerAdapter(FragmentManager fm, int meshCount) {
            super(fm);
            screens.add(new NavigationMainScreen.NavigationFragment());
            screens.add(new MusicScreen.MusicFragment());
            screens.add(new MeshScreen.MeshFragment1(mViewPager));
            screens.add(new MeshScreen.MeshFragment2(mViewPager));
            screens.add(new PhoneScreen.PhoneFragment());
            screens.add(new ContactScreen.ContactFragment());
            screens.add(new MessageScreen.MessageFragment());
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return screens.get(position);
        }

        @Override
        public int getCount() {
            return screens.size();
        }

        @Override
        public int getSize(){
            return 0;
        }
    }

    public class InfinitePagerAdapter extends FragmentPagerAdapter implements MyPagerAdapter {

        private List<Fragment> screens = new ArrayList<Fragment>();

        public InfinitePagerAdapter(FragmentManager fm, int meshCount) {
            super(fm);
            screens.add(new NavigationMainScreen.NavigationFragment());
            screens.add(new MusicScreen.MusicFragment());
            screens.add(new MeshScreen.MeshFragment1(mViewPager));
            screens.add(new MeshScreen.MeshFragment2(mViewPager));
            screens.add(new PhoneScreen.PhoneFragment());
            screens.add(new ContactScreen.ContactFragment());
            screens.add(new MessageScreen.MessageFragment());
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return screens.get(position % screens.size());
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            return super.instantiateItem(container, position % screens.size());
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            int virtualPosition = position % screens.size();
            super.destroyItem(container, virtualPosition, object);
        }

        @Override
        public int getCount() {
            return Integer.MAX_VALUE;
        }

        @Override
        public int getSize(){
            return screens.size();
        }
    }

    private interface MyPagerAdapter{
        public int getSize();
    }
}
