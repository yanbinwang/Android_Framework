package com.dataqin.common.dao;

import java.util.Map;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.identityscope.IdentityScopeType;
import org.greenrobot.greendao.internal.DaoConfig;

import com.dataqin.common.model.MobileFileDB;

import com.dataqin.common.dao.MobileFileDBDao;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.

/**
 * {@inheritDoc}
 * 
 * @see org.greenrobot.greendao.AbstractDaoSession
 */
public class DaoSession extends AbstractDaoSession {

    private final DaoConfig mobileFileDBDaoConfig;

    private final MobileFileDBDao mobileFileDBDao;

    public DaoSession(Database db, IdentityScopeType type, Map<Class<? extends AbstractDao<?, ?>>, DaoConfig>
            daoConfigMap) {
        super(db);

        mobileFileDBDaoConfig = daoConfigMap.get(MobileFileDBDao.class).clone();
        mobileFileDBDaoConfig.initIdentityScope(type);

        mobileFileDBDao = new MobileFileDBDao(mobileFileDBDaoConfig, this);

        registerDao(MobileFileDB.class, mobileFileDBDao);
    }
    
    public void clear() {
        mobileFileDBDaoConfig.clearIdentityScope();
    }

    public MobileFileDBDao getMobileFileDBDao() {
        return mobileFileDBDao;
    }

}
