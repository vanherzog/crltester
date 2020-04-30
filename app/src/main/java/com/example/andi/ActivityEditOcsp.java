package com.example.andi;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.OpenableColumns;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class ActivityEditOcsp extends ActivityEdit {

    private static final String TAG = "ActivityEditOcsp";
    protected TextInputLayout textInputLink;
    protected TextInputLayout textInputUsername;
    protected TextInputLayout textInputSeriennummer;
    Spinner dropdown;
    String displayName;
    Integer position = 0;
    String filepath;
    Uri selectedFile;
    LinearLayout uploader_area;
    private static final int REQUEST_WRITE_STORAGE = 112;
    String out;
    boolean upload = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_layout);


        textInputUsername =findViewById(R.id.text_input_username);
        textInputLink=findViewById(R.id.text_input_link);
        textInputSeriennummer=findViewById(R.id.text_input_serienNr);
        dropdown = findViewById(R.id.spinner1);
        uploader_area = (LinearLayout) findViewById(R.id.uploader_area);
        Button select_button = (Button) findViewById(R.id.button_selectpic);
        Button upload_button = (Button) findViewById(R.id.button_upload);
        Intent intent = getIntent();
        position = intent.getIntExtra("Position",0);
        final ServerList serverList = ServerList.getInstance(getSharedPreferences("SharedPreferences", MODE_PRIVATE));


        switch (type){
            case 1:
                textInputUsername.getEditText().setText(serverList.listOcsp.get(position).getName());
                textInputLink.getEditText().setText(serverList.listOcsp.get(position).getLink());
                break;
            case 2:
                textInputUsername.getEditText().setText(serverList.listLdap.get(position).getName());
                textInputLink.getEditText().setText(serverList.listLdap.get(position).getLink());
                break;
            case 3:
                textInputUsername.getEditText().setText(serverList.listHttp.get(position).getName());
                textInputLink.getEditText().setText(serverList.listHttp.get(position).getLink());
                break;
        }

        //Popup Settings
        Window window = getWindow();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        window.setLayout((int) (width*.9),(int) (height*.35));
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setGravity(Gravity.TOP|Gravity.CENTER);

        // after that, setting values for x and y works "naturally"
        WindowManager.LayoutParams params = window.getAttributes();
        params.y = 300;
        window.setAttributes(params);

        Boolean hasPermission = (ContextCompat.checkSelfPermission(ActivityEditOcsp.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(ActivityEditOcsp.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }else {

        }

        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("*/*"); // intent.setType("video/*"); to select videos to upload
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), 1);
            }
        });


        upload_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null) {
                    out= Environment.getExternalStorageDirectory().getAbsolutePath() + "/CAs/" ;

                    File outFile = new File(out, displayName);
                    try {
                        copyFile(getAssets().open(filepath), new FileOutputStream(outFile));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    upload = true;
                    /**
                     //new UploadFileToServer().execute();
                     String[] parts = displayName.split(".");
                     String prefix = parts[0];
                     String suffix = parts[1];**/
                }else{
                    Toast.makeText(getApplicationContext(), "Please select a file to upload.", Toast.LENGTH_SHORT).show();
                }
            }
        });



        //create a list of items for the spinner.
        String[] items = new String[]{"Wähle einen aus", "SHA-1", "SHA-256", "SHA-512", "MD5", "RIPEMD160"};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        dropdown.setAdapter(adapter);


    }



    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }





    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode)
        {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    //reload my activity with permission granted or use the features what required the permission
                } else
                {
                    Toast.makeText(ActivityEditOcsp.this, "You must give access to storage.", Toast.LENGTH_LONG).show();
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
            File myFile = new File(uriString);
            String path = myFile.getAbsolutePath();
            displayName = null;

            if (uriString.startsWith("content://")) {
                Cursor cursor = null;
                try {
                    cursor = ActivityEditOcsp.this.getContentResolver().query(uri, null, null, null, null);
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
            Log.d(TAG, "XXXXXXXXXXXXXXXXXXXXonActivityResult:" + selectedFile);
            filepath = selectedFile.getPath();
            Log.d(TAG, "XXXXXXXXXXXXXXXXXXXxonActivityResult:" + filepath);
        }
    }

    protected boolean validateUsername(){
        String UsernameInput = textInputUsername.getEditText().getText().toString().trim();

        if(UsernameInput.isEmpty()){
            textInputUsername.setError("Field cant be empty");
            return false;
        }else{
            textInputUsername.setError(null);
            textInputUsername.setErrorEnabled(false);
            return true;
        }
    }

    protected boolean validateLink(){
        String LinkInput = textInputLink.getEditText().getText().toString().trim();
        if(LinkInput.isEmpty()){
            textInputLink.setError("Field cant be empty");
            return false;
        }else if(LinkInput.length() < 10){
            textInputLink.setError("too short");
            return false;
        }else{
            textInputLink.setError(null);
            return true;
        }
    }

    protected boolean validateSeriennummer() {
        String sNrInput = textInputSeriennummer.getEditText().getText().toString().trim();
        if (sNrInput.isEmpty()) {
            textInputSeriennummer.setError("Field cant be empty");
            return false;
        }else if(sNrInput.length() < 10){
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
            Toast.makeText(ActivityEditOcsp.this, "EKeinen Hashalgorithmus ausgwählt", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }

    protected boolean validateCert(){
        if(!upload){
            Toast.makeText(ActivityEditOcsp.this, "Kein Zertifikat ausgewählt", Toast.LENGTH_SHORT).show();
        }
        return upload;
    }


    public void confirmInput(View v){
        final ServerList serverList = ServerList.getInstance(getSharedPreferences("SharedPreferences", MODE_PRIVATE));

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

        editServer(serverList, position);

        Intent intent = new Intent(com.example.andi.ActivityEditOcsp.this, ActivityEdit.class);
        intent.putExtra("Type", type);
        startActivity(intent);
    }

    private void editServer(ServerList serverList, Integer position) {
        String name = textInputUsername.getEditText().getText().toString();
        String link = textInputLink.getEditText().getText().toString();
        BigInteger b = new BigInteger(textInputSeriennummer.getEditText().getText().toString());
        String hash = dropdown.toString();

        switch (type){
            case 1:
                serverList.setOcsp(position, name, link, out, b, hash);
                break;
            case 2:
                serverList.setLDAP(position, name, link);
                break;
            case 3:
                serverList.setHTTP(position, name, link);
                break;
        }
    }

}



