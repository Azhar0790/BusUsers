package busdriver.com.vipassengers;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import busdriver.com.vipassengers.service.GPSTracker;

//AIzaSyBwGiV2fpAiuARdiz802sTlG7mfGP99miY
public class RegisterActivity extends AppCompatActivity {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    EditText name, pass, phone;
    private Spinner spinner1, city, spinnerh, spinnera, best, spinnerCat, spinnerprimarybus;
    String resp, resp2, resp3, email, respcat, respprimarybus;
    GPSTracker gps;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
//    private double currentLatitude;
//    private double currentLongitude;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    List<String> cities = new ArrayList<String>();
    List<String> hear = new ArrayList<String>();
    List<String> age = new ArrayList<String>();
    List<String> bestlist = new ArrayList<String>();
    List<String> list = new ArrayList<String>();
    List<String> cat = new ArrayList<String>();
    List<String> p_bus = new ArrayList<String>();
    double latitude, longitude;
    String currentLatitude, currentLongitude;
    TextView tv_date;
    static final int DATE_DIALOG_ID = 0;
    private int mYear, mMonth, mDay;
    String outputDateStr, outputDateStr2;
    SimpleDateFormat sdf;
    List country_code;
    Spinner sp_c_code;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        gps = new GPSTracker(RegisterActivity.this);
        name = (EditText) findViewById(R.id.name);
        pass = (EditText) findViewById(R.id.pass);
        phone = (EditText) findViewById(R.id.phone);
        tv_date = (TextView) findViewById(R.id.tv_date);
        spinnera = (Spinner) findViewById(R.id.spinnera);
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinnerh = (Spinner) findViewById(R.id.spinnerh);
        spinnerCat = (Spinner) findViewById(R.id.spinnerCategory);
        spinnerprimarybus = (Spinner) findViewById(R.id.spinnerprimarybus);
        best = (Spinner) findViewById(R.id.best);
        city = (Spinner) findViewById(R.id.city);
        Bundle bundle = getIntent().getExtras();
        currentLatitude = bundle.getString("currentLatitude");
        currentLongitude = bundle.getString("currentLongitude");


