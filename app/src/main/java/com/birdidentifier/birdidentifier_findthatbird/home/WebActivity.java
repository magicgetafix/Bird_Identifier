package com.birdidentifier.birdidentifier_findthatbird.home;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.birdidentifier.birdidentifier_findthatbird.R;

public class WebActivity extends AppCompatActivity {


        private WebView webView;
        private ProgressBar progressBar;
        private Toolbar toolbar;
        public static String URL_KEY = "url_key";
        public static String BIRD_NAME_KEY = "bird_name_key";

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_web);
            webView = findViewById(R.id.web_view);
            progressBar = findViewById(R.id.wiki_webview_progressbar);
            toolbar = findViewById(R.id.toolbar);

            Bundle extras = getIntent().getExtras();
            if (extras!=null){
                String birdName = extras.getString(BIRD_NAME_KEY, "Bird Details");
                String webUrl = extras.getString(URL_KEY, "https://en.wikipedia.org/");
                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebChromeClient(new WebChromeClient(){
                    @Override
                    public void onReceivedTitle(WebView view, String title) {
                        if (getSupportActionBar()!=null){
                            getSupportActionBar().setTitle(birdName);
                        }

                    }
                });

                setSupportActionBar(toolbar);
                ActionBar actionBar = getSupportActionBar();
                if (actionBar!=null){
                    actionBar.setDisplayHomeAsUpEnabled(true);
                    actionBar.setHomeButtonEnabled(true);
                    actionBar.setTitle(birdName);
                }

                webView.setWebViewClient(new WebViewClient() {
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        return false;
                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
                webView.loadUrl(webUrl);
            }
        }

    }


