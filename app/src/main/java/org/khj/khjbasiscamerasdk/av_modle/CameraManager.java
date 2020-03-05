package org.khj.khjbasiscamerasdk.av_modle;

import android.annotation.SuppressLint;

import com.vise.log.ViseLog;

import org.khj.khjbasiscamerasdk.App;
import org.khj.khjbasiscamerasdk.database.EntityManager;
import org.khj.khjbasiscamerasdk.database.entity.DeviceEntity;
import org.khj.khjbasiscamerasdk.greendao.DeviceEntityDao;
import org.khj.khjbasiscamerasdk.utils.SharedPreferencesUtil;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by ShuRun on 2018/4/24.
 * 管理摄像头连接
 */
public class CameraManager {
    private static CameraManager instance;
    private ConcurrentHashMap<String, CameraWrapper> hashMap;
    private ConcurrentHashMap<String, CameraWrapper> tempApMap;//存储临时热点设备
    private long lastConnectTime;//上一次点击重连的时间
    private long lastDisconnectTime;//上一次点击重连的时间

    private CameraManager() {
        hashMap = new ConcurrentHashMap();
        tempApMap = new ConcurrentHashMap();
    }

    public static CameraManager getInstance() {
        if (instance == null) {
            synchronized (CameraManager.class) {
                if (instance == null) {
                    instance = new CameraManager();
                }
            }
        }
        return instance;
    }

    public void addCamera(String uid, CameraWrapper camera) {
        ViseLog.i("连接camera设备" + uid);
        hashMap.put(uid, camera);
    }

    /**
     * Uid是否已在用户的设备表中
     *
     * @param uid
     * @return
     */
    public boolean isBoundCamera(String uid) {
        return hashMap.containsKey(uid);
    }

    public void removeCamera(String uid) {
        CameraWrapper cameraWrapper = hashMap.get(uid);
        if (cameraWrapper != null) {
            cameraWrapper.release();
            hashMap.remove(uid);
        }

    }

    public void removeCamera(CameraWrapper cameraWrapper) {
        hashMap.remove(cameraWrapper.getUid());
        cameraWrapper.release();
//        KLog.e("删除当前"+cameraWrapper.getUid());

    }


    /**
     * 连接所有设备
     *
     * @param entityList
     */
    public void initCameras(List<DeviceEntity> entityList) {
        hashMap.clear();
        for (int i = 0; i < entityList.size(); i++) {
            DeviceEntity deviceEntity = entityList.get(i);
            String uid = deviceEntity.getDeviceUid();
            CameraWrapper cameraWrapper = new CameraWrapper(deviceEntity);
            addCamera(uid, cameraWrapper);
        }
    }

    /**
     * 从服务器下拉设备表，更新数据
     *
     * @param entityList
     */
    public void updateCameras(List<DeviceEntity> entityList) {
        ViseLog.i("updateCameras:" + entityList.size());
        Iterator<String> iterator = hashMap.keySet().iterator();
        while (iterator.hasNext()) {
            String uid = iterator.next();
            boolean isContain = false;
            for (DeviceEntity device : entityList) {
                if (device.getDeviceUid().equals(uid)) {
                    isContain = true;
                    break;
                }
            }
            if (!isContain) {
                removeCamera(uid);
            }
        }

        for (int i = 0; i < entityList.size(); i++) {
            String deviceUid = entityList.get(i).getDeviceUid();
            if (hashMap.containsKey(deviceUid)) {
                CameraWrapper cameraWrapper = hashMap.get(deviceUid);
                if (cameraWrapper != null) {
                    cameraWrapper.setDeviceEntity(entityList.get(i));
                }
            } else {
                CameraWrapper cameraWrapper = new CameraWrapper(entityList.get(i));
                addCamera(deviceUid, cameraWrapper);
            }
        }
    }


    public List<CameraWrapper> getCameras() {
        Collection<CameraWrapper> values = hashMap.values();
        if (values == null) {
            return new ArrayList<>();
        }
        ArrayList<CameraWrapper> cameraWrappers = new ArrayList<>(hashMap.values());
        Collections.sort(cameraWrappers, (o1, o2) -> o1.getDeviceEntity().getOrder() - o2.getDeviceEntity().getOrder());
        return cameraWrappers;
    }

    /**
     * 返回主设备集合
     *
     * @return
     */
    public List<CameraWrapper> getAdminCameras() {
        Collection<CameraWrapper> values = hashMap.values();
        ArrayList<CameraWrapper> adminList = new ArrayList<>();
        for (CameraWrapper value : values) {
            if (value.getDeviceEntity().getIsAdmin()) {
                adminList.add(value);
            }
        }
        return adminList;
    }

    public CameraWrapper getCameraWrapper(String uid) {
        for (String s : hashMap.keySet()) {
            ViseLog.e(s);
        }
        return hashMap.get(uid);
    }

    @SuppressLint("CheckResult")
    public void updateDatabase() {
        if (hashMap != null && hashMap.size() > 0) {
            Observable.just(hashMap.values())
                    .subscribeOn(Schedulers.io())
                    .subscribe(cameraWrappers -> {
                        for (CameraWrapper cameraWrapper : cameraWrappers) {
                            EntityManager.getInstance().getDeviceEntityDao().update(cameraWrapper.getDeviceEntity());
                        }
                    });
        }

    }