        country_code = new ArrayList<Integer>();
        for (int i = 1; i <= 1000; i++) {
            country_code.add(Integer.toString(i));
        }
        ArrayAdapter<Integer> spinnerArrayAdapter = new ArrayAdapter<Integer>(
                this, android.R.layout.simple_spinner_item, country_code);
        spinnerArrayAdapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item );

        sp_c_code = (Spinner)findViewById(R.id.sp_c_code);
        sp_c_code.setAdapter(spinnerArrayAdapter);
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                // The next two lines tell the new client that “this” current class will handle connection stuff
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                //fourth line adds the LocationServices API endpoint from GooglePlayServices
//                .addApi(LocationServices.API)
//                .build();

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds
        new AsyncMethod().execute();
        new AsyncMethodHear().execute();
        new AsyncMethodAges().execute();
        new AsyncMethodBest().execute();
        new AsyncMethodCat().execute();
        // check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();
            //if(latit==mylat && longit==mylong)

            //imgcaptureimage.setVisibility(View.VISIBLE);
        } else {
            // can't get location
            // GPS or Network is not enabled
            // Ask user to enable GPS/network in settings

            gps.showSettingsAlert();

        }
        list.add("Female");
        list.add("Male");
        hear.add("Loading...");
        cities.add("Loading...");
        age.add("Loading...");
        bestlist.add("Loading...");
        cat.add("Loading...");
        p_bus.add("Loading...");
        Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        //String dateFormat = "dd/MM/yyyy";
        sdf = new SimpleDateFormat("MM/dd/yyyy");
        String d = sdf.format(c.getTime());
        DateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date = null, date2 = null;
        try {
            date = sdf.parse(d);
            date2 = sdf.parse(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        outputDateStr = outputFormat.format(date);
        outputDateStr2 = sdf.format(date2);
        System.out.println("outputDateStr :- " + outputDateStr);
        System.out.println("outputDateStr2 :-" + outputDateStr2);
        tv_date.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                showDialog(DATE_DIALOG_ID);

            }
        });
        GET_EMAIL_ADDRESSES();
        // Set Adapters to Spinners
        addListenerOnSpinnerItemSelection();
        ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, age);

        ageAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> bestAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, bestlist);

        bestAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_spinner_item, list);

        dataAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>
                (RegisterActivity.this, android.R.layout.simple_spinner_item, cities);

        cityAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        ArrayAdapter<String> hearAdapter = new ArrayAdapter<String>
                (RegisterActivity.this, android.R.layout.simple_spinner_item, hear);

        hearAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> CatAdapter = new ArrayAdapter<String>
                (RegisterActivity.this, android.R.layout.simple_spinner_item, cat);

        CatAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> p_busAdapter = new ArrayAdapter<String>
                (RegisterActivity.this, android.R.layout.simple_spinner_item, p_bus);

        CatAdapter.setDropDownViewResource
                (android.R.layout.simple_spinner_dropdown_item);

        spinnerprimarybus.setAdapter(p_busAdapter);
        spinnerCat.setAdapter(CatAdapter);
        best.setAdapter(bestAdapter);
        city.setAdapter(cityAdapter);
        spinnera.setAdapter(ageAdapter);
        spinnerh.setAdapter(hearAdapter);
        spinner1.setAdapter(dataAdapter);

        final Button btnRegister = (Button) findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!InternetConnection.isInternetOn(RegisterActivity.this)) {
                    android.app.AlertDialog.Builder alertbox = new android.app.AlertDialog.Builder(RegisterActivity.this);
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
                    if (name.getText().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Name cannot be blank", Toast.LENGTH_LONG).show();
                        name.setError("Name cannot be blank");
                        return;
                    } else if (phone.getText().toString().length() < 6 || phone.getText().toString().length() > 12 ) {
                        Toast.makeText(getApplicationContext(), "Add valid phone number", Toast.LENGTH_LONG).show();
                        phone.setError("Add your phone number");
                        return;
                    } else if (pass.getText().toString().length() < 6) {
                        Toast.makeText(getApplicationContext(), "Password cannot be less than 6 digits", Toast.LENGTH_LONG).show();
                        pass.setError("Password cannot be less than 6 digits");
                        return;
                    }else if (tv_date.getText().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Please enter your birth date", Toast.LENGTH_LONG).show();
                        tv_date.setError("Please enter your birth date");
                        return;
                    }
                    else {
                        AlertDialog.Builder dg = new AlertDialog.Builder(RegisterActivity.this);
                        dg.setMessage("Your username is: " + email);
                        dg.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String lat = String.valueOf(currentLatitude);
                                String lng = String.valueOf(currentLongitude);
                                System.out.println("lat :- " + lat);
                                System.out.println("lng :- " + lng);
                                String c_phone=sp_c_code.getSelectedItem().toString()+phone.getText().toString();
                                System.out.println("c_phone :- "+c_phone);
//                                String[] phone_ = phone.getText().toString().split("\\+");
//
//                                String phone_1 = phone_[1];
                                addUserToServer add = new addUserToServer(RegisterActivity.this, tv_date.getText().toString(), pass.getText().toString(),
                                        spinner1.getSelectedItem().toString(), name.getText().toString(), city.getSelectedItem().toString(), email,
                                        spinnerh.getSelectedItem().toString(), best.getSelectedItem().toString(), spinnerCat.getSelectedItem().toString(),
                                        spinnerprimarybus.getSelectedItem().toString(), lat, lng, c_phone);
                                add.SendRequestServiAppMethod();

                            }
                        });
                        dg.setCancelable(false);
                        dg.show();

                    }
