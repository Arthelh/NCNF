package com.ncnf.map;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.EventActivity;
import com.ncnf.feed.ui.EventAdapter;

import java.util.List;

import static com.ncnf.Utils.UUID_KEY;

public class MarkerInfoWindowManager implements GoogleMap.InfoWindowAdapter, ClusterManager.OnClusterItemClickListener<Marker>, ClusterManager.OnClusterItemInfoWindowClickListener<Marker> {

    private final Context context;
    private final View window;
    private Marker item;

    public MarkerInfoWindowManager(Context context){
        this.context = context;
        this.window = LayoutInflater.from(context).inflate(R.layout.map_marker_info, null);
    }

    @Override
    public boolean onClusterItemClick(Marker item) {
        this.item = item;
        Log.i("MIWM", "Clicked on cluster item");
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(Marker item) {
        //TODO Implement going to event page
    }

    private void renderInfoWindow(){
        Log.i("MIWM", "Rendering Info Window");

        TextView tvTitle = (TextView) window.findViewById(R.id.test_title);
        TextView tvSnippet = (TextView) window.findViewById(R.id.test_snippet);

        tvTitle.setText(item.getTitle());
        tvSnippet.setText(item.getSnippet());
    }

    @Override
    public View getInfoWindow(com.google.android.gms.maps.model.Marker marker) {
        if (item == null)
            return null;
        renderInfoWindow();
        return window;
    }

    @Override
    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
        //if (item == null)
        //    return null;
        Log.i("MIWM", "Getting info content");
        TextView tvTitle = (TextView) window.findViewById(R.id.test_title);
        TextView tvSnippet = (TextView) window.findViewById(R.id.test_snippet);

        tvTitle.setText(item.getTitle());
        tvSnippet.setText(item.getSnippet());
        return window;
    }
}
