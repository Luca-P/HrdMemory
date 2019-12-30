package com.aminluxury.luca1.hrdmemory;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;

import com.aminluxury.luca1.hrdmemory.MainActivity;
import com.aminluxury.luca1.hrdmemory.VideoPlayerActivity;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.Func;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca1 on 18/04/18.
 */

public class FragmentDiamond extends Fragment   {
    public String code = "";
    public String carat = "";
    public String colour = "";
    public String clarity = "";
    public String shape = "";
    public String certificateLink = "";
    public String htmlString;
    private String videoLink;
    MainActivity mainA;
    private  ImageButton buttonVideo;
    List<String> listing;
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.diamond_fragment, container, false);
        // getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);

       mainA = (MainActivity) getActivity();

       //  mainA.fetchFileFromS3(null);
        listing = new ArrayList<>();
       reloadVideos();
      // Log.d("s3 LOG", listing.toString());
       buttonVideo = (ImageButton) view.findViewById(R.id.imageButtonVideo);
        buttonVideo.setVisibility(View.INVISIBLE);


        buttonVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listing = mainA.listing;
                for (int i=0; i< listing.size(); i++)
                {
                    if (listing.get(i).contains(code))
                    {
                        videoLink = listing.get(i);
                        videoLink.replace("&#64","@");
                        videoLink = videoLink.replaceAll("\\s+","%2B");
                    }
                }
                if  (videoLink==null)
                {
                    Toast.makeText(getActivity(), R.string.Video_not_present,Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                    intent.putExtra("videoLink", videoLink);
                    startActivity(intent);
                }
            }
        });


       ImageButton button = (ImageButton) view.findViewById(R.id.imageButton2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               listing = mainA.listing;
                for (int i=0; i< listing.size(); i++)
                {
                    if (listing.get(i).contains(code))
                    {
                        videoLink = listing.get(i);
                        videoLink.replace("&#64","@");
                        videoLink = videoLink.replaceAll("\\s+","%2B");

                    }
                }
                if  (videoLink==null)
                {
                    Toast.makeText(getActivity(), R.string.Video_not_present,Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                    intent.putExtra("videoLink", videoLink);
                    startActivity(intent);
                }
            }
        });


        ImageButton buttonVideoDia = (ImageButton) view.findViewById(R.id.imageVideoDia);
        buttonVideoDia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listing = mainA.listing;
                for (int i=0; i< listing.size(); i++)
                {
                    if (listing.get(i).contains(code))
                    {

                        videoLink = listing.get(i);
                        videoLink.replace("&#64","@");
                    }
                }
                if (videoLink==null)
                {
                    Toast.makeText(getActivity(), R.string.Video_not_present,Toast.LENGTH_LONG).show();
                }
                else {
                    Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                    intent.putExtra("videoLink", videoLink);
                    startActivity(intent);
                }
            }
        });


        ImageButton buttonUpload = (ImageButton) view.findViewById(R.id.imageButtonUpload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listing = mainA.listing;
                for (int i=0; i< listing.size(); i++)
                {
                    if (listing.get(i).contains(code))
                    {
                        videoLink = listing.get(i);
                    }
                }
                if (videoLink==null) {
                    Intent intent = new Intent(getActivity(), UploadActivity.class);
                    intent.putExtra("code", code);
                    startActivity(intent);
                }
                else {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setMessage(R.string.cancel_video);
                    builder.setTitle(R.string.Video_present);
                    builder.setPositiveButton(R.string.ok_proceed, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent intent = new Intent(getActivity(), UploadActivity.class);
                            intent.putExtra("code", code);
                            startActivity(intent);
                        }
                    });
                    builder.setNeutralButton(R.string.cancel,null);
                    AlertDialog dialog = builder.create();
                    dialog.show();




                }
            }
        });

        ImageButton buttonCertificate = (ImageButton) view.findViewById(R.id.imageButtonCert);
        buttonCertificate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {






                String urlPath = certificateLink;
                if (urlPath.endsWith("DID")){
                    WebSite3 fragment2 = new WebSite3();

                    Intent intent = new Intent(getActivity(), WebSite3.class);
                    intent.putExtra("html", htmlString);
                    startActivity(intent);

                }
                else {
                    Intent intent = new Intent(getActivity(), PDFViewClass.class);
                    intent.putExtra("code", code);
                    intent.putExtra("certificate", urlPath);

                    startActivity(intent);
                }
            }
        });
       // checkVideo();

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("listFetched"));

        return view;
    }
    @Override
    public void onStart() {
        super.onStart();



        // manage other components that need to respond
        // to the activity lifecycle
    }


    @Override
    public void onResume(){

        Intent intent = new Intent("reloadList");
        LocalBroadcastManager.getInstance(null).sendBroadcast(intent);
        super.onResume();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms

                //checkVideo();
                mainA = (MainActivity) getActivity();
                listing = mainA.listing;
                if (listing!=null) {
                    for (int i = 0; i < listing.size(); i++) {
                        if (listing.get(i).contains(code)) {
                            videoLink = listing.get(i);
                            videoLink.replace("\u00A0", "%2B");
                            videoLink.replaceAll("\\s+","%2B");
                            if (videoLink.contains(" "))
                            {
                                Log.d("receiver", "Got : " + videoLink);
                               videoLink = videoLink.replaceAll("\\s+","+");
                            }
                            videoLink.replaceAll("%20","%2B");
                            videoLink.replace("&#64","@");


                        }
                    }

                    // convert String to char[] array
                    Log.d("receiver", "Got : " + videoLink);
                         if (videoLink != null) {
                             // using simple for loop
                             for (int i = 0; i < videoLink.length(); i++) {
                                 System.out.print(videoLink.charAt(i));
                                 Log.d("receiver", "Got : " + videoLink.charAt(i));
                                 if (videoLink.charAt(i) == " ".charAt(0)) {
                                     videoLink = videoLink.replaceFirst(String.valueOf(videoLink.charAt(i)), "%2B");
                                 }
                             }
                         }



                    if (videoLink == null) {
                        buttonVideo.setVisibility(View.INVISIBLE);

                    } else {

                        buttonVideo.setVisibility(View.VISIBLE);
                    }

                }
            }
        }, 2000);


    }



    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent


           //checkVideo();


        }
    };


    void reloadVideos(){

        mainA = (MainActivity) getActivity();
        mainA.fetchFileFromS3(null);

    }


     void checkVideo() {

         listing = mainA.listing;

         for (int i = 0; i < listing.size(); i++) {
             if (listing.get(i).contains(code)) {
                 videoLink = listing.get(i);
                 videoLink.replace("&#64","@");

             }
         }
         if (videoLink == null) {
             buttonVideo.setVisibility(View.INVISIBLE);

         } else {

             buttonVideo.setVisibility(View.VISIBLE);
         }
     }


    public void onClick(View v) {




    }



    public  void updateInterface()
    {
        TextView myCodeLabel = (TextView) getActivity().findViewById(R.id.textViewCodeDia);
        String codeText = "HRD Antpwer " + code;
        myCodeLabel.setText(codeText);
        Log.d("receiver", "Got web codeDia: " + code);
        TextView myCodeCut = (TextView) getActivity().findViewById(R.id.textViewCut);
        String cutText = "Cut: " + shape;
        myCodeCut.setText(cutText);
        Log.d("receiver", "Got web shapeDia: " + shape);

        TextView myClarity = (TextView) getActivity().findViewById(R.id.textViewClarity);
        String clarityText = "Clarity: " + clarity;
        myClarity.setText(clarityText);

        TextView myColor = (TextView) getActivity().findViewById(R.id.textViewColor);
        String colorText = "Color: " + colour;
        myColor.setText(colorText);

        TextView myCarat = (TextView) getActivity().findViewById(R.id.textViewCarat);
        String caratText = "Carat: " + carat;
        myCarat.setText(caratText);

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super().
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        updateInterface();



    }


}
