package com.example.andi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import crltester.conf.CRLTester;

import static android.widget.Toast.LENGTH_SHORT;

public class Activity extends AppCompatActivity {
    private CheckBox check;
    protected ImageView green;
    protected ImageView red;
    private Button btn;
    private TextView edit;
    private StringBuffer sb = null;
    protected RecyclerViewAdapter adapter;
    protected RecyclerView recyclerView;
    protected int type = 1;
    protected Toolbar toolbar;
    private static final String TAG ="Activity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_server);

        final ServerList serverList = ServerList.getInstance(getSharedPreferences("SharedPreferences", MODE_PRIVATE));
        final Intent intent = getIntent();
        btn = findViewById(R.id.button4);
        check= findViewById(R.id.checkBox);
        edit = findViewById(R.id.edit);
        green = findViewById(R.id.imageViewOnline);
        red = findViewById(R.id.imageViewOffline);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
            }
        });




        type = intent.getIntExtra("Type", 1);


        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity.this, ActivityEdit.class);
                intent.putExtra("Type", type);
                startActivity(intent);
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sb = new StringBuffer();
                 for(Server p : adapter.noCheckedServer){
                     p.getStatus().setTested(false);
                 }

                    switch (type) {
                        case 1:
                            CRLTester.performOcspTests(adapter.checkedServers);
                            break;
                        case 2:
                            CRLTester.performLdapTests(adapter.checkedServers);
                            break;
                        case 3:
                            CRLTester.performHttpTests(adapter.checkedServers);
                            break;
                    }
                    adapter.notifyDataSetChanged();


                if(adapter.checkedServers.size()>0){
                    Toast.makeText(Activity.this, sb.toString() , LENGTH_SHORT).show();
                }else{
                    Toast.makeText(Activity.this, "Please select Server", LENGTH_SHORT).show();
                }
            }
        });


        buildRecycler(serverList);
    }



    protected void buildRecycler(ServerList serverList){

        switch(type){
            case 1:
                adapter = new RecyclerViewAdapter(this, serverList.getOCSP());
                break;
            case 2:
                adapter = new RecyclerViewAdapter(this, serverList.getLDAP());
                break;
            case 3:
                adapter = new RecyclerViewAdapter(this, serverList.getHTTP());
                break;
        }

        recyclerView = findViewById(R.id.ocsp_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));




    }

    public void build(final ServerList serverList){

        adapter.setOnItemClickListener(new RecyclerViewAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(int position) {

            }

        });
    }
}

