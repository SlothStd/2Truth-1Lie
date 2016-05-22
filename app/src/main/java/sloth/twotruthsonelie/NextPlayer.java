package sloth.twotruthsonelie;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class NextPlayer extends Activity {

    String firstS, secondS, thirdS, roundsS;
    TextView firstTW, secondTW, thirdTW, playerGuessing;
    Button switchPlayer;
    Integer round, current_round, player1, player2;
    Boolean firstLie, secondLie, thirdLie;
    Animation animFadeIn;
    Typeface canter;
    GraphHistory graphHistory;
    boolean wasTrue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_player_layout);

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        canter = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/canter.otf");

        playerGuessing = (TextView) findViewById(R.id.playerGuessing);
        playerGuessing.setTypeface(canter);

        firstTW = (TextView) findViewById(R.id.firstTW);
        firstTW.setTypeface(canter);

        secondTW = (TextView) findViewById(R.id.secondTW);
        secondTW.setTypeface(canter);

        thirdTW = (TextView) findViewById(R.id.thirdTW);
        thirdTW.setTypeface(canter);

        switchPlayer = (Button) findViewById(R.id.switch_player);

        try {
            firstS = getIntent().getExtras().getString("firstS", null);
            secondS = getIntent().getExtras().getString("secondS", null);
            thirdS = getIntent().getExtras().getString("thirdS", null);
        } catch (NullPointerException e) {
        }

        try {
            firstTW.setText(firstS.toString());
            secondTW.setText(secondS.toString());
            thirdTW.setText(thirdS.toString());
        } catch (NullPointerException e) {
        }

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

                SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());


                SharedPreferences graphHistorySP = getSharedPreferences("SingleGraphHistory", Context.MODE_PRIVATE);
                if (!graphHistorySP.getString("GraphHistory", "err").equals("err")) {
                    Log.d("GraphHistory", "good");
                    graphHistory = new GraphHistory(graphHistorySP.getString("GraphHistory", null), "single");
                } else {
                    Log.d("GraphHistory", "bad");
                    graphHistory = new GraphHistory("single");
                }

                SharedPreferences.Editor graphHistorySPEditor = graphHistorySP.edit();
                Log.d("GraphHistory", String.valueOf(currentR.getInt("currentR", 0)) + String.valueOf(currentR.getInt("currentR", 0) % 2));
                if ((currentR.getInt("currentR", 0) % 2) == 0) {
                    Log.d("GraphHistory", "my");
                    graphHistory.addMyHistory(wasTrue);
                } else {
                    Log.d("GraphHistory", "his");
                    graphHistory.addHisHistory(wasTrue);
                }
                graphHistorySPEditor.putString("GraphHistory", graphHistory.encrypt()).commit();


                try {
                    roundsS = SP.getString("setRounds", "0");
                    round = Integer.parseInt(roundsS) * 2 - 1;
                } catch (NullPointerException e) {
                    round = 2;
                }

                try {
                    current_round = currentR.getInt("currentR", 0);
                } catch (NullPointerException e) {
                    current_round = 1;
                }

                if (current_round < round) {

                    current_round++;

                    Intent next_turn = new Intent(NextPlayer.this, OnePhone.class);
                    startActivity(next_turn);
                    finish();

                    currentEditor.putInt("currentR", current_round).apply();
                } else {
                    Intent scoreboard = new Intent(NextPlayer.this, ScoreActivity.class);
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

                firstTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);

                firstLie = preferences.getBoolean("firstLie", false);
                secondLie = preferences.getBoolean("secondLie", false);
                thirdLie = preferences.getBoolean("thirdLie", false);

                if (!firstLie) {
                    SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
                    SharedPreferences.Editor editor = points.edit();
                    SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);

                    wasTrue = true;

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

                    switchPlayer.setVisibility(View.VISIBLE);
                    firstTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else if (!secondLie) {

                    wasTrue = false;

                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {

                        }

                        public void onFinish() {

                        }
                    }.start();

                    switchPlayer.setVisibility(View.VISIBLE);
                    secondTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else {

                    wasTrue = false;

                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
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

                secondTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);

                firstLie = preferences.getBoolean("firstLie", false);
                secondLie = preferences.getBoolean("secondLie", false);
                thirdLie = preferences.getBoolean("thirdLie", false);


                if (!secondLie) {
                    SharedPreferences rounds = getSharedPreferences("totalRounds", MODE_PRIVATE);
                    SharedPreferences points = getSharedPreferences("playerPoints", MODE_PRIVATE);
                    SharedPreferences.Editor editor = points.edit();

                    SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);

                    wasTrue = true;

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

//                    Toast.makeText(NextPlayer.this, "CORRECT!", Toast.LENGTH_SHORT).show();
                    switchPlayer.setVisibility(View.VISIBLE);
                    secondTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else if (!firstLie) {
                    wasTrue = false;

                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
//                            
//                            
                        }

                        public void onFinish() {
//                            
//                           
                        }
                    }.start();

                    switchPlayer.setVisibility(View.VISIBLE);
                    firstTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else {
                    wasTrue = false;

                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
//                            
//                            
                        }

                        public void onFinish() {
//                            
//                           
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

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                thirdTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));

                SharedPreferences preferences = getSharedPreferences("TrueOrFalse", MODE_PRIVATE);

                firstLie = preferences.getBoolean("firstLie", false);
                secondLie = preferences.getBoolean("secondLie", false);
                thirdLie = preferences.getBoolean("thirdLie", false);


                if (!thirdLie) {
                    wasTrue = true;

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

//                    Toast.makeText(NextPlayer.this, "CORRECT!", Toast.LENGTH_SHORT).show();
                    switchPlayer.setVisibility(View.VISIBLE);
                    thirdTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else if (!firstLie) {
                    wasTrue = false;

                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
//                            
//                            
                        }

                        public void onFinish() {
//                            
//                           
                        }
                    }.start();

                    switchPlayer.setVisibility(View.VISIBLE);
                    firstTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else {
                    wasTrue = false;

                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
//                            
//                            
                        }

                        public void onFinish() {
//                            
//                           
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

        final CustomDialog custom_dialog = new CustomDialog();
        custom_dialog.showDiaolg(NextPlayer.this, "Exit", "Cancle", "Are you sure you want to exit?");
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

                Intent intent = new Intent(NextPlayer.this, MainActivity.class);
                finish();
                startActivity(intent);

            }
        });

    }


    public void resetGraph() {
        getSharedPreferences("SingleGraphHistory", Context.MODE_PRIVATE).edit().clear().apply();
    }


}


