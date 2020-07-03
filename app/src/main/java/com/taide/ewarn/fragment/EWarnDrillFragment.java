package com.taide.ewarn.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.taide.ewarn.R;
import com.taide.ewarn.activity.DevContentActivity;
import com.taide.ewarn.base.BaseFragment;
import com.taide.ewarn.model.DataMemoryManager;
import com.taide.ewarn.utils.XToastUtils;

public class EWarnDrillFragment extends BaseFragment {
    View currentView;
    LocationReceiver locationReceiver;

    @Override
    public View setLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.frament_ewarn,container,false);

        locationReceiver = new LocationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.taide.ewarn.EWARN_SUCCESS");
        getActivity().registerReceiver(locationReceiver, filter);
        return currentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        EditText ew_lon_edt = currentView.findViewById(R.id.ew_lon_edt);
        EditText ew_lat_edt = currentView.findViewById(R.id.ew_lat_edt);
        EditText ew_dep_edt = currentView.findViewById(R.id.ew_dep_edt);
        EditText ew_mag_edt = currentView.findViewById(R.id.ew_mag_edt);


        Button Button_saveInfo = (Button) currentView.findViewById(R.id.set_ewarn_btn);
        Button_saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DevContentActivity devActivity = (DevContentActivity ) getActivity();
                String lon = ew_lon_edt.getText().toString();
                String lat = ew_lat_edt.getText().toString();
                String dep = ew_dep_edt.getText().toString();
                String mag = ew_mag_edt.getText().toString();
                devActivity.sendCommand("SET_EWARN");
                //XToastUtils.success("发送预警演练.");
                //devActivity.sendCommand("SET_EWARN@LON="+lon+"#LAT="+lat+"#DEP"+dep+"#MAG"+mag);
            }
        });


    }


    /**
     * 添加MQTT广播，接收广播后切换导预警界面
     */
    public class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String intentAction = intent.getAction();
            if (intentAction.equals("com.taide.ewarn.EWARN_SUCCESS")) {
                XToastUtils.success("触发预警发送成功.");
            }
        }
    }
}
