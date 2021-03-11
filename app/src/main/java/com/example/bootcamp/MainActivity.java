package com.example.bootcamp;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String EXTRA_MESSAGE = "com.example.bootcamp.EXTRA_MESSAGE";
    public static final String LAST_KEY = "last";
    public static final String FIRST_KEY = "first";
    public static final String AGE_KEY = "age";
    public static final String TAG = "eloidebug";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /*Called when the user clicks the button*/
    public void sendMessage(View view){

        /**
         * First step is to store the user
         */

        // We get the field of the user
        EditText firstText = (EditText) findViewById(R.id.first_field);
        String first_name = firstText.getText().toString();

        EditText lastText = (EditText) findViewById(R.id.last_field);
        String last_name = lastText.getText().toString();

        EditText ageText =  (EditText) findViewById(R.id.birth_field);
        String birth_str = ageText.getText().toString();
        if(first_name.isEmpty() | last_name.isEmpty() | birth_str.isEmpty()){
            return;
        }

        String id = last_name.substring(0, 3) + first_name.charAt(0) + birth_str.substring(0,2);

        int birth_year = Integer.parseInt(birth_str);

        Log.d(TAG,"Got the correct strings");


        //We get the db
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Log.d(TAG,"Successfully created a database");

        // Create the user and store it
        Map<String, Object> user = new HashMap<>();
        user.put(FIRST_KEY, first_name);
        user.put(LAST_KEY, last_name);
        user.put(AGE_KEY, birth_year);

        DocumentReference userDoc = db.document("users/" + id);
        userDoc.set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "User has been saved");
                } else {
                    Log.d(TAG, "User hasn't been saved");
                }
            }
        });

        /**
         * Second step is to display the name of the user saying its a been stored correctly
         */
        Intent intent = new Intent(this, DisplayMessageActivity.class);


        String message = first_name + " " + last_name + "/" + id;

        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }
}