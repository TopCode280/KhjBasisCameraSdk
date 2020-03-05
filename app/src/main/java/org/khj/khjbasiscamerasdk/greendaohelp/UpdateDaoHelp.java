package org.khj.khjbasiscamerasdk.greendaohelp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import org.greenrobot.greendao.database.Database;
import org.khj.khjbasiscamerasdk.greendao.DaoMaster;
import org.khj.khjbasiscamerasdk.greendao.DeviceEntityDao;

public class UpdateDaoHelp extends DaoMaster.OpenHelper {
    public UpdateDaoHelp(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        //把需要管理的数据库表DAO作为最后一个参数传入到方法中
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, DeviceEntityDao.class);
    }
}

