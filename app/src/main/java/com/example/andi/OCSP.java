package com.example.andi;

import android.util.Log;

import java.math.BigInteger;

public class OCSP extends Server{
    private static final String TAG ="OCSP";
    BigInteger seriennummer;
    String hash;
    String cert;

    public OCSP(String name, String link, String cert, BigInteger seriennummer, String hash) {
        super(name,link);
        this.cert=cert;
        this.seriennummer = seriennummer;
        this.hash = hash;
    }

    public OCSP(){

    }

    public String getCert() {
        return cert;
    }

    public void setCert(String cert) {
        this.cert = cert;
    }

    public BigInteger getSeriennummer() {
        return seriennummer;
    }

    public void setSeriennummer(BigInteger seriennummer) {
        this.seriennummer = seriennummer;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    @Override
    public void check() {
        Log.d(TAG, "check: " + getLink());
    }


}
