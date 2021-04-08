package com.ncnf.event.create;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.Event.Type;
import com.ncnf.event.PrivateEvent;
import com.ncnf.main.MainActivity;
import com.ncnf.utilities.InputValidator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static com.ncnf.Utils.*;

@RequiresApi(api = Build.VERSION_CODES.O)
public class EventCreateActivity extends AppCompatActivity {

    private Type eventype;
    private LocalDate eventDate = LocalDate.now();
    private LocalTime eventTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    private int selYear, selMonth, selDay, selHour, selMin;
    private static final int PICK_IMAGE = 100;
    private ImageView pictureView;

    private final List<EditText> allFields = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        Spinner eventTypeSelSpinner = findViewById(R.id.event_creation_spinner);
        eventTypeSelSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, Type.values()));
        eventTypeSelSpinner.setSelection(Type.values().length-1);
        eventTypeSelSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                eventype = Type.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                return;
            }
        });

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

                DatePickerDialog datePickerDialog = new DatePickerDialog(EventCreateActivity.this, new DatePickerDialog.OnDateSetListener() {
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
                TimePickerDialog timePickerDialog = new TimePickerDialog(EventCreateActivity.this,
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

        //Select image
        pictureView =  findViewById(R.id.event_picture);

        pictureView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        //Validate
        validateCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAllFieldsAreFilledAndCorrect()) {


                    //TODO: for now some fields aren't used and it only creates private event -> should be extended afterward
                    PrivateEvent event = new PrivateEvent(
                            FirebaseAuth.getInstance().getCurrentUser().getUid().toString(),
                            eventName.getText().toString(),
                            dateConversion(eventDate, eventTime),
                            getLocationFromAddress(eventAddress.getText().toString()),
                            eventAddress.getText().toString(),
                            eventDescription.getText().toString(),
                            eventype);

                    event.store().thenAccept(task1->{
                        task1.thenAccept(task2 ->{
                            if(task2.isSuccessful()){
                                nextStep();
                            } else {
                                Log.d(DEBUG_TAG, "couldn't store event");
                            }
                        });
                    });
                }
            }
        });
    }

    //TODO : decide what next step is
    private void nextStep(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    //Helpers
    private void openGallery(){
        Intent gallery = new Intent(Intent.ACTION_PICK);
        gallery.setDataAndType( MediaStore.Images.Media.INTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Uri imageUri = data.getData();
            pictureView.setImageURI(imageUri);
        }
    }

    private boolean checkAllFieldsAreFilledAndCorrect() {
        return allFields.stream().map(InputValidator::verifyGenericInput).reduce(true, (a, b) -> a && b) && eventype != Type.NOTHING;
    }

    private Date dateConversion(LocalDate date, LocalTime time){
        LocalDateTime datetime = LocalDateTime.of(date, time);
        return Date.from(datetime.atZone(ZoneId.systemDefault()).toInstant());

    }

    public GeoPoint getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(this);
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address coordinates = address.get(0);
            coordinates.getLatitude();
            coordinates.getLongitude();

            Log.d(DEBUG_TAG, coordinates.toString());

            GeoPoint location = new GeoPoint((double) (coordinates.getLatitude()),
                    (double) (coordinates.getLongitude()));

            Log.d(DEBUG_TAG, location.toString());

            return location;
        } catch (Exception e){
            return null;
        }
    }

}