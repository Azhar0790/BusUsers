package busdriver.com.vipassengers;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

/**
 * Created by Sarps on 2/10/2017.
 */
public class FacebookPageActivity extends Activity {
    WebView webview;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fbactivity);
        webview=(WebView)findViewById(R.id.webview);
        load("https://www.facebook.com/Vi-Passenger-1711920192468944/");

    }
    private void load(String url) {
        final ProgressDialog pd = ProgressDialog.show(FacebookPageActivity.this, "", "Please wait while laoding ...", true);
        webview.setWebViewClient(new WebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        webview.getSettings().setLoadWithOverviewMode(true);
        webview.getSettings().setUseWideViewPort(true);
        webview.getSettings().setBuiltInZoomControls(true);


        webview.setWebViewClient(new WebViewClient() {
                                     public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                         Toast.makeText(FacebookPageActivity.this, description, Toast.LENGTH_SHORT).show();
                                     }

                                     @Override
                                     public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                         pd.show();
                                     }


                                     @Override
                                     public void onPageFinished(WebView view, String url) {
                                         pd.dismiss();

                                         String webUrl = webview.getUrl();

                                     }
                                 });
        webview.loadUrl(url);
    }
}
