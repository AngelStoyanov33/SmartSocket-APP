package com.smartsocket.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.smartsocket.app.R;

public class DashboardActivity extends AppCompatActivity {

    private Button signOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        signOutButton = (Button) findViewById(R.id.logOutButton);
        signOutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent switchToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(switchToLogin);
        });
    }
}