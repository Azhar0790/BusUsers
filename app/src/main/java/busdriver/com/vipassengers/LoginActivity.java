package busdriver.com.vipassengers;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.inthecheesefactory.lib.fblike.widget.FBLikeView;

import java.util.Timer;
import java.util.TimerTask;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    Button ing, reg;
    DatabaseHandlerUser databaseHandlerUser;
    // share button
    private Button shareButton, fb,btn_reset;
    WebView webView;
    DevicePolicyManager mDevicePolicyManager;
    SharedPreferences pref;
    String token;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private double currentLatitude;
    private double currentLongitude;
    private GoogleApiClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        //OneSignal.startInit(this).init();
        // ACRA.init(LoginActivity.this.getApplication());
        setContentView(R.layout.activity_login);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                // The next two lines tell the new client that “this” current class will handle connection stuff
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                //fourth line adds the LocationServices API endpoint from GooglePlayServices
                .addApi(LocationServices.API)
                .build();
        if (!InternetConnection.isInternetOn(LoginActivity.this)) {

            android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(LoginActivity.this);
            alertbox.setTitle(getResources().getString(R.string.app_name));
            alertbox.setMessage(getResources().getString(R.string.internet));
            alertbox.setPositiveButton(
                    getResources().getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            Intent a = new Intent(Intent.ACTION_MAIN);
                            a.addCategory(Intent.CATEGORY_HOME);
                            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(a);
                        }
                    });
            alertbox.show();
        } else {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    // The next two lines tell the new client that “this” current class will handle connection stuff
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    //fourth line adds the LocationServices API endpoint from GooglePlayServices
                    .addApi(LocationServices.API)
                    .build();

            // Create the LocationRequest object
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds
            pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);
            ing = (Button) findViewById(R.id.btnIngresar);
            reg = (Button) findViewById(R.id.btnRegistro);
            btn_reset = (Button) findViewById(R.id.btn_reset);
            final EditText Username = (EditText) findViewById(R.id.editText);
            final EditText Password = (EditText) findViewById(R.id.editText2);
            Intent intent = this.getIntent();
            if (intent != null) {
                if (intent.getExtras() != null) {
                    if (intent.getExtras().getString("s").equals("s")) {
                    } else {
                        new AdsLoader(LoginActivity.this, 5).LoadAd();
                    }
                } else {
                    new AdsLoader(LoginActivity.this, 5).LoadAd();
                }
            } else {
                new AdsLoader(LoginActivity.this, 5).LoadAd();
            }
            token=pref.getString("refreshedToken","");
            System.out.println("token :- "+token);
//            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
//
//                //When the broadcast received
//                //We are sending the broadcast from GCMRegistrationIntentService
//
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    //If the broadcast has received with success
//                    //that means device is registered successfully
//                    if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_SUCCESS)){
//                        //Getting the registration token from the intent
//                        token = intent.getStringExtra("token");
//                        System.out.println("token :- "+token);
//                        //Displaying the token as toast
////					Toast.makeText(getApplicationContext(), "Registration token:" + token, Toast.LENGTH_LONG).show();
////Log.v("Token ","Token:" +token);
//                        //if the intent is not with success then displaying error messages
//                    } else if(intent.getAction().equals(GCMRegistrationIntentService.REGISTRATION_ERROR)){
//                        Toast.makeText(getApplicationContext(), "GCM registration error!", Toast.LENGTH_LONG).show();
//                    } else {
//                        Toast.makeText(getApplicationContext(), "Error occurred", Toast.LENGTH_LONG).show();
//                    }
//                }
//            };

            databaseHandlerUser = new DatabaseHandlerUser(LoginActivity.this);
            if (databaseHandlerUser.EstaLogueado()) {
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
                    }
                }, 0);
            }
            ing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    String username = Username.getText().toString();
                    String password = Password.getText().toString();
                    if (username.length() > 0 && password.length() > 0) {
                        Log.i("DATOS", "" + username + " / " + password);
                        User user = new User(username.replaceAll("\\+", "55j"), password,token);
                        String androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
                        System.out.println("androidId :- " + androidId);
                        SharedPreferences.Editor editor = pref.edit();
                        editor.putString("email_id", username);
                        editor.putString("androidId", androidId);
                        editor.commit();

                        authenticate(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Please complete the fields..", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            reg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i=new Intent(LoginActivity.this, RegisterActivity.class);
                            i.putExtra("currentLatitude",""+currentLatitude);
                            i.putExtra("currentLongitude",""+currentLongitude);
                    startActivity(i);
                }
            });


            //share button
            shareButton = (Button) findViewById(R.id.share_btn);
            shareButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");
                    String shareBody = "https://play.google.com/store/apps/details?id=busdriver.com.vipassengers&hl=en";
                    String shareSub = "https://play.google.com/store/apps/details?id=busdriver.com.vipassengers&hl=en";
                    sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                    sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share using"));

                }
            });
            fb = (Button) findViewById(R.id.btnLike);
            fb.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
