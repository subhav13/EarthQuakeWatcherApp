package com.example.earthqk.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.earthqk.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class coustom_info_window implements GoogleMap.InfoWindowAdapter {
    private Context context;
    private View view;
    private LayoutInflater layoutInflater;

    public coustom_info_window(Context context) {
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = layoutInflater.inflate(R.layout.coustom_info_window,null);
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        TextView title  = (TextView) view.findViewById(R.id.winTitle);
        title.setText(marker.getTitle());
        TextView magnitude = (TextView)view.findViewById(R.id.Magnitude);
        magnitude.setText(marker.getSnippet());
        return view;
    }
}
