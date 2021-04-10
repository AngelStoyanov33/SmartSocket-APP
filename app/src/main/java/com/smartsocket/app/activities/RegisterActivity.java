package com.smartsocket.app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.smartsocket.app.R;
import com.smartsocket.app.utils.EmailUtils;
import com.smartsocket.app.utils.PasswordUtils;
import com.smartsocket.app.utils.UsernameUtils;

import java.util.ArrayList;


public class RegisterActivity extends AppCompatActivity {

    private EditText usernameInput;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordConfirmInput;
    private Button switchToLoginButton;
    private Button signUpButton;

    private FirebaseAuth fireAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_register);

        switchToLoginButton = (Button) findViewById(R.id.switchToLoginButton);
        switchToLoginButton.setOnClickListener(view -> {
            Intent switchToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(switchToLogin);
            finish();
        });

        fireAuth = FirebaseAuth.getInstance();

        usernameInput = (EditText) findViewById(R.id.signUpUsernameInput);
        emailInput = (EditText) findViewById(R.id.signUpEmailInput);
        passwordInput = (EditText) findViewById(R.id.signUpPasswordInput);
        passwordConfirmInput = (EditText) findViewById(R.id.signUpPasswordConfirmInput);
        signUpButton = (Button)findViewById(R.id.signUpButton);

        signUpButton.setOnClickListener(view -> {
            String username = usernameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim().toLowerCase();
            String password = passwordInput.getText().toString().trim();
            String passwordConfirm = passwordConfirmInput.getText().toString().trim();

            ArrayList<String> errors = new ArrayList<>();
            if(!UsernameUtils.isValid(username)){
                usernameInput.setError("Invalid username");
                return;
            }
            if(!EmailUtils.isValid(email)){
                emailInput.setError("Invalid email");
                return;
            }
            if(!PasswordUtils.isSecure(password, passwordConfirm, errors)){
                passwordInput.setError(errors.get(0));
                errors = null;
                return;
            }

            fireAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener(authResult -> {
                        Intent switchToDashboard = new Intent(getApplicationContext(), DashboardActivity.class);
                        startActivity(switchToDashboard);
                        finish();

                    }).addOnFailureListener(e -> Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show());


        });


    }
}