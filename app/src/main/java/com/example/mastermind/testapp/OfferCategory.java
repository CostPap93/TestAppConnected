package com.example.mastermind.testapp;

/**
 * Created by mastermind on 19/4/2018.
 */

public class OfferCategory {

    private int catid;
    private String title;

    public OfferCategory(int catid,String title,boolean checked){
        this.catid=catid;
        this.title = title;
    }
    public OfferCategory(){

    }

    public int getCatid() {
        return catid;
    }

    public void setCatid(int catid) {
        this.catid = catid;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
