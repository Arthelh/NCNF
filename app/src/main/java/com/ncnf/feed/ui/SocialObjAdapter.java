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

import com.ncnf.socialObject.Event;
import com.ncnf.socialObject.SocialObjRelevanceCalculator;
import com.ncnf.socialObject.SocialObject;
import com.ncnf.utilities.DateAdapter;

public class SocialObjAdapter extends RecyclerView.Adapter<SocialObjAdapter.SocialObjViewHolder> implements Filterable {
    private List<SocialObject> socialObjects;
    private List<SocialObject> socialsFull;
    private final OnSocialObjListener onSocialObjListener;
    private SortingMethod sortingMethod;

    public enum SortingMethod {
        DATE, RELEVANCE
    }

    public interface OnSocialObjListener {
        void onSocialObjectClick(SocialObject socialObject);
    }

    public SocialObjAdapter(List<SocialObject> items, OnSocialObjListener onSocialObjListener, SortingMethod sortingMethod) {
        //ensure proper copy of the List

        this.sortingMethod = SortingMethod.DATE;

        if (sortingMethod == SortingMethod.DATE) {
            socialObjects = new LinkedList<>(items);
            Collections.sort(socialObjects);
            socialsFull = new LinkedList<>(items);
            Collections.sort(socialsFull);
        } else {
            SocialObjRelevanceCalculator e = new SocialObjRelevanceCalculator(items);
            socialObjects = e.getSortedList();
            this.socialsFull = new LinkedList<>(socialObjects);
        }

        this.onSocialObjListener = onSocialObjListener;
    }

    public List<SocialObject> getSocialObjects() {
        return Collections.unmodifiableList(socialObjects);
    }

    public void setSocialObjects(List<SocialObject> socialObjects) {
        this.socialObjects = socialObjects;
        notifyDataSetChanged();
    }

    public void addEvent(Event Event) {
        // Add the Event at the beginning of the list
        socialObjects.add(0, Event);
        socialsFull.add(0, Event);

        orderBy(sortingMethod);

        // Notify the insertion so the view can be refreshed
        notifyItemInserted(socialObjects.indexOf(Event));
    }


    @Override
    public int getItemCount() {
        return socialObjects.size();
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
                filteredList.addAll(socialsFull);
            } else {
                String input = constraint.toString().toLowerCase().trim();
                for (SocialObject s : socialsFull) {
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
            socialObjects.clear();
            socialObjects.addAll((List) results.values);
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

        public void bind(final SocialObject e, final OnSocialObjListener listener) {
            socialObject.setText(e.getName());
            itemView.setOnClickListener(v -> listener.onSocialObjectClick(e));
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
        SocialObject socialObject = socialObjects.get(position);

        viewHolder.socialObject.setText(socialObject.getName());
        viewHolder.date.setText(new DateAdapter(socialObject.getDate()).toString());
        viewHolder.description.setText(socialObject.getDescription());

        viewHolder.bind(socialObject, onSocialObjListener);
    }


    public void orderBy(SortingMethod sortingMethod) {
        // RELEVANCE & DEFAULT CASE
        if (sortingMethod == SortingMethod.DATE) {
            Collections.sort(socialObjects);
            Collections.sort(socialsFull);
        } else {
            SocialObjRelevanceCalculator e = new SocialObjRelevanceCalculator(socialObjects);
            socialObjects = e.getSortedList();
            this.socialsFull = new LinkedList<>(socialObjects);
        }

        // Notify the insertion so the view can be refreshed
        notifyDataSetChanged();
    }

}