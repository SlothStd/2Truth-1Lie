package sloth.twotruthsonelie;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_layout);

        setUpGraph();

        player1TW = (TextView) findViewById(R.id.player1TV);
        player2TW = (TextView) findViewById(R.id.player2TV);

        progressBarLeft = (ProgressBar) findViewById(R.id.leftProgress);
        progressBarRight = (ProgressBar) findViewById(R.id.rightProgress);

        percentageLeft = (TextView) findViewById(R.id.leftProgressPercentage);
        percentageRight = (TextView) findViewById(R.id.rightProgressPercentage);

        SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
        SharedPreferences.Editor editor = points.edit();

        final SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        roundsS = SP.getString("setRounds", "0");
        round = Integer.parseInt(roundsS);
        round = round * 10000;
        Toast.makeText(ScoreActivity.this, "rounds" + round, Toast.LENGTH_SHORT).show();

        player1 = points.getInt("player1", 0) * 10000;
        Toast.makeText(ScoreActivity.this, "player1 " + player1, Toast.LENGTH_SHORT).show();
        player2 = points.getInt("player2", 0) * 10000;
        Toast.makeText(ScoreActivity.this, "player2 " + player2, Toast.LENGTH_SHORT).show();

        percentage2 = (double) player2 / round;
        percentage1 = (double) player1 / round;

        percentage2 =  percentage2*100;
        percentage1 =  percentage1*100;

        int percentageInt2 = (int) percentage2;
        int percentageInt1 = (int) percentage1;

        percentageLeft.setText(String.valueOf(percentageInt1) + "%");
        percentageRight.setText(String.valueOf(percentageInt2) + "%");

        Toast.makeText(ScoreActivity.this, "firstPercentage " + percentage1, Toast.LENGTH_SHORT).show();
        Toast.makeText(ScoreActivity.this, "secondPercentage " + percentage2, Toast.LENGTH_SHORT).show();

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

    public void setUpGraph(){
        SharedPreferences prefs = getSharedPreferences("SingleGraphHistory", Context.MODE_PRIVATE);
        if (!prefs.getString("GraphHistory", "err").equals("err")) {
            Log.d("GraphHistory", "good");
            graphHistory = new GraphHistory(prefs.getString("GraphHistory", null), "single");
        } else {
            Log.d("GraphHistory", "bad");
            graphHistory = new GraphHistory("single");
        }

        GraphView graph = (GraphView) findViewById(R.id.graphSP);
        graph.removeAllSeries();

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(graphHistory.getMyDataPoints());
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(graphHistory.getHisDataPoints());

        series1.setColor(getResources().getColor(R.color.truth));
        series2.setColor(getResources().getColor(R.color.lie));

        graph.addSeries(series1);
        graph.addSeries(series2);
    }

    public void resetGraph(){
        getSharedPreferences("SingleGraphHistory", Context.MODE_PRIVATE).edit().clear().apply();
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

        resetGraph();

        Intent toMenu = new Intent(ScoreActivity.this, MainActivity.class);
        ScoreActivity.this.finish();
        startActivity(toMenu);
    }

    @Override
    protected void onStop() {
        resetGraph();
        super.onStop();
    }
}
