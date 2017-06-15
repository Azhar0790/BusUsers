package busdriver.com.vipassengers;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

/**
 * Created by ajldpc on 27/08/2016.
 */
public class addUserToServer {
    Context context;
    String city,dob,gender,pass,best, name, hear,email,resp,type,primary_bus,lat,lng,phn;

    public addUserToServer(Context context, String dob, String pass, String gender, String name, String city, String email, String hear,String best,String type,String primary_bus,String lat,String lng,String phn){
        this.context = context;
        this.dob = dob;
        this.pass = pass;
        this.name = name;
        this.gender = gender;
        this.city = city;
        this.email = email;
        this.hear = hear;
        this.best = best;
        this.type = type;
        this.primary_bus = primary_bus;
        this.lat = lat;
        this.lng = lng;
        this.phn = phn;
    }

    public void SendRequestServiAppMethod(){
        AsyncMethod task = new AsyncMethod();
        task.execute();
    }

    private class AsyncMethod extends AsyncTask<Void, Integer, Boolean> {

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
            String request = "http://webview.bvibus.com/admin/insertnewdatauser.php?dob="+dob+"&pass="+pass.trim()+"&nombre="+name.trim()+"&city="+city.trim()+"&gn="+gender.trim()+"&em="+email.trim()+"&hear="+hear.trim()+"&best="+best+"&type="+type.trim()+"&primary_bus="+primary_bus.trim()+"&user_lat="+lat+"&user_long="+lng+"&phone_num="+phn;
             resp = handler.post(request.trim().replaceAll("\\+","55j").replaceAll(" ",""));

            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {

            System.out.println("result :- "+result);
            if(result!=null) {
                context.startActivity(new Intent(context, LoginActivity.class));
                Toast.makeText(context, "Registration approved. You can now login", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
