package com.ncnf.map;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.ncnf.R;
import com.ncnf.feed.ui.FeedFragment;
import com.ncnf.socialObject.SocialObjActivity;
import com.ncnf.socialObject.SocialObject;

import java.util.List;

import static com.ncnf.utilities.StringCodes.UUID_KEY;

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
        return false;
    }

    @Override
    public void onClusterItemInfoWindowClick(NCNFMarker item) {
        if (item.isEvent()) {

            List<SocialObject> socialObjects = item.getEventList();

            if (socialObjects.size() == 1) { //When the marker represents only one event

                SocialObject e = socialObjects.get(0);
                Intent intent = new Intent(context, SocialObjActivity.class);
                intent.putExtra(UUID_KEY, e.getUuid().toString());
                context.startActivity(intent);

            } else {

                Fragment feedFragment = new FeedFragment(socialObjects);
                ConstraintLayout feedContainer = globalWindow.findViewById(R.id.map_feed_container);
                FrameLayout feedFrame = globalWindow.findViewById(R.id.map_feed_fragment);
                Button feedButton = globalWindow.findViewById(R.id.map_feed_button);

                feedContainer.setBackgroundResource(R.drawable.main_background_gradient);
                feedContainer.setVisibility(View.VISIBLE);
                feedFrame.setVisibility(View.VISIBLE);
                feedButton.setVisibility(View.VISIBLE);

                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.map_feed_fragment, feedFragment).commit();

                feedButton.setOnClickListener(v -> {
                    fragmentManager.beginTransaction().remove(feedFragment).commit();
                    feedContainer.setVisibility(View.GONE);
                });

            }
        } else {
            //TODO show the organization page
        }
    }

    private void renderInfoWindow(){
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
        if (item == null)
            return null;
        renderInfoWindow();
        return markerWindow;
    }
}
