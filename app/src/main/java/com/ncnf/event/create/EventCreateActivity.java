package com.ncnf.event.create;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.event.Event;
import com.ncnf.event.PublicEvent;
import com.ncnf.main.MainActivity;
import com.ncnf.storage.FileStore;
import com.ncnf.user.User;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.InputValidator;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.DEBUG_TAG;
import static com.ncnf.utilities.StringCodes.POPUP_POSITIVE_BUTTON;
import static com.ncnf.utilities.StringCodes.POPUP_TITLE;

@AndroidEntryPoint
public class EventCreateActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Inject
    public User user;

    private Event.Type eventType;
    private LocalDate eventDate = LocalDate.now();
    private LocalTime eventTime = LocalTime.now().truncatedTo(ChronoUnit.MINUTES);
    private int selYear, selMonth, selDay, selHour, selMin;
    private static final int PICK_IMAGE = 100;
    private ImageView pictureView;

    private int minAgeVal = 0;
    private double priceVal = 0;

    EditText eventName;
    EditText eventDescription;
    EditText eventEmail;
    EditText eventAddress;
    EditText minAge;
    EditText eventPrice;


    private final List<EditText> allFields = new LinkedList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creation);

        Button validate = findViewById(R.id.validate_event);

        eventName = findViewById(R.id.set_event_name);
        eventDescription = findViewById(R.id.set_event_description);
        eventAddress = findViewById(R.id.set_event_address);
        eventEmail = findViewById(R.id.set_contact_email);

        minAge = findViewById(R.id.set_min_age);
        eventPrice = findViewById(R.id.set_event_price);

        //Date Selection
        Button dateSelection = findViewById(R.id.set_event_date_button);
        TextView dateDisplay = (TextView) findViewById(R.id.display_event_date);
        dateSelection.setFocusable(false);

        dateSelection.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            selYear = calendar.get(Calendar.YEAR);
            selMonth = calendar.get(Calendar.MONTH);
            selDay = calendar.get(Calendar.DATE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(EventCreateActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view1, year, month, dayOfMonth) -> {
                selYear = year;
                selMonth = month;
                selDay = dayOfMonth;
                eventDate = LocalDate.of(selYear, Month.of(selMonth+1), dayOfMonth);
                dateDisplay.setText(eventDate.toString());
            }, selYear, selMonth, selDay);
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        // Time Selection

        Button timeSelection = findViewById(R.id.set_event_time_button);
        TextView timeDisplay = (TextView) findViewById(R.id.display_event_time);
        timeSelection.setFocusable(false);

        timeSelection.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(EventCreateActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view12, hourOfDay, minute) -> {
                selHour = hourOfDay;
                selMin = minute;
                eventTime = LocalTime.of(selHour, selMin);
                String text = (hourOfDay < 10) ? "0" + hourOfDay + ":" : String.valueOf(hourOfDay) + ":";
                text += (minute < 10) ? "0" + minute : String.valueOf(minute);
                timeDisplay.setText(text);
            }, 0, 0, true);
            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.show();
        });

        //Select image
        pictureView = findViewById(R.id.set_event_image);
        pictureView.setOnClickListener(v -> openGallery());

        //Email default setting
        CheckBox useDefault = findViewById(R.id.use_personal_email_checkbox);
        useDefault.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked) {
                if(user != null) {
                    eventEmail.setText(user.getEmail());
                }
            }
            else {
                eventEmail.setText("");
            }
        });

        // Select event type

        Spinner spinner = (Spinner) findViewById(R.id.select_event_type);
        spinner.setOnItemSelectedListener(EventCreateActivity.this);
        List<String> options = Stream.of(Event.Type.values()).map(Event.Type::name).collect(Collectors.toList());

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(EventCreateActivity.this, android.R.layout.simple_spinner_item, options);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(typeAdapter);

        // Set price

        eventPrice.setOnEditorActionListener((TextView.OnEditorActionListener) (v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String s = eventPrice.getText().toString().contains("€") ? eventPrice.getText().toString().replaceAll("€", "") : eventPrice.getText().toString();
                double price = Double.parseDouble(s);
                DecimalFormat euroFormat = new DecimalFormat("#.00");
                eventPrice.setText(euroFormat.format(price) + "€");
                return true;
            }
            else {
                return false;
            }
        });

        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkAllFieldsAreFilledAndCorrect()) {

                    if (user != null) {

                        // TODO: might change with the new event class
                        // File upload must happened after the event is saved, such that the image path
                        // contains the event's UUID
                        FileStore file = new FileStore(Event.IMAGE_PATH, String.format(Event.IMAGE_NAME, "PLEASE_REPLACE_WITH_UUID"));
                        pictureView.setDrawingCacheEnabled(true);
                        pictureView.buildDrawingCache();
                        Bitmap bitmap = ((BitmapDrawable) pictureView.getDrawable()).getBitmap();
                        file.uploadImage(bitmap);

                        //TODO: for now some fields aren't used and it only creates private event -> should be extended afterward
                        DateAdapter date = new DateAdapter(eventDate.getYear(), eventDate.getMonthValue(), eventDate.getDayOfMonth(), eventTime.getHour(), eventTime.getMinute());
                        PublicEvent event = new PublicEvent(user.getUuid(),
                                eventName.getText().toString(),
                                date.getDate(), getLocationFromAddress(eventAddress.getText().toString()),
                                eventAddress.getText().toString(),
                                eventDescription.getText().toString(),
                                eventType, minAgeVal, priceVal, eventEmail.getText().toString()
                        );
                        if(event != null) {

                            user.createEvent(event).thenAccept(task -> nextStep()).exceptionally(exception -> {
                                failToCreateEvent(exception.getMessage());
                                return null;
                            });
                        }
                    }
                    else {
                        Log.d(DEBUG_TAG, "Can't create new event if not logged in");
                    }
                }
            }
        });
    }


    //Helpers
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

            GeoPoint location = new GeoPoint((double) (coordinates.getLatitude()),
                    (double) (coordinates.getLongitude()));

            return location;
        } catch (Exception e){
            return null;
        }


    }

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

    //TODO : decide what next step is
    private void nextStep(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Pop-up telling the user that the the application failed to save its event
     */
    private void failToCreateEvent(String s){
        AlertDialog.Builder popup = new AlertDialog.Builder(this);
        popup.setCancelable(true);
        popup.setTitle(POPUP_TITLE);
        popup.setMessage(s);
        popup.setPositiveButton(POPUP_POSITIVE_BUTTON, (dialog, which) -> {
            dialog.cancel();
        });
        popup.show();

    }


    private boolean checkAllFieldsAreFilledAndCorrect() {

        EditText[] fields = new EditText[] {eventName, eventDescription, eventEmail, eventAddress};
        boolean interm = Arrays.asList(fields).stream().map(InputValidator::verifyGenericInput).reduce(true, (a, b) -> a && b) && eventType != Event.Type.NOTHING;
        if(!interm) {
            return false;
        }

        // TODO : find a way to reject invalid addresses
        /**
         if(getLocationFromAddress(eventAddress.getText().toString()) == null) {
         eventAddress.setError("Invalid address");
         return false;
         }
         **/

        if(!(InputValidator.verifyEmailInput(eventEmail.getText().toString()))) {
            eventEmail.setError("Please enter a correct email address.");
            return false;
        }

        if(!(minAge == null || minAge.getText().length() == 0)) {
            minAgeVal = Integer.parseInt(minAge.getText().toString());
        }

        if(!(eventPrice == null || eventPrice.getText().length() == 0)) {
            priceVal = Double.parseDouble(eventPrice.getText().toString().replaceAll("€", ""));
        }

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        eventType = Event.Type.valueOf(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

}