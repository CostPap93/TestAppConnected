package com.example.mastermind.testapp;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by mastermind on 26/4/2018.
 */

public class TaskSetDefaultCateogries extends AsyncTask<Integer,Integer,String> {

    AccessServiceAPI m_AccessServiceAPI = new AccessServiceAPI();
    SharedPreferences settingsPreferences ;


    protected void onPreExecute() {
        settingsPreferences = PreferenceManager.getDefaultSharedPreferences(MyApplication.getAppContext());
        super.onPreExecute();;

    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);

    }

    @Override
    protected String doInBackground(Integer... params) {

        Map<String, String> postParam = new HashMap<>();
        postParam.put("action", "show");
        try {
            String jsonString = m_AccessServiceAPI.getJSONStringWithParam_POST("http://10.0.2.2/android/jobOfferCategories.php?", postParam);
            JSONObject jsonObjectAll = new JSONObject(jsonString);
            JSONArray jsonArray = jsonObjectAll.getJSONArray("joboffercategories");
            System.out.println(jsonArray.length());
            settingsPreferences.edit().putInt("numberOfCategories",jsonArray.length()).apply();
            settingsPreferences.edit().putInt("numberOfCheckedCategories",jsonArray.length()).apply();
            System.out.println(settingsPreferences.getInt("numberOfCategories",0));
            for(int i=0;i<jsonArray.length();i++) {
                JSONObject jsonObjectCategory = jsonArray.getJSONObject(i);
                settingsPreferences.edit().putInt("offerCategoryId " +i,Integer.valueOf(jsonObjectCategory.getString("jacat_id"))).apply();
                settingsPreferences.edit().putInt("checkedCategoryId " +i,Integer.valueOf(jsonObjectCategory.getString("jacat_id"))).apply();
                settingsPreferences.edit().putString("offerCategoryTitle " +i,jsonObjectCategory.getString("jacat_title")).apply();
                settingsPreferences.edit().putString("checkedCategoryTitle " +i,jsonObjectCategory.getString("jacat_title")).apply();
                System.out.println(jsonObjectCategory.toString());
                System.out.println(settingsPreferences.getInt("checkedCategoryId " +i,0) +"In The Task set Default");
                System.out.println(settingsPreferences.getString("checkedCategoryTitle " +i,""));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Task Completed";
    }

}