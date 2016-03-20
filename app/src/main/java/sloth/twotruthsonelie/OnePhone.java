package sloth.twotruthsonelie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class OnePhone extends Activity {

    EditText firstS, secondS, thirdS;
    String fString, sString, tString;
    Snackbar snackbarEmpty2, snackbarEmpty;
    Boolean first, second, third;
    TextView player1TW, player2TW;
    Animation fade_in;
    Button proceed;
    Integer current_round, player1, player2;
    CheckBox firstTruth, firstLie;
    CheckBox secondTruth, secondLie;
    CheckBox thirdTruth, thirdLie;
    Integer height;
    MainActivity main = new MainActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        height = size.y;

//        if (height > 800) {
        setContentView(R.layout.one_phone);
//        } else {
//            setContentView(R.layout.one_phone_small);
//        }

        firstS = (EditText) findViewById(R.id.firstS);
        secondS = (EditText) findViewById(R.id.secondS);
        thirdS = (EditText) findViewById(R.id.thirdS);

        fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        player1TW = (TextView) findViewById(R.id.player1);
        player2TW = (TextView) findViewById(R.id.player2);

        proceed = (Button) findViewById(R.id.proceed);


        SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);

        current_round = currentR.getInt("currentR", 0);
        if ((current_round % 2) == 0) {
            player1TW.setVisibility(View.VISIBLE);
            player1TW.setAnimation(fade_in);
        } else {
            player2TW.setVisibility(View.VISIBLE);
            player2TW.setAnimation(fade_in);
        }

        /////////////////////////FIRST////////////////////////////

        firstS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                secondLie.setChecked(false);
                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);

                thirdLie.setChecked(false);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);

                firstLie.setChecked(true);
                firstS.setBackgroundResource(R.drawable.custom_edittex_lie);
                firstTruth.setChecked(false);

                editor.putBoolean("firstLie", false).apply();
                editor.putBoolean("secondLie", true).apply();
                editor.putBoolean("thirdLie", true).apply();

                return true;
            }
        });


        firstLie = (CheckBox) findViewById(R.id.firstLie);
        firstLie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                secondLie.setChecked(false);
                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);

                thirdLie.setChecked(false);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);

                firstLie.setChecked(true);
                firstS.setBackgroundResource(R.drawable.custom_edittex_lie);
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
                firstS.setBackgroundResource(R.drawable.custom_edittext_truth);
                firstLie.setChecked(false);
            }
        });

        /////////////////////////SECOND//////////////////////////////

        secondS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                firstLie.setChecked(false);
                firstS.setBackgroundResource(R.drawable.custom_edittext_truth);

                thirdLie.setChecked(false);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);

                secondLie.setChecked(true);
                secondS.setBackgroundResource(R.drawable.custom_edittex_lie);
                secondTruth.setChecked(false);

                editor.putBoolean("secondLie", false).apply();
                editor.putBoolean("firstLie", true).apply();
                editor.putBoolean("thirdLie", true).apply();
                return true;
            }
        });

        secondLie = (CheckBox) findViewById(R.id.secondLie);
        secondLie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                firstLie.setChecked(false);
                firstS.setBackgroundResource(R.drawable.custom_edittext_truth);

                thirdLie.setChecked(false);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);

                secondLie.setChecked(true);
                secondS.setBackgroundResource(R.drawable.custom_edittex_lie);
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
                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);
                secondLie.setChecked(false);
            }
        });

        /////////////////////////THIRD/////////////////////////////////

        thirdS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                firstLie.setChecked(false);
                firstS.setBackgroundResource(R.drawable.custom_edittext_truth);

                secondLie.setChecked(false);
                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);

                thirdLie.setChecked(true);
                thirdS.setBackgroundResource(R.drawable.custom_edittex_lie);
                thirdTruth.setChecked(false);

                editor.putBoolean("thirdLie", false).apply();
                editor.putBoolean("secondLie", true).apply();
                editor.putBoolean("firstLie", true).apply();

                return true;
            }
        });

        thirdLie = (CheckBox) findViewById(R.id.thirdLie);
        thirdLie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                firstLie.setChecked(false);
                firstS.setBackgroundResource(R.drawable.custom_edittext_truth);

                secondLie.setChecked(false);
                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);

                thirdLie.setChecked(true);
                thirdS.setBackgroundResource(R.drawable.custom_edittex_lie);
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


                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);

                snackbarEmpty = Snackbar.make(findViewById(android.R.id.content), "Don't leave the spaces blank", Snackbar.LENGTH_INDEFINITE);
                snackbarEmpty2 = Snackbar.make(findViewById(android.R.id.content), "Choose a lie", Snackbar.LENGTH_INDEFINITE);

                first = preferences.getBoolean("fristLie", false);
                second = preferences.getBoolean("secondLie", false);
                third = preferences.getBoolean("thirdLie", false);

                fString = firstS.getText().toString();
                sString = secondS.getText().toString();
                tString = thirdS.getText().toString();

                Intent nextPlayer = new Intent(OnePhone.this, NextPlayer.class);
                if (fString.matches("") || sString.matches("") || tString.matches("")) {
                    snackbarEmpty.setActionTextColor(getResources().getColor(R.color.purple_ish));
                    snackbarEmpty.setDuration(Snackbar.LENGTH_SHORT);
                    snackbarEmpty.show();
                } else if (!first && !second && !third) {
                    snackbarEmpty2.setDuration(Snackbar.LENGTH_SHORT);
                    snackbarEmpty2.show();
                } else {
                    Toast.makeText(getApplicationContext(), " " + first + " " + second + " " + third, Toast.LENGTH_LONG).show();
                    nextPlayer.putExtra("firstS", firstS.getText().toString());
                    nextPlayer.putExtra("secondS", secondS.getText().toString());
                    nextPlayer.putExtra("thirdS", thirdS.getText().toString());
                    startActivity(nextPlayer);
                    finish();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

        AlertDialog.Builder cancleGame = new AlertDialog.Builder(OnePhone.this);
        cancleGame.setMessage("Quit Game?");
        cancleGame.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
                SharedPreferences.Editor editor = points.edit();
                editor.clear().apply();

                SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = currentR.edit();
                editor1.clear().apply();

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = preferences.edit();
                editor2.clear().apply();

                Intent intent = new Intent(OnePhone.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        });

        cancleGame.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog dialog = cancleGame.create();
        dialog.show();

    }
}
