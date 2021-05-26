package com.ncnf.views.fragments.organization;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.models.Event;
import com.ncnf.models.Organization;
import com.ncnf.models.SocialObject;
import com.ncnf.models.User;
import com.ncnf.repositories.EventRepository;
import com.ncnf.repositories.OrganizationRepository;
import com.ncnf.storage.firebase.FirebaseFileStore;
import com.ncnf.utilities.DateAdapter;
import com.ncnf.utilities.InputValidator;
import com.ncnf.views.activities.organization.OrganizationProfileActivity;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static android.app.Activity.RESULT_OK;
import static com.ncnf.utilities.StringCodes.DEBUG_TAG;
import static com.ncnf.utilities.StringCodes.POPUP_POSITIVE_BUTTON;
import static com.ncnf.utilities.StringCodes.POPUP_TITLE;
import static com.ncnf.views.fragments.organization.OrganizationTabFragment.ORGANIZATION_UUID_KEY;
import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

@AndroidEntryPoint
public class EventCreateFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    @Inject
    public FirebaseUser user;

    @Inject
    public EventRepository eventRepository;

    @Inject
    public FirebaseFileStore firebaseFileStore;

    @Inject
    public OrganizationRepository organizationRepository;

    private Organization organization;
    private String uuid;
    private String userEmail;
    private String userUUID;

    private SocialObject.Type eventType;
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

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        requireActivity().getSupportFragmentManager().setFragmentResultListener("request Key", getViewLifecycleOwner(), (requestKey, result) -> {
            uuid = result.getString("organization_id");
        });
        userEmail = user.getEmail();
        userUUID = user.getUid();
        return inflater.inflate(R.layout.fragment_event_creation, container, false);
    }

    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        organizationRepository.getByUUID(this.uuid).thenAccept(o -> this.organization = o.get(0));

        Button validate =v.findViewById(R.id.validate_event);

        eventName = v.findViewById(R.id.set_event_name);
        eventDescription = v.findViewById(R.id.set_event_description);
        eventAddress = v.findViewById(R.id.set_event_address);
        eventEmail = v.findViewById(R.id.set_contact_email);

        minAge = v.findViewById(R.id.set_min_age);
        eventPrice = v.findViewById(R.id.set_event_price);

        //Date Selection
        Button dateSelection = v.findViewById(R.id.set_event_date_button);
        TextView dateDisplay = v.findViewById(R.id.display_event_date);
        dateSelection.setFocusable(false);

        dateSelection.setOnClickListener(view -> {
            Calendar calendar = Calendar.getInstance();
            selYear = calendar.get(Calendar.YEAR);
            selMonth = calendar.get(Calendar.MONTH);
            selDay = calendar.get(Calendar.DATE);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view1, year, month, dayOfMonth) -> {
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

        Button timeSelection = v.findViewById(R.id.set_event_time_button);
        TextView timeDisplay = (TextView) v.findViewById(R.id.display_event_time);
        timeSelection.setFocusable(false);

        timeSelection.setOnClickListener(view -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(requireContext(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view12, hourOfDay, minute) -> {
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
        pictureView = v.findViewById(R.id.set_event_image);
        pictureView.setOnClickListener(c -> openGallery());

        //Email default setting
        CheckBox useDefault = v.findViewById(R.id.use_personal_email_checkbox);
        useDefault.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                eventEmail.setText(userEmail);
            }
            else {
                eventEmail.setText("");
            }
        });

        // Select event type

        Spinner spinner = v.findViewById(R.id.select_event_type);
        spinner.setOnItemSelectedListener(EventCreateFragment.this);
        List<String> options = Stream.of(SocialObject.Type.values()).map(SocialObject.Type::name).collect(Collectors.toList());

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, options);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(typeAdapter);

        // Set price

        eventPrice.setOnEditorActionListener((TextView.OnEditorActionListener) (ve, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                String s = eventPrice.getText().toString().contains("€") ? eventPrice.getText().toString().replaceAll("€", "") : eventPrice.getText().toString();
                double price = parseDouble(s);
                DecimalFormat euroFormat = new DecimalFormat("#.00");
                eventPrice.setText(euroFormat.format(price) + "€");
                return true;
            }
            else {
                return false;
            }
        });

        validate.setOnClickListener(v1 -> {
            if (checkAllFieldsAreFilledAndCorrect()) {
                UUID eventUUID = UUID.randomUUID();

                DateAdapter date = new DateAdapter(eventDate.getYear(), eventDate.getMonthValue(), eventDate.getDayOfMonth(), eventTime.getHour(), eventTime.getMinute());
                Event event = new Event(
                        organization.getUuid().toString(),
                        eventUUID,
                        eventName.getText().toString(),
                        LocalDateTime.of(eventDate.getYear(), eventDate.getMonthValue(), eventDate.getDayOfMonth(), eventTime.getHour(), eventTime.getMinute()),
                        getLocationFromAddress(eventAddress.getText().toString()),
                        eventAddress.getText().toString(),
                        eventDescription.getText().toString(),
                        eventType,
                        new LinkedList<>(),
                        parseInt(minAge.getText().toString()),
                        parseDouble(eventPrice.getText().toString()),
                        new LinkedList<>(),
                        eventEmail.getText().toString());

                firebaseFileStore.setPath(SocialObject.IMAGE_PATH, String.format(SocialObject.IMAGE_NAME, event.getUuid()));
                pictureView.setDrawingCacheEnabled(true);
                pictureView.buildDrawingCache();
                Bitmap bitmap = ((BitmapDrawable) pictureView.getDrawable()).getBitmap();

                // Simultaneously upload the image and save the group.
                CompletableFuture.allOf(
                        firebaseFileStore.uploadImage(bitmap),
                        eventRepository.storeEvent(event),
                        organizationRepository.addEventToOrganization(organization.getUuid().toString(), eventUUID.toString())
                ).thenAccept(t -> nextStep()).exceptionally(e -> {
                    failToCreateEvent(e.getMessage());
                    return null;
                });
            }
        });
    }

    //Helpers
    public GeoPoint getLocationFromAddress(String strAddress){

        Geocoder coder = new Geocoder(requireContext());
        List<Address> address;

        try {
            address = coder.getFromLocationName(strAddress,5);
            if (address==null) {
                return null;
            }
            Address coordinates = address.get(0);
            coordinates.getLatitude();
            coordinates.getLongitude();

            return new GeoPoint((double) (coordinates.getLatitude()),
                    (double) (coordinates.getLongitude()));
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
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE){
            Uri imageUri = data.getData();
            pictureView.setImageURI(imageUri);
        }
    }

    private void nextStep(){
        FragmentManager fm = getParentFragmentManager();
        fm.popBackStack();
    }

    /**
     * Pop-up telling the user that the the application failed to save its event
     */
    private void failToCreateEvent(String s){
        AlertDialog.Builder popup = new AlertDialog.Builder(requireContext());
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
        boolean interm = Arrays.stream(fields).map(InputValidator::verifyGenericInput).reduce(true, (a, b) -> a && b) && eventType != SocialObject.Type.NOTHING;
        if(!interm) {
            return false;
        }

        if(!(InputValidator.verifyEmailInput(eventEmail.getText().toString()))) {
            eventEmail.setError("Please enter a correct email address.");
            return false;
        }

        if(!(minAge == null || minAge.getText().length() == 0)) {
            minAgeVal = parseInt(minAge.getText().toString());
        }

        if(!(eventPrice == null || eventPrice.getText().length() == 0)) {
            priceVal = parseDouble(eventPrice.getText().toString().replaceAll("€", ""));
        }

        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String item = parent.getItemAtPosition(position).toString();
        eventType = SocialObject.Type.valueOf(item);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // do nothing
    }

}