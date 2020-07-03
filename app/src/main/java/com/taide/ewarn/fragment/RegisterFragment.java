package com.taide.ewarn.fragment;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.taide.ewarn.R;
import com.taide.ewarn.activity.DevContentActivity;
import com.taide.ewarn.base.BaseFragment;
import com.taide.ewarn.model.DataMemoryManager;
import com.taide.ewarn.utils.DateUtil;
import com.taide.ewarn.utils.FileUtil;
import com.taide.ewarn.utils.HttpRequestProxy;
import com.taide.ewarn.utils.IniFile;
import com.taide.ewarn.utils.RegisterUtil;
import com.taide.ewarn.utils.XToastUtils;
import com.xuexiang.xui.widget.actionbar.TitleBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class RegisterFragment extends BaseFragment {
    View currentView;
    TitleBar mTitleBar;

    public static final String SELECT_INFO = "Station_SN";
    String TAG = "RegisterFragment";

    EditText Register_Edit_address;
    EditText Register_Edit_License;
    EditText Register_Edit_Owner;
    EditText Register_Edit_Type;
    EditText Register_Edit_groupId;
    EditText Register_Edit_clientId;
    EditText Register_Edit_uname;
    EditText Register_Edit_pwd;
    EditText Register_Edit_Action;
    Switch Register_Switch_Action;
    EditText Register_Edit_scope;
    EditText Register_Edit_ackscope;
    EditText Register_Edit_topic;
    EditText Register_Edit_acktopic;
    EditText Register_Edit_mqtthost;
    EditText Register_Edit_mqttport;
    EditText Register_Edit_alterinfo;

    Button Register_Button_register;
    Button Register_Button_altertinfo;
    Button Register_Button_suspend;
    Button Register_Button_authorize;
    Button Register_Button_cancelAuth;
    Button Start_Button_MQTT;
    Button Register_Button_getTokenInfo;

    TextView Register_Edit_getinfo;
    TextView Register_Edit_result;
    EditText Register_Edit_ackmqtthost;
    EditText Register_Edit_ackmqttport;

    EditText Register_Edit_altergroupId;
    EditText Register_Edit_alterpwd;
    EditText Register_Edit_alterowner;
    EditText Register_Edit_altertype;

    @Override
    public View setLayoutView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        currentView = inflater.inflate(R.layout.fragment_register,container,false);

        return currentView;
    }

    @Override
    public void onResume() {
        super.onResume();

//        mTitleBar = mActivity.findViewById(R.id.topbar);
//        mTitleBar.addAction(new TitleBar.ImageAction(R.drawable.ic_fab_content) {
//            @Override
//            public void performAction(View view) {
//                XToastUtils.toast("保存");
//            }
//        });


        //获取台站参数
        Register_Button_register = (Button) currentView.findViewById(R.id.Register_Button_register);
        Register_Button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegisterAcition("register");
            }
        });
        Register_Button_altertinfo = (Button) currentView.findViewById(R.id.Register_Button_altertinfo);
        Register_Button_altertinfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegisterAcition("altertinfo");
            }
        });
        Register_Button_suspend = (Button) currentView.findViewById(R.id.Register_Button_suspend);
        Register_Button_suspend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegisterAcition("suspend");
            }
        });
        Register_Button_authorize = (Button) currentView.findViewById(R.id.Register_Button_authorize);
        Register_Button_authorize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegisterAcition("authorize");
            }
        });
        Register_Button_cancelAuth = (Button) currentView.findViewById(R.id.Register_Button_cancelAuth);
        Register_Button_cancelAuth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegisterAcition("cancelAuth");
            }
        });
        Register_Button_getTokenInfo = (Button) currentView.findViewById(R.id.Register_Button_getTokenInfo);
        Register_Button_getTokenInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doRegisterAcition("getTokenInfo");
            }
        });

        Button Button_saveInfo = (Button) currentView.findViewById(R.id.Button_saveInfo);
        Button_saveInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadParams();
            }
        });

        Button Button_startMqtt = (Button) currentView.findViewById(R.id.Button_startMqtt);
        Button_startMqtt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMqtt();
            }
        });


        Register_Edit_address = (EditText)currentView.findViewById(R.id.Register_Edit_address);
        Register_Edit_License = (EditText)currentView.findViewById(R.id.Register_Edit_License);
        Register_Edit_Owner = (EditText)currentView.findViewById(R.id.Register_Edit_Owner);
        Register_Edit_Type = (EditText)currentView.findViewById(R.id.Register_Edit_Type);
        Register_Edit_groupId = (EditText)currentView.findViewById(R.id.Register_Edit_groupId);
        Register_Edit_clientId = (EditText)currentView.findViewById(R.id.Register_Edit_clientId);
        Register_Edit_uname = (EditText)currentView.findViewById(R.id.Register_Edit_uname);
        Register_Edit_pwd = (EditText)currentView.findViewById(R.id.Register_Edit_pwd);
        Register_Edit_Action = (EditText)currentView.findViewById(R.id.Register_Edit_Action);
        //Register_Switch_Action = (Switch) currentView.findViewById(R.id.Register_Switch_Action);

        Register_Edit_scope = (EditText)currentView.findViewById(R.id.Register_Edit_scope);
        Register_Edit_topic = (EditText)currentView.findViewById(R.id.Register_Edit_topic);
        Register_Edit_mqtthost = (EditText)currentView.findViewById(R.id.Register_Edit_mqtthost);
        Register_Edit_mqttport = (EditText)currentView.findViewById(R.id.Register_Edit_mqttport);

        Register_Edit_ackscope = (EditText)currentView.findViewById(R.id.Register_Edit_ackscope);
        Register_Edit_acktopic = (EditText)currentView.findViewById(R.id.Register_Edit_acktopic);
        Register_Edit_ackmqtthost = (EditText)currentView.findViewById(R.id.Register_Edit_ackmqtthost);
        Register_Edit_ackmqttport = (EditText)currentView.findViewById(R.id.Register_Edit_ackmqttport);

        Register_Edit_alterinfo = (EditText)currentView.findViewById(R.id.Register_Edit_alterinfo);
        Register_Edit_getinfo = (TextView)currentView.findViewById(R.id.Register_Edit_getinfo);
        Register_Edit_result = (TextView)currentView.findViewById(R.id.Register_Edit_result);


        Register_Edit_altergroupId = (EditText)currentView.findViewById(R.id.Register_Edit_altergroupId);
        Register_Edit_alterowner = (EditText)currentView.findViewById(R.id.Register_Edit_alterowner);
        Register_Edit_altertype = (EditText)currentView.findViewById(R.id.Register_Edit_altertype);
        Register_Edit_alterpwd = (EditText)currentView.findViewById(R.id.Register_Edit_alterpwd);

        updateInfo();
    }

    public void doRegisterAcition(final String actionName){

        final String Registeraddress = Register_Edit_address.getText().toString().trim();
        String License = Register_Edit_License.getText().toString().trim();
        String Owner = Register_Edit_Owner.getText().toString().trim();
        String Type = Register_Edit_Type.getText().toString().trim();
        String clientId = Register_Edit_clientId.getText().toString().trim();
        String uname = Register_Edit_uname.getText().toString().trim();
        String pwd = Register_Edit_pwd.getText().toString().trim();
        String scope = Register_Edit_ackscope.getText().toString().trim().equals("") ? Register_Edit_scope.getText().toString().trim() :
                Register_Edit_scope.getText().toString().trim() + "," + Register_Edit_ackscope.getText().toString().trim();
        String action = Register_Edit_Action.getText().toString().trim();



        /**
         * 注册请求
         */
        Map<String, String> params = null;
        String url = Registeraddress;
        if(actionName.equals("register")){      //终端注册
            params = RegisterUtil.register(Registeraddress, License, Owner, Type);
            url = Registeraddress+"/register/tm_register";//register URL
            Register_Edit_getinfo.setText(params.toString());
            //FileUtil.writeTolog("/Log/Core/","RegisterServiceActivity","[RegisterServiceActivity.doRegisterAcition] Starting register action with params="+params.toString()+",url="+url);

        }else if(actionName.equals("authorize")){   //授权申请
            Log.d(TAG, "doRegisterAcition: scope="+scope);
            url = Registeraddress+"/register/tm_authorize";//Authorization apply or update
            params = RegisterUtil.authorize(url, clientId, uname, pwd, scope);
            Register_Edit_getinfo.setText(params.toString());
            //FileUtil.writeTolog("/Log/Core/","RegisterServiceActivity","[RegisterServiceActivity.doRegisterAcition] Starting authorize action with params="+params.toString()+",url="+url);

        }else if(actionName.equals("altertinfo")){      //变更
            //变更信息包括groupId、owner、type、pwd
            String alterinfo = "";
            String altergroupId = Register_Edit_altergroupId.getText().toString().trim();
            String alterowner = Register_Edit_alterowner.getText().toString().trim();
            String altertype = Register_Edit_altertype.getText().toString().trim();
            String alterpwd = Register_Edit_alterpwd.getText().toString().trim();
            if(!altergroupId.equals("")){
                alterinfo = "groupId:"+altergroupId;
            }
            if(!alterowner.equals("")){
                alterinfo += alterinfo.equals("") ? "owner:"+alterowner : ",owner:"+alterowner;
            }
            if(!altertype.equals("")){
                alterinfo += alterinfo.equals("") ? "type:"+altertype : ",type:"+altertype;
            }
            if(!alterpwd.equals("")){
                alterinfo += alterinfo.equals("") ? "pwd:"+alterpwd : ",pwd:"+alterpwd;
            }
            Log.d(TAG, "doRegisterAcition: alterinfo="+alterinfo);
            //原来的信息
            Owner = DataMemoryManager.iniFile.get("MQTT", "Owner");
            Type = DataMemoryManager.iniFile.get("MQTT", "Type");
            pwd = DataMemoryManager.iniFile.get("MQTT", "pwd");
            url = Registeraddress+"/register/tm_altertinfo";//Authorization apply or update
            params = RegisterUtil.altertinfo(url,clientId,  Owner, Type, uname, pwd, alterinfo);
            Register_Edit_getinfo.setText(params.toString());
            //FileUtil.writeTolog("/Log/Core/","RegisterServiceActivity","[RegisterServiceActivity.doRegisterAcition] Starting altertinfo action with params="+params.toString()+",url="+url);

        }else if(actionName.equals("suspend")){     //暂停/恢复
            url = Registeraddress+"/register/tm_suspend";//Authorization apply or update
            params = RegisterUtil.suspend(url,clientId, uname, pwd,action);
            Register_Edit_getinfo.setText(params.toString());
            //FileUtil.writeTolog("/Log/Core/","RegisterServiceActivity","[RegisterServiceActivity.doRegisterAcition] Starting suspend action with params="+params.toString()+",url="+url);

        }else if(actionName.equals("getTokenInfo")){    //授权查询
            url = Registeraddress+"/register/tm_getTokenInfo";//Authorization apply or update
            showAuthSearchDialog(url,clientId, uname, pwd);
            return;
        }else if(actionName.equals("cancelAuth")){      //授权撤销
            url = Registeraddress+"/register/tm_cancelAuth";//Authorization apply or update
            showCancelAuthDialog(url,clientId, uname, pwd);
            return;
        }

        Log.d(TAG, "actionName: "+actionName);

        /**
         * 返回处理
         */
        httpRequestHandle(url , params , actionName);

    }

    private void updateInfo(){
        Log.d(TAG, "updateInfo: RegisterFragment");
        Register_Edit_address.setText(DataMemoryManager.iniFile.get("MQTT", "Registeraddress"));
        Register_Edit_License.setText(DataMemoryManager.iniFile.get("MQTT", "License"));
        Register_Edit_Owner.setText(DataMemoryManager.iniFile.get("MQTT", "Owner"));
        Register_Edit_Type.setText(DataMemoryManager.iniFile.get("MQTT", "Type"));
        Register_Edit_groupId.setText(DataMemoryManager.iniFile.get("MQTT", "groupId"));
        Register_Edit_clientId.setText(DataMemoryManager.iniFile.get("MQTT", "clientId"));
        Register_Edit_uname.setText(DataMemoryManager.iniFile.get("MQTT", "uname"));
        Register_Edit_pwd.setText(DataMemoryManager.iniFile.get("MQTT", "pwd"));
        Register_Edit_Action.setText(DataMemoryManager.iniFile.get("MQTT", "Action"));
        Register_Edit_scope.setText(DataMemoryManager.iniFile.get("MQTT", "scope"));
        Register_Edit_topic.setText(DataMemoryManager.iniFile.get("MQTT", "topic"));
        Register_Edit_mqtthost.setText(DataMemoryManager.iniFile.get("MQTT", "mqtthost"));
        Register_Edit_mqttport.setText(DataMemoryManager.iniFile.get("MQTT", "mqttport"));
        Register_Edit_ackscope.setText(DataMemoryManager.iniFile.get("MQTT", "ackscope"));
        Register_Edit_acktopic.setText(DataMemoryManager.iniFile.get("MQTT", "acktopic"));
        Register_Edit_ackmqtthost.setText(DataMemoryManager.iniFile.get("MQTT", "ackmqtthost"));
        Register_Edit_ackmqttport.setText(DataMemoryManager.iniFile.get("MQTT", "ackmqttport"));
    }

    private void setInfo(){
        DataMemoryManager.iniFile.set("MQTT", "Registeraddress",Register_Edit_address.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "License",Register_Edit_License.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "Owner",Register_Edit_Owner.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "Type",Register_Edit_Type.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "groupId",Register_Edit_groupId.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "clientId",Register_Edit_clientId.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "uname",Register_Edit_uname.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "pwd",Register_Edit_pwd.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "Action",Register_Edit_Action.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "scope",Register_Edit_scope.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "topic",Register_Edit_topic.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "mqtthost",Register_Edit_mqtthost.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "mqttport",Register_Edit_mqttport.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "ackscope",Register_Edit_ackscope.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "acktopic",Register_Edit_acktopic.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "ackmqtthost",Register_Edit_ackmqtthost.getText().toString().trim());
        DataMemoryManager.iniFile.set("MQTT", "ackmqttport",Register_Edit_ackmqttport.getText().toString().trim());
        DataMemoryManager.iniFile.save();

        //Toast.makeText(this,"参数设置成功!",Toast.LENGTH_SHORT).show();
        //XToastUtils.info("参数保存");
        showTipDialog("参数保存成功.");
    }

    private void uploadParams(){
        setInfo();
        DevContentActivity devActivity = (DevContentActivity ) getActivity();
        Log.d(TAG, "setInfo: "+DataMemoryManager.iniFile.readFileString());
        showTipDialog("已执行参数上传.");
        devActivity.sendCommand("SETPARAMS@"+DataMemoryManager.iniFile.readFileString());
    }
    private void startMqtt(){
        DevContentActivity devActivity = (DevContentActivity ) getActivity();
        Log.d(TAG, "setInfo: "+DataMemoryManager.iniFile.readFileString());
        showTipDialog("已执行启动MQTT服务.");
        devActivity.sendCommand("STARTMQTT");
    }

    Handler handler=new Handler();
    public void showTipDialog(String tipword){
        XToastUtils.success(tipword);
    }

    /**
     * 授权查询
     * @param url
     * @param clientId
     * @param uname
     * @param pwd
     */
    private void showAuthSearchDialog(final String url, final String clientId, final String uname, final String pwd) {

        String scopes = Register_Edit_scope.getText().toString().trim();
        if(!"".equals(scopes)){
            final String[] items = scopes.split(",");
//            new QMUIDialog.MenuDialogBuilder(this)
//                    .addItems(items, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            Toast.makeText(RegisterServiceActivity.this, "你选择了 " + items[which], Toast.LENGTH_SHORT).show();
//                            dialog.dismiss();
//                            String scope = items[which];
//                            Map<String, String> params = RegisterUtil.getTokenInfo(url,clientId, uname, pwd, scope);
//                            Register_Edit_getinfo.setText(params.toString());
//                            FileUtil.writeTolog("/Log/Core/","RegisterServiceActivity","[RegisterServiceActivity.doRegisterAcition] Starting getTokenInfo action with params="+params.toString()+",url="+url);
//                            httpRequestHandle(url ,params , "getTokenInfo");
//                        }
//                    })
//                    .create(mCurrentDialogStyle).show();
        }
    }


    private void showCancelAuthDialog(final String url, final String clientId, final String uname, final String pwd) {
        String scopes = Register_Edit_scope.getText().toString().trim();
        if(!"".equals(scopes)){
//            final String[] items = scopes.split(",");
//            final QMUIDialog.MultiCheckableDialogBuilder builder = new QMUIDialog.MultiCheckableDialogBuilder(this)
//                    .setCheckedItems(new int[]{1, 3})
//                    .addItems(items, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//
//                        }
//                    });
//            builder.addAction("取消", new QMUIDialogAction.ActionListener() {
//                @Override
//                public void onClick(QMUIDialog dialog, int index) {
//                    dialog.dismiss();
//                }
//            });
//            builder.addAction("提交", new QMUIDialogAction.ActionListener() {
//                @Override
//                public void onClick(QMUIDialog dialog, int index) {
//                    String scope = "";
//                    for (int i = 0; i < builder.getCheckedItemIndexes().length; i++) {
//                        //result += "" + builder.getCheckedItemIndexes()[i] + ",";
//                        scope += scope == "" ? items[builder.getCheckedItemIndexes()[i]] : "," +items[builder.getCheckedItemIndexes()[i]];
//                    }
//                    Toast.makeText(RegisterServiceActivity.this, "选择："+scope, Toast.LENGTH_SHORT).show();
//                    dialog.dismiss();
//                    Map params = RegisterUtil.cancelAuth(url,clientId, uname, pwd, scope);
//                    Register_Edit_getinfo.setText(params.toString());
//                    FileUtil.writeTolog("/Log/Core/","RegisterServiceActivity","[RegisterServiceActivity.doRegisterAcition] Starting cancelAuth action with params="+params.toString()+",url="+url);
//                    httpRequestHandle(url ,params , "cancelAuth");
//                }
//            });
//            builder.create(mCurrentDialogStyle).show();
        }

    }


    /**
     * 注册服务器请求处理
     * @param url
     * @param params
     * @param actionName
     */
    private void httpRequestHandle(String url , Map params , final String actionName){
        Log.d(TAG, "httpRequestHandle: url="+url);
        Log.d(TAG, "httpRequestHandle: params="+params.toString());
        HttpRequestProxy.doPost(url , params, "utf-8", new HttpRequestProxy.HttpCallBack() {
            @Override
            public void onFailure(IOException e) {
                Log.d(TAG, "onFailure: "+e.getMessage());
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Register_Edit_result.setText(response);
                        Register_Edit_result.setText("register fail!");
                    }
                });
            }
            @Override
            public void onResponse(final String response) throws IOException {
                //FileUtil.writeTolog("/Log/Core/","RegisterServiceActivity","[RegisterServiceActivity.doRegisterAcition] Get result="+response);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Log.d(TAG, "response: "+response);
                            JSONObject jsonObject = new JSONObject(response);
                            if(actionName.equals("register")){  //返回注册信息
                                //{"Result":{"result":"成功","groupId":"TESTGroup_1","clientId":"TESTClient_16","uname":"TESTClient_16","pwd":"D56A8713BF95388F","suspend":false}}
                                JSONObject result = jsonObject.getJSONObject("Result");
                                Register_Edit_groupId.setText(result.getString("groupId"));
                                Register_Edit_clientId.setText(result.getString("clientId"));
                                Register_Edit_uname.setText(result.getString("uname"));
                                Register_Edit_pwd.setText(result.getString("pwd"));
                                Register_Edit_Action.setText(result.getString("suspend"));

                                if(result.has("suspend")){
                                    if(result.getString("suspend").equals("true")){
                                        Register_Edit_Action.setText("true");
                                        Register_Button_suspend.setText("暂停");
                                    }else {
                                        Register_Edit_Action.setText("false");
                                        Register_Button_suspend.setText("恢复");
                                    }
                                }

                                setInfo();
                            }
                            if(actionName.equals("authorize")){    //授权申请
                                //{"Result":[{"scope":"eew\/json","mqtthost":"47.107.139.63","port":"1883","topic":"test\/msg\/eew"}]}
                                //{"Result":[{"scope":"eew\/json","mqtthost":"47.107.139.63","port":"1883","topic":"test\/msg\/eew\/json"},{"scope":"eqr\/json","mqtthost":"47.107.139.63","port":"1883","topic":"test\/msg\/eqr\/json"},{"scope":"eew\/response","mqtthost":"47.107.139.63","port":"1883","topic":"test\/response\/TESTClient_16\/eew"}]}
                                String scopes = "";
                                JSONArray resultArray = jsonObject.getJSONArray("Result");
                                if(resultArray.length() > 0){
                                    Map<String,String> mqttMap = new HashMap<String, String>();
                                    for(int i=0;i<resultArray.length();i++){
                                        JSONObject result = resultArray.getJSONObject(i);
                                        String scope = result.getString("scope");
                                        String topic = result.getString("topic");
                                        String mqtthost = result.getString("mqtthost");
                                        String port = result.getString("port");
                                        if(scope.equals(Register_Edit_ackscope.getText().toString().trim())){
                                            Register_Edit_acktopic.setText(topic);
                                            Register_Edit_ackmqtthost.setText(mqtthost);
                                            Register_Edit_ackmqttport.setText(port);
                                        }else{
                                            scopes += scopes.equals("") ? scope : "," + scope;
                                            if(mqttMap.get(mqtthost+":"+port) != null){
                                                mqttMap.put(mqtthost+":"+port,mqttMap.get(mqtthost+":"+port) + ","+topic);
                                            }else {
                                                mqttMap.put(mqtthost+":"+port,topic);
                                            }
                                        }
                                    }

                                    String mqtthosts = "";
                                    String topics = "";
                                    String ports = "";

                                    for(Map.Entry<String,String> entry : mqttMap.entrySet()){
                                        String hostport = entry.getKey();
                                        String host = hostport.split(":")[0];
                                        String port = hostport.split(":")[1];

                                        mqtthosts += mqtthosts.equals("") ? host : "#" + host;
                                        ports += ports.equals("") ? port : "#" + port;
                                        topics += topics.equals("") ? entry.getValue() : "#" + entry.getValue();

                                    }

                                    Register_Edit_scope.setText(scopes);
                                    Register_Edit_mqtthost.setText(mqtthosts);
                                    Register_Edit_topic.setText(topics);
                                    Register_Edit_mqttport.setText(ports);

                                    setInfo();
                                }

                            }else if(actionName.equals("altertinfo")){     //变更通过
                                //{"result":"通过","details":{"type":{"former":"TIA-20","present":"TIA-30"}}}
                                //{"result":"通过","details":{"owner":{"former":"TD041700","present":"TD123"}}}
                                //变更信息包括groupId、owner、type、pwd
                                if(jsonObject.getString("Result").equals("通过") || jsonObject.getString("result").equals("通过") ){
                                    JSONObject details = jsonObject.getJSONObject("details");
                                    Log.d(TAG, "details: "+details.toString());
                                    if(details.has("groupId"))
                                        Register_Edit_groupId.setText(details.getJSONObject("groupId").getString("present"));
                                    if(details.has("owner"))
                                        Register_Edit_Owner.setText(details.getJSONObject("owner").getString("present"));
                                    if(details.has("type"))
                                        Register_Edit_Type.setText(details.getJSONObject("type").getString("present"));
                                    if(details.has("pwd"))
                                        Register_Edit_pwd.setText(details.getJSONObject("pwd").getString("present"));
                                    setInfo();

                                }
                            }else if(actionName.equals("suspend")){ //暂停与恢复
                                //{"Result":"通过","details":{"suspend":{"former":false,"present":true}}}
                                //{"Result":"通过","details":{"suspend":{"former":false,"present":false}}}
                                String result = jsonObject.getString("Result");
                                if(result.equals("通过")){
                                    JSONObject details = jsonObject.getJSONObject("details");
                                    JSONObject supend = details.getJSONObject("suspend");
                                    String value = supend.getString("present");
                                    Register_Edit_Action.setText("false");
                                    Register_Button_suspend.setText("恢复");
                                    if(value.equals("false")){
                                        Register_Edit_Action.setText("true");
                                        Register_Button_suspend.setText("暂停");
                                    }

                                }
                            }else if(actionName.equals("cancelAuth")){
                                //{"Result":[{"scope":"eew/json","result":"通过"}]}

                                JSONArray resultArray = jsonObject.getJSONArray("Result");

                                if(resultArray.length() > 0){

                                    String scopeEdt = Register_Edit_scope.getText().toString().trim();
                                    String topicEdt = Register_Edit_topic.getText().toString().trim();

                                    for(int i=0;i<resultArray.length();i++){
                                        JSONObject result = resultArray.getJSONObject(i);
                                        scopeEdt = scopeEdt.replaceAll(result.getString("scope"),"");
                                    }
                                    String scopeResult = "";
                                    String[] scopeArr = scopeEdt.split(",");
                                    for(String scope : scopeArr){
                                        if(!"".equals(scope)){
                                            scopeResult += scopeResult.equals("") ? scope : ","+scope;
                                        }
                                    }
                                    String[] topicArr = topicEdt.split(",");
                                    Log.d(TAG, "scopeEdt: "+scopeEdt);
                                    Register_Edit_scope.setText(scopeResult);
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Register_Edit_result.setText(response);
                    }
                });
            }
        });
    }

    public boolean isServiceExisted(String className) {
        ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList = am.getRunningServices(Integer.MAX_VALUE);
        int myUid = android.os.Process.myUid();
        for (ActivityManager.RunningServiceInfo runningServiceInfo : serviceList) {
            if (runningServiceInfo.uid == myUid && runningServiceInfo.service.getClassName().equals(className)) {
                return true;
            }
        }
        return false;
    }

}
