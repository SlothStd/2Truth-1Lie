package sloth.twotruthsonelie;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

/**
 * Created by Daniel on 1/28/2016.
 */
public class ScoreActivity extends Activity {

    TextView player1Level, player2Level, playerOnePoints,
             playerTwoPoints, player1Name, player2Name;
    Integer player1, player2;
    ImageView scoreboardBckg, circularBckg1, circularBckg2;
    ProgressBar player1Progress, player2Progress;
    ObjectAnimator animation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.KITKAT) {

        } else {

            requestWindowFeature(Window.FEATURE_NO_TITLE);
            this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        }

        setContentView(R.layout.scoreboard_layout);

        scoreboardBckg = (ImageView) findViewById(R.id.score_board_bg);
        circularBckg2 = (ImageView) findViewById(R.id.circle_bckg2);
        circularBckg1 = (ImageView) findViewById(R.id.circle_bckg1);

        player1Progress = (ProgressBar) findViewById(R.id.player1_progress);
        player2Progress = (ProgressBar) findViewById(R.id.player2_progress);

        player1Name = (TextView) findViewById(R.id.player1SBname);
        player2Name = (TextView) findViewById(R.id.player2SBname);
        SharedPreferences prefs = getSharedPreferences("playerNames", MODE_PRIVATE);
            player1Name.setText(prefs.getString("playerOneName", "Player 1"));
            player2Name.setText(prefs.getString("playerTwoName", "Player 2"));

        player1Level = (TextView) findViewById(R.id.player1_level);
        player2Level = (TextView) findViewById(R.id.player2_level);

        playerOnePoints = (TextView) findViewById(R.id.playerOnePoints);
        playerTwoPoints = (TextView) findViewById(R.id.playerTwoPoints);


        float rotation1 = player1Progress.getRotation();
        player1Progress.setRotation(rotation1 + 40);
        float rotation2 = player2Progress.getRotation();
        player2Progress.setRotation(rotation2 + 40);

        if (hasSoftKeys()) {

            final float scale = getResources().getDisplayMetrics().density;
            int top = (int) (24 * scale + 0.5f);
            int bottom = (int) (48 * scale + 0.5f);

            findViewById(R.id.scores_main).setPadding(0, top, 0, bottom);
        } else {
            findViewById(R.id.scores_main).setPadding(0, 0, 0, 0);
        }


        ///////////////////////////////////TOTOOOOOOOO a DOLE////////////////////////////////
        SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);

        player1 = points.getInt("player1", 0);
        player2 = points.getInt("player2", 0);

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String roundsPrefs = SP.getString("setRounds", "0");
        int rounds = Integer.parseInt(roundsPrefs);

        double playerOne = (double) player1 / rounds;
        double playerTwo = (double) player2 / rounds;

        playerOne = playerOne * 100;
        playerTwo = playerTwo * 100;

        int playerOnePercentage = (int) playerOne;
        int playerTwoPercentage = (int) playerTwo;

        try {
            player1Level.setText(playerOnePercentage + "%");
        } catch (NullPointerException e) {
            player1Level.setText("0%");
        }
        try {
            player2Level.setText(playerTwoPercentage + "%");
        } catch (NullPointerException e) {
            player2Level.setText("0%");
        }


        if (player1 == player2) {
            scoreboardBckg.setImageResource(R.drawable.scoreboard_green);
            circularBckg2.setImageResource(R.drawable.green_circle_bckg);
        }

        if (player2 > player1) {
            float rotation = scoreboardBckg.getRotation();
            scoreboardBckg.setRotation(rotation + 180);
            circularBckg1.setImageResource(R.drawable.red_circle_bckg);
            circularBckg2.setImageResource(R.drawable.green_circle_bckg);
        }

        try {
            playerOnePoints.setText(String.valueOf(player1));
            if (player1 == 1) {
                TextView textView1 = (TextView)findViewById(R.id.points1);
                textView1.setText("point");
            }
        } catch (NullPointerException e) {
            playerOnePoints.setText("0");
        }
        try {
            playerTwoPoints.setText(String.valueOf(player2));
            if (player2 == 1) {
                TextView textView2 = (TextView)findViewById(R.id.points2);
                textView2.setText("point");
            }
        } catch (NullPointerException e) {
            playerTwoPoints.setText("0");
        }

        player1Progress.setMax(rounds * 10000);
        player1Progress.setProgress(0);
        player2Progress.setMax(rounds * 10000);
        player2Progress.setProgress(0);

        //tu si dopln int na levelprogress do progressbarov (to uz mas nastavene iba tieto inty si uprav)
        final int player1LevelProgress = player1 * 10000; // krat 10k kvoli smooth animacii
        final int player2LevelProgress = player2 * 10000;

        CountDownTimer timer = new CountDownTimer(1300, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                animation = new ObjectAnimator().ofInt(player1Progress, "progress", 0, player1LevelProgress);
                animation.setDuration(2000);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();
            }
        }.start();

        CountDownTimer timer2 = new CountDownTimer(1300, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                animation = new ObjectAnimator().ofInt(player2Progress, "progress", 0, player2LevelProgress);
                animation.setDuration(2000);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();
            }
        }.start();

        ////////////////////////////////////////////POTIALTO///////////////////////////


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
