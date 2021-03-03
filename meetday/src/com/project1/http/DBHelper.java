package com.project1.http;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.project1.meetday.ChatHistory;

public class DBHelper extends SQLiteOpenHelper {
	private final String TAG = this.getClass().getName();
	final static String database = "Mydatabase";
	final static int version = 1;

	public static final int COLUMN_ID = 0;
	public static final int COLUMN_NAME = 1;
	public static final int COLUMN_TIME = 2;
	public static final int COLUMN_FID = 3;
	public static final int COLUMN_DURATION = 4;
	public static final int COLUMN_CALLID = 5;
	public static final int COLUMN_MSG = 6;
	public static final int COLUMN_PATTERN = 7;
	public static final int COLUMN_TYPE = 8;

	//private String table;

	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	public DBHelper(Context context) {
		super(context, database, null, version);
		//this.table = table;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		//if(table.equals("history")){
	        db.execSQL("CREATE TABLE "+"history"+"(_id integer primary key autoincrement," +
	        		"name text not null," +
	        		"time text not null," +
	        		"fid text not null," +
	        		"duration text," +
	        		"callid text not null," +
	        		"msg text," +
	        		"pattern text," +
	        		"type text not null)");
		//} else if (table.equals("contact")){
			db.execSQL("CREATE TABLE "+"contact"+"(_id integer primary key autoincrement," +
	        		"name text not null," +
	        		"fid text not null," +
	        		//"duration text," +
	        		"type text)");
			db.execSQL("CREATE TABLE "+"giftcard"+"(_id integer primary key autoincrement," +
	        		"callid text," +
	        		"msg text," +
	        		"pattern integer," +
	        		"type text)");
		//}
	}
    
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldversion, int newversion) {
		// TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+"history");
        db.execSQL("DROP TABLE IF EXISTS "+"contact");
        onCreate(db);
	}
	
	public void onDropTable(SQLiteDatabase db, String table) {
		// TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS "+table);
	}
	
	public void onCreateTable(SQLiteDatabase db, String table) {
		// TODO Auto-generated method stub
		if(table.equals("history")){
	        db.execSQL("CREATE TABLE "+"history"+"(_id integer primary key autoincrement," +
	        		"name text not null," +
	        		"time text not null," +
	        		"fid text not null," +
	        		"duration text," +
	        		"callid text not null," +
	        		"msg text," +
	        		"pattern text," +
	        		"type text not null)");
		} else if (table.equals("contact")){
			db.execSQL("CREATE TABLE "+"contact"+"(_id integer primary key autoincrement," +
	        		"name text not null," +
	        		"fid text not null," +
	        		//"duration text," +
	        		"type text)");
		} else if(table.equals("giftcarddb")){
			db.execSQL("CREATE TABLE "+"giftcard"+"(_id integer primary key autoincrement," +
	        		"callid text," +
	        		"msg text," +
	        		"pattern integer," +
	        		"type text)");
		} else if(table.equals("msgdb")){
			db.execSQL("CREATE TABLE "+"msg"+"(_id integer primary key autoincrement," +
					"callid text," +
					"msg text," +
					"pattern integer," +
					"type text)");
		}
	}
	
	public void insertData(SQLiteDatabase db, String table, Const.SQL_data data){
		ContentValues cv = new ContentValues();
		long id;
		if(data.name!=null)
			cv.put("name", data.name);
		if(data.fid!=null)
			cv.put("fid", data.fid);
		if(data.time!=null)
			cv.put("time", data.time);
		if(data.type!=null)
			cv.put("type", data.type);
		if(data.duration!=null)
			cv.put("duration", data.duration);
		if(data.msg!=null)
			cv.put("msg", data.msg);
		if(data.callid!=null)
			cv.put("callid", data.callid);
		if(data.pattern!=null)
			cv.put("pattern", data.pattern);
		//cv.put("type", String.valueOf(Const.History_Type.HType_Dial));
		id = db.insert(table, null, cv);
		if (id > 0) {
			Log.d(this.getClass().getName(),"Success DB");
		} else {
			Log.d(this.getClass().getName(),"Fail DB");
		}
	}

	public void updateData(SQLiteDatabase db, String table, String uid, ContentValues cv){
		long id;
		id = db.update(table, cv, "_id=" + "'" + uid + "'",
				null);
		
		if (id > 0) {
			Log.d(this.getClass().getName(),"Success DB");
		} else {
			Log.d(this.getClass().getName(),"Fail DB");
		}
	}
	
	/*public String ggetNamefromSql(SQLiteDatabase db, String table, String uid){
		String[] col = {"name", "fid"};
		Cursor cur = db.query(table, col, "fid=" + "'" + uid
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(0);
		} else {
			return null;
		}

	}
	
	public String ggetfidfromSql(SQLiteDatabase db, String table, String uid){
		String[] col = {"fid"};
		Cursor cur = db.query(table, col, "_id=" + "'" + uid
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(0);
		} else {
			return null;
		}

	}
		
	public String ggetmsgfromSql(SQLiteDatabase db, String table, String time){
		String[] col = {"msg"};
		Cursor cur = db.query(table, col, "time=" + "'" + time
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(0);
		} else {
			return null;
		}

	}
	
	public String ggetpatternfromSql(SQLiteDatabase db, String table, String time){
		String[] col = {"pattern"};
		Cursor cur = db.query(table, col, "time=" + "'" + time
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(0);
		} else {
			return null;
		}

	}
	
	public String ggetnamefromSql(SQLiteDatabase db, String table, String time){
		String[] col = {"name"};
		Cursor cur = db.query(table, col, "time=" + "'" + time
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(0);
		} else {
			return null;
		}

	}
	
	
	public String ggetcallidfromSql(SQLiteDatabase db, String table, String time){
		String[] col = {"callid"};
		Cursor cur = db.query(table, col, "time=" + "'" + time
				+ "'", null, null, null, null);
		
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(0);
		} else {
			return null;
		}

	}
	*/
	
	public String getdatafromSql(SQLiteDatabase db, String table, String data,
			String query, String value){
		String[] col = {data};
		Cursor cur = db.query(table, col, query+"=" + "'" + value
				+ "'", null, null, null, null);
		
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return cur.getString(0);
		} else {
			return null;
		}
	}
	
	public String[] getdatalstfromSql(SQLiteDatabase db, String table, String data, String query, String value){
		String[] col = {data};
		Cursor cur = db.query(table, col, query+"=" + "'" + value
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			String ret[] = new String[cur.getCount()];
			for (int i = 0; i < cur.getCount(); i++) {
				ret[i] = cur.getString(0);
				cur.moveToNext();
			}
			return ret;
		} else {
			return null;
		}

	}

	public ChatHistory getChatHistory(SQLiteDatabase db, String table, String data, String query, String value){
		//String SQL = "SELECT * FROM " + Const.hisinfo.getTableName() + " WHERE `fid` = '" + value + "' AND (`type` = 'Htype_SendMessage' OR `type` = 'Htype_GetMessage')";
		String SQL = "SELECT * FROM " + Const.hisinfo.getTableName() + " WHERE `fid` = '" + value + "'"; // read both call history and chat history
		Log.d(TAG, "SQL = " + SQL);
		Cursor cur = db.rawQuery(SQL, null);
		/*
		int column_number = cur.getColumnCount();
		String[] columnNames = cur.getColumnNames();
		Log.i(TAG, "Column Number: " + column_number);
		for(int i = 0; i < columnNames.length; i++){
			Log.d(TAG, "Column ID = " + i + ", Column Name: " + columnNames[i]);
		}
		*/
		int msg_number = cur.getCount();
		Log.d(TAG, "Message Number: " + Integer.toString(msg_number));
		if (msg_number > 0) {
			cur.moveToFirst();
			ChatHistory chat_history = new ChatHistory(msg_number);
			String type;
			int direction;
			for (int i = 0; i < cur.getCount(); i++) {
				type = cur.getString(COLUMN_TYPE);
				Log.d(TAG, "type = " + type);
				if(type.equals("Htype_SendMessage") == true){
					direction = ChatHistory.SEND;
				}
				else if(type.equals("Htype_GetMessage") == true){
					direction = ChatHistory.RECEIVE;
				}
				else if(type.equals("HType_Dial") == true){
					direction = ChatHistory.CALL_OUT;
				}
				else if(type.equals("HType_Incoming") == true){
					direction = ChatHistory.CALL_IN_ANSWERED;
				}
				else if(type.equals("HType_Missed") == true){
					direction = ChatHistory.CALL_IN_MISSED;
				}
				else{
					direction = ChatHistory.UNKNOWN;
					Log.e(TAG, "direction = ChatHistory.NOT_CHAT_HISTORY");
				}
				if(chat_history.setChatHistory(i, cur.getString(COLUMN_NAME), cur.getString(COLUMN_TIME), cur.getString(COLUMN_FID), cur.getString(COLUMN_MSG), direction) != 0){
					Log.e(TAG, "Error: setChatHistory");
				}
				cur.moveToNext();
			}
			return chat_history;
		} else {
			return null;
		}

	}

	public String[] getdatalstfromSql(SQLiteDatabase db, String table, String data){
		String[] col = {data};
		Cursor cur = db.query(table, col, null, null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			String ret[] = new String[cur.getCount()];
			for (int i = 0; i < cur.getCount(); i++) {
				ret[i] = cur.getString(0);
				cur.moveToNext();
			}
			return ret;
		} else {
			return null;
		}

	}

	
	public String[] ggetfidfromSql(SQLiteDatabase db, String table){
		String[] col = {"fid"};
		Cursor cur = db.query(table, col, null, null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			String ret[] = new String[cur.getCount()];
			for (int i = 0; i < cur.getCount(); i++) {
				ret[i] = cur.getString(0);
				cur.moveToNext();
			}
			return ret;
		} else {
			return null;
		}

	}
	/*
	public String[] ggetnamelstfromSql(SQLiteDatabase db, String table, int pos){
		String[] col = {"name"};
		Cursor cur = db.query(table, col, "loc_id=" + "'" + pos
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			String ret[] = new String[cur.getCount()];
			for (int i = 0; i < cur.getCount(); i++) {
				ret[i] = cur.getString(0);
				cur.moveToNext();
			}
			return ret;
		} else {
			return null;
		}

	}
	
	public String[] ggettellstfromSql(SQLiteDatabase db, String table, int pos){
		String[] col = {"tel"};
		Cursor cur = db.query(table, col, "loc_id=" + "'" + pos
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			String ret[] = new String[cur.getCount()];
			for (int i = 0; i < cur.getCount(); i++) {
				ret[i] = cur.getString(0);
				cur.moveToNext();
			}
			return ret;
		} else {
			return null;
		}

	}
	
	public String[] ggeteidlstfromSql(SQLiteDatabase db, String table, int pos){
		String[] col = {"eid"};
		Cursor cur = db.query(table, col, "loc_id=" + "'" + pos
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			String ret[] = new String[cur.getCount()];
			for (int i = 0; i < cur.getCount(); i++) {
				ret[i] = cur.getString(0);
				cur.moveToNext();
			}
			return ret;
		} else {
			return null;
		}

	}
	*/
	public String gettimeidfromSql(SQLiteDatabase db, String table, 
			String callid, String time){
		String[] col = {"time"};
		String ret = null;
		Cursor cur = db.query(table, col, "callid=" + "'" + callid
				+ "'", null, null, null, null);
		//Log.d("XXXX", String.valueOf(cur.getCount()));
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			//Log.d("XXXX", cur.getString(0));
			for (int i = 0; i < cur.getCount(); i++) {
				//ret[i] = cur.getString(0);
				if(cur.getString(0).equals(time))
					cur.moveToNext();
				else
					ret = cur.getString(0);
			}
			//return cur.getString(0);
		} 
		return ret;
	}
	
	public String[] getidfromSql(SQLiteDatabase db, String table){
		String[] col = {"_id"};
		Cursor cur = db.query(table, col, null, null, null, null, "name"+" COLLATE NOCASE");
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			String ret[] = new String[cur.getCount()];
			for (int i = 0; i < cur.getCount(); i++) {
				ret[i] = cur.getString(0);
				cur.moveToNext();
			}
			return ret;
		} else {
			return null;
		}

	}
	
	public int getnumfromSql(SQLiteDatabase db, String table){
		//String[] col = {"_id"};
		Cursor cur = db.rawQuery("SELECT COUNT(*) FROM "+table+";", null);//(table, col, null, null, null, null, "name"+" COLLATE NOCASE");
		cur.moveToFirst();
		return cur.getInt(0);
	}

	public boolean checkexistfromSql(SQLiteDatabase db, String table, String uid){
		String[] col = {"fid"};
		Cursor cur = db.query(table, col, "fid=" + "'" + uid
				+ "'", null, null, null, null);
		if (cur.getCount() > 0) {
			cur.moveToFirst();
			return true;
		} else {
			return false;
		}
	}
	
	public int getfidnumerfromSql(SQLiteDatabase db, String table){
		int ret = 0;
		
		String[] colum = { "fid"};
		Cursor c = db.query(table, colum, null, null, null, null, null);
		if (c.getCount() > 0) {
			c.moveToFirst();
			String friend_list = "";
			for (int i = 0; i < c.getCount(); i++) {
				if(!FileAccess.check_friend_exist(c.getString(0), friend_list)){
					friend_list = friend_list + c.getString(0)+"/";
					ret++;
				}
				c.moveToNext();
			}
		}
		return ret;
	}
	
}

