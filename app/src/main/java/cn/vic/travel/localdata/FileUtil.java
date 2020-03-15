package cn.vic.travel.localdata;

import android.graphics.Bitmap;
import android.os.Environment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cn.vic.travel.Utils;

import static cn.vic.travel.Utils.debugToast;

/**
 * 文件操作工具类
 * Snake 创建于 2018/6/2.
 */

public class FileUtil {

    /**
     * 检查外部存储是否可读写
     * @return
     */
    public static boolean isExternalStorageWritable(){
        String state =Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)){
            return true;
        }
        return false;
    }

    /**
     * 检查外部存储是否可读取
     * @return
     */
    public static boolean isExternalStorageReadable(){
        String state =Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
            return true;
        }
        return false;
    }

    /**
     * 将图片储存至指定路径
     * @param bitmap 图片
     * @param dir 路径
     */
    public void saveBitmap(Bitmap bitmap,String dir){
        createFolder(dir);
        File file = new File(dir,"PHOTO_"+System.currentTimeMillis()+".png");// 在SDcard的目录下创建图片文,以当前时间为其命名
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)){
                //out.println("_________保存到____sd______指定目录文件夹下____________________");
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        debugToast("已经保存至"+dir);
    }

    /**
     * 获取指定路径下的文件
     * @param dir 路径
     * @return 文件路径和文件名列表
     */
    public List<String> findFile(String dir){
        File dirFile  = new File(dir);      //目录转化成文件夹
        if (!dirFile .exists()) {              //如果不存在
            debugToast("目录不存在");
        }
        List<String> list= new ArrayList<>();
        File[] files = new File(dir).listFiles();
        if (files != null) {
            for (File file : files) {
                list.add(file.getPath());
            }
        }
        else{
            debugToast("files == null");
        }
        return list;
    }

    /**
     * 创建文件夹
     * @param dir 路径
     */
    public void createFolder(String dir){
        File dirFile  = new File(dir);  //目录转化成文件夹
        if (!dirFile .exists()) {              //如果不存在，那就建立这个文件夹
            dirFile .mkdirs();
        }
    }

    /**
     * 字符串保存到手机内存设备中
     * @param content 字符串
     */
    public void saveFile(String content, String dir,String fileName) {
        String strWithNewline=content+"\r\n";
        // 创建String对象保存文件名路径
        try {
            // 创建指定路径的文件
            File file = new File(dir, fileName+".txt");
            File dirFile  = new File(dir);      //目录转化成文件夹
            //如果不存在
            if (!dirFile .exists()) {
                createFolder(dir);  //创建新文件夹
            }
            // 如果文件不存在
            if (!file.exists()) {
                // 创建新的空文件
                file.createNewFile();
            }
            // 获取文件的输出流对象
            FileOutputStream outStream = new FileOutputStream(file,true);
            // 获取字符串对象的byte数组并写入文件流
            outStream.write(strWithNewline.getBytes());
            // 最后关闭文件输出流
            outStream.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除指定文件
     * @param filePathAndName 文件路径和文件名
     */
    public void deleteFile(String filePathAndName) {
        try {
            // 找到文件所在的路径并删除该文件
            File file = new File(filePathAndName);
            file.delete();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 获取文件扩展名
     * @param filename 文件名
     * @return 扩展名
     */
    public String getExtensionName(String filename) {
        if ((filename != null) && (filename.length() > 0)) {
            int dot = filename.lastIndexOf('.');
            if ((dot >-1) && (dot < (filename.length() - 1))) {
                return filename.substring(dot + 1);
            }
        }
        return filename;
    }

    /**
     * 读取文件里面的内容
     * @return 内容
     */
    public static String getFile(String fileName) {
        try {
            // 创建文件
            File file = new File(Environment.getExternalStorageDirectory(),fileName);
            // 创建FileInputStream对象
            FileInputStream fis = new FileInputStream(file);
            // 创建字节数组 每次缓冲1M
            byte[] b = new byte[1024];
            int len = 0;// 一次读取1024字节大小，没有数据后返回-1.
            // 创建ByteArrayOutputStream对象
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // 一次读取1024个字节，然后往字符输出流中写读取的字节数
            while ((len = fis.read(b)) != -1) {
                baos.write(b, 0, len);
            }
            // 将读取的字节总数生成字节数组
            byte[] data = baos.toByteArray();
            // 关闭字节输出流
            baos.close();
            // 关闭文件输入流
            fis.close();
            // 返回字符串对象
            return new String(data);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
