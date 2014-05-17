package com.example.dearcar;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Dialog;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Yael on 6/11/13.
 */
public class MessageScreen{
    public static class MessageFragment extends ListFragment {

        public static final String HEAR_YOUR_MESSAGES = "Hear messages from:";
        private ImageButton spkReplay;
        private TextView senderName;
        private ImageView senderPic;
        private TextView timer;
        private ProgressBar spinner;
        private TextView header;
        int counterReading;
        String messageReading;

        Dialog readingMessageDialog;
        VoiceSearchDialog newMessageDialog;

        MessageListAdapter list;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.message_screen, container, false);

            // storing string resources into Array
            String[] message_senders = getResources().getStringArray(R.array.Unread_messages);
            int[] pics = convertArray(getResources().obtainTypedArray(R.array.Unread_messages_pics));
            int[] num_of_unread_messages = getResources().getIntArray(R.array.Num_Of_Unread_messages);
            String[] message_sound = getResources().getStringArray(R.array.Unread_messages_sound);
            ArrayList<MessageEntry> messages=new ArrayList<MessageEntry>();
            for(int i=0; i<num_of_unread_messages.length; i++){
                messages.add(new MessageEntry(message_senders[i],pics[i],num_of_unread_messages[i], message_sound[i]));
            }
            list=new MessageListAdapter(getActivity(), R.layout.message_logs, messages);
            // Binding resources Array to ListAdapter
            setListAdapter(list);

