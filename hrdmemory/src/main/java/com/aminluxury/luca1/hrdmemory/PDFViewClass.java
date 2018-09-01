package com.aminluxury.luca1.hrdmemory;



import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Func;
import com.tonyodev.fetch2.Func2;
import com.tonyodev.fetch2.NetworkType;
import com.tonyodev.fetch2.Priority;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.Status;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.LinearLayout;


import android.support.v4.content.SharedPreferencesCompat;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;

import com.github.barteksc.pdfviewer.PDFView;
import com.tonyodev.fetch2.Download;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchListener;
import com.tonyodev.fetch2.Func;
import com.tonyodev.fetch2.Func2;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2.Status;

import org.jetbrains.annotations.NotNull;

/**
 * Created by Luca1 on 05/05/18.
 */

public class PDFViewClass  extends AppCompatActivity implements FetchListener{
    private WebView mWebView;

    private Context mContext;
    private static final int MY_PERMISSION_REQUEST_CODE = 123;
    private Activity mActivity;


    private Fetch mainFetch;
    private Request request;
    private String filePath;
    private String urlCert;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pdfview);
        Button buttonBack = (Button) findViewById(R.id.buttonB);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //fetch.close();
                finish();

            }
        });





         urlCert = getIntent().getExtras().getString("certificate");
        String code = getIntent().getExtras().getString("code");

        if (urlCert.length()==0) {
            urlCert = "http://ws2.hrdantwerp.com/HRD.CertificateService.WebAPI/certificate?certificateNumber=170003080331&certificateType=CERT";
            code = "pdfProva";
        }

        filePath =Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .toString() + "/HRD" + "/certificate/"+code+".PDF";




        // Get the application context
        mContext = getApplicationContext();
        mActivity = PDFViewClass.this;
        // Check permission for write external storage





        checkPermission();






















    }

    private void loadAgain(){

        File file = new File(filePath);
        if(file.exists()){
            openPdf();
        }

        else{
            downloadPDF();
        }


    }

    private  void downloadPDF()
    {

        String nameSpace = urlCert + filePath;
        nameSpace = nameSpace.replace("/", "-");
        mainFetch = new Fetch.Builder(getApplicationContext(), nameSpace)
                .setDownloadConcurrentLimit(3) // Allows Fetch to download 4 downloads in Parallel.
                .enableLogging(true)
                .build();

        //Single enqueuing example
        final Request request = new Request(urlCert, filePath);
        request.setPriority(Priority.HIGH);
        request.setNetworkType(NetworkType.ALL);
        request.addHeader("clientKey", "SD78DF93_3947&MVNGHE1WONG");
        mainFetch.addListener(this);
        mainFetch.enqueue(request, new Func<Download>() {
            @Override
            public void call(Download download) {
                //Request successfully Queued for download
            }
        }, new Func<Error>() {
            @Override
            public void call(Error error) {
                //An error occurred when enqueuing a request.
            }
        });

    }

  private  void openPdf()
  {
      PDFView pdfView = (PDFView) findViewById(R.id.pdfView);
      Integer pageNumber = 0;
      String pdfFileName;

      //   Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
      //   intent.setType("application/pdf");
      //  startActivityForResult(intent, REQUEST_CODE);
      String pathT = "file://"+filePath;
      Uri link = Uri.parse(pathT);
      pdfView.fromUri(link).load();



  }


    @Override
    public void onQueued(@NotNull Download download) {
      //  Toast.makeText(this, "download onQueued", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCompleted(@NotNull Download download) {
        Log.d("download", "onCompleted: FINITO!!!");

       // Toast.makeText(this, "download finito", Toast.LENGTH_LONG).show();
        if (mainFetch!=null) {
         //   mainFetch.removeListener(this);
            mainFetch.close();
        }
       openPdf();
    }


    @Override
    protected void onPause() {
        super.onPause();
        if (mainFetch!=null) {
          //  mainFetch.removeListener(this);
            mainFetch.close();
        }
    }

    @Override
    public void onError(@NotNull Download download) {
        Toast.makeText(this, "download error", Toast.LENGTH_LONG).show();
        if (mainFetch!=null) {
           // mainFetch.removeListener(this);
            mainFetch.close();
        }

    }

    @Override
    public void onProgress(@NotNull Download download, long etaInMilliseconds, long downloadedBytesPerSecond) {
        Log.d("download", "IN PROGRESS...");
       // Toast.makeText(this, "download on progress", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPaused(@NotNull Download download) {

    }

    @Override
    public void onResumed(@NotNull Download download) {
        Log.d("download", "resumed");

    }

    @Override
    public void onCancelled(@NotNull Download download) {
        if (mainFetch!=null) {
         //   mainFetch.removeListener(this);
            mainFetch.close();
        }
        Log.d("download cancel", "cancel");

    }

    @Override
    public void onRemoved(@NotNull Download download) {
        if (mainFetch!=null) {
          //  mainFetch.removeListener(this);
            mainFetch.close();
        }
        Log.d("download", "removed");
    }

    @Override
    public void onDeleted(@NotNull Download download) {
        if (mainFetch!=null) {
         //   mainFetch.removeListener(this);
            mainFetch.close();
        }
        Log.d("download", "deleded");
    }








    protected void checkPermission(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                    // show an alert dialog
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage("Write external storage permission is required.");
                    builder.setTitle("Please grant permission");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(
                                    mActivity,
                                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                    MY_PERMISSION_REQUEST_CODE
                            );
                        }
                    });
                    builder.setNeutralButton("Cancel",null);
                    AlertDialog dialog = builder.create();
                    dialog.show();

                }else {
                    // Request permission
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSION_REQUEST_CODE
                    );

                }
            }else {

                loadAgain();
                // Permission already granted
            }
        }
        else {
            loadAgain();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch(requestCode){
            case MY_PERMISSION_REQUEST_CODE:{
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    // Permission granted
                    loadAgain();
                }else {
                    // Permission denied
                }
            }
        }
    }
}





