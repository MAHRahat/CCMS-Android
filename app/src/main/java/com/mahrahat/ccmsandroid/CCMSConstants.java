package com.mahrahat.ccmsandroid;


/**
 * Defines some constants like API endpoints
 */

interface CCMSConstants {
    //Password lengths
    int MIN_PASSWORD_LENGTH = 8;
    int MAX_PASSWORD_LENGTH = 20;
    //Server URL (with port)
    String SERVER_URL = "http://10.0.2.2:8080"; //FixMe: need to change in production or when run from a real device
    //REST API Endpoints
    String EP_REGISTER = "/rest-auth/registration/";
    String EP_LOGIN = "/rest-auth/login/";
    String EP_LOGOUT = "/rest-auth/logout/";
    String EP_USER = "/rest-auth/user/";
    String EP_PASSWORD_CHANGE = "/rest-auth/password/change/";
    String EP_UPDATE_PROFILE = "/users/";  //Need to append id
    String EP_CREATE_COMPLAINT = "/complaints";
    String EP_LIST_OF_COMPLAINTS = "/complaints/user/"; //Need to append id
}
