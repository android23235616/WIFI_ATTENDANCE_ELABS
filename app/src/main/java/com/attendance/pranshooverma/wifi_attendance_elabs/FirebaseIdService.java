package com.attendance.pranshooverma.wifi_attendance_elabs;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseIdService extends FirebaseInstanceIdService{


    @Override

    public void onTokenRefresh() {
        // TODO: Return the communication channel to the service.

    }
}
