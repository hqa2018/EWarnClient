package com.taide.ewarn.fragment;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.taide.ewarn.R;
import com.taide.ewarn.activity.DevContentActivity;
import com.taide.ewarn.base.BaseFragment;
import com.taide.ewarn.model.DataMemoryManager;
import com.taide.ewarn.model.TIAStation;
import com.taide.ewarn.service.SocketService;
import com.taide.ewarn.utils.FileUtil;
import com.taide.ewarn.utils.XToastUtils;
import com.xuexiang.xui.widget.dialog.DialogLoader;
import com.xuexiang.xui.widget.textview.supertextview.SuperTextView;

import org.litepal.LitePal;

import butterknife.BindView;

public class DevContentFragment extends BaseFragment implements DevContentActivity.DevActivityListener,View.OnClickListener {
    //@BindView(R.id.dev_register_btn)
    SuperTextView devRegisterBtn;
    SuperTextView wifiConnectBtn;
    View currentView;
    TextView connectStatusTv;

    @Override
    public void onStatusChange(String status) {
        if(getActivity()!=null){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(connectStatusTv != null)
                        connectStatusTv.setText(status);
                }
            });
        }
        //connectStatusTv.setText(status);
    }

    @Override
    public void onWifiChange(String name) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                wifiConnectBtn.setLeftString(name);
            }
        });
    }

    @Override
    public void onClickActivity() {

    }

    @Override
    public View setLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout .fragment_dev_content,container,false);
        return currentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateStatus();
    }

    private void updateStatus() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                DevContentActivity devActivity = (DevContentActivity ) getActivity();
                if(devActivity.getSocketStatus() == DataMemoryManager.DEVICE_CONNECTED){
                    connectStatusTv.setText("设备连接成功");
                }else {
                    connectStatusTv.setText("设备连接失败");
                }
            }
        });
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        currentView.findViewById(R.id.dev_register_btn).setOnClickListener(this);
        currentView.findViewById(R.id.dev_voice_btn).setOnClickListener(this);
        currentView.findViewById(R.id.dev_ewarn_btn).setOnClickListener(this);
        currentView.findViewById(R.id.dev_reboot_btn).setOnClickListener(this);
        currentView.findViewById(R.id.dev_delete_btn).setOnClickListener(this);
        currentView.findViewById(R.id.dev_getparam_btn).setOnClickListener(this);
        currentView.findViewById(R.id.dev_baseinfo_btn).setOnClickListener(this);

        //currentView.findViewById(R.id.fileButton).setOnClickListener(this);
//        devRegisterBtn = currentView.findViewById(R.id.dev_register_btn);
        wifiConnectBtn = currentView.findViewById(R.id.wifi_connectTv);
        wifiConnectBtn.setOnClickListener(this);

        connectStatusTv = currentView.findViewById(R.id.connect_status_tv);

        wifiConnectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction("android.net.wifi.PICK_WIFI_NETWORK");
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.wifi_connectTv:

                break;
            case R.id.dev_baseinfo_btn:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new BaseInfoFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.dev_getparam_btn:
                DevContentActivity devActivity = (DevContentActivity ) getActivity();
                devActivity.sendCommand("GETPARAM");
                break;
            case R.id.dev_register_btn:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new RegisterFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.dev_voice_btn:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new VoiceControlFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.dev_ewarn_btn:
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new EWarnDrillFragment())
                        .addToBackStack(null)
                        .commit();
                break;
            case R.id.dev_reboot_btn:
                DialogLoader.getInstance().showConfirmDialog(
                        getContext(),
                        "是否重启设备",
                        "是",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                XToastUtils.toast("发送重启命令！");
                                DevContentActivity devActivity = (DevContentActivity ) getActivity();
                                devActivity.sendCommand("SET_REBOOT");
                                dialog.dismiss();
                            }
                        },
                        "否",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //XToastUtils.toast("不同意打开蓝牙！");
                                dialog.dismiss();
                            }
                        }
                );
                break;
            case R.id.dev_delete_btn:
                AlertDialog.Builder customizeDialog =
                        new AlertDialog.Builder(getActivity());
                customizeDialog.setTitle("系统提示");
                customizeDialog.setMessage("确定要删除台站吗?");
                customizeDialog.setPositiveButton("删除",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String paramsPath = FileUtil.getFilesPath(getActivity()) +"/"+ DevContentActivity.CUR_STATION.getSN()+"/Params/tia.ini";
                                TIAStation tiaStation = DataMemoryManager.tiaListViewArray.get(DevContentActivity.CUR_POSITION);
                                //删除数据以及文件
                                LitePal.deleteAll(TIAStation.class,"SN = ?",tiaStation.getSN());
                                FileUtil.deleteFile(paramsPath);
                                XToastUtils.success("删除成功");
                                //Toast.makeText(getActivity(), "删除成功", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                                getActivity().finish();
                                //updateStationList();
                                //localDeviceAdapter.updateItems(true);
                            }
                        });
                customizeDialog.setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //...To-do
                            }
                        });

                AlertDialog alertDialog = customizeDialog.create();
                alertDialog.show();

                break;
            default:
        }

    }
}
