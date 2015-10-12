package com.mycarlocation;

import com.firebase.client.Firebase;


/**
 * Created by rafaxplayer on 03/10/2015.
 */
public class mycarlocation extends android.app.Application{

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);

    }
}
