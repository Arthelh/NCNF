package ncnf.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;

import ncnf.eventCreation.EventCreationActivity;
import ncnf.feed.FeedActivity;
import ncnf.main.map.MapActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.bootcamp.EXTRA_MESSAGE";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launchFeed = (Button) findViewById(R.id.feed_view_button);
        Button launchEventCreation = (Button) findViewById(R.id.create_event_button);

        launchFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedView();
            }
        });

        launchEventCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eventCreationView();
            }
        });
    }

    /*Called when the user clicks the button*/
    public void sendMessage(View view){
        Intent intent = new Intent(this, MapActivity.class);
        EditText editText = (EditText) findViewById(R.id.name_field);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void feedView() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }

    public void eventCreationView(){
        Intent intent = new Intent(this, EventCreationActivity.class);
        startActivity(intent);
    }
}