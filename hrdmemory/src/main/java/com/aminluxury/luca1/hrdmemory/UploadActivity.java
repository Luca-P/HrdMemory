package com.aminluxury.luca1.hrdmemory;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
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

import android.support.v7.app.AlertDialog;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Luca1 on 23/04/18.
 */

public class UploadActivity extends AppCompatActivity {

    public String code = "";
     Uri selectedVideo;
    AmazonS3 s3Client;
    String bucket = "video.memory.hrd";
    TransferUtility transferUtility;
    File uploadToS3;// = new File("/storage/sdcard0/DCIM/Camera/VID_20180424_123452.mp4");
    String pathVideo;
    ProgressBar simpleProgressBar;
    TextView textProgress;
    Boolean isChina;
    Boolean isVideoBox;
    String emailSaved;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_activity);

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




        Button buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });




        TextView testoCode = (TextView) findViewById(R.id.hrd_antwerp_123456789012);
        testoCode.setText("HRD Antwerp "+ code);



        final EditText nameText=(EditText)findViewById(R.id.editTextName);
        final EditText emailText=(EditText)findViewById(R.id.editTextNameMail);

     /*   emailText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                // or other action, which you are using
                if (actionId == EditorInfo.IME_ACTION_DONE) {

                    emailText.setFocusable(false);
                    return false;
                }

                return false;
            }
        });
*/


        final Switch switchBox = (Switch)findViewById(R.id.switchVideo);
        final CheckBox checkPrivacy = (CheckBox)findViewById(R.id.checkBoxPolicy);
        Button buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

              if (nameText.getText().length()==0)
              {
                 showMessageName();
                  return;
              }
              String mailT = emailText.getText().toString();
                if ((mailT.length()==0)||(!isEmail(mailT)))
                {
                    showMessageEmail();
                    return;
                }




                if (!checkPrivacy.isChecked())
                {
                    showMessagePrivacy();
                    return;
                }


                emailSaved=emailText.getText().toString();
                isVideoBox = switchBox.isChecked();
                goToVideoChoose();

             /*   try {
                   // uploadVideo();
                    goToVideoChoose();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
                */
            }
        });


        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("close"));

    }
    // Our handler for received Intents. This will be called whenever an Intent
// with an action named "custom-event-name" is broadcasted.
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent


            // close
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Do something after 100ms

                    finish();

                }
            }, 400);


        }
    };
    private void goToVideoChoose()
    {
        Intent intent = new Intent(this, videoChoose.class);
        intent.putExtra("code", code);
        intent.putExtra("mail", emailSaved);
        intent.putExtra("isVideoBox", isVideoBox);
        startActivity(intent);
    }







    boolean isEmail(String emailText){
        boolean isValid = false;

        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = emailText;

        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        if (matcher.matches()) {
            isValid = true;
            Log.e("reciver","email valid");
        }

        return isValid;
    }

    private void showMessageName(){
        String message = "Please insert your name";
        Toast.makeText(this, R.string.Please_insert_your_name, Toast.LENGTH_LONG).show();

    }

    private void showMessageEmail(){
        String message = "Please insert a valid email";
        Toast.makeText(this, R.string.Please_insert_a_valid_email, Toast.LENGTH_LONG).show();

    }
    private void showMessageVideo(){
        String message = "Please choose a video";
        Toast.makeText(this, R.string.Please_choose_a_video, Toast.LENGTH_LONG).show();

    }
    private void showMessagePrivacy(){
        String message = "Please agree with terms and conditions";
        Toast.makeText(this, R.string.Please_agree_with_terms_and_conditions, Toast.LENGTH_LONG).show();

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


}
