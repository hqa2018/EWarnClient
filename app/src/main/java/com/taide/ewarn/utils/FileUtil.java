package com.taide.ewarn.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.storage.StorageManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * 文件工具类
 */
public class FileUtil {
    /**
     本地根目录：/storage/emulated/0/EWarn/
     SD卡根目录：/storage/sdcard/EWarn/Params/
     */
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static String getSDCardRootPath(){
        return "/storage/sdcard/EWarn";
    }

    public static String getLocalRootPath(){
        return "/storage/emulated/0/data";
    }


    /**
     * 获取手机内置/外置sd卡根路径
     * isExternalSdcard true返回外置sd卡 false返回内置sd卡
     * @param context
     * @param isExternalSdcard
     * @return
     */
    public static String getStoragePath(Context context, boolean isExternalSdcard) {
        String path = "";
        //使用getSystemService(String)检索一个StorageManager用于访问系统存储功能
        StorageManager mStorageManager = (StorageManager) context.getSystemService(Context.STORAGE_SERVICE);
        Class<?> storageVolumeClazz = null;
        try {
            storageVolumeClazz = Class.forName("android.os.storage.StorageVolume");
            Method getVolumeList = mStorageManager.getClass().getMethod("getVolumeList");
            Method getPath = storageVolumeClazz.getMethod("getPath");
            Method isRemovable = storageVolumeClazz.getMethod("isRemovable");
            Object result = getVolumeList.invoke(mStorageManager);

            for (int i = 0; i < Array.getLength(result); i++) {
                Object storageVolumeElement = Array.get(result, i);
                path = (String) getPath.invoke(storageVolumeElement);
                boolean removable = (Boolean) isRemovable.invoke(storageVolumeElement);
                if (isExternalSdcard == removable) {
                    return path;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return path;
    }

    /**
     * 判断目录文件是否存在
     * @param strFile
     * @return
     */
    public static boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                return false;
            }

        }
        catch (Exception e)
        {
            return false;
        }

        return true;
    }






    /**
     * 系统运行日志
     * @param msg
     * @return
     */
    public static String writeTolog(String msg) {
        String storagePath = "/storage/sdcard/EWarn/Log/MQTT";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
        String date = simpleDateFormat.format(new Date());

        String time = sdf.format(new Date());

        String fileName = File.separator + "log_" +date + ".log";
        msg = time + "  " + msg + "\r\n";

        try {
            File dir = new File(storagePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(storagePath + fileName, true);
            fos.write(msg.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storagePath + fileName;
    }


    public static String writeTocatalogtxt(String name, String msg) {
        String storagePath = "/storage/sdcard/EWarn/Catalog/";
        String fileName = "catalog.txt";
        String content = sdf.format(new Date()) + "  " + msg + "\r\n";

        try {

            File dir = new File(storagePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            FileOutputStream fos = new FileOutputStream(storagePath + fileName, true);
            fos.write(content.getBytes());
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return storagePath + fileName;
    }

    /*
    public static Date TimestampToDate(Integer time){
        long temp = (long)time*1000;
        Timestamp ts = new Timestamp(temp);
        Date date = new Date();
        try {
            date = ts;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
    */

    public static boolean createTxtFile(String destFileName, String content){
        File file = new File(destFileName);

        if (destFileName.endsWith(File.separator)) {
            return false;
        }
        if(!file.getParentFile().exists()) {
            if(!file.getParentFile().mkdirs()) {
                return false;
            }
        }
        //创建目标文件
        try {

            if(!file.exists())
                file.createNewFile();

            FileOutputStream out=new FileOutputStream(file,true); //如果追加方式用true
            out.write(content.getBytes("utf-8"));//注意需要转换对应的字符集
            out.close();

            return true;

        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }

    }

    /**
     * 写入txt文本
     * @param destFileName  文件名
     * @param content       文本内容
     * @param isAppend      是否追加的方式
     * @return
     */
    public static boolean writeTxtFile(String destFileName, String content, boolean isAppend){
        File file = new File(destFileName);

        if (destFileName.endsWith(File.separator)) {
            return false;
        }
        if(!file.getParentFile().exists()) {
            if(!file.getParentFile().mkdirs()) {
                Log.d("AddStationActivity", "writeTxtFile: mkdirs");
                return false;
            }
        }
        //创建目标文件
        try {
            if(!file.exists())
                file.createNewFile();

            FileOutputStream out=new FileOutputStream(file,isAppend); //如果追加方式用true
            out.write(content.getBytes("utf-8"));//注意需要转换对应的字符集
            out.close();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("AddStationActivity", "创建单个文件" + destFileName + "失败！" + e.getMessage());
            System.out.println("创建单个文件" + destFileName + "失败！" + e.getMessage());
            return false;
        }
    }


    /**
     * 根据文件路径读取文件内容
     * @param path
     * @return
     */
    public static String readTxtFile(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        String laststr = "";
        StringBuffer strb = new StringBuffer();
        if(file.exists()){
            try {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(path),"UTF-8"));
                // reader = new BufferedReader(new FileReader(file));
                String tempString = null;
                while ((tempString = reader.readLine()) != null) {
                    strb.append(tempString);
                    if("".equals(laststr)){
                        laststr = laststr + tempString;
                    }else {
                        laststr = laststr + "\r\n" +tempString;
                    }

                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e1) {
                    }
                }
            }
        }
        //return strb.toString();
        return laststr;
    }

    /**
     * 删除单个文件
     * @param   filePath    被删除文件的文件名
     * @return 文件删除成功返回true，否则返回false
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return delFile(file);
    }

    public static boolean delFile(File file) {
        if (!file.exists()) {
            return false;
        }

        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File f : files) {
                delFile(f);
            }
        }
        return file.delete();
    }

    /**
     * 删除文件夹以及目录下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirectory(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            if (files[i].isFile()) {
                //删除子文件
                flag = deleteFile(files[i].getAbsolutePath());
                if (!flag) break;
            } else {
                //删除子目录
                flag = deleteDirectory(files[i].getAbsolutePath());
                if (!flag) break;
            }
        }
        if (!flag) return false;
        //删除当前空目录
        return dirFile.delete();
    }


    /**
     * 删除文件夹下的文件
     * @param   filePath 被删除目录的文件路径
     * @return  目录删除成功返回true，否则返回false
     */
    public static boolean deleteDirListFiles(String filePath) {
        boolean flag = false;
        //如果filePath不以文件分隔符结尾，自动添加文件分隔符
        if (!filePath.endsWith(File.separator)) {
            filePath = filePath + File.separator;
        }
        File dirFile = new File(filePath);
        if (!dirFile.exists() || !dirFile.isDirectory()) {
            return false;
        }
        flag = true;
        File[] files = dirFile.listFiles();
        //遍历删除文件夹下的所有文件(包括子目录)
        for (int i = 0; i < files.length; i++) {
            deleteDirectory(files[i].getAbsolutePath());
        }
        if (!flag) return false;
        return flag;
    }

    /**
     *  根据路径删除指定的目录或文件，无论存在与否
     *@param filePath  要删除的目录或文件
     *@return 删除成功返回 true，否则返回 false。
     */
    public boolean deleteFolder(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        } else {
            if (file.isFile()) {
                // 为文件时调用删除文件方法
                return deleteFile(filePath);
            } else {
                // 为目录时调用删除目录方法
                return deleteDirectory(filePath);
            }
        }
    }

    /**
     * 列出目录文件按文件名排序
     * @param filePath
     * @return
     */
    public static File[] listFilesByName(String filePath) {
        File[] files = null;
        try {
            File file = new File(filePath);
            if(file.exists()){
                files = file.listFiles();
                List fileList = Arrays.asList(files);
                Collections.sort(fileList, new Comparator<File>() {
                    @Override
                    public int compare(File o1, File o2) {
                        if (o1.isDirectory() && o2.isFile())
                            return -1;
                        if (o1.isFile() && o2.isDirectory())
                            return 1;
                        return o1.getName().compareTo(o2.getName());
                    }
                });
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return files;
    }

    /**
     * 列出目录文件按时间排序
     * @param filePath
     */
    public static File[] listFilesByDate(String filePath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.lastModified() - f2.lastModified();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
            }

            public boolean equals(Object obj) {
                return true;
            }

        });
        for (int i = 0; i < files.length; i++) {
            System.out.println(files[i].getName());
            System.out.println(new Date(files[i].lastModified()));
        }
        return files;
    }

