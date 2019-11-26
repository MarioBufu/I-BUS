package mbapp.com.btcarcontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    CheckBox enableBT,visibleBT;
    TextView deviceName,pairedDevices;
    ListView pairedDevicesList;

    BluetoothAdapter btAdapter;
    Set<BluetoothDevice> pairedBTDevices;
    ArrayList<BluetoothDevice> pairedBTDevicesList = new ArrayList<>();
    CountDownTimer countDownTimer = new CountDownTimer(120000,1000) {
        @Override
        public void onTick(long l) {
            if(visibleBT.isChecked()) {
                visibleBT.setText(String.format("Device visible (%d)", l / 1000));
            }
        }

        @Override
        public void onFinish() {
            visibleBT.setText(R.string.discoverable);
            visibleBT.setChecked(false);
        }
    };

    private static final int BT_ENABLE_CHECK_CODE = 1;
    private static final int BT_DISCOVERABLE_CHECK_CODE = 2;
    public static  final String EXTRA_ADDRESS = "extra address";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        enableBT = findViewById(R.id.enableBT);
        enableBT.setText(R.string.enableBT);
        visibleBT = findViewById(R.id.visibleBT);
        visibleBT.setText(R.string.discoverable);
        deviceName = findViewById(R.id.deviceName);
        pairedDevicesList = findViewById(R.id.pairedList);
        pairedDevices = findViewById(R.id.pairedDevices);

        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter == null)
        {
            Toast.makeText(this,"BT adapter not found",Toast.LENGTH_SHORT).show();
        }
        if (btAdapter.getName() != null)
        {
            deviceName.setText(btAdapter.getName());
        }

        if (btAdapter.isEnabled())
        {
            enableBT.setChecked(true);
            enableBT.setText(R.string.disableBT);
        }

        enableBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(!b) {
                    btAdapter.disable();
                    Toast.makeText(MainActivity.this,"Bluetooth Off",Toast.LENGTH_SHORT).show();
                    enableBT.setText(R.string.enableBT);
                }
                else {
                    Intent btOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(btOn,BT_ENABLE_CHECK_CODE);
                    Toast.makeText(MainActivity.this,"Bluetooth On",Toast.LENGTH_SHORT).show();
                    //the enableBT.set will becalled when activity respond
                }
            }
        });

        visibleBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!b) {
                    visibleBT.setText(R.string.discoverable);
                    countDownTimer.cancel();
                }
                else {
                    Intent discoverabeOn = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                    startActivityForResult(discoverabeOn,BT_DISCOVERABLE_CHECK_CODE);
                }
            }
        });

        pairedDevicesList.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Toast.makeText(MainActivity.this,adapterView.getItemAtPosition(index).toString(),Toast.LENGTH_SHORT).show();
                //call method to connect to bt
                //if connection is successful start a new activity with bt communication
                BluetoothDevice bluetoothDevice = pairedBTDevicesList.get(index);
                String address = bluetoothDevice.getAddress();
                Intent controlCar = new Intent(MainActivity.this,SendCommandsActivity.class);
                controlCar.putExtra(EXTRA_ADDRESS,address);
                startActivity(controlCar);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == BT_ENABLE_CHECK_CODE) {
            if (resultCode == RESULT_OK) {
                enableBT.setText(R.string.disableBT);
            }
            else {
                enableBT.setText(R.string.enableBT);
                enableBT.setChecked(false);
            }
        }
        else if (requestCode == BT_DISCOVERABLE_CHECK_CODE) {
            if (resultCode == 120) {
                visibleBT.setText(R.string.deviceVisible);
                // countdown 2 min
                countDownTimer.start();
            }
            else if (resultCode == RESULT_CANCELED) {
                visibleBT.setText(R.string.discoverable);
                visibleBT.setChecked(false);
            }
        }
    }

    public void itemPressed(View v){
        pairedBTDevices = btAdapter.getBondedDevices();

        ArrayList<String> list = new ArrayList<>();

        for(BluetoothDevice bt : pairedBTDevices){
            list.add(bt.getName());
            pairedBTDevicesList.add(bt);
        }

        ArrayAdapter arrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,list);
        pairedDevicesList.setAdapter(arrayAdapter);
    }
}
