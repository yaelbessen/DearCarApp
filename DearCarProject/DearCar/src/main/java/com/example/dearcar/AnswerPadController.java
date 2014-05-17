package com.example.dearcar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

/**
 * Created by Yael on 24/06/13.
 */
class AnswerPadController implements View.OnTouchListener{

    static public interface PadControls{
        public void left();
        public void right();
        public void top();
        public void bottom();
    }

    private final static int START_DRAGGING = 0;
    private final static int STOP_DRAGGING = 1;

    private Button controller;
    private RelativeLayout layout;
    private int status;

    private ImageView image;
    private ImageView pad;
    private Bitmap buttonOnBg;

    PadControls funcs;

    private CtrlButton[] mButtons = new CtrlButton[4];
    private CtrlDirection lastDir = CtrlDirection.NONE;

    public static enum CtrlDirection{
        LEFT, RIGHT, TOP, BOTTOM, NONE;
    }

    private boolean isDragging = false;
    private FragmentActivity ctx;
    private View.OnTouchListener onTouch;

    public AnswerPadController(Button answerController, ImageView answerPad,
                               FragmentActivity context, RelativeLayout parent,
                               PadControls ctrls, int answerButtonPic, View.OnTouchListener onTouch){
        controller = answerController;
        pad = answerPad;
        ctx = context;
        layout = parent;
        funcs = ctrls;
        this.onTouch = onTouch;

        controller = (Button) ctx.findViewById(R.id.answer_button);
        controller.setOnTouchListener(this);
        pad = (ImageView) ctx.findViewById(R.id.answer_panel);

        buttonOnBg = BitmapFactory.decodeResource(ctx.getResources(), answerButtonPic);

        mButtons[0] = new CtrlButton((ImageView)ctx.findViewById(R.id.answer_panel_left));
        mButtons[1] = new CtrlButton((ImageView)ctx.findViewById(R.id.answer_panel_right));
        mButtons[2] = new CtrlButton((ImageView)ctx.findViewById(R.id.answer_panel_top));
        mButtons[3] = new CtrlButton((ImageView)ctx.findViewById(R.id.answer_panel_bottom));

    }

    @Override
    public boolean onTouch(View view, MotionEvent me) {
        onTouch.onTouch(view,me);
        if (me.getAction() == MotionEvent.ACTION_DOWN) {
            status = START_DRAGGING;
            image = new ImageView(ctx);
            image.setImageBitmap(buttonOnBg);
            layout.addView(image, controller.getLayoutParams());
            controller.setVisibility(View.INVISIBLE);

            ((Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(50);

            ((Button) ctx.findViewById(R.id.answer_left)).getHitRect(mButtons[0].bounds);
            ((Button) ctx.findViewById(R.id.answer_right)).getHitRect(mButtons[1].bounds);
            ((Button) ctx.findViewById(R.id.answer_top)).getHitRect(mButtons[2].bounds);
            ((Button) ctx.findViewById(R.id.answer_bottom)).getHitRect(mButtons[3].bounds);
        }
        if (me.getAction() == MotionEvent.ACTION_UP) {
            status = STOP_DRAGGING;
            layout.removeView(image);
            controller.setVisibility(View.VISIBLE);
            disableAll();
            dispatch();
        } else if (me.getAction() == MotionEvent.ACTION_MOVE) {
            if (status == START_DRAGGING) {
                double radius = Math.sqrt(Math.pow(controller.getWidth(),2) + Math.pow(controller.getHeight(),2))/2;
                double dx = radius * Math.sin(45) +10;
                double dy = radius * Math.cos(45) +10;
                float nextX = controller.getX() + me.getX() - (float)dx;
                float nextY = controller.getY() + me.getY() - (float)dy;
                if(!isInBounds(nextX, nextY)){
                    return false;
                }
                image.setX(nextX);
                image.setY(nextY);
                image.invalidate();
                turnOnButtons(controller.getX() + me.getX(), controller.getY() + me.getY());
            }
        }

        return false;
    }

    private boolean isInBounds(float x, float y){
        Rect rec = new Rect();
        pad.getDrawingRect(rec);
        rec.set(rec.left - 30, rec.top - 30, rec.right, rec.bottom);
        return rec.contains((int)x,(int)y);
    }

    private void turnOnButtons(float x, float y){
        CtrlDirection dir = choice(x,y);
        if(dir != lastDir){
            lastDir = dir;
            if(dir != CtrlDirection.NONE){
                ((Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(200);
            }
        }
        disableAll();
        switch (dir){
            case LEFT:  mButtons[0].enable();
                break;
            case RIGHT: mButtons[1].enable();
                break;
            case TOP:   mButtons[2].enable();
                break;
            case BOTTOM: mButtons[3].enable();
                break;
            default:
        }
    }

    private CtrlDirection choice(float pos_x, float pos_y){
        int x = (int) pos_x;
        int y = (int) pos_y;
        if(mButtons[0].contains(x,y)){
            return CtrlDirection.LEFT;
        }
        if(mButtons[1].contains(x, y)){
            return CtrlDirection.RIGHT;
        }
        if(mButtons[2].contains(x, y)){
            return CtrlDirection.TOP;
        }
        if(mButtons[3].contains(x, y)){
            return CtrlDirection.BOTTOM;
        }
        return CtrlDirection.NONE;
    }

    private void dispatch(){
        switch (lastDir){
            case LEFT:  funcs.left();
                break;
            case RIGHT: funcs.right();
                break;
            case TOP:   funcs.top();
                break;
            case BOTTOM: funcs.bottom();
                break;
            default:
        }
    }
    private void disableAll(){
        for(CtrlButton b : mButtons){
            b.disable();
        }
    }

    private class CtrlButton{
        Rect bounds;
        ImageView bg;

        public CtrlButton(ImageView view){
            bounds = new Rect();
            bg = view;
            disable();
        }

        public void disable(){
            bg.setVisibility(View.INVISIBLE);
        }

        public void enable(){
            bg.setVisibility(View.VISIBLE);
        }

        public boolean contains(int x, int y){
            return bounds.contains(x,y);
        }
    }
}
