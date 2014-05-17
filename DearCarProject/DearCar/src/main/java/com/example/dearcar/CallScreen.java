package com.example.dearcar;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.dearcar.AnswerPadController.PadControls;

import java.util.Timer;
import java.util.TimerTask;

public class CallScreen {


    static public class IncomingCallActivity extends FragmentActivity implements PadControls {
        RelativeLayout controllerLayout;
        RelativeLayout panelLayout;
        GridLayout controlsLayout;
        protected TimeCounter timer;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.incoming_call_screen);
            Bundle b = getIntent().getExtras();
            if(b == null){
                return;
            }

            CallEntry call = new CallEntry(b.getString("caller"),
                    b.getString("number"),
                    b.getInt("caller_pic"));

            //Set the caller parameters on screen
            ImageView callerPic = (ImageView) findViewById(R.id.answer_caller_pic);
            TextView callerName = (TextView) findViewById(R.id.caller_id);
            TextView callerNum = (TextView) findViewById(R.id.caller_number);
            TextView callTime = (TextView) findViewById(R.id.call_time);
            if( call.number.length() >0 ){
                if(b.getBoolean("is_pic_resource")){
                    callerPic.setImageResource(call.pic);
                }else{
                    int pic = getResources().getIdentifier("contact"+call.pic,"drawable", getPackageName());
                    callerPic.setImageResource(pic);
                }
            }
            callerName.setText(call.caller);
            callerNum.setText(call.number);
            timer = new TimeCounter(callTime);

            ImageView pad = (ImageView) findViewById(R.id.answer_panel);
            panelLayout = (RelativeLayout) findViewById(R.id.answer_panel_layout);
            controllerLayout = (RelativeLayout)findViewById(R.id.answer_panel_overlay);
            controlsLayout = (GridLayout) findViewById(R.id.blabla);
            Button controller = (Button) findViewById(R.id.answer_button);

            controller.setDrawingCacheEnabled(true);
            controller.setOnTouchListener(new AnswerPadController(controller, pad,
                    this, controllerLayout, this, R.drawable.answer_button_on
                    , new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    return false;
                }
            }));

            controlsLayout.setVisibility(View.INVISIBLE);
            final Button endButton = (Button)findViewById(R.id.end_button);
            endButton.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    switch ((motionEvent.getAction())) {
                        case MotionEvent.ACTION_DOWN:
                            endButton.setBackgroundResource(R.drawable.answered_08);
                            break;
                        case MotionEvent.ACTION_UP:
                            endButton.setBackgroundResource(R.drawable.answered_09);
                            endCall();
                            break;
                    }
                    return true;
                }
            });

            final LinearLayout inputSwitch = (LinearLayout) findViewById(R.id.input_switch);
            ((Button) findViewById(R.id.switch_bt)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputSwitch.setBackgroundResource(R.drawable.answered_03);
                    //Todo: Turn bluetooth on and speaker off
                }
            });

            ((Button) findViewById(R.id.switch_SPK)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    inputSwitch.setBackgroundResource(R.drawable.answered_04);
                    //Todo: Turn bluetooth off and speaker on
                }
            });

            //Turn off any music:
            MainActivity.mainPlayer.stop();
        }

        @Override
        public void left() {
            //Answer
            panelLayout.setVisibility(View.GONE);
            controllerLayout.setVisibility(View.GONE);
            controlsLayout.setVisibility(View.VISIBLE);
            timer.start();
        }

        @Override
        public void right() {
            //Decline
            controllerLayout.setVisibility(View.INVISIBLE);
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {}
            //Todo: insert voice output.
            finish();
        }

        @Override
        public void top() {
            Utils.playMediaFile(getAssets(), "default.mp3");
            finish();
        }

        @Override
        public void bottom() {
            Utils.playMediaFile(getAssets(), "silenced.mp3");
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    finish();
                }
            }, 5000);
        }

        private void endCall(){
            timer.end();
            finish();
        }

    }

    static public class CallActivity extends IncomingCallActivity{
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //Immediate answer
            super.left();
        }
    }
}
