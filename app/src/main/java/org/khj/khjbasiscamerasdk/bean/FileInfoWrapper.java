package org.khj.khjbasiscamerasdk.bean;

import org.khj.khjbasiscamerasdk.utils.FileUtil;

/**
 * Created by ShuRun on 2018/5/9.
 */
public class FileInfoWrapper {
    public FileInfoWrapper(String filename, String downloadedName) {
        this.filename = filename;
        this.downloadedName = downloadedName;
        this.date= FileUtil.getPicDate(filename);
    }

    public String filename;//
    public String downloadedName;//下载后存储在手机中的文件路径
    public long date;
    public boolean hasDownload;
    public boolean isMP4=true;

    public FileInfoWrapper() {
    }

    public FileInfoWrapper(String filename, boolean hasDownload, boolean isMP4) {
        this.filename = filename;
        this.hasDownload = hasDownload;
        this.isMP4 = isMP4;
        this.date= FileUtil.getPicDate(filename);
    }
}
