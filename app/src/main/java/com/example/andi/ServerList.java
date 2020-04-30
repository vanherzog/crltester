package com.example.andi;

import android.content.SharedPreferences;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;



public class ServerList {
    ArrayList<OCSP> listOcsp = new ArrayList<>();
    ArrayList<LDAP> listLdap = new ArrayList<>();
    ArrayList<HTTP> listHttp = new ArrayList<>();
    private static ServerList instance;
    private static SharedPreferences mSharedPreferences;
    private static final String TAG = "ServerList";

    private ServerList(){

    }

    public static synchronized ServerList getInstance (SharedPreferences sharedPreferences) {
        if (ServerList.instance == null) {
            ServerList.instance = new ServerList ();
        }
        mSharedPreferences = sharedPreferences;
        loadData();
        return ServerList.instance;
    }

    public void setUp(){
        instance.listHttp.add(new HTTP("HTTP_2014-2","http://crl.bundeswehr.org/Bw_V-PKI_CA_2014_-_2.crl"));
        instance.listHttp.add(new HTTP("HTTP_2015","http://crl.bundeswehr.org/Bw_V-PKI_CA_2015.crl"));
        instance.listHttp.add(new HTTP("HTTP_2016","http://crl.bundeswehr.org/Bw_V-PKI_CA_2016.crl"));
        instance.listHttp.add(new HTTP("HTTP_2017","http://crl.bundeswehr.org/Bw_V-PKI_CA_2017.crl"));
        instance.listLdap.add(new LDAP("LDAP_2013","ldap://ldap.bundeswehr.org/cn=Bw%20V-PKI%20CA%202013,ou=Bundeswehr,o=PKI-1-Verwaltung,c=DE?certificateRevocationList"));
        instance.listLdap.add(new LDAP("LDAP_2014","ldap://ldap.bundeswehr.org/cn=Bw%20V-PKI%20CA%202014,ou=Bundeswehr,o=PKI-1-Verwaltung,c=DE?certificateRevocationList"));
        instance.listLdap.add(new LDAP("LDAP_2014-2","ldap://ldap.bundeswehr.org/cn=Bw%20V-PKI%20CA%202014%20-%202,ou=Bundeswehr,o=PKI-1-Verwaltung,c=DE?certificateRevocationList"));
        instance.listLdap.add(new LDAP("LDAP_2015","ldap://ldap.bundeswehr.org/cn=Bw%20V-PKI%20CA%202015,ou=Bundeswehr,o=PKI-1-Verwaltung,c=DE?certificateRevocationList"));
        instance.listLdap.add(new LDAP("LDAP_2016","ldap://ldap.bundeswehr.org/cn=Bw%20V-PKI%20CA%202016,ou=Bundeswehr,o=PKI-1-Verwaltung,c=DE?certificateRevocationList"));
        instance.listLdap.add(new LDAP("LDAP_2017","ldap://ldap.bundeswehr.org/cn=Bw%20V-PKI%20CA%202017,ou=Bundeswehr,o=PKI-1-Verwaltung,c=DE?certificateRevocationList"));
        instance.listLdap.add(new LDAP("LDAP_2017-2","ldap://ldap.bundeswehr.org/cn=Bw%20V-PKI%20CA%202017%20-%202,ou=Bundeswehr,o=PKI-1-Verwaltung,c=DE?certificateRevocationList"));
        instance.listLdap.add(new LDAP("LDAP_2017-3","ldap://ldap.bundeswehr.org/cn=Bw%20V-PKI%20CA%202017%20-%203,ou=Bundeswehr,o=PKI-1-Verwaltung,c=DE?certificateRevocationList"));
        saveData();
    }

    public ArrayList<? extends Server> getOCSP(){
        return instance.listOcsp;
    }

    public void addOCSP(OCSP p){
        instance.listOcsp.add(p);
        saveData();
    }
    public void removeOCSP(Integer position){
        instance.listOcsp.remove(listOcsp.get(position));
        saveData();
    }
    public void setOcsp(Integer position, String name, String link, String cert, BigInteger seriennummer, String hash){
        instance.listOcsp.set(position, listOcsp.get(position)).setName(name);
        instance.listOcsp.set(position, listOcsp.get(position)).setLink(link);
        instance.listOcsp.set(position, listOcsp.get(position)).setCert(cert);
        instance.listOcsp.set(position, listOcsp.get(position)).setSeriennummer(seriennummer);
        instance.listOcsp.set(position, listOcsp.get(position)).setHash(hash);
        saveData();
    }

    public ArrayList<? extends Server> getLDAP(){
        return instance.listLdap;
    }

    public void addLDAP(LDAP p){
        instance.listLdap.add(p);
        saveData();
    }
    public void removeLDAP(Integer position){
        instance.listLdap.remove(listLdap.get(position));
        saveData();
    }
    public void setLDAP(Integer position, String name, String link){
        instance.listLdap.set(position, listLdap.get(position)).setName(name);
        instance.listLdap.set(position, listLdap.get(position)).setLink(link);
        saveData();
    }

    public ArrayList<? extends Server> getHTTP(){
        return instance.listHttp;
    }

    public void addHTTP(HTTP p){
        instance.listHttp.add(p);
        saveData();
    }
    public void removeHTTP(Integer position){
        instance.listHttp.remove(listHttp.get(position));
        saveData();
    }
    public void setHTTP(Integer position, String name, String link){
        instance.listHttp.set(position, listHttp.get(position)).setName(name);
        instance.listHttp.set(position, listHttp.get(position)).setLink(link);
        saveData();
    }


    private static void loadData(){
        Gson gson = new Gson();
        String json = mSharedPreferences.getString("ServerList", null);
        if(null!=json) {
            Log.d(TAG, "loadData: " + json);
            Type typ = new TypeToken<ServerList>() {
            }.getType();
            instance = gson.fromJson(json, typ);
        }
    }



    private void saveData(){
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(instance);
        Log.d(TAG, "saveData: " + json);
        editor.putString("ServerList", json);
        editor.apply();
    }

}
