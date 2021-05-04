package com.ncnf.settings.ui;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButtonToggleGroup;
import com.ncnf.R;
import com.ncnf.event.create.EventCreateActivity;
import com.ncnf.settings.Settings;

import java.time.LocalDate;
import java.time.Month;
import java.util.Calendar;

public class SettingsActivity extends AppCompatActivity {

    private int distanceSeekBarValue = Settings.getCurrentMaxDistance();
    private LocalDate minDate = Settings.getMinDate();
    private LocalDate maxDate = Settings.getMaxDate();
    private TextView distanceTextView, minDateTextView, maxDateTextView;
    private MaterialButtonToggleGroup toggleGroup;
    private int selYear, selMonth, selDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        distanceTextView = findViewById(R.id.distanceTextView);

        setText();

        SeekBar distanceSeekBar = findViewById(R.id.distanceSeekBar);
        distanceSeekBar.setMax(Settings.MAX_ACCEPTED_DISTANCE - 1); //Min accepted value is always 0, this to make it 1
        distanceSeekBar.setProgress(distanceSeekBarValue);
        distanceSeekBar.setOnSeekBarChangeListener(createOnSeekBarChangeListener());

        toggleGroup = findViewById(R.id.settingsToggleGroup);
        toggleGroup.addOnButtonCheckedListener(createOnButtonCheckedListener());

        Button maxDateSelection = findViewById(R.id.settingsMaxDateButton);
        Button minDateSelection = findViewById(R.id.settingsMinDateButton);
        minDateTextView = findViewById(R.id.settingsMinDateView);
        minDateTextView.setText(minDate.toString());
        maxDateTextView = findViewById(R.id.settingsMaxDateView);
        maxDateTextView.setText(maxDate.toString());
        minDateSelection.setFocusable(false);
        maxDateSelection.setFocusable(false);

        minDateSelection.setOnClickListener(createDateOnClickListener(true));

        maxDateSelection.setOnClickListener(createDateOnClickListener(false));
    }

    private void setText(){
        String textViewText = getString(R.string.settings_distance_slider) + distanceSeekBarValue + " km";
        distanceTextView.setText(textViewText);

        Button discard = findViewById(R.id.discardButton);
        discard.setText(R.string.settings_discard_button);

        Button accept = findViewById(R.id.validateButton);
        accept.setText(getString(R.string.settings_accept_button));
    }

    public void validate(View view){
        //Set all changes in
        Settings.setCurrentMaxDistance(distanceSeekBarValue);
        Settings.setMinDate(minDate);
        Settings.setMaxDate(maxDate);
        finish();
    }

    public void discard(View view){
        finish();
    }

    private MaterialButtonToggleGroup.OnButtonCheckedListener createOnButtonCheckedListener(){
        MaterialButtonToggleGroup.OnButtonCheckedListener oBCL = (group, checkedId, isChecked) -> {
            if (isChecked){
                group.setSelectionRequired(true);
                switch (checkedId){
                    case R.id.settingsToggleToday:
                        minDate = LocalDate.now();
                        maxDate = minDate;
                        break;
                    case R.id.settingsToggleTomorrow:
                        minDate = LocalDate.now().plusDays(1);
                        maxDate = minDate;
                        break;
                    case R.id.settingsToggleWeek:
                        minDate = LocalDate.now();
                        maxDate = minDate.plusDays(7);
                }
                minDateTextView.setText(minDate.toString());
                maxDateTextView.setText(maxDate.toString());
            }
        };
        return oBCL;
    }

    private SeekBar.OnSeekBarChangeListener createOnSeekBarChangeListener(){
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                distanceSeekBarValue = progress + 1;
                String textViewText = getString(R.string.settings_distance_slider) + distanceSeekBarValue + " km";
                distanceTextView.setText(textViewText);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        };
    }

    private View.OnClickListener createDateOnClickListener(boolean isMin){
        DatePickerDialog.OnDateSetListener oDSL = (view1, year, month, dayOfMonth) -> {
            selYear = year;
            selMonth = month;
            selDay = dayOfMonth;
            if (isMin) {
                minDate = LocalDate.of(selYear, Month.of(selMonth + 1), dayOfMonth);
                minDateTextView.setText(minDate.toString());
                if (minDate.isAfter(maxDate)){
                    maxDate = minDate;
                    maxDateTextView.setText(maxDate.toString());
                }
            } else {
                maxDate = LocalDate.of(selYear, Month.of(selMonth + 1), dayOfMonth);
                maxDateTextView.setText(maxDate.toString());
                if (maxDate.isBefore(minDate)){
                    minDate = maxDate;
                    minDateTextView.setText(minDate.toString());
                }
            }
            toggleGroup.setSelectionRequired(false);
            toggleGroup.uncheck(toggleGroup.getCheckedButtonId());
        };
        if (isMin){
            return view -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, oDSL, minDate.getYear(), minDate.getMonthValue() - 1, minDate.getDayOfMonth());
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            };
        } else {
            return view -> {
                DatePickerDialog datePickerDialog = new DatePickerDialog(this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, oDSL, maxDate.getYear(), maxDate.getMonthValue() - 1, maxDate.getDayOfMonth());
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            };
        }

    }
}