package busdriver.com.vipassengers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Window;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by richardalexander on 05/02/16.
 */
/*@ReportsCrashes(
        formUri = "http://www.abastececombustible.com.ve/acra_serviapp159753.php",
        reportType = org.acra.sender.HttpSender.Type.JSON,
        sendReportsAtShutdown = false
)*/

public class SplashScreen extends Activity {

    private BroadcastReceiver mRegistrationBroadcastReceiver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ACRA.init(SplashScreen.this.getApplication());

        // Set portrait orientation
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Hide title bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        setContentView(R.layout.splash);
        ImageView image = (ImageView) findViewById(R.id.imageView6);
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                SplashScreen.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                        startActivity(new Intent(SplashScreen.this, LoginActivity.class));
                    }
                });


            }


            // finish();
        };
        timer.schedule(task,5000);



    }
        // Simulate a long loading process on application startup.


}
