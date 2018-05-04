package com.example.mastermind.testapp;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;

import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.nex3z.notificationbadge.NotificationBadge;
import com.txusballesteros.bubbles.BubbleLayout;
import com.txusballesteros.bubbles.BubblesManager;
import com.txusballesteros.bubbles.OnInitializedCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity  {
    AccessServiceAPI m_AccessServiceAPI;
    SharedPreferences settingsPreferences;
    ArrayList<JobOffer> offers;
    ArrayList<JobOffer> asyncOffers;
    private int MY_PERMISSION = 1000;
    private BubblesManager bubblesManager;
    private NotificationBadge mBadge;
    private int count;
    BroadcastReceiver br;

    NotificationCompat.Builder notification;
    private static final int uniqueID = 45612;

    private PendingIntent pendingIntentA;


    ListView lv;
    DateFormat format;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Datalabs");
        setSupportActionBar(toolbar);
        br = new ConnectivityChangeReceiver();
        lv = findViewById(R.id.listView);
        m_AccessServiceAPI = new AccessServiceAPI();
        asyncOffers = new ArrayList<>();
        offers = new ArrayList<>();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        registerReceiver(br,new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println(settingsPreferences.getInt("numberOfCategories", 0) == 0);
        System.out.println(settingsPreferences.getInt("numberOfCheckedCategories", 0) == 0);


        System.out.println(settingsPreferences.getBoolean("checkIsChanged", false));

        if(getIntent().hasExtra("source")){
            for (int i = 0; i < settingsPreferences.getInt("numberOfUnseenOffers", 0); i++) {

                JobOffer jobOffer = new JobOffer();
                jobOffer.setId(settingsPreferences.getInt("offerId " + i, 0));
                jobOffer.setCatid(settingsPreferences.getInt("offerCatid " + i, 0));
                jobOffer.setTitle(settingsPreferences.getString("offerTitle " + i, ""));
                jobOffer.setDate(new Date(settingsPreferences.getLong("offerDate " + i, 0)));
                jobOffer.setDownloaded(settingsPreferences.getString("offerDownloaded " + i, ""));
                offers.add(jobOffer);
            }
        }else {

            for (int i = 0; i < settingsPreferences.getInt("numberOfOffers", 0); i++) {

                JobOffer jobOffer = new JobOffer();
                jobOffer.setId(settingsPreferences.getInt("offerId " + i, 0));
                jobOffer.setCatid(settingsPreferences.getInt("offerCatid " + i, 0));
                jobOffer.setTitle(settingsPreferences.getString("offerTitle " + i, ""));
                jobOffer.setDate(new Date(settingsPreferences.getLong("offerDate " + i, 0)));
                jobOffer.setDownloaded(settingsPreferences.getString("offerDownloaded " + i, ""));
                offers.add(jobOffer);
            }
        }
        System.out.println(offers.toString());
        JobOfferAdapter jobOfferAdapter = new JobOfferAdapter(getApplicationContext(), offers);
        lv.setAdapter(jobOfferAdapter);
        System.out.println(settingsPreferences.getLong("interval",0));

        unregisterReceiver(br);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent intent = new Intent(MainActivity.this,SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
