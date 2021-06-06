package com.ncnf.adapters;

import android.content.Context;
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

import com.ncnf.R;
import com.ncnf.models.Event;
import com.ncnf.storage.firebase.FirebaseCacheFileStore;
import com.ncnf.models.SocialObject;
import com.ncnf.utilities.DateAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EventListAdapter extends RecyclerView.Adapter<EventListAdapter.SocialObjViewHolder> implements Filterable {
    private List<Event> events;
    private final List<Event> eventsFull;
    private final OnSocialObjListener onSocialObjListener;
    protected final Context context;

    public interface OnSocialObjListener {
        void onEventClick(Event event);
    }

    public EventListAdapter(Context context, List<Event> items, OnSocialObjListener onSocialObjListener) {
        //ensure proper copy of the List

        events = new LinkedList<>(items);
        Collections.sort(events);
        eventsFull = new LinkedList<>(items);
        Collections.sort(eventsFull);


        this.onSocialObjListener = onSocialObjListener;
        this.context = context;
    }

    /**
     * Getter for event list
     */
    public List<SocialObject> getEvents() {
        return Collections.unmodifiableList(events);
    }

    /**
     * Setter for event list (also notify recycler view)
     */
    public void setEvents(List<Event> events) {
        this.events = events;
        notifyDataSetChanged();
    }

    /**
     * Add an event and sort the list
     */
    public void addEvent(Event Event) {
        // Add the Event at the beginning of the list
        events.add(0, Event);
        eventsFull.add(0, Event);

        orderBy();

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
                        if (event.getName().contains(input)) {
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
            socialObject = v.findViewById(R.id.set_event_name);
            date = v.findViewById(R.id.event_date);
            description = v.findViewById(R.id.event_descr);
            CardView imageHolder = v.findViewById(R.id.event_card_image);
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
            fileStore.setPath(SocialObject.IMAGE_PATH, String.format(SocialObject.IMAGE_NAME, event.getOwnerId()));
            fileStore.downloadImage(image, BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.default_event_header_picture));
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


    public void orderBy() {
        // RELEVANCE & DEFAULT CASE
        Collections.sort(events);
        Collections.sort(eventsFull);


        // Notify the insertion so the view can be refreshed
        notifyDataSetChanged();
    }

}