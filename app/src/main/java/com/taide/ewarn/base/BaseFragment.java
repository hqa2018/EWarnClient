package com.taide.ewarn.base;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.taide.ewarn.R;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import butterknife.BindView;

/**
 * 封装基础Fragment
 */
public abstract class BaseFragment extends Fragment {
    @BindView(R.id.titlebar)
    TitleBar titleBar;
    protected Activity mActivity;

    private void initTitle() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (AppCompatActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //initTitle();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return setLayoutView(inflater,container,savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        findViewById(view);
        setViewData(view);
        setClickEvent(view);
    }

    /**
     * 设置布局
     *
     * @return
     * @param inflater
     * @param container
     */
    public abstract View setLayoutView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState);

    /**
     * findViewById
     */
    public void findViewById(View view){}

    /**
     * setViewData
     */
    public void setViewData(View view){}

    /**
     * setClickEvent
     */
    public void setClickEvent(View view){}

    /**
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_button:
                // 一、从同一个Activity的一个Fragment跳到另外一个Fragment
                //1、压栈式跳转
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container, new YourFragment(), null)
                        .addToBackStack(null)
                        .commit();
                //2、非压栈式跳转//
                //  getActivity().getSupportFragmentManager()//
                //          .beginTransaction()//
                //          .replace(R.id.fragment_container,new YourFragment(),null)//
                //          .commit();
                break;
            case R.id.my_return:                //返回到上一个Fragment（同一个Activity中）
                getActivity().getSupportFragmentManager().popBackStack();
                break;

             //二、从一个Activity的Fragment跳转到另外一个Activity(等同于Activity之间的跳转（上下文是getActivity）)
            case R.id.my_other:
                Intent intent = new Intent(getActivity(),OtherActivity.class);
                startActivity(intent);
                break;
        }
    }
    **/


}
