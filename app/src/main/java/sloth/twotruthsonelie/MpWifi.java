package sloth.twotruthsonelie;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.turnbased.OnTurnBasedMatchUpdateReceivedListener;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatch;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMatchConfig;
import com.google.android.gms.games.multiplayer.turnbased.TurnBasedMultiplayer;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.plus.Plus;
import com.google.example.games.basegameutils.BaseGameUtils;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LabelFormatter;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

/**
 * Created by Robo on 28-Jan-16.
 * E:\Program Files\Android Studio\SDK\platform-tools
 * adb install C:\AndroidStudio\2Truth-1Lie\app\app-release.apk
 */
public class MpWifi extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnInvitationReceivedListener,
        OnTurnBasedMatchUpdateReceivedListener,
        View.OnClickListener {

    public static final String TAG = "2T1L MpWifi";

    // Client used to interact with Google APIs
    private GoogleApiClient mGoogleApiClient;

    // Are we currently resolving a connection failure?
    private boolean mResolvingConnectionFailure = false;

    // Has the user clicked the sign-in button?
    private boolean mSignInClicked = false;

    // Automatically start the sign-in flow when the Activity starts
    private boolean mAutoStartSignInFlow = true;

    // Current turn-based match
    private TurnBasedMatch mTurnBasedMatch;

    private AlertDialog mAlertDialog;

    // For our intents
    final static int RC_SIGN_IN = 9001;
    final static int RC_SELECT_PLAYERS = 10000;
    final static int RC_LOOK_AT_MATCHES = 10001;

    // How long to show toasts.
    final static int TOAST_DELAY = Toast.LENGTH_SHORT;

    //Game state
    public int gameState = 0;

    // This is the current match we're in; null if not loaded
    public TurnBasedMatch mMatch;
    public MatchData matchData = new MatchData();

    private String myID, hisID;
    private ArrayList<String> IDs;
    private int player;
    private String myDisplayName, hisDisplayName;

    private GraphHistory graphHistory;

    private int roundCount = 2;

    public final int winPoint = 1;
    public final int gameFinishedXp = 10;
    public final int winBonus = 15;
    public final int tieBonus = 5;
    public final int roundWinBonus = 2;

    private EditText firstS, secondS, thirdS;

    int totalWins, totalLoses;

    CountDownTimer timer;
    ProgressBar loading1, loading2, score;
    ProgressDialog progress;
    ObjectAnimator animation;
    CountDownTimer timer2;
    int points;

    LinearLayout layout;
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_wifi);

        score = (ProgressBar) findViewById(R.id.fakeProgressJustBecauseIcan);
        score.setRotation(-90);
        score.setMax(1000000); //100*10k ↓
        points = 700000; //daj ten int *10k aby bola animacia smooth

        timer2 = new CountDownTimer(1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                score.setProgress(0);

            }

            @Override
            public void onFinish() {

                animation = new ObjectAnimator().ofInt(score, "progress", 0, points); //alebo nahrať "points" so svojim intom
                animation.setDuration(2000);
                animation.setInterpolator(new AccelerateDecelerateInterpolator());
                animation.start();

            }
        }.start();


        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();

        // Setup signin and signout buttons
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        params.setMargins(0, 60, 0, 0);
        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.KITKAT) {
            findViewById(R.id.correct_wrong).setLayoutParams(params);
        } else {
        }

        firstS = (EditText) findViewById(R.id.firstS);
        secondS = (EditText) findViewById(R.id.secondS);
        thirdS = (EditText) findViewById(R.id.thirdS);
        danyhoDivneCheckboxy();

        loading1 = (ProgressBar) findViewById(R.id.loading1);
        loading2 = (ProgressBar) findViewById(R.id.loading2);

    }

    @Override
    protected void onStart() {
        super.onStart();

        updateLevels();

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        try {
            String roundsS = SP.getString("setRounds", "2");
            roundCount = Integer.parseInt(roundsS) * 2;
        } catch (NullPointerException e) {
            roundCount = 6;
        }

        loadSP();

        if (hasSoftKeys()) {

            final float scale = getResources().getDisplayMetrics().density;
            int top = (int) (24 * scale + 0.5f);
            int bottom = (int) (48 * scale + 0.5f);

            findViewById(R.id.MpWifi_main_layout).setPadding(0, top, 0, bottom);
        } else {
            findViewById(R.id.MpWifi_main_layout).setPadding(0, 0, 0, 0);
        }

        try {
            double perc = totalWins;
            perc /= (totalWins + totalLoses);
            perc *= 100;

            ((TextView) findViewById(R.id.winRatioTv)).setText(String.valueOf((int) perc) + "%");
        } catch (ArithmeticException e) {
            ((TextView) findViewById(R.id.winRatioTv)).setText("");
        }

        Log.d(TAG, "onStart(): Connecting to Google APIs");
        mGoogleApiClient.connect();
    }

    public void loadSP() {
        if (mMatch != null) {
            SharedPreferences prefs = getSharedPreferences(mMatch.getMatchId(), Context.MODE_PRIVATE);
            if (prefs.getBoolean("isSaved", false)) {
                matchData = new MatchData(prefs.getString("matchData", null));
            }

            if (!prefs.getString("GraphHistory", "err").equals("err")) {
                graphHistory = new GraphHistory(prefs.getString("GraphHistory", null), mMatch.getMatchId());
            } else {
                graphHistory = new GraphHistory(mMatch.getMatchId());
            }
        }

        SharedPreferences preferences = getSharedPreferences("WinRatio", Context.MODE_PRIVATE);
        totalWins = preferences.getInt("Wins", 0);
        totalLoses = preferences.getInt("Loses", 0);
    }

    @Override
    protected void onStop() {
        super.onStop();

        saveSP();

        Log.d(TAG, "onStop(): Disconnecting from Google APIs");
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    public void saveSP() {
        if (mMatch != null) {
            SharedPreferences prefs = getSharedPreferences(mMatch.getMatchId(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();

            if (gameState == 1) {
                editor.putBoolean("isSaved", true);
                editor.putString("matchData", matchData.convertDataToString());
            }
            editor.putString("GraphHistory", graphHistory.encrypt()).apply();
        }

        SharedPreferences.Editor editor = getSharedPreferences("WinRatio", Context.MODE_PRIVATE).edit();
        editor.putInt("Wins", totalWins);
        editor.putInt("Loses", totalLoses);
        editor.apply();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        Log.d(TAG, "onConnected(): Connection successful");

        // Retrieve the TurnBasedMatch from the connectionHint
        if (connectionHint != null) {
            mTurnBasedMatch = connectionHint.getParcelable(Multiplayer.EXTRA_TURN_BASED_MATCH);

            if (mTurnBasedMatch != null) {
                if (mGoogleApiClient == null || !mGoogleApiClient.isConnected()) {
                    Log.d(TAG, "Warning: accessing TurnBasedMatch when not connected");
                }

                updateMatch(mTurnBasedMatch);
                return;
            }
        }

        findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);

        // As a demonstration, we are registering this activity as a handler for
        // invitation and match events.

        // This is *NOT* required; if you do not register a handler for
        // invitation events, you will get standard notifications instead.
        // Standard notifications may be preferable behavior in many cases.
        Games.Invitations.registerInvitationListener(mGoogleApiClient, this);

        // Likewise, we are registering the optional MatchUpdateListener, which
        // will replace notifications you would get otherwise. You do *NOT* have
        // to register a MatchUpdateListener.
        Games.TurnBasedMultiplayer.registerMatchUpdateListener(mGoogleApiClient, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended():  Trying to reconnect.");
        mGoogleApiClient.connect();
        setViewVisibility();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed(): attempting to resolve");
        if (mResolvingConnectionFailure) {
            // Already resolving
            Log.d(TAG, "onConnectionFailed(): ignoring connection failure, already resolving.");
            return;
        }

        // Launch the sign-in flow if the button was clicked or if auto sign-in is enabled
        if (mSignInClicked || mAutoStartSignInFlow) {
            mAutoStartSignInFlow = false;
            mSignInClicked = false;

            mResolvingConnectionFailure = BaseGameUtils.resolveConnectionFailure(this,
                    mGoogleApiClient, connectionResult, RC_SIGN_IN,
                    getString(R.string.signin_other_error));
        }

        setViewVisibility();
    }

    // Displays your inbox. You will get back onActivityResult where
    // you will need to figure out what you clicked on.
    public void onCheckGamesClicked(View view) {
        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
    }

    // Open the create-game UI. You will get back an onActivityResult
    // and figure out what to do.
    public void onStartMatchClicked(View view) {
        Intent intent =
                Games.TurnBasedMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 1, true);
        startActivityForResult(intent, RC_SELECT_PLAYERS);
    }

    // Create a one-on-one automatch game.
    public void onQuickMatchClicked(View view) {

        Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                1, 1, 0);

        TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                .setAutoMatchCriteria(autoMatchCriteria).build();

        showSpinner();

        // Start the match
        ResultCallback<TurnBasedMultiplayer.InitiateMatchResult> cb = new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
            @Override
            public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                processResult(result);
            }
        };
        Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc).setResultCallback(cb);
    }

    // In-game controls

    // Cancel the game. Should possibly wait until the game is canceled before
    // giving up on the view.
    public void onCancelClicked(View view) {
        showSpinner();
        Games.TurnBasedMultiplayer.cancelMatch(mGoogleApiClient, mMatch.getMatchId())
                .setResultCallback(new ResultCallback<TurnBasedMultiplayer.CancelMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.CancelMatchResult result) {
                        processResult(result);
                    }
                });
        gameState = 0;
        setViewVisibility();
    }

    // Leave the game during your turn. Note that there is a separate
    // Games.TurnBasedMultiplayer.leaveMatch() if you want to leave NOT on your turn.
    public void onLeaveClicked(View view) {
        showSpinner();
        String nextParticipantId = getNextParticipantId();

        Games.TurnBasedMultiplayer.leaveMatchDuringTurn(mGoogleApiClient, mMatch.getMatchId(),
                nextParticipantId).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.LeaveMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.LeaveMatchResult result) {
                        processResult(result);
                    }
                });
        setViewVisibility();
    }

    // Upload your new gamestate, then take a turn, and pass it on to the next
    // player.
    public void onDoneClicked(View view) {

        if (matchData.getLiePos() == -1) {
            Toast.makeText(this, "Select a lie you retard", Toast.LENGTH_SHORT).show();
            return;
        }
        if (firstS.getText().toString().isEmpty() ||
                secondS.getText().toString().isEmpty() ||
                thirdS.getText().toString().isEmpty()) {

            Toast.makeText(this, "Fill up all three text fields", Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> sentences = new ArrayList<>();
        sentences.add(firstS.getText().toString());
        sentences.add(secondS.getText().toString());
        sentences.add(thirdS.getText().toString());
        matchData.setSentences(sentences);
        matchData.setSentenceAuthor(myID);

        showSpinner();

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, mMatch.getMatchId(),
                matchData.convertData(), getNextParticipantId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });

        firstS.setText("");
        secondS.setText("");
        thirdS.setText("");
        matchData.setLiePos(-1);

        SharedPreferences prefs = getSharedPreferences(mMatch.getMatchId(), Context.MODE_PRIVATE);
        prefs.edit().clear().apply();
    }

    // Sign-in, Sign out behavior

    // Update the visibility based on what state we're in.
    public void setViewVisibility() {
        boolean isSignedIn = (mGoogleApiClient != null) && (mGoogleApiClient.isConnected());

        findViewById(R.id.MpWifi_main_layout).setBackgroundResource(R.color.darker_bg);

        if (!isSignedIn) {
            findViewById(R.id.buttons).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            findViewById(R.id.sign_out_button).setVisibility(View.GONE);
            findViewById(R.id.setTexts).setVisibility(View.GONE);
            findViewById(R.id.chooseTexts).setVisibility(View.GONE);

            if (mAlertDialog != null) {
                mAlertDialog.dismiss();
            }
            return;
        }

        try {
            double perc = totalWins;
            perc /= (totalWins + totalLoses);
            perc *= 100;

            ((TextView) findViewById(R.id.winRatioTv)).setText(String.valueOf((int) perc) + "%");
        } catch (ArithmeticException e) {
            ((TextView) findViewById(R.id.winRatioTv)).setText("");
        }

        /*((TextView) findViewById(R.id.name_field)).setText(Games.Players.getCurrentPlayer(
                mGoogleApiClient).getDisplayName());*/
        findViewById(R.id.buttons).setVisibility(View.GONE);

        getPlayerIDs();

        switch (gameState) {
            case -1: //Buttons

                findViewById(R.id.buttons).setVisibility(View.VISIBLE);
                findViewById(R.id.setTexts).setVisibility(View.GONE);
                findViewById(R.id.chooseTexts).setVisibility(View.GONE);
                findViewById(R.id.notYourTurn).setVisibility(View.GONE);
                findViewById(R.id.gameFinished).setVisibility(View.GONE);

                Log.d(TAG, "Buttons");

                break;

            case 0: //Not your turn

                findViewById(R.id.setTexts).setVisibility(View.GONE);
                findViewById(R.id.chooseTexts).setVisibility(View.GONE);
                findViewById(R.id.notYourTurn).setVisibility(View.VISIBLE);
                findViewById(R.id.gameFinished).setVisibility(View.GONE);

                metoda1();

                Log.d(TAG, "Not your turn");

                break;

            case 1: //Setting sentences

                findViewById(R.id.setTexts).setVisibility(View.VISIBLE);
                findViewById(R.id.chooseTexts).setVisibility(View.GONE);
                findViewById(R.id.notYourTurn).setVisibility(View.GONE);
                findViewById(R.id.gameFinished).setVisibility(View.GONE);

                randomBg();

                Log.d(TAG, "Setting");

                break;

            case 2: //Guessing

                findViewById(R.id.setTexts).setVisibility(View.GONE);
                findViewById(R.id.chooseTexts).setVisibility(View.VISIBLE);
                findViewById(R.id.notYourTurn).setVisibility(View.GONE);
                findViewById(R.id.gameFinished).setVisibility(View.GONE);

                Log.d(TAG, "Guessing");

                new Guessing().start();

                break;

            case 3: //Game finished

                findViewById(R.id.setTexts).setVisibility(View.GONE);
                findViewById(R.id.chooseTexts).setVisibility(View.GONE);
                findViewById(R.id.notYourTurn).setVisibility(View.GONE);
                findViewById(R.id.gameFinished).setVisibility(View.VISIBLE);

                finishGame();

                break;
        }
    }

    public void randomBg() {
        ArrayList<Integer> backgrounds = new ArrayList<>();

        backgrounds.add(R.drawable.bckg2);
        backgrounds.add(R.drawable.bckg3);
        backgrounds.add(R.drawable.bckg4);
        backgrounds.add(R.drawable.bckg5);
        backgrounds.add(R.drawable.bckg6);
        backgrounds.add(R.drawable.bckg7);

        Random rand = new Random();
        int randNum = rand.nextInt(backgrounds.size());
        findViewById(R.id.MpWifi_main_layout).setBackgroundResource(backgrounds.get(randNum));
    }

    // Switch to gameplay view.
    public void setGameplayUI() {
        getPlayerIDs();

        SharedPreferences prefs = getSharedPreferences(mMatch.getMatchId(), Context.MODE_PRIVATE);
        if (prefs.getBoolean("isSaved", false)) {
            matchData = new MatchData(prefs.getString("matchData", "~||~~|~"));

            gameState = 1;
            setViewVisibility();

            return;
        }

        matchData = new MatchData(mMatch.getData());

        if (matchData.getSentences().size() == 0) {
            gameState = 1;
        } else {
            gameState = 2;
        }

        if (matchData.getCurrentRound() != 0) {
            graphHistory.compare(matchData.getScores(), player);
        }

        setViewVisibility();
    }

    // Helpful dialogs
    public void showSpinner() {
        progress = ProgressDialog.show(this, null, "Loading", true, false);
    }

    public void dismissSpinner() {
        if (progress != null) progress.dismiss();
    }

    // Generic warning/info dialog
    public void showWarning(String title, String message) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // set title
        if (title != null) alertDialogBuilder.setTitle(title);

        alertDialogBuilder.setMessage(message);

        // set dialog message
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                    }
                });

        // create alert dialog
        mAlertDialog = alertDialogBuilder.create();
        // show it
        mAlertDialog.show();
    }

    public void finishGame() {
        if (matchData.getSentences().get(0).equals("`")) {
            Log.d(TAG, "Finished2");

            graphHistory.compare(matchData.getScores(), player);
            setUpGraph();

            Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, mMatch.getMatchId())
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                            processResult(result);
                        }
                    });
        } else {
            Log.d(TAG, "Finished1");

            setUpGraph();

            matchData.setSentences(new ArrayList<String>(Arrays.asList(new String[]{"`", "`", "`"})));
            Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, mMatch.getMatchId(), matchData.convertData())
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                            processResult(result);
                        }
                    });
        }

        ((TextView) findViewById(R.id.scoreTV)).setText("");
        ((TextView) findViewById(R.id.p_2_TV)).setText(matchData.getScores(0) + " - " + matchData.getScores(1));

        SharedPreferences prefs = getSharedPreferences("Levels", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        int xp = prefs.getInt("xp", 0);

        if (matchData.didIWin(player) == null) {
            ((TextView) findViewById(R.id.p_1_TV)).setText(R.string.its_a_tie);

            xp += gameFinishedXp;
            xp += tieBonus;
            xp += (matchData.getScores(player) * roundWinBonus);
        } else if (matchData.didIWin(player)) {
            ((TextView) findViewById(R.id.p_1_TV)).setText(R.string.you_win);

            xp += gameFinishedXp;
            xp += winBonus;
            xp += (matchData.getScores(player) * roundWinBonus);
        } else {
            ((TextView) findViewById(R.id.p_1_TV)).setText(R.string.you_lose);

            xp += gameFinishedXp;
            xp += (matchData.getScores(player) * roundWinBonus);
        }

        editor.putInt("xp", xp).apply();

        totalWins += graphHistory.myWinCount();
        totalLoses += (roundCount / 2) - graphHistory.myWinCount();

        Log.d(TAG, String.valueOf(xp));
    }

    // Rematch dialog
    public void askForRematch() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setMessage("Do you want a rematch?");

        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Sure, rematch!",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                rematch();
                            }
                        })
                .setNegativeButton("No.",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });

        alertDialogBuilder.show();
    }

    // This function is what gets called when you return from either the Play
    // Games built-in inbox, or else the create game built-in interface.
    @Override
    public void onActivityResult(int request, int response, Intent data) {
        super.onActivityResult(request, response, data);
        switch (request) {
            case RC_SIGN_IN:
                mSignInClicked = false;
                mResolvingConnectionFailure = false;
                if (response == Activity.RESULT_OK) {
                    mGoogleApiClient.connect();
                } else {
                    BaseGameUtils.showActivityResultError(this, request, response, R.string.signin_other_error);
                }
                break;
            case RC_LOOK_AT_MATCHES:
                // Returning from the 'Select Match' dialog

                if (response != Activity.RESULT_OK) {
                    // user canceled
                    return;
                }

                TurnBasedMatch match = data
                        .getParcelableExtra(Multiplayer.EXTRA_TURN_BASED_MATCH);

                if (match != null) {
                    updateMatch(match);
                }

                Log.d(TAG, "Match = " + match);
                break;
            case RC_SELECT_PLAYERS:
                // Returned from 'Select players to Invite' dialog

                if (response != Activity.RESULT_OK) {
                    // user canceled
                    return;
                }

                // get the invitee list
                final ArrayList<String> invitees = data
                        .getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

                // get automatch criteria
                Bundle autoMatchCriteria = null;

                int minAutoMatchPlayers = data.getIntExtra(
                        Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
                int maxAutoMatchPlayers = data.getIntExtra(
                        Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

                if (minAutoMatchPlayers > 0) {
                    autoMatchCriteria = RoomConfig.createAutoMatchCriteria(
                            minAutoMatchPlayers, maxAutoMatchPlayers, 0);
                } else {
                    autoMatchCriteria = null;
                }

                TurnBasedMatchConfig tbmc = TurnBasedMatchConfig.builder()
                        .addInvitedPlayers(invitees)
                        .setAutoMatchCriteria(autoMatchCriteria).build();

                // Start the match
                Games.TurnBasedMultiplayer.createMatch(mGoogleApiClient, tbmc).setResultCallback(
                        new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                            @Override
                            public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                                processResult(result);
                            }
                        });
                showSpinner();
                break;
        }
    }

    // startMatch() happens in response to the createTurnBasedMatch()
    // above. This is only called on success, so we should have a
    // valid match object. We're taking this opportunity to setup the
    // game, saving our initial state. Calling takeTurn() will
    // callback to OnTurnBasedMatchUpdated(), which will show the game
    // UI.
    public void startMatch(TurnBasedMatch match) {

        mMatch = match;

        showSpinner();

        getPlayerIDs();

        Games.TurnBasedMultiplayer.takeTurn(mGoogleApiClient, match.getMatchId(),
                matchData.convertData(), myID).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                        processResult(result);
                    }
                });
    }

    public void getPlayerIDs() {
        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        myID = mMatch.getParticipantId(playerId);

        IDs = mMatch.getParticipantIds();

        if (myID.equals(IDs.get(0))) {
            player = 0;
            hisID = IDs.get(1);
        } else {
            player = 1;
            hisID = IDs.get(0);
        }

        myDisplayName = mMatch.getParticipant(myID).getDisplayName();
        hisDisplayName = mMatch.getParticipant(hisID).getDisplayName();

        ((TextView) findViewById(R.id.p_1_TV)).setText(myDisplayName);
        ((TextView) findViewById(R.id.p_2_TV)).setText(hisDisplayName);
        ((TextView) findViewById(R.id.scoreTV)).setText(matchData.getScores().get(player) + " - " + matchData.getScores().get(Math.abs(player - 1)));
    }

    public void updateLevels() {
        SharedPreferences preferences = getSharedPreferences("Levels", Context.MODE_PRIVATE);
        int xp = preferences.getInt("xp", 0),
                a = 50,
                level = 1,
                previous = 0;

        while (a <= xp) {
            level++;

            previous = a;

            a += a + (Math.round(0.1 * a));
        }

        ((TextView) findViewById(R.id.levelTextView)).setText("Level " + String.valueOf(level));

        ProgressBar progressBar = (ProgressBar) findViewById(R.id.levelProgress);

        progressBar.setMax(a);
        progressBar.setProgress(xp - previous);
    }

    // If you choose to rematch, then call it and wait for a response.
    public void rematch() {
        showSpinner();
        Games.TurnBasedMultiplayer.rematch(mGoogleApiClient, mMatch.getMatchId()).setResultCallback(
                new ResultCallback<TurnBasedMultiplayer.InitiateMatchResult>() {
                    @Override
                    public void onResult(TurnBasedMultiplayer.InitiateMatchResult result) {
                        processResult(result);
                    }
                });
        mMatch = null;
        gameState = 0;
    }

    /**
     * Get the next participant. In this function, we assume that we are
     * round-robin, with all known players going before all automatch players.
     * This is not a requirement; players can go in any order. However, you can
     * take turns in any order.
     *
     * @return participantId of next player, or null if automatching
     */
    public String getNextParticipantId() {

        String playerId = Games.Players.getCurrentPlayerId(mGoogleApiClient);
        String myParticipantId = mMatch.getParticipantId(playerId);

        ArrayList<String> participantIds = mMatch.getParticipantIds();

        if (participantIds.get(0).equals(myParticipantId)) {
            return participantIds.get(1);
        } else if (participantIds.get(1).equals(myParticipantId)) {
            return participantIds.get(0);
        } else {
            return null;
        }
    }

    // This is the main function that gets called when players choose a match
    // from the inbox, or else create a match and want to start it.
    public void updateMatch(TurnBasedMatch match) {
        mMatch = match;

        getPlayerIDs();

        if (graphHistory == null || !graphHistory.matchID.equals(mMatch.getMatchId())) {
            loadSP();
        }

        int status = match.getStatus();
        int turnStatus = match.getTurnStatus();

        switch (status) {
            case TurnBasedMatch.MATCH_STATUS_CANCELED:
                showWarning("Canceled!", "This game was canceled!");
                return;
            case TurnBasedMatch.MATCH_STATUS_EXPIRED:
                showWarning("Expired!", "This game is expired.  So sad!");
                return;
            case TurnBasedMatch.MATCH_STATUS_AUTO_MATCHING:
                showWarning("Waiting for auto-match...",
                        "We're still waiting for an automatch partner.");
                return;
            case TurnBasedMatch.MATCH_STATUS_COMPLETE:
                if (turnStatus == TurnBasedMatch.MATCH_TURN_STATUS_COMPLETE) {
                    showWarning(
                            "Complete!",
                            "This game is over; someone finished it, and so did you!  There is nothing to be done.");
                    break;
                }

                // Note that in this state, you must still call "Finish" yourself,
                // so we allow this to continue.
                matchData = new MatchData(mMatch.getData());

                gameState = 3;
                setViewVisibility();

                return;
        }

        // OK, it's active. Check on turn status.
        switch (turnStatus) {
            case TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN:
                setGameplayUI();
                return;
            case TurnBasedMatch.MATCH_TURN_STATUS_THEIR_TURN:
                gameState = 0;
                break;
            case TurnBasedMatch.MATCH_TURN_STATUS_INVITED:
                showWarning("Good inititative!",
                        "Still waiting for invitations.\n\nBe patient!");
                gameState = 0;
        }

        setViewVisibility();
    }

    private void processResult(TurnBasedMultiplayer.CancelMatchResult result) {
        dismissSpinner();

        if (!checkStatusCode(null, result.getStatus().getStatusCode())) {
            return;
        }

        gameState = 0;

        showWarning("Match",
                "This match is canceled.  All other players will have their game ended.");
    }

    private void processResult(TurnBasedMultiplayer.InitiateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        dismissSpinner();

        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        if (match.getData() != null) {
            // This is a game that has already started, so I'll just start
            updateMatch(match);
            return;
        }

        startMatch(match);
    }


    private void processResult(TurnBasedMultiplayer.LeaveMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        dismissSpinner();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }

        showWarning("Left", "You've left this match.");
    }


    public void processResult(TurnBasedMultiplayer.UpdateMatchResult result) {
        TurnBasedMatch match = result.getMatch();
        dismissSpinner();
        if (!checkStatusCode(match, result.getStatus().getStatusCode())) {
            return;
        }
        if (match.canRematch()) {
            askForRematch();
        }

        if (match.getTurnStatus() == TurnBasedMatch.MATCH_TURN_STATUS_MY_TURN) {
            updateMatch(match);
            return;
        }

        if (gameState != 3) {
            gameState = 0;
            setViewVisibility();
        }
    }

    // Handle notification events.
    @Override
    public void onInvitationReceived(Invitation invitation) {
        final Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),
                "An invitation has arrived from "
                        + invitation.getInviter().getDisplayName(),
                Snackbar.LENGTH_LONG);

        snackbar.setAction("Show", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onCheckGamesClicked(v);

                snackbar.dismiss();
            }
        });
        snackbar.show();
    }

    @Override
    public void onInvitationRemoved(String invitationId) {
        Toast.makeText(this, "An invitation was removed.", TOAST_DELAY).show();
    }

    @Override
    public void onTurnBasedMatchReceived(final TurnBasedMatch match) {
        Toast.makeText(this, "A match was updated.", TOAST_DELAY).show();

        if (mMatch.getMatchId().equals(match.getMatchId())) {
            updateMatch(match);
        } else {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

            alertDialogBuilder
                    .setTitle("It's your turn!")
                    .setMessage("It is your turn in a game against " + match.getParticipant(match.getLastUpdaterId()).getDisplayName());

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Let's GO!",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    updateMatch(match);
                                }
                            })
                    .setNegativeButton("Dismiss",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            });

            alertDialogBuilder.show();
        }
    }

    @Override
    public void onTurnBasedMatchRemoved(String matchId) {
        Toast.makeText(this, "A match was removed.", TOAST_DELAY).show();
    }

    public void showErrorMessage(TurnBasedMatch match, int statusCode,
                                 int stringId) {

        showWarning("Warning", getResources().getString(stringId));
    }

    // Returns false if something went wrong, probably. This should handle
    // more cases, and probably report more accurate results.
    private boolean checkStatusCode(TurnBasedMatch match, int statusCode) {
        switch (statusCode) {
            case GamesStatusCodes.STATUS_OK:
                return true;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_DEFERRED:
                // This is OK; the action is stored by Google Play Services and will
                // be dealt with later.
                Toast.makeText(
                        this,
                        "Stored action for later.  (Please remove this toast before release.)",
                        TOAST_DELAY).show();
                // NOTE: This toast is for informative reasons only; please remove
                // it from your final application.
                return true;
            case GamesStatusCodes.STATUS_MULTIPLAYER_ERROR_NOT_TRUSTED_TESTER:
                showErrorMessage(match, statusCode,
                        R.string.status_multiplayer_error_not_trusted_tester);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_ALREADY_REMATCHED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_already_rematched);
                break;
            case GamesStatusCodes.STATUS_NETWORK_ERROR_OPERATION_FAILED:
                showErrorMessage(match, statusCode,
                        R.string.network_error_operation_failed);
                break;
            case GamesStatusCodes.STATUS_CLIENT_RECONNECT_REQUIRED:
                showErrorMessage(match, statusCode,
                        R.string.client_reconnect_required);
                break;
            case GamesStatusCodes.STATUS_INTERNAL_ERROR:
                showErrorMessage(match, statusCode, R.string.internal_error);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_INACTIVE_MATCH:
                showErrorMessage(match, statusCode,
                        R.string.match_error_inactive_match);
                break;
            case GamesStatusCodes.STATUS_MATCH_ERROR_LOCALLY_MODIFIED:
                showErrorMessage(match, statusCode,
                        R.string.match_error_locally_modified);
                break;
            default:
                showErrorMessage(match, statusCode, R.string.unexpected_status);
                Log.d(TAG, "Did not have warning or string to deal with: "
                        + statusCode);
        }

        return false;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                // Check to see the developer who's running this sample code read the instructions :-)
                // NOTE: this check is here only because this is a sample! Don't include this
                // check in your actual production app.
                if (!BaseGameUtils.verifySampleSetup(this, R.string.app_id)) {
                    Log.w(TAG, "*** Warning: setup problems detected. Sign in may not work!");
                }

                mSignInClicked = true;
                mTurnBasedMatch = null;
                findViewById(R.id.sign_in_button).setVisibility(View.GONE);
                findViewById(R.id.sign_out_button).setVisibility(View.VISIBLE);
                mGoogleApiClient.connect();
                break;
            case R.id.sign_out_button:
                mSignInClicked = false;
                Games.signOut(mGoogleApiClient);
                if (mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.disconnect();
                }
                findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
                findViewById(R.id.sign_out_button).setVisibility(View.GONE);
                break;
        }
    }

    public void danyhoDivneCheckboxy() {
        firstS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);
                firstS.setBackgroundResource(R.drawable.custom_edittex_lie);

                matchData.setLiePos(0);

                return true;
            }
        });

        secondS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                firstS.setBackgroundResource(R.drawable.custom_edittext_truth);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);
                secondS.setBackgroundResource(R.drawable.custom_edittex_lie);

                matchData.setLiePos(1);

                return true;
            }
        });

        thirdS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);
                firstS.setBackgroundResource(R.drawable.custom_edittext_truth);
                thirdS.setBackgroundResource(R.drawable.custom_edittex_lie);

                matchData.setLiePos(2);

                return true;
            }
        });
    }

    public void goToFinishScreen(View view) {
        view.setVisibility(View.GONE);
/*
        findViewById(R.id.buttons).setVisibility(View.GONE);
        findViewById(R.id.setTexts).setVisibility(View.GONE);
        findViewById(R.id.chooseTexts).setVisibility(View.GONE);
        findViewById(R.id.notYourTurn).setVisibility(View.GONE);
        findViewById(R.id.gameFinished).setVisibility(View.VISIBLE);

        GraphHistory tempHist = new GraphHistory(new ArrayList<Boolean>(Arrays.asList(new Boolean[]{true, false, true, false})), new ArrayList<Boolean>(Arrays.asList(new Boolean[]{false, true, false, true})));

        GraphView graph = (GraphView) findViewById(R.id.graphMP);

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(tempHist.getMyDataPoints());
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(tempHist.getHisDataPoints());

        series1.setColor(getResources().getColor(R.color.truth));
        series2.setColor(getResources().getColor(R.color.lie));

        graph.getGridLabelRenderer().setNumHorizontalLabels(5);
        graph.getGridLabelRenderer().setNumVerticalLabels(5);

        graph.getGridLabelRenderer().setLabelFormatter(new LabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (value == 0){
                    return "";
                }
                else {
                    return String.valueOf((int) value);
                }
            }
            @Override
            public void setViewport(Viewport viewport) {}
        });

        LineGraphSeries<DataPoint> transparent = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(4, 4)
        });
        transparent.setColor(getResources().getColor(android.R.color.transparent));

        graph.addSeries(series1);
        graph.addSeries(series2);
        graph.addSeries(transparent);*/
    }

    public void setUpGraph() {
        GraphView graph = (GraphView) findViewById(R.id.graphMP);
        graph.removeAllSeries();

        LineGraphSeries<DataPoint> series1 = new LineGraphSeries<>(graphHistory.getMyDataPoints());
        LineGraphSeries<DataPoint> series2 = new LineGraphSeries<>(graphHistory.getHisDataPoints());

        series1.setColor(getResources().getColor(R.color.truth));
        series2.setColor(getResources().getColor(R.color.lie));

        graph.addSeries(series1);
        graph.addSeries(series2);

        series1.setTitle(myDisplayName);
        series2.setTitle(hisDisplayName);

        series1.setThickness(10);
        series2.setThickness(10);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setFixedPosition(0, 0);
        graph.getLegendRenderer().setSpacing(20);
        graph.getLegendRenderer().setBackgroundColor(getResources().getColor(android.R.color.transparent));

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setYAxisBoundsManual(true);

        graph.getViewport().setMaxY(roundCount / 2);
        graph.getViewport().setMinY(0);

        graph.getViewport().setMaxX(roundCount / 2);
        graph.getViewport().setMinX(0);

        graph.getGridLabelRenderer().setNumHorizontalLabels((roundCount / 2) + 1);
        graph.getGridLabelRenderer().setNumVerticalLabels((roundCount / 2) + 1);

        graph.getGridLabelRenderer().setHorizontalLabelsColor(getResources().getColor(android.R.color.white));
        graph.getGridLabelRenderer().setVerticalLabelsColor(getResources().getColor(android.R.color.white));

        /* ? */
        graph.getGridLabelRenderer().setGridColor(getResources().getColor(android.R.color.white));

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

    public class Guessing {

        String firstS = "", secondS = "", thirdS = "";
        TextView firstTW, secondTW, thirdTW;
        Button switchPlayer;
        Animation animFadeIn;
        ImageView cross;
        boolean clicked = false;

        public void start() {

            animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);

            if (matchData == null) {
                showWarning("Error", getString(R.string.general_error));
                return;
            }

            firstTW = (TextView) findViewById(R.id.firstTW);
            secondTW = (TextView) findViewById(R.id.secondTW);
            thirdTW = (TextView) findViewById(R.id.thirdTW);
//            switchPlayer = (Button) findViewById(R.id.switch_player);

            firstS = matchData.getSentences().get(0);
            secondS = matchData.getSentences().get(1);
            thirdS = matchData.getSentences().get(2);

            firstTW.setText(firstS);
            secondTW.setText(secondS);
            thirdTW.setText(thirdS);

            firstTW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!clicked) {
                        areYouSureDialog(0);
                    }
                }
            });

            secondTW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!clicked) {
                        areYouSureDialog(1);
                    }
                }
            });

            thirdTW.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!clicked) {
                        areYouSureDialog(2);
                    }
                }
            });
        }

        public void areYouSureDialog(final int pos) {

            final AlertDialog.Builder builder = new AlertDialog.Builder(MpWifi.this);

            builder.setMessage("Are you sure?");

            setTitleColor(getResources().getColor(R.color.truth));

            final int liePos = matchData.getLiePos();

            builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();

                    clicked = true;

                    matchData.setCurrentRound(matchData.getCurrentRound() + 1);

                    if (pos == liePos) {
                        matchData.setScores(player, matchData.getScores(player) + winPoint);

                        graphHistory.addMyHistory(true);

                        Toast.makeText(getApplicationContext(), "You\'re a fuckin g! " + matchData.getScores().get(0) + " - " + matchData.getScores().get(1) + " " + matchData.getCurrentRound() + "/" + roundCount, Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "You dumb idiot " + matchData.getScores().get(0) + " - " + matchData.getScores().get(1) + " " + matchData.getCurrentRound() + "/" + roundCount, Toast.LENGTH_SHORT).show();

                        graphHistory.addMyHistory(false);
                    }

                    graphHistory.setLastScore(matchData.getScores());

                    if (matchData.getCurrentRound() == roundCount) {
                        gameState = 3;
                    } else {
                        gameState = 1;
                    }

                    final View[] tvs = {findViewById(R.id.firstTW), findViewById(R.id.secondTW), findViewById(R.id.thirdTW)};
                    tvs[pos].setBackgroundResource(R.drawable.custom_edittext_clicked);
                    tvs[liePos].setBackgroundResource(R.drawable.custom_edittex_lie);

                    new CountDownTimer(5000, 5000) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            setViewVisibility();
                            clicked = false;

                            tvs[pos].setBackgroundResource(R.drawable.custom_edittext_truth);
                            tvs[liePos].setBackgroundResource(R.drawable.custom_edittext_truth);
                        }
                    }.start();
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

    public void metoda1() {

        if (true) {

            timer = new CountDownTimer(100, 10) {
                @Override
                public void onTick(long millisUntilFinished) {

                    float rotation = loading1.getRotation();
                    loading1.setRotation(rotation + 3);

                    float rotation2 = loading2.getRotation();
                    loading2.setRotation(rotation2 - 3);
                }

                @Override
                public void onFinish() {

                    metoda1();

                }
            }.start();
        }
    }

    @Override
    public void onBackPressed() {
        if (gameState == -1) {
            Intent toMenu = new Intent(MpWifi.this, MainActivity.class);
            this.finish();
            startActivity(toMenu);
            return;
        }

        gameState = -1;
        setViewVisibility();
    }
}