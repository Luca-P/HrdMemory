package com.aminluxury.luca1.hrdmemory;

/*
 * Copyright (C) The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.AlertDialog;

import org.apache.http.client.HttpClient;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.StrictMode;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.aminluxury.luca1.hrdmemory.FragmentDiamond;
import com.aminluxury.luca1.hrdmemory.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.aminluxury.luca1.hrdmemory.CameraSource;
import com.aminluxury.luca1.hrdmemory.CameraSourcePreview;
import com.aminluxury.luca1.hrdmemory.GraphicOverlay;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.victor.loading.rotate.RotateLoading;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;

import org.xml.sax.InputSource;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import android.os.Vibrator;
/**
 * Activity for the multi-tracker app.  This app detects text and displays the value with the
 * rear facing camera. During detection overlay graphics are drawn to indicate the position,
 * size, and contents of each TextBlock.
 */
public final class OcrClass extends Fragment {
    private static final String TAG = "OcrCaptureActivity";

    // Intent request code to handle updating play services if needed.
    private static final int RC_HANDLE_GMS = 9001;

    // Permission request codes need to be < 256
    private static final int RC_HANDLE_CAMERA_PERM = 2;

    // Constants used to pass extra data in the intent
    public static final String AutoFocus = "AutoFocus";
    public static final String UseFlash = "UseFlash";
    public static final String TextBlockObject = "String";
    private Boolean isParsingWeb = false;
    private CameraSource mCameraSource;
    private CameraSourcePreview mPreview;
    private GraphicOverlay<OcrGraphic> mGraphicOverlay;
    private Boolean isFlashOn = false;
    // Helper objects for detecting taps and pinches.
    private ScaleGestureDetector scaleGestureDetector;
    private GestureDetector gestureDetector;
    private String url = "";
    private RotateLoading rotateLoading;
    private ImageView linea;
    private Animation animation;
    Paint paint = new Paint();
    Rect r = new Rect(10, 10, 200, 100);
    /**
     * Initializes the UI and creates the detector pipeline.
     */

    public void onDraw(Canvas canvas) {
        // fill
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.TRANSPARENT);
        canvas.drawRect(r, paint);

        // border
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.MAGENTA);
        canvas.drawRect(r, paint);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle   savedInstanceState) {
        View view =  inflater.inflate(R.layout.ocr_capture, container, false);
        // getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);


        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                rotateLoading.stop();
                super.handleMessage(msg);
            }
        };
        mPreview = (CameraSourcePreview) getView().findViewById(R.id.preview);
        mGraphicOverlay = (GraphicOverlay<OcrGraphic>) getView().findViewById(R.id.graphicOverlay);
        rotateLoading = (RotateLoading) getView().findViewById(R.id.rotateloading);

        // read parameters from the intent used to launch the activity.
        boolean autoFocus = getActivity().getIntent().getBooleanExtra(AutoFocus, true);
        boolean useFlash = getActivity().getIntent().getBooleanExtra(UseFlash, false);

        // Check for the camera permission before accessing the camera.  If the
        // permission is not granted yet, request permission.
        int rc = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            createCameraSource(autoFocus, useFlash);
        } else {
            requestCameraPermission();
        }

        gestureDetector = new GestureDetector(getActivity(), new CaptureGestureListener());
        scaleGestureDetector = new ScaleGestureDetector(getActivity(), new ScaleListener());

      //  Snackbar.make(mGraphicOverlay, "Tap to capture. Pinch/Stretch to zoom",
      //          Snackbar.LENGTH_LONG)
       //         .show();



        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("custom-event-name"));


        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
                new IntentFilter("cameraOK"));


        TextView myAwesomeTextView = (TextView) getActivity().findViewById(R.id.codeLabel);
        myAwesomeTextView.setText("");
        int delta = dpToPixels(getContext(),150);
        linea = (ImageView) getActivity().findViewById(R.id.imageView3);
        animation = new TranslateAnimation(0, 0,0, delta);
        animation.setDuration(1200);
        animation.setFillAfter(true);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.REVERSE);
        linea.startAnimation(animation);

        ImageButton buttonFlash = (ImageButton) view.findViewById(R.id.imageButtonFlash);
        buttonFlash.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               isFlashOn = !isFlashOn;
                                               if (isFlashOn) {
                                                   mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                                               }
                                               else {
                                                   mCameraSource.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                                                 //  String a = mCameraSource.getFlashMode();
                                               }
                                           }
                                       });

        ImageButton buttonCode = (ImageButton) view.findViewById(R.id.imageButtonCode);
        buttonCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Insert the HRD code");

