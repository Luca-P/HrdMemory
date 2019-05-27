package com.aminluxury.luca1.hrdmemory;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.helper.HttpConnection;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

// Permission request codes need to be < 256
import android.databinding.DataBindingUtil;
import android.content.Intent;


public class MainActivity extends AppCompatActivity


        implements NavigationView.OnNavigationItemSelectedListener {
    AmazonS3 s3Client;
    String bucket = "video.memory.hrd";
    TransferUtility transferUtility;
    File uploadToS3;
    public Boolean isIntroDone = false;
    public Integer isInt = 0;

  public   List<String> listing;
  public  Boolean isChina;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // callback method to call credentialsProvider method.
        Intent intent0 = getIntent();

        if (!intent0.hasExtra("bucketno")) {
            Intent intent = new Intent(MainActivity.this, IntroActivity.class);
            startActivity(intent);
            return;
        }
        else {


            //   Button button = (Button) this.findViewById(R.id.imageVideoDia);
     /*   button.setOnClickListener((OnClickListener) new OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
            }
        });
        */

            isChina = false;
            new JsonTask().execute("http://ip-api.com/json/?callback");


            // callback method to call the setTransferUtility method
            // setTransferUtility();
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            drawer.addDrawerListener(toggle);
            toggle.syncState();

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            displayFirst();


            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();

            int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(this);

            if (resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
                googleApiAvailability.getErrorDialog(this, resultCode, 1000).show();
                return;
            }


            LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                    new IntentFilter("reloadList"));

        }
    }
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent


            fetchFileFromS3(null);


        }
    };
    private class JsonTask extends AsyncTask<String, String, String> {



        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");
                    Log.d("Response0 JSON: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                Log.d("catch: ", "> " + e);
                s3credentialsProvider();

            } catch (IOException e) {
                Log.d("catch1 ", "> " + e);
                s3credentialsProvider();

            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Log.d("catch2 ", "> " + e);
                    s3credentialsProvider();

                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("Response1: ", "json: " + result);   //here u ll get whole response...... :-)
            if (result ==null)
            {
                isChina=false;
                SharedPreferences.Editor editor = getSharedPreferences("MyPreferences", MODE_PRIVATE).edit();

                editor.putBoolean("china", isChina);

                editor.commit();
                s3credentialsProvider();
                return;
            }
            if (result.toLowerCase() == "china")
            {
                isChina=true;
                //--SAVE Data
                SharedPreferences.Editor editor = getSharedPreferences("MyPreferences", MODE_PRIVATE).edit();

                editor.putBoolean("china", isChina);

                editor.commit();
                s3credentialsProviderChina();
            }
            else {
                isChina=false;
                SharedPreferences.Editor editor = getSharedPreferences("MyPreferences", MODE_PRIVATE).edit();

                editor.putBoolean("china", isChina);

                editor.commit();
                s3credentialsProvider();
            }
        }
    }








    public void onClick(View v) {


        Toast.makeText(this, "Button Clicked", Toast.LENGTH_LONG).show();
        //  RegisterBlood activity = (RegisterBlood) getActivity();
        // Now you can contact your activity through activity e.g.:
        //  activity.onKeyDown(KeyEvent.KEYCODE_MENU, null);
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
    public void createAmazonS3Client(CognitoCachingCredentialsProvider
                                             credentialsProvider){

        // Create an S3 client
        s3Client = new AmazonS3Client(credentialsProvider);

        if (isChina)
        {
            s3Client.setRegion(Region.getRegion(Regions.CN_NORTH_1));

        }
        else {
            // Set the region of your S3 bucket
            s3Client.setRegion(Region.getRegion(Regions.EU_WEST_2));
        }
        fetchFileFromS3(null);
    }


    public void fetchFileFromS3(Activity activity){

        // Get List of files from S3 Bucket
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {

                try {
                    Looper.prepare();
                    listing = getObjectNamesForBucket(bucket, s3Client);

                    for (int i=0; i< listing.size(); i++){
                      //  Toast.makeText(MainActivity.this, listing.get(i),Toast.LENGTH_LONG).show();
                        Log.d("tag", "listing "+ listing.get(i));
                    }
                    Looper.loop();
                     Log.d("tag", "listing "+ listing);

                     sendBroadcast();
                    Intent intentClose = new Intent("listFetched");

                    LocalBroadcastManager.getInstance(null).sendBroadcast(intentClose);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    Log.d("tag", "Exception found while listing "+ e);
                }

            }
        });
        thread.start();
    }


    private void sendBroadcast() {

        Intent intentClose = new Intent("listFetched");

        LocalBroadcastManager.getInstance(this).sendBroadcast(intentClose);
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //No call for super(). Bug on API Level > 11.
        outState.putString("WORKAROUND_FOR_BUG_19917_KEY", "WORKAROUND_FOR_BUG_19917_VALUE");
        super.onSaveInstanceState(outState);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode != 2) {

            super.onRequestPermissionsResult(requestCode, permissions, grantResults);

            return;
        }

        if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            Intent intent = new Intent("cameraOK");
            // You can also include some extra data.
            String str = " ";
            intent.putExtra("message", str);
            LocalBroadcastManager.getInstance(null).sendBroadcast(intent);
            displayFirst();


            return;
        }
    }

    private List<String> getObjectNamesForBucket(String bucket, AmazonS3 s3Client) {
        ObjectListing objects=s3Client.listObjects(bucket);
        List<String> objectNames=new ArrayList<String>(objects.getObjectSummaries().size());
        Iterator<S3ObjectSummary> iterator=objects.getObjectSummaries().iterator();
        while (iterator.hasNext()) {
            objectNames.add(iterator.next().getKey());
        }
        while (objects.isTruncated()) {
            objects=s3Client.listNextBatchOfObjects(objects);
            iterator=objects.getObjectSummaries().iterator();
            while (iterator.hasNext()) {
                objectNames.add(iterator.next().getKey());
            }
        }
        return objectNames;
    }
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        displaySelectedScreen(item.getItemId());
        if (id == R.id.nav_camera) {
            // Handle the camera action
        }
     /*   else if (id == R.id.nav_gallery) {


        } */
        else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private  void displayFirst()
    {
        Fragment fragment = null;
        fragment = new com.aminluxury.luca1.hrdmemory.OcrClass();

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);

            ft.commitAllowingStateLoss();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void displaySelectedScreen(int itemId) {

        //creating fragment object
        Fragment fragment = null;

        //initializing the fragment object which is selected
        switch (itemId) {
            case R.id.nav_camera:
                fragment = new com.aminluxury.luca1.hrdmemory.OcrClass();
                break;
            case R.id.nav_view:
                fragment = new Favorites();
               // fragment.code = "123456789012";
                break;
            case R.id.nav_manage:
                fragment = new com.aminluxury.luca1.hrdmemory.WebSite2();
                break;
            case R.id.nav_slideshow:
                fragment = new com.aminluxury.luca1.hrdmemory.WebDite();
                break;
        }

        //replacing the fragment
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commitAllowingStateLoss();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }



}