    /**
     * 当网络切换时，需要全部重新连接，网络切换造成无网络的广播会短时间内发送多次，过滤重复广播
     * 延迟执行重连
     */
    @SuppressLint("CheckResult")
    public void reconnectAll() {
        long current = System.currentTimeMillis();
        if (current - lastConnectTime < 1000) {
            lastConnectTime = current;
            return;
        }
        ViseLog.i("reconnectAll");
        lastConnectTime = current;
        for (CameraWrapper cameraWrapper : hashMap.values()) {
            cameraWrapper.reconnectTimes.set(3);
            int status = cameraWrapper.getStatus();
            if (status != 0 && status != 1)
                cameraWrapper.reconnect();
        }
    }

    /**
     * 网络切换后断开所有连接，网络切换造成无网络的广播会短时间内发送多次，过滤重复广播
     */
    @SuppressLint("CheckResult")
    public void disconnectAll() {
        long current = System.currentTimeMillis();
        if (current - lastDisconnectTime < 5000) {
            lastDisconnectTime = current;
            return;
        }
        lastDisconnectTime = current;
        for (CameraWrapper cameraWrapper : hashMap.values()) {
            cameraWrapper.disconnect();
        }
    }

    /**
     * 网络切换后断开所有连接，网络切换造成无网络的广播会短时间内发送多次，过滤重复广播
     */
    @SuppressLint("CheckResult")
    public void releaseAll() {
        long current = System.currentTimeMillis();
        if (current - lastDisconnectTime < 2000) {
            lastDisconnectTime = current;
            return;
        }
        lastDisconnectTime = current;
        ViseLog.e("disconnectAll************");
        Set<String> keySet = hashMap.keySet();
        Observable.fromIterable(keySet)
                .subscribeOn(Schedulers.io())
                .doFinally(() -> {
                    hashMap.clear();
                    ViseLog.i("释放所有camera");
                }).subscribe(key -> {
                    CameraWrapper remove = hashMap.remove(key);
                    ViseLog.d("删除" + key);
                    if (remove != null) {
                        remove.release();
                    }
                }
        );
    }

    //AP热点设备处理
    public void addApCamera(String uid) {
        tempApMap.clear();
        CameraWrapper cameraWrapper;
        DeviceEntity deviceEntity;
        CameraWrapper localCamera = getCameraWrapper(uid);
        if (localCamera != null) {
            cameraWrapper = localCamera;
        } else {
            deviceEntity = new DeviceEntity();
            deviceEntity.setDeviceUid(uid);
            String pwd = SharedPreferencesUtil.getString(App.context, uid, "888888");
            ViseLog.e("pwd" + pwd);
            deviceEntity.setDevicePwd(pwd);
            deviceEntity.setDeviceName("AP_" + uid);
            deviceEntity.setIsAdmin(true);
            deviceEntity.setDeviceAccount("admin");
            cameraWrapper = new CameraWrapper(deviceEntity, true);
            cameraWrapper.setApMode(true);
        }
        tempApMap.put(uid, cameraWrapper);
    }

    //列表热点设备处理
    public void deviceListAddApCamera(String uid) {
        try {
            tempApMap.clear();
            CameraWrapper cameraWrapper;
            DeviceEntity deviceEntity;
            deviceEntity = new DeviceEntity();
            deviceEntity.setUserId(Long.parseLong(App.userAccount));
            deviceEntity.setDeviceUid(uid);
            String pwd = SharedPreferencesUtil.getString(App.context, uid, "888888");
            deviceEntity.setDevicePwd(pwd);
            deviceEntity.setDeviceName("AP_" + uid);
            deviceEntity.setIsAdmin(true);
            deviceEntity.setDeviceAccount("admin");
            saveDeviceEntity(deviceEntity);
            cameraWrapper = new CameraWrapper(deviceEntity, true);
            cameraWrapper.setApMode(true);
            tempApMap.put(uid, cameraWrapper);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public CameraWrapper getApCamera(String uid) {
        CameraWrapper cameraWrapper = tempApMap.get(uid);
        return cameraWrapper;
    }

    private void saveDeviceEntity(DeviceEntity deviceEntity) {
        DeviceEntityDao deviceEntityDao = EntityManager.getInstance().getDeviceEntityDao();
        List<DeviceEntity> deviceEntityList = deviceEntityDao.queryBuilder().where(
                DeviceEntityDao.Properties.UserId.eq(App.userAccount)
                , (DeviceEntityDao.Properties.DeviceUid.eq(deviceEntity.getDeviceUid()))).list();
        if (deviceEntityList != null) {
            if (deviceEntityList.size() > 0) {  // 删除之前可能存在的数据
                for (DeviceEntity device : deviceEntityList) {
                    deviceEntityDao.delete(device);
                }
            }
        }
        deviceEntity.setUserId(Long.parseLong(App.userAccount));
        deviceEntityDao.save(deviceEntity);
    }
}
