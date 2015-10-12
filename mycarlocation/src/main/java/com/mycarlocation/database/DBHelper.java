package com.mycarlocation.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;


public class DBHelper extends SQLiteOpenHelper {
	private SQLiteDatabase db;
	 public static final int DATABASE_VERSION = 1;
	    private SharedPreferences prefs;
	    public static final String TABLE_NAME = "MyCardLocation";
	    public static final String KEY_ID = "_id";
		public static final String KEY_LATITUDE = "Latitude";
		public static final String KEY_LONGITUDE = "Longitude";
		public static final String KEY_DATE = "Date";
		public static final String KEY_ADRESS = "Adress";
		private static final String SQLCREATE = "CREATE TABLE if not exists " + TABLE_NAME + " ( " 
				+ KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " 
				+ KEY_LATITUDE + " TEXT, "
				+ KEY_LONGITUDE + " TEXT, "
				+ KEY_DATE + " TEXT, "
				+ KEY_ADRESS + " TEXT) ";
	    
	    
	    public DBHelper(Context context, String name,
				CursorFactory factory, int version) {
			super(context, name, factory, version);
			prefs=PreferenceManager.getDefaultSharedPreferences(context);
		}

	

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		try{
			db.execSQL(SQLCREATE);
			}catch(Exception e){
				e.printStackTrace();
			}
		this.db=db;
	}
	public void Close() {
		if(db!=null) {
			db.close();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int OldVersion, int NewVersion) {
		// TODO Auto-generated method stub
		try{
			db.execSQL("DROP TABLE IF EXISTS "+ TABLE_NAME);
			onCreate(db);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public boolean insertLocation(String latitude,String longitude,String date,String adress) {
		ContentValues con = new ContentValues();
		con.put(KEY_LATITUDE, latitude);
		con.put(KEY_LONGITUDE, longitude);
		con.put(KEY_DATE, date);
		con.put(KEY_ADRESS, adress);
		boolean ret =this.getWritableDatabase().insert(TABLE_NAME, null, con)>0;
		deleteFirst(50);
		return ret;
	}
	public Cursor SelectFromId(int id){
		
		Cursor cur= this.getReadableDatabase().query(TABLE_NAME, 
				new String[]{KEY_ID,KEY_LATITUDE,KEY_LONGITUDE,KEY_DATE,KEY_ADRESS}, 
				KEY_ID + "=?", 
				new String[]{String.valueOf(id)}, null,null, null); 
		
		return cur;
		
		
	}
	public int getRowsCount() {
		
		Cursor cur= this.getReadableDatabase().query(TABLE_NAME, 
				new String[]{KEY_ID}, null, null, null,null, null); 
		
		return cur.getCount();
	}
	
	public void deleteFirst(int maxValue) {
		if(getRowsCount()>maxValue) {
			Cursor cur= this.getReadableDatabase().query(TABLE_NAME, null, null, null, null,null, null);
			if(cur.moveToFirst()) {
				int rowId = cur.getInt(cur.getColumnIndex(KEY_ID)); 
	            this.deleteFromID(rowId);
			}
			cur.close();
		}
		
	}
	public boolean deleteLast() {
	
		return this.getWritableDatabase().delete(TABLE_NAME, KEY_ID + "=?",new String[]{"(SELECT MAX(_id) FROM"+TABLE_NAME+")"})>0;
		
	}
	
	public boolean deleteFromID(int id) {
		return this.getWritableDatabase().delete(TABLE_NAME, KEY_ID + "=?",  new String[]{String.valueOf(id)}) > 0;
	}
	
	public Cursor SelectAll() {
		Cursor cur= this.getReadableDatabase().query(TABLE_NAME, 
				new String[]{KEY_ID,KEY_LATITUDE,KEY_LONGITUDE,KEY_DATE,KEY_ADRESS}, null, null, null,null, KEY_DATE + " DESC",prefs.getString("edittext_maxentryes_history", "50")); 
		
		return cur;
	}

}
