package slothstd.twotruthsonelie;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;


public class NextPlayer extends Activity {

    String firstS, secondS, thirdS, roundsS;
    TextView firstTW, secondTW, thirdTW, isGuessing;
    Button switchPlayer, positive, negative;
    Integer round, current_round, player1, player2;
    Boolean firstLie, secondLie, thirdLie;
    Animation animFadeIn;
    Typeface canter;
    GraphHistory graphHistory;
    ImageView playerGuessing;
    boolean wasTrue;
    public Dialog dialogGame;
    private ProgressBar loading1, loading2;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.next_player_layout);

        animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slower);
        canter = Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/canter.otf");

        playerGuessing = (ImageView) findViewById(R.id.playerGuessing);
        SharedPreferences currentR = getSharedPreferences("currentR", MODE_PRIVATE);
        if (currentR.getInt("currentR", 0) % 2 == 0) {
            playerGuessing.setImageResource(R.drawable.user2);
        } else {
            playerGuessing.setImageResource(R.drawable.user1);
        }

        if (hasSoftKeys()) {

            final float scale = getResources().getDisplayMetrics().density;
            int top = (int) (24 * scale + 0.5f);
            int bottom = (int) (48 * scale + 0.5f);

            findViewById(R.id.linearLayout).setPadding(0, 0, 0, bottom);
            findViewById(R.id.gameInfo).setPadding(0, top, 0, 0);
        }

        isGuessing = (TextView) findViewById(R.id.IsGuessingTW);

        firstTW = (TextView) findViewById(R.id.firstTW);
        firstTW.setMovementMethod(new ScrollingMovementMethod());

        secondTW = (TextView) findViewById(R.id.secondTW);
        secondTW.setMovementMethod(new ScrollingMovementMethod());

        thirdTW = (TextView) findViewById(R.id.thirdTW);
        thirdTW.setMovementMethod(new ScrollingMovementMethod());

        loading1 = (ProgressBar) findViewById(R.id.progress1);
        loading2 = (ProgressBar) findViewById(R.id.progress2);

        progressLoading();

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
                    graphHistory = new GraphHistory(graphHistorySP.getString("GraphHistory", null), "single");
                } else {
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


        Dialog();
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGame.dismiss();

                secondTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));
                secondTW.setTextColor(getResources().getColor(R.color.white));
                thirdTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));
                thirdTW.setTextColor(getResources().getColor(R.color.white));
                firstTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));
                firstTW.setTextColor(getResources().getColor(R.color.white));

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
                    switchPlayer.setAnimation(animFadeIn);
                    isGuessing.setVisibility(View.GONE);
                    loading1.setVisibility(View.GONE);
                    loading2.setVisibility(View.GONE);
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
                    switchPlayer.setAnimation(animFadeIn);
                    isGuessing.setVisibility(View.GONE);
                    loading1.setVisibility(View.GONE);
                    loading2.setVisibility(View.GONE);
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
                    switchPlayer.setAnimation(animFadeIn);
                    isGuessing.setVisibility(View.GONE);
                    loading1.setVisibility(View.GONE);
                    loading2.setVisibility(View.GONE);
                    thirdTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                }

            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGame.dismiss();
            }
        });

    }


    public void areYouSureDialogS() {

        Dialog();
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGame.dismiss();

                secondTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));
                secondTW.setTextColor(getResources().getColor(R.color.white));
                thirdTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));
                thirdTW.setTextColor(getResources().getColor(R.color.white));
                firstTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));
                firstTW.setTextColor(getResources().getColor(R.color.white));

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

                    switchPlayer.setVisibility(View.VISIBLE);
                    switchPlayer.setAnimation(animFadeIn);
                    isGuessing.setVisibility(View.GONE);
                    loading1.setVisibility(View.GONE);
                    loading2.setVisibility(View.GONE);
                    secondTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else if (!firstLie) {
                    wasTrue = false;

                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                        }
                    }.start();

                    switchPlayer.setVisibility(View.VISIBLE);
                    switchPlayer.setAnimation(animFadeIn);
                    isGuessing.setVisibility(View.GONE);
                    loading1.setVisibility(View.GONE);
                    loading2.setVisibility(View.GONE);
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

                    switchPlayer.setVisibility(View.VISIBLE);
                    switchPlayer.setAnimation(animFadeIn);
                    firstTW.setClickable(false);
                    thirdTW.setClickable(false);
                    thirdTW.setClickable(false);

                    isGuessing.setVisibility(View.GONE);
                    loading1.setVisibility(View.GONE);
                    loading2.setVisibility(View.GONE);
                    thirdTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));
                }
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGame.dismiss();
            }
        });

    }


    public void areYouSureDialogT() {

        Dialog();
        positive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGame.dismiss();

                secondTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));
                secondTW.setTextColor(getResources().getColor(R.color.white));
                thirdTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));
                thirdTW.setTextColor(getResources().getColor(R.color.white));
                firstTW.setBackground(getResources().getDrawable(R.drawable.custom_edittext_clicked));
                firstTW.setTextColor(getResources().getColor(R.color.white));

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

                    switchPlayer.setVisibility(View.VISIBLE);
                    switchPlayer.setAnimation(animFadeIn);
                    isGuessing.setVisibility(View.GONE);
                    loading1.setVisibility(View.GONE);
                    loading2.setVisibility(View.GONE);
                    thirdTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

                    firstTW.setClickable(false);
                    secondTW.setClickable(false);
                    thirdTW.setClickable(false);

                } else if (!firstLie) {
                    wasTrue = false;

                    new CountDownTimer(2000, 1000) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                        }
                    }.start();

                    switchPlayer.setVisibility(View.VISIBLE);
                    switchPlayer.setAnimation(animFadeIn);
                    isGuessing.setVisibility(View.GONE);
                    loading1.setVisibility(View.GONE);
                    loading2.setVisibility(View.GONE);
                    firstTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));

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
                    switchPlayer.setAnimation(animFadeIn);
                    isGuessing.setVisibility(View.GONE);
                    loading1.setVisibility(View.GONE);
                    loading2.setVisibility(View.GONE);
                    secondTW.setBackgroundDrawable(getResources().getDrawable(R.drawable.custom_edittex_lie));
                }
            }
        });

        negative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogGame.dismiss();
            }
        });
    }

    @Override
    public void onBackPressed() {

        final CustomDialog custom_dialog = new CustomDialog();
        custom_dialog.showDiaolg(NextPlayer.this, "Exit", "Cancel", "Are you sure you want to exit?");
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

    private void Dialog() {

        dialogGame = new Dialog(NextPlayer.this);
        dialogGame.setCancelable(true);
        dialogGame.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogGame.setContentView(R.layout.custom_dialog_game);
        dialogGame.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogGame.show();

        positive = (Button) dialogGame.findViewById(R.id.Yes);
        negative = (Button) dialogGame.findViewById(R.id.No);

    }

    public void resetGraph() {
        getSharedPreferences("SingleGraphHistory", Context.MODE_PRIVATE).edit().clear().apply();
    }


    public void progressLoading() {

        if (true) {

            timer = new CountDownTimer(100, 10) {
                @Override
                public void onTick(long millisUntilFinished) {

                    float rotation = loading1.getRotation();
                    loading1.setRotation(rotation + 5);

                    float rotation2 = loading2.getRotation();
                    loading2.setRotation(rotation2 - 5);
                }

                @Override
                public void onFinish() {

                    progressLoading();

                }
            }.start();

        }
    }

    public boolean hasSoftKeys() {
        boolean hasSoftwareKeys = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display d = this.getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys = (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        } else {
            boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }
}



