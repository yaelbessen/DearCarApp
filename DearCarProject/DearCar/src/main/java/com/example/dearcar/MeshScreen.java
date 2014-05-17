package com.example.dearcar;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

/**
 * Created by Yael on 18/06/13.
 */
public class MeshScreen {

    /**
     * A fragment representing the mesh screen.
     */
    public static class MeshFragment extends Fragment {

        protected ViewPager pager=null;
        protected ImageButton[][] meshButtons = new ImageButton[3][2];

        public MeshFragment(){}

        public MeshFragment(ViewPager viewPager) {
            pager = viewPager;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.mesh_screen, container, false);

            meshButtons[0][0] = (ImageButton) rootView.findViewById(R.id.mesh_00);
            meshButtons[0][1] = (ImageButton) rootView.findViewById(R.id.mesh_01);
            meshButtons[1][0] = (ImageButton) rootView.findViewById(R.id.mesh_10);
            meshButtons[1][1] = (ImageButton) rootView.findViewById(R.id.mesh_11);
            meshButtons[2][0] = (ImageButton) rootView.findViewById(R.id.mesh_20);
            meshButtons[2][1] = (ImageButton) rootView.findViewById(R.id.mesh_21);

            for(ImageButton[] b : meshButtons){
                for (ImageButton button : b){
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Utils.playMediaFile(view.getContext().getAssets(), "beep.wav");
                        }
                    });
                }

            }

            return rootView;
        }

        protected class CallOnClick implements View.OnClickListener{
            String name;
            String number;
            int callerID;

            private CallOnClick(String name, String number, int callerID) {
                this.name = name;
                this.number = number;
                this.callerID = callerID;
            }

            @Override
            public void onClick(View view) {
                Intent callIntent = new Intent(getActivity(), CallScreen.CallActivity.class);
                PhoneScreen.makeACall(callIntent,name, number, callerID, false);
                Utils.playMediaFile(getActivity().getAssets(), "beep.wav");
                startActivity(callIntent);
            }
        }

        protected class MessageOnClick implements View.OnClickListener{
            String name;

            public MessageOnClick(String name) {
                this.name = name;
            }

            @Override
            public void onClick(View view) {
                Intent messageIntent = new Intent(getActivity(), MessageScreen.NewMessageActivity.class);
                messageIntent.putExtra("message_dest", name);
                startActivity(messageIntent);
            }
        }
    }

    public static class MeshFragment1 extends MeshFragment{

        public static final int CONTACTS_SCREEN = 5;

        public MeshFragment1(){}
        public MeshFragment1(ViewPager viewPager) {
            super(viewPager);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            final View rootView = super.onCreateView(inflater, container, savedInstanceState);

            meshButtons[0][0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(getActivity(), CallScreen.IncomingCallActivity.class);
                    Utils.playMediaFile(getActivity().getAssets(), "beep.wav");
                    PhoneScreen.makeACall(callIntent,"Mom", "052-4623587", 1, false);
                    startActivity(callIntent);
                }
            });
            meshButtons[0][0].setBackgroundResource(R.drawable.mesh_contact1);
            meshButtons[0][1].setOnClickListener(new CallOnClick("Ido", "050-6532124", 2));
            meshButtons[0][1].setBackgroundResource(R.drawable.mesh_contact2);
            meshButtons[1][0].setBackgroundResource(R.drawable.mesh_17);
            meshButtons[1][1].setBackgroundResource(R.drawable.mesh_app2);
            meshButtons[2][0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.playMediaFile(view.getContext().getAssets(), "beep.wav");
                    if(pager != null){
                        new Handler().post(new Runnable() {
                            @Override
                            public void run() {
                                pager.setCurrentItem(CONTACTS_SCREEN);
                            }
                        });
                    }
                }
            });
            meshButtons[2][0].setBackgroundResource(R.drawable.go_to_contacts);
            meshButtons[2][1].setBackgroundResource(R.drawable.exit);
            meshButtons[2][1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.playMediaFile(view.getContext().getAssets(), "beep.wav");
                    ((MainActivity)getActivity()).shutdown();
                }
            });

            return rootView;
        }

    }
    public static class MeshFragment2 extends MeshFragment{
        public MeshFragment2(){}
        public MeshFragment2(ViewPager viewPager) {
            super(viewPager);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = super.onCreateView(inflater, container, savedInstanceState);

            meshButtons[0][0].setBackgroundResource(R.drawable.mesh_2_06);
            meshButtons[0][0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.playMediaFile(view.getContext().getAssets(), "beep.wav");
                    NavigationMainScreen.NavigationFragment.mapScreen.setVisibility(View.VISIBLE);
                    NavigationMainScreen.NavigationFragment.mainScreen.setVisibility(View.INVISIBLE);
                    pager.setCurrentItem(pager.getPageMargin());
                }
            });
            meshButtons[0][1].setBackgroundResource(R.drawable.mesh_2_08);
            meshButtons[0][1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.playMediaFile(view.getContext().getAssets(), "beep.wav");
                    NavigationMainScreen.NavigationFragment.mapScreen.setVisibility(View.VISIBLE);
                    NavigationMainScreen.NavigationFragment.mainScreen.setVisibility(View.INVISIBLE);
                    pager.setCurrentItem(pager.getPageMargin());
                }
            });
            meshButtons[1][0].setBackgroundResource(R.drawable.mesh_2_12);
            meshButtons[1][1].setBackgroundResource(R.drawable.mesh_2_14);
            meshButtons[1][1].setOnClickListener(new MessageOnClick("Mom"));
            meshButtons[2][0].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.playMediaFile(view.getContext().getAssets(), "beep.wav");
                    if(pager != null)
                        pager.setCurrentItem(pager.getPageMargin()+1);
                }
            });
            meshButtons[2][0].setBackgroundResource(R.drawable.go_to_music);
            meshButtons[2][1].setBackgroundResource(R.drawable.mesh_2_20);
            meshButtons[2][1].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Utils.playMediaFile(view.getContext().getAssets(), "beep.wav");
                    if(MainActivity.night.getVisibility() == View.VISIBLE){
                        MainActivity.night.setVisibility(View.GONE);
                        return;
                    }
                    MainActivity.night.setVisibility(View.VISIBLE);
                }
            });

            return rootView;
        }
    }
}