            newMessageDialog = new VoiceSearchDialog(getActivity(), 3, 3)
            .setOnOK(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent newMessage = new Intent(getActivity(), NewMessageActivity.class);
                    newMessage.putExtra("message_dest","Dad");
                    startActivity(newMessage);
                }
            }).setSound("contactfound.mp3")
            .setSearchMessage("Say a contact's name").setResult("Dad", R.drawable.contact5)
            .setOkName("Compose").setResultTitle("Contact found:");

            final ImageButton new_message = (ImageButton) rootView.findViewById(R.id.New_Message);
            new_message.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    newMessageDialog.show();
                    MainActivity.mainPlayer.stop();
                    Utils.playNotification(getActivity());
                }
            });

            readingMessageDialog = new Dialog(getActivity());
            readingMessageDialog.setContentView(R.layout.message_popup);
            readingMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            readingMessageDialog.setCanceledOnTouchOutside(false);

            Button close = (Button)readingMessageDialog.getWindow().findViewById(R.id.close);
            spkReplay = (ImageButton) readingMessageDialog.getWindow().findViewById(R.id.message_speaker);
            Button reply = (Button)readingMessageDialog.getWindow().findViewById(R.id.reply);
            senderPic = (ImageView) readingMessageDialog.getWindow().findViewById(R.id.image_icon);
            senderName = (TextView) readingMessageDialog.getWindow().findViewById(R.id.name);
            timer = (TextView) readingMessageDialog.getWindow().findViewById(R.id.timer);
            spinner = (ProgressBar) readingMessageDialog.getWindow().findViewById(R.id.timer_spinner);
            header = (TextView) readingMessageDialog.getWindow().findViewById(R.id.result_title);

            close.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    readingMessageDialog.dismiss();
                }
            });

            spkReplay.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    resetReadMessageDialog();
                    Utils.playMediaFile(getActivity().getAssets(), messageReading);
                }
            });

            reply.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    readingMessageDialog.dismiss();
                    counterReading = 0;
                    Intent newMessage = new Intent(getActivity(), NewMessageActivity.class);
                    newMessage.putExtra("message_dest",senderName.getText().toString());
                    startActivity(newMessage);
                }
            });

            return rootView;

        }

        private void resetReadMessageDialog() {
            spkReplay.setImageResource(R.drawable.speaker);
            spkReplay.setEnabled(false);
            timer.setVisibility(View.GONE);
            spinner.setVisibility(View.GONE);
            header.setText(HEAR_YOUR_MESSAGES);
            counterReading=6;
            new RefreshTaskReadingMessage().execute();
        }

        @Override
        public void onListItemClick(ListView lst, View view, int position, long id) {
            super.onListItemClick(lst, view, position, id);
            MessageEntry entry = list.data.get(position);

            list.data.remove(position);
            list=new MessageListAdapter(getActivity(), R.layout.message_logs, list.data);
            setListAdapter(list);

            resetReadMessageDialog();
            senderName.setText(entry.caller);
            senderPic.setImageResource(entry.pic);
            messageReading = entry.sound;
            readingMessageDialog.show();

            Utils.playNotification(getActivity());
            Utils.playMediaFile(getActivity().getAssets(), messageReading);
        }

        private int[] convertArray(TypedArray ar){
            int len = ar.length();
            int[] res = new int[len];
            for (int i = 0; i < len; i++)
                res[i] = ar.getResourceId(i, 0);
            ar.recycle();
            return res;
        }
        public  class RefreshTaskReadingMessage extends AsyncTask {
            @Override
            protected void onProgressUpdate(Object... values) {
                super.onProgressUpdate(values);
                if(counterReading>=0){
                    timer.setText(Integer.toString(counterReading));
                }
                if(counterReading==4){
                    spkReplay.setImageResource(R.drawable.replay);
                    spkReplay.setEnabled(true);
                    timer.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.VISIBLE);
                }
                counterReading--;
            }
            @Override
            protected Object doInBackground(Object... params) {

                while(counterReading>=-1) {
                    try {
                        //sleep for 1s in background...
                        Thread.sleep(1000);
                        //and update textview in ui thread
                        publishProgress();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    };
                }
                readingMessageDialog.dismiss();
                return null;
            }
        }

        public static class MessageListAdapter extends ArrayAdapter<MessageEntry> {

            Context context;
            int layoutResourceId;
            public ArrayList<MessageEntry> data=new ArrayList<MessageEntry>();

            public MessageListAdapter(Context context, int layoutResourceId,  ArrayList<MessageEntry> data) {
                super(context, layoutResourceId, data);
                this.layoutResourceId = layoutResourceId;
                this.context = context;
                this.data.addAll(data);
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
                    holder.txtTitle = (TextView)row.findViewById(R.id.message_item);
                    holder.layout = (LinearLayout)row.findViewById(R.id.message_entry_layout);
                    holder.numIcon=(TextView)row.findViewById(R.id.Unread_messages_num);

                    row.setTag(holder);
                }
                else{
                    holder = (EntryHolder)row.getTag();
                }
                if(position<data.size()){
                    MessageEntry entry = data.get(position);
                    holder.txtTitle.setText(entry.caller);
                    holder.imgIcon.setImageBitmap(Utils.roundImageCornersBorder(context.getResources(),
                            entry.pic, 20, Color.BLACK, 2));
                    holder.numIcon.setText(Integer.toString(entry.num_of_unread_messages));
                    if(position % 2 == 1){
                        holder.layout.setBackgroundResource(R.drawable.bg_row2);
                    }else{
                        holder.layout.setBackgroundResource(R.drawable.bg_row);
                    }
                }
                return row;
            }
        }

        static class EntryHolder {
            ImageView imgIcon;
            TextView txtTitle;
            LinearLayout layout;
            TextView numIcon;
        }
    }

    public static class NewMessageActivity extends FragmentActivity implements AnswerPadController.PadControls{
        public static final String INITIAL_MESSAGE = "Start saying your message...";
        RelativeLayout controllerLayout;
        Dialog readMessageDialog;
        int counterReadingMSoFar;
        SpeechRecognizer speechRecognizer;
        private ImageView micIndicator;
        private TextView messageText;
        private boolean isRecording = false;
        private ImageView overlay;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.new_message);

            Bundle b = getIntent().getExtras();
            if(b == null){
                return;
            }

            //Set the contact parameters on screen
            TextView sendTo = (TextView) findViewById(R.id.message_dest);
            sendTo.setText(b.getString("message_dest"));

            ImageView pad = (ImageView) findViewById(R.id.answer_panel);
            controllerLayout = (RelativeLayout)findViewById(R.id.answer_panel_layout);
            Button controller = (Button) findViewById(R.id.answer_button);
            overlay = (ImageView) findViewById(R.id.answer_panel_icons);

            micIndicator = (ImageView) findViewById(R.id.mic_volume_indicator);
            messageText = (TextView) findViewById(R.id.message_content);

            controller.setDrawingCacheEnabled(true);
            controller.setOnTouchListener(new AnswerPadController(controller, pad,
                    this, controllerLayout, this, R.drawable.mic_button_on, new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_UP){
                        micIndicator.setVisibility(View.VISIBLE);
                    }else{
                        micIndicator.setVisibility(View.INVISIBLE);
                    }
                    return false;
                }
            }));

            //Set up voice recognition
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new VoiceListener());

            startVoiceRecognition();
        }

        private void resetSpeechRecognition() {
            speechRecognizer.cancel();
            speechRecognizer.destroy();
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new VoiceListener());
        }

        @Override
        public void left() {
            Toast.makeText(this,"Your message has been sent!",Toast.LENGTH_SHORT).show();
            Utils.playMediaFile(getAssets(), "message_sent.mp3");
            finish();
        }

        @Override
        public void right() {
            if(!isRecording){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) { }
                speechRecognizer.cancel();
                speechRecognizer.destroy();
                finish();
                return;
            }
            isRecording = false;
            messageText.setText("");
            overlay.setImageResource(R.drawable.answer_panel_cancel_message);
            //Todo: block other buttons
        }

        @Override
        public void top() {
            String content = messageText.getText().toString();
            if(!content.contains(" ")){
                messageText.setText("");
                return;
            }
            messageText.setText(content.substring(0, content.lastIndexOf(" ")));
        }

        @Override
        public void bottom() {
            counterReadingMSoFar=2;
            readMessageDialog =new Dialog(this);
            readMessageDialog.setContentView(R.layout.reading_message_popup);
            readMessageDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
            readMessageDialog.setCanceledOnTouchOutside(false);
            readMessageDialog.getWindow().findViewById(R.id.speaker).setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    readMessageDialog.dismiss();
                }
            });
            readMessageDialog.show();
            new RefreshTaskReadingMessage().execute();

        }

        private TextView mText;

        class VoiceListener implements RecognitionListener{
            public void onReadyForSpeech(Bundle params){ }
            public void onBeginningOfSpeech(){
//                Toast.makeText(NewMessageActivity.this, "Beginning speech", Toast.LENGTH_SHORT).show();
                beginningOfSpeech();
            }
            public void onRmsChanged(float rmsdB){
                double volume = 10*Math.pow(10, ((double)rmsdB/(double)10));
                int size = 130 + (int) (volume);

                if(volume < 10){
                    size = 0;
                }

                RelativeLayout.LayoutParams newSize = new RelativeLayout.LayoutParams(size, size);
                newSize.addRule(RelativeLayout.CENTER_IN_PARENT, -1);

                micIndicator.setLayoutParams(newSize);
            }
            public void onBufferReceived(byte[] buffer){ }
            public void onEndOfSpeech(){ }
            public void onError(int error){
//                Utils.playMediaFile(getAssets(), "beep.wav");
                resetSpeechRecognition();
                startVoiceRecognition();
            }
            public void onResults(Bundle results){
                ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                String str = messageText.getText().toString() + " " + data.get(0);
                messageText.setText(str);
                overlay.setImageResource(R.drawable.answer_panel_messages);
                isRecording=true;
                resetSpeechRecognition();
                startVoiceRecognition();
            }

            public void onPartialResults(Bundle partialResults){ }
            public void onEvent(int eventType, Bundle params){
                resetSpeechRecognition();
                startVoiceRecognition();
            }
        }

        private void beginningOfSpeech() {
            if(messageText.getText().toString().equals(INITIAL_MESSAGE)){
                messageText.setText("");
                messageText.setTextColor(Color.BLACK);
                isRecording = true;
            }
        }

        public void startVoiceRecognition() {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE,"voice.recognition.test");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS,3);
            speechRecognizer.startListening(intent);
        }

        public  class RefreshTaskReadingMessage extends AsyncTask {
            @Override
            protected void onProgressUpdate(Object... values) {
                super.onProgressUpdate(values);
                counterReadingMSoFar--;
            }
            @Override
            protected Object doInBackground(Object... params) {

                while(counterReadingMSoFar>=-1) {
                    try {
                        //sleep for 1s in background...
                        Thread.sleep(1000);
                        //and update textview in ui thread
                        publishProgress();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    };
                }
                readMessageDialog.dismiss();
                return null;
            }
        }

    }
}