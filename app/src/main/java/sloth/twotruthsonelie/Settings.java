package sloth.twotruthsonelie;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by Daniel on 1/28/2016.
 */
public class Settings extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setTheme(R.style.whiteText);

        PreferenceScreen screen = getPreferenceScreen();

        ListView listView = getListView();

        listView.setBackgroundColor(Color.TRANSPARENT);
        listView.setCacheColorHint(getResources().getColor(R.color.white));
        listView.setBackgroundColor(getResources().getColor(R.color.purple_ish));

        PreferenceCategory margin = (PreferenceCategory) findPreference("margin");


        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
        if (currentapiVersion >= Build.VERSION_CODES.KITKAT){

        } else {
            screen.removePreference(margin);
        }


        Preference share = (Preference) findPreference("share");
        share.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                Toast.makeText(Settings.this, "rekt", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_TEXT, "YOLO TEST KAPPA 123");
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

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Settings.this, MainActivity.class);
        finish();
        startActivity(intent);
    }


}
