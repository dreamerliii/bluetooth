package com.anddle.anddlechat.view;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.anddle.anddlechat.controller.ConnectionManager;
import com.anddle.anddlechat.R;
import com.anddle.anddlechat.model.DeviceListActivity;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Random;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {
        private TextView textView_uid_information;//机器号显示
        private TextView textView_number_information;//人数显示
        private TextView textView_temperature_information;//温度显示
        private TextView textView_humidity_information;//湿度显示
        private Button button_search;//点击查看温湿度

        private static final String TAG = "ChatActivity";
        private final int RESULT_CODE_BTDEVICE = 0;
        private Socket socket;

        private ConnectionManager mConnectionManager;
        private ListView mMessageListView;
        private MenuItem mConnectionMenuItem;

        private final static int MSG_SENT_DATA = 0;
        private final static int MSG_RECEIVE_DATA = 1;
        private final static int MSG_UPDATE_UI = 2;
        String deviceAddr;

        private Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                byte [] data = (byte []) msg.obj;
                if(data != null) {
                    String str = new String(data);
                    textView_uid_information.setText(deviceAddr);
                    String length = String.valueOf(str.length());
                    if (str.length()<=3){
                        textView_number_information.setText(str);
                    }
                    else {
                        textView_temperature_information.setText(str.substring(2,4)+"℃");
                        textView_humidity_information.setText(str.substring(0,2)+"%");
                    }
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                while(true){
                                    Log.v("I","asdasdasd");
                                    socket = new Socket("62.234.14.145", 8888);
                                    ObjectOutputStream pw = new ObjectOutputStream(socket.getOutputStream());
                                    String uid = (String) textView_uid_information.getText();
                                    String number = (String) textView_number_information.getText();
                                    String temperature = (String) textView_temperature_information.getText();
                                    String humidity = (String) textView_humidity_information.getText();
                                    Log.v("I","654546");
                                    pw.writeObject(new String[]{uid, number, temperature, humidity});
                                    pw.flush();
                                    Thread.sleep(2000);
                                    //关闭输出流
                                    socket.shutdownOutput();
                                    //关闭流
                                    pw.close();
                                    socket.close();
                                }


                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                    //textView_humidity_information.setText(str.substring(5,7));
//                    if (str.substring(str.length()-2).equals("00")){
//                        //我也不知道具体的数据是啥样的，就先这样写了
//                        //如果我不在，就看看怎么截字符串，应该没啥问题我脚着
//                        textView_temperature_information.setText(stwr);
//                        textView_humidity_information.setText(str);
//                    }else {
//                        textView_number_information.setText(str);
//                    }

//                    textView_temperature_information.setText(deviceAddr);
//                    textView_humidity_information.setText(deviceAddr);

                    //mConnectionManager.sendData(uid.getBytes());
                    //textView_number_information.setText(String.valueOf(data[1]));

//                    ChatMessage chatMsg = new ChatMessage();
//                    chatMsg.messageSender = ChatMessage.MSG_SENDER_OTHERS;
//                    chatMsg.messageContent = new String(data);
//
//                    MessageAdapter adapter = (MessageAdapter) mMessageListView.getAdapter();
//                    adapter.add(chatMsg);
//                    adapter.notifyDataSetChanged();
                }
            }
        };


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat);
            //new Thread(networkTask).start();
            textView_uid_information = (TextView) findViewById(R.id.uid_information);
            textView_number_information = (TextView) findViewById(R.id.number_information);
            textView_temperature_information = (TextView) findViewById(R.id.temperature_information);
            textView_humidity_information = (TextView) findViewById(R.id.humidity_information);


            button_search = (Button) findViewById(R.id.see_button);
            button_search.setOnClickListener(this);
        BluetoothAdapter BTAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!BTAdapter.isEnabled()) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivity(i);
            finish();
            return;
        }

        int hasPermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (hasPermission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            android.Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
            finish();
            return;
        }

        mConnectionManager = new ConnectionManager(mConnectionListener);
        mConnectionManager.startListen();

        if(BTAdapter.getScanMode() != BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            i.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(i);
        }

    }

    Runnable networkTask = new Runnable() {
        @Override
        public void run() {
            // TODO
            try {
                socket = new Socket("62.234.14.145", 8888);
                ObjectOutputStream pw = new ObjectOutputStream(socket.getOutputStream());
                while(true){
                    Random ra =new Random();
                    String uid = (String) textView_uid_information.getText();
                    String number = (String) textView_number_information.getText();
                    String temperature = (String) textView_temperature_information.getText();
                    String humidity = (String) textView_humidity_information.getText();
                    Log.v("I",humidity);
                    pw.writeObject(new String[]{String.valueOf(ra.nextInt(10)+1), String.valueOf(ra.nextInt(10)+1),String.valueOf(ra.nextInt(10)+1),String.valueOf(ra.nextInt(10)+1)});
                    pw.flush();
                    Thread.sleep(2000);
                    //关闭输出流
                    socket.shutdownOutput();
                    //关闭流
                    pw.close();
                    socket.close();
                    }


            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(MSG_UPDATE_UI);
        mHandler.removeMessages(MSG_SENT_DATA);
        mHandler.removeMessages(MSG_RECEIVE_DATA);

        if(mConnectionManager != null) {
            mConnectionManager.disconnect();
            mConnectionManager.stopListen();
        }
    }

    private ConnectionManager.ConnectionListener mConnectionListener = new ConnectionManager.ConnectionListener() {

        @Override
        public void onConnectStateChange(int oldState, int State) {

            mHandler.obtainMessage(MSG_UPDATE_UI).sendToTarget();
        }

        @Override
        public void onListenStateChange(int oldState, int State) {

            mHandler.obtainMessage(MSG_UPDATE_UI).sendToTarget();
        }

        @Override
        public void onSendData(boolean suc, byte[] data) {

            mHandler.obtainMessage(MSG_SENT_DATA, suc?1:0, 0, data).sendToTarget();
        }

        @Override
        public void onReadData(byte[] data) {

            mHandler.obtainMessage(MSG_RECEIVE_DATA,  data).sendToTarget();

        }

    };

//    private View.OnClickListener mSendClickListener = new View.OnClickListener() {
//
//        @Override
//        public void onClick(View v) {
//            sendMessage();
//        }
//    };

//    private void sendMessage() {
//        String content = mMessageEditor.getText().toString();
//        if(content != null) {
//            content = content.trim();
//            if(content.length() > 0) {
//                boolean ret = mConnectionManager.sendData(content.getBytes());
//                if(!ret) {
//                    Toast.makeText(ChatActivity.this, R.string.send_fail, Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
//    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.main_menu, menu);
        mConnectionMenuItem = menu.findItem(R.id.connect_menu);
        //updateUI();

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.connect_menu: {
                if(mConnectionManager.getCurrentConnectState() == ConnectionManager.CONNECT_STATE_CONNECTED) {
                    mConnectionManager.disconnect();

                }
                else if(mConnectionManager.getCurrentConnectState() == ConnectionManager.CONNECT_STATE_CONNECTING) {
                    mConnectionManager.disconnect();

                }
                else if(mConnectionManager.getCurrentConnectState() == ConnectionManager.CONNECT_STATE_IDLE) {
                    Intent i = new Intent(ChatActivity.this, DeviceListActivity.class);
                    startActivityForResult(i, RESULT_CODE_BTDEVICE);
                }

            }
            return true;

//            case R.id.about_menu: {
//                Intent i = new Intent(this, AboutActivity.class);
//                startActivity(i);
//            }
//            return true;

            default:
                return false;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult, requestCode="+requestCode+" resultCode="+resultCode );
        if(requestCode == RESULT_CODE_BTDEVICE && resultCode == RESULT_OK) {
            deviceAddr = data.getStringExtra("DEVICE_ADDR");
            mConnectionManager.connect(deviceAddr);
        }
    }

        @Override
        public void onClick(View v) {
            mConnectionManager.sendData("1".getBytes());
        }

//    private void updateUI()
//    {
//        if(mConnectionManager == null) {
//            return;
//        }
//
//        if(mConnectionMenuItem == null) {
//            mMessageEditor.setEnabled(false);
//            mSendBtn.setEnabled(false);
//
//            return;
//        }
//
//        Log.d(TAG, "current BT ConnectState="+mConnectionManager.getState(mConnectionManager.getCurrentConnectState())
//                +" ListenState="+mConnectionManager.getState(mConnectionManager.getCurrentListenState()));
//
//        if(mConnectionManager.getCurrentConnectState() == ConnectionManager.CONNECT_STATE_CONNECTED) {
//            mConnectionMenuItem.setTitle(R.string.disconnect);
//
//            mMessageEditor.setEnabled(true);
//            mSendBtn.setEnabled(true);
//        }
//        else if(mConnectionManager.getCurrentConnectState() == ConnectionManager.CONNECT_STATE_CONNECTING) {
//            mConnectionMenuItem.setTitle(R.string.cancel);
//
//            mMessageEditor.setEnabled(false);
//            mSendBtn.setEnabled(false);
//        }
//        else if(mConnectionManager.getCurrentConnectState() == ConnectionManager.CONNECT_STATE_IDLE) {
//            mConnectionMenuItem.setTitle(R.string.connect);
//
//            mMessageEditor.setEnabled(false);
//            mSendBtn.setEnabled(false);
//        }
//    }
}
