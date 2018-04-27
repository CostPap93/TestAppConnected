package com.example.mastermind.testapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by mastermind on 18/4/2018.
 */

public class SettingActivity  extends AppCompatActivity {
    SharedPreferences settingsPreferences;
    AccessServiceAPI m_AccessServiceAPI;
    CheckBox checkBox;
    ListView lv;
    Button btnSave, btnCancel;
    RadioButton radioButton, radioButton1, radioButton2;
    ArrayList<Boolean> checkIsChanged;
    ArrayList<JobOffer> asyncOffers;
    ArrayList<OfferCategory> categories;
    SimpleDateFormat format;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        m_AccessServiceAPI = new AccessServiceAPI();
        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        checkBox = findViewById(R.id.chbox_category);
        lv = findViewById(R.id.lv_categories);
        btnSave = findViewById(R.id.btn_save);
        btnCancel = findViewById(R.id.btn_cancel);
        radioButton = findViewById(R.id.rb_day);
        radioButton1 = findViewById(R.id.rb_once);
        radioButton2 = findViewById(R.id.rb_twice);
        checkIsChanged = new ArrayList<>();
        asyncOffers = new ArrayList<>();
        categories = new ArrayList<>();
        format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        System.out.println(settingsPreferences.getInt("numberOfCategories", 0));
        settingsPreferences.edit().putBoolean("checkIsChanged", false).apply();

