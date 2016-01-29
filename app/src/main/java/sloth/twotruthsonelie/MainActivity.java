package sloth.twotruthsonelie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.*;
import com.google.example.games.basegameutils.BaseGameUtils;

/**
 * Created by Daniel on 1/27/2016.
 */
public class MainActivity extends Activity {

    Button onePhone, twoPhones, settings, wifi, bt;
    boolean is2PhoneOpen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onePhone = (Button) findViewById(R.id.Onephone);
        twoPhones = (Button) findViewById(R.id.Twophones);
        settings = (Button) findViewById(R.id.Settings);
        wifi = (Button) findViewById(R.id.WiFi);
        bt = (Button) findViewById(R.id.BT);

        onePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent singleplayer = new Intent(MainActivity.this, OnePhone.class);
                startActivity(singleplayer);

            }
        });

        twoPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                is2PhoneOpen = true;

                onePhone.setVisibility(View.GONE);
                twoPhones.setVisibility(View.GONE);
                settings.setVisibility(View.GONE);

                wifi.setVisibility(View.VISIBLE);
                bt.setVisibility(View.VISIBLE);

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toSettigns = new Intent(MainActivity.this, Settings.class);
                startActivity(toSettigns);

            }
        });

        wifi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent singleplayer = new Intent(MainActivity.this, MpWifi.class);
                startActivity(singleplayer);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onBackPressed() {

        if (is2PhoneOpen) {

            is2PhoneOpen = false;

            onePhone.setVisibility(View.VISIBLE);
            twoPhones.setVisibility(View.VISIBLE);
            settings.setVisibility(View.VISIBLE);

            wifi.setVisibility(View.GONE);
            bt.setVisibility(View.GONE);
        }
        else {
            super.onBackPressed();
        }
    }
}
