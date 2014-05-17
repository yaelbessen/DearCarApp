package com.example.dearcar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Yael on 6/20/13.
 */
public class ContactScreen {
    /**
     * A fragment representing the mesh screen.
     */
    public static class ContactFragment extends ListFragment {

        public ContactFragment() {
        }

        VoiceSearchDialog contactDialog;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.contact_screen, container, false);

            // storing string resources into Array
            int[] pics_left = Utils.convertArray(getResources().obtainTypedArray(R.array.Contact_pic_left));
            String[] contact_name_left = getResources().getStringArray(R.array.Contact_name_left);
            String[] numbers_left = getResources().getStringArray(R.array.Contact_number_left);
            int[] pics_right = Utils.convertArray(getResources().obtainTypedArray(R.array.Contact_pic_right));
            String[] contact_name_right = getResources().getStringArray(R.array.Contact_name_right);
            String[] numbers_right = getResources().getStringArray(R.array.Contact_number_right);

            ArrayList<Item> items = new ArrayList<Item>();

            items.add(new ItemSection("Favorites", "#38005c"));
            for(int i=0; i<pics_left.length; i++){
                items.add(new ContactEntry(pics_left[i],contact_name_left[i], numbers_left[i],
                        pics_right[i],contact_name_right[i], numbers_right[i]));
            }
           items.add(2,new ItemSection("Contacts", "#000000"));

            ContactListAdapter adapter = new ContactListAdapter(getActivity(), items);
            setListAdapter(adapter);


            contactDialog = new VoiceSearchDialog(getActivity(), 3, 3)
            .setOnOK(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent callIntent = new Intent(getActivity(), CallScreen.CallActivity.class);
                    PhoneScreen.makeACall(callIntent,"Dad", "054-4635872", 5, false);
                    startActivity(callIntent);
                }
            }).setOkName("Call").setResult("Dad", R.drawable.contact5)
              .setSearchMessage("Say a contact's name...").setResultTitle("Contact found:");

            ImageButton mic = (ImageButton)rootView.findViewById(R.id.contact_voice_rec);
            mic.setOnTouchListener(new MicOnClick.SearchListener(mic,new MicOnClick.MicAction() {
                @Override
                public void onMicPressed() {
                    contactDialog.show();
                }
            }));

            return rootView;
        }

        @Override
        public void onListItemClick(ListView l, View v, int position, long id) {
        }
    }

    public static class ContactListAdapter extends ArrayAdapter<Item> {

        private Context context;
        private ArrayList<Item> items;
        private LayoutInflater vi;

        public ContactListAdapter(Context context,ArrayList<Item> items) {
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
                    final TextView sectionView = (TextView) v.findViewById(R.id.contact_section);
                    sectionView.setText(si.title);
                    sectionView.setTextColor(Color.parseColor(si.color));
                }else{
                    final ContactEntry ei = (ContactEntry)i;
                    v = vi.inflate(R.layout.contact_logs, null);


                    ImageView imgIcon_left = (ImageView)v.findViewById(R.id.imgIcon_left);
                    TextView name_left = (TextView)v.findViewById(R.id.contactName_left);

                    imgIcon_left.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent callIntent = new Intent(context, CallScreen.CallActivity.class);
                            PhoneScreen.makeACall(callIntent, ei.name_left, ei.number_left, ei.pic_left, true);
                            context.startActivity(callIntent);
                        }
                    });

                    ImageView imgIcon_right = (ImageView)v.findViewById(R.id.imgIcon_right);
                    TextView name_right=(TextView)v.findViewById(R.id.contactName_right);

                    imgIcon_right.setOnClickListener( new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent callIntent = new Intent(context, CallScreen.CallActivity.class);
                            PhoneScreen.makeACall(callIntent, ei.name_right, ei.number_right, ei.pic_right, true);
                            context.startActivity(callIntent);
                        }
                    });
                    imgIcon_left.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            Intent mIntent = new Intent(context, MessageScreen.NewMessageActivity.class);
                            mIntent.putExtra("message_dest",ei.name_left);
                            context.startActivity(mIntent);
                            Utils.vibrate(getContext(), 100);
                            return false;
                        }
                    });
                    imgIcon_right.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            Intent mIntent = new Intent(context, MessageScreen.NewMessageActivity.class);
                            mIntent.putExtra("message_dest",ei.name_right);
                            context.startActivity(mIntent);
                            Utils.vibrate(getContext(), 100);
                            return false;
                        }
                    });

                    if (ei!=null && imgIcon_left!=null ){
                        /*imgIcon_left.setImageBitmap(DearCarUtils.roundImageCornersBorder(context.getResources(),
                                ei.pic_left, 20, Color.BLACK, 2));*/
                        imgIcon_left.setImageResource(ei.pic_left);
                        name_left.setText(ei.name_left);
                        /*imgIcon_right.setImageBitmap(DearCarUtils.roundImageCornersBorder(context.getResources(),
                                ei.pic_right, 20, Color.BLACK, 2));*/
                        imgIcon_right.setImageResource(ei.pic_right);
                        name_right.setText(ei.name_right);
                    }
                }
            }
            return v;
        }


   }
}