//                else {
//                    Toast.makeText(RegisterActivity.this, "Register: [FAIL] Complete all fields...", Toast.LENGTH_SHORT).show();
//                }
//                }
//                if (email != null && pass.getText().toString() != null && name.getText().toString() != null &&spinnerprimarybus.getSelectedItem().toString()!=null) {
//
//
                }
            }
        });

        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }
    // Add spinner data

    public void addListenerOnSpinnerItemSelection() {
        spinnera.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinnerh.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinnerprimarybus.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinnerCat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                new AsyncMethodPrimaryBus().execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private class AsyncMethod extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();

            String request = "http://webview.bvibus.com/admin/getCities.php";
            resp = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            cities.clear();
            System.out.println(resp);
            String delimiter = ",";
            String strArray[] = resp.split(delimiter);

            int size = strArray.length;
            for (int i = 0; i < size; i++) {
                if (!strArray[i].equals("")) {
                    cities.add(strArray[i]);
                }
            }
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<String>
                    (RegisterActivity.this, android.R.layout.simple_spinner_item, cities);

            cityAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            city.setAdapter(cityAdapter);
        }

    }

    private class AsyncMethodCat extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();
            String request = "http://webview.bvibus.com/admin/getCategories.php";
            respcat = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            cat.clear();
            System.out.println(respcat);
            String delimiter = ",";
            String strArray[] = respcat.split(delimiter);

            int size = strArray.length;
            for (int i = 0; i < size; i++) {
                if (!strArray[i].equals("")) {
//                    cat.remove(strArray[1]);
                    cat.add(strArray[i]);
                }
            }

            ArrayAdapter<String> catAdapter = new ArrayAdapter<String>
                    (RegisterActivity.this, android.R.layout.simple_spinner_item, cat);

            catAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            spinnerCat.setAdapter(catAdapter);
        }

    }

    private class AsyncMethodPrimaryBus extends AsyncTask<Void, Integer, Boolean> {
        String cat_item = spinnerCat.getSelectedItem().toString();

        @Override
        protected Boolean doInBackground(Void... params) {
            System.out.println("cat_item :- " + cat_item);
            String request = "http://webview.bvibus.com/admin/listDriver.php";
            ServiceHandler sh = new ServiceHandler();
            List<NameValuePair> param = new ArrayList<>();
            param.add(new BasicNameValuePair("cat", cat_item));
            respprimarybus = sh.makeServiceCall(request, ServiceHandler.GET, param);
//            httpHandler handler = new httpHandler();
//            String request = "http://webview.bvibus.com/admin/listDriver.php?cat="+cat_item;
//            respprimarybus = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            p_bus.clear();
            System.out.println(respprimarybus);

            String delimiter = ",";
            String strArray[] = respprimarybus.split(delimiter);

            int size = strArray.length;
            p_bus.add(0, "None");
            for (int i = 0; i < size; i++) {

                if (!strArray[i].equals("")) {
                    p_bus.add(strArray[i]);
                    System.out.println("array :- " + strArray[i].toString());
                }
            }

            ArrayAdapter<String> catAdapter = new ArrayAdapter<String>
                    (RegisterActivity.this, android.R.layout.simple_spinner_item, p_bus);

            catAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            spinnerprimarybus.setAdapter(catAdapter);
        }

    }

    private class AsyncMethodHear extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();

            String request = "http://webview.bvibus.com/admin/getHear.php";
            resp2 = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            hear.clear();
            System.out.println(resp2);
            String delimiter = ",";
            String strArray[] = resp2.split(delimiter);

            int size = strArray.length;
            for (int i = 0; i < size; i++) {
                if (!strArray[i].equals("")) {
                    hear.add(strArray[i]);
                }
            }
            ArrayAdapter<String> hearAdapter = new ArrayAdapter<String>
                    (RegisterActivity.this, android.R.layout.simple_spinner_item, hear);

            hearAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            spinnerh.setAdapter(hearAdapter);
        }
    }

    private class AsyncMethodAges extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();

            String request = "http://webview.bvibus.com/admin/getAges.php";
            resp2 = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            age.clear();
            System.out.println(resp2);
            String delimiter = ",";
            String strArray[] = resp2.split(delimiter);

            int size = strArray.length;
            for (int i = 0; i < size; i++) {
                if (!strArray[i].equals("")) {
                    age.add(strArray[i]);
                }
            }
            ArrayAdapter<String> ageAdapter = new ArrayAdapter<String>
                    (RegisterActivity.this, android.R.layout.simple_spinner_item, age);

            ageAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            spinnera.setAdapter(ageAdapter);
        }
    }

    private String getEmiailID(Context context) {
        AccountManager accountManager = AccountManager.get(context);
        Account account = getAccount(accountManager);
        if (account == null) {
            return null;
        } else {
            return account.name;
        }
    }

    private static Account getAccount(AccountManager accountManager) {
        Account[] accounts = accountManager.getAccountsByType("com.google");
        Account account;
        if (accounts.length > 0) {
            account = accounts[0];
        } else {
            account = null;
        }
        return account;
    }

    public String GET_EMAIL_ADDRESSES() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.GET_ACCOUNTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.GET_ACCOUNTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            email = getEmiailID(getApplicationContext());
            System.out.println("CORREO SELECCIONADO: " + email);
        }
        return email;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                GET_EMAIL_ADDRESSES();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Now lets connect to the API
