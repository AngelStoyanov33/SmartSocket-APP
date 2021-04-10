package com.smartsocket.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.smartsocket.app.R;
import com.smartsocket.app.utils.EmailUtils;
import com.smartsocket.app.utils.PasswordUtils;
import com.smartsocket.app.utils.UsernameUtils;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button switchToRegisterButton;
    private Button signInButton;

    private FirebaseAuth fireAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_login);

        switchToRegisterButton = (Button) findViewById(R.id.switchToRegisterButton);
        switchToRegisterButton.setOnClickListener(view -> {
            Intent switchToRegister = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(switchToRegister);
            finish();
        });

        fireAuth = FirebaseAuth.getInstance();

        emailInput = (EditText) findViewById(R.id.signInEmailInput);
        passwordInput = (EditText) findViewById(R.id.signInPasswordInput);
        signInButton = (Button)findViewById(R.id.signInButton);
        signInButton.setOnClickListener(view -> {
            String email = emailInput.getText().toString().trim().toLowerCase();
            String password = passwordInput.getText().toString().trim();
            ArrayList<String> errors = new ArrayList<>();

            if(!EmailUtils.isValid(email)){
                emailInput.setError("Invalid email");
                return;
            }
            if(password.isEmpty()){
                passwordInput.setError("Invalid password");
                return;
            }
            fireAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {


                        Intent switchToDashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(switchToDashboard);
                        finish();

                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());


        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser() != null){
            Intent switchToDashboard = new Intent(getApplicationContext(), DashboardActivity.class);
            startActivity(switchToDashboard);
            finish();
        }
    }

    @Override
    public void onBackPressed() { //Disable non token activity show up
    }
}