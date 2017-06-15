package busdriver.com.vipassengers;

import android.content.Context;
import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by richardalexander on 03/03/16.
 */
public class BackgroundTaskRealTimeLocation {

    Context context;
    Location latLng;

    public BackgroundTaskRealTimeLocation(Context context, Location latLng){
    this.context = context;
        this.latLng = latLng;
    }

    public void RealTimeLocatioSubmit(){
        TaskRealTimeLocationSubmit task = new TaskRealTimeLocationSubmit();
        task.execute();
    }

    private class TaskRealTimeLocationSubmit extends AsyncTask<Void, Integer, Boolean> {



            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    httpHandler handler = new httpHandler();
                    DatabaseHandlerUser db = new DatabaseHandlerUser(context);
                    String email = db.getEmail();
                    String txt = handler.post("http://serviapp.com.ve/includes/IAmOnline.php?em=" + email + "&lat=" + latLng.getLatitude() + "&lng=" + latLng.getLongitude());
                    Log.e("REALTIMELOCATION", txt);
                }catch (Exception e){

                }
                return true;
            }



            @Override
            protected void onPostExecute(Boolean result) {
                if(result)
                  Log.i("Location real time upda", "Good");
            }

        }

}
