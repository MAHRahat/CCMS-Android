package com.mahrahat.ccmsandroid;


import android.content.Context;
import android.widget.Toast;


/**
 * Some common methods to help do various tasks
 */

public abstract class HelperMethods {

    /**
     * Shows a toast for LENGTH_LONG duration
     *
     * @param str The string to be displayed
     */
    public static void showLongToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    /**
     * Shows a toast for LENGTH_SHORT duration
     *
     * @param str The string to be displayed
     */
    public static void showShortToast(Context context, String str) {
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
