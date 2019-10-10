/*
 * Property of IT Man AS, Bryne Norway It is strictly forbidden to copy or modify the authors file, however it is possible to
 * use the open source libraries under the GNU licence protocols terms.
 * © 2015 IT Man AS
 *
 * Created by Nigussie on 03.03.2015.
 */
package helper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
public class SQLiteHandler extends SQLiteOpenHelper {
    private static final String TAG = SQLiteHandler.class.getSimpleName();
    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "android_api";
    // Login table name
    private static final String TABLE_LOGIN = "login";
    // Login Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_UID = "uid";
    private static final String KEY_CREATED_AT = "created_at";
    private static final String KEY_LOGIN_AT="login_at";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_DESC="desc";
    //public static final String KEY_ROWID = "_id";
    ///////// For storing sms history in sqllite
    public SQLiteHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    //
    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_LOGIN_TABLE = "CREATE TABLE " + TABLE_LOGIN + "("
                + KEY_ID + " INTEGER PRIMARY KEY,"
                + KEY_NAME + " TEXT,"
                + KEY_EMAIL + " TEXT UNIQUE,"
                + KEY_PHONE + " TEXT UNIQUE,"
                + KEY_UID + " TEXT,"
                + KEY_CREATED_AT + " TEXT,"
                + KEY_LOGIN_AT + " TEXT,"
                + KEY_DESC + " TEXT"+ ")";
        db.execSQL(CREATE_LOGIN_TABLE);
        Log.d(TAG, "Database tables created");

    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGIN);
        // Create tables again
        onCreate(db);
    }
    /*To retrive
    * user information from the db for sip activity*/
    public HashMap<String, String> getUser() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("name", cursor.getString(1));
            user.put("email", cursor.getString(2));
            user.put("uid", cursor.getString(3));
            user.put("created_at", cursor.getString(4));

        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }
    /**
     *
     * Storing user details in database
     *
     * */
    public void addUser(String name, String email, String phone, String uid, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, name); // Name
        values.put(KEY_EMAIL, email); // Email
        values.put(KEY_PHONE, phone); // phone
        values.put(KEY_UID, uid); // unique ID
        values.put(KEY_CREATED_AT, created_at); // Created At
        // Inserting Row
        long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
        Log.d(TAG, "New user inserted into sqlite: " + id);
    }
    /**
     * Updating user details in database
     *
     * */

    public void updateUser(String uid, String email, String password, String created_at) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String myemail= (String) values.get(KEY_EMAIL);
        values.put(KEY_UID, uid); // unique ID
        values.put(KEY_PASSWORD, password); // Name
        values.put(KEY_CREATED_AT, created_at); // Created At
        // Updating row
        //long id = db.insert(TABLE_LOGIN, null, values);
        db.update(TABLE_LOGIN, values, myemail + "=" + email, null);
        db.close(); // Closing database connection
    }
    /*
    *
    * Delete user by email
    *
    * */
    public void Delete_user_byemail(String uid, String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        String myemail= (String) values.get(KEY_EMAIL);
        uid= (String) values.get(KEY_UID); // unique ID
        //values.put(KEY_CREATED_AT, created_at); // Created At
        //long id = db.insert(TABLE_LOGIN, null, values);
        db.delete(TABLE_LOGIN, myemail + " = " + email, null);
        //   long id = db.insert(TABLE_LOGIN, null, values);
        db.close(); // Closing database connection
    }

    /*
    *
    * */

    //db.getPhoneUser(uid, email,name,phone);
    public HashMap<String, String> getPhoneUser(String name,String phone) {
        HashMap<String, String> user = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();

        if(db.isOpen()) {
            String selectQuery = "SELECT * FROM " + TABLE_LOGIN+"WHERE phone = "+"'"+phone+"'";
            Cursor cursor = db.rawQuery(selectQuery, null);
            //Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                user.put("name", cursor.getString(1));
                user.put("email", cursor.getString(2));
                user.put("phone", cursor.getString(3));//
            }
            else{
                Log.d(TAG,"No stored values for the given phone");
            }
            cursor.close();
            db.close();
            //return user
            Log.d(TAG, "Fetching user from Sqlite: " + TABLE_LOGIN.toString());
        }
        else{
            Log.d(TAG,"The database is not opened needed to be opened: =======ppppp============");
        }
        return user;
    }

     /**
     * Getting user data from database
     *
     **/
    public HashMap<String, String> getUserDetails(String email) {
        HashMap<String, String> user = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Cursor cursor= mDatabase.query("TABLE_LOGIN",null,null,null,null,null,null);
        //onCreate(db);
        if(db.isOpen()) {
            String selectQuery = "SELECT * FROM " + TABLE_LOGIN; //+"WHERE email = "+email;
            Cursor cursor = db.rawQuery(selectQuery, null);
            //Move to first row
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                user.put("name", cursor.getString(1));
                user.put("email", cursor.getString(2));
                user.put("phone", cursor.getString(3));//
                user.put("uid", cursor.getString(4));
                //user.put("created_at", cursor.getString(5));
                //user.put("login_at",cursor.getString(6));
            }
            cursor.close();
            db.close();
            //return user
            Log.d(TAG, "Fetching user from Sqlite: " + TABLE_LOGIN.toString());
        }
        else{
            Log.d(TAG,"The database is not opened needed to be opened: =======ppppp============");
        }
        return user;
    }
    /**
     *Getting user login status return true if rows are there in table
     *
     * */
    public int getRowCount() {
        String countQuery = "SELECT * FROM " + TABLE_LOGIN;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int rowCount = cursor.getCount();
        db.close();
        cursor.close();
        // return row count
        return rowCount;
    }
    /**
     * Re crate database Delete all tables and create them again
     * */
    public void deleteUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete All Rows
        db.delete(TABLE_LOGIN, null, null);
        db.close();
        Log.d(TAG, "Deleted all user info from sqlite");
    }
    public void setTimeStamp(String uid,String email, String _login_at) {
/*
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(db.isOpen()) {
            String myemail = (String) values.get(KEY_EMAIL);
            Log.d(TAG,"KEY_EMAIL myemail and email in sqllite is: "+KEY_EMAIL+ myemail+" "+ email);
            try {
                    values.put(KEY_UID,uid);
                    values.put(KEY_LOGIN_AT, _login_at); // Login At
                    //Inserting Row
                    db.update(TABLE_LOGIN, values, myemail + "=" + email, null);
            }catch(Exception e){

                e.printStackTrace();
            }
            db.close(); // Closing database connection
        }else{
            Log.d(TAG,"The db is not opened");
        }*/
    }
}
