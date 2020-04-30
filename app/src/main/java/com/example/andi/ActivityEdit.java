package com.example.andi;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;


import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class ActivityEdit extends Activity {

    protected RecyclerViewAdapterEdit adapter;
    private TextView fertig;
    private FloatingActionButton addServer;
    private TextView serverName;
    private ImageView serverItemEdit;
    protected Integer id;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_server_edit);


        fertig = findViewById(R.id.Fertig);
        addServer= findViewById(R.id.Add);
        serverName = findViewById(R.id.server_name_edit);
        serverItemEdit= findViewById(R.id.server_item_edit);
        final ServerList serverList = ServerList.getInstance(getSharedPreferences("SharedPreferences", MODE_PRIVATE));


        addServer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent;

                switch (type) {
                    case 1:
                        intent = new Intent(ActivityEdit.this, ActivityAddOcsp.class);
                        intent.putExtra("Type", type);
                        startActivity(intent);
                        break;
                    case 2:
                    case 3:
                        intent = new Intent(ActivityEdit.this, ActivityAddServer.class);
                        intent.putExtra("Type", type);
                        startActivity(intent);
                        break;
                }

                adapter.notifyDataSetChanged();
            }
        });


        fertig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ActivityEdit.this, Activity.class);
                intent.putExtra("Type", type);
                startActivity(intent);
            }
        });


        buildRecycler(serverList);

    }




    protected void buildRecycler(ServerList serverList){

        switch (type) {
            case 1:
                adapter = new RecyclerViewAdapterEdit(this, serverList.getOCSP());
                break;
            case 2:
                adapter = new RecyclerViewAdapterEdit(this, serverList.getLDAP());
                break;
            case 3:
                adapter = new RecyclerViewAdapterEdit(this, serverList.getHTTP());
                break;
        }


        recyclerView = findViewById(R.id.ocsp_recycler);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        build(serverList);

    }



    public void build(final ServerList serverList){

        adapter.setOnItemClickListener(new RecyclerViewAdapterEdit.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                switch(type){
                    case 1:
                        Toast.makeText(recyclerView.getContext(),"Link: " +  serverList.getOCSP().get(position).getLink(), Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(recyclerView.getContext(),"Link: " +  serverList.getLDAP().get(position).getLink(), Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(recyclerView.getContext(),"Link: " +  serverList.getHTTP().get(position).getLink(), Toast.LENGTH_LONG).show();
                        break;
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onDeleteClick(int position) {
                switch(type){
                    case 1:
                        serverList.removeOCSP(position);
                        break;
                    case 2:
                        serverList.removeLDAP(position);
                        break;
                    case 3:
                        serverList.removeHTTP(position);
                        break;
                }
                adapter.notifyItemRemoved(position);
            }

            @Override
            public void onEditClick(int position) {
                Intent intent;

                switch (type) {
                    case 1:
                        intent = new Intent(ActivityEdit.this, ActivityAddOcsp.class);
                        intent.putExtra("Type", type);
                        intent.putExtra("Position", position);
                        startActivity(intent);
                        break;
                    case 2:
                    case 3:
                        intent = new Intent(ActivityEdit.this, ActivityEditServer.class);
                        intent.putExtra("Type", type);
                        intent.putExtra("Position", position);
                        startActivity(intent);
                        break;
                }
            }
        });
    }






}

