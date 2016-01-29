package sloth.twotruthsonelie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
        setContentView(R.layout.score_layout);

        player1TW = (TextView) findViewById(R.id.player1);
        player2TW = (TextView) findViewById(R.id.player2);
        winner = (TextView) findViewById(R.id.winner);

        SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);

        player1 = points.getInt("player1", 0);
        player2 = points.getInt("player2", 0);
        player1TW.setText(String.valueOf(player1));
        player2TW.setText(String.valueOf(player2));

        if (player1 > player2) {
            winner.setText("PLAYER 1 WON!");
        } else if (player1 < player2){
            winner.setText("PLAYER 2 WON!");
        } else {
            winner.setText("IT'S A TIE!");
        }

        toMenu = (Button) findViewById(R.id.toMenu);
        toMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
                SharedPreferences.Editor pointsEditor = points.edit();

                SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);
                SharedPreferences.Editor currentEditor = currentR.edit();

                Intent toMenu = new Intent(ScoreActivity.this, MainActivity.class);
                currentEditor.clear().apply();
                pointsEditor.clear().apply();
                startActivity(toMenu);
                finish();

            }
        });

        SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear().apply();
    }


    @Override
    public void onBackPressed() {
        SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
        SharedPreferences.Editor pointsEditor = points.edit();

        SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);
        SharedPreferences.Editor currentEditor = currentR.edit();

        Intent toMenu = new Intent(ScoreActivity.this, MainActivity.class);
        currentEditor.clear().apply();
        pointsEditor.clear().apply();
        startActivity(toMenu);
        finish();
    }
}
