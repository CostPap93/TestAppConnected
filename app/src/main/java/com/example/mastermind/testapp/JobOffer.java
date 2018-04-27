package com.example.mastermind.testapp;

import java.util.Date;

/**
 * Created by mastermind on 19/4/2018.
 */

public class JobOffer {

    private int id;
    private int catid;
    private String title;
    private Date date;
    private String downloaded;


    public JobOffer(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDownloaded() {
        return downloaded;
    }

    public void setDownloaded(String downloaded) {
        this.downloaded = downloaded;
    }
}
