package com.ncnf.views.fragments.group;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.models.User;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import dagger.hilt.android.AndroidEntryPoint;

import static com.google.android.material.snackbar.BaseTransientBottomBar.LENGTH_SHORT;
import static com.ncnf.utilities.StringCodes.IMG_PICK_CODE;
import static java.lang.Math.max;
import static java.lang.Math.min;

@AndroidEntryPoint
public class GroupEditingFragment extends Fragment {

    private final User user;
    private TextView meetingPoint;
    private GeoPoint userLocation;
    private GeoPoint meetingPointLocation;
    private final ActivityResultLauncher<Intent> searchBarLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> updateGroupLocation(result));

    public TextView groupName;

    private TimePickerDialog timePickerDialog;
    private LocalTime time;
    private TextView meetingTime;

    private TextView description;

    private DatePickerDialog datePickerDialog;
    private LocalDate date;
    private TextView meetingDate;

    private ImageView groupPicture;
    private Bitmap bitMap;

    public GroupEditingFragment(User user){
        this.user = user;
    }

    public LocalTime getGroupTime() {
        return time;
    }

    public GeoPoint getMeetingPointLocation() {
        return meetingPointLocation;
    }

    public String getGroupName(){
        return this.groupName.getText().toString();
    }

    public String getGroupDescription(){
        return this.description.getText().toString();
    }

    public LocalDate getGroupDate() {
        return date;
    }

    public String getMeetingPointAddress() {
        return this.meetingPoint.getText().toString();
    }

    public ImageView getGroupPicture() {
        return groupPicture;
    }

    public Bitmap getPictureBitMap(){
        return this.bitMap;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_group_editing, container, false);
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        this.meetingDate = requireView().findViewById(R.id.date_text_group_creation);
        this.meetingTime = requireView().findViewById(R.id.time_text_group_creation);
        this.meetingPoint = requireView().findViewById(R.id.meeting_point_group_creation);
        this.groupPicture = requireView().findViewById(R.id.personal_profile_picture);
        this.groupName = requireView().findViewById(R.id.group_name);
        this.description = requireView().findViewById(R.id.group_bio_edit);

        this.groupPicture.setImageBitmap(BitmapFactory.decodeResource(this.getResources(), R.drawable.default_profile_picture));

        LocalTime time = LocalTime.now();

        this.timePickerDialog = new TimePickerDialog(requireActivity(), (view1, hourOfDay, minute) -> {
            this.time = LocalTime.of(hourOfDay, minute);
            this.meetingTime.setText(this.time.toString());
        }, time.getHour(), time.getMinute(), true);

        LocalDate date = LocalDate.now();
        this.datePickerDialog = new DatePickerDialog(requireActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, (view1, year, month, day) -> {
            this.date = LocalDate.of(year, month, day);
            this.meetingDate.setText(this.date.toString());
        }, date.getYear(), date.getMonth().getValue()-1, date.getDayOfMonth());
        this.datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        this.meetingDate.setOnClickListener(v -> setDate(v));
        this.meetingTime.setOnClickListener(v -> setTime(v));
        this.meetingPoint.setOnClickListener(v -> launchAddressSearchBar(v));
        this.groupPicture.setOnClickListener(v -> changeProfilePicture(v));

        initUser();
    }

    private void initUser() {
        this.user.loadUserFromDB().thenAccept(user -> {
            this.userLocation = user.getLocation();
        });
    }

    public void setTime(View view){
        timePickerDialog.show();
    }

    public void setDate(View view){
        this.datePickerDialog.show();
    }

    public void launchAddressSearchBar(View view){
        List<Place.Field> fields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

        Autocomplete.IntentBuilder intent = new Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields);

        if(user.getLocation() != null){
            LatLng northEast = new LatLng(min(this.userLocation.getLatitude() + 1, 180), min(this.userLocation.getLongitude() + 1, 180));
            LatLng southWest = new LatLng(max(this.userLocation.getLatitude() - 1, -180), max(this.userLocation.getLongitude() - 1, -180));

            intent.setLocationBias(RectangularBounds.newInstance(northEast, southWest));

        }
        searchBarLauncher.launch(intent.build(getActivity()));
    }

    private void updateGroupLocation(ActivityResult result) {
        if (result.getResultCode() == Activity.RESULT_OK) {
            Place place = Autocomplete.getPlaceFromIntent(result.getData());
            String meetingPointAddress = place.getName() + ", " + place.getAddress();
            this.meetingPointLocation = new GeoPoint(place.getLatLng().latitude, place.getLatLng().longitude);
            this.meetingPoint.setText(meetingPointAddress);
        }
    }

    public void changeProfilePicture(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMG_PICK_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == IMG_PICK_CODE ){
            if(resultCode == AppCompatActivity.RESULT_OK){
                Uri imageUri = data.getData();
                try {
                    this.bitMap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                this.groupPicture.setImageURI(data.getData());
            } else {
                Snackbar.make(getActivity().findViewById(android.R.id.content), "Error importing the group picture : please retry", LENGTH_SHORT).show();
            }
        }
    }


}