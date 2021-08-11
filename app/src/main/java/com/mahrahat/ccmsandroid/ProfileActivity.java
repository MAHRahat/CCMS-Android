package com.mahrahat.ccmsandroid;

import android.database.DatabaseUtils;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.mahrahat.ccmsandroid.CCMSConstants.EP_USER;
import static com.mahrahat.ccmsandroid.CCMSConstants.SERVER_URL;
import static com.mahrahat.ccmsandroid.HelperMethods.showLongToast;


/**
 * Shows profile information of the current user and also allows to update.
 */

public class ProfileActivity extends UpButtonMenuActivity {

    private final String TAG = "CCMS Profile Activity";

    private String token;

    private TextView etProfileId, etProfileEmail;
    private EditText etProfileName, etProfileAddress;
    private Button btnProfileUpdate;
    private String profileURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle(R.string.menu_profile);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        token = DBHelper.getInstance(getApplicationContext()).readValueOfKee("Token");
        if (token == null) {
            DBHelper.getInstance(getApplicationContext()).deleteDataOnLogOut();
            finishAffinity();
            return;
        }
        initUI();
        setUpButton();
        fetchNShow();
    }

    private void initUI() {
        etProfileId = findViewById(R.id.et_profile_id);
        etProfileEmail = findViewById(R.id.et_profile_email);
        etProfileName = findViewById(R.id.et_profile_name);
        etProfileAddress = findViewById(R.id.et_profile_address);
        btnProfileUpdate = findViewById(R.id.btn_profile_update);
    }

    private void setUpButton() {
        btnProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String proName = etProfileName.getText().toString().trim();
                String proAddress = etProfileAddress.getText().toString().trim();
                tryToPatch(proName, proAddress);
            }
        });
    }

    /**
     * Sends updated profile information to the server via PATCH request.
     *
     * @param proName    Name of the user
     * @param proAddress Address of the user
     */
    private void tryToPatch(String proName, String proAddress) {
        profileURL = SERVER_URL + EP_USER;
        final HashMap<String, String> profileHeader = new HashMap<>();
        final HashMap<String, String> profileData = new HashMap<>();
        profileHeader.put("Authorization", "Token " + token);
        profileData.put("name", proName);
        profileData.put("address", proAddress);
        RequestQueue requestQueue = VRQSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(
                Request.Method.PATCH,
                profileURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showLongToast(getApplicationContext(), "Updated");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                        showLongToast(getApplicationContext(), error.toString());
                    }
                }
        ) {
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return profileData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return profileHeader;
            }
        };
        VRQSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    /**
     * Sends a GET request to the server to fetch profile information and then displays them.
     */
    private void fetchNShow() {
        profileURL = SERVER_URL + EP_USER;
        final HashMap<String, String> profileHeader = new HashMap<>();
        profileHeader.put("Authorization", "Token " + token);
        RequestQueue requestQueue = VRQSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                profileURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            String userId = jsonResponse.getString("user_id");
                            String userEmail = jsonResponse.getString("email");
                            String userName = jsonResponse.getString("name");
                            String userAddress = jsonResponse.getString("address");
                            DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                            if (DatabaseUtils.queryNumEntries(dbHelper.getReadableDatabase(), "user") > 1) {
                                dbHelper.deleteValueOfKee("id");
                                dbHelper.deleteValueOfKee("email");
                                dbHelper.deleteValueOfKee("name");
                                dbHelper.deleteValueOfKee("address");
                            }
                            if (userName == null || userName.equals("null")) {
                                userName = "";
                            }
                            if (userAddress == null || userAddress.equals("null")) {
                                userAddress = "";
                            }
                            dbHelper.insertValueOfKee("id", userId);
                            dbHelper.insertValueOfKee("email", userEmail);
                            dbHelper.insertValueOfKee("name", userName);
                            dbHelper.insertValueOfKee("address", userAddress);
                            etProfileId.setText(userId);
                            etProfileEmail.setText(userEmail);
                            etProfileName.setText(userName);
                            etProfileAddress.setText(userAddress);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                        showLongToast(getApplicationContext(), error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return profileHeader;
            }
        };
        VRQSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
