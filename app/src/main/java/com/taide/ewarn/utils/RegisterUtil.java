package com.taide.ewarn.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * 注册类
 */
public class RegisterUtil {

    public static String encoding = "UTF-8";

    /**
     * 客户端注册
     * @param rooturl "http://192.168.86.237:8080/seishare/";
     * @param license
     * @param owner
     * @param type
     */
    public static Map<String, String> register(String rooturl, String license, String owner, String type){
        Map<String, String> params = new HashMap<String, String>();
        String url = rooturl+"register/tm_register";//register URL
        params.put("license", license);
        params.put("owner", owner);
        params.put("type", type);
        return params;
    }

    /**
     * 更新信息
     */
    public static Map<String, String> altertinfo(String rooturl, String clientId, String owner, String type, String uname, String pwd, String alterinfo){
        Map<String, String> params = new HashMap<String, String>();
        String url = rooturl+"register/tm_altertinfo";//Change information
        params.put("clientId", clientId);
        params.put("owner", owner);
        params.put("type", type);
        params.put("uname", uname);
        params.put("pwd", pwd);
        params.put("alterinfo", alterinfo);//变更信息参数
        return params;
    }

    /**
     * 暂停/恢复
     */
    public static Map<String, String> suspend(String rooturl, String clientId, String uname, String pwd, String action){
        Map<String, String> params = new HashMap<String, String>();
        String url = rooturl+"register/tm_suspend";//Change information
        params.put("action", action);//true -- stop, false -- start,_action原为action，与域冲突，重命名
        params.put("suspend", action);
        params.put("clientId", clientId);
        params.put("uname", uname);
        params.put("pwd", pwd);
        return params;
    }


    /**
     * 授权申请或更新
     */
    public static Map<String, String> authorize(String rooturl, String clientId, String uname, String pwd, String scope){
        Map<String, String> params = new HashMap<String, String>();
        String url = rooturl+"register/tm_authorize";//Authorization apply or update
        params.put("clientId", clientId);
        params.put("uname", uname);
        params.put("pwd", pwd);
        params.put("scope", scope);//订阅多个，逗号隔开
        return params;
    }

    /**
     * 查询授权
     */
    public static Map<String, String> getTokenInfo(String rooturl, String clientId, String uname, String pwd, String scope){
        Map<String, String> params = new HashMap<String, String>();
        String url = rooturl+"register/tm_getTokenInfo";//select the uthorization for scope
        params.put("clientId", clientId);
        params.put("uname", uname);
        params.put("pwd", pwd);
        params.put("scope", scope);
        return params;
    }

    /**
     * 授权撤销
     */
    public static Map<String, String> cancelAuth(String rooturl, String clientId, String uname, String pwd, String scope){
        Map<String, String> params = new HashMap<String, String>();
        String url = rooturl+"register/tm_cancelAuth";//Cancellation scope
        params.put("clientId", clientId);
        params.put("uname", uname);
        params.put("pwd", pwd);
        params.put("scope", scope);
        return params;
    }


}
