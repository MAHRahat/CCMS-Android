package com.mahrahat.ccmsandroid;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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

import static com.mahrahat.ccmsandroid.CCMSConstants.EP_PASSWORD_CHANGE;
import static com.mahrahat.ccmsandroid.CCMSConstants.SERVER_URL;
import static com.mahrahat.ccmsandroid.HelperMethods.showLongToast;


/**
 * Display Settings screen
 */

public class SettingsActivity extends UpButtonMenuActivity {

    private final String TAG = "CCMS Settings";

    private String token;

    private String cpURL;
    private EditText etPassOld, etPassNew1, etPassNew2;
    private Button btnUpdatePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getSupportActionBar().setTitle(R.string.menu_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        token = DBHelper.getInstance(getApplicationContext()).readValueOfKee("Token");
        if (token == null) {
            DBHelper.getInstance(getApplicationContext()).deleteDataOnLogOut();
            finishAffinity();
            return;
        }
        initUI();
        setUpUpdateButton();
    }

    private void initUI() {
        etPassOld = findViewById(R.id.et_cp_old);
        etPassNew1 = findViewById(R.id.et_cp_new1);
        etPassNew2 = findViewById(R.id.et_cp_new2);
        btnUpdatePass = findViewById(R.id.btn_update_pass);
    }

    private void setUpUpdateButton() {
        btnUpdatePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPass = etPassOld.getText().toString();
                String newPass1 = etPassNew1.getText().toString();
                String newPass2 = etPassNew2.getText().toString();
                //Todo validate passwords
                tryToUpdatePassword(oldPass, newPass1, newPass2);
            }
        });
    }

    /**
     * Sends a POST request to change password.
     *
     * @param oldPass  Old password
     * @param newPass1 New password
     * @param newPass2 New password entered again
     */
    private void tryToUpdatePassword(String oldPass, String newPass1, String newPass2) {
        cpURL = SERVER_URL + EP_PASSWORD_CHANGE;
        final HashMap<String, String> cpHeader = new HashMap<>();
        final HashMap<String, String> cpData = new HashMap<>();
        cpHeader.put("Authorization", "Token " + token);
        cpData.put("old_password", oldPass);
        cpData.put("new_password1", newPass1);
        cpData.put("new_password2", newPass2);
        RequestQueue requestQueue = VRQSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                cpURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        etPassOld.getText().clear();
                        etPassNew1.getText().clear();
                        etPassNew2.getText().clear();
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("detail")) {
                                showLongToast(getApplicationContext(), jsonResponse.getString("detail"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, response);
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
                return cpHeader;
            }

            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return cpData;
            }
        };
        VRQSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
