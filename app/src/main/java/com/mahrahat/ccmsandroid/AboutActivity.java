package com.mahrahat.ccmsandroid;

import android.os.Bundle;


/**
 * Activity to show some information about the app.
 */

public class AboutActivity extends UpButtonMenuActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setTitle(R.string.menu_about);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
}
