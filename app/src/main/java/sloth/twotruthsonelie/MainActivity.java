package sloth.twotruthsonelie;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;


/**
 * Created by Daniel on 1/27/2016.
 */

public class MainActivity extends Activity {

    Button onePhone, twoPhones, powerups, settings,
            wifi, bt, googlePlusMULTI, checkGamesMULTI, playMULTI;
    boolean is2PhoneOpen = false;

    Animation fadeIn;
    Animation fadeInSlower;
    Animation fadeInSlowest;
    Animation slideUP, slideDOWN;
    RelativeLayout levelInfo;
    ImageView logo;
    ObjectAnimator animation;

    ProgressBar progressBarPlayMULTI, progressBarCheckMULTI, progressGooglePLus;
    ProgressBar progressBar, progressBarSett, progressBarMulti, level;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onePhone = (Button) findViewById(R.id.Onephone);
        twoPhones = (Button) findViewById(R.id.Twophones);
        settings = (Button) findViewById(R.id.Settings);
        wifi = (Button) findViewById(R.id.WiFi);
        bt = (Button) findViewById(R.id.BT);
        powerups = (Button) findViewById(R.id.powerups);
        logo = (ImageView) findViewById(R.id.appNameLogo);

        ////////////////////////Buttony namiesto WIFI → Activita////////////////
        checkGamesMULTI = (Button) findViewById(R.id.checkGamesMulti);
        playMULTI = (Button) findViewById(R.id.playMULTI);
        googlePlusMULTI = (Button) findViewById(R.id.googlePlusMULTI);

        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeInSlower = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slower);
        fadeInSlowest = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slowest);
        slideDOWN = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.lie), PorterDuff.Mode.SRC_IN);
        progressBarSett = (ProgressBar) findViewById(R.id.progressBarSett);
        progressBarSett.getProgressDrawable().setColorFilter(getResources().getColor(R.color.lie), PorterDuff.Mode.SRC_IN);
        progressBarMulti = (ProgressBar) findViewById(R.id.progressBarMulti);
        progressBarMulti.getProgressDrawable().setColorFilter(getResources().getColor(R.color.lie), PorterDuff.Mode.SRC_IN);

        level = (ProgressBar) findViewById(R.id.progressBarLevel);

        progressBarPlayMULTI = (ProgressBar) findViewById(R.id.progressBarPlayMULTI);
        progressBarPlayMULTI.getProgressDrawable().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
        progressBarCheckMULTI = (ProgressBar) findViewById(R.id.progressCheckMULTI);
        progressBarCheckMULTI.getProgressDrawable().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);
        progressGooglePLus = (ProgressBar) findViewById(R.id.progressGooglePluskMULTI);
        progressGooglePLus.getProgressDrawable().setColorFilter(getResources().getColor(R.color.orange), PorterDuff.Mode.SRC_IN);

        levelInfo = (RelativeLayout) findViewById(R.id.level);

        progressBar.setProgress(0);
        progressBar.clearAnimation();

        progressBarSett.setProgress(0);
        progressBarSett.clearAnimation();

        progressBarMulti.setProgress(0);
        progressBarMulti.clearAnimation();

        powerups.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


        playMULTI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(MainActivity.this, "watevah", Toast.LENGTH_SHORT).show();

                animation = ObjectAnimator.ofInt(progressBarPlayMULTI, "progress", 0, 500);
                animation.setDuration(700); //in milliseconds bruv
                animation.setInterpolator(new DecelerateInterpolator());

                new CountDownTimer(800, 700) {

                    public void onTick(long millisUntilFinished) {
                        animation.start();
                    }

                    public void onFinish() {

                        /*
                        * Tuto
                        * daj
                        * chujoviny
                        * so Spustením hry
                        * */

                        Intent multi = new Intent(MainActivity.this, MpWifi.class);
                        startActivity(multi);

                        progressBarPlayMULTI.setProgress(0);
                        animation.cancel();
                    }
                }.start();

            }

        });

        checkGamesMULTI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                final ObjectAnimator animation = ObjectAnimator.ofInt(progressBarCheckMULTI, "progress", 0, 500);
                animation.setDuration(700); //in milliseconds bruv
                animation.setInterpolator(new DecelerateInterpolator());

                new CountDownTimer(800, 700) {

                    public void onTick(long millisUntilFinished) {
                        animation.start();
                    }

                    public void onFinish() {


                        /*
                        * Tuto
                        * daj
                        * chujoviny
                        * s "CheckGames"
                        * */

                        progressBarCheckMULTI.setProgress(0);
                        animation.cancel();
                    }
                }.start();

            }
        });

        googlePlusMULTI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ObjectAnimator animation = ObjectAnimator.ofInt(progressGooglePLus, "progress", 0, 500);
                animation.setDuration(700); //in milliseconds bruv
                animation.setInterpolator(new DecelerateInterpolator());

                new CountDownTimer(800, 700) {

                    public void onTick(long millisUntilFinished) {
                        animation.start();
                    }

                    public void onFinish() {

                        /*
                        * Tuto
                        * daj
                        * chujoviny
                        * s GooglePlusom
                        * */

                        progressGooglePLus.setProgress(0);
                        animation.cancel();
                    }
                }.start();

            }
        });

        onePhone.setVisibility(View.VISIBLE);
        onePhone.startAnimation(fadeIn);
        progressBar.setVisibility(View.VISIBLE);
        progressBar.startAnimation(fadeIn);
        onePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500);
                animation.setDuration(700); //in milliseconds bruv
                animation.setInterpolator(new DecelerateInterpolator());

                new CountDownTimer(100, 1000) {

                    public void onTick(long millisUntilFinished) {
//                        animation.start();
                    }

                    public void onFinish() {


                        Intent singleplayer = new Intent(MainActivity.this, OnePhone.class);
                        startActivity(singleplayer);
                        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        MainActivity.this.finish();
                    }
                }.start();

            }

        });

        twoPhones.setVisibility(View.VISIBLE);
        twoPhones.startAnimation(fadeIn);
        progressBarMulti.setVisibility(View.VISIBLE);
        progressBarMulti.setAnimation(fadeIn);
        twoPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ObjectAnimator animation = ObjectAnimator.ofInt(progressBarMulti, "progress", 0, 500);
                animation.setDuration(700); //in milliseconds bruv
                animation.setInterpolator(new DecelerateInterpolator());

                new CountDownTimer(100, 1000) {

                    public void onTick(long millisUntilFinished) {
//                        animation.start();
                    }

                    public void onFinish() {


                        checkGamesMULTI.clearAnimation();
                        playMULTI.clearAnimation();
                        googlePlusMULTI.clearAnimation();
                        setSlideDOWN();

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


//
//                        wifi.setVisibility(View.VISIBLE);
//                        wifi.setAnimation(fadeIn);
//
//                        bt.setVisibility(View.VISIBLE);
//                        bt.setAnimation(fadeInSlower);
                    }
                }.start();
            }
        });


        settings.startAnimation(fadeIn);
        progressBarSett.setVisibility(View.VISIBLE);
        settings.setVisibility(View.VISIBLE);
        progressBarSett.setAnimation(fadeIn);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ObjectAnimator animation = ObjectAnimator.ofInt(progressBarSett, "progress", 0, 500);
                animation.setDuration(700); //in milliseconds bruv
                animation.setInterpolator(new DecelerateInterpolator());

                new CountDownTimer(500, 1000) {

                    public void onTick(long millisUntilFinished) {
//                        animation.start();
                    }

                    public void onFinish() {


                        Intent toSettigns = new Intent(MainActivity.this, Settings.class);
                        startActivity(toSettigns);
                        MainActivity.this.finish();

                    }
                }.start();
            }
        });

        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleplayer = new Intent(MainActivity.this, MpWifi.class);
                startActivity(singleplayer);
                MainActivity.this.finish();
            }
        });

        ////
        findViewById(R.id.kappa123).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mp = new Intent(MainActivity.this, MpWifi.class);
                startActivity(mp);
                MainActivity.this.finish();
            }
        });
        ///

        bt.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                Intent bluetooth = new Intent(MainActivity.this, Bluetooth.class);
                startActivity(bluetooth);
                MainActivity.this.finish();
            }
        });
    }

    private void setSlideUP() {

        new CountDownTimer(100, 500) {

            public void onTick(long millisUntilFinished) {


            }

            public void onFinish() {

                powerups.setVisibility(View.VISIBLE);
                powerups.setTranslationY(0);
                powerups.animate().translationY(200);

                levelInfo.setVisibility(View.VISIBLE);
                levelInfo.setTranslationY(0);
                levelInfo.animate().translationY(-600);

                progressBarPlayMULTI.setVisibility(View.GONE);
                progressBarCheckMULTI.setVisibility(View.GONE);
                progressGooglePLus.setVisibility(View.GONE);
            }

        }.start();

    }

    private void setSlideDOWN() {


//        new CountDownTimer(100, 500) {
//
//            public void onTick(long millisUntilFinished) {
//            }
//
//            public void onFinish() {
        powerups.setVisibility(View.VISIBLE);
        powerups.setTranslationY(200);
        powerups.animate().translationY(0);

        levelInfo.setVisibility(View.VISIBLE);
        levelInfo.setTranslationY(-600);
        levelInfo.animate().translationY(0);

        playMULTI.clearAnimation();
        playMULTI.setVisibility(View.VISIBLE);
        playMULTI.setAnimation(fadeInSlower);


        checkGamesMULTI.clearAnimation();
        checkGamesMULTI.setVisibility(View.VISIBLE);
        checkGamesMULTI.setAnimation(fadeInSlower);

        googlePlusMULTI.clearAnimation();
        googlePlusMULTI.setVisibility(View.VISIBLE);
        googlePlusMULTI.setAnimation(fadeInSlower);

        progressBarPlayMULTI.setVisibility(View.VISIBLE);
        progressBarPlayMULTI.setAnimation(fadeInSlower);

        progressBarCheckMULTI.setVisibility(View.VISIBLE);
        progressBarCheckMULTI.setAnimation(fadeInSlower);

        progressGooglePLus.setVisibility(View.VISIBLE);
        progressGooglePLus.setAnimation(fadeInSlower);
    }

