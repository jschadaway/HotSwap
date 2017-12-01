package com.teamcaffeine.hotswap.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.teamcaffeine.hotswap.R;
import com.teamcaffeine.hotswap.activity.HomeActivity;
import com.teamcaffeine.hotswap.activity.navigation.NavigationActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddUserDetailsActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private EditText firstName;
    private EditText lastName;
    private EditText phoneNumber;
    private Button btnAddAddress;
    private Button btnAddPayment;
    private Button btnSubmit;

    private FirebaseDatabase database;
    private DatabaseReference users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_details);

        // get an instance of the Firebase and get the reference to the current user
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        // get references to all views
        firstName = findViewById(R.id.edtFirstName);
        lastName = findViewById(R.id.edtLastName);
        phoneNumber = findViewById(R.id.edtPhoneNumber);
        btnAddAddress = findViewById(R.id.btnAddress);
        btnAddPayment = findViewById(R.id.btnPayment);
        btnSubmit = findViewById(R.id.btnSubmit);

        // get an instance of the database
        database = FirebaseDatabase.getInstance();
        // get a reference to the Users table
        users = database.getReference().child("Users");

        // set the functionality of the "Submit" button
        // see the "submit()" method below
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        // set functionality of the "add address" button
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // set functionality of the "add payment" button
        btnAddPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    // Submit method
    // When the user clicks "submit," their full user account is created
    public void submit() {
        // get the strings in each of the Edit Texts
        String firstName_string = firstName.getText().toString();
        String lastName_string = lastName.getText().toString();
        String phoneNumber_string = phoneNumber.getText().toString();

        // only allow the user to move forward through login if they have entered a name and phone number
        if (firstName_string != "" && lastName_string != "" && phoneNumber_string != "") {

            // now that their details have been added, we set the addDetails flag to true
            // this flag will be used on login in the future to indicate that the user does not need to
            // go to the AddUserDetailsActivity next time they login
            boolean addedDetails = true;
            // get the user ID and email from the existing database entry that was created on the initial login
            String uid = user.getUid();
            String email = user.getEmail();
            // create a date format for the memberSince attribute
            DateFormat dateFormat = new SimpleDateFormat("MMMM, yyyy");
            // set the memberSince attribute to the current date
            // this will show on the user's profile to indicate how long they have been a HotSwap member
            Date memberSince = new Date();

            // create a new User object will all user details
            User userData = new User(addedDetails, uid, firstName_string, lastName_string, email, dateFormat.format(memberSince), phoneNumber_string);

            // create a hashmap object
            // this will be used to enter the data into firebase
            Map<String, Object> userUpdate = new HashMap<>();
            // enter the information from the User object into the hashmap
            userUpdate.put(uid, userData.toMap());

            // update the existing user database entry to include the new information
            users.updateChildren(userUpdate);

            // now that te user information has been updated, the user can enter the app
            Intent i = new Intent(AddUserDetailsActivity.this, NavigationActivity.class);
            startActivity(i);
        } else {
            // if the user did not enter all of their details, show a toast to instruct them to enter all detals
            Toast.makeText(AddUserDetailsActivity.this, "Please enter all details",
                    Toast.LENGTH_LONG).show();
        }
    }
}
