package com.hzkc.parent.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.hzkc.parent.greendao.entity.LocationDataBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LOCATION_DATA_BEAN".
*/
public class LocationDataBeanDao extends AbstractDao<LocationDataBean, Long> {

    public static final String TABLENAME = "LOCATION_DATA_BEAN";

    /**
     * Properties of entity LocationDataBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property Childuuid = new Property(1, String.class, "childuuid", false, "CHILDUUID");
        public final static Property Longitude = new Property(2, String.class, "longitude", false, "LONGITUDE");
        public final static Property Latitude = new Property(3, String.class, "latitude", false, "LATITUDE");
        public final static Property Lasttime = new Property(4, String.class, "lasttime", false, "LASTTIME");
    };


    public LocationDataBeanDao(DaoConfig config) {
        super(config);
    }
    
    public LocationDataBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LOCATION_DATA_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: _id
                "\"CHILDUUID\" TEXT," + // 1: childuuid
                "\"LONGITUDE\" TEXT," + // 2: longitude
                "\"LATITUDE\" TEXT," + // 3: latitude
                "\"LASTTIME\" TEXT);"); // 4: lasttime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LOCATION_DATA_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, LocationDataBean entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String childuuid = entity.getChilduuid();
        if (childuuid != null) {
            stmt.bindString(2, childuuid);
        }
 
        String longitude = entity.getLongitude();
        if (longitude != null) {
            stmt.bindString(3, longitude);
        }
 
        String latitude = entity.getLatitude();
        if (latitude != null) {
            stmt.bindString(4, latitude);
        }
 
        String lasttime = entity.getLasttime();
        if (lasttime != null) {
            stmt.bindString(5, lasttime);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, LocationDataBean entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String childuuid = entity.getChilduuid();
        if (childuuid != null) {
            stmt.bindString(2, childuuid);
        }
 
        String longitude = entity.getLongitude();
        if (longitude != null) {
            stmt.bindString(3, longitude);
        }
 
        String latitude = entity.getLatitude();
        if (latitude != null) {
            stmt.bindString(4, latitude);
        }
 
        String lasttime = entity.getLasttime();
        if (lasttime != null) {
            stmt.bindString(5, lasttime);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public LocationDataBean readEntity(Cursor cursor, int offset) {
        LocationDataBean entity = new LocationDataBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // childuuid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // longitude
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // latitude
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4) // lasttime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, LocationDataBean entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setChilduuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setLongitude(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setLatitude(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setLasttime(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(LocationDataBean entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(LocationDataBean entity) {
        if(entity != null) {
            return entity.get_id();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
