package com.taide.ewarn.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.taide.ewarn.model.DataMemoryManager;
import com.taide.ewarn.model.TIAStation;
import com.taide.ewarn.utils.FileUtil;
import com.taide.ewarn.utils.IniFile;
import com.taide.ewarn.utils.NetworkUtil;
import com.taide.ewarn.utils.XToastUtils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

public class SocketService extends Service {
    String TAG = "SocketService";

    private Callback callback;
    /*socket*/
    private Socket socket;
    /*连接线程*/
    private Thread connectThread;
    private Timer timer = new Timer();
    private OutputStream outputStream;
    private InputStream inputStream;

    private SocketBinder sockerBinder = new SocketBinder();
    private String ip;
    private String port;
    private TimerTask task;
    private String SERVER_IP;       //服务器IP
    private int SERVER_PORT = 9742;     //服务器端口
    //private int SERVER_PORT = 54321;     //服务器端口
    private int timeout = 30000;//连接等待时间

    private int SOCKET_STATUS = 0;

    DataOutputStream output;
    DataInputStream input;


    /*默认重连*/
    private boolean isReConnect = true;
    private Handler handler = new Handler(Looper.getMainLooper());


    @Override
    public IBinder onBind(Intent intent) {
        return sockerBinder;
    }

    public class SocketBinder extends Binder {
        /*返回SocketService 在需要的地方可以通过ServiceConnection获取到SocketService  */
        public SocketService getService() {
            return SocketService.this;
        }
    }

    public SocketService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        initSocket();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 连接终端
     */
    public boolean login()
    {
        try {
            socket = new Socket();
            String routeIP = NetworkUtil.getWifiRouteIPAddress(getApplicationContext());
            socket.connect(new InetSocketAddress(routeIP, SERVER_PORT), timeout);
            socket.setSoTimeout(this.timeout);
            if (socket.isConnected()) {
                output = new DataOutputStream(socket.getOutputStream());
                input = new DataInputStream(socket.getInputStream());
                return true;
            }else {
                return false;
            }
        } catch (Exception e) {
            Log.d(TAG, "login: socket连接失败");
            e.printStackTrace(System.out);
            return false;
        }
    }

