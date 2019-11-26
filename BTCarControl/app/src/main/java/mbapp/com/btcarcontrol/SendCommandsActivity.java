package mbapp.com.btcarcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


public class SendCommandsActivity extends AppCompatActivity {

    static final String TAG = "SendCommand CLASS";
    static final String DWD = "driverdown";
    static final String DWU = "driverup";
    static final String PWD = "passengerdown";
    static final String PWU = "passengerup";
    static final String LOCK = "lock";
    static final String UNLOCK = "unlock";

    protected UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    protected BluetoothSocket btSocket;
    protected BluetoothAdapter btAdapter;
    protected boolean isConnected = false;
    boolean debugMode = false;
    protected String address;

    Button button_driverWindowDown, button_driverWindowUp, button_passengerWindowDown, button_passengerWindowUp, button_lock, button_unlock, button_sendCommand;
    EditText commandInputText;
    TextView outputDebugMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_commands);

        address = getIntent().getStringExtra(MainActivity.EXTRA_ADDRESS);

        button_driverWindowDown = findViewById(R.id.buttonDWD);
        button_driverWindowUp = findViewById(R.id.buttonDWU);
        button_passengerWindowDown = findViewById(R.id.buttonPWD);
        button_passengerWindowUp = findViewById(R.id.buttonPWU);
        button_lock = findViewById(R.id.buttonLock);
        button_unlock = findViewById(R.id.buttonUnlock);
        button_sendCommand = findViewById(R.id.buttonSendCommand);
        commandInputText = findViewById(R.id.editText);
        outputDebugMessage = findViewById(R.id.textView);

        outputDebugMessage.setText(R.string.connecting);

        new ConnectToDevice(this).execute();

    }

    public void sendCommand(String command) {
        Log.d(TAG,"called send command with "+ command);
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write(command.getBytes());
                if(debugMode) {
                    outputDebugMessage.setText("sending: "+command+'\n');
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void disconnect() {
        if (btSocket != null){
            try {
                btSocket.close();
                btSocket = null;
                isConnected = false;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        finish();
    }

    public void sendCommand(View view) {
        int viewID = view.getId();
        if(viewID == button_driverWindowDown.getId()){
            sendCommand(DWD);
        }else if (viewID == button_driverWindowUp.getId()) {
            sendCommand(DWU);
        }else if (viewID == button_passengerWindowDown.getId()) {
            sendCommand(PWD);
        }else if (viewID == button_passengerWindowUp.getId()) {
            sendCommand(PWU);
        }else if (viewID == button_lock.getId()) {
            sendCommand(LOCK);
        }else if (viewID == button_unlock.getId()) {
            sendCommand(UNLOCK);
        }else if (viewID == button_sendCommand.getId()) {
            String input = commandInputText.getText().toString();
            if(input.equals("start debug")) {
                debugMode = true;
                commandInputText.setText("");
                Toast.makeText(this,"Debug enabled",Toast.LENGTH_LONG).show();
            } else if (input.equals("stop debug")) {
                debugMode = false;
                commandInputText.setText("");
                outputDebugMessage.setText("");
                Toast.makeText(this,"Debug disabled",Toast.LENGTH_LONG).show();
            } else {
                sendCommand(input);
            }
        }

    }


    private class ConnectToDevice extends AsyncTask{

        private boolean connected = true;
        private Context context;

        public ConnectToDevice(Context c)
        {
            context = c;
        }
        @Override
        protected void onPreExecute()
        {

        }
        @Override
        protected Object doInBackground(Object[] objects) {
            if(btSocket == null || !isConnected) {
                btAdapter = BluetoothAdapter.getDefaultAdapter();
                btAdapter.cancelDiscovery();
                BluetoothDevice btDevice = btAdapter.getRemoteDevice(address);
                Log.d("async bt connection","connect to address: "+address);
                try {
                    btSocket = btDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    btSocket.connect();
                } catch (IOException e) {
                    connected = false;
                    e.printStackTrace();
                }

            }
            return null;
        }

        @Override
        protected void onPostExecute(Object result) {
            if (connected) {
                isConnected = true;
                Toast.makeText(SendCommandsActivity.this,"Connected" , Toast.LENGTH_SHORT).show();
                outputDebugMessage.setText("");
            }
            else {
                Toast.makeText(SendCommandsActivity.this,"Could not connect",Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }
}
