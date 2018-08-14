/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.quakereport;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class EarthquakeActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Earthquake>> {
    TextView tv;
    private static final int LOADER_ID = 1;
    private EarthQuakeAdapter mAdapter;
    public static final String LOG_TAG = EarthquakeActivity.class.getName();
    private SwipeRefreshLayout swipeRefreshLayout;
    private boolean checkConnection(){
        final ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if(connectivityManager!=null){
            activeNetwork = connectivityManager.getActiveNetworkInfo();
        }
        if(activeNetwork!=null&&activeNetwork.isConnected()){
            return true;
        }
        return false;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_settings : {
                Intent intent = new Intent(this,SettingsActivity.class);
                startActivity(intent);
                return true;
            }
            case R.id.action_refresh : {
                mAdapter.clear();
                getLoaderManager().restartLoader(LOADER_ID,null,this);
                tv.setVisibility(View.GONE);
                View prg = findViewById(R.id.progress);
                prg.setVisibility(View.VISIBLE);

            }
        }


        return super.onOptionsItemSelected(item);
    }

    private static final String USGS_REQUEST_URL =
            "https://earthquake.usgs.gov/fdsnws/event/1/query";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.earthquake_activity);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.Swipe_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mAdapter.clear();
                if(checkConnection()) {
                    tv.setVisibility(View.GONE);
                    getLoaderManager().restartLoader(LOADER_ID, null, EarthquakeActivity.this);
                }
                else{

                    swipeRefreshLayout.setRefreshing(false);
                    tv = (TextView) findViewById(R.id.emptyState);
                    if(tv!=null) {
                        tv.setText("Please Check your network connection");
                        tv.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
        mAdapter = new EarthQuakeAdapter(this,new ArrayList<Earthquake>());
        ListView listView = (ListView)findViewById(R.id.list);
        tv = (TextView) findViewById(R.id.emptyState);
        if(listView!=null){
            listView.setAdapter(mAdapter);
            listView.setDivider(null);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Earthquake current = mAdapter.getItem(i);
                    String url = current.getUrl();
                    Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                    startActivity(intent);
                }
            });
        }
        getLoaderManager().initLoader(LOADER_ID,null,this);
    }
    @Override
    public Loader<List<Earthquake>> onCreateLoader(int i, Bundle bundle) {

        Log.v(LOG_TAG,"Inside create loader");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String minMag = sharedPreferences.getString(getString(R.string.settings_min_magnitude_key),getString(R.string.settings_min_magnitude_default));
        String orderBy = sharedPreferences.getString(getString(R.string.settings_order_by_key),getString(R.string.settings_order_by_default));
        String limit = sharedPreferences.getString(getString(R.string.settings_limit_key),getString(R.string.settings_limit_defaultValue));
        Uri baseUri = Uri.parse(USGS_REQUEST_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();
        uriBuilder.appendQueryParameter("format","geojson");
        uriBuilder.appendQueryParameter("orderby",orderBy);
        uriBuilder.appendQueryParameter("limit",limit);
        uriBuilder.appendQueryParameter("minmag",minMag);
        Log.v(" TEST : URL CHECK ",uriBuilder.toString());
        return new EarthQuakeAsyncLoader(this,uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<Earthquake>> loader, List<Earthquake> earthquakes) {
        Log.v(LOG_TAG,"Inside finishLoad");
        mAdapter.clear();
        View prg = findViewById(R.id.progress);
        prg.setVisibility(View.GONE);
        if(earthquakes!=null && !earthquakes.isEmpty()){
            mAdapter.addAll(earthquakes);
            tv.setVisibility(View.GONE);
        }
        else{
            tv.setVisibility(View.VISIBLE);
            if(checkConnection()){
                tv.setText("No EarthQuakes Found...");
            }
            else{
                tv.setText("Please Check Your Internet Connection..... ");
            }
        }
        if(swipeRefreshLayout!=null&&swipeRefreshLayout.isRefreshing()){
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Earthquake>> loader) {
        Log.v(LOG_TAG,"Inside resetLoad");
        mAdapter.clear();
    }
}
