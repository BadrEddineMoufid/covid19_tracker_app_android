package com.badr.covid19_tracker_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.leo.simplearcloader.SimpleArcLoader;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;

public class MainActivity extends AppCompatActivity {
    TextView cases, recovered, active, critical, todayCases, totalDeaths, todayDeaths, affectedCountries;
    PieChart pieChart;
    ScrollView scrollView;
    SimpleArcLoader simpleArcLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("Track Covid19");

        cases = findViewById(R.id.cases);
        recovered = findViewById(R.id.recovered);
        active = findViewById(R.id.active);
        critical = findViewById(R.id.critical);
        todayCases = findViewById(R.id.todayCases);
        totalDeaths = findViewById(R.id.totalDeaths);
        todayDeaths = findViewById(R.id.todayDeaths);
        affectedCountries = findViewById(R.id.affectedCountries);

        simpleArcLoader = findViewById(R.id.loader);
        scrollView = findViewById(R.id.scrollStats);
        pieChart = findViewById(R.id.pieChart);

        Button trackBtn = findViewById(R.id.trackBtn);

        trackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, affected_countries.class));
            }
        });

        fetchData();
    }

    private void fetchData() {

        String url ="https://corona.lmao.ninja/v2/all";

        //starting arcLoader Animation while fetching for data
        simpleArcLoader.start();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.toString());

                    //setting textViews Data from API

                    cases.setText(formatNumbers(jsonObject.getString("cases")));
                    recovered.setText(formatNumbers(jsonObject.getString("recovered")));
                    critical.setText(formatNumbers(jsonObject.getString("critical")));
                    active.setText(formatNumbers(jsonObject.getString("active")));
                    todayCases.setText(formatNumbers(jsonObject.getString("todayCases")));
                    totalDeaths.setText(formatNumbers(jsonObject.getString("deaths")));
                    todayDeaths.setText(formatNumbers(jsonObject.getString("todayDeaths")));
                    affectedCountries.setText(formatNumbers(jsonObject.getString("affectedCountries")));

                    //adding data to pieChart
                    pieChart.addPieSlice(new PieModel("cases",Integer.parseInt(jsonObject.getString("cases")), Color.parseColor("#ede321")));
                    pieChart.addPieSlice(new PieModel("active",Integer.parseInt(jsonObject.getString("active")), Color.parseColor("#0362B5")));
                    pieChart.addPieSlice(new PieModel("recovered",Integer.parseInt(jsonObject.getString("recovered")), Color.parseColor("#13C606")));
                    pieChart.addPieSlice(new PieModel("deaths",Integer.parseInt(jsonObject.getString("deaths")), Color.parseColor("#FF0000")));
                    pieChart.startAnimation();

                    //Stopping arcLoaderAnimation and setting visibility to gone
                    simpleArcLoader.stop();
                    simpleArcLoader.setVisibility(View.GONE);

                    //setting scrollView visibility to visible
                    scrollView.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    //logging ERROR
                    e.printStackTrace();

                    //Stopping arcLoaderAnimation
                    simpleArcLoader.stop();
                    simpleArcLoader.setVisibility(View.GONE);
                    scrollView.setVisibility(View.VISIBLE);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Stopping arcLoaderAnimation
                simpleArcLoader.stop();
                simpleArcLoader.setVisibility(View.GONE);
                scrollView.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();

            }
        });

       AppController.getInstance().addToRequestQueue(stringRequest);
    }

    private String formatNumbers(String num){
        String res;
        return res = NumberFormat.getNumberInstance().format(Integer.parseInt(num)).toString();
    }
}
