package com.ncnf.map;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.EventActivity;
import com.ncnf.feed.ui.FeedFragment;

import java.util.List;

import static com.ncnf.Utils.DEBUG_TAG;
import static com.ncnf.Utils.UUID_KEY;

public class MarkerInfoWindowManager implements GoogleMap.InfoWindowAdapter, ClusterManager.OnClusterItemClickListener<NCNFMarker>, ClusterManager.OnClusterItemInfoWindowClickListener<NCNFMarker> {

    private final Activity context;
    private final View markerWindow;
    private final Window globalWindow;
    private final FragmentManager fragmentManager;
    private NCNFMarker item;

    public MarkerInfoWindowManager(Activity context, Window globalWindow, FragmentManager fragmentManager){
        this.context = context;
        this.fragmentManager = fragmentManager;

        this.globalWindow = globalWindow;
        this.markerWindow = LayoutInflater.from(context).inflate(R.layout.map_marker_info, null);
    }

    @Override
    public boolean onClusterItemClick(NCNFMarker item) {
        this.item = item;
        Log.i(DEBUG_TAG, "Clicked on cluster item");
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(NCNFMarker item) {
        //TODO Implement going to event page
        Log.i(DEBUG_TAG, "Item info window clicked");
        if (item.getType() == NCNFMarker.TYPE.EVENT) {
            List<Event> events = item.getEventList();
            if (events.size() == 1) { //When the marker represents only one event
                Log.i(DEBUG_TAG, "Launching event activity");
                Event e = events.get(0);
                Intent intent = new Intent(context, EventActivity.class);
                intent.putExtra(UUID_KEY, e.getUuid().toString());
                context.startActivity(intent);
            } else {
                Log.i(DEBUG_TAG, "Launching feed activity");

                Fragment feedFragment = new FeedFragment(events);
                FrameLayout feedFrame = globalWindow.findViewById(R.id.map_feed_fragment);
                ImageButton imageButton = globalWindow.findViewById(R.id.map_feed_button);

                feedFrame.setBackgroundResource(R.drawable.main_background_gradient);
                feedFrame.setVisibility(View.VISIBLE);
                imageButton.setVisibility(View.VISIBLE);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.map_feed_fragment, feedFragment).commit();

                imageButton.setOnClickListener(v -> {
                    fragmentManager.beginTransaction().remove(feedFragment).commit();
                    feedFrame.setVisibility(View.GONE);
                });

            }
        } else {
            //TODO show the organization page
        }
    }

    private void renderInfoWindow(){
        Log.i(DEBUG_TAG, "Rendering Info Window");

        TextView tvTitle = (TextView) markerWindow.findViewById(R.id.marker_title);
        TextView tvSnippet = (TextView) markerWindow.findViewById(R.id.marker_snippet);

        tvTitle.setText(item.getTitle());
        tvSnippet.setText(item.getSnippet());
    }

    @Override
    public View getInfoWindow(Marker marker) {
        if (item == null)
            return null;
        renderInfoWindow();
        return markerWindow;
    }

    @Override
    public View getInfoContents(com.google.android.gms.maps.model.Marker marker) {
        //if (item == null)
        //    return null;
        Log.i(DEBUG_TAG, "Getting info content");
        TextView tvTitle = (TextView) markerWindow.findViewById(R.id.marker_title);
        TextView tvSnippet = (TextView) markerWindow.findViewById(R.id.marker_snippet);

        tvTitle.setText(item.getTitle());
        tvSnippet.setText(item.getSnippet());
        return markerWindow;
    }
}
