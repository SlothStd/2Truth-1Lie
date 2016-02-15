package sloth.twotruthsonelie;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;

/**
 * Created by Daniel on 1/27/2016.
 */

public class MainActivity extends Activity {

    Button onePhone, twoPhones, settings, wifi, bt;
    boolean is2PhoneOpen = false;

    Animation fadeIn;
    Animation fadeInSlower;
    Animation fadeInSlowest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onePhone = (Button) findViewById(R.id.Onephone);
        twoPhones = (Button) findViewById(R.id.Twophones);
        settings = (Button) findViewById(R.id.Settings);
        wifi = (Button) findViewById(R.id.WiFi);
        bt = (Button) findViewById(R.id.BT);

        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeInSlower = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slower);
        fadeInSlowest = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slowest);

        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        final ProgressBar progressBarSett = (ProgressBar) findViewById(R.id.progressBarSett);
        final ProgressBar progressBarMulti = (ProgressBar) findViewById(R.id.progressBarMulti);


        progressBar.setVisibility(View.VISIBLE);
        progressBar.startAnimation(fadeIn);
        onePhone.setVisibility(View.VISIBLE);
        onePhone.startAnimation(fadeIn);
        onePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500);
                animation.setDuration(700); //in milliseconds bruv
                animation.setInterpolator(new DecelerateInterpolator());

                new CountDownTimer(800, 700) {

                    public void onTick(long millisUntilFinished) {
                        animation.start();
                    }

                    public void onFinish() {

                        progressBar.setProgress(0);
                        animation.cancel();
                        progressBar.clearAnimation();


                        Intent singleplayer = new Intent(MainActivity.this, OnePhone.class);
                        startActivity(singleplayer);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);


                    }
                }.start();

            }

        });


        progressBarMulti.setVisibility(View.VISIBLE);
        progressBarMulti.setAnimation(fadeIn);
        twoPhones.setVisibility(View.VISIBLE);
        twoPhones.startAnimation(fadeIn);
        twoPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ObjectAnimator animation = ObjectAnimator.ofInt(progressBarMulti, "progress", 0, 500);
                animation.setDuration(700); //in milliseconds bruv
                animation.setInterpolator(new DecelerateInterpolator());

                new CountDownTimer(800, 700) {

                    public void onTick(long millisUntilFinished) {
                        animation.start();
                    }

                    public void onFinish() {

                        progressBar.setProgress(0);
                        progressBar.clearAnimation();
                        progressBar.setVisibility(View.GONE);


                        progressBarMulti.setProgress(0);
                        animation.cancel();
                        progressBarMulti.clearAnimation();
                        progressBarMulti.setVisibility(View.GONE);


                        is2PhoneOpen = true;

                        onePhone.clearAnimation();
                        onePhone.setVisibility(View.GONE);

                        twoPhones.clearAnimation();
                        twoPhones.setVisibility(View.GONE);

                        settings.clearAnimation();

                        wifi.setVisibility(View.VISIBLE);
                        wifi.setAnimation(fadeIn);

                        bt.setVisibility(View.VISIBLE);
                        bt.setAnimation(fadeInSlower);


                    }
                }.start();

            }
        });


        progressBarSett.setVisibility(View.VISIBLE);
        progressBarSett.setAnimation(fadeIn);
        settings.setVisibility(View.VISIBLE);
        settings.startAnimation(fadeIn);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ObjectAnimator animation = ObjectAnimator.ofInt(progressBarSett, "progress", 0, 500);
                animation.setDuration(700); //in milliseconds bruv
                animation.setInterpolator(new DecelerateInterpolator());

                new CountDownTimer(800, 700) {

                    public void onTick(long millisUntilFinished) {
                        animation.start();
                    }

                    public void onFinish() {

                        progressBarSett.setProgress(0);
                        progressBarSett.clearAnimation();
                        animation.cancel();


                        Intent toSettigns = new Intent(MainActivity.this, Settings.class);
                        startActivity(toSettigns);

                        progressBarSett.clearAnimation();
                        animation.cancel();


                    }
                }.start();


            }
        });

        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleplayer = new Intent(MainActivity.this, MpWifi.class);
                startActivity(singleplayer);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent bluetooth = new Intent(MainActivity.this, Bluetooth.class);
                startActivity(bluetooth);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (is2PhoneOpen) {

            is2PhoneOpen = false;
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        } else {
            super.onBackPressed();
        }
    }
}