package com.taide.ewarn.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/**
 * <pre>
 * HTTP请求代理类,在这里主要用于定时下载速报目录
 * </pre>
 *
 * @author benl
 * @version 1.0, 2007-7-3
 */
public class HttpRequestProxy
{
    /**
     * 连接超时
     */
    private static int connectTimeOut = 5000;

    /**
     * 读取数据超时
     */
    private static int readTimeOut = 10000;
    
    private HttpURLConnection url_con = null;
    private InputStream inputStream = null;

    /**
     * 请求编码
     */
    private static String requestEncoding = "utf-8";

    public void closeConnection() {
    	if(url_con != null)
    		url_con.disconnect();
    }
    
    public void closeInputStream() {
    	try {
    		if(inputStream != null)
    			inputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
    }

    /**
     * <pre>
     * 发送带参数的GET的HTTP请求
     * </pre>
     *
     * @param reqUrl HTTP请求URL
     * @param parameters 参数映射表
     * @return HTTP响应的字符串
     */
    public static String doGet(String reqUrl, Map parameters,
                               String recvEncoding)
    {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try
        {
            StringBuffer params = new StringBuffer();
            for (Iterator iter = parameters.entrySet().iterator(); iter
                    .hasNext();)
            {
                Entry element = (Entry) iter.next();
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(element.getValue().toString(),
                        HttpRequestProxy.requestEncoding));
                params.append("&");
            }

            if (params.length() > 0)
            {
                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("GET");
            System.setProperty("sun.net.client.defaultConnectTimeout", String
                    .valueOf(HttpRequestProxy.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
            System.setProperty("sun.net.client.defaultReadTimeout", String
                    .valueOf(HttpRequestProxy.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
            // url_con.setConnectTimeout(5000);//（单位：毫秒）jdk
            // 1.5换成这个,连接超时
            // url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();

            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in,
                    recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer temp = new StringBuffer();
            String crlf= System.getProperty("line.separator");
            while (tempLine != null)
            {
                temp.append(tempLine);
                temp.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
        }
        catch (IOException e)
        {

        }
        finally
        {
            if (url_con != null)
            {
                url_con.disconnect();
            }
        }

        return responseContent;
    }

    
    /**
     * 从输入流中获取字节数组
     * @param inputStream
     * @return
     * @throws IOException
     */
    private static  byte[] readInputStream(InputStream inputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int len = 0;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        while((len = inputStream.read(buffer)) != -1) {
            bos.write(buffer, 0, len);
        }
        bos.close();
        return bos.toByteArray();
    }
    
    public static void  downLoadFromUrl(String urlStr, String fileName, String savePath) throws IOException {
    	URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
        conn.setConnectTimeout(3*1000);
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

        InputStream inputStream = conn.getInputStream();
        
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,"GBK"));
        StringBuilder response = new StringBuilder();
        String line;
        while((line=reader.readLine())!=null){
        	response.append(line);
        }


        File saveDir = new File(savePath);
        if(!saveDir.exists()){
            saveDir.mkdir();
        }
        File file = new File(saveDir+ File.separator+fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(response.toString().getBytes("GBK"));
        if(fos!=null){
            fos.close();
        }
        if(inputStream!=null){
            inputStream.close();
        }
    }

    
    
    
    
    /**
     * <pre>
     * 发送不带参数的GET的HTTP请求
     * </pre>
     *
     * @param reqUrl HTTP请求URL
     * @return HTTP响应的字符串
     */
    public static String doGet(String reqUrl, String recvEncoding)
    {
        HttpURLConnection url_con = null;
        String responseContent = null;
        try
        {
            StringBuffer params = new StringBuffer();
            String queryUrl = reqUrl;
            int paramIndex = reqUrl.indexOf("?");

            if (paramIndex > 0)
            {
                queryUrl = reqUrl.substring(0, paramIndex);
                String parameters = reqUrl.substring(paramIndex + 1, reqUrl
                        .length());
                String[] paramArray = parameters.split("&");
                for (int i = 0; i < paramArray.length; i++)
                {
                    String string = paramArray[i];
                    int index = string.indexOf("=");
                    if (index > 0)
                    {
                        String parameter = string.substring(0, index);
                        String value = string.substring(index + 1, string
                                .length());
                        params.append(parameter);
                        params.append("=");
                        params.append(URLEncoder.encode(value,
                                HttpRequestProxy.requestEncoding));
                        params.append("&");
                    }
                }

                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(queryUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("GET");
            System.setProperty("sun.net.client.defaultConnectTimeout", String
                    .valueOf(HttpRequestProxy.connectTimeOut));// （单位：毫秒）jdk1.4换成这个,连接超时
            System.setProperty("sun.net.client.defaultReadTimeout", String
                    .valueOf(HttpRequestProxy.readTimeOut)); // （单位：毫秒）jdk1.4换成这个,读操作超时
            // url_con.setConnectTimeout(5000);//（单位：毫秒）jdk
            // 1.5换成这个,连接超时
            // url_con.setReadTimeout(5000);//（单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();
            url_con.getOutputStream().close();
            InputStream in = url_con.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in,
                    recvEncoding));
            String tempLine = rd.readLine();
            StringBuffer temp = new StringBuffer();
            String crlf= System.getProperty("line.separator");
            while (tempLine != null)
            {
                temp.append(tempLine);
                temp.append(crlf);
                tempLine = rd.readLine();
            }
            responseContent = temp.toString();
            rd.close();
            in.close();
        }
        catch (IOException e)
        {
        }
        finally
        {
            if (url_con != null)
            {
                url_con.disconnect();
            }
        }

        return responseContent;
    }
    
    /**
     * <pre>
     * 发送带参数的POST的HTTP请求
     * </pre>
     *
     * @param reqUrl HTTP请求URL
     * @param parameters 参数映射表
     * @return HTTP响应的字符串
     */
    public static void doPost(final String reqUrl, final Map parameters, final String recvEncoding, final HttpCallBack hCallBack)
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection url_con = null;
                String responseContent = null;
                try {
                    StringBuffer params = new StringBuffer();
                    for (Iterator iter = parameters.entrySet().iterator(); iter
                            .hasNext(); ) {
                        Entry element = (Entry) iter.next();
                        params.append(element.getKey().toString());
                        params.append("=");
                        params.append(URLEncoder.encode(element.getValue().toString(),
                                HttpRequestProxy.requestEncoding));
                        params.append("&");
                    }

                    if (params.length() > 0) {
                        params = params.deleteCharAt(params.length() - 1);
                    }

                    URL url = new URL(reqUrl);
                    url_con = (HttpURLConnection) url.openConnection();
                    url_con.setRequestMethod("POST");
                    url_con.setConnectTimeout(30000);//（单位：毫秒）jdk
                    // 1.5换成这个,连接超时
                    url_con.setReadTimeout(100000);//（单位：毫秒）jdk 1.5换成这个,读操作超时
                    url_con.setDoOutput(true);
                    byte[] b = params.toString().getBytes();
                    //     System.out.println(params);
                    url_con.getOutputStream().write(b, 0, b.length);
                    url_con.getOutputStream().flush();

                    InputStream in = url_con.getInputStream();
                    BufferedReader rd = new BufferedReader(new InputStreamReader(in,
                            recvEncoding));
                    String tempLine = rd.readLine();
                    StringBuffer tempStr = new StringBuffer();
                    String crlf = System.getProperty("line.separator");
                    while (tempLine != null) {
                        tempStr.append(tempLine);
                        tempStr.append(crlf);
                        tempLine = rd.readLine();
                    }
                    responseContent = tempStr.toString();
                    hCallBack.onResponse(responseContent);
                    rd.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    hCallBack.onFailure(e);
                } finally {
                    if (url_con != null) {
                        url_con.disconnect();
                    }
                }
            }
        }).start();
    }

    public interface HttpCallBack {

        void onFailure(IOException e);

        void onResponse(String response) throws IOException;
    }
    
    /**
     * 
     * @param reqUrl url值
     * @param parameters 传的参数值
     * @param recvEncoding 编码类型
     * @return
     */
    public InputStream doPostForStream(String reqUrl, Map parameters, String recvEncoding) {
        //HttpURLConnection url_con = null;
        //InputStream inputStream = null;
        try
        {
        	StringBuffer params = new StringBuffer();
            for (Iterator iter = parameters.entrySet().iterator(); iter.hasNext();)
            {
                Entry element = (Entry) iter.next();
                params.append(element.getKey().toString());
                params.append("=");
                params.append(URLEncoder.encode(element.getValue().toString(),
                        HttpRequestProxy.requestEncoding));
                params.append("&");
            }

            if (params.length() > 0)
            {
                params = params.deleteCharAt(params.length() - 1);
            }

            URL url = new URL(reqUrl);
            url_con = (HttpURLConnection) url.openConnection();
            url_con.setRequestMethod("POST");
            url_con.setConnectTimeout(15000);//（单位：毫秒）jdk
            // 1.5换成这个,连接超时
            url_con.setReadTimeout(15000);//（单位：毫秒）jdk 1.5换成这个,读操作超时
            url_con.setDoOutput(true);
            byte[] b = params.toString().getBytes();
            url_con.getOutputStream().write(b, 0, b.length);
            url_con.getOutputStream().flush();

            inputStream = url_con.getInputStream();
        }
        catch (IOException e)
        {
        	e.printStackTrace();
        }
        return inputStream;
    }

    /**
     * @return 连接超时(毫秒)
     */
    public static int getConnectTimeOut()
    {
        return HttpRequestProxy.connectTimeOut;
    }

    /**
     * @return 读取数据超时(毫秒)
     */
    public static int getReadTimeOut()
    {
        return HttpRequestProxy.readTimeOut;
    }

    /**
     * @return 请求编码
     */
    public static String getRequestEncoding()
    {
        return requestEncoding;
    }

    /**
     * @param connectTimeOut 连接超时(毫秒)
     */
    public static void setConnectTimeOut(int connectTimeOut)
    {
        HttpRequestProxy.connectTimeOut = connectTimeOut;
    }

    /**
     * @param readTimeOut 读取数据超时(毫秒)
     */
    public static void setReadTimeOut(int readTimeOut)
    {
        HttpRequestProxy.readTimeOut = readTimeOut;
    }

    /**
     * @param requestEncoding 请求编码
     */
    public static void setRequestEncoding(String requestEncoding)
    {
        HttpRequestProxy.requestEncoding = requestEncoding;
    }

    //推送数据测试
    public static void pushdata(){
        try {
            JSONObject monitorData = new JSONObject();
            JSONArray dataArray = new JSONArray();
            JSONObject dataObj = new JSONObject();
            dataObj.put("manCode", "TD");
            dataObj.put("staCode", "TIA02");
            dataObj.put("dataTime", "2019-10-02 21:00:01");
            dataObj.put("monitorType","2");
            dataObj.put("deviceType", "TIA-20");
            dataObj.put("monitorItems", "4211,4232,4228,4223,4233,4234,4235,4236");
            dataObj.put("monitorValues", "1,1,1,1,1,1,1,1");

            dataArray.put(dataObj);
            Map<String, String> map = new HashMap<>();
            monitorData.put("data", dataArray);
            map.put("data", monitorData.toString());

            monitorData.put("apikey", "4fe5ab69de5970fd48bab87ee59a5e94");
            //String responseContent = HttpRequestProxy.doPost("http://120.25.123.11:8080/seishare/monitor/uploaddata", map, "utf-8");
            //System.out.println(responseContent);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    
   
    public static void main(String[] args)
    {
        Map<String, String> map = new HashMap<>();
        String result = doGet("http://192.168.86.237:8080/EWarn/TD.TIA30/Params/tia.ini",map,"urf-8");
        System.out.println(result);
        //pushdata();
    }
}
