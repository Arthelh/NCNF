package com.ncnf.settings;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ncnf.R;

public class SettingsActivity extends AppCompatActivity {

    private int distanceSeekBarValue = Settings.getCurrent_max_distance();
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        textView = findViewById(R.id.distanceTextView);

        setText();

        SeekBar distanceSeekBar = findViewById(R.id.distanceSeekBar);
        distanceSeekBar.setMax(Settings.MAX_ACCEPTED_DISTANCE - 1); //Min accepted value is always 0, this to make it 1
        distanceSeekBar.setProgress(distanceSeekBarValue);
        distanceSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceSeekBarValue = progress + 1;
                String textViewText = LanguagePack.settings_distance_slider[Settings.getLang().index] + distanceSeekBarValue + " km";
                textView.setText(textViewText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setText(){
        String textViewText = LanguagePack.settings_distance_slider[Settings.getLang().index] + distanceSeekBarValue + " km";
        textView.setText(textViewText);

        Button discard = findViewById(R.id.discardButton);
        discard.setText(LanguagePack.settings_discard_button[Settings.getLang().index]);

        Button accept = findViewById(R.id.validateButton);
        accept.setText(LanguagePack.settings_accept_button[Settings.getLang().index]);
    }

    public void validate(View view){
        //Set all changes in
        Settings.setCurrent_max_distance(distanceSeekBarValue);
        finish();
    }

    public void discard(View view){
        finish();
    }
}