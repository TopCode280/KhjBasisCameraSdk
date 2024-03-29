package org.khj.khjbasiscamerasdk.greendao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import org.khj.khjbasiscamerasdk.database.entity.DeviceEntity;

import org.khj.khjbasiscamerasdk.greendao.DeviceEntityDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig deviceEntityDaoConfig;

    private final DeviceEntityDao deviceEntityDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        deviceEntityDaoConfig = daoConfigMap.get(DeviceEntityDao.class).clone();
        deviceEntityDaoConfig.initIdentityScope(type);

        deviceEntityDao = new DeviceEntityDao(deviceEntityDaoConfig, this);

        registerDao(DeviceEntity.class, deviceEntityDao);
    }
    
    public void clear() {
        deviceEntityDaoConfig.clearIdentityScope();
    }

    public DeviceEntityDao getDeviceEntityDao() {
        return deviceEntityDao;
    }

}
