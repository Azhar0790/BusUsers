package busdriver.com.vipassengers;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class AdsActivity extends AppCompatActivity {

    ProgressDialog pDialog;
    String url = "http://webview.bvibus.com/admin/getAds.php";
    String urlimagen = "", descripcion, urlimagen2 = "", descripcion2;
    ImageView imageView;
    TextView tv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads);
        imageView = (ImageView) findViewById(R.id.adImage);
        tv = (TextView) findViewById(R.id.adcontent);
        Intent intent = this.getIntent();
        if (intent != null){
            if (intent.getExtras() != null) {
                String info = intent.getExtras().getString("sp");
                String delimiter = ",";
                String strArray[] = info.split(delimiter);
                urlimagen2 = strArray[0];
                descripcion2 = strArray[1];
            }
        }
        new LoadAd().execute();
    }

    public void load_add(){
        new LoadAd().execute();
    }
    private class LoadAd extends AsyncTask<Void, Void, Void> {

        private static final String TAG = "";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(AdsActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {


            HttpHandleer sh = new HttpHandleer();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url);

            Log.e(TAG, "Response from url: " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);
                    urlimagen =  jsonObj.getString("imagen");
                    descripcion = jsonObj.getString("descripcion");
                } catch (final JSONException e) {
                    Log.e(TAG, "Json parsing error: " + e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            /*Toast.makeText(getApplicationContext(),
                                    "Json parsing error: " + e.getMessage(),
                                    Toast.LENGTH_LONG)
                                    .show();*/
                        }
                    });
                }
            } else {
                Log.e(TAG, "Couldn't get json from server.");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                     /*   Toast.makeText(getApplicationContext(),
                                "Couldn't get json from server. Check LogCat for possible errors!",
                                Toast.LENGTH_LONG)
                                .show();*/
                    }
                });

            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            if (!urlimagen2.equals("")) {
                Picasso.with(AdsActivity.this).load(urlimagen2).into(imageView);
                System.out.println("ImagenUrl 2:" + urlimagen2);
                tv.setText(descripcion2);
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        finish();
                        AdsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //Your code to run in GUI thread here
                                Intent it = new Intent(new Intent(AdsActivity.this, LoginActivity.class));
                                it.putExtra("s","s");
                                startActivity(it);

                            }//public void run() {
                        });
                    }
                }, 5000);
            } else {
                if (urlimagen != null && !urlimagen.equals("")) {
                        System.out.println("Imagen url:" + urlimagen);
                        try {
                            Picasso.with(AdsActivity.this).load(urlimagen).into(imageView);
                        } catch (Exception e) {
                            //   Toast.makeText(AdsActivity.this, "Error 404: Image not load", Toast.LENGTH_SHORT).show();
                        }
                        if (descripcion != null) {
                            tv.setText(descripcion);
                        }
                    Timer t = new Timer();
                    t.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            finish();
                            AdsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //Your code to run in GUI thread here
                                    Intent it = new Intent(new Intent(AdsActivity.this, LoginActivity.class));
                                    it.putExtra("s","s");
                                    startActivity(it);
                                }//public void run() {
                            });
                        }
                    }, 5000);
                } else {
                    Log.e("No cargó el anuncio", "Ocurrió un error al cargar el anuncio URL de imagen NULO!");
                    finish();
                    Intent it = new Intent(new Intent(AdsActivity.this, LoginActivity.class));
                    it.putExtra("s","s");
                    startActivity(it);
                }
            }
        }
    }
}
