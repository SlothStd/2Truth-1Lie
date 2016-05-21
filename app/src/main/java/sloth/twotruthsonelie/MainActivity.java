package sloth.twotruthsonelie;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
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

    Button onePhone, twoPhones, powerups, settings;

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

        if (getIntent().getFlags() == 0x14000000){
            Intent multi = new Intent(MainActivity.this, MpWifi.class);
            startActivity(multi);
        }

        onePhone = (Button) findViewById(R.id.Onephone);
        twoPhones = (Button) findViewById(R.id.Twophones);
        settings = (Button) findViewById(R.id.Settings);
        powerups = (Button) findViewById(R.id.powerups);
        logo = (ImageView) findViewById(R.id.appNameLogo);

        ////////////////////////Buttony namiesto WIFI → Activita////////////////

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



        progressBar.setVisibility(View.VISIBLE);
//        progressBar.startAnimation(fadeIn);
        onePhone.setVisibility(View.VISIBLE);
//        onePhone.startAnimation(fadeIn);
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

        progressBarMulti.setVisibility(View.VISIBLE);
//        progressBarMulti.setAnimation(fadeIn);
        twoPhones.setVisibility(View.VISIBLE);
//        twoPhones.startAnimation(fadeIn);
        twoPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent multiplayer = new Intent(MainActivity.this, MpWifi.class);
                MainActivity.this.finish();
                startActivity(multiplayer);

            }
        });


//        settings.startAnimation(fadeIn);
        progressBarSett.setVisibility(View.VISIBLE);
        settings.setVisibility(View.VISIBLE);
//        progressBarSett.setAnimation(fadeIn);
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


    @Override
    public void onBackPressed() {

        MainActivity.this.finish();
    }
}
