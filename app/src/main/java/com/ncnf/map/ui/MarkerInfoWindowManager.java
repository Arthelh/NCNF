package com.ncnf.map.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterManager;
import com.ncnf.R;
import com.ncnf.map.NCNFMarker;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.socialObject.ui.SocialObjFragment;

import java.util.List;

public class  MarkerInfoWindowManager implements GoogleMap.InfoWindowAdapter, ClusterManager.OnClusterItemClickListener<NCNFMarker>, ClusterManager.OnClusterItemInfoWindowClickListener<NCNFMarker> {

    private final AppCompatActivity context;
    private final View markerWindow;
    private final Window globalWindow;
    private final FragmentManager fragmentManager;
    private NCNFMarker item;

    public MarkerInfoWindowManager(AppCompatActivity context, Window globalWindow, FragmentManager fragmentManager){
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
            Fragment fragment;

            if (socialObjects.size() == 1) { //When the marker represents only one event
                SocialObject e = item.getEventList().get(0);
                fragment = new SocialObjFragment(e);
            } else {
                fragment = new MapFeedFragment(socialObjects, globalWindow, fragmentManager);
            }

            ConstraintLayout feedContainer = globalWindow.findViewById(R.id.map_feed_container);
            FrameLayout feedFrame = globalWindow.findViewById(R.id.map_feed_fragment);
            Button feedButton = globalWindow.findViewById(R.id.map_feed_button);

            feedContainer.setBackgroundResource(R.drawable.main_background_gradient);
            feedContainer.setVisibility(View.VISIBLE);
            feedFrame.setVisibility(View.VISIBLE);
            feedButton.setVisibility(View.VISIBLE);

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.map_feed_fragment, fragment).addToBackStack(null).commit();

            OnBackPressedCallback callback = new OnBackPressedCallback(true) {
                @Override
                public void handleOnBackPressed() {
                    destroyChildFragment(fragmentManager, fragment, feedContainer, this);
                }
            };

            feedButton.setOnClickListener(v -> {
                destroyChildFragment(fragmentManager, fragment, feedContainer, callback);
            });

            context.getOnBackPressedDispatcher().addCallback(context, callback);

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
    public View getInfoContents(Marker marker) {
        if (item == null)
            return null;
        renderInfoWindow();
        return markerWindow;
    }

    private void destroyChildFragment(FragmentManager fragmentManager, Fragment fragment, ConstraintLayout feedContainer, OnBackPressedCallback callback){
        fragmentManager.beginTransaction().remove(fragment).commit();
        feedContainer.setVisibility(View.GONE);
        callback.setEnabled(false);
    }

}
