package com.ncnf.views.activities.event;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.ncnf.R;
import com.ncnf.database.firebase.FirebaseDatabase;
import com.ncnf.models.SocialObject;
import com.ncnf.utilities.InputValidator;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_LONG;

@AndroidEntryPoint
public class EventNewsActivity extends AppCompatActivity {

    @Inject
    FirebaseDatabase db;

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
                    SocialObject.addNews(db, uuid, textField.getText().toString()).thenAccept(res -> {
                    Snackbar bar = Snackbar.make(findViewById(android.R.id.content), "News published !", LENGTH_LONG);
                    textField.setText("");
                    bar.show();
                    textField.setEnabled(true);
                }).exceptionally(exception -> {
                    Snackbar bar = Snackbar.make(findViewById(android.R.id.content), "Could not publish the news ! Try again later.", LENGTH_LONG);
                    bar.show();
                    return null;
                });
            }
        });
    }

}
