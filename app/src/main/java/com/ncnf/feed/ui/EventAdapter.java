package com.ncnf.feed.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;
import com.ncnf.socialObject.Event;
import com.ncnf.socialObject.EventRelevanceCalculator;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.utilities.DateAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.SocialObjViewHolder> implements Filterable {
    private List<Event> events;
    private List<Event> eventsFull;
    private final OnSocialObjListener onSocialObjListener;
    private SortingMethod sortingMethod;

    public enum SortingMethod {
        DATE, RELEVANCE
    }

    public interface OnSocialObjListener {
        void onEventClick(Event event);
    }

    public EventAdapter(List<Event> items, OnSocialObjListener onSocialObjListener, SortingMethod sortingMethod) {
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

    public static class SocialObjViewHolder extends RecyclerView.ViewHolder {
        // Card fields
        private final TextView socialObject;
        private final TextView date;
        private final TextView description;

        public SocialObjViewHolder(View v, OnSocialObjListener e) {
            super(v);
            socialObject = (TextView) v.findViewById(R.id.set_event_name);
            date = (TextView) v.findViewById(R.id.event_date);
            description = (TextView) v.findViewById(R.id.event_descr);
            //add timestamp
        }

        public void bind(final Event e, final OnSocialObjListener listener) {
            socialObject.setText(e.getName());
            itemView.setOnClickListener(v -> listener.onEventClick(e));
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