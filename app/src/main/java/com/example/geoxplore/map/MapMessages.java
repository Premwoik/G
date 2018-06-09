package com.example.geoxplore.map;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.transition.Slide;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.geoxplore.R;


/**
 * Created by prw on 09.06.18.
 */

public class MapMessages {
    private LinearLayout textLayout;
    private Context context;
    private Resources resources;
    private AssetManager assetManager;
    private Typeface mainFont;

    public MapMessages(LinearLayout textLayout, Context context, Resources resources, AssetManager assetManager) {
        textLayout.setBackgroundColor(resources.getColor(R.color.colorPrimary));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayout.setLayoutParams(params);
        textLayout.setOrientation(LinearLayout.VERTICAL);

        mainFont = Typeface.createFromAsset(assetManager,"main.ttf");

        this.textLayout = textLayout;
        this.context = context;
        this.resources = resources;
        this.assetManager= assetManager;

    }


    public void clearMessages(){
        textLayout.removeAllViews();
    }

    public void clearMessage(View view){
        textLayout.removeView(view);
    }


    public void displaySetHomeMessage(){
        displayMessage("Click on the map to set home position!");
    }


    public void displayTooFarFromBoxMessage(){
        displayMessage("You are to far from this chest", 1500);

    }

    public void displayBoxAlreadyOpenMessage(){
        displayMessage("You've already opened that chest!", 1500);

    }

    public void displayMessage(String text, int time) {
        View v = displayMessage(text);
        clearAfterTime(time, v);
    }

    public View displayMessage(String text) {

        return displayMessage(text,20, mainFont);
    }

    public View displayMessage(String text, int size, Typeface typeface){
        return displayMessage(text,size,typeface,20,20);
    }

    public View displayMessage(String text, int size, Typeface typeface, int paddingTop, int paddingBot){
        TextView b = new TextView(context);
        b.setText(text);
        b.setTextSize(size);
        b.setTypeface(typeface);
        b.setPadding(0, paddingTop, 0, paddingBot);
        b.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL);
        b.setLayoutParams(params);
        Transition transition = new Slide(Gravity.TOP);
        TransitionManager.beginDelayedTransition(textLayout, transition);
        textLayout.addView(b);
        return b;
    }


    private void clearAfterTime(Integer time, View view){
        if (time != null){
            final Handler handler = new Handler();
            handler.postDelayed(() -> clearMessage(view), time);
        }
    }



}