    /**
     * 列出目录文件按大小排序
     * @param filePath
     */
    public static File[] listFilesLength(String filePath) {
        File file = new File(filePath);
        File[] files = file.listFiles();
        List<File> fileList = Arrays.asList(files);
        Collections.sort(fileList, new Comparator<File>() {
            public int compare(File f1, File f2) {
                long diff = f1.length() - f2.length();
                if (diff > 0)
                    return 1;
                else if (diff == 0)
                    return 0;
                else
                    return -1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
            }

            public boolean equals(Object obj) {
                return true;
            }
        });
        for (File file1 : files) {
            if (file1.isDirectory()) continue;
            System.out.println(file1.getName() + ":" + file1.length());
        }
        return files;
    }

    public static void saveImgFile(String path, Bitmap mBitmap){
        File file = new File(path);         // 保存到sdcard根目录下，文件名为share_pic.png
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file,false);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 10, fos);
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }
        try {
            fos.close();
        }catch (IOException e){

        }
    }

    /**
     * 保存bitmap到本地
     *
     * @param savePath
     * @param mBitmap
     * @return
     */
    public static String saveBitmap(String savePath, Bitmap mBitmap) {
        mBitmap = compressImage(mBitmap);
        File filePic;
        try {
            filePic = new File(savePath);
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            //mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) { // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        return filePic.getAbsolutePath();
    }

    /**
     * 压缩图片质量
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 90;
        while (baos.toByteArray().length / 1024 > 800) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset(); // 重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
            // 这里压缩options%，把压缩后的数据存放到baos中
            if (options > 10) {//设置最小值，防止低于0时出异常
                options -= 10;// 每次都减少10
            }
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//
        // 把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
        // 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 获取安卓文件存储路径
     * 函数返回路径/storage/emulated/0/Android/data/包名/files
     * 用来存储一些长时间保留的数据,应用卸载会被删除
     * @param context
     * @return
     */
    public static String getFilesPath(Context context){
         String filePath ;
         if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
             || !Environment.isExternalStorageRemovable()) {
             //外部存储可用
             filePath = context.getExternalFilesDir(null).getPath();
         }else {
             //外部存储不可用
             filePath = context.getFilesDir().getPath() ;
         }
         return filePath ;
    }

    /**
     * 获取安卓缓存文件存储路径
     * 函数返回路径/storage/emulated/0/Android/data/包名/cache
     * 用来存储一些临时缓存数据
     * @param context
     * @return
     */
    public String getCachePath( Context context ){
        String cachePath ;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
            || !Environment.isExternalStorageRemovable()) {
            //外部存储可用
            cachePath = context.getExternalCacheDir().getPath() ;
        }else {
              //外部存储不可用
            cachePath = context.getCacheDir().getPath() ;
        }
        return cachePath ;
      }





}
