package busdriver.com.vipassengers.fcm;


import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by Belal on 5/27/2016.
 */


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public static final String REGISTRATION_SUCCESS = "RegistrationSuccess";
SharedPreferences pref;

    @Override
    public void onTokenRefresh() {
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        pref=getSharedPreferences("Pref",MODE_PRIVATE);
        SharedPreferences.Editor editor=pref.edit();
        editor.putString("refreshedToken",refreshedToken);
        editor.commit();
    }

    private void sendRegistrationToServer(String token) {

    }
}