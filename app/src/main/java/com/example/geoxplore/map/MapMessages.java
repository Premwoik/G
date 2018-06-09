package com.example.geoxplore.utils;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Handler;
import android.transition.Fade;
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

import java.util.ArrayList;
import java.util.List;

/**
 * Created by prw on 09.06.18.
 */

public class MapMessages {
    private LinearLayout textLayout;
    private Context context;
    private Resources resources;

    public MapMessages(LinearLayout textLayout, Context context, Resources resources) {
        textLayout.setBackgroundColor(resources.getColor(R.color.colorPrimary));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textLayout.setLayoutParams(params);

        this.textLayout = textLayout;
        this.context = context;
        this.resources = resources;
    }


    public void clearMessages(){
        textLayout.removeAllViews();
    }

    public void displaySetHomeMessage(){
        displayMessage("CLICK ON THE MAP TO SET HOME POSITION");
    }


    public void displayTooFarFromBoxMessage(){
        clearMessages();
        displayMessage("You are to far from box");
        clearAfterTime(1000);

    }

    public void displayBoxAlreadyOpenMessage(){
        displayMessage("You've already opened that chest!");
        clearAfterTime(1000);

    }

    public void displayMessage(String text, int time) {
        displayMessage(text);
        clearAfterTime(time);
    }

    public void displayMessage(String text) {
        displayMessage(text,25,Typeface.DEFAULT);
    }

    public void displayMessage(String text, int size, Typeface typeface){
        displayMessage(text,size,typeface,20,20);
    }

    public void displayMessage(String text, int size, Typeface typeface, int paddingTop, int paddingBot){
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
    }


    private void clearAfterTime(Integer time){
        if (time != null){
            final Handler handler = new Handler();
            handler.postDelayed(this::clearMessages, time);
        }
    }



}
