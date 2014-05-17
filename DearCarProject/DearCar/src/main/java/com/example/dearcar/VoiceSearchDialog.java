package com.example.dearcar;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Yael on 05/07/13.
 */
public class VoiceSearchDialog {
    Activity myActivity;

    Dialog dialog;

    ImageView resultIcon;

    TextView resultName;
    TextView searchMessage;
    TextView resultTitle;
    TextView timer;

    Button cancelButton;
    Button retryButton;
    Button okButton;

    int timerCounter;

    boolean userPressed =false;
    boolean resultEnabled = true;

    LinearLayout searchLayout;
    LinearLayout resultLayout;

    View.OnClickListener onCancel;
    View.OnClickListener onOK;
    View.OnClickListener onRetry;

    int secondsToClose;
    int secondsToWait;

    String sound = "";

    public VoiceSearchDialog(final Activity ctx, final int secsToClose, final int secsToWait){
        myActivity = ctx;
        secondsToClose = secsToClose;
        secondsToWait = secsToWait;
        this.onOK = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        };
        this.onRetry = this.onOK;
        this.onCancel = this.onOK;

        AlertDialog.Builder builder = new AlertDialog.Builder(myActivity);
        View content = myActivity.getLayoutInflater().inflate(R.layout.generic_popup, null,false);
        builder.setView(content);

        dialog = new Dialog(myActivity);
        dialog.setContentView(content);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        dialog.setCanceledOnTouchOutside(false);

        searchLayout = (LinearLayout) content.findViewById(R.id.popup_layout_search);
        resultLayout = (LinearLayout) content.findViewById(R.id.popup_layout_result);

        resultIcon = (ImageView) content.findViewById(R.id.image_icon);
        resultName =(TextView) content.findViewById(R.id.name);
        resultTitle = (TextView) content.findViewById(R.id.result_title);

        cancelButton = (Button) content.findViewById(R.id.cancel);
        retryButton = (Button) content.findViewById(R.id.retry);
        okButton = (Button) content.findViewById(R.id.ok);
        timer =(TextView) content.findViewById(R.id.timer);
        searchMessage = (TextView) content.findViewById(R.id.search_message);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                userPressed = true;
                timerCounter = 0;
                onCancel.onClick(v);
            }
        });
        retryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultLayout.setVisibility(View.GONE);
                searchLayout.setVisibility(View.VISIBLE);
                timerCounter = secondsToClose + secondsToWait;
                onRetry.onClick(v);
                Utils.playMediaFile(v.getContext().getAssets(), "beep.wav");
            }
        });
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                userPressed = true;
                onOK.onClick(v);
            }
        });
    }

    public void show(){
        timerCounter= secondsToClose + secondsToWait;
        userPressed=false;
        new AfterTimerTask().execute();
        searchLayout.setVisibility(View.VISIBLE);
        resultLayout.setVisibility(View.GONE);
        dialog.show();
    }

    public VoiceSearchDialog setSearchMessage(String m){
        searchMessage.setText(m);
        return this;
    }

    public VoiceSearchDialog setResult(String resultName, int resultPic){
        this.resultName.setText(resultName);
        resultIcon.setImageBitmap(Utils.roundImageCornersBorder(myActivity.getResources(),
                resultPic, 30, Color.parseColor("#001931"), 5));
        return this;
    }

    public VoiceSearchDialog setOkName(String newOK){
        okButton.setText(newOK);
        return this;
    }

    public VoiceSearchDialog setResultTitle(String title){
        resultTitle.setText(title);
        return this;
    }

    public VoiceSearchDialog setOnOK(View.OnClickListener onOK){
        this.onOK = onOK;
        return this;
    }

    public VoiceSearchDialog setOnRetry(View.OnClickListener onRetry){
        this.onRetry = onRetry;
        return this;
    }

    public VoiceSearchDialog setOnCancel(View.OnClickListener onCancel){
        this.onCancel = onCancel;
        return this;
    }

    public VoiceSearchDialog disableResult(){
        resultEnabled = false;
        return this;
    }

    public VoiceSearchDialog enableResult(){
        resultEnabled = true;
        return this;
    }

    public VoiceSearchDialog setSound(String sound){
        this.sound = sound;
        return this;
    }

    public class AfterTimerTask extends AsyncTask {

        public static final int ONE_SECOND = 1000;

        @Override
        protected void onProgressUpdate(Object... values) {
            super.onProgressUpdate(values);
            if(timerCounter>=0){
                timer.setText(Integer.toString(timerCounter));
            }
            if(timerCounter == secondsToClose){
                if(resultEnabled){
                    resultLayout.setVisibility(View.VISIBLE);
                    searchLayout.setVisibility(View.GONE);
                }else{
                    timerCounter = 0;
                }
                if (!sound.equals(""))
                    Utils.playMediaFile(myActivity.getAssets(), sound);
            }
            timerCounter--;
        }
        @Override
        protected Object doInBackground(Object... params) {
            while(timerCounter>=-1) {
                try {
                    //sleep for 1s in background...
                    Thread.sleep(ONE_SECOND);
                    //and update textview in ui thread
                    publishProgress();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                };
            }
            dialog.dismiss();
            if(!userPressed){
                onOK.onClick(dialog.getCurrentFocus());
            }

            return null;
        }
    }

}
