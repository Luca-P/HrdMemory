package com.aminluxury.luca1.hrdmemory;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.javiersantos.appupdater.AppUpdater;

import agency.tango.materialintroscreen.MaterialIntroActivity;
import agency.tango.materialintroscreen.MessageButtonBehaviour;
import agency.tango.materialintroscreen.SlideFragmentBuilder;
import agency.tango.materialintroscreen.animations.IViewTranslation;
public class IntroActivity extends MaterialIntroActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableLastSlideAlphaExitTransition(true);

        AppUpdater appUpdater = new AppUpdater(this);
        appUpdater.start();


        CustomSlide0 custom0 = new CustomSlide0();

        addSlide(custom0);

        CustomSlide custom1 = new CustomSlide();
        custom1.numberSlide = 1;
        addSlide(custom1);

        CustomSlide custom2 = new CustomSlide();
        custom2.numberSlide = 2;
        addSlide(custom2);


        CustomSlide custom3 = new CustomSlide();
        custom3.numberSlide = 3;
        addSlide(custom3);

        CustomSlide custom4 = new CustomSlide();
        custom4.numberSlide = 4;
        addSlide(custom4);

        CustomSlide custom5 = new CustomSlide();
        custom5.numberSlide = 5;
        addSlide(custom5);

        CustomSlide custom6 = new CustomSlide();
        custom6.numberSlide = 6;
        addSlide(custom6);

        CustomSlide custom7 = new CustomSlide();
        custom7.numberSlide = 7;
        addSlide(custom7);

        CustomSlide custom8 = new CustomSlide();
        custom8.numberSlide = 8;
        addSlide(custom8);

        CustomSlide custom9 = new CustomSlide();
        custom9.numberSlide = 9;
        addSlide(custom9);

        CustomSlide custom10 = new CustomSlide();
        custom10.numberSlide = 10;
        addSlide(custom10);

        CustomSlide custom11 = new CustomSlide();
        custom11.numberSlide = 11;
        addSlide(custom11);

       // setSkipButtonVisible();
        hideBackButton();

      ImageButton buttonNext = (ImageButton) findViewById(R.id.button_next);
      buttonNext.setVisibility(View.INVISIBLE);

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("skip"));

    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

             onFinish();
        }


    };
    @Override
    public void onFinish() {
        super.onFinish();
        Intent intent = new Intent(IntroActivity.this, MainActivity.class);
        intent.putExtra("bucketno", 1);
        startActivity(intent);
        return;

    }
}