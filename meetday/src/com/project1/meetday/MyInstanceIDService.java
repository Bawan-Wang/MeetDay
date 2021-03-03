package com.project1.meetday;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.project1.http.Const;
import com.project1.http.FileAccess;

/**
 * Created by 517 on 2017/1/4.
 */
public class MyInstanceIDService extends FirebaseInstanceIdService {
    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        FileAccess.putStringInPreferences(getBaseContext(), token,
                Const.eUsrType.UsrType_RegId, Const.projinfo.sSharePreferenceName);
        Log.d("FCM", "Token:" + token);
    }
}