package com.taide.ewarn.activity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.LocationListener;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.taide.ewarn.R;
import com.taide.ewarn.base.BaseActivity;
import com.taide.ewarn.client.ConnectThread;
import com.taide.ewarn.client.TIASetClient;
import com.taide.ewarn.fragment.DevContentFragment;
import com.taide.ewarn.interfaces.HandlerSetParamData;
import com.taide.ewarn.model.DataMemoryManager;
import com.taide.ewarn.model.TIAStation;
import com.taide.ewarn.service.SocketService;
import com.taide.ewarn.utils.DateUtil;
import com.taide.ewarn.utils.FileUtil;
import com.taide.ewarn.utils.IniFile;
import com.taide.ewarn.utils.NetworkUtil;
import com.taide.ewarn.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.net.ConnectException;
import java.net.Socket;
import java.util.List;

import butterknife.BindView;

public class DevContentActivity extends BaseActivity implements HandlerSetParamData {

    public static int CUR_POSITION ;            //当前选中设备
    public static TIAStation CUR_STATION;   //当前选择台站
    private int PORT = 9742;
    //private int PORT = 54321;
    @BindView(R.id.titlebar)
    TitleBar mTitleBar;
    SuperTextView wifiConnectTv;
    TextView connectStatusTv;
    TIASetClient client;
    WifiManager mWifiManager;
    ConnectThread connectThread;

    SocketService.SocketBinder socketBinder;
    //调用连接绑定另一个服务
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            socketBinder = (SocketService.SocketBinder) iBinder;
            SocketService mService = socketBinder.getService();
            //实现回调，得到实时socket连接状态
            mService.setCallback(new SocketService.Callback() {
                @Override
                public void getSocketStatus(int status) {
                    switch (status){
                        case DataMemoryManager.DEVICE_CONNECTING:
                            activityListener.onStatusChange("正在连接设备...");
                            break;
                        case DataMemoryManager.DEVICE_CONNECTED:
                            activityListener.onStatusChange("设备正在连接...");
                            break;
                        case DataMemoryManager.DEVICE_DISCONNECT:
                            activityListener.onStatusChange("设备连接失败...");
                            break;
                        case DataMemoryManager.DEVICE_NOTRETURN:
                            activityListener.onStatusChange("设备正在重连...");
                            break;
                        case DataMemoryManager.GET_MSG:
                            activityListener.onStatusChange("设备连接成功");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    XToastUtils.success("成功获取终端参数");
                                }
                            });
                            break;
                        default:
                    }
                }
            });
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };


    /**
     *接口定义
     **/
    public DevActivityListener activityListener;
    public interface DevActivityListener {
        void onStatusChange(String status);
        void onWifiChange(String name);
        void onClickActivity();
    }

    @Override
    public void onAttachFragment(@NonNull Fragment fragment) {
        try {
            activityListener = (DevActivityListener) fragment;
        } catch (Exception e) {

        }
        super.onAttachFragment(fragment);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_content);
        initData();
        initToolBar();
        initFragment();
        mWifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //绑定并启动socket服务
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);


    }

    @Override
    protected void onResume() {
        super.onResume();
        getStatus();
    }

    /**
     * 获取WIFI信息，判断是否有台站设备
     */
    public void getStatus(){
        WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
        boolean networkok = false;
        if(wifiInfo.getSSID() != null && wifiInfo.getSSID().length() > 3){
            activityListener.onWifiChange(wifiInfo.getSSID());
            Log.d("DevContentActivity", "getSSID: "+wifiInfo.getSSID());
            if(wifiInfo.getSSID().contains("TIA")){
                networkok = true;
            }
        }else {
            //activityListener.onWifiChange("Wi-Fi未连接");
        }

    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DataMemoryManager.DEVICE_CONNECTING:
                    //connectThread = new ConnectThread(listenerThread.getSocket(), handler);
                    //connectThread.start();
                    break;
                case DataMemoryManager.DEVICE_CONNECTED:
                    activityListener.onStatusChange("设备连接成功,正在获取参数");
                    connectThread.sendData("GETPARAM"); //发送获取参数命令
                    break;
                case DataMemoryManager.SEND_MSG_SUCCSEE:
                    activityListener.onStatusChange("发送消息成功:"+msg.getData().getString("MSG"));
                    //text_state.setText("发送消息成功:" + msg.getData().getString("MSG"));
                    break;
                case DataMemoryManager.SEND_MSG_ERROR:
                    activityListener.onStatusChange("发送消息失败");
                    //text_state.setText("发送消息失败:" + msg.getData().getString("MSG"));
                    break;
                case DataMemoryManager.GET_MSG:
                    activityListener.onStatusChange("收到消息");
                    //text_state.setText("收到消息:" + msg.getData().getString("MSG"));
                    break;
            }
        }
    };

    public int getSocketStatus(){
        int status = 0;
        if(socketBinder != null)
            status = socketBinder.getService().getServerStatus();
        return status;
    }


    /**
     * 向服务端发送消息
     */
    public void sendCommand(final String command){
        socketBinder.getService().sendMessage(command);
    }

    private void initFragment() {
        Fragment devFragment = new DevContentFragment();
        Bundle bundle=new Bundle();
        bundle.putString("SN",CUR_STATION.getSN());
        devFragment.setArguments(bundle);

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,new DevContentFragment())
                //.addToBackStack(null)
                .commit();
    }

    /**
     * 初始化传递过来的参数
     */
    private void initData() {
        Intent intent=getIntent();
        String currentSN = intent.getStringExtra("SN");
        CUR_POSITION = intent.getIntExtra("POSITION",0);
        if(!currentSN.equals("")){
            List<TIAStation> tias = LitePal.where("SN = ?",currentSN).find(TIAStation.class);
            CUR_STATION = tias.get(0);
        }
        String inipath = FileUtil.getFilesPath(this) +"/"+currentSN+"/Params/tia.ini" ;
        DataMemoryManager.iniFile = new IniFile(new File(inipath));
    }

    private void initToolBar() {
        mTitleBar.setTitle(CUR_STATION.getSTACODE());
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onKeyDown(KeyEvent.KEYCODE_BACK, null);
                onBackPressed();
            }
        });
    }

    @Override
    public void processParamsData(String type, Boolean status) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent intent = new Intent(this, SocketService.class);
        stopService(intent);
        unbindService(serviceConnection);
    }

}
