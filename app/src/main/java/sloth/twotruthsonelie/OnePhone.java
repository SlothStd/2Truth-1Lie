package sloth.twotruthsonelie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

public class OnePhone extends Activity implements View.OnKeyListener {

    EditText firstS, secondS, thirdS;
    String fString, sString, tString;
    Snackbar snackbarEmpty2, snackbarEmpty;
    Boolean first, second, third;
    TextView proceed, player1points, player2points, playerOneName, playerTwoName;
    Animation fade_in;
    Integer current_round;
    CheckBox firstTruth, firstLie;
    CheckBox secondTruth, secondLie;
    CheckBox thirdTruth, thirdLie;
    Integer height;
    LinearLayout.LayoutParams params;
    LinearLayout linearLayout;
    GraphHistory graphHistory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.one_phone);

        player1points = (TextView) findViewById(R.id.player1points);
        player2points = (TextView) findViewById(R.id.player2points);

        playerOneName = (TextView) findViewById(R.id.player1TV);
        playerTwoName = (TextView) findViewById(R.id.player2TV);

        SharedPreferences prefs = getSharedPreferences("playerNames", MODE_PRIVATE);
        try {
            playerOneName.setText(prefs.getString("playerOneName", null));
        } catch (NullPointerException e) {
            playerOneName.setText("Player 1");
        }
        try {
            playerTwoName.setText(prefs.getString("playerTwoName", null));
        } catch (NullPointerException e) {
            playerTwoName.setText("Player 2");
        }

        SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
        int player1 = points.getInt("player1", 0);
        int player2 = points.getInt("player2", 0);

        try {
            player1points.setText(String.valueOf(player1));
        } catch (NullPointerException e) {
            player1points.setText("0");
        }

        try {
            player2points.setText(String.valueOf(player2));
        } catch (NullPointerException e) {
            player2points.setText("0");
        }

        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.KITKAT) {
            (findViewById(R.id.gameInfo)).setPadding(0, 30, 0, 0);
        }

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        height = size.y;

        graphHistory = new GraphHistory("single");

        firstS = (EditText) findViewById(R.id.firstS);
        secondS = (EditText) findViewById(R.id.secondS);
        thirdS = (EditText) findViewById(R.id.thirdS);

        findViewById(R.id.firstS).setOnKeyListener(this);
        findViewById(R.id.secondS).setOnKeyListener(this);
        findViewById(R.id.thirdS).setOnKeyListener(this);

        fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slower);

        proceed = (TextView) findViewById(R.id.proceed);


        SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);

        /////////////////////////FIRST////////////////////////////

        firstS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                proceed.setVisibility(View.VISIBLE);
                proceed.setAnimation(fade_in);

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                secondLie.setChecked(false);
                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);
                secondS.setTextColor(getResources().getColor(R.color.black));

                thirdLie.setChecked(false);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);
                thirdS.setTextColor(getResources().getColor(R.color.black));

                firstLie.setChecked(true);
                firstS.setBackgroundResource(R.drawable.custom_edittex_lie);
                firstS.setTextColor(getResources().getColor(R.color.white));
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
                secondS.setTextColor(getResources().getColor(R.color.black));

                thirdLie.setChecked(false);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);
                thirdS.setTextColor(getResources().getColor(R.color.black));

                firstLie.setChecked(true);
                firstS.setBackgroundResource(R.drawable.custom_edittex_lie);
                firstS.setTextColor(getResources().getColor(R.color.white));
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

                proceed.setVisibility(View.VISIBLE);
                proceed.setAnimation(fade_in);

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                firstLie.setChecked(false);
                firstS.setBackgroundResource(R.drawable.custom_edittext_truth);
                firstS.setTextColor(getResources().getColor(R.color.black));

                thirdLie.setChecked(false);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);
                thirdS.setTextColor(getResources().getColor(R.color.black));

                secondLie.setChecked(true);
                secondS.setBackgroundResource(R.drawable.custom_edittex_lie);
                secondS.setTextColor(getResources().getColor(R.color.white));
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
                firstS.setTextColor(getResources().getColor(R.color.black));

                thirdLie.setChecked(false);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);
                thirdS.setTextColor(getResources().getColor(R.color.black));

                secondLie.setChecked(true);
                secondS.setBackgroundResource(R.drawable.custom_edittex_lie);
                secondS.setTextColor(getResources().getColor(R.color.white));
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

                proceed.setVisibility(View.VISIBLE);
                proceed.setAnimation(fade_in);

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                firstLie.setChecked(false);
                firstS.setBackgroundResource(R.drawable.custom_edittext_truth);
                firstS.setTextColor(getResources().getColor(R.color.black));

                secondLie.setChecked(false);
                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);
                secondS.setTextColor(getResources().getColor(R.color.black));

                thirdLie.setChecked(true);
                thirdS.setBackgroundResource(R.drawable.custom_edittex_lie);
                thirdS.setTextColor(getResources().getColor(R.color.white));
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
                firstS.setTextColor(getResources().getColor(R.color.black));

                secondLie.setChecked(false);
                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);
                secondS.setTextColor(getResources().getColor(R.color.black));

                thirdLie.setChecked(true);
                thirdS.setBackgroundResource(R.drawable.custom_edittex_lie);
                thirdS.setTextColor(getResources().getColor(R.color.white));
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

                    snackbarEmpty.setActionTextColor(getResources().getColor(R.color.white));
                    snackbarEmpty.getView().setBackgroundColor(getResources().getColor(R.color.blue));
                    View view = snackbarEmpty.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snackbarEmpty.setDuration(Snackbar.LENGTH_SHORT);
                    snackbarEmpty.show();

                } else if (!first && !second && !third) {

                    snackbarEmpty2.setActionTextColor(getResources().getColor(R.color.white));
                    snackbarEmpty2.getView().setBackgroundColor(getResources().getColor(R.color.coolRed));
                    View view = snackbarEmpty2.getView();
                    TextView tv = (TextView) view.findViewById(android.support.design.R.id.snackbar_text);
                    tv.setTextColor(Color.WHITE);
                    snackbarEmpty2.setDuration(Snackbar.LENGTH_SHORT);
                    snackbarEmpty2.show();

                } else {

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


        final CustomDialog custom_dialog = new CustomDialog();
        custom_dialog.showDiaolg(OnePhone.this, "Exit", "Cancle", "Are you sure you want to exit? Your current game session will end.");
        custom_dialog.positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                custom_dialog.dialog.dismiss();
                SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
                SharedPreferences.Editor editor = points.edit();
                editor.clear().apply();

                SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);
                SharedPreferences.Editor editor1 = currentR.edit();
                editor1.clear().apply();

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor editor2 = preferences.edit();
                editor2.clear().apply();

                resetGraph();

                Intent intent = new Intent(OnePhone.this, MainActivity.class);
                finish();
                startActivity(intent);

            }
        });

    }

    public void resetGraph(){
        getSharedPreferences("SingleGraphHistory", Context.MODE_PRIVATE).edit().clear().apply();
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        if ((event.getAction() == KeyEvent.ACTION_DOWN)
                && (keyCode == KeyEvent.KEYCODE_ENTER)) {

            switch (v.getId()) {
                case R.id.firstS:

                    findViewById(R.id.firstS).clearFocus();
                    findViewById(R.id.secondS).requestFocus();

                    return true;

                case R.id.secondS:

                    findViewById(R.id.secondS).clearFocus();
                    findViewById(R.id.thirdS).requestFocus();

                    return true;

                case R.id.thirdS:

                    findViewById(R.id.thirdS).clearFocus();
                    findViewById(R.id.firstS).requestFocus();

                    return true;
            }
        }

        return false;
    }
}
