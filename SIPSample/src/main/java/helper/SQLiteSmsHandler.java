package helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

/**
 * Created by Nigussie on 05.11.2015.
 */
public class SQLiteSmsHandler extends SQLiteOpenHelper {
    /*myDB.execSQL("CREATE TABLE IF NOT EXISTS "
               + TableName+" (msg_id INTEGER PRIMARY KEY, sender TEXT UNIQUE, msg TEXT NULL,date TEXT);");*/
    // msgid sender msgdata msgdate smsDb
    public static final String TAG=SQLiteSmsHandler.class.getSimpleName();
    private static final String KEY_SMS_ID="msgid";
    private static final String KEY_SMS_SENDER="sender";
    private static final String KEY_SMS_MSG="msgdata";
    private static final String KEY_SMS_DATE="msgdate";
    private static final String DATABASE_NAME="smsDb";
    private static final int DATABASE_VERSION=1;
    public static final String KEY_ROWID = "_id";
    private static final SQLiteDatabase myDB= null;
    private static final String TableName = "mySmsTable";

    public SQLiteSmsHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_SMS_TABLE = "CREATE TABLE " + TableName + "("
                + KEY_SMS_ID + " INTEGER PRIMARY KEY,"
                + KEY_SMS_SENDER + " TEXT,"
                + KEY_SMS_MSG + " TEXT NULL,"
                + KEY_SMS_DATE + " TEXT NULL"
                + ")";
        db.execSQL(CREATE_SMS_TABLE);
        Log.d(TAG, "Database tables created");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TableName);
        // Create tables again
        onCreate(db);
    }
    public void addMessage(String msg_id,String sender_from,String msg,String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SMS_ID, msg_id); //
        values.put(KEY_SMS_SENDER, sender_from); //
        values.put(KEY_SMS_MSG, msg); //
        values.put(KEY_SMS_DATE, date); // Created At
        // Inserting Row
        long id = db.insert(TableName, null, values);
        db.close(); //
        Log.d(TAG, "New user inserted into sqlite: " + id);
    }
    public HashMap<String, String> getMsgDetails(String sender) {
        HashMap<String, String> user = new HashMap<String, String>();
        SQLiteDatabase db = this.getReadableDatabase();
        // Cursor cursor= mDatabase.query("TABLE_LOGIN",null,null,null,null,null,null);
        //onCreate(db);
        if(db.isOpen()) {
            String selectQuery = "SELECT * FROM " + TableName +" WHERE sender = "+sender;
            Cursor cursor = db.rawQuery(selectQuery, null);
            //Move to first row
            ///msgid sender msgdata msgdate smsDb
            cursor.moveToFirst();
            if (cursor.getCount() > 0) {
                user.put("msgid", cursor.getString(1));
                user.put("sender", cursor.getString(2));
                user.put("msgdata", cursor.getString(3));//
                user.put("msgdate", cursor.getString(4));
            }
            cursor.close();
            db.close();
            //return user
            Log.d(TAG, "Fetching user from Sqlite: " + TableName.toString());
        }
        else{
            Log.d(TAG,"The database is not opened needed to be opened: =======ppppp============");
        }
        return user;
    }
}
