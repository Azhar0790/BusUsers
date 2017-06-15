package busdriver.com.vipassengers;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import busdriver.com.vipassengers.service.GPSTracker;

public class MapsActivity extends ActionBarActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = "MapsActivity";
    private static final int MY_PERMISSION_LOCATION = 0;
    private GoogleMap mMap;
    GPSTracker gps;
    Button close, btncurrentlocation, btnenablegps;
    Button wk;
    protected GoogleApiClient mGoogleApiClient;
    Marker marker;
    Handler UI_HANDLER = new Handler();
    LocationManager locationManager;
    boolean GpsStatus;
    Timer gpstimer;
    double dLat, dLong, dlat_gps, dLong_gps;
    int btn = 0;
    SharedPreferences pref;
    String id, email_id, cat_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        pref = getSharedPreferences("Pref", Context.MODE_PRIVATE);
        cat_id = pref.getString("cat_id", "");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        MapsActivity.this.getSupportActionBar().show();
        MapsActivity.this.getSupportActionBar().setTitle("VI Passenger Map");
        MapsActivity.this.getSupportActionBar().setHomeButtonEnabled(true);
        //startService(new Intent(this, AllRequestSErvice.class));
        marshmallowGPSPremissionCheck();

        if (!InternetConnection.isInternetOn(MapsActivity.this)) {
            android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(MapsActivity.this);
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

            if (Build.VERSION.SDK_INT != Build.VERSION_CODES.M) {
                buildGoogleApiClient();
            }
            Intent intent = this.getIntent();
            if (intent != null)
                if (intent.getExtras() != null) {
                    if (intent.getExtras().getString("type").equals("1")) {
                        new AdsLoader(MapsActivity.this, 3).LoadSpecificBanner(intent.getExtras().getString("show"));
                    } else {
                        AlertDialog.Builder dg = new AlertDialog.Builder(MapsActivity.this);
                        dg.setTitle("Bus driver info: \n");
                        dg.setMessage(intent.getExtras().getString("show"));
                        dg.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //nothing..
                            }
                        });
                        dg.show();
                    }
                }


            close = (Button) findViewById(R.id.btnclose);
            btncurrentlocation = (Button) findViewById(R.id.btncurrentlocation);
            btnenablegps = (Button) findViewById(R.id.btnenablegps);
            gps = new GPSTracker(MapsActivity.this);
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DatabaseHandlerUser(MapsActivity.this).resetTables();
                    finish();
                    startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                }
            });
            CheckGpsStatus();




            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                System.out.println("........1 :- ");
                btnenablegps.setVisibility(View.VISIBLE);
                btncurrentlocation.setVisibility(View.GONE);
                btnenablegps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            System.out.println("........ :- ");
                            gps.showSettingsAlert();
                            btnenablegps.setVisibility(View.VISIBLE);
                            btncurrentlocation.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Error on gps :- " + e);
                        }

                    }
                });

            } else {
                System.out.println("........2 :- ");
                btnenablegps.setVisibility(View.GONE);
                btnenablegps.setVisibility(View.VISIBLE);

            }
            btnenablegps.setVisibility(View.GONE);
            btnenablegps.setVisibility(View.VISIBLE);
            gpstimer = new Timer();
            gpstimer.schedule(new TimerTask() {
                @Override
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

                                System.out.println("........1 :- ");
                                btnenablegps.setVisibility(View.VISIBLE);
                                btncurrentlocation.setVisibility(View.GONE);
                                btnenablegps.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        try {
                                            System.out.println("........ :- ");
                                            gps.showSettingsAlert();
                                            btnenablegps.setVisibility(View.GONE);
                                            btncurrentlocation.setVisibility(View.VISIBLE);

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            System.out.println("Error on gps :- " + e);
                                        }

                                    }
                                });

                            } else {
                                System.out.println("........2 :- ");
                                btnenablegps.setVisibility(View.GONE);
                            }
                        }
                    });

                }
            }, 3300, 3300);


        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.close:
                new DatabaseHandlerUser(MapsActivity.this).resetTables();
                finish();
                startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                break;

            case R.id.trialperiod:
                trial_dialog();
                break;
            case R.id.message:
                startActivity(new Intent(MapsActivity.this, MessageActivity.class));
                break;
            case R.id.cust_feedback:

                startActivity(new Intent(MapsActivity.this, CustomerActivity.class));
                break;
            case R.id.admin_message:
                startActivity(new Intent(MapsActivity.this, AdminMessageActivity.class));
                break;
            case R.id.share:
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = "https://play.google.com/store/apps/details?id=busdriver.com.vipassengers&hl=en";
                String shareSub = "https://play.google.com/store/apps/details?id=busdriver.com.vipassengers&hl=en";
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, shareSub);
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share using"));
                break;
        }
        return true;
    }

    private void marshmallowGPSPremissionCheck() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && MapsActivity.this.checkSelfPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && MapsActivity.this.checkSelfPermission(
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_LOCATION);
        } else {
            //   gps functions..
            buildGoogleApiClient();
            UI_UPDTAE_RUNNABLE.run();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSION_LOCATION
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            //  gps functionality
            buildGoogleApiClient();


            UI_UPDTAE_RUNNABLE.run();

        }
    }


    Runnable UI_UPDTAE_RUNNABLE = new Runnable() {
        @Override
        public void run() {
            if (mMap != null) {
                RAmarkers();
            }
            UI_HANDLER.postDelayed(UI_UPDTAE_RUNNABLE, 7000);
        }
    };

    public void RAmarkers() {
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            dLat = mLastLocation.getLatitude();
            dLong = mLastLocation.getLongitude();
            if (dLat != 0.0 && dLong != 0.0) {
                new getMarkersAsync(MapsActivity.this, mMap, new LatLng(dLat, dLong)).getMarkersAsyncMethod();
                new AllBusDriverAsynctask().execute();

            }
        }
        btncurrentlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btncurrentlocation.setVisibility(View.GONE);
                btnenablegps.setVisibility(View.GONE);
                LatLng sydney = new LatLng(dLat, dLong);
                System.out.println("dLat :- " + dlat_gps + "dLong :- " + dLong_gps);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));

            }
        });


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new MyInfoWindowAdapter());
        mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            return;
        }
        mMap.setMyLocationEnabled(true);
        LatLng sydney = new LatLng(18.4229441, -64.6836995);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));

    }


    public void CheckGpsStatus() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        GpsStatus = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected())
            mGoogleApiClient.connect();

    }


    @Override
    public void onConnected(Bundle connectionHint) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            dLat = mLastLocation.getLatitude();
            dLong = mLastLocation.getLongitude();
            //Does this log?
            new AllBusDriverAsynctask().execute();

            Log.d(getClass().getSimpleName(), String.valueOf(dLat) + ", " + String.valueOf(dLong));

        } else {
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        dLat = location.getLatitude();
        dLong = location.getLongitude();
        mMap.addMarker(new MarkerOptions().position(
                new LatLng(dLat, dLong)).title("¡You are here!"));


    }

    class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

        private final View myContentsView;

        MyInfoWindowAdapter() {
            myContentsView = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
        }

        @Override
        public View getInfoContents(Marker marker) {

            TextView tvTitle = ((TextView) myContentsView.findViewById(R.id.title));
            tvTitle.setText(marker.getTitle());

            TextView tvSnippet = ((TextView) myContentsView.findViewById(R.id.snippet));
            tvSnippet.setText(marker.getSnippet());
            return myContentsView;
        }

        @Override
        public View getInfoWindow(Marker marker) {
            // TODO Auto-generated method stub
            return null;
        }

    }

    class AllBusDriverAsynctask extends AsyncTask<String, String, JSONArray> {
        String url = "http://webview.bvibus.com/admin/getDrivers.php";
        //        String url = "http://webview.bvibus.com/admin/getDriverdataBymail.php";
//        String e_id = pref.getString("email_id", "");
        Float latt, lngg;
        String email, lat, lng, name, nombre, ws, route, schedule;
        JSONArray jsonArray = null;

        @Override
        protected JSONArray doInBackground(String... params) {
            ServiceHandler sh = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("cat_id", cat_id));
            String jsnStr = sh.makeServiceCall(url, ServiceHandler.POST, param);

            if (jsnStr != null) {
                try {
                    jsonArray = new JSONArray(jsnStr);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonArray;
        }

        @Override
        protected void onPostExecute(JSONArray jsonArray) {
            super.onPostExecute(jsonArray);

            try {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    email = jsonObject.getString("email");
                    lat = jsonObject.getString("lat");
                    lng = jsonObject.getString("lng");
                    name = jsonObject.getString("name");
                    nombre = jsonObject.getString("nombre");
                    ws = jsonObject.getString("ws");
                    route = jsonObject.getString("route");
                    schedule = jsonObject.getString("horario");
                    latt = Float.parseFloat(lat);
                    lngg = Float.parseFloat(lng);
                    LatLng sydney = new LatLng(latt, lngg);

                    if (name.equals("Bus")) {
                        drawMarker(sydney, "Name: " + nombre, "Phone No: " + ws + "\n" + "Route: " + route, R.drawable.bus);

                    } else if (name.equals("Blue Team")) {
                        drawMarker(sydney, "Name: " + nombre, "Phone No: " + ws + "\n" + "Route: " + route, R.drawable.blue_team);

                    } else if ((name.equals("Green Team"))) {
                        drawMarker(sydney, "Name: " + nombre, "Phone No: " + ws + "\n" + "Route: " + route, R.drawable.green_team);

                    } else if ((name.equals("Taxi"))) {

                        drawMarker(sydney, "Name: " + nombre, "Phone No: " + ws + "\n" + "Route: " + route, R.drawable.taxi);

                    } else if ((name.equals("null"))) {
                        drawMarker(sydney, "Name: " + nombre, "Phone No: " + ws + "\n" + "Route: " + route, R.drawable.bus);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    private void drawMarker(LatLng point, String text, String snippet, int drawable) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point).title(text).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(drawable));
        mMap.addMarker(markerOptions);
    }

    class ActivationPeriod extends AsyncTask<String, String, String> {
        String jsnStr = null, message = null;

        @Override
        protected String doInBackground(String... params) {
            email_id = pref.getString("email_id", "");
            String url = "http://webview.bvibus.com/admin/trialPeriodPassenger.php";
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("mailid", email_id));
            jsnStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET, param);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (jsnStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsnStr);
                    message = jsonObject.getString("message");
                    if (message.equals("Expired")) {
                        new DatabaseHandlerUser(MapsActivity.this).resetTables();
                        finish();
                        startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                    } else if (message.equals("0 Months,  2 Days  Remain")) {
                        Toast.makeText(getApplicationContext(), "2 days are remaining for expiration. Please activate your account", Toast.LENGTH_LONG).show();
                    } else if (message.equals("0 Months,  1 Days  Remain")) {
                        Toast.makeText(getApplicationContext(), "1 day is remaining for expiration. Please activate your account", Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class TrialPeriod extends AsyncTask<String, String, String> {
        String jsnStr = null, message = null;

        @Override
        protected String doInBackground(String... params) {
            email_id = pref.getString("email_id", "");
            String url = "http://webview.bvibus.com/admin/trialPeriodPassenger.php";
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("mailid", email_id));
            jsnStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET, param);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            android.app.AlertDialog.Builder alertbox;
            if (jsnStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsnStr);
                    message = jsonObject.getString("message");
                    alertbox = new android.app.AlertDialog.Builder(MapsActivity.this);
                    alertbox.setTitle("Trial Remaining");
                    alertbox.setMessage(message);
                    alertbox.setPositiveButton(
                            getResources().getString(R.string.ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface arg0, int arg1) {
                                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                }
                            });
                    alertbox.show();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void trial_dialog() {
        final Dialog dialog = new Dialog(MapsActivity.this);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_trial);
        Button btn_cancel_sub = (Button) dialog.findViewById(R.id.btn_cancel_sub);
        btn_cancel_sub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new CancelSubscriptionAsynctask().execute();
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    class CancelSubscriptionAsynctask extends AsyncTask<String, String, String> {
        String jsnStr = null, message = null;

        @Override
        protected String doInBackground(String... params) {
            email_id = pref.getString("email_id", "");
            String url = "http://webview.bvibus.com/stripe/cancelSubscription.php";
            ServiceHandler serviceHandler = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("user_mail", email_id));
            jsnStr = serviceHandler.makeServiceCall(url, ServiceHandler.GET, param);

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (jsnStr != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsnStr);
                    message = jsonObject.getString("message");
                    if (message.equals("success")) {
                        Toast.makeText(getApplicationContext(), "Subscription cancel successfully", Toast.LENGTH_SHORT).show();
                        new DatabaseHandlerUser(MapsActivity.this).resetTables();
                        finish();
                        startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    class gpsAsynctask extends AsyncTask<String, String, String> {
        String jsnStr = null, message = null;

        @Override
        protected String doInBackground(String... params) {


            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new gpsAsynctask().execute();
                }
            },3300);
            if (gps.canGetLocation()) {
                System.out.println("........1 :- ");

                btncurrentlocation.setVisibility(View.GONE);
                btncurrentlocation.setVisibility(View.VISIBLE);
                if (btncurrentlocation.getText().toString().equals("Click here to see your location"))
                    btncurrentlocation.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            btncurrentlocation.setVisibility(View.GONE);
                            btnenablegps.setVisibility(View.GONE);
                            LatLng sydney = new LatLng(dLat, dLong);
                            System.out.println("dLat :- " + dLat + "dLong :- " + dLong);
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12));

                        }
                    });
            } else {
                System.out.println("........2 :- ");
                btnenablegps.setVisibility(View.VISIBLE);
                btncurrentlocation.setVisibility(View.GONE);
                btnenablegps.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            System.out.println("........ :- ");
                            gps.showSettingsAlert();
                            btnenablegps.setVisibility(View.GONE);
                            btncurrentlocation.setVisibility(View.VISIBLE);

                        } catch (Exception e) {
                            e.printStackTrace();
                            System.out.println("Error on gps :- " + e);
                        }

                    }
                });

            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//***Change Here***
        startActivity(intent);
        finish();
        System.exit(0);
    }
}
