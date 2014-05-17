package com.example.dearcar;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;

/**
 * Created by Yael on 03/07/13.
 */
public class MicOnClick {

    public static interface MicAction{
        public void onMicPressed();
    }

    public static class MicOnTouchListener implements View.OnTouchListener{
        MicAction action;
        ImageButton mic;
        protected int pic = R.drawable.phone_button;
        protected int picPressed = R.drawable.phone_button_pressed;

        public MicOnTouchListener(MicAction onClickAction, ImageButton micButton){
            action = onClickAction;
            mic = micButton;
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            switch (motionEvent.getAction()){
                case MotionEvent.ACTION_DOWN:
//                    mic.setImageResource(picPressed);
                    break;
                case MotionEvent.ACTION_UP:
//                    mic.setImageResource(pic);
                    MainActivity.mainPlayer.stop();
                    action.onMicPressed();

                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(view.getContext()) ;
                    Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                    builder.setSound(alarmSound);
                    NotificationManager manager = (NotificationManager) view.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(1, builder.build());
            }
            return false;
        }
    }

    public static class SearchListener extends MicOnTouchListener{
        public SearchListener(ImageButton button, MicAction onClick){
            super(onClick,button);
            pic = R.drawable.search_button;
            picPressed = R.drawable.search_button_pressed;
        }
    }
}
