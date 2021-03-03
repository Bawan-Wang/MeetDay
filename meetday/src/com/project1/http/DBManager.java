package com.project1.http;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.R;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

public class DBManager {  
	  
	 private final int BUFFER_SIZE = 400000;  
	 public static final String DB_NAME = "ttt.sql"; // �O�s����Ʈw�ɮצW  
	 public static final String PACKAGE_NAME = "tw.android";  
	 public static final String DB_PATH = PictUtil.getSavePath().getAbsolutePath();//"/data"  
	   //+ Environment.getDataDirectory().getAbsolutePath() + "/"  
	   //+ PACKAGE_NAME;// �b����̦s���Ʈw����m(/data/data/tw.android/db2.db)  
	  
	 private SQLiteDatabase database;  
	 private Context context;  
	  
	 public DBManager(Context coNtext) {  
	  this.context = coNtext;  
	 }  
	  
	 public SQLiteDatabase getDatabase() {  
	  return database;  
	 }  
	  
	 public void setDatabase(SQLiteDatabase database) {  
	  this.database = database;  
	 }  
	  
	 public void openDatabase() {  
	  this.database = this.openDatabase(DB_PATH + "/" + Const.projinfo.sExpertTableFile);  
	 }  
	  
	 private SQLiteDatabase openDatabase(String dbfile) {  
	  
	  //try {  
	   if (!(new File(dbfile).exists())) {  
	    Log.i("have db2????????", "no");  
	    // �P�_��Ʈw�ɮ׬O�_�s�b�A�Y���s�b�h����ɤJ�A�_�h�������}��Ʈw  
	    /*ZInputStream is = this.context.getResources().openRawResource(  
	      R.raw.db2); // ���ɤJ����Ʈw  
	    FileOutputStream fos = new FileOutputStream(dbfile);  
	    byte[] buffer = new byte[BUFFER_SIZE];  
	    int count = 0;  
	    while ((count = is.read(buffer)) > 0) {  
	     fos.write(buffer, 0, count);  
	    }  
	    fos.close();  
	    is.close();  */
	   }  
	   SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(dbfile,  
	     null);  
	   return db;  
	 }  
	  
	 public void closeDatabase() {  
	  this.database.close();  
	  
	 }  
	}  
