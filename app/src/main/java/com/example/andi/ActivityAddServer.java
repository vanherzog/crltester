package com.example.andi;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

public class ActivityAddServer extends ActivityEdit{

    String name;
    protected TextInputLayout textInputLink;
    protected TextInputLayout textInputUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_layout);


        textInputUsername =findViewById(R.id.text_input_username);
        textInputLink=findViewById(R.id.text_input_link);


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

    //Button OnClick

    public void confirmInput(View v){
        if(!validateUsername() | !validateLink()){
            return;
        }
        String input = "Email: " + textInputUsername.getEditText().getText().toString();
        input += "\n";
        input += "Username: " + textInputLink.getEditText().getText().toString();


        Toast.makeText(this, input, Toast.LENGTH_SHORT).show();
        addServer();

        Intent intent = new Intent(ActivityAddServer.this, ActivityEdit.class);
        intent.putExtra("Type", type);
        startActivity(intent);
    }




    private void addServer(){
        final ServerList serverList = ServerList.getInstance(getSharedPreferences("SharedPreferences", MODE_PRIVATE));
        name = textInputUsername.getEditText().getText().toString();
        String link = textInputLink.getEditText().getText().toString();

        switch(type){
            case 2:
                serverList.addLDAP(new LDAP(name, link));
                break;
            case 3:
                serverList.addHTTP(new HTTP(name, link));
                break;
        }
    }


}