//                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/Vi-Passenger-1711920192468944/"));
                    Intent browserIntent = new Intent(getApplicationContext(), FacebookPageActivity.class);
                    startActivity(browserIntent);


                }
            });
        /*FBLikeView fbLikeView = (FBLikeView) findViewById(R.id.fbLikeView);
        LikeView likeView = fbLikeView.getLikeView();
        fbLikeView.getLikeView().setObjectIdAndType("https://www.facebook.com/BBBT284/", LikeView.ObjectType.OPEN_GRAPH);*/
            final ImageView image = (ImageView) findViewById(R.id.adBanner);
            Timer tt = new Timer();
            tt.schedule(new TimerTask() {
                @Override
                public void run() {
                    new AdsLoader(LoginActivity.this, 5).LoadBanner(image);
                }
            }, 7000);
            client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        }
        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    private void authenticate(User user) {
        ServerRequests serverRequest = new ServerRequests(LoginActivity.this);
        serverRequest.fetchUserDataAsyncTask(user, new GetUserCallback() {
            @Override
            public void done(User returnedUser) {
                if (returnedUser == null) {
                    showErrorMessage();
                    Log.i("ERROR", String.valueOf(returnedUser));
                } else {
                    logUserIn(returnedUser);
//                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
//                    finish();
                }
            }
        });
        ImageView adBanner = (ImageView) findViewById(R.id.adBanner);
        new AdsLoader(LoginActivity.this, 0).LoadBanner(adBanner);
    }

    private void showErrorMessage() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(LoginActivity.this);
        dialogBuilder.setMessage("Username and password do not match. Please try again");
        dialogBuilder.setPositiveButton("Ok", null);
        dialogBuilder.show();
    }

    private void logUserIn(User returnedUser) {
        databaseHandlerUser.addUser(returnedUser);
        startActivity(new Intent(LoginActivity.this, MapsActivity.class));
        this.finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FBLikeView.onActivityResult(requestCode, resultCode, data);
    }
    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }


    }
    @Override
    public void onConnected(Bundle bundle) {
        Location location = null;
        try {
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        if (location == null) {
            try {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            } catch (SecurityException e) {
                e.printStackTrace();
            }

        } else {
            //If everything went fine lets get latitude and longitude
            currentLatitude = location.getLatitude();
            currentLongitude = location.getLongitude();

        }
    }


    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
            /*
             * Google Play services can resolve some errors it detects.
             * If the error has a resolution, try sending an Intent to
             * start a Google Play services activity that can resolve
             * error.
             */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                    /*
                     * Thrown if Google Play services canceled the original
                     * PendingIntent
                     */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
                /*
                 * If no resolution is available, display a dialog to the
                 * user with the error.
                 */
            Log.e("Error", "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    /**
     * If locationChanges change lat and long
     *
     *
     * @param location
     */
    @Override
    public void onLocationChanged(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();

    }

}
