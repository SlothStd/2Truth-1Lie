package sloth.twotruthsonelie;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * Created by Daniel on 1/28/2016.
 */
public class ScoreActivity extends Activity {

    TextView player1TW, player2TW, winner;
    Integer player1, player2;
    Button toMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_layout);

        player1TW = (TextView) findViewById(R.id.player1);
        player2TW = (TextView) findViewById(R.id.player2);

        ProgressBar player1Level = (ProgressBar) findViewById(R.id.levelPlayer1);
        ProgressBar player2Level = (ProgressBar) findViewById(R.id.levelPlayer2);
        ProgressBar player1Progress = (ProgressBar) findViewById(R.id.player1Graph);
        ProgressBar player2Progress = (ProgressBar) findViewById(R.id.player2Graph);

        final ObjectAnimator animation = ObjectAnimator.ofInt(player1Progress, "progress", 0, 100);
        animation.setDuration(900); //in milliseconds bruv
        animation.setInterpolator(new DecelerateInterpolator());

        SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);

        player1 = points.getInt("player1", 0);
        player2 = points.getInt("player2", 0);
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
}
