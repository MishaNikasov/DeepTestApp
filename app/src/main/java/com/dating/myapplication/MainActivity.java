package com.dating.myapplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.facebook.applinks.AppLinkData;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import bolts.AppLinks;

public class MainActivity extends AppCompatActivity {

    TextView text;
    String deepLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);

        printHashKey(this);
        getDeepLink();
    }

    void printHashKey(Context context) {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.d("HASH_KEY", "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e("HASH_KEY", "printHashKey()", e);
        } catch (Exception e) {
            Log.e("HASH_KEY", "printHashKey()", e);
        }
    }


    private void getDeepLink() {
        AppLinkData.fetchDeferredAppLinkData(this,
                new AppLinkData.CompletionHandler() {
                    @Override
                    public void onDeferredAppLinkDataFetched(AppLinkData appLinkData) {
                        if (appLinkData != null) {

                            Log.d("nikasov", "DeepLink.getTargetUri().toString() = "
                                    + appLinkData.getTargetUri().toString());

                            deepLink = appLinkData.getTargetUri().toString();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    text.setText(deepLink);
                                }
                            });

                        }
                        else {
                            Log.d("nikasov", "onDeferredAppLinkDataFetched is null");

                            deepLink = tryAgain();

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    text.setText(deepLink);
                                }
                            });

                        }
                    }
                }
        );
    }

    private String tryAgain() {
        Uri targetUrl = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), getIntent());

        if (targetUrl != null) {

            targetUrl = AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), getIntent());
            targetUrl.toString();
            Log.d("nikasov",
                    "AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), " +
                            "getIntent()) " + targetUrl.toString());
            return targetUrl.toString();
        }
        else {
            Log.d("nikasov",
                    "AppLinks.getTargetUrlFromInboundIntent(getApplicationContext(), " +
                            "getIntent()) " + "is null");
            return "";
        }
    }

}
