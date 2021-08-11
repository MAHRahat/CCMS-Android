package com.mahrahat.ccmsandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

import static com.mahrahat.ccmsandroid.CCMSConstants.EP_LOGOUT;
import static com.mahrahat.ccmsandroid.CCMSConstants.SERVER_URL;


/**
 * Helps the user to log out and exit.
 */

public class Logout extends AppCompatActivity {

    private final String TAG = "CCMS LogOut Activity";

    private String logOutURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        doLogOut();
    }

    /**
     * Calls necessary methods to log out from server and delete token and other local data.
     */
    private void doLogOut() {
        tryToLogOutFromServer();
        DBHelper.getInstance(getApplicationContext()).deleteDataOnLogOut();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finishAffinity();   //API level 16+
        Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
    }

    /**
     * Sends a POST request to server to log out.
     */
    private void tryToLogOutFromServer() {
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        final String token = dbHelper.readValueOfKee("Token");
        if (token == null) {
            return;
        }
        logOutURL = SERVER_URL + EP_LOGOUT;
        final HashMap<String, String> logOutHeader = new HashMap<>();
        logOutHeader.put("Authorization", "Token " + token);
        RequestQueue requestQueue = VRQSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                logOutURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e(TAG, error.toString());
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return logOutHeader;
            }
        };
        VRQSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
