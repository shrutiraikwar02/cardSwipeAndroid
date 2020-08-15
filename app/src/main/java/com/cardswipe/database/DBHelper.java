package com.cardswipe.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.cardswipe.R;
import com.cardswipe.models.Dob;
import com.cardswipe.models.Id;
import com.cardswipe.models.Name;
import com.cardswipe.models.Picture;
import com.cardswipe.models.UserData;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "shaadiDB";

    //_________________________user table__________________________
    private static final String TABLE_USER = "user";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_Name = "name";
    private static final String KEY_GENDER = "gender";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_DOB = "dob";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_PROFILE_PIC = "profile_pic";
    private static final String KEY_CARD_STATUS = "card_status";

    //-------------------------location table------------------
    private static final String TABLE_LOCATION = "location";
    private static final String KEY_LAT = "latitute";
    private static final String KEY_LONG = "longitute";
    private static final String KEY_TIME = "time";
    //private static final String KEY_USER_ID = "user_id";


    private Context mContext;

    /*
     * Instantiates a new Sqlite db.
     *
     * @param context the context

     */
    public DBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //------------------------user table-----------------------
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
                + KEY_USER_ID + " TEXT ," + KEY_Name + " TEXT," + KEY_GENDER + " TEXT," + KEY_PROFILE_PIC + " TEXT,"
                + KEY_EMAIL + " TEXT," + KEY_DOB + " TEXT ," + KEY_PHONE + " TEXT,"
                + KEY_CARD_STATUS + " TEXT"
                + ")";

        //------------------------location table-----------------------

        String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_LOCATION + "("
                + KEY_LAT + " TEXT ," + KEY_LONG + " TEXT," + KEY_TIME + " TEXT," + KEY_USER_ID + " TEXT"
                + ")";

        sqLiteDatabase.execSQL(CREATE_USER_TABLE);
        sqLiteDatabase.execSQL(CREATE_LOCATION_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    /*
     * Add user.
     *
     * @param user the user*/


    public void addUser(UserData user) {
        Gson gson = new Gson();
        SQLiteDatabase db = null;
        Log.d("inside addUser", "user= " + user.getName().getFirst() +"id ="+user.getId().getValue());
        try {
            String id = gson.toJson(user.getId());
            db = this.getWritableDatabase();
            if (!checkUser(db, id)) {
                Log.d("inside addUser", "user= " + user.getName().getFirst() +"id ="+user.getId().getValue());
                ContentValues values = new ContentValues();
                String pic = gson.toJson(user.getPicture());
                String dob = gson.toJson(user.getDob());
                String name = gson.toJson(user.getName());
                values.put(KEY_USER_ID, id);
                values.put(KEY_Name, name);
                values.put(KEY_PROFILE_PIC, pic);
                values.put(KEY_EMAIL, user.getEmail());
                values.put(KEY_GENDER, user.getGender());
                values.put(KEY_DOB, dob);
                values.put(KEY_PHONE, user.getPhone());
                values.put(KEY_CARD_STATUS, "");

                long l = db.insert(TABLE_USER, null, values);//-1 if error occur
                Log.d("inside addUser", "= " + l);

            } else {
                upDateUserTable(db, user);// update table where user id = user.getuser_id()
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Check user boolean.
     *
     * @param db      the db
     * @param user_id the user id
     * @return the boolean*/
    public boolean checkUser(SQLiteDatabase db, String user_id) {
        try {
            String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE " + KEY_USER_ID + "=" + "\'" + user_id + "\'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                return true;
            }
            cursor.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /*
     * Up date user table.
     *
     * @param db   the db
     * @param user the user*/


    public void upDateUserTable(SQLiteDatabase db, UserData user) {
        try {
            Log.d("inside upDateUserTable", "user= " + user.getName().getFirst() +"id ="+user.getId().getValue());
            Gson gson = new Gson();
            ContentValues values = new ContentValues();
            String id = gson.toJson(user.getId());
            String pic = gson.toJson(user.getPicture());
            String dob = gson.toJson(user.getDob());
            String name = gson.toJson(user.getName());

            values.put(KEY_USER_ID, id);
            values.put(KEY_Name, name);
            values.put(KEY_PROFILE_PIC, pic);
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_GENDER, user.getGender());
            values.put(KEY_DOB, dob);
            values.put(KEY_PHONE, user.getPhone());
            values.put(KEY_CARD_STATUS, user.getCardStatus());

            long l = db.update(TABLE_USER, values, KEY_USER_ID + "=" + "\'" + id + "\'", null);
            Log.d("inside update", "= " + l);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*
     * Gets users.
     *
     * @return the users*/
    public List<UserData> getUsers() {
        Log.d("inside", "getUsers");
        List<UserData> users = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    UserData userModel = new UserData();
                    userModel.setId(gson.fromJson(cursor.getString(0), Id.class));//convert it to object
                    userModel.setName(gson.fromJson(cursor.getString(1), Name.class));//convert it to object
                    userModel.setGender(cursor.getString(2));
                    userModel.setPicture(gson.fromJson(cursor.getString(3), Picture.class));//profile pic object
                    userModel.setEmail(cursor.getString(4));
                    userModel.setDob(gson.fromJson(cursor.getString(5), Dob.class));
                    userModel.setPhone(cursor.getString(6));
                    userModel.setCardStatus(cursor.getString(7));
                    users.add(userModel);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("inside", "getUsers list =" + users.size());

        return users;
    }

    /*
     * Gets accepted users.
     *
     * @return the accepted users*/
    public List<UserData> getAcceptedUsers() {
        Log.d("inside", "getAcceptedUsers");
        List<UserData> users = new ArrayList<>();

        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE " + KEY_CARD_STATUS + "='"
                + mContext.getResources().getString(R.string.accept) + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    UserData userModel = new UserData();
                    userModel.setId(gson.fromJson(cursor.getString(0), Id.class));//convert it to object
                    userModel.setName(gson.fromJson(cursor.getString(1), Name.class));//convert it to object
                    userModel.setGender(cursor.getString(2));
                    userModel.setPicture(gson.fromJson(cursor.getString(3), Picture.class));//profile pic object
                    userModel.setEmail(cursor.getString(4));
                    userModel.setDob(gson.fromJson(cursor.getString(5), Dob.class));
                    userModel.setPhone(cursor.getString(6));
                    userModel.setCardStatus(cursor.getString(7));
                    users.add(userModel);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("inside", "getAcceptedUsers list =" + users.size());

        return users;
    }

    /*
     * Gets declined users.
     *
     * @return the declined users*/
    public List<UserData> getDeclinedUsers() {
        Log.d("inside", "getDeclinedUsers");
        List<UserData> users = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE " + KEY_CARD_STATUS + "='"
                + mContext.getResources().getString(R.string.decline) + "'";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    UserData userModel = new UserData();
                    userModel.setId(gson.fromJson(cursor.getString(0), Id.class));//convert it to object
                    userModel.setName(gson.fromJson(cursor.getString(1), Name.class));//convert it to object
                    userModel.setGender(cursor.getString(2));
                    userModel.setPicture(gson.fromJson(cursor.getString(3), Picture.class));//profile pic object
                    userModel.setEmail(cursor.getString(4));
                    userModel.setDob(gson.fromJson(cursor.getString(5), Dob.class));
                    userModel.setPhone(cursor.getString(6));
                    userModel.setCardStatus(cursor.getString(7));
                    users.add(userModel);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("inside", "getDeclinedUsers list =" + users.size());

        return users;
    }


    /*
     * Gets declined users.
     *
     * @return the declined users*/
    public List<UserData> getUnTouchedUsers() {
        Log.d("inside", "getAcceptedUsers");
        List<UserData> users = new ArrayList<>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER + " WHERE " + KEY_CARD_STATUS + "=''";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                try {
                    Gson gson = new Gson();
                    UserData userModel = new UserData();
                    userModel.setId(gson.fromJson(cursor.getString(0), Id.class));//convert it to object
                    userModel.setName(gson.fromJson(cursor.getString(1), Name.class));//convert it to object
                    userModel.setGender(cursor.getString(2));
                    userModel.setPicture(gson.fromJson(cursor.getString(3), Picture.class));//profile pic object
                    userModel.setEmail(cursor.getString(4));
                    userModel.setDob(gson.fromJson(cursor.getString(5), Dob.class));
                    userModel.setPhone(cursor.getString(6));
                    userModel.setCardStatus(cursor.getString(7));
                    users.add(userModel);

                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            } while (cursor.moveToNext());
        }
        cursor.close();
        Log.d("inside", "getUnTouchedUsers list =" + users.size());

        return users;
    }

    /*
     * Delete user.*/
    public void deleteUser() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_USER);
        db.close();
    }

}
