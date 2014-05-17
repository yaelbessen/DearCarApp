package com.example.dearcar;

import android.app.NotificationManager;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;

import java.io.IOException;

public class Utils {

    public static int[] convertArray(TypedArray ar) {
        int len = ar.length();
        int[] res = new int[len];
        for (int i = 0; i < len; i++)
            res[i] = ar.getResourceId(i, 0);
        ar.recycle();
        return res;
    }

    public static Bitmap roundImageCorners(Resources r, int imageID, int pixels){
        Bitmap pic = BitmapFactory.decodeResource(r,imageID);
        return getRoundedCornerBitmap(pic, pixels, 0, 0);
    }

    public static Bitmap roundImageCornersBorder(Resources r, int imageID, int pixels,
                                                 int borderColor, int borderWidth){
        Bitmap pic = BitmapFactory.decodeResource(r,imageID);
        return getRoundedCornerBitmap(pic, pixels, borderColor, borderWidth);
    }

    private static Bitmap getRoundedCornerBitmap(Bitmap bitmap, int pixels,
                                                int borderColor, int borderWidth) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        Paint paintStroke = new Paint();

        paintStroke.setStrokeWidth(borderWidth);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(borderColor);
        paintStroke.setAntiAlias(true);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);

        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paintStroke);

        return output;
    }

    public static void playNotification(Context ctx) {
        MainActivity.mainPlayer.stop();
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(ctx) ;
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        builder.setSound(alarmSound);
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(1, builder.build());
    }

    public static void playMediaFile(AssetManager assets, String filename){
        try {
            MainActivity.mainPlayer.reset();
            AssetFileDescriptor afd = assets.openFd(filename);
            MainActivity.mainPlayer.setDataSource(afd.getFileDescriptor(),afd.getStartOffset(),afd.getLength());
            MainActivity.mainPlayer.prepare();
            MainActivity.mainPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void vibrate(Context ctx, int milliseconds){
        ((Vibrator) ctx.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(milliseconds);
    }
}