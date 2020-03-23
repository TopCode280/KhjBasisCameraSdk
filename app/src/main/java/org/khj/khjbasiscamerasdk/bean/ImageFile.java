package org.khj.khjbasiscamerasdk.bean;

import androidx.annotation.NonNull;

import org.khj.khjbasiscamerasdk.utils.FileUtil;

import java.io.File;
import java.io.Serializable;

/**
 * Created by ShuRun on 2018/4/10.
 */
public class ImageFile implements Comparable,Serializable{
    public File file;
    public String path;
    public String fileName;
    public int position;
    public boolean isImage;//true代表图片，false代表mp4
    public String createTime;//"yyyy-MM-dd HH:mm:ss"
    public long date;//"yyyy-MM-dd日期值"
    public String deviceName;

    public ImageFile(File file) {
        this.file = file;
        isImage=true;
        fileName=file.getName();
        path=file.getAbsolutePath();
        date= FileUtil.getPicDate(file.getName());
        deviceName=fileName.substring(fileName.lastIndexOf("_")+1,fileName.lastIndexOf("."));

    }
    public ImageFile(File file, boolean is) {
        this.file = file;
        fileName=file.getName();
        isImage=is;
        path=file.getAbsolutePath();
        date= FileUtil.getPicDate(file.getName());
    }

    @Override
    public int compareTo(@NonNull Object o) {
        ImageFile imageFile = (ImageFile) o;
        return this.file.compareTo(imageFile.file);
    }
}