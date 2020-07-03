package com.taide.ewarn.utils;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

public class NetworkUtil {



    /**
     * wifi获取 已连接网络路由  路由ip地址
     * @param context
     * @return
     */
    public static String getWifiRouteIPAddress(Context context) {
        WifiManager wifi_service = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        //        WifiInfo wifiinfo = wifi_service.getConnectionInfo();
        //        System.out.println("Wifi info----->" + wifiinfo.getIpAddress());
        //        System.out.println("DHCP info gateway----->" + Formatter.formatIpAddress(dhcpInfo.gateway));
        //        System.out.println("DHCP info netmask----->" + Formatter.formatIpAddress(dhcpInfo.netmask));
        //DhcpInfo中的ipAddress是一个int型的变量，通过Formatter将其转化为字符串IP地址
        String routeIp = Formatter.formatIpAddress(dhcpInfo.gateway);
        Log.i("route ip", "wifi route ip：" + routeIp);

        return routeIp;
    }
}
