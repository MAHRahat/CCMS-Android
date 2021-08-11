package com.mahrahat.ccmsandroid;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.mahrahat.ccmsandroid.CCMSConstants.EP_LOGIN;
import static com.mahrahat.ccmsandroid.CCMSConstants.MAX_PASSWORD_LENGTH;
import static com.mahrahat.ccmsandroid.CCMSConstants.MIN_PASSWORD_LENGTH;
import static com.mahrahat.ccmsandroid.CCMSConstants.SERVER_URL;
import static com.mahrahat.ccmsandroid.HelperMethods.showLongToast;


/**
 * Allows a registered user to log in.
 */

public class LoginActivity extends UpButtonMenuActivity {

    private final String TAG = "CCMS Login";

    private Button btnLogin;
    private EditText etLogEmail, etLogPass;
    private String loginURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initUI();
        setUpLoginButton();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        btnLogin = findViewById(R.id.btn_login);
        etLogEmail = findViewById(R.id.et_sign_email);
        etLogPass = findViewById(R.id.et_sign_pass);
    }

    /**
     * Sets the 'OnClickListener' for the 'Log In' button
     */
    private void setUpLoginButton() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String logEmail;
                String logPass;
                logEmail = etLogEmail.getText().toString().trim().toLowerCase();
                logPass = etLogPass.getText().toString();
                if (validate(logEmail, logPass)) {
                    tryToLogin(logEmail, logPass);
                }
            }
        });
    }

    /**
     * Validates email and password against some criteria.
     *
     * @param logEmail email provided by the user
     * @param logPass  string entered in 'password' field
     * @return true, if the parameters pass the checks, false, otherwise
     */
    private boolean validate(String logEmail, String logPass) {
        boolean flag = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(logEmail).matches()) {
            etLogEmail.setError("Wrong Email format!");
            flag = false;
        }
        if (logEmail.equals("")) {
            etLogEmail.setError("Email can not be empty!");
            flag = false;
        }
        if (!logPass.matches(".*\\d.*")) {
            etLogPass.setError("At least one digit required!");
            flag = false;
        }
        if (!logPass.matches(".*[a-z].*")) {
            etLogPass.setError("At least one lowercase letter required");
            flag = false;
        }
        if (!logPass.matches(".*[A-Z].*")) {
            etLogPass.setError("At least one uppercase letter required");
            flag = false;
        }
        if (logPass.length() > MAX_PASSWORD_LENGTH) {
            etLogPass.setError("Password must be at most " + MAX_PASSWORD_LENGTH + " characters long!");
            flag = false;
        }
        if (logPass.length() < MIN_PASSWORD_LENGTH) {
            etLogPass.setError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long!");
            flag = false;
        }
        if (logPass.equals("")) {
            etLogPass.setError("Password can not be empty!");
            flag = false;
        }
        return flag;
    }

    /**
     * Sends email and password to server via a POST request to log in.
     *
     * @param logEmail email provided by the user
     * @param logPass  string entered in 'password' field
     */
    private void tryToLogin(String logEmail, String logPass) {
        loginURL = SERVER_URL + EP_LOGIN;
        final HashMap<String, String> loginData = new HashMap<>();
        loginData.put("email", logEmail.toLowerCase());
        loginData.put("password", logPass);
        RequestQueue requestQueue = VRQSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                loginURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("key")) {
                                String token = jsonResponse.getString("key");
                                DBHelper dbHelper = DBHelper.getInstance(getApplicationContext());
                                dbHelper.insertValueOfKee("Token", token);
                                showLongToast(getApplicationContext(), "Login Successful");
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finishAffinity();
                            } else {
                                showLongToast(getApplicationContext(), "Login Failed");
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
            @NotNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return loginData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
        VRQSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
