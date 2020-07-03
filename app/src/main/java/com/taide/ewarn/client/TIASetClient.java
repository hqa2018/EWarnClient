package com.taide.ewarn.client;

import android.util.Log;

import com.taide.ewarn.interfaces.HandlerSetParamData;
import com.taide.ewarn.model.DataMemoryManager;
import com.taide.ewarn.model.TIAStation;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Scanner;

public class TIASetClient extends Thread {
    String TAG = "TIASetClient";
    private String ip="127.0.0.1";//台站IP
    private int port=9742;//通讯端口
    private String serverip;//台站IP
    private int serverport=5000;//通讯端口
    private int timeout = 30000;//连接等待时间
    HandlerSetParamData handler = null;

    private int socketType = 0; //1:发送获取参数命令 2:发送设置参数命令

    private boolean mRun = false;
    //通信连接对象
    Socket connection;
    //PrintWriter output;
    DataOutputStream output;
    DataInputStream input;


    /**
     * 构造函数
     */
    public TIASetClient(HandlerSetParamData handler, String ip, int port){
        this.ip = ip;
        this.port = port;
        this.handler = handler;
    }

    /**
     * 连接数采
     */
    public boolean login()
    {
        try {
            Log.d(TAG, "login: ");
            connection = new Socket();
            connection.connect(new InetSocketAddress(ip, port), timeout);
            connection.setSoTimeout(this.timeout);
            output = new DataOutputStream(connection.getOutputStream());
            //output = new PrintWriter(new BufferedWriter(new OutputStreamWriter(connection.getOutputStream())), true);
            input = new DataInputStream(connection.getInputStream());
            //input = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            return true;

        } catch (Exception e) {
            Log.d(TAG, "login: socket连接失败");
            //System.out.println("At login method Failed Login.");
            e.printStackTrace(System.out);
            return false;
        }
    }


    /**
     * 发送服务端消息
     * @param message
     */
    public void sendMessage(String message){
        try {
            if (output != null) {
                output.writeUTF(message);// 发给服务端
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "sendMessage: "+e.getMessage());
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
                        if(line.contains("BASEINFO")){
                            DataMemoryManager.currentSocketTIAInfo = new TIAStation();
                            String SN = "";
                            String[] strlist = line.split("\r\n");
                            for(String content : strlist){
                                //Log.d("SetTIAParamsActivity", "content: "+content);
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
                                        DataMemoryManager.currentSocketTIAInfo.setDEVTYPE(sps[1]);
                                    }
                                    if(sps[0].equals("LATITUDE")){
                                        DataMemoryManager.currentSocketTIAInfo.setDEVTYPE(sps[1]);
                                    }
                                }
                            }
                            Log.d("SetTIAParamsActivity", "currentSNCode: "+SN);
                            DataMemoryManager.currentTIAParams = line;
                            handler.processParamsData("gettiaparams",true);
                        }else if(line.contains("SUCCESS")){
                            handler.processParamsData("settiaparams",true);
                        }else{
                            handler.processParamsData("gettiaparams",false);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    /**
     * 断开连接
     */
    public void logout(){
        try {
            if(connection != null)
                connection.close();
        }catch(IOException e) {
            System.out.println("Socket not closed");
        }
    }


    @Override
    public void run() {
        if(login()){
            Log.d(TAG, "run: send GETPARAM");
            this.handler.processParamsData("TIASetParam", true);
            listenServerReply(input);
//            Scanner scanner = new Scanner(System.in);
//            String line = null;
//            while((line = scanner.nextLine()) != null){//读取从键盘输入的一行
//                sendMessage(line);
//            }

            sendMessage("GETPARAM");
            socketType = 1;

        }else{
            System.out.println("Connect fail!!");
            if(this.handler != null){
                this.handler.processParamsData("TIASetParam", false);
            }
        }
    }

    public void startClient() {
        try {
            // 连接到服务器
            Socket socket = new Socket("127.0.0.1", 9742);
            DataInputStream dis = new DataInputStream(socket.getInputStream());
            DataOutputStream dos = new DataOutputStream(
                    socket.getOutputStream());
            Scanner scanner = new Scanner(System.in);
            String line = null;
            listenServerReply(dis);
            while ((line = scanner.nextLine()) != null) {// 读取从键盘输入的一行
                dos.writeUTF(line);// 发给服务端
                System.out.println("client send msg : " + line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        //TIASetClient r = new TIASetClient("127.0.0.1", 9742);
        //r.start();
        //new TIASetClient("127.0.0.1", 9742).startClient();
    }
}
