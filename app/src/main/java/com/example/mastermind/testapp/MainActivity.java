package com.example.mastermind.testapp;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
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
        lv = findViewById(R.id.listView);
        m_AccessServiceAPI = new AccessServiceAPI();
        asyncOffers = new ArrayList<>();
        offers = new ArrayList<>();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        System.out.println(settingsPreferences.getInt("numberOfCategories", 0) == 0);
        System.out.println(settingsPreferences.getInt("numberOfCheckedCategories", 0) == 0);


        System.out.println(settingsPreferences.getBoolean("checkIsChanged", false));

        if (settingsPreferences.getInt("numberOfCategories", 0) == 0 && isConn()) {
            settingsPreferences.edit().putLong("interval", 86400000).apply();

            new TaskSetDefaultCateogries().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

            for (int v = 0; v < (settingsPreferences.getInt("numberOfCheckedCategories", 0)); v++) {
                if (settingsPreferences.getInt("checkedCategoryId " + v, 0) != 0) {
                    System.out.println(settingsPreferences.getInt("checkedCategoryId " + v, 0) + "Before the task show for the first time");
                    System.out.println(settingsPreferences.getString("checkedCategoryTitle " + v, ""));
                    new TaskShowOffersFromCategories().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(settingsPreferences.getInt("checkedCategoryId " + v, 0)));
                }
            }
        } else if (settingsPreferences.getInt("numberOfCategories", 0) == 0 && !isConn()) {
            Toast.makeText(this, "You Have To Be Connected To The Internet The First Time", Toast.LENGTH_LONG).show();
        } else {
            Thread thread = new Thread() {
                @Override
                public void run() {
                    for (int i = 0; i < settingsPreferences.getInt("numberOfOffers", 0); i++) {

                        JobOffer jobOffer = new JobOffer();
                        jobOffer.setId(settingsPreferences.getInt("offerId " + i, 0));
                        jobOffer.setCatid(settingsPreferences.getInt("offerCatid " + i, 0));
                        jobOffer.setTitle(settingsPreferences.getString("offerTitle " + i, ""));
                        jobOffer.setDate(new Date(settingsPreferences.getLong("offerDate " + i, 0)));

                        jobOffer.setDownloaded(settingsPreferences.getString("offerDownloaded " + i, ""));
                        offers.add(jobOffer);
                    }
                    System.out.println(offers.toString());
                    JobOfferAdapter jobOfferAdapter = new JobOfferAdapter(getApplicationContext(), offers);
                    lv.setAdapter(jobOfferAdapter);
                }
            };

            thread.start();
            try {
                thread.join(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        start();
    }

    public void start() {

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent alarmIntent = new Intent(MainActivity.this, AlarmReceiver.class);
        pendingIntentA = PendingIntent.getBroadcast(MainActivity.this, 0, alarmIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 6000, pendingIntentA);

        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntentA);
        Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
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

    public class TaskShowOffersFromCategories extends AsyncTask<String,Integer,ArrayList<JobOffer>> {
        SharedPreferences settingsPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());


        protected void onPreExecute() {



            super.onPreExecute();

        }

        @Override
        protected void onPostExecute(ArrayList<JobOffer> fiveOffers) {


            for(int j=0;j<5;j++){
                settingsPreferences.edit().remove("offerId "+j).apply();
                settingsPreferences.edit().remove("offerCatid "+j).apply();
                settingsPreferences.edit().remove("offerTitle "+j).apply();
                settingsPreferences.edit().remove("offerDate "+j).apply();
                settingsPreferences.edit().remove("offerDownloaded "+j).apply();
            }
            for(int i=0;i<fiveOffers.size();i++) {
                if(i<5) {

                    settingsPreferences.edit().putInt("offerId " + i, fiveOffers.get(i).getId()).apply();
                    settingsPreferences.edit().putInt("offerCatid " + i, fiveOffers.get(i).getCatid()).apply();
                    settingsPreferences.edit().putString("offerTitle " + i, fiveOffers.get(i).getTitle()).apply();
                    settingsPreferences.edit().putLong("offerDate " + i, fiveOffers.get(i).getDate().getTime()).apply();
                    settingsPreferences.edit().putString("offerDownloaded " + i, fiveOffers.get(i).getDownloaded()).apply();
                    System.out.println(settingsPreferences.getLong("offerDate " + i, 0));
                    System.out.println(settingsPreferences.getString("offerTitle " + i, ""));
                    settingsPreferences.edit().putInt("numberOfOffers",fiveOffers.size()).apply();
                }else
                    settingsPreferences.edit().putInt("numberOfOffers",5).apply();
            }

            settingsPreferences.edit().putLong("lastSeenDate",fiveOffers.get(settingsPreferences.getInt("numberOfOffers",0)-1).getDate().getTime()).apply();
            System.out.println(settingsPreferences.getLong("lastSeenDate",0));

            if (!(settingsPreferences.getInt("numberOfCheckedCategories", 0) == 0)) {
                for (int i = 0; i < settingsPreferences.getInt("numberOfOffers", 0); i++) {

                    JobOffer jobOffer = new JobOffer();
                    jobOffer.setId(settingsPreferences.getInt("offerId " + i, 0));
                    jobOffer.setCatid(settingsPreferences.getInt("offerCatid " + i, 0));
                    jobOffer.setTitle(settingsPreferences.getString("offerTitle " + i, ""));
                    jobOffer.setDate(new Date(settingsPreferences.getLong("offerDate " + i, 0)));

                    jobOffer.setDownloaded(settingsPreferences.getString("offerDownloaded " + i, ""));
                    offers.add(jobOffer);

                }
                System.out.println(offers.toString());

                JobOfferAdapter jobOfferAdapter = new JobOfferAdapter(getApplicationContext(), offers);

                lv.setAdapter(jobOfferAdapter);

            }






        }

        @Override
        protected ArrayList<JobOffer> doInBackground(String... params) {
            Map<String, String> postParam = new HashMap<>();
            postParam.put("action", "showOffersFromCategory");
            postParam.put("jacat_id",params[0]);

            try {
                String jsonString = m_AccessServiceAPI.getJSONStringWithParam_POST("http://10.0.2.2/android/jobAds.php?", postParam);
                JSONObject jsonObjectAll = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObjectAll.getJSONArray("offers");
                int i = 0;

                while(i<jsonArray.length() && i<5) {

                    JSONObject jsonObjectCategory = jsonArray.getJSONObject(i);

                    JobOffer offer = new JobOffer();
                    offer.setId(Integer.valueOf(jsonObjectCategory.getString("jad_id")));
                    offer.setCatid(Integer.valueOf(jsonObjectCategory.getString("jad_catid")));
                    offer.setTitle(jsonObjectCategory.getString("jad_title"));
                    offer.setDate(format.parse(jsonObjectCategory.getString("jad_date")));
                    offer.setDownloaded(jsonObjectCategory.getString("jad_downloaded"));
                    System.out.println(offer.getTitle() + " first time");

                    asyncOffers.add(offer);


                    Collections.sort(asyncOffers, new Comparator<JobOffer>() {
                        @Override
                        public int compare(JobOffer jobOffer, JobOffer t1) {
                            if(jobOffer.getDate().getTime()-t1.getDate().getTime()<0)
                                return 1;
                            else if(jobOffer.getDate().getTime()-t1.getDate().getTime()==0)
                                return 0;
                            else
                                return -1;
                        }
                    });
                    for(int x=0;x<asyncOffers.size();x++) {
                        System.out.println(asyncOffers.get(x).getTitle());
                    }

                    i++;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return asyncOffers;
        }

    }

    public boolean isConn(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();
        Log.d("connection", "Wifi connected: " + isWifiConn);
        Log.d("connection", "Mobile connected: " + isMobileConn);
        return isWifiConn || isMobileConn;
    }

    private void initBubble(){
        bubblesManager = new BubblesManager.Builder(this)
                .setTrashLayout(R.layout.bubble_remove)
                .setInitializationCallback(new OnInitializedCallback() {
                    @Override
                    public void onInitialized() {
                        count=0;
                    }
                }).build();
        bubblesManager.initialize();
    }

    private void addNewBubble(int count){
        BubbleLayout bubbleView = (BubbleLayout) LayoutInflater.from(this)
                .inflate(R.layout.bubble_layout,null);
        mBadge = (NotificationBadge) bubbleView.findViewById(R.id.badge);
        mBadge.setNumber(count);

        bubbleView.setOnBubbleRemoveListener(new BubbleLayout.OnBubbleRemoveListener() {
            @Override
            public void onBubbleRemoved(BubbleLayout bubble) {
                Toast.makeText(MainActivity.this, "Removed", Toast.LENGTH_SHORT).show();
            }
        });

        bubbleView.setOnBubbleClickListener(new BubbleLayout.OnBubbleClickListener() {
            @Override
            public void onBubbleClick(BubbleLayout bubble) {
                Toast.makeText(MainActivity.this,"Clicked",Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(intent);

            }
        });


        bubblesManager.addBubble(bubbleView,60,20);


    }

}
