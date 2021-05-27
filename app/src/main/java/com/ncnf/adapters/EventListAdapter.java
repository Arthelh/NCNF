package com.ncnf.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.ncnf.R;
import com.ncnf.models.Event;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;
import com.ncnf.utilities.event.EventRelevanceCalculator;
import com.ncnf.models.SocialObject;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.views.activities.main.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static android.graphics.BitmapFactory.decodeResource;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.SocialObjViewHolder> implements Filterable {
    private List<Event> events;
    private List<Event> eventsFull;
    private final OnSocialObjListener onSocialObjListener;
    private SortingMethod sortingMethod;
    protected final Context context;

    public enum SortingMethod {
        DATE, RELEVANCE
    }

    public interface OnSocialObjListener {
        void onEventClick(Event event);
    }

    public EventListAdapter(Context context, List<Event> items, OnSocialObjListener onSocialObjListener, SortingMethod sortingMethod) {
        //ensure proper copy of the List

        this.sortingMethod = SortingMethod.DATE;

        if (sortingMethod == SortingMethod.DATE) {
            events = new LinkedList<>(items);
            Collections.sort(events);
            eventsFull = new LinkedList<>(items);
            Collections.sort(eventsFull);
        } else {
            EventRelevanceCalculator e = new EventRelevanceCalculator(items);
            events = e.getSortedList();
            this.eventsFull = new LinkedList<>(events);
        }

        this.onSocialObjListener = onSocialObjListener;
        this.context = context;
    }

    public List<SocialObject> getEvents() {
        return Collections.unmodifiableList(events);
    }

    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    public void addEvent(Event Event) {
        // Add the Event at the beginning of the list
        events.add(0, Event);
        eventsFull.add(0, Event);

        orderBy(sortingMethod);

        // Notify the insertion so the view can be refreshed
        notifyItemInserted(events.indexOf(Event));
    }


    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public Filter getFilter() {
        return socialObjFilter;
    }

    private final Filter socialObjFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SocialObject> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(eventsFull);
            } else {
                String input = constraint.toString().toLowerCase().trim();
                for (SocialObject s : eventsFull) {
                    if(s instanceof Event){
                        Event event = (Event) s;
                        if (event.filterTags(input)) {
                            filteredList.add(s);
                        }
                    }

                }
            }

            FilterResults res = new FilterResults();
            res.values = filteredList;

            return res;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            events.clear();
            events.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public class SocialObjViewHolder extends RecyclerView.ViewHolder {
        // Card fields
        private final TextView socialObject;
        private final TextView date;
        private final TextView description;
        private final ImageView image;

        public SocialObjViewHolder(View v, OnSocialObjListener e) {
            super(v);
            socialObject = (TextView) v.findViewById(R.id.set_event_name);
            date = (TextView) v.findViewById(R.id.event_date);
            description = (TextView) v.findViewById(R.id.event_descr);
            CardView imageHolder = (CardView) v.findViewById(R.id.event_card_image);
            image = imageHolder.findViewById(R.id.event_picture);
            //add timestamp
        }

        public void bind(final Event e, final OnSocialObjListener listener) {
            socialObject.setText(e.getName());
            itemView.setOnClickListener(v -> listener.onEventClick(e));
            setEventImage(e);
        }

        private void setEventImage(Event event){
            FirebaseCacheFileStore fileStore = new FirebaseCacheFileStore();
            fileStore.setContext(context);
            fileStore.setPath(SocialObject.IMAGE_PATH, String.format(SocialObject.IMAGE_NAME, event.getUuid()));
            fileStore.downloadImage(image, BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_event_bg));
        }
    }

    @NonNull
    @Override
    public SocialObjViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_row, viewGroup, false);

        return new SocialObjViewHolder(v, onSocialObjListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialObjViewHolder viewHolder, int position) {
        Event event = events.get(position);

        viewHolder.socialObject.setText(event.getName());
        viewHolder.date.setText(new DateAdapter(event.getDate()).toString());
        viewHolder.description.setText(event.getDescription());

        viewHolder.bind(event, onSocialObjListener);
    }


    public void orderBy(SortingMethod sortingMethod) {
        // RELEVANCE & DEFAULT CASE
        if (sortingMethod == SortingMethod.DATE) {
            Collections.sort(events);
            Collections.sort(eventsFull);
        } else {
            EventRelevanceCalculator e = new EventRelevanceCalculator(events);
            events = e.getSortedList();
            this.eventsFull = new LinkedList<>(events);
        }

        // Notify the insertion so the view can be refreshed
        notifyDataSetChanged();
    }

}