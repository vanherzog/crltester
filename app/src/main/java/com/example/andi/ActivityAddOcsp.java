package com.example.andi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import static java.lang.System.in;
//out = Environment.getExternalStorageDirectory().getAbsolutePath() + "/CAs/";
//File outFile = new File(out, displayName);
//copyFile(getAssets().open(out + displayName), new FileOutputStream(outFile));

public class ActivityAddOcsp extends ActivityEdit{
    private static final String TAG = "ActivityAddOcsp";
    String filepath;
    Uri selectedFile;
    LinearLayout uploader_area;
    String name;
    protected TextInputLayout textInputLink;
    protected TextInputLayout textInputUsername;
    protected TextInputLayout textInputSeriennummer;
    Spinner dropdown;
    String displayName;
    String out;
    boolean upload = false;
    TextView textview2;
    String pathname;

    File myFile = null;


    private static final int REQUEST_WRITE_STORAGE = 112;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.popout_layout_ocsp);

        uploader_area = (LinearLayout) findViewById(R.id.uploader_area);
        Button select_button = (Button) findViewById(R.id.button_selectpic);
        Button upload_button = (Button) findViewById(R.id.button_upload);
        textInputUsername =findViewById(R.id.text_input_username);
        textInputLink=findViewById(R.id.text_input_link);
        textInputSeriennummer=findViewById(R.id.text_input_serienNr);
        dropdown = findViewById(R.id.spinner1);
        textview2 = findViewById(R.id.textView2);


        Boolean hasPermission = (ContextCompat.checkSelfPermission(ActivityAddOcsp.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(ActivityAddOcsp.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }else {

        }

        //create a list of items for the spinner.
        String[] items = new String[]{"Wähle einen aus","SHA-1", "SHA-256", "SHA-512", "MD5", "RIPEMD160"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);



        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
            }
        });



        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    test123();
                    //moveFile(myFile, getFilesDir());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void test123(){
        // your sd card
        String sdCard = Environment.getExternalStorageDirectory().getAbsolutePath();

        // the file to be moved or copied
        File sourceLocation = new File (sdCard + "/Download/" +  displayName);

        // make sure your target location folder exists!
        pathname = "/data/user/0/com.example.andi/files/"  + displayName;
        File targetLocation = new File(pathname);

        // just to take note of the location sources
        System.out.println( "XXXXXXXXXXXXXXXXXXXXXsourceLocation: " + sourceLocation);
        System.out.println( "XXXXXXXXXXXXXXXXXXXXXtargetLocation: " + targetLocation);

        try {
            // 1 = move the file, 2 = copy the file
            int actionChoice = 2;
            // moving the file to another directory
            if(actionChoice==1){
                if(sourceLocation.renameTo(targetLocation)){
                    System.out.println( "Move file successful.");
                }else{
                    System.out.println( "Move file failed.");
                }
            }
            // we will copy the file
            else{
                // make sure the target file exists
                if(sourceLocation.exists()){
                    InputStream in = new FileInputStream(sourceLocation);
                    OutputStream out = new FileOutputStream(targetLocation);
                    // Copy the bits from instream to outstream
                    byte[] buf = new byte[1024];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                    in.close();
                    out.close();

                    System.out.println( "Copy file successful.");
                    upload = true;
                }else{
                    System.out.println( "XXXXXXXXXXXXXXXXXXXXXXXx Copy file failed. Source file missing.");
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean copyFile(File source, File dest){
        try{
            // Declaration et ouverture des flux
            java.io.FileInputStream sourceFile = new java.io.FileInputStream(source);

            try{
                java.io.FileOutputStream destinationFile = null;

                try{
                    destinationFile = new FileOutputStream(dest);

                    // Lecture par segment de 0.5Mo
                    byte buffer[] = new byte[512 * 1024];
                    int nbLecture;

                    while ((nbLecture = sourceFile.read(buffer)) != -1){
                        destinationFile.write(buffer, 0, nbLecture);
                    }
                } finally {
                    destinationFile.close();
                }
            } finally {
                sourceFile.close();
            }
        } catch (IOException e){
            e.printStackTrace();
            return false; // Erreur
        }

        return true; // Rsultat OK
    }

    public boolean moveFile(File source,File destination) throws FileNotFoundException {
        if( !destination.exists() ) {
            boolean result = source.renameTo(destination);
            if( !result ) {
                result = true;
                result &= copyFile(source,destination);
                if(result) result &= source.delete();

            } return(result);
        } else {
            return(false);
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    Toast.makeText(ActivityAddOcsp.this, "You must give access to storage.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1 && resultCode == RESULT_OK) {
            // Get the Uri of the selected file
            Uri uri = data.getData();
            String uriString = uri.toString();
            myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = ActivityAddOcsp.this.getContentResolver().query(uri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                } finally {
                    cursor.close();
                }
            } else if (uriString.startsWith("file://")) {
                displayName = myFile.getName();
            }
            Log.d(TAG, "onActivityResult: " + displayName);
            selectedFile = data.getData();
            Log.d(TAG, "onActivityResult:" + selectedFile);
            filepath = selectedFile.getPath();
            Log.d(TAG, "onActivityResult:" + filepath);
            System.out.println("Filepath:  " + filepath);
            System.out.println("selectedFile:  " + selectedFile);
            System.out.println("Displayname:  " + displayName);

        }
    }

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Files.FileColumns.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    protected boolean validateUsername(){
        String emailInput = textInputUsername.getEditText().getText().toString().trim();

        if(emailInput.isEmpty()){
            textInputUsername.setError("Field cant be empty");
            return false;
        }else{
            textInputUsername.setError(null);
            textInputUsername.setErrorEnabled(false);
            return true;
        }
    }

    protected boolean validateLink() {
        String usernameInput = textInputLink.getEditText().getText().toString().trim();
        if (usernameInput.isEmpty()) {
            textInputLink.setError("Field cant be empty");
            return false;
        }else if(usernameInput.length() < 10){
            textInputLink.setError("too short");
            return false;
        }
        else{
            textInputLink.setError(null);
            return true;
        }
    }


    protected boolean validateSeriennummer() {
        String sNrInput = textInputSeriennummer.getEditText().getText().toString().trim();
        if (sNrInput.isEmpty()) {
            textInputSeriennummer.setError("Field cant be empty");
            return false;
        }else if(sNrInput.length() < 2){
            textInputSeriennummer.setError("too short");
            return false;
        }
        else{
            textInputSeriennummer.setError(null);
            return true;
        }
    }

    protected boolean validateDropdown(){
        if (dropdown.getSelectedItem().toString().trim().equals("Wähle einen aus")) {
            Toast.makeText(ActivityAddOcsp.this, "Keinen Hashalgorithmus ausgwählt", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    protected boolean validateCert(){
        if(!upload){
            Toast.makeText(ActivityAddOcsp.this, "Kein Zertifikat ausgewählt", Toast.LENGTH_SHORT).show();
        }
        return upload;
    }

    //Button OnClick

    public void confirmInput(View v){
        if(!validateUsername() | !validateLink() | !validateSeriennummer() | !validateDropdown() | !validateCert()){
            return;
        }
        String input = "Email: " + textInputUsername.getEditText().getText().toString();
        input += "\n";
        input += "Username: " + textInputLink.getEditText().getText().toString();
        input += "\n";
        input += "Seriennummer " + textInputSeriennummer.getEditText().getText().toString();
        input += "\n";
        input += "Hashalgorithmus " + dropdown.getSelectedItem().toString();


        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
        addServer();

        Intent intent = new Intent(ActivityAddOcsp.this, ActivityEdit.class);
        intent.putExtra("Type", type);
        startActivity(intent);
    }




    private void addServer(){
        final ServerList serverList = ServerList.getInstance(getSharedPreferences("SharedPreferences", MODE_PRIVATE));
        name = textInputUsername.getEditText().getText().toString();
        String link = textInputLink.getEditText().getText().toString();
        BigInteger b = new BigInteger(textInputSeriennummer.getEditText().getText().toString());

        String hash = dropdown.toString();
        String cert = pathname;

        switch(type){
            case 1:
                serverList.addOCSP(new OCSP(name, link, cert, b, hash));
                break;
            case 2:
                serverList.addLDAP(new LDAP(name, link));
                break;
            case 3:
                serverList.addHTTP(new HTTP(name, link));
                break;
        }
    }
}