    /*初始化socket*/
    private void initSocket() {
        if (socket == null && connectThread == null) {
            connectThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    if(login()){
                        if(callback!=null){
                            callback.getSocketStatus(DataMemoryManager.DEVICE_CONNECTED);
                        }
                        SOCKET_STATUS = DataMemoryManager.DEVICE_CONNECTED;
                        Log.d(TAG, "连接成功");
                        listenServerReply(input);
                        sendMessage("GETPARAM");
                        Log.d(TAG, "发送 GETPARAM");
                    }else{
                        if(callback!=null){
                            callback.getSocketStatus(DataMemoryManager.DEVICE_DISCONNECT);
                        }
                        SOCKET_STATUS = DataMemoryManager.DEVICE_DISCONNECT;
                        Log.d(TAG, "连接失败,五秒后重连...");
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException ex) {
                            ex.printStackTrace();
                        }
                        releaseSocket();
                    }
                }
            });
            /*启动连接线程*/
            connectThread.start();
        }
    }

    //监听服务端回复的消息
    public void listenServerReply(final DataInputStream dis){
        new Thread(){
            @Override
            public void run() {
                super.run();
                String line = null;
                try {
                    while((line = dis.readUTF()) != null){
                        Log.d(TAG, "接收服务端消息: "+line);
                        if(line.contains("BASEINFO")){
                            DataMemoryManager.currentSocketTIAInfo = new TIAStation();
                            String SN = "";
                            String[] strlist = line.split("\r\n");
                            for(String content : strlist){
                                //System.out.println("content: " + content);
                                if(content.contains("=")){
                                    String[] sps = content.split("=");
                                    if(sps[0].equals("SN")){
                                        DataMemoryManager.currentSocketTIAInfo.setSN(sps[1]);
                                    }
                                    if(sps[0].equals("NETCODE")){
                                        DataMemoryManager.currentSocketTIAInfo.setNETCODE(sps[1]);
                                    }
                                    if(sps[0].equals("STACODE")){
                                        DataMemoryManager.currentSocketTIAInfo.setSTACODE(sps[1]);
                                    }
                                    if(sps[0].equals("DEVTYPE")){
                                        DataMemoryManager.currentSocketTIAInfo.setDEVTYPE(sps[1]);
                                    }
                                    if(sps[0].equals("LONGITUDE")){
                                        DataMemoryManager.currentSocketTIAInfo.setLONGITUDE(sps[1]);
                                    }
                                    if(sps[0].equals("LATITUDE")){
                                        DataMemoryManager.currentSocketTIAInfo.setLATITUDE(sps[1]);
                                    }
                                }
                            }
                            Log.d(TAG, "SN: "+SN);
                            DataMemoryManager.currentTIAParams = line;
                            String paramsPath = FileUtil.getFilesPath(getApplicationContext()) +"/"+ DataMemoryManager.currentSocketTIAInfo.getSN()+"/Params/tia.ini";
                            Log.d(TAG, "paramsPath: "+paramsPath);
                            File paramFile = new File(paramsPath);

                            if(paramFile.exists()){
                                Log.d(TAG, "File exist");
                                FileUtil.writeTxtFile(paramsPath,DataMemoryManager.currentTIAParams,false);
                                DataMemoryManager.iniFile = new IniFile(new File(paramsPath));
                            }
                            if(callback!=null){
                                callback.getSocketStatus(DataMemoryManager.GET_MSG);
                            }
                        }else if(line.contains("SUCCESS")){
                            if(line.contains("MQTT")){
                                //XToastUtils.success("MQTT启动成功");
                                Intent intent = new Intent();
                                intent.setAction("com.taide.ewarn.MQTT_SUCCESS");
                                sendBroadcast(intent);
                            }else{
                                Log.d(TAG, "EWARN_SUCCESS");
                                Intent intent = new Intent();
                                intent.setAction("com.taide.ewarn.EWARN_SUCCESS");
                                sendBroadcast(intent);
                            }
                        }else{

                        }
                    }
                } catch (IOException e) {
                    Log.d(TAG, "listenServer ERROR: "+e.getMessage());
                    e.printStackTrace();
                    //releaseSocket();
                }
            }
        }.start();
    }

    /**
     * 发送服务端消息
     * @param message
     */
    public void sendMessage(String message){
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    if (output != null) {
                        Log.d(TAG, "output: "+output);
                        Log.d(TAG, "message: "+message);
                        output.writeUTF(message);// 发给服务端
                        output.flush();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(TAG, "sendMessage: "+e.getMessage());
                }
            }
        }).start();
    }

    /**
     *
     * @return
     */
    public int getServerStatus(){
        return SOCKET_STATUS;
    }


    /*定时发送心跳数据*/
    private void sendBeatData() {
        if (timer == null) {
            timer = new Timer();
        }
        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        outputStream = socket.getOutputStream();
                        //Log.d(TAG, "发送心跳信息test.");
                        outputStream.write(("test").getBytes("utf-8"));
                        outputStream.flush();
                    } catch (Exception e) {
//                        if(callback!=null){
//                            callback.getSocketStatus(DataMemoryManager.DEVICE_RECONNECT);
//                        }
                        //XToastUtils.toast("连接断开，正在重连");
                        releaseSocket();//设备重连
                        e.printStackTrace();
                    }
                }
            };
        }
        timer.schedule(task, 0, 5000);
    }


    /*释放资源*/
    private void releaseSocket() {
        Log.d(TAG, "重启socket");
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }

        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            output = null;
        }

        if (socket != null) {
            try {
                socket.close();

            } catch (IOException e) {
            }
            socket = null;
        }

        if (connectThread != null) {
            connectThread = null;
        }
        /*重新初始化socket*/
        if (isReConnect) {
            initSocket();
        }
    }

    /**
     * 提供接口回调方法
     * @param callback
     */
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    /**
     * 回调接口
     * @author lenovo
     */
    public static interface Callback {
        void getSocketStatus(int num);  //实时更新socket状态
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSocket();
    }

    /**
     * 停掉socket线程并是否资源
     */
    private void stopSocket() {
        Log.d(TAG, "停掉socket");
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (outputStream != null) {
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            outputStream = null;
        }

        if (output != null) {
            try {
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            output = null;
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
            }
            socket = null;
        }
        if (connectThread != null) {
            connectThread = null;
        }
    }

    /**
     * 发送服务端消息

     public void sendMessage(final String message){
     new Thread(new Runnable(){
    @Override
    public void run() {
    try {
    if (outputStream != null) {
    outputStream = socket.getOutputStream();
    outputStream.write((message).getBytes("utf-8"));
    outputStream.flush();
    if(callback!=null){
    callback.getSocketStatus(DataMemoryManager.SEND_MSG_SUCCSEE);
    }
    Log.d(TAG, "发送信息:"+message);
    //outputStream.writeUTF(message);// 发给服务端
    }
    } catch (IOException e) {
    e.printStackTrace();
    if(callback!=null){
    callback.getSocketStatus(DataMemoryManager.SEND_MSG_ERROR);
    }
    }
    }
    }).start();
     }
     */

    /*
                    socket = new Socket();
                    try {
                        String routeIP = NetworkUtil.getWifiRouteIPAddress(getApplicationContext());
                        socket.connect(new InetSocketAddress(routeIP, SERVER_PORT), 2000);
                        if (socket.isConnected()) {
                            Log.d(TAG, "socket连接成功.");
                            if(callback!=null){
                                callback.getSocketStatus(DataMemoryManager.DEVICE_CONNECTED);
                            }
                            //获取数据流
                            inputStream = socket.getInputStream();
                            outputStream = socket.getOutputStream();
                            //sendBeatData();
                            //sendMessage("GETPARAM");
                            //监听服务端回复的消息
                            byte[] buffer = new byte[4096];
                            int bytes;
                            while (true) {
                                bytes = inputStream.read(buffer);
                                Log.d(TAG, "bytes: "+bytes);
                                if (bytes > 0) {
                                    //String readcontext = readStreamToString(inputStream);
                                    //Log.d(TAG, "readcontext: "+readcontext);
                                    final byte[] data = new byte[bytes];
                                    System.arraycopy(buffer, 0, data, 0, bytes);
                                    String line = new String(data);
                                    Log.d(TAG, "bytes["+bytes+"]");
                                    Log.d(TAG, "读取到数据["+line.length()+"]:" + line);
                                    if(line.contains("BASEINFO")){
                                        DataMemoryManager.currentSocketTIAInfo = new TIAStation();
                                        String SN = "";
                                        String[] strlist = line.split("\r\n");
                                        for(String content : strlist){
                                            //System.out.println("content: " + content);
                                            if(content.contains("=")){
                                                String[] sps = content.split("=");
                                                if(sps[0].equals("SN")){
                                                    DataMemoryManager.currentSocketTIAInfo.setSN(sps[1]);
                                                }
                                                if(sps[0].equals("NETCODE")){
                                                    DataMemoryManager.currentSocketTIAInfo.setNETCODE(sps[1]);
                                                }
                                                if(sps[0].equals("STACODE")){
                                                    DataMemoryManager.currentSocketTIAInfo.setSTACODE(sps[1]);
                                                }
                                                if(sps[0].equals("DEVTYPE")){
                                                    DataMemoryManager.currentSocketTIAInfo.setDEVTYPE(sps[1]);
                                                }
                                            }
                                        }
                                        Log.d(TAG, "SN: "+SN);
                                        DataMemoryManager.currentTIAParams = line;
                                        String paramsPath = FileUtil.getFilesPath(getApplicationContext()) +"/"+ DataMemoryManager.currentSocketTIAInfo.getSN()+"/Params/tia.ini";
                                        Log.d(TAG, "paramsPath: "+paramsPath);
                                        File paramFile = new File(paramsPath);

                                        if(paramFile.exists()){
                                            Log.d(TAG, "File exist");
                                            FileUtil.writeTxtFile(paramsPath,DataMemoryManager.currentTIAParams,false);
                                        }
                                        if(callback!=null){
                                            callback.getSocketStatus(DataMemoryManager.GET_MSG);
                                        }
                                        //handler.processParamsData("gettiaparams",true);
                                    }else if(line.contains("SUCCESS")){
                                        //handler.processParamsData("settiaparams",true);
                                    }else{
                                        //handler.processParamsData("gettiaparams",false);
                                    }
                                }else{
                                    break;  //退出循环，并关闭输入流
                                }
                            }
                            inputStream.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (e instanceof SocketTimeoutException) {
                            //toastMsg("连接超时，正在重连");
                            if(callback!=null){
                                callback.getSocketStatus(DataMemoryManager.DEVICE_CONNECTING);
                            }
                            Log.d(TAG, "连接超时，正在重连");
                            releaseSocket();
                        } else if (e instanceof NoRouteToHostException) {
                            if(callback!=null){
                                callback.getSocketStatus(DataMemoryManager.DEVICE_DISCONNECT);
                            }
                            //toastMsg("该地址不存在，请检查");
                            Log.d(TAG, "该地址不存在，请检查");
                            stopSelf();
                        } else if (e instanceof ConnectException) {
                            if(callback!=null){
                                callback.getSocketStatus(DataMemoryManager.DEVICE_DISCONNECT);
                            }
                            Log.d(TAG, "连接异常或被拒绝，请检查："+e.getMessage()+",五秒后重连");
                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ex) {
                                ex.printStackTrace();
                            }
                            releaseSocket();
                            stopSelf();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    */

}
