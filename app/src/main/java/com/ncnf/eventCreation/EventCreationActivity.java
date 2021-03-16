package com.ncnf.eventCreation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.ncnf.R;
import com.ncnf.event.EventType;
import com.ncnf.main.MainActivity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventCreationActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private Spinner eventTypeSelSpinner;
    private EventType eventType;
    private LocalDate eventDate = LocalDate.now();
    private LocalTime eventTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    private LocalDateTime dbEventEntry;
    private int selYear, selMonth, selDay, selHour, selMin;

    private final List<EditText> allFields = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        eventTypeSelSpinner = findViewById(R.id.event_creation_spinner);
        eventTypeSelSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, EventType.values()));
        eventTypeSelSpinner.setSelection(EventType.values().length-1);

        Button validateCreation = findViewById(R.id.event_create_button);

        EditText eventName = findViewById(R.id.event_name);
        EditText eventDescription = findViewById(R.id.event_description);
        EditText eventAddress = findViewById(R.id.event_address);
        EditText eventEmailContact = findViewById(R.id.event_email_contact);
        EditText eventPhoneNumber = findViewById(R.id.event_phone_contact);
        EditText eventPrice = findViewById(R.id.event_price);
        EditText eventWebsite = findViewById(R.id.event_website);

        allFields.addAll(Arrays.asList(eventName, eventDescription, eventAddress, eventEmailContact, eventPhoneNumber, eventPrice, eventWebsite));


        //Date Selection
        TextView dateSelection = (TextView) findViewById(R.id.event_create_display_date);
        dateSelection.setFocusable(false);
        dateSelection.setText(eventDate.toString());

        dateSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                selYear = calendar.get(Calendar.YEAR);
                selMonth = calendar.get(Calendar.MONTH);
                selDay = calendar.get(Calendar.DATE);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EventCreationActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @RequiresApi(api = Build.VERSION_CODES.O)
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        selYear = year;
                        selMonth = month;
                        selDay = dayOfMonth;
                        eventDate = LocalDate.of(selYear, Month.of(selMonth+1), dayOfMonth);
                        dateSelection.setText(eventDate.toString());
                    }
                }, selYear, selMonth, selDay);
                datePickerDialog.show();
            }
        });


        //Time selection
        TextView timeSelection = (TextView) findViewById(R.id.event_create_display_time);
        timeSelection.setFocusable(false);
        timeSelection.setText(eventTime.toString());

        timeSelection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventCreationActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                                selHour = hourOfDay;
                                selMin = minute;
                                eventTime = LocalTime.of(selHour, selMin);
                                timeSelection.setText(eventTime.toString());
                            }
                        }, selHour, selMin, false);
                timePickerDialog.show();
            }
        });

        //Validate
        validateCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAllFieldsAreFilledAndCorrect()) {
                    //TODO
                    finish();
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.event_creation_spinner) {
            eventType = (EventType) parent.getItemAtPosition(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        eventType = EventType.NOTHING;
        TextView errorText = (TextView)eventTypeSelSpinner.getSelectedView();
        errorText.setError("please select an item");

    }

    //TODO : decide what next step is
    private void nextStep(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    private boolean checkAllFieldsAreFilledAndCorrect() {
        return allFields.stream().map(InputValidator::verifyGenericInput).reduce(true, (a, b) -> a && b) && eventType != EventType.NOTHING;
    }

}