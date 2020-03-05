package org.khj.khjbasiscamerasdk.database;

import org.khj.khjbasiscamerasdk.greendao.DeviceEntityDao;

/**
 * Created by ShuRun on 2018/4/9.
 */
public class EntityManager {
    private static EntityManager entityManager;
    private DeviceEntityDao deviceEntityDao;

    private EntityManager() {
        if (deviceEntityDao == null) {
            deviceEntityDao = DaoManager.getInstance().getSession().getDeviceEntityDao();
        }
    }

    /**
     * 创建设备表实例
     *
     * @return
     */
    public DeviceEntityDao getDeviceEntityDao() {
        return deviceEntityDao;
    }


    /**
     * 创建单例
     *
     * @return
     */
    public static EntityManager getInstance() {
        if (entityManager == null) {
            synchronized (EntityManager.class) {
                if (entityManager == null) {
                    entityManager = new EntityManager();
                }
            }

        }
        return entityManager;
    }
}
