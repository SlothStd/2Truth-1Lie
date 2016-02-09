package sloth.twotruthsonelie;

import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.widget.ListView;

/**
 * Created by Daniel on 1/28/2016.
 */
public class Settings extends PreferenceActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        setTheme(R.style.whiteText);

        ListView listView = getListView();

        listView.setBackgroundColor(Color.TRANSPARENT);
        listView.setCacheColorHint(getResources().getColor(R.color.white));
        listView.setBackgroundColor(getResources().getColor(R.color.purple_ish));
    }
}
