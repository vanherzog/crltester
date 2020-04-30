package com.example.andi;

import android.util.Log;

import java.math.BigInteger;

public class LDAP extends Server {
    private static final String TAG ="LDAP";


    public LDAP(String name, String link) {
        super(name, link);
    }

    public LDAP(){

    }
    @Override
    public void check() {
        Log.d(TAG, "check: " + getLink());
    }

    @Override
    public BigInteger getSeriennummer() {
        return null;
    }

    @Override
    public String getHash() {
        return null;
    }

    @Override
    public String getCert() {
        return null;
    }
}
