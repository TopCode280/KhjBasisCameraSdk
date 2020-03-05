package org.khj.khjbasiscamerasdk.database;

import org.khj.khjbasiscamerasdk.App;
import org.khj.khjbasiscamerasdk.greendao.DaoMaster;
import org.khj.khjbasiscamerasdk.greendao.DaoSession;
import org.khj.khjbasiscamerasdk.greendaohelp.UpdateDaoHelp;

/**
 * Created by ShuRun on 2018/4/9.
 */
public class DaoManager {
    private static DaoManager mInstance;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;


    private DaoManager() {
        UpdateDaoHelp devOpenHelper = new UpdateDaoHelp(App.context, "my-db", null);
        mDaoMaster = new DaoMaster(devOpenHelper.getWritableDatabase());
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoMaster getMaster() {
        return mDaoMaster;
    }

    public DaoSession getSession() {
        return mDaoSession;
    }

    public static DaoManager getInstance() {
        if (mInstance == null) {
            mInstance = new DaoManager();
        }
        return mInstance;
    }
}
