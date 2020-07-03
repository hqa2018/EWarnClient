package com.taide.ewarn.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.taide.ewarn.R;
import com.taide.ewarn.activity.DevContentActivity;
import com.taide.ewarn.base.BaseFragment;
import com.taide.ewarn.model.DataMemoryManager;
import com.taide.ewarn.model.TIAStation;
import com.taide.ewarn.utils.XToastUtils;

import org.litepal.LitePal;

import java.util.List;

public class BaseInfoFragment extends BaseFragment {
    View currentView;

    EditText basic_sn_edt;
    EditText basic_type_edt;
    EditText basic_netcode_edt;
    EditText basic_stacode_edt;
    EditText basic_lon_edt;
    EditText basic_lat_edt;
    EditText basic_totalfloor_edt;
    EditText basic_floor_edt;
    EditText basic_address_edt;
    EditText basic_contactpersion_edt;
    EditText basic_contacttel_edt;

    @Override
    public View setLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_baseinfo,container,false);
        return currentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        basic_sn_edt = currentView.findViewById(R.id.basic_sn_edt);
        basic_type_edt = currentView.findViewById(R.id.basic_type_edt);
        basic_netcode_edt = currentView.findViewById(R.id.basic_netcode_edt);
        basic_stacode_edt = currentView.findViewById(R.id.basic_stacode_edt);
        basic_lon_edt = currentView.findViewById(R.id.basic_lon_edt);
        basic_lat_edt = currentView.findViewById(R.id.basic_lat_edt);
        basic_totalfloor_edt = currentView.findViewById(R.id.basic_totalfloor_edt);
        basic_floor_edt = currentView.findViewById(R.id.basic_floor_edt);
        basic_address_edt = currentView.findViewById(R.id.basic_address_edt);
        basic_contactpersion_edt = currentView.findViewById(R.id.basic_contactpersion_edt);
        basic_contacttel_edt = currentView.findViewById(R.id.basic_contacttel_edt);

        Button setButton = currentView.findViewById(R.id.dev_base_setbtn);

        setButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setInfo();
            }
        });

        // 读取配置文件,初始化数据
        initData();
    }

    private void initData() {
        basic_sn_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "SN"));
        basic_type_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "DEVTYPE"));
        basic_netcode_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "NETCODE"));
        basic_stacode_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "STACODE"));
        basic_lon_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "LONGITUDE"));
        basic_lat_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "LATITUDE"));
        basic_totalfloor_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "ALTITUDE"));
        basic_floor_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "INSTALLFLOOR"));
        basic_address_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "INSTALLADDRESS"));
        basic_contactpersion_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "CONTACTPERSION"));
        basic_contacttel_edt.setText(DataMemoryManager.iniFile.get("BASEINFO", "CONTACTTEL"));
    }

    private void setInfo() {
        DataMemoryManager.iniFile.set("BASEINFO", "SN", basic_sn_edt.getText().toString().trim());
        DataMemoryManager.iniFile.set("BASEINFO", "DEVTYPE", basic_type_edt.getText().toString().trim());
        DataMemoryManager.iniFile.set("BASEINFO", "NETCODE", basic_netcode_edt.getText().toString().trim());
        DataMemoryManager.iniFile.set("BASEINFO", "STACODE", basic_stacode_edt.getText().toString().trim());
        DataMemoryManager.iniFile.set("BASEINFO", "LONGITUDE", basic_lon_edt.getText().toString().trim());
        DataMemoryManager.iniFile.set("BASEINFO", "LATITUDE", basic_lat_edt.getText().toString().trim());
        DataMemoryManager.iniFile.set("BASEINFO", "ALTITUDE", basic_totalfloor_edt.getText().toString().trim());
        DataMemoryManager.iniFile.set("BASEINFO", "INSTALLFLOOR", basic_floor_edt.getText().toString().trim());
        DataMemoryManager.iniFile.set("BASEINFO", "INSTALLADDRESS", basic_address_edt.getText().toString().trim());
        DataMemoryManager.iniFile.set("BASEINFO", "CONTACTPERSION", basic_contactpersion_edt.getText().toString().trim());
        DataMemoryManager.iniFile.set("BASEINFO", "CONTACTTEL", basic_contacttel_edt.getText().toString().trim());
        DataMemoryManager.iniFile.save();

        //更新数据表
        List<TIAStation> stalist = LitePal.where("SN = ?",DataMemoryManager.iniFile.get("BASEINFO","SN")).
                find(TIAStation.class);
        if(stalist.size() > 0){
            TIAStation tiaStation = stalist.get(0);
            tiaStation.setNETCODE(DataMemoryManager.iniFile.get("BASEINFO","NETCODE"));
            tiaStation.setSTACODE(DataMemoryManager.iniFile.get("BASEINFO","STACODE"));
            tiaStation.setDEVTYPE(DataMemoryManager.iniFile.get("BASEINFO","DEVTYPE"));
            tiaStation.setLONGITUDE(DataMemoryManager.iniFile.get("BASEINFO","LONGITUDE"));
            tiaStation.setLATITUDE(DataMemoryManager.iniFile.get("BASEINFO","LATITUDE"));
            tiaStation.save();
        }

        //保存并设置参数
        DevContentActivity devActivity = (DevContentActivity ) getActivity();
        devActivity.sendCommand("SETPARAMS@"+DataMemoryManager.iniFile.readFileString());

        //XToastUtils.success("添加成功");
        XToastUtils.success("参数设置成功");
    }

}
