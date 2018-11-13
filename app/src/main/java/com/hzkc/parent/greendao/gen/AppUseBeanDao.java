package com.hzkc.parent.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.hzkc.parent.greendao.entity.AppUseBean;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "APP_USE_BEAN".
*/
public class AppUseBeanDao extends AbstractDao<AppUseBean, Long> {

    public static final String TABLENAME = "APP_USE_BEAN";

    /**
     * Properties of entity AppUseBean.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property _id = new Property(0, Long.class, "_id", true, "_id");
        public final static Property A = new Property(1, String.class, "a", false, "A");
        public final static Property B = new Property(2, String.class, "b", false, "B");
        public final static Property C = new Property(3, String.class, "c", false, "C");
        public final static Property D = new Property(4, String.class, "d", false, "D");
        public final static Property Childuuid = new Property(5, String.class, "childuuid", false, "CHILDUUID");
    };


    public AppUseBeanDao(DaoConfig config) {
        super(config);
    }
    
    public AppUseBeanDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"APP_USE_BEAN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY ," + // 0: _id
                "\"A\" TEXT," + // 1: a
                "\"B\" TEXT," + // 2: b
                "\"C\" TEXT," + // 3: c
                "\"D\" TEXT," + // 4: d
                "\"CHILDUUID\" TEXT);"); // 5: childuuid
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"APP_USE_BEAN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, AppUseBean entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String a = entity.getA();
        if (a != null) {
            stmt.bindString(2, a);
        }
 
        String b = entity.getB();
        if (b != null) {
            stmt.bindString(3, b);
        }
 
        String c = entity.getC();
        if (c != null) {
            stmt.bindString(4, c);
        }
 
        String d = entity.getD();
        if (d != null) {
            stmt.bindString(5, d);
        }
 
        String childuuid = entity.getChilduuid();
        if (childuuid != null) {
            stmt.bindString(6, childuuid);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, AppUseBean entity) {
        stmt.clearBindings();
 
        Long _id = entity.get_id();
        if (_id != null) {
            stmt.bindLong(1, _id);
        }
 
        String a = entity.getA();
        if (a != null) {
            stmt.bindString(2, a);
        }
 
        String b = entity.getB();
        if (b != null) {
            stmt.bindString(3, b);
        }
 
        String c = entity.getC();
        if (c != null) {
            stmt.bindString(4, c);
        }
 
        String d = entity.getD();
        if (d != null) {
            stmt.bindString(5, d);
        }
 
        String childuuid = entity.getChilduuid();
        if (childuuid != null) {
            stmt.bindString(6, childuuid);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public AppUseBean readEntity(Cursor cursor, int offset) {
        AppUseBean entity = new AppUseBean( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // _id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // a
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // b
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // c
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // d
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5) // childuuid
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, AppUseBean entity, int offset) {
        entity.set_id(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setA(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setB(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setC(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setD(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setChilduuid(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(AppUseBean entity, long rowId) {
        entity.set_id(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(AppUseBean entity) {
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