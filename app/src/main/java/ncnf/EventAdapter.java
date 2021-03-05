package ncnf;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bootcamp.R;

import java.util.LinkedList;
import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder>{
        private List<Event> items;

        public EventAdapter(List<Event> items) {
            //ensure proper copy of the List
            this.items = new LinkedList<>(items);
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

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        // Card fields
        public TextView event;
        public TextView id;
        public TextView data;

        public EventViewHolder(View v) {
            super(v);
            event = (TextView) v.findViewById(R.id.event);
            id = (TextView) v.findViewById(R.id.id);
            data = (TextView) v.findViewById(R.id.data);
            //add timestamp
        }
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.event_row, viewGroup, false);

        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder viewHolder, int position) {
        Event event = items.get(position);

        viewHolder.event.setText(event.getName());
        viewHolder.id.setText(event.getUID());
        viewHolder.data.setText(event.getData());
    }


}