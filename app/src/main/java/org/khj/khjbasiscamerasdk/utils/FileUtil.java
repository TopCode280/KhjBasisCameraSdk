package org.khj.khjbasiscamerasdk.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.view.TextureView;

import com.vise.log.ViseLog;

import org.greenrobot.eventbus.EventBus;
import org.khj.khjbasiscamerasdk.bean.ImageFile;
import org.khj.khjbasiscamerasdk.database.entity.DeviceEntity;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


/**
 * Created by ShuRun on 2018/4/9.
 */
public class FileUtil {
    private static FileUtil fileUtil;

    private FileUtil(Context context) {
    }

    public static FileUtil getInstance(Context context) {
        if (fileUtil == null) {
            synchronized (FileUtil.class) {
                if (fileUtil == null) {
                    fileUtil = new FileUtil(context);
                    return fileUtil;
                }
                return fileUtil;
            }
        }
        return fileUtil;

    }


    /**
     * 将文件按名字降序排列
     */
    class FileComparator implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            return file2.getName().compareTo(file1.getName());
        }
    }

    /**
     * 将文件按时间降序排列
     */
    class FileComparator2 implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            if (file1.lastModified() < file2.lastModified()) {
                return 1;// 最后修改的文件在前
            } else {
                return -1;
            }
        }
    }

    class FileComparator3 implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            if (file1.length() < file2.length()) {
                return -1;// 小文件在前
            } else {
                return 1;
            }
        }
    }

    /**
     * 根据图片名字排序yyyy_MM_dd_HH_mm_ss
     */
    class FileComparator4 implements Comparator<File> {

        @Override
        public int compare(File file1, File file2) {
            if (TimeUtil.date2Long(file1.getName().substring(0, 19), "yyyy_MM_dd_HH_mm_ss") < TimeUtil.date2Long(file2.getName().substring(0, 19), "yyyy_MM_dd_HH_mm_ss")) {
                return 1;// 新文件在前
            } else {
                return -1;
            }
        }
    }

    /**
     *
     */
    public enum MediaType {
        JPG,
        MP4,
        ALL //包括图片和视频
    }

    /**
     * @param dir          文件夹路径
     * @param date         选择日期，0代表所有日期，格式为毫秒utc
     * @param deviceInfoId 设备的deviceInfoId，一台设备的唯一标识，""代表查询所有设备
     * @param mediaType    媒体类型
     * @return
     */
    public List<Object> getImageListByDate(String dir, long date, String deviceInfoId, MediaType mediaType) {
        ViseLog.e(dir + "*" + date + "*" + deviceInfoId);
        List<Object> data = new ArrayList();

        Long lastTime = new Long(0);
        File dirs = new File(dir);
        if (!dirs.exists()) {
            ViseLog.e("文件夹不存在");
            return null;
        }
        File[] files = dirs.listFiles(new MediaFileFilter(date, deviceInfoId, mediaType));
        if (files == null) {
            ViseLog.e("文件夹找不到匹配的图片和mp4");
            return null;
        }
        if (files.length == 0) {
            ViseLog.d("文件夹找不到匹配的图片和mp4为0");
            return null;
        }
        List<File> fileList = Arrays.asList(files);
        ViseLog.d("文件数量：" + fileList.size());
        Collections.sort(fileList, new FileComparator4());
        Iterator<File> fileIterator = fileList.iterator();
        while (fileIterator.hasNext()) {
            File file = fileIterator.next();
            String name = file.getName();
            Long picDate = getPicDate(name);
//            KLog.w(picDate);
            if (picDate == null) {
                continue;
            }
            if (!picDate.equals(lastTime)) {
                //如果当前文件的日期与上一个不同，则添加一条日期数据
                lastTime = picDate;
                data.add(name.substring(0, 10));

            }
            ImageFile imageFile = new ImageFile(file);
            if (name.endsWith(".mp4") || name.endsWith(".mov")) {
                imageFile.isImage = false;
            }
            data.add(imageFile);
        }
        return data;
    }

    public static Long getPicDate(String name) {
        String substring = name.substring(0, 10);
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy_MM_dd");
        try {
            Long time = inputFormat.parse(substring).getTime();
            return time;
        } catch (ParseException e) {
            ViseLog.e("时间转换出错啦"+name);
            return null;
        }
    }

    /**
     * 过滤图片文件
     */
    public class MediaFileFilter implements FileFilter {
        public MediaFileFilter(long time, String deviceInfoId, MediaType mediaType) {
            this.time = time;
            this.deviceInfoId = deviceInfoId;
            this.mediaType = mediaType;
        }

        private long time;
        private String deviceInfoId;
        private MediaType mediaType;

        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            boolean ifSatisfy = false;
            switch (mediaType) {
                case ALL:
                    ifSatisfy = name.endsWith(".jpg") || name.endsWith(".JPEG") || name.endsWith(".mp4") || name.endsWith(".mov");
                    break;
                case JPG:
                    ifSatisfy = name.endsWith(".jpg") || name.endsWith(".JPEG");
                    break;
                case MP4:
                    ifSatisfy = name.endsWith(".mp4") || name.endsWith(".mov");
                    break;
            }
            if (ifSatisfy) {

                if (time == 0) {
                    if (deviceInfoId.equals("")) {
//                        KLog.e("找到一个");
//                        KLog.d(name);
                        return true;
                    } else {
                        if (name.contains(deviceInfoId)) {
                            ViseLog.d(name);
                            return true;
                        } else {
                            return false;
                        }
                    }

                } else {
                    if (getPicDate(name) == time) {
                        ViseLog.e(getPicDate(name));
                        if (deviceInfoId.equals("")) {
                            ViseLog.d(name);
                            return true;
                        } else {
                            ViseLog.d(name);
                            if (name.contains(deviceInfoId)) {

                                return true;
                            } else {
                                return false;
                            }
                        }

                    } else {
                        return false;
                    }

                }

            }
            return false;
        }
    }
    public static boolean isExist(String folder,String fileName){
        File file=new File(folder);
        if (!file.exists()){
            return false;
        }
        String[] list = file.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
//                KLog.d(name);
                if (name.equals(fileName)) {
//                    KLog.e(name+"*"+fileName);
                    return true;
                }
                return false;
            }
        });
        if (list!=null&&list.length>0){
            return true;
        }
        return false;
    }

    /**
     * 查询从设备sd卡上下载的视频
     *
     * @param date 选择日期
     * @param dir  文件夹路径
     * @return
     */

    public List<File> getFilesListByDate(long date, String dir, String deviceInfoId) {
        ViseLog.e("date" + date + dir);
        List<File> fileArrayList;
        File file = new File(dir);
        if (!file.exists()) {
            return null;
        } else {
            File[] files = new File[0];
            try {
                files = file.listFiles(new TimeFilter(date, deviceInfoId));
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (files != null) {
                fileArrayList = Arrays.asList(files);
                return fileArrayList;
            }

        }
        return null;
    }

    /**
     * 删除当前用户下该设备的所有文件
     *
     * @param dir          文件夹路径
     * @param deviceInfoId 设备deviceInfoId
     */
    public void deleteAllMediaFiles(String dir, String deviceInfoId) {
        if (CommonUtil.isNull(dir) || CommonUtil.isNull(deviceInfoId)) {
            return;
        }
        File file = new File(dir);
        if (file == null) {
            return;
        }
        if (!file.exists()) {
            return;
        } else {
            File[] files = file.listFiles(new DeviceInfoIdFilter(deviceInfoId));
            if (files==null||files.length==0){
                return;
            }
            for (File file1 : files) {
                file1.delete();
            }

        }
    }

    public class TimeFilter implements FileFilter {
        public TimeFilter(long date, String deviceInfoId) {
            this.date = date;
            this.deviceInfoId = deviceInfoId;
        }

        private long date;
        private String deviceInfoId;

        @Override
        public boolean accept(File pathname) {
            String name = pathname.getName();
            if (name.endsWith(".mp4")) {

//                KLog.w("name"+name);
                String substring = name.substring(0, name.indexOf("."));
                if (CommonUtil.isNull(substring)) {
                    return false;
                }
                if (date == 0) {
                    if (substring.endsWith(deviceInfoId)) {
                        return true;
                    }


                } else {
                    if (getPicDate(name) == date) {
                        if (substring.endsWith(deviceInfoId)) {
                            return true;
                        }
                        return true;
                    } else {
                        return false;
                    }

                }

            }
            return false;
        }
    }

    public class DeviceInfoIdFilter implements FileFilter {
        public DeviceInfoIdFilter(String deviceInfoId) {
            this.deviceInfoId = deviceInfoId;
        }

        private String deviceInfoId;

        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return false;
            }
            String name = pathname.getName();
//            KLog.i(name);
            if (name.length() < 20 || !name.endsWith(".jpg") || !name.endsWith(".mov")) {
                return false;
            }
            String substring = name.substring(0, name.indexOf("."));

            if (substring.endsWith(deviceInfoId)) {
                return true;

            }
            return false;
        }
    }

    public static File saveBitmap(Bitmap mBitmap, String path) {
        String savePath;
        File filePic;
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {

        } else {

            return null;
        }
        try {
            filePic = new File(path, "temp");
            if (!filePic.exists()) {
                filePic.getParentFile().mkdirs();
                filePic.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(filePic);
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
        ViseLog.i(filePic.getAbsolutePath());
        return filePic;
    }

    private static boolean checkInstallation(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    /**
     * 复制文件
     *
     * @param source 输入文件
     * @param target 输出文件
     */
    public static void copy(File source, File target) {
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream = null;
        try {
            fileInputStream = new FileInputStream(source);
            fileOutputStream = new FileOutputStream(target);
            byte[] buffer = new byte[1024];
            while (fileInputStream.read(buffer) > 0) {
                fileOutputStream.write(buffer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 复制文件目录
     *
     * @param srcDir  要复制的源目录 eg:/mnt/sdcard/DB
     * @param destDir 复制到的目标目录 eg:/mnt/sdcard/db/
     * @return
     */
    public static boolean copyDir(String srcDir, String destDir) {
        File sourceDir = new File(srcDir);
        //判断文件目录是否存在
        if (!sourceDir.exists()) {
            return false;
        }
        //判断是否是目录
        if (sourceDir.isDirectory()) {
            File[] fileList = sourceDir.listFiles();
            File targetDir = new File(destDir);
            //创建目标目录
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            //遍历要复制该目录下的全部文件
            for (int i = 0; i < fileList.length; i++) {
                if (fileList[i].isDirectory()) {//如果如果是子目录进行递归
                    copyDir(fileList[i].getPath() + "/",
                            destDir + fileList[i].getName() + "/");
                } else {//如果是文件则进行文件拷贝
                    copyFile(fileList[i].getPath(), destDir + fileList[i].getName());
                }
            }
            return true;
        } else {
            copyFileToDir(srcDir, destDir);
            return true;
        }
    }


    /**
     * 复制文件（非目录）
     *
     * @param srcFile  要复制的源文件
     * @param destFile 复制到的目标文件
     * @return
     */
    private static boolean copyFile(String srcFile, String destFile) {
        try {
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * 把文件拷贝到某一目录下
     *
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean copyFileToDir(String srcFile, String destDir) {
        File fileDir = new File(destDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String destFile = destDir + "/" + new File(srcFile).getName();
        try {
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    /**
     * 把文件拷贝到某一目录下
     *
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean copyFileToDir(String srcFile, String destDir,String destNmae) {
        File fileDir = new File(destDir);
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }
        String destFile = destDir + "/" +destNmae;
        try {
            InputStream streamFrom = new FileInputStream(srcFile);
            OutputStream streamTo = new FileOutputStream(destFile);
            byte buffer[] = new byte[1024];
            int len;
            while ((len = streamFrom.read(buffer)) > 0) {
                streamTo.write(buffer, 0, len);
            }
            streamFrom.close();
            streamTo.close();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }


    /**
     * 移动文件目录到某一路径下
     *
     * @param srcFile
     * @param destDir
     * @return
     */
    public static boolean moveFile(String srcFile, String destDir) {
        //复制后删除原目录
        if (copyDir(srcFile, destDir)) {
            deleteFile(new File(srcFile));
            return true;
        }
        return false;
    }

    /**
     * 删除文件（包括目录）
     *
     * @param delFile
     */
    public static void deleteFile(File delFile) {
        //如果是目录递归删除
        if (delFile.isDirectory()) {
            File[] files = delFile.listFiles();
            for (File file : files) {
                deleteFile(file);
            }
        } else {
            delFile.delete();
        }
        //如果不执行下面这句，目录下所有文件都删除了，但是还剩下子目录空文件夹
        delFile.delete();
    }
    /**
     * 重命名文件
     *
     * @param oldPath 原来的文件地址
     * @param newPath 新的文件地址
     */
    public static void renameFile(String oldPath, String newPath) {
        File oleFile = new File(oldPath);
        File newFile = new File(newPath);
        //执行重命名
        oleFile.renameTo(newFile);
    }


    /**
     * 复制单个文件
     *
     * @param outPath String 输出文件路径 如：data/user/0/com.test/files
     * @param fileName String 要复制的文件名 如：abc.txt
     * @return <code>true</code> if and only if the file was copied; <code>false</code> otherwise
     */
    public static boolean copyAssetsSingleFile(Context context,String outPath, String fileName) {
        File file = new File(outPath);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                Log.e("--Method--", "copyAssetsSingleFile: cannot create directory.");
                return false;
            }
        }
        try {
            InputStream inputStream = context.getAssets().open(fileName);
            File outFile = new File(file, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(outFile);
            // Transfer bytes from inputStream to fileOutputStream
            byte[] buffer = new byte[1024];
            int byteRead;
            while (-1 != (byteRead = inputStream.read(buffer))) {
                fileOutputStream.write(buffer, 0, byteRead);
            }
            inputStream.close();
            fileOutputStream.flush();
            fileOutputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }





}
