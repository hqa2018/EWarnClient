package com.taide.ewarn.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.taide.ewarn.R;
import com.taide.ewarn.activity.DevContentActivity;
import com.taide.ewarn.base.BaseFragment;
import com.taide.ewarn.utils.XToastUtils;
import com.xuexiang.xui.widget.picker.XSeekBar;

public class VoiceControlFragment extends BaseFragment {

    View currentView;
    XSeekBar xsb;

    @Override
    public View setLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_voice_control,container,false);
        return currentView;
    }

    @Override
    public void onResume() {
        super.onResume();
        //滑块控件
        xsb = currentView.findViewById(R.id.dev_voice_xsb);
        //设置音量
        Button Voice_Button_set = (Button) currentView.findViewById(R.id.dev_voice_setbtn);
        Voice_Button_set.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d("VoiceControlFragment", "onClick: setVoice="+getActivity());
                DevContentActivity devActivity = (DevContentActivity ) getActivity();
                devActivity.sendCommand("SETVOICE@"+xsb.getSelectedNumber());
                XToastUtils.success("音量设置完毕");
            }
        });

    }
}
