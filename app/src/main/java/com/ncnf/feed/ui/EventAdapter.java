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

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import com.ncnf.event.Event;
import com.ncnf.event.EventRelevanceCalculator;
import com.ncnf.event.PublicEvent;
import com.ncnf.utilities.DateAdapter;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> implements Filterable {
    private List<Event> events;
    private List<Event> eventsFull;
    private final OnEventListener onEventListener;
    private String sortingMethod;

    public EventAdapter(List<Event> items, OnEventListener onEventListener, String sortingMethod) {
        //ensure proper copy of the List

        this.sortingMethod = sortingMethod;

        if(sortingMethod.equals("DATE")) {
            events = new LinkedList<>(items);
            Collections.sort(events);
            eventsFull = new LinkedList<>(items);
            Collections.sort(eventsFull);
        }

        else {
            EventRelevanceCalculator e = new EventRelevanceCalculator(items);
            events = e.getSortedList();
            this.eventsFull = new LinkedList<>(events);
        }


        this.onEventListener = onEventListener;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        // Add the event at the beginning of the list
        events.add(0, event);
        eventsFull.add(0, event);

        if(sortingMethod == "DATE") {
            Collections.sort(events);
            Collections.sort(eventsFull);
        }
        else {
            EventRelevanceCalculator e = new EventRelevanceCalculator(events);
            events = e.getSortedList();
            this.eventsFull = new LinkedList<>(events);
        }

        // Notify the insertion so the view can be refreshed
        notifyItemInserted(events.indexOf(event));
    }

    public void changeOrder(String method) {
        if(method == "DATE") {
            Collections.sort(events);
            Collections.sort(eventsFull);
        }
        else {
            EventRelevanceCalculator e = new EventRelevanceCalculator(events);
            events = e.getSortedList();
            this.eventsFull = new LinkedList<>(events);
        }

        // Notify the insertion so the view can be refreshed
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    private Filter eventFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Event> filteredList = new ArrayList<>();

            if(constraint == null || constraint.length() == 0) {
                filteredList.addAll(eventsFull);
            }
            else {
                String input = constraint.toString().toLowerCase().trim();
                for(Event e : eventsFull) {
                    if(e.getPubPriv().equals(Event.PubPriv.valueOf("PUBLIC"))) {
                        PublicEvent event = (PublicEvent) e;
                        if(event.filterTags(input)) {
                            filteredList.add(e);
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

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Card fields
        private TextView event;
        private TextView date;
        private TextView description;
        private OnEventListener onEventListener;

        public EventViewHolder(View v, OnEventListener e) {
            super(v);
            onEventListener = e;
            event = (TextView) v.findViewById(R.id.event_name);
            date = (TextView) v.findViewById(R.id.event_date);
            description = (TextView) v.findViewById(R.id.event_descr);
            //add timestamp
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onEventListener.onEventClick(getAdapterPosition());
        }
    }

    public interface OnEventListener {
        void onEventClick(int position);
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i){
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_row, viewGroup, false);

        return new EventViewHolder(v, onEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder viewHolder, int position) {
        Event event = events.get(position);

        viewHolder.event.setText(event.getName());
        viewHolder.date.setText(new DateAdapter(event.getDate()).toString());
        viewHolder.description.setText(event.getDescription());

    }


}