package edu.hastings.hastingscollege;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Window;

public class SplashActivity extends Activity {

    private final long SPLASH_TIME_OUT = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);    // Removes title bar
        setContentView(R.layout.splash);
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isSplashEnabled = sp.getBoolean("isSplashEnabled", true);

        if (isSplashEnabled) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    SplashActivity.this.finish();
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    SplashActivity.this.startActivity(i);
                    // transition from splash to main menu
                    overridePendingTransition(R.anim.left,
                            R.anim.right);
                }
            }, SPLASH_TIME_OUT);
        }
        else {
            Intent i = new Intent(SplashActivity.this, MainActivity.class);
            SplashActivity.this.startActivity(i);
            finish();
        }
    }
}