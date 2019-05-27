package com.aminluxury.luca1.hrdmemory;





import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import agency.tango.materialintroscreen.SlideFragment;

public class CustomSlide0 extends SlideFragment {
    private TextView numeroLabel;
    public TextView introText;
    public ImageView immagineTut;
    public int numberSlide = 0;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_custom_slide0, container, false);


        Button buttonSkip = (Button) view.findViewById(R.id.buttonSkip);
        buttonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent("skip");
                // You can also include some extra data.

                intent.addFlags(Intent.FLAG_RECEIVER_FOREGROUND);

                // LocalBroadcastManager.getInstance(null).sendBroadcast(intent);


                LocalBroadcastManager localBroadcastManager2 = LocalBroadcastManager.getInstance(getContext());
                localBroadcastManager2.sendBroadcast(intent);

            }

        });

        return view;
    }






    @Override
    public int backgroundColor() {
        return R.color.colorBeige;
    }

    @Override
    public int buttonsColor() {
        return R.color.colorPrimary;
    }

    @Override
    public boolean canMoveFurther() {
        return true;
    }

    @Override
    public String cantMoveFurtherErrorMessage() {
        return getString(R.string.ocr_error);
    }
}