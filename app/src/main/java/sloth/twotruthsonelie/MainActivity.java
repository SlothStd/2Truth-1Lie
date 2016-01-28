package sloth.twotruthsonelie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

/**
 * Created by Daniel on 1/27/2016.
 */
public class MainActivity extends Activity {

    Button onePhone, twoPhones, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onePhone = (Button) findViewById(R.id.Onephone);
        twoPhones = (Button) findViewById(R.id.Twophones);
        settings = (Button) findViewById(R.id.Settings);

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

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toSettigns = new Intent(MainActivity.this, Settings.class);
                startActivity(toSettigns);

            }
        });

    }
}
