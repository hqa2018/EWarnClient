package com.taide.ewarn.utils;

import com.taide.ewarn.model.DataMemoryManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;


/** 
 * ini文件工具类 
 *  
 * @author 衣旧  2014-11-13 
 *   
 */  
public class IniFile {  
      
    /** 
     * 点节 
     *  
     * @author liucf 
     * 
     */  
    public class Section {  
  
        private String name;
  
        private Map<String, Object> values = new LinkedHashMap<String, Object>();
  
        public String getName() {
            return name;  
        }  
  
        public void setName(String name) {
            this.name = name;  
        }  
  
        public void set(String key, Object value) {
            values.put(key, value);  
        }  
  
        public Object get(String key) {
            return values.get(key);  
        }  
  
        public Map<String, Object> getValues() {
            return values;  
        }  
          
          
    }  
      
    /** 
     * 换行符 
     */  
    private String line_separator = "\r\n";
      
    /** 
     * 编码 
     */  
    private String charSet = "UTF-8";
      
    private Map<String, Section> sections = new LinkedHashMap<String, Section>();
  
    /** 
     * 指定换行符 
     *  
     * @param line_separator 
     */  
    public void setLineSeparator(String line_separator){
        this.line_separator = line_separator;  
    }  
      
    /** 
     * 指定编码 
     *  
     * @param charSet 
     */  
    public void setCharSet(String charSet){
        this.charSet = charSet;  
    }  
      
    /** 
     * 设置值 
     *  
     * @param section 
     *          节点 
     * @param key 
     *          属性名 
     * @param value 
     *          属性值 
     */  
    public void set(String section, String key, Object value) {
        Section sectionObject = sections.get(section);  
        if (sectionObject == null)  
            sectionObject = new Section();  
        sectionObject.name = section;  
        sectionObject.set(key, value);  
        sections.put(section, sectionObject);  
    }  
  
    /** 
     * 获取节点 
     *  
     * @param section 
     *          节点名称 
     * @return 
     */  
    public Section get(String section){
        return sections.get(section);  
    }  
      
    /** 
     * 获取值 
     *  
     * @param section 
     *          节点名称 
     * @param key 
     *          属性名称 
     * @return 
     */  
    public String get(String section, String key) {
        return (String)get(section, key, null);
    }  
  
    /** 
     * 获取值 
     *  
     * @param section 
     *          节点名称 
     * @param key 
     *          属性名称 
     * @param defaultValue 
     *          如果为空返回默认值 
     * @return 
     */  
    public String get(String section, String key, String defaultValue) {
        Section sectionObject = sections.get(section);  
        if (sectionObject != null) {  
            Object value = sectionObject.get(key);
            if (value == null || value.toString().trim().equals(""))  
                return defaultValue;  
            return (String)value;
        }  
        return null;  
    }  
      
    /** 
     * 删除节点 
     *  
     * @param section 
     *          节点名称 
     */  
    public void remove(String section){
        sections.remove(section);  
    }  
      
    /** 
     * 删除属性 
     *  
     * @param section 
     *          节点名称 
     * @param key 
     *          属性名称 
     */  
    public void remove(String section, String key){
        Section sectionObject = sections.get(section);  
        if(sectionObject!=null)sectionObject.getValues().remove(key);  
    }  
      
  
    /** 
     * 当前操作的文件对像 
     */  
    private File file = null;

    public String logfile= null;
      
    public IniFile(){
    }

    public IniFile(File file, String logfile){
        this.file = file;
        this.logfile = logfile;
        initFromFile(file);

        if(get("BASEINFO", "SN") != null){
            FileUtil.writeTxtFile(logfile,DateUtil.format(System.currentTimeMillis(), DateUtil.FormatType.yyyy__MM__dd_HH_mm_ss)+" Load IniFile successfull! \r\n",true);
        }else{
            FileUtil.writeTxtFile(logfile,DateUtil.format(System.currentTimeMillis(), DateUtil.FormatType.yyyy__MM__dd_HH_mm_ss)+" Load IniFile fail! \r\n",true);
        }

    }

    public IniFile(File file) {
        this.file = file;  
        initFromFile(file);  
    }  
  
    public IniFile(InputStream inputStream) {
        initFromInputStream(inputStream);  
    }

    public String readFileString(){
        String str="";
        try {
            FileInputStream in = new FileInputStream(this.file);
            // size 为字串的长度 ，这里一次性读完
            int size = in.available();
            byte[] buffer=new byte[size];
            in.read(buffer);
            in.close();
            str=new String(buffer,"utf-8");
        } catch (IOException e) {
            // TODO Auto-generated catch block
            return null;
        }
        return str;
    }

