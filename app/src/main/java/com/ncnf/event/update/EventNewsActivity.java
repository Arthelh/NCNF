package com.ncnf.event.update;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.ncnf.R;
import com.ncnf.database.DatabaseService;
import com.ncnf.event.Event;
import com.ncnf.event.PublicEvent;
import com.ncnf.utilities.InputValidator;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;
import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;

@AndroidEntryPoint
public class EventNewsActivity extends AppCompatActivity {

    @Inject
    DatabaseService db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_news);

        Intent intent = getIntent();
        String uuid = intent.getStringExtra("uuid");

        Button publishButton = findViewById(R.id.event_news_publish_button);
        EditText textField = findViewById(R.id.event_news_field);

        publishButton.setOnClickListener(view -> {
            if (InputValidator.verifyGenericInput(textField)) {
                textField.setEnabled(false);
                Event.addNews(db, uuid, textField.getText().toString()).thenAccept(res -> {
                    Snackbar bar;
                    if (res.isSuccessful()) {
                        bar = Snackbar.make(findViewById(R.id.eventNewsRoot), "News published !", LENGTH_LONG);
                        textField.setText("");
                    } else {
                        bar = Snackbar.make(findViewById(R.id.eventNewsRoot), "Could not publish the news ! Try again later.", LENGTH_LONG);
                    }
                    bar.show();
                    textField.setEnabled(true);
                });
            }
        });
    }

}