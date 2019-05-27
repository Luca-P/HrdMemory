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

public class CustomSlide extends SlideFragment {
    private TextView numeroLabel;
    public TextView introText;
    public ImageView immagineTut;
    public int numberSlide = 0;
    private View view;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_custom_slide, container, false);

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


        if (numberSlide == 1) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page1);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("1");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut1);
        }
        if (numberSlide == 2) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page2);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("2");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut2);
        }
        if (numberSlide == 3) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page3);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("3");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut3);
        }
        if (numberSlide == 4) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page4);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("4");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut4);
        }
        if (numberSlide == 5) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page5);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("5");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut5);
        }
        if (numberSlide == 6) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page6);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("6");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut6);
        }
        if (numberSlide == 7) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page7);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("7");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut7);
        }
        if (numberSlide == 8) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page8);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("8");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut8);
        }
        if (numberSlide == 9) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page9);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("9");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut9);
        }
        if (numberSlide == 10) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page10);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("10");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut10);
        }
        if (numberSlide == 11) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page11);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("11");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut11);
        }
        if (numberSlide == 12) {
            ImageView imageView = (ImageView) view.findViewById(R.id.imageTut);
            imageView.setImageResource(R.drawable.page12);

            TextView textNumero = (TextView) view.findViewById(R.id.numeroLabelIntro);
            textNumero.setText("12");

            TextView textDescrizione = (TextView) view.findViewById(R.id.textIntro);
            textDescrizione.setText(R.string.tut12);
        }


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