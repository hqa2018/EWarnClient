package com.taide.ewarn.activity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;

import com.taide.ewarn.R;
import com.taide.ewarn.adapter.LocalDeviceAdapter;
import com.taide.ewarn.base.BaseActivity;
import com.taide.ewarn.model.DataMemoryManager;
import com.taide.ewarn.model.TIAStation;
import com.taide.ewarn.utils.FileUtil;
import com.taide.ewarn.utils.IniFile;
import com.taide.ewarn.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LauncherActivity extends BaseActivity {
    String TAG = "LauncherActivity";
    @BindView(R.id.titlebar)
    TitleBar mTitleBar;
    @BindView(R.id.rv_stations)
    RecyclerView rvStations;
    LocalDeviceAdapter localDeviceAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        ButterKnife.bind(this);
        initPermission();   //初始化权限
        initToolBar();
        initStations();

    }

    @Override
    protected void onResume() {
        super.onResume();

        DataMemoryManager.tiaListViewArray.clear();
        List<TIAStation> tiaStationList = LitePal.findAll(TIAStation.class);
        for(TIAStation tiaStation : tiaStationList){
            DataMemoryManager.tiaListViewArray.add(tiaStation);
        }
        localDeviceAdapter.updateItems(true);
    }

    private void initPermission() {
        //申请读写,定位权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//检查是否有了权限
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            } else {
                //没有权限即动态申请
                List<String> permissionList = new ArrayList<>();
                permissionList.add(Manifest.permission.RECORD_AUDIO);
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                permissionList.add(Manifest.permission.READ_EXTERNAL_STORAGE);
                permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
                permissionList.add(Manifest.permission.ACCESS_WIFI_STATE);
                String[] permissions = permissionList.toArray(new String[permissionList.size()]);
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    //初始化台站数据
    private void initStations() {
        //设置布局
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this) {
            @Override
            protected int getExtraLayoutSpace(RecyclerView.State state) {
                return 300;
            }
        };
        rvStations.setLayoutManager(linearLayoutManager);

        //读取文件
//        File[] list = FileUtil.listFilesByDate(FileUtil.getFilesPath(this));
//        for(int i=0;i<list.length;i++){
//            File file = list[i];
//            String inipath = file.getAbsolutePath()+"/Params/tia.ini";
//            if(FileUtil.fileIsExists(inipath)){
//                DataMemoryManager.iniFile = new IniFile(new File(inipath));
//            }
//        }

        //读取数据库
        DataMemoryManager.tiaListViewArray.clear();
        List<TIAStation> tiaStationList = LitePal.findAll(TIAStation.class);

        for(TIAStation tiaStation : tiaStationList){
            Log.d(TAG, "getSTACODE: "+tiaStation.getSTACODE());
            Log.d(TAG, "getLONGITUDE: "+tiaStation.getLONGITUDE());
            Log.d(TAG, "getLATITUDE: "+tiaStation.getLATITUDE());
            DataMemoryManager.tiaListViewArray.add(tiaStation);
        }

        localDeviceAdapter  = new LocalDeviceAdapter(this,DataMemoryManager.tiaListViewArray);
        localDeviceAdapter.setOnItemClickListener(new LocalDeviceAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, TIAStation tiaStation) {
                Intent intent = new Intent(LauncherActivity.this,DevContentActivity.class);
                intent.putExtra("SN",tiaStation.getSN());
                intent.putExtra("POSITION",position);
                startActivity(intent);
                Toast.makeText(LauncherActivity.this, "click " + position, Toast.LENGTH_SHORT).show();
            }
        });
        rvStations.setAdapter(localDeviceAdapter);

    }

    private void initToolBar() {
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                XToastUtils.toast("点击添加");
                Intent it = new Intent();
                it.setClass(LauncherActivity.this, AddStationActivity.class);
                startActivity(it);
            }
        }).setCenterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XToastUtils.toast("点击标题");
            }
        }).addAction(new TitleBar.ImageAction(R.drawable.ic_navigation_more) {
            @Override
            public void performAction(View view) {
                XToastUtils.toast("点击更多！");
            }
        });

    }


}
