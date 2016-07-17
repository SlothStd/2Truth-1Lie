package sloth.twotruthsonelie;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


/**
 * Created by Daniel on 1/27/2016.
 */

public class MainActivity extends Activity {

    Button onePhone, twoPhones, settings;

    Animation fadeIn;
    Animation fadeInSlower;
    Animation fadeInSlowest;
    Animation slideUP, slideDOWN;
    ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getIntent().getFlags() == 0x14000000) {
            Intent multi = new Intent(MainActivity.this, MpWifi.class);
            startActivity(multi);
        }

        if (hasSoftKeys()){

            final float scale = getResources().getDisplayMetrics().density;
            int top = (int) (24 * scale + 0.5f);
            int bottom = (int) (48 * scale + 0.5f);

            findViewById(R.id.mainMainLayout).setPadding(0, top, 0, bottom);
        }else {
            findViewById(R.id.mainMainLayout).setPadding(0, 0, 0, 0);
        }

        onePhone = (Button) findViewById(R.id.Onephone);
        twoPhones = (Button) findViewById(R.id.Twophones);
        settings = (Button) findViewById(R.id.settings);
        logo = (ImageView) findViewById(R.id.appNameLogo);

        ////////////////////////Buttony namiesto WIFI → Activita////////////////

        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in);
        fadeInSlower = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slower);
        fadeInSlowest = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in_slowest);
        slideDOWN = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_down);
        slideUP = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_up);

        onePhone.setVisibility(View.VISIBLE);
        onePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent singleplayer = new Intent(MainActivity.this, OnePhone.class);
                startActivity(singleplayer);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                MainActivity.this.finish();
            }

        });

        twoPhones.setVisibility(View.VISIBLE);
        twoPhones.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent multiplayer = new Intent(MainActivity.this, MpWifi.class);
                MainActivity.this.finish();
                startActivity(multiplayer);

            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent toSettigns = new Intent(MainActivity.this, Settings.class);
                startActivity(toSettigns);
                MainActivity.this.finish();

            }
        });
    }

    public boolean hasSoftKeys(){
        boolean hasSoftwareKeys = true;

        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN_MR1){
            Display d = this.getWindowManager().getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            hasSoftwareKeys =  (realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0;
        }else{
            boolean hasMenuKey = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean hasBackKey = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            hasSoftwareKeys = !hasMenuKey && !hasBackKey;
        }
        return hasSoftwareKeys;
    }

    @Override
    public void onBackPressed() {

        MainActivity.this.finish();
    }
}