//        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(this.getClass().getSimpleName(), "onPause()");

        //Disconnect from API onPause()
//        if (mGoogleApiClient.isConnected()) {
//            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
//            mGoogleApiClient.disconnect();
//        }


    }

    private class AsyncMethodBest extends AsyncTask<Void, Integer, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {
            httpHandler handler = new httpHandler();
           /* AsyncHttpClient client = new AsyncHttpClient();
            client.get("http://webview.bvibus.com/admin/insertnewdatauser.php?age="+age+"&pass="+pass+"&nombre="+name+"&city="+city+"&gn="+gender+"&em="+email, new AsyncHttpResponseHandler() {

                @Override
                public void onStart() {
                    // called before request is started
                }

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] response) {
                    // called when response HTTP status is "200 OK"
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] errorResponse, Throwable e) {
                    // called when response HTTP status is "4XX" (eg. 401, 403, 404)
                }

                @Override
                public void onRetry(int retryNo) {
                    // called when request is retried
                }
            });*/
            String request = "http://webview.bvibus.com/admin/getBest.php";
            resp3 = handler.post(request);
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            bestlist.clear();
            System.out.println(resp3);
            String delimiter = ",";
            String strArray[] = resp3.split(delimiter);

            int size = strArray.length;
            for (int i = 0; i < size; i++) {
                if (!strArray[i].equals("")) {
                    bestlist.add(strArray[i]);
                }
            }
            ArrayAdapter<String> bestAdapter = new ArrayAdapter<String>
                    (RegisterActivity.this, android.R.layout.simple_spinner_item, bestlist);

            bestAdapter.setDropDownViewResource
                    (android.R.layout.simple_spinner_dropdown_item);

            best.setAdapter(bestAdapter);
        }
    }

    /**
     * If connected get lat and long
     */
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case DATE_DIALOG_ID:
                return new DatePickerDialog(this,
                        mDateSetListener, mYear, mMonth, mDay);

        }

        return null;

    }


    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            String d_license = "" + new StringBuilder().append(mDay).append("-").append(mMonth + 1).append("-").append(mYear);
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = sdf.parse(d_license);
                date2 = sdf.parse(outputDateStr);
                if (date1.before(date2)) {
//                    tv_date.setText(new StringBuilder().append(mDay).append("-").append(mMonth + 1).append("-").append(mYear));
                    tv_date.setText(new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
                    System.out.println("date :- " + new StringBuilder().append(mYear).append("-").append(mMonth + 1).append("-").append(mDay));
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter the correct date", Toast.LENGTH_SHORT).show();
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }


        }

    };
}
