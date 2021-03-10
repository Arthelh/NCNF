package ncnf.feed;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;

import java.util.ArrayList;
import java.util.List;

import ncnf.event.Event;
import ncnf.event.EventType;
import ncnf.event.PublicEvent;
import ncnf.organizer.PublicOrganizer;

public class FeedActivity extends AppCompatActivity implements EventAdapter.OnEventListener {
    private RecyclerView.LayoutManager lManager;
    private EventAdapter adapter;
    private List<Event> eventList;
    private static final String CHANNEL_NAME = "events_to_be_shown";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_feed);
        setContentView(R.layout.activity_feed);
        findViewById(R.id.recycler_view);
        // Get the RecyclerView
        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler_view);

        // Use LinearLayout as the layout manager
        lManager = new LinearLayoutManager(this);
        recycler.setLayoutManager(lManager);

        // Set the custom adapter
        eventList = new ArrayList<>();
        eventList.add(new PublicEvent(new PublicOrganizer("testOrganizer"), EventType.Museum, "testName", "testData"));
        adapter = new EventAdapter(eventList, this);
        recycler.setAdapter(adapter);

        EventListener eventListener = event -> runOnUiThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("Received event with data: " + event.getData());
                adapter.addEvent(event);
                ((LinearLayoutManager)lManager).scrollToPositionWithOffset(0, 0);
            }
        });
    }

    @Override
    public void onEventClick(int position) {
        Intent intent = new Intent(this, EventActivity.class);
        intent.putExtra("event_uid", eventList.get(position).getUID());
        startActivity(intent);
    }
}