package sloth.twotruthsonelie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

/**
 * Created by Daniel on 1/27/2016.
 */
public class MainActivity extends Activity {

    Button onePhone, twoPhones, settings;
    Animation fadeIn;
    Animation fadeInSlower;
    Animation fadeInSlowest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        onePhone = (Button) findViewById(R.id.Onephone);
        twoPhones = (Button) findViewById(R.id.Twophones);
        settings = (Button) findViewById(R.id.Settings);

        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeInSlower = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slower);
        fadeInSlowest = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slowest);


        onePhone.setVisibility(View.VISIBLE);
        onePhone.startAnimation(fadeIn);
        onePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent singleplayer = new Intent(MainActivity.this, OnePhone.class);
                startActivity(singleplayer);

            }
        });

        twoPhones.setVisibility(View.VISIBLE);
        twoPhones.startAnimation(fadeInSlower);
        twoPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        settings.setVisibility(View.VISIBLE);
        settings.startAnimation(fadeInSlowest);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toSettigns = new Intent(MainActivity.this, Settings.class);
                startActivity(toSettigns);

            }
        });

    }
}
