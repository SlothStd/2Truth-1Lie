package sloth.twotruthsonelie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class NextPlayer extends Activity {

    String firstS, secondS, thirdS;
    TextView firstTW, secondTW, thirdTW;
    Button switchPlayer;
    Integer round, current_round;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_player_layout);

        firstTW = (TextView) findViewById(R.id.firstTW);
        secondTW = (TextView) findViewById(R.id.secondTW);
        thirdTW = (TextView) findViewById(R.id.thirdTW);
        switchPlayer = (Button) findViewById(R.id.switch_player);

        firstS = getIntent().getExtras().getString("firstS", null);
        secondS = getIntent().getExtras().getString("secondS", null);
        thirdS = getIntent().getExtras().getString("thirdS", null);

        firstTW.setText(firstS.toString());
        secondTW.setText(secondS.toString());
        thirdTW.setText(thirdS.toString());

        firstTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               areYouSureDialogF();

            }
        });

        secondTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                areYouSureDialogS();

            }
        });

        thirdTW.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                areYouSureDialogT();

            }
        });

        switchPlayer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences preferences = getSharedPreferences("totalRounds", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                round = preferences.getInt("rounds", 0);
                Toast.makeText(NextPlayer.this, "Rounds set to: " + round, Toast.LENGTH_SHORT).show();

                try {
                    current_round = preferences.getInt("currentR", 0);
                } catch (NullPointerException e) {
                    current_round = 0;
                }

                if (current_round < round) {

                    Toast.makeText(NextPlayer.this, "Current Round is: " + current_round, Toast.LENGTH_SHORT).show();

                    current_round++;

                    Intent next_turn = new Intent(NextPlayer.this, OnePhone.class);
                    startActivity(next_turn);

                    editor.putInt("currentR", current_round).apply();
                } else {
                    Intent scoreboard = new Intent(NextPlayer.this, ScoreActivity.class);
                    startActivity(scoreboard);
                    finish();
                    editor.putInt("currentR", 0).apply();
                }

            }
        });

    }


    public void areYouSureDialogF() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure?")
                .setMessage("I am 100% sure this is a lie");

        setTitleColor(getResources().getColor(R.color.truth));


        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchPlayer.setVisibility(View.VISIBLE);
            }
        });

        builder.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public void areYouSureDialogS() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure?")
                .setMessage("I am 100% sure this is a lie");

        setTitleColor(getResources().getColor(R.color.truth));


        builder.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchPlayer.setVisibility(View.VISIBLE);
            }
        });

        builder.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }



    public void areYouSureDialogT() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Are you sure?")
                .setMessage("Imma sure, you pleb");
        setTitleColor(getResources().getColor(R.color.truth));

        builder.setPositiveButton("CONTINUE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switchPlayer.setVisibility(View.VISIBLE);
            }
        });

        builder.setNegativeButton("BACK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        exitSession();
    }

    public void exitSession() {

        AlertDialog.Builder exit = new AlertDialog.Builder(this);
            exit.setMessage("You are about to leave your current session");

        exit.setPositiveButton("EXIT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });


        exit.setNegativeButton("Cancle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
    }
}
