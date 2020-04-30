package com.example.andi;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;


import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class SearchExample extends AppCompatActivity {

    MaterialSearchView searchView;
    String [] listSource =  {"harry","rom","snape","eins","zwei"};
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_server);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Material Search");
        toolbar.setTitleTextColor(Color.parseColor("#FFFFFF"));


        listView=findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,listSource);
        listView.setAdapter(adapter);


        searchView = findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {

            }

            @Override
            public void onSearchViewClosed() {
                //if closed Search VIew, listvowe will return default
                listView=findViewById(R.id.listView);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchExample.this, android.R.layout.simple_list_item_1,listSource);
                listView.setAdapter(adapter);
            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText != null && !newText.isEmpty()){
                    List<String> listFound= new ArrayList<String>();
                    for(String item:listSource){
                        if(item.contains(newText))
                            listFound.add(item);
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchExample.this, android.R.layout.simple_list_item_1,listFound);
                    listView.setAdapter(adapter);
                }else{
                    //if search text is null
                    //return default
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(SearchExample.this, android.R.layout.simple_list_item_1,listSource);
                    listView.setAdapter(adapter);
                }

                return true;
            }
        });




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_item, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
     }


    }
