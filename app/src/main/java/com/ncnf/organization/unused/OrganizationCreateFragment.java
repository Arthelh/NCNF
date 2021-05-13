package com.ncnf.organization.unused;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;


import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.GeoPoint;
import com.ncnf.R;
import com.ncnf.user.User;
import com.ncnf.organization.Organization;
import com.ncnf.organization.OrganizationRepository;
import com.ncnf.utilities.InputValidator;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.ncnf.utilities.StringCodes.PICK_IMAGE;


@AndroidEntryPoint
public class OrganizationCreateFragment extends Fragment {

    @Inject
    public User user; //=CurrentUserModule.getCurrentUser();

    @Inject
    public OrganizationRepository organizationRepository;

    private EditText name;
    private EditText postalAddress;
    private EditText email;
    private EditText phone;
    private ImageView picture;
    private Button validateButton;


    public OrganizationCreateFragment() {
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_organization_creation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initFields(view);
        initButton();
        initCancelButton(view);
    }


    private void initCancelButton(View v){
        Button cancel = v.findViewById(R.id.cancel_button);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStackImmediate();
            }
        });
    }

    private void initFields(View view) {
        name = view.findViewById(R.id.organization_name);
        postalAddress = view.findViewById(R.id.organization_postal_address);
        email = view.findViewById(R.id.organization_email);
        phone = view.findViewById(R.id.organization_phone);
        picture = view.findViewById(R.id.organization_picture);
        picture.setOnClickListener(v -> openGallery());
        validateButton = view.findViewById(R.id.organization_validate_button);
    }

    private void initButton(){
        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInputs()){
                    if(user == null){
                        InputValidator.setErrorMsg(validateButton, "This user is invalid");
                    } else {
                        //Create the organisation
                        Organization organization = new Organization(name.getText().toString(),
                                new GeoPoint(0.0, 0.0),
                                postalAddress.getText().toString(), email.getText().toString(),
                                phone.getText().toString(), user.getUuid());
                        //Update the database
                        organizationRepository.addUserToOrganization(user.getUuid(), organization.getUuid().toString()).thenAccept(suc -> {
                           if (!suc) {
                               //TODO handle exception
                            }
                        });
                    }
                }
            }
        });
    }

    private void openGallery() {
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, PICK_IMAGE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            picture.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
    }

    //TODO move and adapt function to InputValidator
    private boolean validateInputs(){
        EditText[] fields = new EditText[] {name, email, phone, postalAddress};
        boolean firstCheck = Arrays.stream(fields).map(InputValidator::verifyGenericInput).reduce(true, (a, b) -> a && b);
        if(!firstCheck){
            validateButton.setError("Some fields are either missing or badly formatted");
            return false;
        } else {
            if(!InputValidator.verifyEmailInput(email.getText().toString())){
                email.setError("Email Incorrectly Formatted");
                return false;
            } else if(!InputValidator.verifyPhoneNumberInput(phone.getText().toString())){
                phone.setError("Phone Number Incorrectly Formatted");
                return false;
            }
            return true;
        }
    }
}
