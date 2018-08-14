package com.example.android.quakereport;

import android.content.Context;
import android.content.AsyncTaskLoader;
import android.util.Log;

import java.util.List;

public class EarthQuakeAsyncLoader extends AsyncTaskLoader< List<Earthquake> > {
    private static final String LOG_TAG = "EarthQuakeAsyncLoader";
    private String url;
    EarthQuakeAsyncLoader(Context context, String url){
        super(context);
        this.url = url;
    }
    @Override
    public List<Earthquake> loadInBackground() {
        Log.v(LOG_TAG,"LoadinBack");
        if(url == null) return null;
        return QueryUtils.fetchData(url);
    }

    @Override
    protected void onStartLoading() {
        Log.v(LOG_TAG,"Start loading");
        forceLoad();
    }
}
