package ncnf.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.ncnf.R;

import ncnf.feed.FeedActivity;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "Hello there!";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView r = (RecyclerView)findViewById(R.id.recycler_view);

        Button launchFeed = (Button) findViewById(R.id.feed_view_button);

        launchFeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                feedView();
            }
        });
    }

    /*Called when the user clicks the button*/
    public void sendMessage(View view){
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.name_field);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void feedView() {
        Intent intent = new Intent(this, FeedActivity.class);
        startActivity(intent);
    }
}