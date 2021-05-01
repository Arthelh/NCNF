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
import com.ncnf.event.Social;
import com.ncnf.utilities.DateAdapter;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.SocialViewHolder> implements Filterable {
    private List<Social> socials;
    private List<Social> socialsFull;
    private final OnEventListener onEventListener;
    private SortingMethod sortingMethod;

    public enum SortingMethod {
        DATE, RELEVANCE
    }

    public interface OnEventListener {
        void onEventClick(Social social);
    }

    public EventAdapter(List<Social> items, OnEventListener onEventListener, SortingMethod sortingMethod) {
        //ensure proper copy of the List

        this.sortingMethod = SortingMethod.DATE;

        if (sortingMethod == SortingMethod.DATE) {
            socials = new LinkedList<>(items);
            Collections.sort(socials);
            socialsFull = new LinkedList<>(items);
            Collections.sort(socialsFull);
        } else {
            EventRelevanceCalculator e = new EventRelevanceCalculator(items);
            socials = e.getSortedList();
            this.socialsFull = new LinkedList<>(socials);
        }

        this.onEventListener = onEventListener;
    }

    public List<Social> getSocials() {
        return Collections.unmodifiableList(socials);
    }

    public void setSocials(List<Social> events) {
        this.socials = events;
        notifyDataSetChanged();
    }

    public void addEvent(Event Event) {
        // Add the Event at the beginning of the list
        socials.add(0, Event);
        socialsFull.add(0, Event);

        orderBy(sortingMethod);

        // Notify the insertion so the view can be refreshed
        notifyItemInserted(socials.indexOf(Event));
    }


    @Override
    public int getItemCount() {
        return socials.size();
    }

    @Override
    public Filter getFilter() {
        return eventFilter;
    }

    private final Filter eventFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Social> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(socialsFull);
            } else {
                String input = constraint.toString().toLowerCase().trim();
                for (Social s : socialsFull) {
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
            socials.clear();
            socials.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public static class SocialViewHolder extends RecyclerView.ViewHolder {
        // Card fields
        private final TextView event;
        private final TextView date;
        private final TextView description;

        public SocialViewHolder(View v, OnEventListener e) {
            super(v);
            event = (TextView) v.findViewById(R.id.set_event_name);
            date = (TextView) v.findViewById(R.id.event_date);
            description = (TextView) v.findViewById(R.id.event_descr);
            //add timestamp
        }

        public void bind(final Social e, final OnEventListener listener) {
            event.setText(e.getName());
            itemView.setOnClickListener(v -> listener.onEventClick(e));
        }
    }

    @NonNull
    @Override
    public SocialViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_row, viewGroup, false);

        return new SocialViewHolder(v, onEventListener);
    }

    @Override
    public void onBindViewHolder(@NonNull SocialViewHolder viewHolder, int position) {
        Social social = socials.get(position);

        viewHolder.event.setText(social.getName());
        viewHolder.date.setText(new DateAdapter(social.getDate()).toString());
        viewHolder.description.setText(social.getDescription());

        viewHolder.bind(social, onEventListener);
    }


    public void orderBy(SortingMethod sortingMethod) {
        // RELEVANCE & DEFAULT CASE
        if (sortingMethod == SortingMethod.DATE) {
            Collections.sort(socials);
            Collections.sort(socialsFull);
        } else {
            EventRelevanceCalculator e = new EventRelevanceCalculator(socials);
            socials = e.getSortedList();
            this.socialsFull = new LinkedList<>(socials);
        }

        // Notify the insertion so the view can be refreshed
        notifyDataSetChanged();
    }

}