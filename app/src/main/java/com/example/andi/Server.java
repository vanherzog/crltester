package com.example.andi;


import android.util.Log;

import java.math.BigInteger;

import crltester.test.State;

public abstract class Server{

    public static final int STATUS_RED = 3;
    public static final int STATUS_YELLOW = 2;
    public static final int STATUS_GREEN = 1;

    private String name;
    private String link;
    protected State status;
    private static final String TAG ="Server";

    public Server(String name, String link) {
        this.name = name;
        this.link = link;
        this.status = new State();
    }
    public Server(){
        this.status = new State();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public abstract void check();

    public void setStatus(State status){
        this.status = status;
    }

    public State getStatus(){
        return status;
    }

    public abstract BigInteger getSeriennummer();

    public abstract String getHash();

    public abstract String getCert();
}
