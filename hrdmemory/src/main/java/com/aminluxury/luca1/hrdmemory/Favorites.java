package com.aminluxury.luca1.hrdmemory;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.*;

import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Favorites extends Fragment {
    ListView listView ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.favorites, container, false);
        // getWindow().requestFeature(Window.FEATURE_ACTION_MODE_OVERLAY);


        return view;
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //No call for super().
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles

        // Get ListView object from xml
        listView = (ListView) getView().findViewById(R.id.list);
        final SharedPreferences preferences = getContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE);
        ArrayList<String> values =  new ArrayList<String>();
        Map<String, ?> allEntries = preferences.getAll();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            Log.d("map values", entry.getKey() + ": " + entry.getValue().toString());
            String str = entry.getKey();
            str = str.replaceAll("\\D+","");
            if (str.length()>=10) {
                values.add(str);
            }
        }



        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                R.layout.row_layout, R.id.mainTextCell, values);



        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.cancel_code);
                builder.setTitle(R.string.cancel_favorites);
                builder.setPositiveButton(R.string.ok_proceed, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String  itemValue    = (String) listView.getItemAtPosition(position);
                        values.remove(position);


                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(itemValue);
                        editor.apply();


                        adapter.notifyDataSetChanged();

                        Toast.makeText(getContext(), R.string.cancella, Toast.LENGTH_LONG).show();
                    }
                });
                builder.setNeutralButton(R.string.cancel,null);
                AlertDialog dialog = builder.create();
                dialog.show();



                return true;
            }

        });



        // ListView Item Click Listener
        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // ListView Clicked item index
                int itemPosition     = position;

                // ListView Clicked item value
                String  itemValue    = (String) listView.getItemAtPosition(position);


// then you use
                Set<String> set = preferences.getStringSet(itemValue, new HashSet<String>());
                FragmentDiamond fragment2 = new FragmentDiamond();
                List<String> list = new ArrayList<String>(set);
                Iterator iter = set.iterator();
                for (String element : list) {
                    if (element.contains("html:"))
                    {
                        fragment2.htmlString = element.replace("html:","");
                    }
                    if (element.contains("carat:"))
                    {
                        fragment2.carat = element.replace("carat:","");
                    }
                    if (element.contains("colour:"))
                    {
                        fragment2.colour = element.replace("colour:","");
                    }
                    if (element.contains("shape:"))
                    {
                        fragment2.shape = element.replace("shape:","");
                    }
                    if (element.contains("code:"))
                    {
                        fragment2.code = element.replace("code:","");
                    }
                    if (element.contains("clarity:"))
                    {
                        fragment2.clarity = element.replace("clarity:","");
                    }
                    if (element.contains("certificate:"))
                    {
                        fragment2.certificateLink = element.replace("certificate:","");
                    }
                }

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment2);
                fragmentTransaction.commit();


            }

        });
    }






}
