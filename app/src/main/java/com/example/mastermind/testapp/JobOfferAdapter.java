package com.example.mastermind.testapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by mastermind on 19/4/2018.
 */

public class JobOfferAdapter extends BaseAdapter{
    Context context;
    ArrayList<JobOffer> offers = new ArrayList<>();
    SharedPreferences settingsPreferences;
    CheckBox checkBox;


    public JobOfferAdapter(Context context, ArrayList<JobOffer> offers) {
        this.context = context;
        this.offers = offers;
        this.settingsPreferences = PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Override
    public int getCount() {
        return offers.size();
    }

    @Override
    public Object getItem(int i) {
        return offers.get(i);
    }

    @Override
    public long getItemId(int i) {
        return offers.indexOf(getItem(i));
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.joboffer_list_item, null);
        }
        TextView txt_offertitle = view.findViewById(R.id.txtv_offertitle);
        TextView txt_offerdate = view.findViewById(R.id.txtv_offerdate);

        txt_offertitle.setText(offers.get(i).getTitle());
        txt_offerdate.setText(offers.get(i).getDate().toString());



        return view;
    }


}
