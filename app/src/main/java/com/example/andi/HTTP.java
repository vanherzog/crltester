package com.example.andi;

import android.util.Log;

import java.math.BigInteger;

import crltester.test.http.HTTPCrlState;
import crltester.test.ocsp.OCSPCertState;

public class HTTP extends Server {

    private static final String TAG ="HTTP";

    public HTTP(String name, String link){
        super(name, link);
        super.status = new HTTPCrlState();
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
