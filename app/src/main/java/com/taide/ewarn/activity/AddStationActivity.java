package com.taide.ewarn.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.taide.ewarn.R;
import com.taide.ewarn.base.BaseActivity;
import com.taide.ewarn.client.TIASetClient;
import com.taide.ewarn.interfaces.HandlerSetParamData;
import com.taide.ewarn.model.DataMemoryManager;
import com.taide.ewarn.model.TIAStation;
import com.taide.ewarn.service.SocketService;
import com.taide.ewarn.utils.FileUtil;
import com.taide.ewarn.utils.Utils;
import com.taide.ewarn.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AddStationActivity extends BaseActivity implements HandlerSetParamData, LocationListener {
    String TAG = "AddStationActivity";

    @BindView(R.id.titlebar)
    TitleBar mTitleBar;
    @BindView(R.id.tv_connect_sn)
    SuperTextView devSNCodeTv;
    @BindView(R.id.tv_connect_netcode)
    SuperTextView devNetCodeTv;
    @BindView(R.id.tv_connect_stacode)
    SuperTextView devStaCodeTv;

    @BindView(R.id.dev_add_btn)
    Button devAddBtn;
    @BindView(R.id.wifi_connectTv)
    SuperTextView wifiConnectTv;
    @BindView(R.id.connect_status_tv)
    TextView connectStatusTv;
    @BindView(R.id.dev_info_llayout)
    LinearLayout devInfoLlayout;


    WifiManager mWifiManager;
    ConnectivityManager mConnectivityManager;
    TIASetClient client;

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
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            updateWifiStatus();
                            switch (status){
                                case DataMemoryManager.DEVICE_CONNECTING:
                                    connectStatusTv.setText("正在连接设备...");
                                    break;
                                case DataMemoryManager.DEVICE_CONNECTED:
                                    connectStatusTv.setText("设备连接成功,点击获取终端信息...");
                                    //socketBinder.getService().sendMessage("GETPARAM");
                                    break;
                                case DataMemoryManager.DEVICE_DISCONNECT:
                                    //connectStatusTv.setText("设备连接失败");
                                    break;
                                case DataMemoryManager.DEVICE_NOTRETURN:
                                    break;
                                case DataMemoryManager.GET_MSG:
                                    connectStatusTv.setText("接入终端成功");
                                    devInfoLlayout.setVisibility(View.VISIBLE);
                                    devAddBtn.setEnabled(true);
                                    devSNCodeTv.setCenterString(DataMemoryManager.currentSocketTIAInfo.getSN());
                                    devNetCodeTv.setCenterString(DataMemoryManager.currentSocketTIAInfo.getNETCODE());
                                    devStaCodeTv.setCenterString(DataMemoryManager.currentSocketTIAInfo.getSTACODE());
                                    break;
                                default:
                            }
                        }
                    });
                }
            });
        }
        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_station);

        initPermission();
        initToolBar();
        mWifiManager= (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        //绑定并启动socket服务
        Intent intent = new Intent(this, SocketService.class);
        startService(intent);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);

        connectStatusTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                socketBinder.getService().sendMessage("GETPARAM");
            }
        });

    }

    /**
     * 初始化权限
     */
    private void initPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            int REQUEST_CODE_PERMISSION_STORAGE = 100;
            String[] permissions = {
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            };

            for (String str : permissions) {
                if (this.checkSelfPermission(str) != PackageManager.PERMISSION_GRANTED) {
                    this.requestPermissions(permissions, REQUEST_CODE_PERMISSION_STORAGE);
                    return;
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    /**
     * 获取WIFI信息，判断是否有台站设备
     */
    public void updateWifiStatus(){
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            boolean networkok = false;
            if(wifiInfo.getSSID() != null && wifiInfo.getSSID().length() > 3){
                Log.d(TAG, "getSSID: "+wifiInfo.getSSID());
                wifiConnectTv.setLeftString(wifiInfo.getSSID());      //获取WIFI名
                if(wifiInfo.getSSID().contains("TIA")){
                    networkok = true;
                }
            }
            if(networkok){
                connectStatusTv.setText("WIFI连接正常，请检查设备系统是否正常运行...");
                if(socketBinder.getService().getServerStatus() == DataMemoryManager.DEVICE_CONNECTED){
                    connectStatusTv.setText("设备连接成功");
                }
            }else{
                connectStatusTv.setText("连接\"TIA_xxxx\"开头的Wi-Fi网络");
            }
    }


    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    /**
     * 实现终端连接相应接口
     * @param type
     * @param status
     */
    @Override
    public void processParamsData(String type, Boolean status) {
        if(type.equals("TIASetParam")){
            if(status){
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("tiaserviceresult", "connectok");
                msg.setData(data);
                conntmaHandler.sendMessage(msg);
            }else{
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("tiaserviceresult", "connectfail");
                msg.setData(data);
                conntmaHandler.sendMessage(msg);
            }
        }else if(type.equals("gettiaparams")){
            if(status){
                Message msg = new Message();
                Bundle data = new Bundle();
                data.putString("tiaserviceresult", "gettiaparamok");
                msg.setData(data);
                conntmaHandler.sendMessage(msg);
            }else {
                //statusTextView.setText("获取参数失败");
            }
        }
    }

    Handler conntmaHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("tiaserviceresult");
            if(val.equals("connectok")){
                //   TextView connstatusView = (TextView) findViewById(R.id.short_conn_status_textView);
                //    connstatusView.setText("连接TMA-63成功!");
                //Toast.makeText(SetTIAParamsActivity.this, "连接预警终端设备成功!", Toast.LENGTH_SHORT).show();
            }else if(val.equals("connectfail")){
                // Button setWifiBt = (Button)findViewById(R.id.set_tma_conn_button);
                // setWifiBt.setEnabled(true);
                //Toast.makeText(SetTIAParamsActivity.this, "连接预警终端设备失败!", Toast.LENGTH_SHORT).show();
            } else if(val.equals("gettiaparamok")) {
                Log.d(TAG, "handleMessage: ");
                connectStatusTv.setTextColor(Color.parseColor("#00CD00"));
                connectStatusTv.setText("接入终端成功");

                devInfoLlayout.setVisibility(View.VISIBLE);
                devAddBtn.setEnabled(true);
            }
        }
    };

    public void conWifiAction(View view) {
        Intent intent = new Intent();
        intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
        startActivity(intent);
    }

    public void addTIAStationAction(View view) {
        if(DataMemoryManager.currentTIAParams != null && !"".equals(DataMemoryManager.currentTIAParams)){
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
//            String paramsPath = FileUtil.getStoragePath(this,false)+"/EWarnManager/"+DataMemoryManager.currentSocketTIAInfo.getSN()+"/Params/tia.ini";
//            Log.d(TAG, "addTIAStationAction: "+paramsPath);
//            boolean result = FileUtil.writeTxtFile(paramsPath, DataMemoryManager.currentTIAParams,false);
            String paramsPath = FileUtil.getFilesPath(this) +"/"+ DataMemoryManager.currentSocketTIAInfo.getSN()+"/Params/tia.ini";
            boolean result = FileUtil.writeTxtFile(paramsPath,DataMemoryManager.currentTIAParams,false);
            //将记录添加到数据库
            TIAStation tiaStation = new TIAStation();
            tiaStation.setSN(DataMemoryManager.currentSocketTIAInfo.getSN());
            tiaStation.setNETCODE(DataMemoryManager.currentSocketTIAInfo.getNETCODE());
            tiaStation.setSTACODE(DataMemoryManager.currentSocketTIAInfo.getSTACODE());
            tiaStation.setDEVTYPE(DataMemoryManager.currentSocketTIAInfo.getDEVTYPE());
            tiaStation.setLONGITUDE(DataMemoryManager.currentSocketTIAInfo.getLONGITUDE());
            tiaStation.setLATITUDE(DataMemoryManager.currentSocketTIAInfo.getLATITUDE());
            tiaStation.setSSID(wifiInfo.getSSID());
            tiaStation.setPWD("12345678");

            List<TIAStation> stalist = LitePal.where("SN = ?",DataMemoryManager.currentSocketTIAInfo.getSN()).
                    find(TIAStation.class);
            if(stalist.size() == 0){
                tiaStation.save();
            }
            XToastUtils.success("添加成功");
            finish();
        }
    }

    private void initToolBar() {
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //解除绑定socket服务
        Intent intent = new Intent(this, SocketService.class);
        unbindService(serviceConnection);
        stopService(intent);
    }
}
