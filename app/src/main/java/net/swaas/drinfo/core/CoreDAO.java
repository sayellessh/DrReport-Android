package net.swaas.drinfo.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import net.swaas.drinfo.adapters.RecentDoctorsRecyclerAdapter;
import net.swaas.drinfo.core.annotations.ExcludeColumn;
import net.swaas.drinfo.dao.DoctorDAO;
import net.swaas.drinfo.dao.RecentDAO;
import net.swaas.drinfo.logger.LogTracer;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by SwaaS on 5/18/2015.
 */
public class CoreDAO extends SQLiteOpenHelper {

    protected Context mContext;
    private final String TAG = this.getClass().getCanonicalName();
    private static final LogTracer LOG_TRACER = LogTracer.instance(CoreDAO.class);
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "dr_info";
    public static final class ColumnTypes {
        public static final String TEXT = "TEXT";
        public static final String INTEGER = "INTEGER";
        public static final String BIGINT = "BIGINT";
        public static final String INTEGER_PRIMARY = "INTEGER PRIMARY KEY";
    }

    public CoreDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DoctorDAO.getCreateQuety().toString());
        db.execSQL(RecentDAO.getCreateQuety().toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        String dropString = "DROP TABLE IF EXISTS ";
        db.execSQL(dropString + DoctorDAO.TABLE_NAME);
        db.execSQL(dropString + RecentDAO.TABLE_NAME);
    }

    public static String addNewColumn(Object columnName, String columnType) {
        return addNewColumn(columnName, columnType, true);
    }

    public static String addNewColumn(Object columnName, String columnType, boolean comma) {
        return columnName + " " + columnType + (comma ? ", ":"");
    }

    public static String addAlterColumn(String tableName, String columnName, String columnType) {
        StringBuilder sb = new StringBuilder("ALTER TABLE ");
        sb.append(tableName);
        sb.append(" ADD ");
        sb.append(columnName);
        sb.append(" " + columnType + ";");
        return sb.toString();
    }

    public void truncateTables() {

    }

    public <T> ContentValues parseContentValues(T oClass, Class<T> mClass) {
        try {
            ContentValues contentValues = new ContentValues();
            Field[] fields = mClass.getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                Object fClass = field.get(oClass);
                if (!field.isAnnotationPresent(ExcludeColumn.class)) {
                    if (field.get(oClass) != null) {
                        if (fClass.getClass().getName().equals(String.class.getName())) {
                            contentValues.put(field.getName(), field.get(oClass).toString());
                        } else if (fClass.getClass().getName().equals(Integer.class.getName())) {
                            contentValues.put(field.getName(), Integer.parseInt(field.get(oClass).toString()));
                        } else if (fClass.getClass().getName().equals(int.class.getName())) {
                            contentValues.put(field.getName(), field.getInt(oClass));
                        } else if (fClass.getClass().getName().equals(Boolean.class.getName())) {
                            contentValues.put(field.getName(), field.getBoolean(oClass));
                        } else if (fClass.getClass().getName().equals(boolean.class.getName())) {
                            contentValues.put(field.getName(), field.getBoolean(oClass));
                        } else if (fClass.getClass().getName().equals(Date.class.getName())) {
                            contentValues.put(field.getName(), ((Date) field.get(oClass)).getTime());
                        } else if (fClass.getClass().getName().equals(long.class.getName())) {
                            contentValues.put(field.getName(), ((long) field.get(oClass)));
                        } else if (fClass.getClass().getName().equals(Long.class.getName())) {
                            contentValues.put(field.getName(), ((Long) field.get(oClass)));
                        } else if (fClass.getClass().getName().equals(Double.class.getName())) {
                            contentValues.put(field.getName(), ((Double) field.get(oClass)));
                        } else if (fClass.getClass().getName().equals(double.class.getName())) {
                            contentValues.put(field.getName(), ((double) field.get(oClass)));
                        } else if (fClass.getClass().getName().equals(BigInteger.class.getName())) {
                            contentValues.put(field.getName(), ((BigInteger) field.get(oClass)).toString());
                        }/* else {
                        contentValues.put(field.getName(), field.get(oClass).toString());
                    }*/
                    }
                }
            }
            return contentValues;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }
    public <T> T parseCursorData(Cursor res, Class<T> tClass) {
        try {
            T oClass = tClass.newInstance();
            Field[] fields = tClass.getDeclaredFields();
            for(Field field : fields) {
                field.setAccessible(true);
                String type = field.getType().getName();
                if(res.getColumnIndex(field.getName()) >= 0) {
                    if (field.getType().getName().equals(String.class.getName())) {
                        field.set(oClass, res.getString(res.getColumnIndex(field.getName())));
                    } else if (field.getType().getName().equals(Integer.class.getName())) {
                        if(field.getName().equals("rowid")){
                            field.set(oClass, (Integer)res.getInt(res.getColumnIndex(field.getName())));
                        }else {
                            field.setInt(oClass, Integer.parseInt(res.getString(res.getColumnIndex(field.getName()))));
                        }
                    } else if (field.getType().getName().equals(int.class.getName())) {
                        field.setInt(oClass, res.getInt(res.getColumnIndex(field.getName())));
                    } else if (field.getType().getName().equals(long.class.getName())) {
                        field.setLong(oClass, res.getLong(res.getColumnIndex(field.getName())));
                    } else if (field.getType().getName().equals(Long.class.getName())) {
                        field.setLong(oClass, res.getLong(res.getColumnIndex(field.getName())));
                    } else if (field.getType().getName().equals(float.class.getName())) {
                        field.setFloat(oClass, res.getFloat(res.getColumnIndex(field.getName())));
                    } else if (field.getType().getName().equals(Float.class.getName())) {
                        field.setFloat(oClass, res.getFloat(res.getColumnIndex(field.getName())));
                    } else if (field.getType().getName().equals(double.class.getName())) {
                        field.setDouble(oClass, res.getDouble(res.getColumnIndex(field.getName())));
                    } else if (field.getType().getName().equals(Double.class.getName())) {
                        field.setDouble(oClass, res.getDouble(res.getColumnIndex(field.getName())));
                    } else if (field.getType().getName().equals(Boolean.class.getName())) {
                        field.setBoolean(oClass, res.getInt(res.getColumnIndex(field.getName())) > 0 ? true : false);
                    } else if (field.getType().getName().equals(boolean.class.getName())) {
                        int colIdx = res.getColumnIndex(field.getName());
                        if (colIdx >= 0) {
                            field.setBoolean(oClass, res.getInt(colIdx) > 0 ? true : false);
                        }
                    } else if (field.getType().getName().equals(Date.class.getName())) {
                        Date d = new Date(res.getLong(res.getColumnIndex(field.getName())));
                        field.set(oClass, d);
                    } else if (field.getType().getName().equals(BigInteger.class.getName())) {
                        String val = res.getString(res.getColumnIndex(field.getName()));
                        if (!TextUtils.isEmpty(val)) {
                            field.set(oClass, new BigInteger(val));
                        }
                    } else {
                        field.set(oClass, res.getString(res.getColumnIndex(field.getName())));
                    }
                }
            }
            return oClass;
        } catch (Exception e) {
            Log.e(TAG, e.toString(), e);
        }
        return null;
    }

}
