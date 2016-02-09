package sloth.twotruthsonelie;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by Daniel on 1/30/2016.
 */
public class Bluetooth extends Activity {

    private final static int REQUEST_ENABLE_BT = 1;
    ArrayAdapter<String> mArrayAdapter;
    Button joinLobby, makeLobby;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bluetooth_layout);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(Bluetooth.this, "Bluetooth not supported", Toast.LENGTH_SHORT).show();
            finish();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        } else {

        }

        joinLobby = (Button) findViewById(R.id.joinLobby);
        makeLobby = (Button) findViewById(R.id.makeLobby);

        joinLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findLobby = new Intent(Bluetooth.this, JoinLobby.class);
                startActivity(findLobby);
            }
        });


        makeLobby.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lobby = new Intent(Bluetooth.this, Lobby.class);
                startActivity(lobby);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(Bluetooth.this, "Bluetootch must be enabled to continue", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}
