package com.taide.ewarn.model;

import com.taide.ewarn.MyApp;
import com.taide.ewarn.utils.FileUtil;
import com.taide.ewarn.utils.IniFile;

import java.io.File;
import java.util.ArrayList;

public class DataMemoryManager {

    public static final int DEVICE_CONNECTING = 1;//有设备正在连接
    public static final int DEVICE_CONNECTED  = 2;//有设备连上成功
    public static final int DEVICE_DISCONNECT = 3;//设备连接失败
    public static final int DEVICE_NOTRETURN  = 4;//设备连接成功，但没有返回
    public static final int SEND_MSG_SUCCSEE  = 5;//发送消息成功
    public static final int SEND_MSG_ERROR    = 6;//发送消息失败
    public static final int GET_MSG           = 7;//获取新消息


    public static TIAStation currentSocketTIAInfo = null;	 //当前socket连接的设备基础信息
    public static String currentTIAParams;
    public static ArrayList<TIAStation> tiaListViewArray = new ArrayList<TIAStation>();
    public static IniFile iniFile = null;			//当前选中的台站参数文件

    //初始化数据库表
    public static void initDevLitePalData(){
        //加载文件
        File[] list = FileUtil.listFilesByDate(FileUtil.getFilesPath(MyApp.getContextObject()));
        for(int i=0;i<list.length;i++){
            File file = list[i];
            String inipath = file.getAbsolutePath()+"/Params/tia.ini";
            if(FileUtil.fileIsExists(inipath)){
                IniFile iniFile = new IniFile(new File(inipath));
            }
        }

    }

}