// Set up the input
                final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_NUMBER);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                      String  m_Text = input.getText().toString();
                        codeFound(m_Text);
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();

            }
        });

       // linea.setVisibility(0);

      //  parseWeb("https://my.hrdantwerp.com/?id=34&record_number=170003080331&L=");
    }

    public static int dpToPixels(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
            if (message.length()==12) {

                codeFound(message);

            }
            else {
                boolean autoFocus = getActivity().getIntent().getBooleanExtra(AutoFocus,true);
                boolean useFlash = getActivity().getIntent().getBooleanExtra(UseFlash, false);

                createCameraSource(autoFocus, useFlash);
            }
        }
    };
    /**
     * Handles the requesting of the camera permission.  This includes
     * showing a "Snackbar" message of why the permission is needed then
     * sending the request.
     */
    private void requestCameraPermission() {
        Log.w(TAG, "Camera permission is not granted. Requesting permission");

        final String[] permissions = new String[]{Manifest.permission.CAMERA};

        if (!ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.CAMERA)) {
            ActivityCompat.requestPermissions(getActivity(), permissions, RC_HANDLE_CAMERA_PERM);
            return;
        }

        final Activity thisActivity = getActivity();

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.requestPermissions(thisActivity, permissions,
                        RC_HANDLE_CAMERA_PERM);
            }
        };

        Snackbar.make(mGraphicOverlay, R.string.permission_camera_rationale,
                Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.ok, listener)
                .show();
    }


    public void codeFound(String message){
        rotateLoading.start();

        //ImageView linea = (ImageView) getActivity().findViewById(R.id.imageView3);
        linea.setVisibility(View.INVISIBLE);
        linea.setBackgroundColor(Color.TRANSPARENT);
        linea.clearAnimation();
        final MediaPlayer mp = MediaPlayer.create(getContext(), R.raw.end_video_record);
        TextView myAwesomeTextView = (TextView) getActivity().findViewById(R.id.codeLabel);
        myAwesomeTextView.setText(message);
        myAwesomeTextView.setCompoundDrawablesWithIntrinsicBounds(0,0,R.drawable.qrcorretto,0);
        if (mPreview != null) {
            mPreview.stop();
        }

        Vibrator v = (Vibrator)getContext().getSystemService(Context.VIBRATOR_SERVICE);
        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
        }else{
            //deprecated in API 26
            v.vibrate(500);
        }
        mp.start();
      final   String urlString = "https://my.hrdantwerp.com/?id=34&record_number=" + message + "&L=";
      final String message1 = message;

       url = urlString;


       if (!isParsingWeb) {
           AsyncTask.execute(new Runnable() {
               @Override
               public void run() {
                   //TODO your background code
                   parseWeb(urlString, message1);
               }
           });
           // add a button
       }



      //  new Description().execute();

    }
    private void parseWeb(String url, String code){

        if(isParsingWeb){
            return;
        }

        isParsingWeb=true;
        Log.d("receiver", "Got web0: " + url);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        String htmlString = "<!DOCTYPE html> <html>  <head>  <style>  table, th, td {           border-collapse: collapse;     padding: 50px;     margin-left: 35px;     margin-bottom: 25px;  }  th, td {      padding: 5px;           }  dd {     color: rgb(27, 165, 222);     text-align: left;     font-weight: bold;     font-family: 'Helvetica Neue'; font-size:27px; }  #inline, #inline2{        display: inline;  }  att {      text-align: center;     font-weight: normal;     font-family: 'Helvetica Neue'; font-size:24px; }  val {          font-weight: bold;     font-family: 'Helvetica Neue'; font-size:24px; }  </style>  </head>  <body> <img src='https://s3.eu-west-2.amazonaws.com/amin.tech/pdfHead.png'>   <div id='inline3'>  <dd >NATURAL DIAMOND IDENTIFICATION REPORT</dd>  <dd id='inline'>N°";
        try {
            // Connect to the web site
            Document mBlogDocument = Jsoup.connect(url).validateTLSCertificates(false).get();
            System.out.println(mBlogDocument);
            // Using Elements to get the Meta data
            Elements mElementDataSize = mBlogDocument.select("td");
            Elements mLinkList = mBlogDocument.select("a");
            Elements dateLink = mBlogDocument.select("h6");
            Set certificateList = new HashSet();

            // Locate the content attribute

            for (int i = 0; i < dateLink.size(); i++) {
                Elements mElementDate = dateLink.eq(i);
                String descrizione = mElementDate.text();
                if (descrizione.contains("Date of Issue:")){
                    Elements mElementToSave = dateLink.eq(i+1);
                    String descrizioneToSave = mElementDate.text();
                    certificateList.add("Date of Issue: " + descrizioneToSave);
                    htmlString = htmlString  + code + " &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp;</dd>  <span style='display:inline-block; width: 400;'></span>   <val id='inline2'>              " + descrizioneToSave;
                }

            }


            int mElementSize = mElementDataSize.size();
            Log.d("receiver", "Got web1: " + String.valueOf(mElementDataSize));
            String carat = "";
            String clarity = "";
            String cut = "";
            String colour ="";
            String certificate = "";
            for (int i = 0; i < mElementSize; i++) {

                Elements mElementAuthorName = mBlogDocument.select("td").eq(i);
                String descrizione = mElementAuthorName.text();

                if (descrizione.contains("Shape"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web2 shapesh: " + shape);
                    cut = shape;
                    certificateList.add(shape);
                    htmlString = htmlString + "</val>  </div>      <table style='width:60%'>    <tr>      <th></th>   </tr>    <tr>      <td><att>Shape</att></td>      <td><val>"+cut;
                }
                if (descrizione.contains("Carat"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web carat: " + shape);
                    carat = shape;
                    certificateList.add(shape);
                    htmlString = htmlString + "</val></td>    </tr>     <tr>      <td><att>Carat (weight)</att></td>      <td><val>" +carat;
                }
                if (descrizione.contains("Colour"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web color: " + shape);
                    colour= shape;
                    certificateList.add(shape);
                    htmlString = htmlString + "</val></td>    </tr>        <tr>      <td><att>Colour Grade</att></td>      <td><val>"+colour;
                }
                if (descrizione.contains("Clarity"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    clarity=shape;
                    certificateList.add(shape);
                    htmlString = htmlString + "</val></td>    </tr>    <tr>      <td><att>Clarity Grade</att></td>      <td><val>"+ clarity;
                }
                if (descrizione.contains("Proportions:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);

                    certificateList.add(shape);
                    htmlString = htmlString + "</val></td>    </tr>    <tr>      <td><att>Cut (Prop./Pol./Symm.)</att></td>      <td><val>"+shape;
                }
                if (descrizione.contains("Polish:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);

                    certificateList.add(shape);
                    htmlString = htmlString +"/ "+shape;
                }
                if (descrizione.contains("Symmetry:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);

                    certificateList.add(shape);
                    htmlString = htmlString +"/ "+shape;
                }
                if (descrizione.contains("Fluorescence:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);

                    certificateList.add(shape);
                    htmlString = htmlString + "</val></td>    </tr>  </table>  <dd >TECHNICAL INFORMATION</dd>  <table style='width:60%'>    <tr>      <th></th>   </tr>  <tr>      <td><att>Fluorescence</att></td>      <td><val>"+shape;
                }
                if (descrizione.contains("Measurements:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    htmlString = htmlString +"</val></td>    </tr>  <tr>      <td><att>Measurements</att></td>      <td><val>"+ shape;
                    certificateList.add(shape);
                }
                if (descrizione.contains("Girdle:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    htmlString = htmlString + "</val></td>    </tr>     <tr>      <td><att>Girdle</att></td>      <td><val>"+shape;
                    certificateList.add(shape);
                }
                if (descrizione.contains("Culet:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    htmlString = htmlString + "</val></td>    </tr>    <tr>      <td><att>Culet</att></td>      <td><val>"+shape;
                    certificateList.add(shape);
                }
                if (descrizione.contains("Total Depth:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    htmlString = htmlString + "</val></td>    </tr>    <tr>      <td><att>Total Depth</att></td>      <td><val>"+shape;

                    certificateList.add(shape);
                }
                if (descrizione.contains("Table Width:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    htmlString = htmlString + "</val></td>    </tr> <tr>      <td><att>Total Depth</att></td>      <td><val>"+shape;
                            certificateList.add(shape);
                }
                if (descrizione.contains("Crown Height (β):"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    htmlString = htmlString + "</val></td>    </tr>     <tr>      <td><att>Table Width</att></td>      <td><val>%@</val></td>    </tr>    <tr>      <td><att>Crown Height</att></td>      <td><val>"+shape;
                    certificateList.add(shape);
                }
                if (descrizione.contains("Pavilion Depth (α):"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    htmlString = htmlString +"</val></td>    </tr>    <tr>      <td><att>Pavilion Depth</att></td>      <td><val>"+shape;
                    certificateList.add(shape);
                }
                if (descrizione.contains("Length Halves Crown:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    htmlString = htmlString +"</val></td>    </tr>    <tr>      <td><att>Lenght Halves Crown</att></td>      <td><val>"+ shape;
                    certificateList.add(shape);
                }
                if (descrizione.contains("Length Halves Pavilion:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    htmlString = htmlString + "</val></td>    </tr>    <tr>      <td><att>Lenght Halves Pavilion</att></td>      <td><val>"+ shape;
                    certificateList.add(shape);
                }
                if (descrizione.contains("Sum α & β:"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web clarity: " + shape);
                    htmlString = htmlString + "</val></td>    </tr>    <tr>      <td><att>Sum</att></td>      <td><val>"+shape;
                    certificateList.add(shape);
                }

                int mLinkSize = mLinkList.size();

                for (int x = 0; x < mLinkSize; x++)
                {
                    Element myElementLink = mLinkList.get(x);
                    if (myElementLink.attr("href").contains("certificate"))
                    {
                        certificate  = myElementLink.attr("href");

                    }
                }



                isParsingWeb=false;


            }
            htmlString = htmlString + "</val></td>    </tr>  </table> <img src='"+certificate +"' width='600px'>   </body>  </html>";
            Log.d("receiver", "Got web0: " + htmlString);

            Set<String> set = new HashSet<String>();
            set.add("html:"+htmlString);
            set.add("carat:"+carat);
            set.add("clarity:"+clarity);
            set.add("colour:"+colour);
            set.add("shape:"+cut);
            set.add("code:"+code);
            set.add("certificate:"+certificate);


            saveData(code, set);
            //--SAVE Data

            FragmentDiamond fragment2 = new FragmentDiamond();
            fragment2.htmlString= htmlString;
            fragment2.carat=carat;
            fragment2.clarity=clarity;
            fragment2.colour=colour;
            fragment2.shape=cut;
            fragment2.code=code;
            fragment2.certificateLink= certificate;
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment2);
            fragmentTransaction.commit();
         //   rotateLoading.stop();
        } catch (IOException e) {
            e.printStackTrace();
            isParsingWeb=false;
         //   rotateLoading.stop();
        }
    }




    void saveData(String code, Set listCertificate){

        SharedPreferences preferences = getContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(code, listCertificate);
        editor.commit();

    }
    /*
    @Override
    public boolean onTouchEvent(MotionEvent e) {
        boolean b = scaleGestureDetector.onTouchEvent(e);

        boolean c = gestureDetector.onTouchEvent(e);

        return b || c || getActivity().onTouchEvent(e);
    }
*/
    /**
     * Creates and starts the camera.  Note that this uses a higher resolution in comparison
     * to other detection examples to enable the ocr detector to detect small text samples
     * at long distances.
     *
     * Suppressing InlinedApi since there is a check that the minimum version is met before using
     * the constant.
     */
    @SuppressLint("InlinedApi")
    private void createCameraSource(boolean autoFocus, boolean useFlash) {
        Context context = getActivity().getApplicationContext();

        // A text recognizer is created to find text.  An associated processor instance
        // is set to receive the text recognition results and display graphics for each text block
        // on screen.
        TextRecognizer textRecognizer = new TextRecognizer.Builder(context).build();
        textRecognizer.setProcessor(new OcrDetectorProcessor(mGraphicOverlay));
        Log.d(TAG, "is operational"+ textRecognizer.isOperational());
        if (!textRecognizer.isOperational()) {
            // Note: The first time that an app using a Vision API is installed on a
            // device, GMS will download a native libraries to the device in order to do detection.
            // Usually this completes before the app is run for the first time.  But if that
            // download has not yet completed, then the above call will not detect any text,
            // barcodes, or faces.
            //
            // isOperational() can be used to check if the required native libraries are currently
            // available.  The detectors will automatically become operational once the library
            // downloads complete on device.
            Log.w(TAG, "Detector dependencies are not yet available.");

            // Check for low storage.  If there is low storage, the native library will not be
            // downloaded, so detection will not become operational.
            IntentFilter lowstorageFilter = new IntentFilter(Intent.ACTION_DEVICE_STORAGE_LOW);
            boolean hasLowStorage = getActivity().registerReceiver(null, lowstorageFilter) != null;

            if (hasLowStorage) {
                Toast.makeText(getActivity(), R.string.low_storage_error, Toast.LENGTH_LONG).show();
                Log.w(TAG, getString(R.string.low_storage_error));
            }
        }

        // Creates and starts the camera.  Note that this uses a higher resolution in comparison
        // to other detection examples to enable the text recognizer to detect small pieces of text.
        mCameraSource =
                new CameraSource.Builder(getActivity().getApplicationContext(), textRecognizer)
                        .setFacing(CameraSource.CAMERA_FACING_BACK)
                        .setRequestedPreviewSize(1280, 1024)
                        .setRequestedFps(2.0f)
                        .setFlashMode(useFlash ? Camera.Parameters.FLASH_MODE_TORCH : null)
                        .setFocusMode(autoFocus ? Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE : null)
                        .build();



    }

    /**
     * Restarts the camera.
     */
    @Override
    public void onResume() {
        super.onResume();
        startCameraSource();
    }

    /**
     * Stops the camera.
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mPreview != null) {
            mPreview.stop();
        }
    }

    /**
     * Releases the resources associated with the camera source, the associated detectors, and the
     * rest of the processing pipeline.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPreview != null) {
            mPreview.release();
        }

        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != RC_HANDLE_CAMERA_PERM) {
            Log.d(TAG, "Got unexpected permission result: " + requestCode);
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "Camera permission granted - initialize the camera source");
            // We have permission, so create the camerasource
            boolean autoFocus = getActivity().getIntent().getBooleanExtra(AutoFocus,true);
            boolean useFlash = getActivity().getIntent().getBooleanExtra(UseFlash, false);

            createCameraSource(autoFocus, useFlash);

       /*     final Handler handler = new Handler(getContext().getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms
                    ;
                    boolean autoFocus = getActivity().getIntent().getBooleanExtra(AutoFocus,true);
                    boolean useFlash = getActivity().getIntent().getBooleanExtra(UseFlash, false);
                    createCameraSource(autoFocus, useFlash);
                }
            }, 1000);
*/

            return;
        }

        Log.e(TAG, "Permission not granted: results len = " + grantResults.length +
                " Result code = " + (grantResults.length > 0 ? grantResults[0] : "(empty)"));

        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Multitracker sample")
                .setMessage(R.string.no_camera_permission)
                .setPositiveButton(R.string.ok, listener)
                .show();
    }

    /**
     * Starts or restarts the camera source, if it exists.  If the camera source doesn't exist yet
     * (e.g., because onResume was called before the camera source was created), this will be called
     * again when the camera source is created.
     */
    private void startCameraSource() throws SecurityException {
        // Check that the device has play services available.
        int code = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(
                getActivity().getApplicationContext());
        if (code != ConnectionResult.SUCCESS) {
            Dialog dlg =
                    GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), code, RC_HANDLE_GMS);
            dlg.show();

            if (mCameraSource != null) {
                try {
                    mPreview.start(mCameraSource, mGraphicOverlay);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to start camera source.", e);
                    mCameraSource.release();
                    mCameraSource = null;
                }
            }
        }
        else {

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.app_name);
            builder.setMessage(R.string.noServices);
// Set up the input
            final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                    insertManulally();
                }
            });


            builder.show();
        }


    }


    private void insertManulally()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.Insert_the_HRD_code);

// Set up the input
        final EditText input = new EditText(getContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String  m_Text = input.getText().toString();
                codeFound(m_Text);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    /**
     * onTap is called to capture the first TextBlock under the tap location and return it to
     * the Initializing Activity.
     *
     * @param rawX - the raw position of the tap
     * @param rawY - the raw position of the tap.
     * @return true if the activity is ending.
     */
    private boolean onTap(float rawX, float rawY) {
        OcrGraphic graphic = mGraphicOverlay.getGraphicAtLocation(rawX, rawY);
        TextBlock text = null;
        if (graphic != null) {
            text = graphic.getTextBlock();
            if (text != null && text.getValue() != null) {
                Intent data = new Intent();
                data.putExtra(TextBlockObject, text.getValue());
                getActivity().setResult(CommonStatusCodes.SUCCESS, data);
                getActivity().finish();
            }
            else {
                Log.d(TAG, "text data is null");
            }
        }
        else {
            Log.d(TAG,"no text detected");
        }
        return text != null;
    }

    private class CaptureGestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            return onTap(e.getRawX(), e.getRawY()) || super.onSingleTapConfirmed(e);
        }
    }

    private class ScaleListener implements ScaleGestureDetector.OnScaleGestureListener {

        /**
         * Responds to scaling events for a gesture in progress.
         * Reported by pointer motion.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should consider this event
         * as handled. If an event was not handled, the detector
         * will continue to accumulate movement until an event is
         * handled. This can be useful if an application, for example,
         * only wants to update scaling factors if the change is
         * greater than 0.01.
         */
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            return false;
        }

        /**
         * Responds to the beginning of a scaling gesture. Reported by
         * new pointers going down.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         * @return Whether or not the detector should continue recognizing
         * this gesture. For example, if a gesture is beginning
         * with a focal point outside of a region where it makes
         * sense, onScaleBegin() may return false to ignore the
         * rest of the gesture.
         */
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        /**
         * Responds to the end of a scale gesture. Reported by existing
         * pointers going up.
         * <p/>
         * Once a scale has ended, {@link ScaleGestureDetector#getFocusX()}
         * and {@link ScaleGestureDetector#getFocusY()} will return focal point
         * of the pointers remaining on the screen.
         *
         * @param detector The detector reporting the event - use this to
         *                 retrieve extended info about event state.
         */
        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {
            mCameraSource.doZoom(detector.getScaleFactor());
        }
    }
 class Description extends AsyncTask<Void, Void, Void> {
    String desc;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected Void doInBackground(Void... params) {
        Log.d("receiver", "Got web0: " + url);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Log.d("receiver", "Got web0: " + url);
        try {
            // Connect to the web site
            Document mBlogDocument = Jsoup.connect(url).get();
            System.out.println(mBlogDocument);
            // Using Elements to get the Meta data
            Elements mElementDataSize = mBlogDocument.select("td");
            // Locate the content attribute
            int mElementSize = mElementDataSize.size();
            Log.d("receiver", "Got web1: " + String.valueOf(mElementDataSize));

            for (int i = 0; i < mElementSize; i++) {

                Elements mElementAuthorName = mBlogDocument.select("td").eq(i);
                String descrizione = mElementAuthorName.text();
                if (descrizione.contains("Shape"))
                {
                    Elements mElementShape = mBlogDocument.select("td").eq(i+1);
                    String shape = mElementShape.text();
                    Log.d("receiver", "Got web2: " + shape);

                }


            }

            //launch new fragment

            FragmentDiamond fragment2 = new FragmentDiamond();

            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.content_frame, fragment2);
            fragmentTransaction.commit();


        } catch (IOException e) {
            e.printStackTrace();
            Log.d("receiver", "No web0: " + e.getMessage());
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        // Set description into TextView


    }
}


}
