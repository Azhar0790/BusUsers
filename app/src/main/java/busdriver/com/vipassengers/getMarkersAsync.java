package busdriver.com.vipassengers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.support.v4.app.NotificationCompat;
import android.util.Log;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Developed by Richard A. Lozada - rlozada808@gmail.com on 06/08/16.
 */
public class getMarkersAsync {
    Context context;
    LatLng latLng;
    int tipo;
    private static final String KEY_NAME = "name";
    private static final String KEY_LAT = "lat";
    private static final String KEY_LNG = "lng";
    GoogleMap mMap;
    String address, phone, horario, imagen, desc, route;
    float latt, lngg;
    boolean able = true;
    boolean ablead = true;
    String name;

    public getMarkersAsync(Context context, GoogleMap mMap, LatLng latLng) {
        this.context = context;
        this.mMap = mMap;
        this.latLng = latLng;
    }


    public void getMarkersAsyncMethod() {
        AsyncMethod task = new AsyncMethod();
        task.execute();
    }

    private class AsyncMethod extends AsyncTask<Void, Integer, Boolean> {


        ProgressDialog pd = new ProgressDialog(context);

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mMap.clear();
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }


        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                HandleXML parser = new HandleXML();
                String xml = parser.getXmlFromUrl("http://webview.bvibus.com/admin/showDrivers.php"); // getting XML
                Document doc = parser.getDomElement(xml); // getting DOM element
                NodeList nl = doc.getElementsByTagName("marker");
                if (nl.getLength() > 0) {
                    for (int i = 0; i < nl.getLength(); i++) {
                        Element e = (Element) nl.item(i);
                        // adding each child node to HashMap key => value
                        name = e.getAttribute("name");
                        address = e.getAttribute("address");
                        phone = e.getAttribute("phone");
                        horario = e.getAttribute("horario");
                        route = e.getAttribute("route");
                        String lat = e.getAttribute("lat");
                        String lng = e.getAttribute("lng");
                        latt = Float.parseFloat(lat);
                        lngg = Float.parseFloat(lng);
                        if (name.equals("0") || address.equals("0")) {
                            desc = e.getAttribute("desc");
                            imagen = e.getAttribute("imagen");
                        }
                        publishProgress(0);
                    }
                }

            } catch (Exception e) {

            }
            return true;
        }

        @Override
        protected void onProgressUpdate(Integer... Values) {
            super.onProgressUpdate(Values);
            int type = 0;
            LatLng sydney = new LatLng(latt, lngg);

//            mMap.addMarker(new MarkerOptions().position(sydney).draggable(true).title("Bus").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
            if (name.equals("Bus")) {
                drawMarker(sydney, "Name: " + name, "Phone No: " + phone + "\n" + "Route: " + route, R.drawable.bus);

            } else if (name.equals("Blue Team")) {
                drawMarker(sydney, "Name: " + name, "Phone No: " + phone + "\n" + "Route: " + route, R.drawable.blue_team);

            } else if ((name.equals("Green Team"))) {
                drawMarker(sydney, "Name: " + name, "Phone No: " + phone + "\n" + "Route: " + route, R.drawable.green_team);

            } else if ((name.equals("Taxi"))) {

//                drawMarker(sydney, nombre,R.drawable.taxi);
                drawMarker(sydney, "Name: " + name, "Phone No: " + phone + "\n" + "Route: " + route, R.drawable.taxi);

            } else if ((name.equals("null"))) {
//                mMap.addMarker(new MarkerOptions().position(sydney).draggable(true).title("Bus").icon(BitmapDescriptorFactory.fromResource(R.drawable.bus)));
                drawMarker(sydney, "Name: " + name, "Phone No: " + phone + "\n" + "Route: " + route, R.drawable.bus);
            }
            double distance;
            Location locationA = new Location("point A");

            locationA.setLatitude(latt);
            locationA.setLongitude(lngg);

            Location locationB = new Location("point B");

            locationB.setLatitude(latLng.latitude);
            locationB.setLongitude(latLng.longitude);

            distance = locationA.distanceTo(locationB);
            if (distance / 1000 < 5.0)
                if (able && type == 0) {
                    startNotification("Driver name: " + name + "\n route: " + address + "\n Phone Number: " + phone + "\n Schedule: " + horario, "0");
                    able = false;
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            able = true;
                        }
                    }, 1000 * 60 * 5);
                }
            if (ablead && type == 1) {
                startNotification(imagen + "," + desc, "1");
                ablead = false;
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        ablead = true;
                    }
                }, 1000 * 60 * 5);
            }

            // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }


        @Override
        protected void onPostExecute(Boolean result) {
            System.out.println("***** Termino la busqueda de marcaradores *****");
        }
    }

    private void drawMarker(LatLng point, String text, String snippet, int drawable) {

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(point).title(text).snippet(snippet).icon(BitmapDescriptorFactory.fromResource(drawable));
        mMap.addMarker(markerOptions);
    }

    public float distance(float lat_a, float lng_a, double lat_b, double lng_b) {
        double earthRadius = 3958.75;
        double latDiff = Math.toRadians(lat_b - lat_a);
        double lngDiff = Math.toRadians(lng_b - lng_a);
        double a = Math.sin(latDiff / 2) * Math.sin(latDiff / 2) +
                Math.cos(Math.toRadians(lat_a)) * Math.cos(Math.toRadians(lat_b)) *
                        Math.sin(lngDiff / 2) * Math.sin(lngDiff / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = earthRadius * c;

        int meterConversion = 1609;

        return new Float(distance * meterConversion).floatValue();
    }

    private void startNotification(String x, String y) {
        Log.i("NextActivity", "startNotification");
        NotificationCompat.Builder mBuilder = null;
        // Sets an ID for the notification
        int mNotificationId = 001;

        // Build Notification , setOngoing keeps the notification always in status bar
        if (y.equals("1")) {
            String info = x;
            String delimiter = ",";
            String strArray[] = info.split(delimiter);
            String descripcion2 = strArray[1];
            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.bus)
                            .setContentTitle("Advertisements")
                            .setContentText(descripcion2);
        } else {
            mBuilder =
                    new NotificationCompat.Builder(context)
                            .setSmallIcon(R.drawable.bus)
                            .setContentTitle("A bus approaches")
                            .setContentText("More info...");
        }
        // Create pending intent, mention the Activity which needs to be
        //triggered when user clicks on notification(StopScript.class in this case)
        Intent it = new Intent(context, MapsActivity.class);
        it.putExtra("show", x);
        it.putExtra("type", y);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, it
                , PendingIntent.FLAG_UPDATE_CURRENT);


        mBuilder.setContentIntent(contentIntent);


        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);

        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());


    }

}