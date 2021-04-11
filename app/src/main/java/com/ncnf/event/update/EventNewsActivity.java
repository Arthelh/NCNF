package com.ncnf.event.update;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;
import com.ncnf.utilities.InputValidator;

public class EventNewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_news);

        Button publishButton = findViewById(R.id.event_news_publish_button);
        EditText textField = findViewById(R.id.event_news_field);

        publishButton.setOnClickListener(view -> {
            if (InputValidator.verifyGenericInput(textField)) {
                // Update event
            }
        });
    }

}
