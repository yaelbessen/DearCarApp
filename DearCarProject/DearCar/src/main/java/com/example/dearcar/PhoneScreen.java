package com.example.dearcar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class PhoneScreen {
    /**
     * A fragment representing the mesh screen.
     */

    public static class PhoneFragment extends ListFragment {
        VoiceSearchDialog callDialog;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.phone_screen, container, false);

            // storing string resources into Array
            final String[] callers = getResources().getStringArray(R.array.Recent_Calls);
            String[] numbers = getResources().getStringArray(R.array.Recent_Calls_numbers);
            int[] pics = Utils.convertArray(getResources().obtainTypedArray(R.array.Recent_Calls_pics));
            CallEntry[] calls = new CallEntry[callers.length];
            for(int i=0; i<calls.length; i++){
                calls[i] = new CallEntry(callers[i],numbers[i],pics[i]);
            }
            // Binding resources Array to ListAdapter
            setListAdapter(new CallsListAdapter(getActivity(), R.layout.call_logs, calls));

            callDialog = new VoiceSearchDialog(getActivity(), 3, 3)
            .setOnOK(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent call = new Intent(getActivity(), CallScreen.CallActivity.class);
                    makeACall(call,"Dad","054-4635872",R.drawable.contact5,true);
                    startActivity(call);
                }
            }).setSearchMessage("Say a contact's name or a phone number...")
            .setResult("Dad",R.drawable.contact5)
            .setOkName("Call").setResultTitle("Contact found:").setSound("contactfound.mp3");

            ImageButton newCall = (ImageButton) rootView.findViewById(R.id.imageButton);
            newCall.setOnTouchListener(new MicOnClick.MicOnTouchListener(new MicOnClick.MicAction() {
                @Override
                public void onMicPressed() {
                    callDialog.show();
                }
            }, newCall));

            ListView list=(ListView) rootView.findViewById(android.R.id.list);
            list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent mIntent = new Intent(getActivity(), MessageScreen.NewMessageActivity.class);
                    mIntent.putExtra("message_dest",callers[i]);
                    Utils.vibrate(getActivity(), 100);
                    startActivity(mIntent);
                    return false;
                }
            });

            return rootView;
        }


        @Override
        public void onListItemClick(ListView lst, View view, int position, long id) {
            super.onListItemClick(lst, view, position, id);
            TextView name = (TextView) (view.findViewById(R.id.call_item));
            TextView number = (TextView) (view.findViewById(R.id.call_item_sub));

            Intent callIntent = new Intent(getActivity(), CallScreen.CallActivity.class);
            makeACall(callIntent, name.getText().toString(),number.getText().toString(), position+1,
                    false);
            startActivity(callIntent);
        }
    }

    public static void makeACall(Intent callIntent, String name, String number, int picID,
                                 boolean isPicResource){
        Bundle callEntry = new Bundle();
        callEntry.putString("caller", name);
        callEntry.putString("number", number);
        callEntry.putInt("caller_pic", picID);
        callEntry.putBoolean("is_pic_resource", isPicResource);
        callIntent.putExtras(callEntry);
    }

    static class CallsListAdapter extends ArrayAdapter<CallEntry>{

        Context context;
        int layoutResourceId;
        CallEntry data[] = null;

        public CallsListAdapter(Context context, int layoutResourceId, CallEntry[] data) {
            super(context, layoutResourceId, data);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.data = data;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            EntryHolder holder;

            if(row == null){
                LayoutInflater inflater = ((Activity)context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);

                holder = new EntryHolder();
                holder.imgIcon = (ImageView)row.findViewById(R.id.imgIcon);
                holder.txtTitle = (TextView)row.findViewById(R.id.call_item);
                holder.subTxt = (TextView)row.findViewById(R.id.call_item_sub);
                holder.layout = (LinearLayout)row.findViewById(R.id.call_entry_layout);

                row.setTag(holder);
            }
            else{
                holder = (EntryHolder)row.getTag();
            }

            CallEntry entry = data[position];
            holder.txtTitle.setText(entry.caller);
            holder.subTxt.setText(entry.number);
            holder.imgIcon.setImageResource(entry.pic);
            holder.imgIcon.setImageBitmap(Utils.roundImageCornersBorder(context.getResources(),
                    entry.pic, 20, Color.BLACK, 1));
            if(position % 2 != 0){
                holder.layout.setBackgroundResource(R.drawable.bg_row2);
            }else {
                holder.layout.setBackgroundResource(R.drawable.bg_row);
            }
            return row;
        }
    }

    static class EntryHolder {
        ImageView imgIcon;
        TextView txtTitle;
        TextView subTxt;
        LinearLayout layout;
    }
}