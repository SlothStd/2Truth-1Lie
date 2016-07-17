package sloth.twotruthsonelie;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
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
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scoreboard_layout);

        params.setMargins(0, 20, 0, 0);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.KITKAT) {
            findViewById(R.id.graphSP).setLayoutParams(params);
        } else {
        }

        if (hasSoftKeys()){

            final float scale = getResources().getDisplayMetrics().density;
            int top = (int) (24 * scale + 0.5f);
            int bottom = (int) (48 * scale + 0.5f);

            findViewById(R.id.scores_main).setPadding(0, top, 0, bottom);
        }else {
            findViewById(R.id.scores_main).setPadding(0, 0, 0, 0);
        }

        if (getIntent().getBooleanExtra("custom", false)){
            customGraph();
            return;
        }

        setUpGraph();

        player1TW = (TextView) findViewById(R.id.player1TV);
        player1TW.setTextColor(getResources().getColor(R.color.blue));
        player2TW = (TextView) findViewById(R.id.player2TV);
        player2TW.setTextColor(getResources().getColor(R.color.white));

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

    public void setUpGraph(){
        SharedPreferences prefs = getSharedPreferences("SingleGraphHistory", Context.MODE_PRIVATE);
        if (!prefs.getString("GraphHistory", "err").equals("err")) {
            graphHistory = new GraphHistory(prefs.getString("GraphHistory", null), "single");
        } else {
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

        series1.setTitle("Player 1");
        series2.setTitle("Player 2");

        series1.setThickness(10);
        series2.setThickness(10);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setFixedPosition(0, 0);
        graph.getLegendRenderer().setSpacing(20);
        graph.getLegendRenderer().setBackgroundColor(getResources().getColor(android.R.color.transparent));

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setMaxY(2);
        graph.getViewport().setMinY(0);

        graph.getViewport().setMaxX(2);
        graph.getViewport().setMinX(0);

        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getGridLabelRenderer().setNumVerticalLabels(3);

        graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(android.R.color.white));
        graph.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(android.R.color.white));

        /* ? */ graph.getGridLabelRenderer().setGridColor(getResources().getColor(android.R.color.white));

        graph.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX && value == 0)
                    return "";
                else
                    return String.valueOf(value).substring(0, 1);
            }

            @Override
            public void setViewport(Viewport viewport) {
            }
        });
    }

    public void customGraph(){
        GraphView graph = (GraphView) findViewById(R.id.graphSP);
        graph.removeAllSeries();

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(1, 1),
                new DataPoint(2, 2)
        });
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(0, 0),
                new DataPoint(1, 0),
                new DataPoint(2, 1)
        });

        series1.setColor(getResources().getColor(R.color.blue));
        series2.setColor(getResources().getColor(R.color.white));

        graph.addSeries(series1);
        graph.addSeries(series2);

        series1.setTitle("Player 1");
        series2.setTitle("Player 2");

        series1.setThickness(10);
        series2.setThickness(10);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setFixedPosition(0, 0);
        graph.getLegendRenderer().setSpacing(20);
        graph.getLegendRenderer().setBackgroundColor(getResources().getColor(android.R.color.transparent));

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setMaxY(2);
        graph.getViewport().setMinY(0);

        graph.getViewport().setMaxX(2);
        graph.getViewport().setMinX(0);

        graph.getGridLabelRenderer().setNumHorizontalLabels(3);
        graph.getGridLabelRenderer().setNumVerticalLabels(3);

        graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(android.R.color.white));
        graph.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(android.R.color.white));

        /* ? */ graph.getGridLabelRenderer().setGridColor(getResources().getColor(android.R.color.white));

        graph.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (!isValueX && value == 0)
                    return "";
                else
                    return String.valueOf(value).substring(0, 1);
            }

            @Override
            public void setViewport(Viewport viewport) {
            }
        });
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