    /** 
     * 加载一个ini文件 
     *  
     * @param file 
     */  
    public void load(File file){
        this.file = file;  
        initFromFile(file);  
    }  
      
    /** 
     * 加载一个输入流 
     *  
     * @param inputStream 
     */  
    public void load(InputStream inputStream){
        initFromInputStream(inputStream);  
    }  
      
    /** 
     * 写到输出流中 
     *  
     * @param outputStream 
     */  
    public void save(OutputStream outputStream){
        BufferedWriter bufferedWriter;
        try {  
            bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,charSet));
            saveConfig(bufferedWriter);  
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  
        }  
    }  
      
    /** 
     * 保存到文件 
     *  
     * @param file 
     */  
    public void save(File file){
        try {  
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            saveConfig(bufferedWriter);  
        } catch (IOException e) {
            e.printStackTrace();  
        }  
    }  
      
    /** 
     * 保存到当前文件 
     */  
    public void save(){  
        save(this.file);
        //FileUtil.writeTxtFile(paramsPath, DataMemoryManager.currentTIAParams,false);
    }  
  
    /** 
     * 从输入流初始化IniFile 
     *  
     * @param inputStream 
     */  
    private void initFromInputStream(InputStream inputStream) {
        BufferedReader bufferedReader;
        try {  
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream,charSet));
            toIniFile(bufferedReader);  
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * 从文件初始化IniFile 
     *  
     * @param file 
     */  
    private void initFromFile(File file) {
        BufferedReader bufferedReader;
        try {  
            bufferedReader = new BufferedReader(new FileReader(file));
            toIniFile(bufferedReader);  
        } catch (FileNotFoundException e) {
            e.printStackTrace();  
        }  
    }  
  
    /** 
     * 从BufferedReader 初始化IniFile 
     *  
     * @param bufferedReader 
     */  
    private void toIniFile(BufferedReader bufferedReader) {
        String strLine;
        Section section = null;  
        Pattern p = Pattern.compile("^\\[.*\\]$");
        try {  
            while ((strLine = bufferedReader.readLine()) != null) {  
//            	 System.out.println(strLine);
                if (strLine.trim().indexOf("=") == -1) {  
//                	System.out.println("in head!!");
                    strLine = strLine.trim();  
                    section = new Section();  
                    section.name = strLine.substring(1, strLine.length() - 1); 
//                    System.out.println(section.name);
                    sections.put(section.name, section);  
                } else {  
                    String[] keyValue = strLine.split("=");
//                    System.out.println("in body!!");
//                    System.out.println(strLine);
//                    System.out.println(keyValue[0]+"/"+keyValue[1]);
                    if (keyValue.length == 2) {  
                        section.set(keyValue[0], keyValue[1]);  
                    }  
                }  
            }  
            bufferedReader.close();  
        } catch (IOException e) {
            e.printStackTrace();  
        }  
    }  
      
    /** 
     * 保存Ini文件 
     *  
     * @param bufferedWriter 
     */  
    private void saveConfig(BufferedWriter bufferedWriter){
        try {  
            boolean line_spe = false;  
            if(line_separator == null || line_separator.trim().equals(""))line_spe = true;  
            for (Section section : sections.values()) {  
                bufferedWriter.write("["+section.getName()+"]");  
                if(line_spe)  
                    bufferedWriter.write(line_separator);  
                else  
                    bufferedWriter.newLine();  
                for (Map.Entry<String, Object> entry : section.getValues().entrySet()) {
                    bufferedWriter.write(entry.getKey());  
                    bufferedWriter.write("=");  
                    bufferedWriter.write(entry.getValue().toString());  
                    if(line_spe)  
                        bufferedWriter.write(line_separator);  
                    else  
                        bufferedWriter.newLine();  
                }  
            }  
            bufferedWriter.close();  
        } catch (IOException e) {
            e.printStackTrace();  
        }  
    }  
  
    public static void main(String[] args) {
        IniFile file = new IniFile(new File("C:\\Users\\taide\\Desktop\\TD.S0001\\Params\\tia2.ini"));
        System.out.println(file.get("BASEINFO", "SN"));
//      file.save(new File("C:\\Users\\taide\\Desktop\\TD.S0001\\Params\\tia2.ini"));  
//        file.remove("ModelFace");  
//        file.save();  
          
//        IniFile file2 = new IniFile();  
//        file2.set("Config", "属性1", "值1");  
//        file2.set("Config", "属性2", "值2");  
//        file2.set("Config1", "属性3", "值3");  
//        file2.save(new File("C:\\Users\\taide\\Desktop\\TD.S0001\\Params\\tia1.ini"));  
    }  
}