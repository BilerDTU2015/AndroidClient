package com.dtu.biler;

import java.util.ArrayList;
import java.util.Set;

import com.dtu.biler.submenus.BtTerminal;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


public class TestActivity extends Activity implements OnClickListener{

    private Toast toast;
    Context context = this;

    private boolean paired;
    BluetoothAdapter mBluetoothAdapter;
    
    // For discovered bt devices
    ArrayAdapter<String> mArrayAdapter;
    ListView listView;
    
    /*
     * UI stuff
     */
    private Button mbtnPairDevice;
    private Button mbtnOpenTerminal;


    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        toast = Toast.makeText(this,"",Toast.LENGTH_LONG);


        setContentView(R.layout.test_activity);



        mbtnPairDevice = (Button) this.findViewById(R.id.btnMainPairBT);
        mbtnPairDevice.setOnClickListener(this);
        mbtnOpenTerminal = (Button) this.findViewById(R.id.btnMainBtTerminal);
        mbtnOpenTerminal.setOnClickListener(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
        }
        
        listView = (ListView) findViewById(R.id.sampleListView);
        
        ArrayList<String> values = new ArrayList();
    	values.add("hello");
    	values.add("world");
      
        mArrayAdapter = createAdapter(values);
        listView.setAdapter(mArrayAdapter);
        
        // ListView Item Click Listener
        listView.setOnItemClickListener(new OnItemClickListener() {

              @Override
              public void onItemClick(AdapterView<?> parent, View view,
                 int position, long id) {
                
               // ListView Clicked item index
               int itemPosition     = position;
               
               // ListView Clicked item value
               String  itemValue    = (String) listView.getItemAtPosition(position);
                  
                // Show Alert 
                Toast.makeText(getApplicationContext(),
                  "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                  .show();
             
              }
        });
    

        
     	// Register the BroadcastReceiver
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        registerReceiver(mReceiver, filter); // Don't forget to unregister during onDestroy
        

    }
    
    @Override
    protected void onResume() {

        super.onResume();
        updateButtonStates();
    }
    void updateButtonStates() {
        mbtnPairDevice.setEnabled(true);
        mbtnOpenTerminal.setEnabled(paired);
    }
    
    @Override
    public void onPause() {
        super.onPause();
        // unregister broadcastreceiver
    }
    
    @Override
    public void onClick(View v)
    {
        switch (v.getId()) {

            case R.id.btnMainPairBT:
            	scanForDevices();
                break;

            case R.id.btnMainBtTerminal:
            	if (paired) {
            		openTerminal();
            	}
            	break;
        }
    }
    
    private void scanForDevices(){
    	// check if BT is enabled
    	int REQUEST_ENABLE_BT = 1;
    	if (!mBluetoothAdapter.isEnabled()) {
    	    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    	    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    	}
    	
//    	// see if there is allready paired devices 
    	Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
    	ArrayList<String> deviceInfos = new ArrayList();
    	// If there are paired devices
    	if (pairedDevices.size() > 0) {
    	    // Loop through paired devices
    	    for (BluetoothDevice device : pairedDevices) {
    	        // Add the name and address to an array adapter to show in a ListView
    	    	deviceInfos.add(device.getName() + "\n" + device.getAddress());
    	      //  mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
    	    }
    	    listView.setAdapter(createAdapter(deviceInfos) );
    	    
    	}
    	
//    	ArrayList<String> strs = new ArrayList();
//    	strs.add("well");
//    	strs.add("then");
//    	ArrayAdapter<String> asdf = createAdapter(strs);
//    	listView.setAdapter(asdf);
    	
    	//mArrayAdapter.add("hello" + "\n" + "world");
    	//toast.setText("trying to scan");
    	//toast.show();
    }
    
    private ArrayAdapter<String> createAdapter(ArrayList<String> data){
    	return new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, data.toArray(new String[data.size()]));
    }
    
    
    private void openTerminal() {

        Intent intent = new Intent(TestActivity.this,   BtTerminal.class);
        startActivity(intent);  
    }
    
    
 // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
        	toast.setText("receive");
        	toast.show();
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				// Add the name and address to an array adapter to show in a ListView
                mArrayAdapter.add(device.getName() + "\n" + device.getAddress());
            }
           // listView.setAdapter(mArrayAdapter);
        }
    };
}

