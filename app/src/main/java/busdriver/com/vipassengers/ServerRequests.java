package busdriver.com.vipassengers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class ServerRequests {
    ProgressDialog progressDialog;
    public static final int CONNECTION_TIMEOUT = 1000 * 15;
    public static final String SERVER_ADDRESS = "http://webview.bvibus.com/admin/";
    Context context;
    SharedPreferences pref;
    public ServerRequests(Context context) {
        progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("Connecting...");
        pref=context.getSharedPreferences("Pref",Context.MODE_PRIVATE);
        this.context=context;
    }

    public void storeUserDataInBackground(User user,
                                          GetUserCallback userCallBack) {
        progressDialog.show();
        new StoreUserDataAsyncTask(user, userCallBack).execute();
    }

    public void fetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
        progressDialog.show();
        new fetchUserDataAsyncTask(user, userCallBack).execute();
    }

    /**
     * parameter sent to task upon execution progress published during
     * background computation result of the background computation
     */

    public class StoreUserDataAsyncTask extends AsyncTask<Void, Void, Void> {
        User user;
        GetUserCallback userCallBack;

        public StoreUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected Void doInBackground(Void... params) {
            ArrayList<NameValuePair> dataToSend = new ArrayList<>();
            dataToSend.add(new BasicNameValuePair("name", user.name));
            dataToSend.add(new BasicNameValuePair("username", user.username));
            dataToSend.add(new BasicNameValuePair("password", user.password));

            HttpParams httpRequestParams = getHttpRequestParams();

            HttpClient client = new DefaultHttpClient(httpRequestParams);
            HttpPost post = new HttpPost(SERVER_ADDRESS
                    + "Register.php");

            try {
                post.setEntity(new UrlEncodedFormEntity(dataToSend));
                client.execute(post);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private HttpParams getHttpRequestParams() {
            HttpParams httpRequestParams = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            HttpConnectionParams.setSoTimeout(httpRequestParams,
                    CONNECTION_TIMEOUT);
            return httpRequestParams;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            progressDialog.dismiss();
            userCallBack.done(null);
        }

    }

    public class fetchUserDataAsyncTask extends AsyncTask<Void, Void, User> {
        User user;
        GetUserCallback userCallBack;

        public
        fetchUserDataAsyncTask(User user, GetUserCallback userCallBack) {
            this.user = user;
            this.userCallBack = userCallBack;
        }

        @Override
        protected User doInBackground(Void... params) {


            User returnedUser = null;

            try {

                httpHandler httphandler = new httpHandler();
                String responseText = httphandler.post2(SERVER_ADDRESS+"fetchdatauser2.php?username="+user.username+"&pass="+user.password+"&deviceId="+user.token, user.username, user.password,user.token);
                Log.i("Resultado consulta",responseText);
                JSONObject jObject = new JSONObject(responseText);
                if (jObject.length() != 0){
                    JSONObject jsonObject=jObject.getJSONObject("message");
                    String email = jsonObject.getString("email");
                    String cat_id = jsonObject.getString("cat_id");
                    SharedPreferences.Editor editor=pref.edit();
                    editor.putString("emaild",email);
                    editor.putString("cat_id",cat_id);
                    editor.commit();
                    returnedUser = new User(email, 0, 0, user.username, user.password, "app", "app",user.token);

                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return returnedUser;
        }

        @Override
        protected void onPostExecute(User returnedUser) {
            super.onPostExecute(returnedUser);
            progressDialog.dismiss();
            userCallBack.done(returnedUser);
//            context.startActivity(new Intent(context, MapsActivity.class));
//            context.startActivity(new Intent(context, PaymentStripe.class));
//            new TrialPeriod().execute();
        }
    }
    class TrialPeriod extends AsyncTask<String, String, String> {
        String jsnStr = null, message = null, email_id=null;
        @Override
        protected String doInBackground(String... params) {
            email_id = pref.getString("emaild", "");
            System.out.println("email_id :- " + email_id);
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
                        context.startActivity(new Intent(context,Package_activity.class));
                        Toast.makeText(context,"Your Account is not activated",Toast.LENGTH_SHORT).show();
                    }else{
                        context.startActivity(new Intent(context, MapsActivity.class));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}