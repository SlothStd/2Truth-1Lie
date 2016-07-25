package sloth.twotruthsonelie;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Daniel on 1/28/2016.
 */
public class ScoreActivity extends Activity {

    TextView player1TW, player2TW, percentageLeft, percentageRight;
    Integer player1, player2, round;
    double percentage1, percentage2;
    String roundsS;
    ProgressBar progressBarLeft, progressBarRight;
    CountDownTimer timer;
    ObjectAnimator animation;
    GraphHistory graphHistory;
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_layout);


        if (hasSoftKeys()){

            final float scale = getResources().getDisplayMetrics().density;
            int top = (int) (24 * scale + 0.5f);
            int bottom = (int) (48 * scale + 0.5f);

            findViewById(R.id.scores_main).setPadding(0, top, 0, bottom);
        }else {
            findViewById(R.id.scores_main).setPadding(0, 0, 0, 0);
        }

        player1TW = (TextView) findViewById(R.id.player1TV);
        player1TW.setTextColor(getResources().getColor(R.color.blue));
        player2TW = (TextView) findViewById(R.id.player2TV);
        player2TW.setTextColor(getResources().getColor(R.color.white));

        /*progressBarLeft = (ProgressBar) findViewById(R.id.leftProgress);
        progressBarRight = (ProgressBar) findViewById(R.id.rightProgress);

        percentageLeft = (TextView) findViewById(R.id.leftProgressPercentage);
        percentageRight = (TextView) findViewById(R.id.rightProgressPercentage);*/

        SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
        SharedPreferences.Editor editor = points.edit();

        final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        roundsS = SP.getString("setRounds", "0");
        round = Integer.parseInt(roundsS);
        round = round * 10000;

        player1 = points.getInt("player1", 0) * 10000;
        player2 = points.getInt("player2", 0) * 10000;

        percentage2 = (double) player2 / round;
        percentage1 = (double) player1 / round;

        percentage2 =  percentage2*100;
        percentage1 =  percentage1*100;

        int percentageInt2 = (int) percentage2;
        int percentageInt1 = (int) percentage1;

        try {
            percentageLeft.setText(String.valueOf(percentageInt1) + "%");
        } catch (NullPointerException e) {
            percentageLeft.setText("0%");
        }

        try {
            percentageRight.setText(String.valueOf(percentageInt2) + "%");
        } catch (NullPointerException e ) {
            percentageRight.setText("0%");
        }


        progressBarRight.setMax(round);
        progressBarLeft.setMax(round);

        timer = new CountDownTimer(500, 500) {
            @Override
            public void onTick(long millisUntilFinished) {


            }

            @Override
            public void onFinish() {

                animation = ObjectAnimator.ofInt(progressBarLeft, "progress", 0, player1);
                animation.setDuration(3000);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();

                animation = ObjectAnimator.ofInt(progressBarRight, "progress", 0, player2);
                animation.setDuration(3000);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
        SharedPreferences.Editor pointsEditor = points.edit();
        pointsEditor.clear().apply();

        SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);
        SharedPreferences.Editor currentEditor = currentR.edit();
        currentEditor.clear().apply();

        SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();

        Intent toMenu = new Intent(ScoreActivity.this, MainActivity.class);
        ScoreActivity.this.finish();
        startActivity(toMenu);
    }

    public boolean hasSoftKeys(){
        boolean hasSoftwareKeys = true;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            Display d = this.getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys =  (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        }else{
            boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }
}
