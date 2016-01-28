package sloth.twotruthsonelie;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by Daniel on 1/28/2016.
 */
public class ScoreActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);
    }

    @Override
    public void onBackPressed() {
        Intent toMenu = new Intent(ScoreActivity.this, MainActivity.class);
        startActivity(toMenu);

        //Tu sa bude mazať skore
    }
}
