package com.mahrahat.ccmsandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.mahrahat.ccmsandroid.CCMSConstants.EP_CREATE_COMPLAINT;
import static com.mahrahat.ccmsandroid.CCMSConstants.EP_USER;
import static com.mahrahat.ccmsandroid.CCMSConstants.SERVER_URL;
import static com.mahrahat.ccmsandroid.HelperMethods.showLongToast;


/**
 * Default Activity for logged in user. Provides a form to submit a complaint.
 */

public class HomeActivity extends OptionsMenuActivity {

    private final String TAG = "Home Activity";

    private EditText etComplaint;
    private Button btnSubmitComplaint;
    private String complaintURL;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        token = DBHelper.getInstance(getApplicationContext()).readValueOfKee("Token");
        if (token == null) {
            DBHelper.getInstance(getApplicationContext()).deleteDataOnLogOut();
            finishAffinity();
            return;
        }
        if (DBHelper.getInstance(getApplicationContext()).readValueOfKee("id") == null) {
            fetchNStore();
        }
        initUI();
        setUpSubmitComplaintButton();
    }

    private void initUI() {
        etComplaint = findViewById(R.id.et_complaint);
        btnSubmitComplaint = findViewById(R.id.submit_complaint);
    }

    private void setUpSubmitComplaintButton() {
        btnSubmitComplaint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String complaintText = etComplaint.getText().toString().trim();
                if (complaintText.isEmpty()) {
                    showLongToast(getApplicationContext(), "Can not submit empty complaint!");
                    etComplaint.getText().clear();
                } else {
                    tryToSubmitComplaint(complaintText);
                }
            }
        });
    }

    /**
     * Sends a POST request to the server to create a new complaint.
     *
     * @param complaintText The details of the complaint.
     */
    private void tryToSubmitComplaint(String complaintText) {
        complaintURL = SERVER_URL + EP_CREATE_COMPLAINT;
        final HashMap<String, String> complaintHeader = new HashMap<>();
        final HashMap<String, String> complaintData = new HashMap<>();
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        complaintHeader.put("Authorization", "Token " + token);
        complaintData.put("description", complaintText);
        complaintData.put("category_id", String.valueOf(1));
        complaintData.put("citizen_id", dbHelper.readValueOfKee("id"));
        complaintData.put("status", "Submitted");
        JSONObject complaintJSON = new JSONObject(complaintData);
        RequestQueue requestQueue = VRQSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                complaintURL,
                complaintJSON,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        etComplaint.getText().clear();
                        Log.d(TAG, response.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        showLongToast(getApplicationContext(), error.toString());
                        Log.e(TAG, error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return complaintHeader;
            }
        };
        VRQSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    /**
     * Sends a GET request to the server to fetch profile information and then stores them.
     */
    private void fetchNStore() {
        String profileURL = SERVER_URL + EP_USER;
        final HashMap<String, String> profileHeader = new HashMap<>();
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        String token = dbHelper.readValueOfKee("Token");
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
                            dbHelper.insertValueOfKee("id", userId);
                            dbHelper.insertValueOfKee("email", userEmail);
                            dbHelper.insertValueOfKee("name", userName);
                            dbHelper.insertValueOfKee("address", userAddress);
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
