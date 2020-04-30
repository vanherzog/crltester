package com.example.andi;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;



public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Button buttonocsp = findViewById(R.id.button_ocsp);
        buttonocsp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Was soll passieren beim Click?

                Intent intent = new Intent(MainActivity.this, Activity.class);
                intent.putExtra("Type", 1);
                startActivity(intent);

            }
        });

        Button buttonldap = findViewById(R.id.button_ldap);
        buttonldap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Activity.class);
                intent.putExtra("Type", 2);
                startActivity(intent);

            }
        });
        Button buttonhttp = findViewById(R.id.button_http);
        buttonhttp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, Activity.class);
                intent.putExtra("Type", 3);
                startActivity(intent);

            }
        });


        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean firstStart = prefs.getBoolean("firstStart", true);

        if (firstStart) {
            showStartDialog();
        }
    }

    private void showStartDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hello")
                .setMessage("This should only be shown once")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .create().show();

        final ServerList serverList = ServerList.getInstance(getSharedPreferences("SharedPreferences", MODE_PRIVATE));
        serverList.setUp();

        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }

}
