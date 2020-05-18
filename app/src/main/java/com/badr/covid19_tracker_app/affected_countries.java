package com.badr.covid19_tracker_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.badr.covid19_tracker_app.Adapter.RecyclerViewAdapter;
import com.badr.covid19_tracker_app.Model.countryModel;
import com.leo.simplearcloader.SimpleArcLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class affected_countries extends AppCompatActivity {

    private static List<countryModel> countryModelList;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter recyclerViewAdapter;
    private EditText searchInput;
    private SimpleArcLoader arcLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_affected_contries);



        arcLoader  =findViewById(R.id.loader);

        //EditText
        searchInput = findViewById(R.id.searchInput);

        //getting the recyclerview from layout Object
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //instantiating countryList
        countryModelList = new ArrayList<>();

        //getting list of countryModelList full with data from API
        countryModelList = getCountryData();

        //setup adapter
        recyclerViewAdapter = new RecyclerViewAdapter(affected_countries.this, countryModelList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();

        getSupportActionBar().setTitle("Affected Countries");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        searchInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //recyclerViewAdapter.getFilter().filter(s);
                //recyclerViewAdapter.notifyDataSetChanged();
                filter(s.toString());
            }
        });


    }

    private void filter(String search) {
        ArrayList<countryModel> filteredList = new ArrayList<>();
        //iterating over the list of countries
        for(countryModel countryModelItem : countryModelList){

            //checking if the input matches
            if(countryModelItem.getCountry().toLowerCase().contains(search.toLowerCase())){
                filteredList.add(countryModelItem);
            }
        }

        //passing a new filtered list of countries to the recyclerViewAdapter
        recyclerViewAdapter.filterList(filteredList);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private List<countryModel> getCountryData() {

        String url = "https://corona.lmao.ninja/v2/countries";
        arcLoader.start();
        JsonArrayRequest  jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {

                for(int i = 0; i < response.length(); i++){
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        JSONObject countryInfo = jsonObject.getJSONObject("countryInfo");
                        String flag = countryInfo.getString("flag");
                        String cases = jsonObject.getString("cases");
                        String todayCases = jsonObject.getString("todayCases");
                        String deaths = jsonObject.getString("deaths");
                        String todayDeaths = jsonObject.getString("todayDeaths");
                        String recovered = jsonObject.getString("recovered");
                        String active = jsonObject.getString("active");
                        String critical = jsonObject.getString("critical");
                        String country = jsonObject.getString("country");

                        countryModel countryModel = new countryModel(flag,country,cases,todayCases,deaths,todayDeaths,recovered,active,critical);
                        countryModelList.add(countryModel);
                        arcLoader.stop();
                        arcLoader.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        arcLoader.stop();
                        arcLoader.setVisibility(View.GONE);

                    }
                }

                //notifying the recyclerViewAdapter to render the new data
                recyclerViewAdapter.notifyDataSetChanged();


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(affected_countries.this, error.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });

        //adding the request to queue and returning the list of countryModelList objects
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        return countryModelList;
    }
}
