package com.gtaandteam.android.wellcure;

/**
 * Created by Glenn Alex on 24-Aug-18.
 */
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

//the class extending FirebaseInstanceIdService
public class MyFirebaseInstanceIdService extends FirebaseInstanceIdService {
    static String FCMtoken;
    final String LOG_TAG = this.getClass().getSimpleName();
    //this method will be called
    //when the token is generated
    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        //now we will have the token
        String token = FirebaseInstanceId.getInstance().getToken();
        FCMtoken=token;
        Log.d(LOG_TAG,"Entered FCM");
        //for now we are displaying the token in the log
        //copy it as this method is called only when the new token is generated
        //and usually new token is only generated when the app is reinstalled or the data is cleared
        Log.d("MyRefreshedToken : ", token);
        Log.d("New Token : ", token);
    }
}

