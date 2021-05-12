package com.ncnf.socialObject.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.ncnf.R;
import com.ncnf.socialObject.Event;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.storage.CacheFileStore;
import com.ncnf.storage.FileStore;
import com.ncnf.utilities.DateAdapter;

public class EventFragment extends Fragment {

    private SocialObject event;

    public EventFragment(SocialObject event){
        this.event = event;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(false);
        return inflater.inflate(R.layout.fragment_event, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView imageView = view.findViewById(R.id.eventImage);
        FileStore file = new CacheFileStore(this.getContext(), Event.IMAGE_PATH, String.format(Event.IMAGE_NAME, event.getUuid()));
        file.downloadImage(imageView);

        TextView name = view.findViewById(R.id.eventName);
        name.setText(event.getName());

        TextView date = view.findViewById(R.id.eventDate);
        date.setText("Event takes place on : " + new DateAdapter(event.getDate()).toString());

        TextView loc = view.findViewById(R.id.eventLocation);
        loc.setText("Event held at : " + event.getAddress());

        TextView desc = view.findViewById(R.id.eventDescription);
        desc.setText(event.getDescription());

        TextView owner = view.findViewById(R.id.eventOwner);
        owner.setText("Event hosted by " + event.getOwnerId());
    }
}