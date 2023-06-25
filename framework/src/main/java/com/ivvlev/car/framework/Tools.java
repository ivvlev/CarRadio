package com.ivvlev.car.framework;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v7.app.AlertDialog;
import android.text.TextPaint;
import android.util.Log;

public class Tools {

    public static void switchToHome(Activity a) {
        try {
            final Intent intent = new Intent("android.intent.action.MAIN");
            intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            intent.addCategory("android.intent.category.HOME");
            a.startActivity(intent);
            //this.condensed = true;
        } catch (Exception ex) {
            Log.e("RadioActivity", Log.getStackTraceString((Throwable) ex));
        }
    }

    public static boolean switchToEqualizer(Activity a) {
        try {
            Intent intent = new Intent();
            intent.setClassName("com.tw.eq", "com.tw.eq.EQActivity");
            intent.setFlags(Intent.FLAG_RECEIVER_FOREGROUND);
            a.startActivity(intent);
            return true;
            //this.condensed = true;
        } catch (Exception ex) {
            Log.e("RadioActivity", Log.getStackTraceString((Throwable) ex));
            new AlertDialog.Builder(a).setMessage(ex.getMessage()).create().show();
            return false;
        }
    }

    public static Bitmap createTextBitmap(final String text, final Typeface typeface, final float textSizePixels, final int textColour) {
        final TextPaint textPaint = new TextPaint();
        textPaint.setTypeface(typeface);
        textPaint.setTextSize(textSizePixels);
        textPaint.setAntiAlias(true);
        textPaint.setSubpixelText(true);
        textPaint.setColor(textColour);
        textPaint.setTextAlign(Paint.Align.LEFT);

        Bitmap myBitmap = Bitmap.createBitmap((int) textPaint.measureText(text), (int) textSizePixels, Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(myBitmap);
        myCanvas.drawText(text, 0, myBitmap.getHeight(), textPaint);

        return myBitmap;
    }

    public static boolean isEmptyString(String str) {
        final int len = str.length();
        boolean empty = true;
        if (str != null)
            for (int i = 0; i < len; i++) {
                if (str.charAt(i) != ' ') {
                    empty = false;
                    break;
                }
            }
        else
            empty = false;
        return empty;
    }

}
