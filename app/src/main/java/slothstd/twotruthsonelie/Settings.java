package slothstd.twotruthsonelie;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Daniel on 1/28/2016.
 */
public class Settings extends PreferenceActivity {

    private EditText player1, player2;
    private Button set, cancle;
    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setTheme(R.style.whiteText);

        PreferenceScreen screen = getPreferenceScreen();
        prefs = getSharedPreferences("playerNames", MODE_PRIVATE);
        editor = prefs.edit();

        ListView listView = getListView();

        listView.setBackgroundColor(Color.TRANSPARENT);
        listView.setCacheColorHint(getResources().getColor(R.color.white));
        listView.setBackgroundColor(getResources().getColor(R.color.purple_ish));

        ListPreference rounds = (ListPreference) findPreference("setRounds");

        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String roundsPrefs = SP.getString("setRounds", "0");
        try {
            rounds.setValueIndex(Integer.parseInt(roundsPrefs) - 1);
        } catch (Exception e) {
            rounds.setValueIndex(0);
        }

        Preference share = (Preference) findPreference("share");
        share.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=slothstd.twotruthsonelie");
                intent.setType("text/plain");
                intent.setPackage("com.facebook.orca");
                try {
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(Settings.this, "Please Install Facebook Messenger", Toast.LENGTH_SHORT).show();
                }
                        return true;
            }
        });

        Preference howTo = (Preference) findPreference("howTo");
        howTo.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                DialogHowTo dialog = new DialogHowTo();
                dialog.Dialog(Settings.this);

                return true;
            }
        });

        Preference names = (Preference) findPreference("names");
        names.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                showDialog();

                return true;
            }
        });

    }

    private void showDialog() {


        final Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_player_names);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        player1 = (EditText) dialog.findViewById(R.id.player1ET);
        player2 = (EditText) dialog.findViewById(R.id.player2ET);
        cancle = (Button) dialog.findViewById(R.id.Cancle);
        set = (Button) dialog.findViewById(R.id.Set);

        try {
            player1.setText(prefs.getString("playerOneName", null));
        } catch (NullPointerException e) {
            player1.setText("Player 1");
        }

        try {
            player2.setText(prefs.getString("playerTwoName", null));
        } catch (NullPointerException e) {
            player2.setText("Player 2");
        }

        set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String playerOneName = player1.getText().toString();
                String playerTwoName = player2.getText().toString();

                editor.putString("playerOneName", playerOneName).apply();
                editor.putString("playerTwoName", playerTwoName).apply();

                dialog.dismiss();

            }
        });

        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();

            }
        });

        dialog.show();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Settings.this, MainActivity.class);
        finish();
        startActivity(intent);
    }


}
