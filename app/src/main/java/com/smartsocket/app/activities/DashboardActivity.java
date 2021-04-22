package com.smartsocket.app.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.auth.FirebaseUser;
import com.smartsocket.app.R;
import com.smartsocket.app.service.HTTPRequestManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

public class DashboardActivity extends AppCompatActivity{

    private Button signOutButton;
    private TextView welcomeText;
    private BottomNavigationView bottomNavigationView;

    private String DUID = "";
    private String token = "";
    private int statusGlobal = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        setContentView(R.layout.activity_dashboard);
        MaterialCardView socketCard = (MaterialCardView) findViewById(R.id.socket_card);
        socketCard.setVisibility(View.INVISIBLE);

        signOutButton = (Button) findViewById(R.id.logOutButton);
        signOutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent switchToLogin = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(switchToLogin);
        });

        ImageButton socketIButton = (ImageButton) findViewById(R.id.socket_cv_button);
        TextView socketStatus = (TextView) findViewById(R.id.socket_cv_state);

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
                            FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                            mUser.getIdToken(true)
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful()) {
                                            String idToken = task.getResult().getToken();
                                            HTTPRequestManager httpRequestManager = new HTTPRequestManager();
                                            HashMap<String, String> params = new HashMap<>();
                                            HashMap<String, String> response = new HashMap<>();
                                            params.put("token", idToken);
                                            params.put("device_code", deviceUIDInput.getText().toString());
                                            httpRequestManager.sendJSONRequestVolley(HTTPRequestManager.SERVER_LOCATION + "/register", params, this.getApplicationContext(), response);
                                            dialog.cancel();
                                        } else {
                                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });


                        }

                    });

                    return true;
            }
            return false;
        });

        socketIButton.setOnClickListener(view -> {
            if(statusGlobal == 1){
                HashMap<String, String> params3 = new HashMap<>();
                params3.put("token", token);
                params3.put("device_code", DUID);
                params3.put("state", 0 + "");
                JsonObjectRequest req3 = new JsonObjectRequest(HTTPRequestManager.SERVER_LOCATION + "/state", new JSONObject(params3),
                        response3 -> {
                            Log.d("Response", response3.toString());
                            try {
                                int statusReceive = Integer.parseInt((String) response3.get("device_status"));
                                if(statusReceive == 0){
                                    statusGlobal = statusReceive;
                                    socketIButton.setImageResource(R.drawable.socket_power_off);
                                    socketStatus.setText("Inactive");
                                    socketStatus.setTextColor(getResources().getColor(R.color.red));
                                }else if(statusReceive == 1){
                                    statusGlobal = statusReceive;
                                    socketIButton.setImageResource(R.drawable.socket_power_on);
                                    socketStatus.setText("Active");
                                    socketStatus.setTextColor(getResources().getColor(R.color.green));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> VolleyLog.e("Error: ", error.getMessage()));

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(req3);
            }else if(statusGlobal == 0){
                HashMap<String, String> params3 = new HashMap<>();
                params3.put("token", token);
                params3.put("device_code", DUID);
                params3.put("state", 1 + "");
                JsonObjectRequest req3 = new JsonObjectRequest(HTTPRequestManager.SERVER_LOCATION + "/state", new JSONObject(params3),
                        response3 -> {
                            Log.d("Response", response3.toString());
                            try {
                                int statusReceive = Integer.parseInt((String) response3.get("device_status"));
                                socketCard.setVisibility(View.VISIBLE);
                                if(statusReceive == 0){

                                    socketIButton.setImageResource(R.drawable.socket_power_off);
                                    socketStatus.setText("Inactive");
                                    socketStatus.setTextColor(getResources().getColor(R.color.red));
                                    statusGlobal = statusReceive;
                                }else if(statusReceive == 1){
                                    socketIButton.setImageResource(R.drawable.socket_power_on);
                                    socketStatus.setText("Active");
                                    socketStatus.setTextColor(getResources().getColor(R.color.green));
                                    statusGlobal = statusReceive;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }, error -> VolleyLog.e("Error: ", error.getMessage()));

                RequestQueue requestQueue = Volley.newRequestQueue(this);
                requestQueue.add(req3);
            }
        });



        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            public void run() {
                FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                HashMap<String, String> params = new HashMap<>();
                                HashMap<String, String> response = new HashMap<>();
                                params.put("token", idToken);
                                params.put("device_code", DUID);
                                JsonObjectRequest req2 = new JsonObjectRequest(HTTPRequestManager.SERVER_LOCATION + "/status", new JSONObject(params),
                                        response2 -> {
                                            Log.d("Response", response2.toString());
                                            try {
                                                int status = Integer.parseInt((String) response2.get("device_status"));
                                                socketCard.setVisibility(View.VISIBLE);
                                                statusGlobal = status;
                                                token = idToken;
                                                DUID = response.get("device_code");
                                                if (status == 5) {
                                                    socketCard.setVisibility(View.VISIBLE);
                                                    socketIButton.setImageResource(R.drawable.socket_power_offline);
                                                    socketStatus.setText("Offline");
                                                    socketStatus.setTextColor(getResources().getColor(R.color.gray));
                                                    return;
                                                }

                                                if (status == 0) {
                                                    socketIButton.setImageResource(R.drawable.socket_power_off);
                                                    socketStatus.setText("Inactive");
                                                    socketStatus.setTextColor(getResources().getColor(R.color.red));
                                                    return;
                                                } else if (status == 1) {
                                                    socketIButton.setImageResource(R.drawable.socket_power_on);
                                                    socketStatus.setText("Active");
                                                    socketStatus.setTextColor(getResources().getColor(R.color.green));
                                                    return;
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }, error -> VolleyLog.e("Error: ", error.getMessage()));

                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                requestQueue.add(req2);
                            } });
            }
        };
        timer.schedule(timerTask, 0, 15000);



    }

    @Override
    protected void onStart() {
        super.onStart();

        MaterialCardView socketCard = (MaterialCardView) findViewById(R.id.socket_card);
        ImageButton socketIButton = (ImageButton) findViewById(R.id.socket_cv_button);
        TextView socketName = (TextView) findViewById(R.id.socket_cv_name);
        TextView socketStatus = (TextView) findViewById(R.id.socket_cv_state);

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String idToken = task.getResult().getToken();
                        HashMap<String, String> params = new HashMap<>();
                        HashMap<String, String> response = new HashMap<>();
                        params.put("token", idToken);
                        JsonObjectRequest req = new JsonObjectRequest(HTTPRequestManager.SERVER_LOCATION + "/load", new JSONObject(params),
                                response1 -> {
                                    Log.d("Response", response1.toString());
                                    try {
                                        response.put("device_code", (String) response1.get("device_code"));
                                        DUID = response.get("device_code");
                                        Log.d("DUID", response.get("device_code"));

                                        if(DUID != null){
                                            socketCard.setVisibility(View.VISIBLE);
                                            socketIButton.setImageResource(R.drawable.socket_power_offline);
                                            socketStatus.setText("Offline");
                                            socketStatus.setTextColor(getResources().getColor(R.color.gray));
                                        }


                                        socketName.setText(String.format("Socket %s", response.get("device_code")));
                                        HashMap<String, String> params2 = new HashMap<>();
                                        params2.put("token", idToken);
                                        params2.put("device_code", response.get("device_code"));
                                        JsonObjectRequest req2 = new JsonObjectRequest(HTTPRequestManager.SERVER_LOCATION + "/status", new JSONObject(params2),
                                                response2 -> {
                                                    Log.d("Response", response2.toString());
                                                    try {
                                                        int status = Integer.parseInt((String) response2.get("device_status"));
                                                        socketCard.setVisibility(View.VISIBLE);
                                                        statusGlobal = status;
                                                        token = idToken;
                                                        DUID = response.get("device_code");

                                                        if(status == 0){
                                                            socketIButton.setImageResource(R.drawable.socket_power_off);
                                                            socketStatus.setText("Inactive");
                                                            socketStatus.setTextColor(getResources().getColor(R.color.red));
                                                        }else if(status == 1){
                                                            socketIButton.setImageResource(R.drawable.socket_power_on);
                                                            socketStatus.setText("Active");
                                                            socketStatus.setTextColor(getResources().getColor(R.color.green));
                                                        }

                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }, error -> VolleyLog.e("Error: ", error.getMessage()));

                                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                                        requestQueue.add(req2);



                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }, error -> VolleyLog.e("Error: ", error.getMessage()));

                        RequestQueue requestQueue = Volley.newRequestQueue(this);
                        requestQueue.add(req);

                    } else {
                        Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

}