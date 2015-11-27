package net.swaas.drinfo.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import net.swaas.drinfo.beans.Doctor;
import net.swaas.drinfo.core.CoreDAO;
import net.swaas.drinfo.logger.LogTracer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vinoth on 10/22/15.
 */
public class DoctorDAO extends CoreDAO {

    private static final LogTracer LOG_TRACER = LogTracer.instance(DoctorDAO.class);

    public static final String TABLE_NAME = "ALL_DOCTORS";

    public enum Column {
        Doctor_Id, Company_Code, First_Name, Last_Name, Speciality_Name, Speciality_Code, Hospital_Name,
        Hospital_Photo_Url, Latitude, Longitude, Location_Full_Address,
        Phone_Number, Email_Id, Landmark, Assistant_Name, Assistant_Phone_Number,
        Remarks, Employee_Code, Region_Code, Region_Id, Manager_User_Code,
        Manager_User_Id, Manager_Region_Code, Manager_Region_Id, Created_DateTime,
        User_Code, User_Id, Created_By, Updated_By, Updated_DateTime, Working_From_Time,
        Working_To_Time, Working_From_Time_2, Working_To_Time_2, Working_From_Time_3,
        Working_To_Time_3, Trainer_Code
    }

    public static StringBuilder getCreateQuety() {
        StringBuilder sb = new StringBuilder("CREATE TABLE IF NOT EXISTS ");
        sb.append(TABLE_NAME);
        sb.append(" (");
        sb.append(addNewColumn(Column.Doctor_Id, ColumnTypes.INTEGER_PRIMARY));
        sb.append(addNewColumn(Column.Company_Code, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.First_Name, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Last_Name, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Speciality_Code, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Speciality_Name, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Hospital_Name, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Hospital_Photo_Url, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Latitude, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Longitude, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Location_Full_Address, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Phone_Number, ColumnTypes.BIGINT));
        sb.append(addNewColumn(Column.Email_Id, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Landmark, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Assistant_Name, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Assistant_Phone_Number, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Remarks, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Employee_Code, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Region_Code, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Region_Id, ColumnTypes.INTEGER));
        sb.append(addNewColumn(Column.Manager_User_Code, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Manager_User_Id, ColumnTypes.INTEGER));
        sb.append(addNewColumn(Column.Manager_Region_Code, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Manager_Region_Id, ColumnTypes.INTEGER));
        sb.append(addNewColumn(Column.Created_DateTime, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.User_Code, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.User_Id, ColumnTypes.INTEGER));
        sb.append(addNewColumn(Column.Created_By, ColumnTypes.INTEGER));
        sb.append(addNewColumn(Column.Updated_By, ColumnTypes.INTEGER));
        sb.append(addNewColumn(Column.Working_From_Time, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Working_To_Time, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Working_From_Time_2, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Working_To_Time_2, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Working_From_Time_3, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Working_To_Time_3, ColumnTypes.TEXT));
        sb.append(addNewColumn(Column.Trainer_Code, ColumnTypes.INTEGER));
        sb.append(addNewColumn(Column.Updated_DateTime, ColumnTypes.TEXT, false));
        sb.append(");");
        return sb;
    }

    public DoctorDAO(Context context) {
        super(context);
    }

    public List<Doctor> insert(List<Doctor> list) {
        List<Doctor> newItems = new ArrayList<>(list.size());
        if (list != null && list.size() > 0) {
            for (int i = 0; i <= list.size() - 1; i++) {
                long out = insert(list.get(i));
                if (out >= 0) newItems.add(list.get(i));
                LOG_TRACER.d("insert id " + out);
            }
        }
        return newItems;
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            db.execSQL("DELETE FROM " + TABLE_NAME);
        } catch (Exception e) {
            LOG_TRACER.e(e);
        } finally {
            if (db != null)
                db.close();
        }
    }

    public long insert(Doctor item) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = parseContentValues(item, Doctor.class);
            long out = db.insert(TABLE_NAME, null, contentValues);
            return out;
        } catch (Exception e) {
            //Log.e(TAG, e.toString(), e);
            LOG_TRACER.e(e);
        } finally {
            if (db != null)
                db.close();
        }
        return -1;
    }

    public long update(Doctor item) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            ContentValues contentValues = parseContentValues(item, Doctor.class);
            long out = db.update(TABLE_NAME, contentValues, Column.Doctor_Id + "=?", new String[]{Integer.toString(item.getDoctor_Id())});
            return out;
        } catch (Exception e) {
            //Log.e(TAG, e.toString(), e);
            LOG_TRACER.e(e);
        } finally {
            if (db != null)
                db.close();
        }
        return -1;
    }

    public List<Doctor> getAlphabetically(int limit, int offset) {
        Cursor res = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            List<Doctor> items = new ArrayList<>();
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " ORDER BY " + Column.First_Name + " ASC OFFSET " + offset + " LIMIT " + limit, null);
            res.moveToFirst();
            Doctor item = null;
            while (res.isAfterLast() == false) {
                //int cnt = res.getColumnCount();
                item = parseCursorData(res, Doctor.class);
                if (item != null) {
                    items.add(item);
                }
                res.moveToNext();
            }
            return items;
        } catch (Exception e) {
            //Log.e(TAG, e.toString(), e);
            LOG_TRACER.e(e);
        } finally {
            if (res != null)
                res.close();
            if (db != null)
                db.close();
        }
        return null;
    }

    public List<Doctor> getAll() {
        Cursor res = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            List<Doctor> items = new ArrayList<>();
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
            res.moveToFirst();
            Doctor item = null;
            while (res.isAfterLast() == false) {
                //int cnt = res.getColumnCount();
                item = parseCursorData(res, Doctor.class);
                if (item != null) {
                    items.add(item);
                }
                res.moveToNext();
            }
            return items;
        } catch (Exception e) {
            //Log.e(TAG, e.toString(), e);
            LOG_TRACER.e(e);
        } finally {
            if (res != null)
                res.close();
            if (db != null)
                db.close();
        }
        return null;
    }

    public Doctor get(int doctorId) {
        Cursor res = null;
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            Doctor item = null;
            res = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + Column.Doctor_Id + "=?", new String[]{Integer.toString(doctorId)});
            res.moveToFirst();
            while (res.isAfterLast() == false) {
                //int cnt = res.getColumnCount();
                item = parseCursorData(res, Doctor.class);
                if (item != null) {
                    return item;
                }
                res.moveToNext();
            }
            return null;
        } catch (Exception e) {
            //Log.e(TAG, e.toString(), e);
            LOG_TRACER.e(e);
        } finally {
            if (res != null)
                res.close();
            if (db != null)
                db.close();
        }
        return null;
    }
}
