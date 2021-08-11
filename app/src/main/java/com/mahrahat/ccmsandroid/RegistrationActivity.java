package com.mahrahat.ccmsandroid;

import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
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

import static com.mahrahat.ccmsandroid.CCMSConstants.EP_REGISTER;
import static com.mahrahat.ccmsandroid.CCMSConstants.MAX_PASSWORD_LENGTH;
import static com.mahrahat.ccmsandroid.CCMSConstants.MIN_PASSWORD_LENGTH;
import static com.mahrahat.ccmsandroid.CCMSConstants.SERVER_URL;


/**
 * Allows a user to register a new account.
 */

public class RegistrationActivity extends UpButtonMenuActivity {

    private final String TAG = "CCMS Registration";

    private Button btnRegister;
    private EditText etRegEmail, etRegPass1, etRegPass2;
    private String registerURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        initUI();
        setUpRegisterButton();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    private void initUI() {
        btnRegister = findViewById(R.id.btn_register);
        etRegEmail = findViewById(R.id.et_reg_email);
        etRegPass1 = findViewById(R.id.et_reg_pass1);
        etRegPass2 = findViewById(R.id.et_reg_pass2);
    }

    /**
     * Set the 'OnClickListener' for the register button
     */
    private void setUpRegisterButton() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regEmail;
                String regPass1, regPass2;
                regEmail = etRegEmail.getText().toString().trim().toLowerCase();
                regPass1 = etRegPass1.getText().toString();
                regPass2 = etRegPass2.getText().toString();
                if (validate(regEmail, regPass1, regPass2)) {
                    tryToRegister(regEmail, regPass1);
                }
            }
        });
    }

    /**
     * Validates email and password against some criteria.
     *
     * @param regEmail email provided by the user
     * @param regPass1 string entered in 'password' field
     * @param regPass2 string entered in 'confirm password' field
     * @return true, if the parameters pass the checks, otherwise false
     */
    private boolean validate(String regEmail, String regPass1, String regPass2) {
        boolean flag = true;
        if (!Patterns.EMAIL_ADDRESS.matcher(regEmail).matches()) {
            etRegEmail.setError("Wrong Email format!");
            flag = false;
        }
        if (regEmail.equals("")) {
            etRegEmail.setError("Email can not be empty!");
            flag = false;
        }
        if (!regPass1.matches(".*\\d.*")) {
            etRegPass1.setError("At least one digit required!");
            flag = false;
        }
        if (!regPass1.matches(".*[a-z].*")) {
            etRegPass1.setError("At least one lowercase letter required");
            flag = false;
        }
        if (!regPass1.matches(".*[A-Z].*")) {
            etRegPass1.setError("At least one uppercase letter required");
            flag = false;
        }
        if (regPass1.length() > MAX_PASSWORD_LENGTH) {
            etRegPass1.setError("Password must be at most " + MAX_PASSWORD_LENGTH + " characters long!");
            flag = false;
        }
        if (regPass1.length() < MIN_PASSWORD_LENGTH) {
            etRegPass1.setError("Password must be at least " + MIN_PASSWORD_LENGTH + " characters long!");
            flag = false;
        }
        if (regPass1.equals("")) {
            etRegPass1.setError("Password can not be empty!");
            flag = false;
        }
        if (regPass2.equals("")) {
            etRegPass2.setError("Password can not be empty!");
            flag = false;
        }
        if (!regPass1.equals("") && !regPass1.equals(regPass2)) {
            etRegPass2.setError("Passwords are different!");
            flag = false;
        }
        return flag;
    }

    /**
     * Sends email and password to server via a POST request to create a new account.
     *
     * @param regEmail email provided by the user
     * @param regPass  string entered in 'password' field
     */
    private void tryToRegister(String regEmail, String regPass) {
        registerURL = SERVER_URL + EP_REGISTER;
        final HashMap<String, String> registerData = new HashMap<>();
        registerData.put("email", regEmail.toLowerCase());
        registerData.put("password1", regPass);
        registerData.put("password2", regPass);
        registerData.put("is_staff", "false");
        RequestQueue requestQueue = VRQSingleton.getInstance(this.getApplicationContext()).getRequestQueue();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                registerURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonResponse = new JSONObject(response);
                            if (jsonResponse.has("key")) {
                                HelperMethods.showLongToast(getApplicationContext(), "Registration Successful!");
                            } else {
                                HelperMethods.showLongToast(getApplicationContext(), "Registration Failed!");
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
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                return registerData;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return super.getHeaders();
            }
        };
        VRQSingleton.getInstance(this).addToRequestQueue(stringRequest);
    }
}
