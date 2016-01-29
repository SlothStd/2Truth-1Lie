package sloth.twotruthsonelie;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

public class OnePhone extends Activity {

    EditText firstS, secondS, thirdS;
    Snackbar snackbar;
    Button proceed;
    Integer current_round, player1, player2;
    CheckBox firstTruth, firstLie;
    CheckBox secondTruth, secondLie;
    CheckBox thirdTruth, thirdLie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_phone);

        firstS = (EditText) findViewById(R.id.firstS);
        secondS = (EditText) findViewById(R.id.secondS);
        thirdS = (EditText) findViewById(R.id.thirdS);

        proceed = (Button) findViewById(R.id.proceed);

        SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);
        SharedPreferences.Editor currentEditor = currentR.edit();

        current_round = currentR.getInt("currentR", 0);
        if ( (current_round % 2) == 0) {
            snackbar = Snackbar.make(findViewById(android.R.id.content), "player1", Snackbar.LENGTH_INDEFINITE);
            snackbar.setDuration(Snackbar.LENGTH_SHORT);
            snackbar.show();
        } else {
            snackbar = Snackbar.make(findViewById(android.R.id.content), "player2", Snackbar.LENGTH_INDEFINITE);
            snackbar.setDuration(Snackbar.LENGTH_SHORT);
            snackbar.show();
        }

        /////////////////////////FIRST////////////////////////////

        firstLie = (CheckBox) findViewById(R.id.firstLie);
        firstLie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                secondLie.setChecked(false);
                secondS.setBackgroundColor(getResources().getColor(R.color.truth));

                thirdLie.setChecked(false);
                thirdS.setBackgroundColor(getResources().getColor(R.color.truth));

                firstLie.setChecked(true);
                firstS.setBackgroundColor(getResources().getColor(R.color.lie));
                firstTruth.setChecked(false);

                editor.putBoolean("firstLie", false).apply();
                editor.putBoolean("secondLie", true).apply();
                editor.putBoolean("thirdLie", true).apply();
            }
        });

        firstTruth = (CheckBox) findViewById(R.id.firstTruth);
        firstTruth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstTruth.setChecked(true);
                firstS.setBackgroundColor(getResources().getColor(R.color.truth));
                firstLie.setChecked(false);
            }
        });

        /////////////////////////SECOND//////////////////////////////

        secondLie = (CheckBox) findViewById(R.id.secondLie);
        secondLie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                firstLie.setChecked(false);
                firstS.setBackgroundColor(getResources().getColor(R.color.truth));

                thirdLie.setChecked(false);
                thirdS.setBackgroundColor(getResources().getColor(R.color.truth));

                secondLie.setChecked(true);
                secondS.setBackgroundColor(getResources().getColor(R.color.lie));
                secondTruth.setChecked(false);

                editor.putBoolean("secondLie", false).apply();
                editor.putBoolean("firstLie", true).apply();
                editor.putBoolean("thirdLie", true).apply();
            }
        });

        secondTruth = (CheckBox) findViewById(R.id.secondTruth);
        secondTruth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                secondTruth.setChecked(true);
                secondS.setBackgroundColor(getResources().getColor(R.color.truth));
                secondLie.setChecked(false);
            }
        });

        /////////////////////////THIRD/////////////////////////////////

        thirdLie = (CheckBox) findViewById(R.id.thirdLie);
        thirdLie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                firstLie.setChecked(false);
                firstS.setBackgroundColor(getResources().getColor(R.color.truth));

                secondLie.setChecked(false);
                secondS.setBackgroundColor(getResources().getColor(R.color.truth));

                thirdLie.setChecked(true);
                thirdS.setBackgroundColor(getResources().getColor(R.color.lie));
                thirdTruth.setChecked(false);

                editor.putBoolean("thirdLie", false).apply();
                editor.putBoolean("secondLie", true).apply();
                editor.putBoolean("firstLie", true).apply();
            }
        });

        thirdTruth = (CheckBox) findViewById(R.id.thirdTruth);
        thirdTruth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thirdTruth.setChecked(true);
                thirdS.setBackgroundColor(getResources().getColor(R.color.truth));
                thirdLie.setChecked(false);
            }
        });

        ////////////////////////////BUTTON//////////////////////////////////

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent nextPlayer = new Intent(OnePhone.this, NextPlayer.class);
                    nextPlayer.putExtra("firstS", firstS.getText().toString());
                    nextPlayer.putExtra("secondS", secondS.getText().toString());
                    nextPlayer.putExtra("thirdS", thirdS.getText().toString());
                startActivity(nextPlayer);
                finish();
            }
        });

    }

}
