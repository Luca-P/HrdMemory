package com.aminluxury.luca1.hrdmemory;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.*;
import android.widget.Toast;
import android.os.Handler;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.budiyev.android.circularprogressbar.CircularProgressBar;

import android.support.v7.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class videoChoose extends AppCompatActivity {

    public String code = "";
    Uri selectedVideo;
    AmazonS3 s3Client;
    String bucket = "video.memory.hrd";
    TransferUtility transferUtility;
    File uploadToS3;// = new File("/storage/sdcard0/DCIM/Camera/VID_20180424_123452.mp4");
    String pathVideo;
    String emailSaved;
    Boolean isVideoBox;

    Boolean isChina;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_choose);

        SharedPreferences.Editor editor = getSharedPreferences("MyPreferences", MODE_PRIVATE).edit();
        SharedPreferences settings = getSharedPreferences("MyPreferences", MODE_PRIVATE);
        isChina = settings.getBoolean("china", false);
        if (isChina){
            s3credentialsProviderChina();
        }
        else {
            s3credentialsProvider();
        }




        setTransferUtility();
        code = getIntent().getExtras().getString("code");
        emailSaved = getIntent().getExtras().getString("mail");
        isVideoBox = getIntent().getExtras().getBoolean("isVideoBox");


        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Button buttonGallery = (Button) findViewById(R.id.buttonGallery);
        buttonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                galleryButton();

            }
        });


        Button buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraButton();

            }
        });






        Button buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonUpload.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                try {
                    uploadVideo();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        });


    }




    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
       ImageView imageView = (ImageView) findViewById(R.id.imagePreview);
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        switch(requestCode) {
            case 0:
                if(resultCode == RESULT_OK){
                    // from camera
                    selectedVideo = imageReturnedIntent.getData();
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    pathVideo = picturePath;
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(pathVideo,
                            MediaStore.Images.Thumbnails.MINI_KIND);
                    imageView.setImageBitmap(getCroppedBitmap(thumb));
                    if (pathVideo.length()>=1)
                    {

                    }
                    else {
                        Button camera = (Button) findViewById(R.id.buttonCamera);
                        camera.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        Button gallery = (Button) findViewById(R.id.buttonCamera);
                        gallery.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }

                break;
            case 1:
                if(resultCode == RESULT_OK){
                    selectedVideo = imageReturnedIntent.getData();
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };
                    Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    pathVideo = picturePath;
                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(pathVideo,
                            MediaStore.Images.Thumbnails.FULL_SCREEN_KIND);
                    imageView.setImageBitmap(getCroppedBitmap(thumb));
                    if (pathVideo.length()>=1)
                    {

                    }
                    else {
                        Button camera = (Button) findViewById(R.id.buttonCamera);
                        camera.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                        Button gallery = (Button) findViewById(R.id.buttonCamera);
                        gallery.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                    }
                }
                break;
        }
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        int pixel = dpToPixels(this,96);
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getWidth(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getWidth());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getWidth() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        Bitmap _bmp = Bitmap.createScaledBitmap(output, pixel, pixel, false);
       // return _bmp;
        return output;
    }
    public static int dpToPixels(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
    private void uploadVideo() throws UnsupportedEncodingException {

  if (pathVideo.length()>0) {
      // IF PATH VIDEOS

      //  final EditText nameText=(EditText)findViewById(R.id.editTextName);
      //  final EditText emailText=(EditText)findViewById(R.id.editTextNameMail);
      //  final Switch switchBox=(Switch)findViewById(R.id.switchVideo);
      final String emailString = emailSaved;
      uploadToS3 = new File(pathVideo);
      String memoryBox = "N";
      if (isVideoBox) {
          memoryBox = "Y";
      }
      String keyFile = code + "-" + emailString + "-" + memoryBox + ".mp4";
      String safe = keyFile.replace("@", "&#64");
      String m = URLDecoder.decode(keyFile, "UTF-8");
      //   String s = URLEncoder.encode(keyFile, "");

      Log.e("upload", safe);
      uploadVideoToS3(this, safe);
  }
  else {


  }
    }

    public void uploadVideoToS3(videoChoose view, String keyName){

        TransferObserver transferObserver = transferUtility.upload(
                bucket,          /* The bucket to upload to */
                keyName,/* The key for the uploaded object */
                uploadToS3       /* The file where the data to upload exists */
        );


        transferObserverListener(transferObserver);
    }


    void showProgress(int percentage){
        Log.d("percentage"," " + percentage );
        // Toast.makeText(this, "Progress in %" + percentage,
        //  Toast.LENGTH_SHORT).show();

    }
    void showFinalMessage(){
        // Toast.makeText(this, "Finito",
        //  Toast.LENGTH_SHORT).show();

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                Intent intent = new Intent("reloadList");
                LocalBroadcastManager.getInstance(null).sendBroadcast(intent);


            }
        }, 2000);


        // You can also include some extra data.

        Intent intentClose = new Intent("close");

        LocalBroadcastManager.getInstance(this).sendBroadcast(intentClose);


        if (isVideoBox){
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.Upload_completed);
            builder.setMessage(R.string.Upload_message);

            // add a button
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms
                            Intent intent = new Intent("reloadList");
                            LocalBroadcastManager.getInstance(null).sendBroadcast(intent);
                            finish();

                        }
                    }, 1000);
                }
            });

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        else {
            // setup the alert builder
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.Upload_completed);
            builder.setMessage(R.string.Upload_message2);

            // add a button
            builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

                public void onClick(DialogInterface dialog, int which) {
                    // TODO Auto-generated method stub
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            //Do something after 100ms

                            finish();

                        }
                    }, 1000);
                }
            });

            // create and show the alert dialog
            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }

    private void galleryButton() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this

                    ,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this
                        ,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        1);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickPhoto , 1);//one can be replaced with any action code
          //  Button camera = (Button) findViewById(R.id.buttonGallery);
          //  camera.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        }
    }

    private void cameraButton() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this

                    ,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(this
                        ,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        0);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        } else {
            // Permission has already been granted

            Intent takePicture = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            startActivityForResult(takePicture, 0);//zero can be replaced with any action code

         //   Button camera = (Button) findViewById(R.id.buttonCamera);
         //   camera.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        }
    }


    public void s3credentialsProviderChina(){

        // Initialize the AWS Credential
        CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider =
                new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "cn-north-1:12843444-cd0a-44e5-9f8d-a62c36da8f25", // Identity Pool ID
                        Regions.CN_NORTH_1 // Region
                );
        createAmazonS3Client(cognitoCachingCredentialsProvider);
    }

    public void s3credentialsProvider(){

        // Initialize the AWS Credential
        CognitoCachingCredentialsProvider cognitoCachingCredentialsProvider =
                new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "eu-west-2:fa688154-6279-43d2-bec0-9795049cc576", // Identity Pool ID
                        Regions.EU_WEST_2 // Region
                );
        createAmazonS3Client(cognitoCachingCredentialsProvider);
    }

    public void createAmazonS3Client(CognitoCachingCredentialsProvider
                                             credentialsProvider){

        // Create an S3 client
        s3Client = new AmazonS3Client(credentialsProvider);

        // Set the region of your S3 bucket
        if (isChina)
        {
            s3Client.setRegion(Region.getRegion(Regions.CN_NORTH_1));
        }
        else {
            s3Client.setRegion(Region.getRegion(Regions.EU_WEST_2));
        }



    }
    public void setTransferUtility(){

        transferUtility = new TransferUtility(s3Client, this);
    }

    /**
     * This method is used to upload the file to S3 by using TransferUtility class
     * @param view
     */
    public void uploadFileToS3(UploadActivity view, String keyName){

        TransferObserver transferObserver = transferUtility.upload(
                bucket,          /* The bucket to upload to */
                keyName,/* The key for the uploaded object */
                uploadToS3       /* The file where the data to upload exists */
        );


        transferObserverListener(transferObserver);
    }
    public void transferObserverListener(TransferObserver transferObserver){

        transferObserver.setTransferListener(new TransferListener(){

            @Override
            public void onStateChanged(int id, TransferState state) {
                // Toast.makeText(getApplicationContext(), "State Change" + state,
                // Toast.LENGTH_SHORT).show();
                if (state==TransferState.COMPLETED)
                {
                    showFinalMessage();
                   // textProgress.setVisibility(View.INVISIBLE);

                }
                else if (state==TransferState.IN_PROGRESS)
                {
                  //  textProgress.setVisibility(View.VISIBLE);
                }
                else if (state==TransferState.FAILED)
                {
                    //textProgress.setVisibility(View.INVISIBLE);
                    CircularProgressBar progressBar = findViewById(R.id.progress_bar);
                    progressBar.setProgress(0);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                double percentF= (double)bytesCurrent/(double)bytesTotal;
                int percentage = (int) (percentF * 100);
               // showProgress(percentage);
                CircularProgressBar progressBar = findViewById(R.id.progress_bar);
                progressBar.setProgress(percentage);

            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("error s3","error");
            }

        });
    }
}




