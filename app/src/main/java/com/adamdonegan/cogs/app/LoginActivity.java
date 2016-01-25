package com.adamdonegan.cogs.app;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.adamdonegan.cogs.R;
import com.adamdonegan.cogs.util.DiscogsClient;
import com.adamdonegan.cogs.util.PreferencesManager;

import timber.log.Timber;

public class LoginActivity extends AppCompatActivity {
    private static final String CONSUMER_KEY = "tZplWaLrLakbPmeKDnNR";
    private static final String CONSUMER_SECRET = "WlvAHSrMKkEokrhICslQndFmlwjafEwW";
    private static final String USER_AGENT = "Cogs/0.1 +https://github.com/ajdons/Cogs";
    private static String CALLBACK_URL = "http://www.callback.com";

    private Button loginButton;
    private WebView webview;
    private PreferencesManager prefsManager;
    private DiscogsClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Timber.plant(new Timber.DebugTree());

        client = new DiscogsClient(CONSUMER_KEY, CONSUMER_SECRET, USER_AGENT, CALLBACK_URL);
        webview = (WebView) findViewById(R.id.webview);
        webview.setWebViewClient(new MyWebViewClient());
        webview.getSettings().setJavaScriptEnabled(true);
        loginButton = (Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new RequestTokenTask().execute();
            }
        });

        Timber.d("Checking for oauth token...");
        PreferencesManager.initializeInstance(this.getApplicationContext());
        prefsManager = PreferencesManager.getInstance();

        if(prefsManager.getValue("oauth_token") != null){
            Timber.d("Oauth token exists, no login required");
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        else{
            Timber.d("Oauth token does not exist, authorization is required");
        }
    }


    @Override
    public void onBackPressed() {
        if (webview.canGoBack()) {
            webview.goBack();
        } else {
            webview.destroy();
        }
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView webView, String url) {

            if(url.startsWith(CALLBACK_URL)){
                Uri uri=Uri.parse(url);
                String verifier = uri.getQueryParameter("oauth_verifier");

                if(verifier != null){
                    Timber.d("Verifier received: %s", verifier);

                    new AccessTokenTask().execute(verifier);
                }
            }
            else
            webView.loadUrl(url);
            return true;
        }
    }

    public class RequestTokenTask extends AsyncTask<String, Void, Object> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            Timber.d("Getting request token in background...");
            client.getRequestToken();
            return "Done";
        }

        @Override
        protected void onPostExecute(Object result) {
            Timber.d("Running post-execute code...");
            loginButton.setVisibility(View.INVISIBLE);
            webview.setVisibility(View.VISIBLE);
            webview.loadUrl(client.getAuthorizationURL());
        }
    }

    public class AccessTokenTask extends AsyncTask<String, Void, Object> {


        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... strings) {
            String verifier = strings[0];
            Timber.d("Getting access token in background...");
            Timber.d("Verifier recieved: ", verifier);
            client.getAccessToken(verifier);
            prefsManager.setValue("oauth_token", client.getOauthToken());
            prefsManager.setValue("oauth_token_secret", client.getOauthTokenSecret());
            return "Done";
        }

        @Override
        protected void onPostExecute(Object result) {
            Timber.d("Running post-execute code...");
            if(prefsManager.getValue("oauth_token") != null){
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        }
    }
}
