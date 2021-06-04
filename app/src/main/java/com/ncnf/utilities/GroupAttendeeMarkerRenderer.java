package com.ncnf.utilities;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;

public class GroupAttendeeMarkerRenderer extends DefaultClusterRenderer<GroupAttendeeMarker> {

    /**
     * This class was largely done thanks to the youtube Tutorial by CodingWithMitch.
     * Source code : https://github.com/mitchtabian/Google-Maps-2018/blob/building-a-service-for-gps-updates-end/app/src/main/java/com/codingwithmitch/googlemaps2018/services/LocationService.java
     */

    private final IconGenerator icon;
    private final ImageView view;
    private final int markerWidth;
    private final int markerHeight;
    private Context context;


    public GroupAttendeeMarkerRenderer(Context context, GoogleMap map, ClusterManager<GroupAttendeeMarker> clusterManager) {
        super(context, map, clusterManager);

        this.icon = new IconGenerator(context.getApplicationContext());
        this.view = new ImageView(context.getApplicationContext());
        this.markerHeight = 90;
        this.markerWidth = 90;

        this.context = context;


        view.setLayoutParams(new ViewGroup.LayoutParams(markerWidth, markerHeight));
        view.setPadding(2, 2, 2, 2);

        icon.setContentView(view);

    }

    public void updatePosition(GroupAttendeeMarker clusterMarker) {
        Marker marker = getMarker(clusterMarker);
        if (marker != null) {
            marker.setPosition(clusterMarker.getPosition());
        }
    }

    public void updateName(GroupAttendeeMarker clusterMarker) {
        Marker marker = getMarker(clusterMarker);
        if (marker != null) {
            marker.setTitle(clusterMarker.getTitle());
        }
    }

    public void updateSnippet(GroupAttendeeMarker clusterMarker) {
        Marker marker = getMarker(clusterMarker);
        if (marker != null) {
            marker.setSnippet(clusterMarker.getSnippet());
        }
    }

    @Override
    protected void onBeforeClusterItemRendered(GroupAttendeeMarker item, MarkerOptions markerOptions) {

        view.setImageBitmap(item.getBitmap());
        Bitmap finalIcon = icon.makeIcon();
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(finalIcon)).title(item.getTitle());
    }
}
