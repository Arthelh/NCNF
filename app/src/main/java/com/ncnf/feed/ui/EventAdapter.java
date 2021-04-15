package com.ncnf.feed.ui;

import android.util.Log;
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

import static com.ncnf.Utils.DEBUG_TAG;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> implements Filterable {
    private List<Event> events;
    private List<Event> eventsFull;
    private final OnEventListener onEventListener;
    private SortingMethod sortingMethod;

    public enum SortingMethod {
        DATE, RELEVANCE
    }

    public interface OnEventListener {
        void onEventClick(Event event);
    }

    public EventAdapter(List<Event> items, OnEventListener onEventListener, SortingMethod sortingMethod) {
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

        this.onEventListener = onEventListener;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void addEvent(Event event) {
        // Add the event at the beginning of the list
        events.add(0, event);
        eventsFull.add(0, event);

        orderBy(sortingMethod);

        // Notify the insertion so the view can be refreshed
        notifyItemInserted(events.indexOf(event));
    }


    @Override
    public int getItemCount() {
        return events.size();
    }

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    private final Filter eventFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Event> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(eventsFull);
            } else {
                String input = constraint.toString().toLowerCase().trim();
                for (Event e : eventsFull) {
                    if (e.getVisibility().equals(Event.Visibility.valueOf("PUBLIC"))) {
                        PublicEvent event = (PublicEvent) e;
                        if (event.filterTags(input)) {
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

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // Card fields
        private final TextView event;
        private final TextView date;
        private final TextView description;

        public EventViewHolder(View v, OnEventListener e) {
            super(v);
            event = (TextView) v.findViewById(R.id.event_name);
            date = (TextView) v.findViewById(R.id.event_date);
            description = (TextView) v.findViewById(R.id.event_descr);
            //add timestamp
        }

        public void bind(final Event e, final OnEventListener listener) {
            event.setText(e.getName());
            itemView.setOnClickListener(v -> listener.onEventClick(e));
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_row, viewGroup, false);

        return new EventViewHolder(v, onEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder viewHolder, int position) {
        Event event = events.get(position);
        Log.d(DEBUG_TAG, "Entering here");

        viewHolder.event.setText(event.getName());
        viewHolder.date.setText(new DateAdapter(event.getDate()).toString());
        viewHolder.description.setText(event.getDescription());

        viewHolder.bind(event, onEventListener);
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