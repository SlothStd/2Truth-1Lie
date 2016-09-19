package slothstd.twotruthsonelie;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.support.v7.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
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

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.ImageManager;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.Player;
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

import slothstd.twotruthsonelie.util.IabBroadcastReceiver;
import slothstd.twotruthsonelie.util.IabHelper;
import slothstd.twotruthsonelie.util.IabResult;
import slothstd.twotruthsonelie.util.Inventory;
import slothstd.twotruthsonelie.util.Purchase;

/**
 * Created by Robo on 28-Jan-16.
 * E:\Program Files\Android Studio\SDK\platform-tools
 * adb install -r C:\AndroidStudio\2Truth-1Lie\app\app-release.apk
 */
public class MpWifi extends Activity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnInvitationReceivedListener,
        OnTurnBasedMatchUpdateReceivedListener,
        IabBroadcastReceiver.IabBroadcastListener,
        View.OnClickListener,
        RewardedVideoAdListener,
        View.OnKeyListener{

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
    final static int RC_BUY_REQUEST = 10001;

    // How long to show toasts.
    final static int TOAST_DELAY = Toast.LENGTH_SHORT;

    //Game state
    public int gameState = 0;

    // This is the current match we're in; null if not loaded
    public TurnBasedMatch mMatch;
    public MatchData matchData = new MatchData();

    Tocenie tocenieGuess, tocenieNyt;

    private String myID, hisID;
    private ArrayList<String> IDs;
    private int player;
    private String myDisplayName, hisDisplayName;
    private String myPhoto, hisPhoto, myBigPhoto;

    private GraphHistory graphHistory;

    public static final int winPoint = 1;
    public static final int gameFinishedXp = 10;
    public static final int winBonus = 15;
    public static final int tieBonus = 5;
    public static final int roundWinBonus = 2;

    private EditText firstS, secondS, thirdS;

    int totalWins, totalLoses;

    int gameTokens;

    ProgressBar score;
    ProgressDialog progress;
    ObjectAnimator animation;
    CountDownTimer timer2;
    int points;
    ProgressBar player1Progress, player2Progress;
    CheckBox firstTruth, firstLie;
    CheckBox secondTruth, secondLie;
    CheckBox thirdTruth, thirdLie;

    LinearLayout layout;
    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);

    boolean isPremium = false;
    boolean isDeveloper = false;
    boolean isUnlimited = false;
    static final String SKU_PREMIUM = "premium_account";

    IabHelper mHelper;
    IabBroadcastReceiver mBroadcastReceiver;

    private RewardedVideoAd mAd;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mp_wifi);


        mAd = MobileAds.getRewardedVideoAdInstance(this);
        mAd.setRewardedVideoAdListener(this);
        loadRewardedVideoAd();

        showSpinner();


        // Create the Google API Client with access to Plus and Games
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API).addScope(Plus.SCOPE_PLUS_LOGIN)
                .addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .build();



        //initialize and load bigBanner add for future call
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-4648715887566496/7671269765");
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();

        // Setup signin and signout buttons
        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_out_button).setOnClickListener(this);

        firstS = (EditText) findViewById(R.id.firstS);
        secondS = (EditText) findViewById(R.id.secondS);
        thirdS = (EditText) findViewById(R.id.thirdS);
        danyhoDivneCheckboxy();

        findViewById(R.id.menu_avatar_layout).setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                Toast.makeText(getApplicationContext(), R.string.success_rate_msg, Toast.LENGTH_LONG).show();

                return false;
            }
        });
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice("SEE_YOUR_LOGCAT_TO_GET_YOUR_DEVICE_ID")
                .build();

        mInterstitialAd.loadAd(adRequest);
    }

    private void loadRewardedVideoAd() {
        mAd.loadAd("ca-app-pub-4648715887566496/4299000966", new AdRequest.Builder().build());
    }

    @Override
    public void onRewarded(RewardItem reward) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor1 = sp.edit();
        Toast.makeText(MpWifi.this, "3 Game Tokens were added", Toast.LENGTH_SHORT).show();
        gameTokens = 3;
        editor1.putInt("gameTokens", gameTokens).apply();
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onResume() {
        mAd.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
        mAd.pause(this);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mAd.destroy(this);

        unregisterReceiver(mBroadcastReceiver);

        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();

        /*if (hasSoftKeys()) {

            final float scale = getResources().getDisplayMetrics().density;
            int top = (int) (24 * scale + 0.5f);
            int bottom = (int) (48 * scale + 0.5f);

            findViewById(R.id.MpWifi_main_layout).setPadding(0, top, 0, bottom);
        } else {
            findViewById(R.id.MpWifi_main_layout).setPadding(0, 0, 0, 0);
        }*/

        updateLevels(0);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        try {
            gameTokens = sp.getInt("gameTokens", 3);

        } catch (NullPointerException e) {
            gameTokens = 3;
        }


        Log.d(TAG, "Game tokens" + String.valueOf(gameTokens));

        loadSP();

        updateMenu();

        tocenieGuess = new Tocenie(findViewById(R.id.progress1_guess), findViewById(R.id.progress2_guess));
        tocenieNyt = new Tocenie(findViewById(R.id.progress1_nyt), findViewById(R.id.progress2_nyt));

        Log.d(TAG, "onStart(): Connecting to Google APIs");
        mGoogleApiClient.connect();

        createIabHelper();

        gameState = -1;
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

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences.Editor editor1 = sp.edit();
        editor1.putInt("gameTokens", gameTokens).apply();

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

    public void clearSp(){
        if (mMatch != null) {

            Log.d(TAG, "clearing SP");

            SharedPreferences prefs = getSharedPreferences(mMatch.getMatchId(), Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear().apply();
        }
    }

    @Override
    public void onConnected(Bundle connectionHint) {

        Log.d(TAG, "onConnected(): Connection successful");

        dismissSpinner();

        ImageView image = (ImageView) findViewById(R.id.MpWifi_menu_avatar);

        Player me = Games.Players.getCurrentPlayer(mGoogleApiClient);

        ImageManager mgr = ImageManager.create(this);
        mgr.loadImage(image, me.getHiResImageUri());
        ImageView defaultImage = (ImageView) findViewById(R.id.defaultImage);
        defaultImage.setVisibility(View.GONE);

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

        SharedPreferences isPremiumSP = getSharedPreferences("PREMIUM", MODE_PRIVATE);
        isDeveloper = isPremiumSP.getBoolean("isDeveloper", false);
        isUnlimited = isPremiumSP.getBoolean("isUnlimited", false);

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

        dismissSpinner();

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

    public void createIabHelper(){
        mHelper = new IabHelper(this, getString(R.string.base64));

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.");
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            public void onIabSetupFinished(IabResult result) {
                Log.d(TAG, "Setup finished.");

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    showWarning(null, "Problem setting up in-app billing: " + result);
                    return;
                }

                // Have we been disposed of in the meantime? If so, quit.
                if (mHelper == null) return;

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver = new IabBroadcastReceiver(MpWifi.this);
                IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                registerReceiver(mBroadcastReceiver, broadcastFilter);

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.");
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener);
                } catch (IabHelper.IabAsyncInProgressException e) {
                    showWarning(null, "Error querying inventory. Another async operation in progress.");
                }
            }
        });
    }

    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
            Log.d(TAG, "Query inventory finished.");

            // Have we been disposed of in the meantime? If so, quit.
            if (mHelper == null) return;

            // Is it a failure?
            if (result.isFailure()) {
                showWarning(null, "Failed to query inventory: " + result);
                return;
            }

            Log.d(TAG, "Query inventory was successful.");

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            isPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            Log.d(TAG, "User is " + (isPremium ? "PREMIUM" : "NOT PREMIUM"));
            Log.d(TAG, "Initial inventory query finished; enabling main UI.");

            findViewById(R.id.premiumButton).setVisibility(isPremium ? View.GONE : View.VISIBLE);

            //Save isPremium state for when the internet is turned off
            SharedPreferences isPremiumSP = getSharedPreferences("PREMIUM", MODE_PRIVATE);
            SharedPreferences.Editor editor = isPremiumSP.edit();
            editor.putBoolean("isPremium", isPremium).apply();
        }
    };

    private boolean verifyDeveloperPayload(Purchase premiumPurchase) {
        return true;
    }

    // Displays your inbox. You will get back onActivityResult where
    // you will need to figure out what you clicked on.
    public void onCheckGamesClicked(View view) {
        if (!mGoogleApiClient.isConnected()){
            showWarning("Please sign in", "Sign in with your Google account to use this online feature");
            return;
        }

        Intent intent = Games.TurnBasedMultiplayer.getInboxIntent(mGoogleApiClient);
        startActivityForResult(intent, RC_LOOK_AT_MATCHES);
    }

    // Open the create-game UI. You will get back an onActivityResult
    // and figure out what to do.
    public void onStartMatchClicked(View view) {
        if (!mGoogleApiClient.isConnected()){
            showWarning("Please sign in", "Sign in with your Google account to use this online feature");
            return;
        }

        if (!isPremium && !isDeveloper && !isUnlimited && gameTokens <= 0){
            noGameTokensDialog();
            return;
        }

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
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("isSaved", false).apply();
    }

    // User clicked the "Upgrade to Premium" button.
    public void onUpgradeButtonClicked(View arg0) {
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.upgrade_to_premium));

        builder.setMessage(getString(R.string.upgrade_msg));

        builder.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                launchPurchase();
            }
        });

        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setNeutralButton(R.string.promo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                promoCodeDialog();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    public void launchPurchase(){
        Log.d(TAG, "Upgrade button clicked; launching purchase flow for upgrade.");

        /* TODO: for security, generate your payload here for verification. See the comments on
         *        verifyDeveloperPayload() for more info. Since this is a SAMPLE, we just use
         *        an empty string, but on a production app you should carefully generate this. */
        String payload = "";

        try {
            mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_BUY_REQUEST,
                    mPurchaseFinishedListener, payload);
        } catch (IabHelper.IabAsyncInProgressException e) {
            mHelper.flagEndAsync();
            try {
                mHelper.launchPurchaseFlow(this, SKU_PREMIUM, RC_BUY_REQUEST,
                        mPurchaseFinishedListener, payload);
            } catch (IabHelper.IabAsyncInProgressException e1) {
                showWarning(null, "Error launching purchase flow. Another async operation in progress.");
            }
        }
    }

    IabHelper.OnIabPurchaseFinishedListener mPurchaseFinishedListener = new IabHelper.OnIabPurchaseFinishedListener() {
        public void onIabPurchaseFinished(IabResult result, Purchase purchase) {
            Log.d(TAG, "Purchase finished: " + result + ", purchase: " + purchase);

            // if we were disposed of in the meantime, quit.
            if (mHelper == null) return;

            if (result.isFailure()) {
                showWarning(null, "Error purchasing: " + result);
                return;
            }
            if (!verifyDeveloperPayload(purchase)) {
                showWarning(null, "Error purchasing. Authenticity verification failed.");
                return;
            }

            Log.d(TAG, "Purchase successful.");

            if (purchase.getSku().equals(SKU_PREMIUM)) {
                // bought the premium upgrade!
                Log.d(TAG, "Purchase is premium upgrade. Congratulating user.");
                showWarning(null, "Thank you for upgrading to premium!");
                isPremium = true;

                findViewById(R.id.premiumButton).setVisibility(View.GONE);

                //Save isPremium state for when the internet is turned off
                SharedPreferences isPremiumSP = getSharedPreferences("PREMIUM", MODE_PRIVATE);
                SharedPreferences.Editor editor = isPremiumSP.edit();
                editor.putBoolean("isPremium", isPremium).apply();

            }
            else {
                Log.d(TAG, "onIabPurchaseFinished: Unknown SKU");
            }
        }
    };

    // Sign-in, Sign out behavior

    // Update the visibility based on what state we're in.
    public void setViewVisibility() {
        boolean isSignedIn = (mGoogleApiClient != null) && (mGoogleApiClient.isConnected());

        findViewById(R.id.MpWifi_main_layout).setBackgroundResource(R.color.darker_bg);

        tocenieGuess.stop();
        tocenieNyt.stop();

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

                updateMenu();

                break;

            case 0: //Not your turn

                findViewById(R.id.setTexts).setVisibility(View.GONE);
                findViewById(R.id.chooseTexts).setVisibility(View.GONE);
                findViewById(R.id.notYourTurn).setVisibility(View.VISIBLE);
                findViewById(R.id.gameFinished).setVisibility(View.GONE);

                if (!isPremium && !isDeveloper){
                    if (mInterstitialAd.isLoaded()) {
                        mInterstitialAd.show();
                    } else {

                    }

                }


                Log.d(TAG, "Not your turn");

                tocenieNyt.start();

                break;

            case 1: //Setting sentences

                findViewById(R.id.setTexts).setVisibility(View.VISIBLE);
                findViewById(R.id.chooseTexts).setVisibility(View.GONE);
                findViewById(R.id.notYourTurn).setVisibility(View.GONE);
                findViewById(R.id.gameFinished).setVisibility(View.GONE);

                findViewById(R.id.firstS).setOnKeyListener(this);
                findViewById(R.id.secondS).setOnKeyListener(this);
                findViewById(R.id.thirdS).setOnKeyListener(this);

                Log.d(TAG, "Setting");

                matchData.setHisXp(getSharedPreferences("Levels", Context.MODE_PRIVATE).getInt("xp", 0));
                Log.d(TAG, "Sending xp: " + String.valueOf(getSharedPreferences("Levels", Context.MODE_PRIVATE).getInt("xp", 0)));

                break;

            case 2: //Guessing

                findViewById(R.id.setTexts).setVisibility(View.GONE);
                findViewById(R.id.chooseTexts).setVisibility(View.VISIBLE);
                findViewById(R.id.notYourTurn).setVisibility(View.GONE);
                findViewById(R.id.gameFinished).setVisibility(View.GONE);

                Log.d(TAG, "Guessing");

                tocenieGuess.start();

                new Guessing().start();


                SharedPreferences prefs = getSharedPreferences(mMatch.getMatchId(), Context.MODE_PRIVATE);
                if(prefs.getInt("hisXp", -1) == -1){

                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putInt("hisXp", matchData.getHisXp()).apply();

                    Log.d(TAG, "receiving: " + String.valueOf(matchData.getHisXp()) + " / " + prefs.getInt("hisXp", -1) + " | " + mMatch.getMatchId());
                }
                else {
                    Log.d(TAG, "not receiving anything");
                }


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

    public void updateMenu(){

        double perc = 0;
        try {
            perc = totalWins;
            perc /= (totalWins + totalLoses);
            perc *= 100;

            ((TextView) findViewById(R.id.winRatioTv)).setText(String.valueOf((int) perc) + "%");
        } catch (ArithmeticException e) {
            ((TextView) findViewById(R.id.winRatioTv)).setText("");
        }

        score = (ProgressBar) findViewById(R.id.fakeProgressJustBecauseIcan);
        score.setRotation(40);
        score.setMax(1000000); //100*10k ↓
        points = (int) perc *10000; //daj ten int *10k aby bola animacia smooth

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
    }

    public void noGameTokensDialog(){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.no_more_game_tokens);

        builder.setMessage(R.string.no_more_game_tokens_message);

        builder.setPositiveButton(R.string.buy, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                onUpgradeButtonClicked(null);
            }
        });

        builder.setNegativeButton(R.string.watch, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                videoAd();
            }
        });

        builder.setNeutralButton(R.string.promo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                promoCodeDialog();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    public void videoAd(){

        if (mAd.isLoaded()) {
            mAd.show();
        }


    }

    public void promoCodeDialog(){
        final AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(R.string.promo_title);

        final EditText editText = new EditText(this);
        editText.setSingleLine(true);
        builder.setView(editText, 40, 0, 40, 0);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        dialog = builder.create();
        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validatePromoCode(editText.getText().toString())){
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(getApplicationContext(), R.string.code_invalid, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean validatePromoCode(String code){
        SharedPreferences isPremiumSP = getSharedPreferences("PREMIUM", MODE_PRIVATE);
        SharedPreferences.Editor editor = isPremiumSP.edit();

        switch (code){
            case "dev":

                isDeveloper = true;
                editor.putBoolean("isDeveloper", isDeveloper);

                editor.apply();
                return true;
            case "unlimited":

                isUnlimited = true;
                editor.putBoolean("isUnlimited", isUnlimited);

                editor.apply();
                return true;
            default:
                return false;
        }
    }

    public void finishGame() {
        if (matchData.getSentences().get(0).equals("`")) {
            Log.d(TAG, "Finished2");

            graphHistory.compare(matchData.getScores(), player);

            Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, mMatch.getMatchId())
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                            processResult(result);
                        }
                    });
        } else {
            Log.d(TAG, "Finished1");

            matchData.setSentences(new ArrayList<String>(Arrays.asList(new String[]{"`", "`", "`"})));
            Games.TurnBasedMultiplayer.finishMatch(mGoogleApiClient, mMatch.getMatchId(), matchData.convertData())
                    .setResultCallback(new ResultCallback<TurnBasedMultiplayer.UpdateMatchResult>() {
                        @Override
                        public void onResult(TurnBasedMultiplayer.UpdateMatchResult result) {
                            processResult(result);
                        }
                    });
        }

        SharedPreferences prefs = getSharedPreferences("Levels", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        SharedPreferences matchPrefs = getSharedPreferences(mMatch.getMatchId(), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor2 = matchPrefs.edit();

        int xp = prefs.getInt("xp", 0);
        int hisXp = matchPrefs.getInt("hisXp", -1);

        Log.d(TAG, "found xp: " + xp + ", " + hisXp + " | " + mMatch.getMatchId());

        xp += gameFinishedXp;
        hisXp += gameFinishedXp;

        xp += (matchData.getScores(player) * roundWinBonus);
        hisXp += (matchData.getScores(Math.abs(player - 1)) * roundWinBonus);

        if (matchData.didIWin(player) == null) {

            xp += tieBonus;
            hisXp += tieBonus;
        }
        else if (matchData.didIWin(player)) {

            xp += winBonus;
        }
        else {

            hisXp += winBonus;
        }

        editor.putInt("xp", xp).apply();
        editor2.putInt("hisXp", hisXp).apply();

        totalWins += graphHistory.myWinCount();
        totalLoses += (matchData.getMatchLength() / 2) - graphHistory.myWinCount();

        Log.d(TAG, String.valueOf(xp));


        ((TextView) findViewById(R.id.finish_name1)).setText(myDisplayName);
        ((TextView) findViewById(R.id.finish_name2)).setText(hisDisplayName);

        TextView playerOnePoints, playerTwoPoints;
        int player1, player2;
        ImageView scoreboardBckg, circularBckg1, circularBckg2;

        scoreboardBckg = (ImageView) findViewById(R.id.score_board_bg);
        circularBckg2 = (ImageView) findViewById(R.id.circle_bckg2);
        circularBckg1 = (ImageView) findViewById(R.id.circle_bckg1);

        player1Progress = (ProgressBar) findViewById(R.id.player1_progress_mp);
        player1Progress.setProgress(0);
        player1Progress.setMax(1000000);
        player2Progress = (ProgressBar) findViewById(R.id.player2_progress_mp);
        player2Progress.setProgress(0);
        player2Progress.setMax(1000000);

        new LoadProfileImage((ImageView) findViewById(R.id.user_avatar1_finish)).execute(myPhoto);
        new LoadProfileImage((ImageView) findViewById(R.id.user_avatar2_finish)).execute(hisPhoto);

        playerOnePoints = (TextView) findViewById(R.id.playerOnePoints_mp);
        playerTwoPoints = (TextView) findViewById(R.id.playerTwoPoints_mp);

        player1Progress.setRotation(40);
        player2Progress.setRotation(40);

        player1 = matchData.getScores(player);
        player2 = matchData.getScores(Math.abs(player - 1));

        updateLevels(1);

        if (player1 == player2) {
            scoreboardBckg.setImageResource(R.drawable.scoreboard_green);

            circularBckg2.setImageResource(R.drawable.green_circle_bckg);
            circularBckg1.setImageResource(R.drawable.green_circle_bckg);
        }
        else if (player2 > player1) {
            scoreboardBckg.setRotation(180);

            scoreboardBckg.setImageResource(R.drawable.scoreboard_new);

            circularBckg1.setImageResource(R.drawable.red_circle_bckg);
            circularBckg2.setImageResource(R.drawable.green_circle_bckg);
        }
        else {
            scoreboardBckg.setRotation(0);

            scoreboardBckg.setImageResource(R.drawable.scoreboard_new);

            circularBckg1.setImageResource(R.drawable.green_circle_bckg);
            circularBckg2.setImageResource(R.drawable.red_circle_bckg);
        }

        try {
            playerOnePoints.setText(String.valueOf(player1));
            if (player1 == 1) {
                TextView textView1 = (TextView)findViewById(R.id.points1);
                textView1.setText(R.string.point);
            }
        } catch (NullPointerException e) {
            playerOnePoints.setText("0");
        }
        try {
            playerTwoPoints.setText(String.valueOf(player2));
            if (player2 == 1) {
                TextView textView2 = (TextView)findViewById(R.id.points2);
                textView2.setText(R.string.point);
            }
        } catch (NullPointerException e) {
            playerTwoPoints.setText("0");
        }

        matchData = new MatchData();
        clearSp();
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
    public void startMatch(TurnBasedMatch match, int length) {

        mMatch = match;

        showSpinner();

        getPlayerIDs();

        gameTokens--;

        matchData.setMatchLength(length * 2);

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

        myPhoto = mMatch.getParticipant(myID).getIconImageUrl();
        hisPhoto = mMatch.getParticipant(hisID).getIconImageUrl();

        ((TextView) findViewById(R.id.player1TV_mp)).setText(myDisplayName);
        ((TextView) findViewById(R.id.player2TV_mp)).setText(hisDisplayName);
        ((TextView) findViewById(R.id.player1points_mp)).setText(String.valueOf(matchData.getScores().get(player)));
        ((TextView) findViewById(R.id.player2points_mp)).setText(String.valueOf(matchData.getScores().get(Math.abs(player - 1))));

        new LoadProfileImage((ImageView) findViewById(R.id.avatar1_mp)).execute(myPhoto);
        new LoadProfileImage((ImageView) findViewById(R.id.avatar2_mp)).execute(hisPhoto);

        new LoadProfileImage((ImageView) findViewById(R.id.user_avatar1_guess)).execute(myPhoto);
    }

    public void updateLevels(int ktore) {
        SharedPreferences preferences = getSharedPreferences("Levels", Context.MODE_PRIVATE);
        float xp = preferences.getInt("xp", 0),
                a = 50,
                level = 1,
                previous = 0;

        while (a <= xp) {
            level++;

            previous = a;

            a += a + (Math.round(0.1 * a));
        }

        switch (ktore){
            case 0: // V maine MpWifi
                ((TextView) findViewById(R.id.levelTextView)).setText("Level " + String.valueOf((int) level));

                ProgressBar progressBar = (ProgressBar) findViewById(R.id.levelProgress);

                progressBar.setMax(100);

                float myTemp = ((float) (xp - previous) / (a - previous)) * 100;
                progressBar.setProgress((int) myTemp);

                Log.d(TAG, String.valueOf(progressBar.getProgress()));

                break;

            case 1: // Na konci hry

                SharedPreferences matchPrefs = getSharedPreferences(mMatch.getMatchId(), Context.MODE_PRIVATE);

                float hisXp = matchPrefs.getInt("hisXp", 0);
                float hisLevel = 1;
                float hisA = 50;
                float hisPrevious = 0;

                while (hisA <= hisXp) {
                    hisLevel++;

                    hisPrevious = hisA;

                    hisA += hisA + (Math.round(0.1 * hisA));
                }

                TextView player1Level = (TextView) findViewById(R.id.player1_level_mp);
                player1Level.setText(String.valueOf((int) level));

                TextView player2Level = (TextView) findViewById(R.id.player2_level_mp);
                player2Level.setText(String.valueOf((int) hisLevel));


                //tu si dopln int na levelprogress do progressbarov (to uz mas nastavene iba tieto inty si uprav)
                myTemp = ((float) (xp - previous) / (a - previous)) * 100;
                float hisTemp = ((float) (hisXp - hisPrevious) / (hisA - hisPrevious)) * 100;

                Log.d(TAG, "Me: " + xp + ", " + level + ", " + previous + ", " + a + ", " + myTemp);
                Log.d(TAG, "Him: " + hisXp + ", " + hisLevel + ", " + hisPrevious + ", " + hisA + ", " + hisTemp);

                final int player1LevelProgress = (int) myTemp * 10000; // krat 10k kvoli smooth animacii
                final int player2LevelProgress = (int) hisTemp * 10000;

                CountDownTimer timer = new CountDownTimer(1300, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        animation = new ObjectAnimator().ofInt(player1Progress, "progress", 0, player1LevelProgress);
                        animation.setDuration(2000);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        animation.start();
                    }
                }.start();

                CountDownTimer timer2 = new CountDownTimer(1300, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        animation = new ObjectAnimator().ofInt(player2Progress, "progress", 0, player2LevelProgress);
                        animation.setDuration(2000);
                        animation.setInterpolator(new AccelerateDecelerateInterpolator());
                        animation.start();
                    }
                }.start();
        }


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
        matchData = new MatchData();
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

    public void roundLengthDialog(final TurnBasedMatch match){
        AlertDialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.select_rounds));

        int array;

        if (isPremium || isDeveloper){
            array = R.array.roundsList_premium;
        }
        else {
            array = R.array.roundsList;
        }

        builder.setSingleChoiceItems(array, 2, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if ((isPremium || isDeveloper || isUnlimited) || which == 2) {
                    startMatch(match, which + 1);

                    dialog.dismiss();
                }
                else {
                    onUpgradeButtonClicked(null);
                }
            }
        });

        builder.setNegativeButton(R.string.back, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
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

        roundLengthDialog(match);
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
                /*Toast.makeText(
                        this,
                        "Stored action for later.  (Please remove this toast before release.)",
                        TOAST_DELAY).show();*/
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
        final TextView proceed = (TextView) findViewById(R.id.proceed);
        final Animation fade_in = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slower);

        /////////////////////////FIRST////////////////////////////

        firstS.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                proceed.setVisibility(View.VISIBLE);
                proceed.setAnimation(fade_in);
                proceed.setText(R.string.click_to_continue);

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

                matchData.setLiePos(0);

                return true;
            }
        });


        firstLie = (CheckBox) findViewById(R.id.firstLie);
        firstLie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                matchData.setLiePos(0);
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
                proceed.setText(R.string.click_to_continue);

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

                matchData.setLiePos(1);
                return true;
            }
        });

        secondLie = (CheckBox) findViewById(R.id.secondLie);
        secondLie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                matchData.setLiePos(1);
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
                proceed.setText(R.string.click_to_continue);

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

                matchData.setLiePos(2);

                return true;
            }
        });

        thirdLie = (CheckBox) findViewById(R.id.thirdLie);
        thirdLie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                matchData.setLiePos(2);
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

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDoneClicked(v);

                firstLie.setChecked(false);
                firstS.setBackgroundResource(R.drawable.custom_edittext_truth);
                firstS.setTextColor(getResources().getColor(R.color.black));

                secondLie.setChecked(false);
                secondS.setBackgroundResource(R.drawable.custom_edittext_truth);
                secondS.setTextColor(getResources().getColor(R.color.black));

                thirdLie.setChecked(false);
                thirdS.setBackgroundResource(R.drawable.custom_edittext_truth);
                thirdS.setTextColor(getResources().getColor(R.color.black));

                proceed.setVisibility(View.GONE);
                proceed.setText("");
            }
        });
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

    @Override
    public void receivedBroadcast() {
        Log.d(TAG, "Received broadcast notification. Querying inventory.");
        try {
            mHelper.queryInventoryAsync(mGotInventoryListener);
        } catch (IabHelper.IabAsyncInProgressException e) {
            showWarning(null, "Error querying inventory. Another async operation in progress.");
        }
    }


    private class Guessing{

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

            firstTW.setTextColor(getResources().getColor(R.color.black));
            secondTW.setTextColor(getResources().getColor(R.color.black));
            thirdTW.setTextColor(getResources().getColor(R.color.black));

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

                    } else {
                        graphHistory.addMyHistory(false);
                    }

                    graphHistory.setLastScore(matchData.getScores());

                    if (matchData.getCurrentRound() >= matchData.getMatchLength()) {
                        gameState = 3;
                    } else {
                        gameState = 1;
                    }

                    final View[] tvs = {findViewById(R.id.firstTW), findViewById(R.id.secondTW), findViewById(R.id.thirdTW)};

                    tvs[pos].setBackgroundResource(R.drawable.custom_edittext_clicked);
                    ((TextView) tvs[pos]).setTextColor(getResources().getColor(R.color.white));

                    tvs[liePos].setBackgroundResource(R.drawable.custom_edittex_lie);
                    ((TextView) tvs[liePos]).setTextColor(getResources().getColor(R.color.white));

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

    @Override
    public void onBackPressed() {
        if (gameState == -1) {
            Intent toMenu = new Intent(MpWifi.this, MainActivity.class);

            this.finish();
            startActivity(toMenu);
            return;
        }

        saveSP();

        gameState = -1;
        setViewVisibility();
    }

    private class LoadProfileImage extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public LoadProfileImage(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    private class Tocenie{

        private ProgressBar loading1;
        private ProgressBar loading2;

        private boolean tocenie = false;

        private CountDownTimer timer;

        public Tocenie(View progressBar1, View progressBar2){
            loading1 = (ProgressBar) progressBar1;
            loading2 = (ProgressBar) progressBar2;
        }

        public void start(){
            if (timer != null){
                timer.cancel();
            }

            tocenie = true;
            tocenie();
        }

        private void tocenie(){

            timer = new CountDownTimer(100, 10) {
                @Override
                public void onTick(long millisUntilFinished) {

                    if (tocenie){

                        float rotation = loading1.getRotation();
                        loading1.setRotation(rotation + 3);

                        float rotation2 = loading2.getRotation();
                        loading2.setRotation(rotation2 - 3);
                    }
                }

                @Override
                public void onFinish() {

                    if (tocenie){
                        tocenie();
                    }

                }
            }.start();
        }

        public void stop(){
            tocenie = false;

            if (timer != null){
                timer.cancel();
            }
        }
    }
}