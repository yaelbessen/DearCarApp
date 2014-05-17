package com.example.dearcar;

import android.os.Handler;
import android.os.SystemClock;
import android.widget.TextView;

/**
 * Created by Yael on 10/07/13.
 */
public class TimeCounter {

    private long startTime = 0L;
    private TextView timeLabel;
    private Handler mHandler = new Handler();
    private int start = 0;

    public TimeCounter(TextView timeText) {
        this.timeLabel = timeText;
        startTime = System.currentTimeMillis();
    }

    public void start(){
        mHandler.removeCallbacks(mUpdateTimeTask);
        mHandler.postDelayed(mUpdateTimeTask, 10);
    }

    public void end(){
        mHandler.removeCallbacks(mUpdateTimeTask);
    }

    private Runnable mUpdateTimeTask = new Runnable() {
        public void run() {
            long millis = SystemClock.uptimeMillis() - startTime;

            int seconds = ++start;
            int minutes = seconds / 60;
            seconds     = seconds % 60;

            if (seconds < 10) {
                timeLabel.setText("" + minutes + ":0" + seconds);
            } else {
                timeLabel.setText("" + minutes + ":" + seconds);
            }

            mHandler.postDelayed(this, 1000);
        }
    };
}
