package busdriver.com.vipassengers.fcm;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import busdriver.com.vipassengers.AdsActivity;
import busdriver.com.vipassengers.AdsLoader;
import busdriver.com.vipassengers.MapsActivity;
import busdriver.com.vipassengers.R;
import busdriver.com.vipassengers.ServiceHandler;


/**
 * Created by Belal on 5/27/2016.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    SharedPreferences pref;
    String email;
    Bitmap bitmap;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Displaying data in log
        //It is optional
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Notification Message Body: " + remoteMessage.getData());
        pref=getSharedPreferences("Pref",Context.MODE_PRIVATE);
        email=pref.getString("email_id","");
        String msg = "" + remoteMessage.getData();
        try {
            String[] msg_edit = msg.split(",");
            String msg_edit1 = msg_edit[0];
            String[] msg_img = msg_edit1.split("=");
            String msg_img_url = msg_img[1];
            System.out.println("msg_edit1 :- "+msg_edit1);
            System.out.println("msg_img_url :- "+msg_img_url);


            String msg_edit2 = msg_edit[1];
            String[] msg_msg = msg_edit2.split("=");
            String msg_msg_txt = msg_msg[1];

            final String[] msg_msg_ = msg_msg_txt.split("\\}");
            System.out.println("msg_msg_[0] :- "+msg_msg_[0]);
            bitmap = getBitmapfromUrl(msg_img_url);
            if(bitmap!=null) {
                sendNotification(msg_msg_[0], bitmap);
            }else{
                sendNotification_msg(msg_img_url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error :- "+e);
        }


        Timer t=new Timer();
        TimerTask hourlyTask = new TimerTask () {
            @Override
            public void run () {
                new Noti_Asyn().execute();
                System.out.println("Time of 10 mins");
//                new Noti_adv_Asyn().execute();
            }
        };
        t.schedule (hourlyTask, 0, 1000*60);



        //Calling method to generate notification
//        sendNotification(remoteMessage.getNotification().getBody());

    }



    //This method is only generating push notification
    //It is same as we did in earlier posts
    private void sendNotification(String messageBody,Bitmap bp) {
        Intent intent = new Intent(this, AdsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.BigPictureStyle s = new NotificationCompat.BigPictureStyle().bigPicture(bp);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(largeIcon)
                .setStyle(s)
                .setContentTitle("Vi Passenger")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        } else
        {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }
        notificationManager.notify(0, notificationBuilder.build());

    }
    private void sendNotification_msg(String messageBody) {
        Intent intent = new Intent(this, AdsActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_ONE_SHOT);
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setLargeIcon(largeIcon)
                .setContentTitle("Vi Passenger")
                .setContentText(messageBody)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);
        notificationBuilder.build().flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
        {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        } else
        {
            notificationBuilder.setSmallIcon(R.mipmap.ic_launcher);
        }
        notificationManager.notify(0, notificationBuilder.build());

    }

    /*
    *To get a Bitmap image from the URL received
    * */
    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(input);
            return bitmap;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;

        }
    }
    class Noti_Asyn extends AsyncTask<String,String,String>{
        String msg;
        String url="http://webview.bvibus.com/distanceNotification.php";


        @Override
        protected String doInBackground(String... params) {
            ServiceHandler sh=new ServiceHandler();
            List<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("user_mail",email));
            String jsnstr=sh.makeServiceCall(url,ServiceHandler.GET,param);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Noti_Asyn().execute();
                }
            },1000*60*60);

        }
    }
    class Noti_adv_Asyn extends AsyncTask<String,String,String>{
        String msg;
        String url="http://webview.bvibus.com/admin/adds_notification.php";


        @Override
        protected String doInBackground(String... params) {
            ServiceHandler sh=new ServiceHandler();
            List<NameValuePair> param=new ArrayList<>();
            param.add(new BasicNameValuePair("user_mail",email));
            String jsnstr=sh.makeServiceCall(url,ServiceHandler.GET,param);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    new Noti_adv_Asyn().execute();
                }
            },1000*60*60);

        }
    }
}