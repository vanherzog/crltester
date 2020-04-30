package com.example.andi;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.material.textfield.TextInputLayout;


public class ActivityEditServer extends ActivityEdit {


    protected TextInputLayout textInputLink;
    protected TextInputLayout textInputUsername;
    Integer position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.popup_layout);


        textInputUsername =findViewById(R.id.text_input_username);
        textInputLink=findViewById(R.id.text_input_link);
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


    public void confirmInput(View v){
        final ServerList serverList = ServerList.getInstance(getSharedPreferences("SharedPreferences", MODE_PRIVATE));

        if(!validateUsername() | !validateLink()){
            return;
        }

        editServer(serverList, position);

        Intent intent = new Intent(ActivityEditServer.this, ActivityEdit.class);
        intent.putExtra("Type", type);
        startActivity(intent);
    }

    private void editServer(ServerList serverList, Integer position) {
        String name = textInputUsername.getEditText().getText().toString();
        String link = textInputLink.getEditText().getText().toString();

        switch (type){
            case 2:
                serverList.setLDAP(position, name, link);
                break;
            case 3:
                serverList.setHTTP(position, name, link);
                break;
        }
    }

}
