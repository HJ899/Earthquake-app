package com.example.android.quakereport;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Date;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class EarthQuakeAdapter extends ArrayAdapter<Earthquake> {
    EarthQuakeAdapter(Activity context, ArrayList<Earthquake> earthquakes){
        super(context,0,earthquakes);
    }
    private static final String LOCATION_SEPARATOR = " of ";
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItemView = convertView;
        final Earthquake eq= getItem(position);
        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.listitem,parent,false);
        }
        //Set Magnitude
        TextView magText = (TextView)listItemView.findViewById(R.id.magnitude);
        GradientDrawable magnitudeCircle = (GradientDrawable) magText.getBackground();
        magnitudeCircle.setColor(getMagnitudecolor(eq.getMag()));
        magText.setText(formatMagnitude(eq.getMag()));
        //Set mag complete

        //Set location
        TextView locText = (TextView)listItemView.findViewById(R.id.location);
        String primary;
        String secondary;
        String loc = eq.getLocation();
        if(loc.contains(LOCATION_SEPARATOR)){
            String [] parts = loc.split(LOCATION_SEPARATOR);
            secondary = parts[0] + LOCATION_SEPARATOR;
            primary = parts[1];
        }
        else{
            primary = loc;
            secondary = "Near the";
        }
        locText.setText(primary);
        TextView locsecText = (TextView)listItemView.findViewById(R.id.nearTO);
        locsecText.setText(secondary);
        //Set Location complete

        //Set Date and time
        Date date = new Date(Long.parseLong(eq.getDate()));
        String formattedDate = formatDate(date);
        String formattedTime = formatTime(date);
        TextView dateTime = (TextView)listItemView.findViewById(R.id.date);
        dateTime.setText(formattedDate);
        dateTime = (TextView)listItemView.findViewById(R.id.time);
        dateTime.setText(formattedTime);
        //Set Date and Time Complete
        return listItemView;

    }
    private int getMagnitudecolor(double mag){
        switch ((int)mag){
            case 2: return ContextCompat.getColor(getContext(),R.color.magnitude2);
            case 3: return ContextCompat.getColor(getContext(),R.color.magnitude3);
            case 4: return ContextCompat.getColor(getContext(),R.color.magnitude4);
            case 5: return ContextCompat.getColor(getContext(),R.color.magnitude5);
            case 6: return ContextCompat.getColor(getContext(),R.color.magnitude6);
            case 7: return ContextCompat.getColor(getContext(),R.color.magnitude7);
            case 8: return ContextCompat.getColor(getContext(),R.color.magnitude8);
            case 9: return ContextCompat.getColor(getContext(),R.color.magnitude9);
            default: return ContextCompat.getColor(getContext(),R.color.magnitude1);
        }
    }
    private String formatDate(Date milliseconds){
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy");
        return sdf.format(milliseconds);
    }
    private String formatTime(Date milliseconds){
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        return sdf.format(milliseconds);
    }
    private String formatMagnitude(double magnitude){
        DecimalFormat df = new DecimalFormat("0.0");
        return df.format(magnitude);
    }
}
