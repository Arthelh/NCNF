package com.ncnf.feed;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;

import java.util.LinkedList;
import java.util.List;

import com.ncnf.event.Event;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    private List<Event> items;
    private final OnEventListener onEventListener;

    public EventAdapter(List<Event> items, OnEventListener onEventListener) {
        //ensure proper copy of the List
        this.items = new LinkedList<>(items);
        this.onEventListener = onEventListener;
    }

    public void addEvent(Event event) {
        // Add the event at the beginning of the list
        items.add(0, event);
        // Notify the insertion so the view can be refreshed
        notifyItemInserted(0);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // Card fields
        private TextView event;
        private TextView id;
        private TextView data;
        private OnEventListener onEventListener;

        public EventViewHolder(View v, OnEventListener e) {
            super(v);
            onEventListener = e;
            event = (TextView) v.findViewById(R.id.event);
            id = (TextView) v.findViewById(R.id.id);
            data = (TextView) v.findViewById(R.id.data);
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
        Event event = items.get(position);

        viewHolder.event.setText(event.getName());
        viewHolder.id.setText(event.getUID());
        viewHolder.data.setText(event.getData());
    }


}