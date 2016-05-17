package sloth.twotruthsonelie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Daniel on 06-Feb-16.
 */

public class NextPlayerBluetooth extends Activity {

    private static final int SUCCESS_CONNECT = 0;
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    public static final String TOAST = "toast";

    String firstS, secondS, thirdS;
    TextView firstTW, secondTW, thirdTW;
    Button switchPlayer;
    Integer round, current_round, player1, player2;
    Boolean firstLie, secondLie, thirdLie;
    Animation animFadeIn;
    ImageView cross;

    Handler mhandler;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_player_layout);

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);


        firstTW = (TextView) findViewById(R.id.firstTW);
        secondTW = (TextView) findViewById(R.id.secondTW);
        thirdTW = (TextView) findViewById(R.id.thirdTW);
        switchPlayer = (Button) findViewById(R.id.switch_player);


        try {
            firstS = getIntent().getExtras().getString("firstS", null);
            secondS = getIntent().getExtras().getString("secondS", null);
            thirdS = getIntent().getExtras().getString("thirdS", null);
        } catch (NullPointerException e) {}

        try {
            firstTW.setText(firstS.toString());
            secondTW.setText(secondS.toString());
            thirdTW.setText(thirdS.toString());
        } catch (NullPointerException e) {}

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

                SharedPreferences tfPrefs = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);
                SharedPreferences.Editor tfEditor = tfPrefs.edit();
                tfEditor.clear().apply();

                SharedPreferences preferences = getSharedPreferences("totalRounds", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);
                SharedPreferences.Editor currentEditor = currentR.edit();

                try {
                    round = preferences.getInt("rounds", 0);
                } catch (NullPointerException e) {
                    round = 2;
                }

                try {
                    current_round = currentR.getInt("currentR", 0);
                } catch (NullPointerException e) {
                    current_round = 1;
                }

                if (current_round < round) {

                    Toast.makeText(NextPlayerBluetooth.this, "Current Round is: " + current_round, Toast.LENGTH_SHORT).show();

                    current_round++;

                    Intent next_turn = new Intent(NextPlayerBluetooth.this, OnePhone.class);
                    startActivity(next_turn);
                    finish();

                    currentEditor.putInt("currentR", current_round).apply();
                } else {
                    Intent scoreboard = new Intent(NextPlayerBluetooth.this, ScoreActivity.class);
                    startActivity(scoreboard);
                    overridePendingTransition(R.anim.fade_in, R.anim.fade_out_slower);
                    finish();
                    editor.putInt("currentR", 0).apply();
                }

            }
        });

    }



    public void areYouSureDialogF() {


        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Are you sure?");

        setTitleColor(getResources().getColor(R.color.truth));

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);

                firstLie = preferences.getBoolean("firstLie", false);
                secondLie = preferences.getBoolean("secondLie", false);
                thirdLie = preferences.getBoolean("thirdLie", false);

                if (!firstLie) {
                    SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
                    SharedPreferences.Editor editor = points.edit();
                    SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);

                    try {
                        player1 = points.getInt("player1", 0);
                    } catch (NullPointerException e) {
                        player1 = 0;
                    }
                    try {
                        player2 = points.getInt("player2", 0);
                    } catch (NullPointerException e) {
                        player2 = 0;
                    }

                    current_round = currentR.getInt("currentR", 0);
                    if ((current_round % 2) == 0) {
                        player2++;
                        editor.putInt("player2", player2).apply();
                    } else {
                        player1++;
                        editor.putInt("player1", player1).apply();
                    }

                    Toast.makeText(NextPlayerBluetooth.this, "CORRECT!", Toast.LENGTH_SHORT).show();
                    switchPlayer.setVisibility(View.VISIBLE);
                    firstTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else if (!secondLie) {
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            cross.setVisibility(View.VISIBLE);
                            cross.startAnimation(animFadeIn);
                        }

                        public void onFinish() {
                            cross.clearAnimation();
                            cross.setVisibility(View.GONE);
                        }
                    }.start();

                    switchPlayer.setVisibility(View.VISIBLE);
                    secondTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else {
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            cross.setVisibility(View.VISIBLE);
                            cross.startAnimation(animFadeIn);
                        }

                        public void onFinish() {
                            cross.clearAnimation();
                            cross.setVisibility(View.GONE);
                        }
                    }.start();

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                    switchPlayer.setVisibility(View.VISIBLE);
                    thirdTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));
                }
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

        builder.setMessage("Are you sure?");

        setTitleColor(getResources().getColor(R.color.truth));


        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);

                firstLie = preferences.getBoolean("firstLie", false);
                secondLie = preferences.getBoolean("secondLie", false);
                thirdLie = preferences.getBoolean("thirdLie", false);


                if (!secondLie) {
                    SharedPreferences rounds = getSharedPreferences("totalRounds", MODE_PRIVATE);
                    SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
                    SharedPreferences.Editor editor = points.edit();

                    SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);
                    SharedPreferences.Editor currentEditor = currentR.edit();

                    try {
                        player1 = points.getInt("player1", 0);
                    } catch (NullPointerException e) {
                        player1 = 0;
                    }
                    try {
                        player2 = points.getInt("player2", 0);
                    } catch (NullPointerException e) {
                        player2 = 0;
                    }

                    current_round = currentR.getInt("currentR", 0);
                    if ((current_round % 2) == 0) {
                        player2++;
                        editor.putInt("player2", player2).apply();
                    } else {
                        player1++;
                        editor.putInt("player1", player1).apply();
                    }

                    Toast.makeText(NextPlayerBluetooth.this, "CORRECT!", Toast.LENGTH_SHORT).show();
                    switchPlayer.setVisibility(View.VISIBLE);
                    secondTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else if (!firstLie) {
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            cross.setVisibility(View.VISIBLE);
                            cross.startAnimation(animFadeIn);
                        }

                        public void onFinish() {
                            cross.clearAnimation();
                            cross.setVisibility(View.GONE);
                        }
                    }.start();

                    switchPlayer.setVisibility(View.VISIBLE);
                    firstTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else {
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            cross.setVisibility(View.VISIBLE);
                            cross.startAnimation(animFadeIn);
                        }

                        public void onFinish() {
                            cross.clearAnimation();
                            cross.setVisibility(View.GONE);
                        }
                    }.start();

                    firstTW.setClickable(false);
                    thirdTW.setClickable(false);
                    thirdTW.setClickable(false);

                    switchPlayer.setVisibility(View.VISIBLE);
                    thirdTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));
                }
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

        builder.setMessage("Are you sure?");
        setTitleColor(getResources().getColor(R.color.truth));

        builder.setPositiveButton("YES",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);

                firstLie = preferences.getBoolean("firstLie", false);
                secondLie = preferences.getBoolean("secondLie", false);
                thirdLie = preferences.getBoolean("thirdLie", false);


                if (!thirdLie) {
                    SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
                    SharedPreferences.Editor editor = points.edit();

                    SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);

                    try {
                        player1 = points.getInt("player1", 0);
                    } catch (NullPointerException e) {
                        player1 = 0;
                    }
                    try {
                        player2 = points.getInt("player2", 0);
                    } catch (NullPointerException e) {
                        player2 = 0;
                    }

                    current_round = currentR.getInt("currentR", 0);
                    if ((current_round % 2) == 0) {
                        player2++;
                        editor.putInt("player2", player2).apply();
                    } else {
                        player1++;
                        editor.putInt("player1", player1).apply();
                    }

                    Toast.makeText(NextPlayerBluetooth.this, "CORRECT!", Toast.LENGTH_SHORT).show();
                    switchPlayer.setVisibility(View.VISIBLE);
                    thirdTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else if (!firstLie) {

                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            cross.setVisibility(View.VISIBLE);
                            cross.startAnimation(animFadeIn);
                        }

                        public void onFinish() {
                            cross.clearAnimation();
                            cross.setVisibility(View.GONE);
                        }
                    }.start();

                    switchPlayer.setVisibility(View.VISIBLE);
                    firstTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else {
                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            cross.setVisibility(View.VISIBLE);
                            cross.startAnimation(animFadeIn);
                        }

                        public void onFinish() {
                            cross.clearAnimation();
                            cross.setVisibility(View.GONE);
                        }
                    }.start();

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                    switchPlayer.setVisibility(View.VISIBLE);
                    secondTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));
                }
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
        super.onBackPressed();
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