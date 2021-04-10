package com.smartsocket.app.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.smartsocket.app.R;
import com.smartsocket.app.service.HTTPRequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class DashboardActivity extends AppCompatActivity{

    private Button signOutButton;
    private TextView welcomeText;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_dashboard);

        signOutButton = (Button) findViewById(R.id.logOutButton);
        signOutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent switchToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(switchToLogin);
        });

        welcomeText = (TextView) findViewById(R.id.welcomeText);
        int index = FirebaseAuth.getInstance().getCurrentUser().getEmail().indexOf('@');
        String uname = FirebaseAuth.getInstance().getCurrentUser().getEmail().substring(0, index);
        uname = uname.replace(".", "_");
        uname = uname.replace("-", "_");
        welcomeText.setText("Hello, " + uname);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener (item -> {
            switch (item.getItemId()){
                case R.id.profile_item:
                    //TODO
                    return true;
                case R.id.add_item:
                    AlertDialog.Builder adBuilder = new AlertDialog.Builder(DashboardActivity.this);
                    View mView = getLayoutInflater().inflate(R.layout.add_item_popup_layout, null);
                    EditText deviceUIDInput = (EditText) mView.findViewById(R.id.input_device_code);
                    Button addButton = (Button) mView.findViewById(R.id.add_device_button);
                    adBuilder.setView(mView);
                    AlertDialog dialog = adBuilder.create();
                    dialog.show();
                    addButton.setOnClickListener(view -> {
                        if(!deviceUIDInput.getText().toString().isEmpty()){
                            //TODO
                            dialog.cancel();

                        }

                    });

                    return true;
            }
            return false;
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        HTTPRequestManager httpRequestManager = new HTTPRequestManager();
                        try {
                            JSONObject json = new JSONObject();
                            json.put("token", idToken);
                            httpRequestManager.sendJSONRequest(HTTPRequestManager.SERVER_LOCATION + "/status", json.toString());
                        } catch (IOException | JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}