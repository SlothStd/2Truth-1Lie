package sloth.twotruthsonelie;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Daniel on 1/28/2016.
 */
public class Settings extends Activity  {

    EditText setRounds;
    Integer rounds = 0;
    String roundsH;
    Button apply;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_layout);

        setRounds = (EditText) findViewById(R.id.setRounds);
        apply = (Button) findViewById(R.id.apply);

        apply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("totalRounds", MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();

                try {
                    roundsH = setRounds.getText().toString();
                    rounds = Integer.parseInt(roundsH);
                } catch (NullPointerException e) {}

                Toast.makeText(Settings.this, "Roudns set to: " + rounds, Toast.LENGTH_SHORT).show();

                editor.putInt("rounds", rounds).apply();
            }
        });


    }
}
