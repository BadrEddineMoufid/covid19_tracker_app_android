package com.badr.covid19_tracker_app;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;

public class DetailsActivity extends AppCompatActivity {

    TextView mCountryName, mCases, mTodayCase, mDeaths, mTodayDeaths, mRecovered, mActive, mCritical, lastUpdated;
    private String countryName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        //finding the views
        mCountryName = findViewById(R.id.mCountryName);
        mCases = findViewById(R.id.mCases);
        mTodayCase = findViewById(R.id.mTodayCases);
        mDeaths = findViewById(R.id.mDeaths);
        mTodayDeaths = findViewById(R.id.mTodayDeaths);
        mRecovered = findViewById(R.id.mRecovered);
        mActive = findViewById(R.id.mActive);
        mCritical = findViewById(R.id.mCritical);
        lastUpdated = findViewById(R.id.lastUpdated);

        getSupportActionBar().setTitle("Country Details ");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //getting the country name from intent
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            countryName = bundle.getString("countryName");
        }

        fetchCountryData(countryName);

    }

    private void fetchCountryData(String countryName) {
        String url = "https://corona.lmao.ninja/v2/countries/";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + countryName, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String updated = jsonObject.getString("updated");
                    String country = jsonObject.getString("country");
                    String cases = jsonObject.getString("cases");
                    String todayCases = jsonObject.getString("todayCases");
                    String deaths = jsonObject.getString("deaths");
                    String todayDeaths = jsonObject.getString("todayDeaths");
                    String recovered = jsonObject.getString("recovered");
                    String active = jsonObject.getString("active");
                    String critical = jsonObject.getString("critical");

                    Log.d("get Text", "cases get: " + mCases.getText());

                    //Log.d("log", "cases" + cases);

                    setUpUI(updated, country, cases, deaths, todayCases, todayDeaths, recovered, active, critical);


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DetailsActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private void setUpUI(String updated, String country, String cases, String deaths, String todayCases, String todayDeaths, String recovered, String active, String critical) {

        mCountryName.setText(country);
        lastUpdated.setText(formatDate(updated));
        mCases.setText(formatNumbers(cases));
        mDeaths.setText(formatNumbers(deaths));
        mTodayCase.setText(todayCases);
        mTodayDeaths.setText(todayDeaths);
        mRecovered.setText(formatNumbers(recovered));
        mActive.setText(active);
        mCritical.setText(critical);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            finish();
        return super.onOptionsItemSelected(item);
    }

    private String formatNumbers(String num){
        String res;
        return res = NumberFormat.getNumberInstance().format(Integer.parseInt(num)).toString();
    }

    private String formatDate(String date){
        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        long millSec = Long.parseLong(date);
        String res = formatter.format(millSec);
        return res;

    }
}