//        }.start();
//    }


    @Override
    public void onBackPressed() {

        boolean isPressed = false;

        if (is2PhoneOpen) {

            logo.setVisibility(View.VISIBLE);
            setSlideUP();

            playMULTI.clearAnimation();
            playMULTI.setVisibility(View.GONE);
            progressBarPlayMULTI.clearAnimation();
            progressBarPlayMULTI.setVisibility(View.GONE);

            googlePlusMULTI.clearAnimation();
            googlePlusMULTI.setVisibility(View.GONE);
            progressGooglePLus.clearAnimation();
            progressGooglePLus.setVisibility(View.GONE);

            checkGamesMULTI.clearAnimation();
            checkGamesMULTI.setVisibility(View.GONE);
            progressBarCheckMULTI.clearAnimation();
            progressBarCheckMULTI.setVisibility(View.GONE);

            progressBar.setVisibility(View.VISIBLE);
            progressBar.startAnimation(fadeIn);
            onePhone.setVisibility(View.VISIBLE);
            onePhone.startAnimation(fadeIn);

            progressBarMulti.setVisibility(View.VISIBLE);
            progressBarMulti.setAnimation(fadeIn);
            twoPhones.setVisibility(View.VISIBLE);
            twoPhones.startAnimation(fadeIn);

            progressBarSett.setVisibility(View.VISIBLE);
            progressBarSett.setAnimation(fadeIn);
            settings.setVisibility(View.VISIBLE);
            settings.startAnimation(fadeIn);

            is2PhoneOpen = false;
//            Intent intent = getIntent();
//            finish();
//            startActivity(intent);

        } else {

            if(isPressed) {
                super.onBackPressed();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setMessage("Press back one more time to exit");

                AlertDialog dialog = builder.create();
                dialog.show();
                isPressed = true;
            }
        }
    }


}