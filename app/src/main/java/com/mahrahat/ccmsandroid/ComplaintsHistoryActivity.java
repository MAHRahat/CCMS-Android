package com.mahrahat.ccmsandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mahrahat.ccmsandroid.CCMSConstants.EP_LIST_OF_COMPLAINTS;
import static com.mahrahat.ccmsandroid.CCMSConstants.SERVER_URL;


/**
 * Activity to fetch, save and display all complaints submitted by the user.
 */

public class ComplaintsHistoryActivity extends OptionsMenuActivity {

    private final String TAG = "CCMS Complaints History";

    private String token;

    private List<Complaint> complaintList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints_history);
        getSupportActionBar().setTitle(R.string.menu_complaints_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        token = DBHelper.getInstance(getApplicationContext()).readValueOfKee("Token");
        if (token == null) {
            finishAffinity();
            return;
        }
        complaintList = new ArrayList<>();
        fetchAndSaveComplaints();
        displayComplaints();
    }

    /**
     * Sends a get request for the complaints submitted by the user and stores them in local database.
     */
    private void fetchAndSaveComplaints() {
        DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
        String historyUrl = SERVER_URL + EP_LIST_OF_COMPLAINTS + dbHelper.readValueOfKee("id");
        final HashMap<String, String> historyHeader = new HashMap<>();
        historyHeader.put("Authorization", "Token " + token);
        RequestQueue requestQueue = VRQSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                historyUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonResponseArray = new JSONArray(response);
                            dbHelper.deleteAllComplaints();
                            for (int i = 0; i < jsonResponseArray.length(); i++) {
                                JSONObject jsonObject = jsonResponseArray.getJSONObject(i);
                                Complaint complaint = new Complaint();
                                complaint.setComplaintId(String.valueOf(jsonObject.getInt("complaints_id")));
                                complaint.setDescription(jsonObject.getString("description"));
                                complaint.setTimeUpdated(jsonObject.getString("time_last_updated"));
                                complaint.setTimeSubmitted(jsonObject.getString("time_submitted"));
                                complaint.setStatus(jsonObject.getString("status"));
                                dbHelper.insertComplaint(complaint);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
                return historyHeader;
            }
        };
        VRQSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    /**
     * Reads complaints stored in local database and displays them.
     */
    private void displayComplaints() {
        complaintList = DBHelper.getInstance(getApplicationContext()).readAllComplaints();
        if (complaintList.isEmpty()) {  //Todo: set a TextView with no history message
            Toast.makeText(this, "No history", Toast.LENGTH_SHORT).show();
            return;
        }
        ((ListView) findViewById(R.id.lvComplaints)).setAdapter(new CHCustomAdapter(getApplicationContext(), complaintList));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.findItem(R.id.menu_item_history).setVisible(false);
        return true;
    }
}