        if (settingsPreferences.getInt("numberOfCategories", 0) != 0) {
            for (int i = 0; i < settingsPreferences.getInt("numberOfCategories", 0); i++) {
                OfferCategory category = new OfferCategory();
                category.setCatid(settingsPreferences.getInt("offerCategoryId " + i, 0));
                category.setTitle(settingsPreferences.getString("offerCategoryTitle " + i, ""));
                categories.add(category);
                System.out.println(categories.get(i).getTitle()+"checkBoxAdapter");
            }
            CheckBoxAdapter checkBoxAdapter = new CheckBoxAdapter(getApplicationContext(), categories);

            lv.setAdapter(checkBoxAdapter);


            checkBoxAdapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, "You have to be connected to get the categories", Toast.LENGTH_LONG);
        }


        if (settingsPreferences.getLong("interval", 0) == 86400000) {
            radioButton.setChecked(true);
        } else if (settingsPreferences.getLong("interval", 0) == 604800000) {
            radioButton1.setChecked(true);
        } else {
            radioButton2.setChecked(true);
        }


    }


    public void btnSaveClicked(View view) {
        int j = 0;
        for (int x = 0; x < lv.getChildCount(); x++) {
            checkBox = (CheckBox) lv.getChildAt(x).findViewById(R.id.chbox_category);
            System.out.println(checkBox.getText() + " BTNSAVECLICKED " + checkBox.isChecked());
            for(int c=0;c<settingsPreferences.getInt("numberOfCheckedCategories",0);c++) {

                System.out.println((checkBox.getText().equals(settingsPreferences.getString("checkedCategoryTitle " + x, ""))));
                System.out.println(settingsPreferences.getString("checkedCategoryTitle " + c, "") + "For remove");

                if (checkBox.getText().equals(settingsPreferences.getString("checkedCategoryTitle " + c, ""))) {
                    if (!checkBox.isChecked()) {

                        System.out.println(settingsPreferences.getString("checkedCategoryTitle " + c, "") + " removed");
                        settingsPreferences.edit().remove("checkedCategoryTitle " + c).apply();
                        settingsPreferences.edit().remove("checkedCategoryId " + c).apply();
                        settingsPreferences.edit().putBoolean("checkIsChanged", true).apply();
                        j--;
                    } else
                        System.out.println(settingsPreferences.getString("checkedCategoryTitle " + c, "") + "Unchecked but not remove");


                } else {
                    if (checkBox.isChecked()) {
                        System.out.println(settingsPreferences.getString("checkedCategoryTitle " + c, "") + "For add");
                        settingsPreferences.edit().putInt("checkedCategoryId " + c, c + 1).apply();
                        settingsPreferences.edit().putString("checkedCategoryTitle " + c, String.valueOf(checkBox.getText())).apply();
                        settingsPreferences.edit().putBoolean("checkIsChanged", true).apply();
                        System.out.println(settingsPreferences.getString("checkedCategoryTitle " + c, "") + " added");
                        j++;
                    } else
                        System.out.println(settingsPreferences.getString("checkedCategoryTitle " + c, "") + "Checked but not add");
                }
            }



            System.out.println(settingsPreferences.getInt("offerCategoryId " + x,0));
            System.out.println(settingsPreferences.getString("offerCategoryTitle " + x,""));
            System.out.println(settingsPreferences.getBoolean("checkIsChanged",false));
        }
        System.out.println(checkIsChanged.toString());
        settingsPreferences.edit().putInt("numberOfCheckedCategories",settingsPreferences.getInt("numberOfCheckedCategories",0)+j).apply();

        if (settingsPreferences.getBoolean("checkIsChanged", true)) {
            System.out.println(settingsPreferences.getInt("numberOfCheckedCategories", 0)+"in the settings taskshow");
            for (int v = 0; v < (settingsPreferences.getInt("numberOfCheckedCategories", 0)); v++) {
                if (settingsPreferences.getInt("checkedCategoryId " + v, 0) != 0) {

                    new TaskShowOffersFromCategories().execute(String.valueOf(settingsPreferences.getInt("checkedCategoryId " + v, 0)));
                }
            }


        }


        if (radioButton.isChecked()) {
            if (!(settingsPreferences.getLong("interval", 0) == 86400000)) {
                settingsPreferences.edit().putLong("interval", 86400000).apply();

            }
        } else if (radioButton1.isChecked()) {
            if (!(settingsPreferences.getLong("interval", 0) == 604800000)) {
                settingsPreferences.edit().putLong("interval", 604800000).apply();

            }
        } else {
            if (!(settingsPreferences.getLong("interval", 0) == 302400000)) {
                settingsPreferences.edit().putLong("interval", 302400000).apply();

            }
        }

        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void btnCancelClicked(View view) {
        Intent intent = new Intent(SettingActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void btnResetClicked(View view){

        settingsPreferences.edit().clear().apply();
        Intent intent = new Intent(SettingActivity.this,MainActivity.class);
        startActivity(intent);
    }

    public class TaskShowOffersFromCategories extends AsyncTask<String, Integer, ArrayList<JobOffer>> {

        ArrayList<JobOffer> fiveOffers = new ArrayList<>();


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

            for (int i = 0; i < fiveOffers.size(); i++) {
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

        }

        @Override
        protected ArrayList<JobOffer> doInBackground(String... params) {
            Map<String, String> postParam = new HashMap<>();
            postParam.put("action", "showOffersFromCategory");
            postParam.put("jacat_id", params[0]);

            try {
                String jsonString = m_AccessServiceAPI.getJSONStringWithParam_POST("http://10.0.2.2/android/jobAds.php?", postParam);
                JSONObject jsonObjectAll = new JSONObject(jsonString);
                JSONArray jsonArray = jsonObjectAll.getJSONArray("offers");
                int i = 0;

                while (i < jsonArray.length() && i < 5) {
                    i++;

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
                            if (jobOffer.getDate().getTime() - t1.getDate().getTime() < 0)
                                return 1;
                            else if (jobOffer.getDate().getTime() - t1.getDate().getTime() == 0)
                                return 0;
                            else
                                return -1;
                        }
                    });
                    for (int x = 0; x < asyncOffers.size(); x++) {
                        System.out.println(asyncOffers.get(x).getTitle());
                    }

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

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }


    public boolean isConn() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConn = networkInfo.isConnected();
        networkInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConn = networkInfo.isConnected();
        Log.d("connection", "Wifi connected: " + isWifiConn);
        Log.d("connection", "Mobile connected: " + isMobileConn);
        return isWifiConn || isMobileConn;
    }
}

