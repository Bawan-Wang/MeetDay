package com.project1.http;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class FileAccess {
	
	private static final int BUFFER_SIZE = 2048;
	private static ReadWriteLock rwlock = new ReentrantReadWriteLock();
	
	//private Context mContext;
	
    public static void putStringInPreferences(Context context, String value, Const.eUsrType type, String pKey) {
    	
		rwlock.writeLock().lock();
		try{
			SharedPreferences sharedPreferences = context.getSharedPreferences(pKey, Context.MODE_PRIVATE);
	        SharedPreferences.Editor editor = sharedPreferences.edit();
	    	String key = "";
	    	String data = "";
	    	switch(type){
		    	case UsrType_Email:
		    		key = "name";
		    		data = value;
		    		break;
		    	case UsrType_Pass:
		    		key = "pass";
		    		data = value;
		    		break;
		    	case UsrType_NickName:
		    		key = "nickname";
		    		data = value;
		    		break;
		    	case UsrType_SearchId:
		    		key = "searchid";
		    		data = value;
		    		break;
		    	case UsrType_Fone:
		    		key = "phone";
		    		data = value;
		    		break;	
		    	case UsrType_UserId:
		    		key = "usrid";
		    		data = value;
		    		break;		
		    	case UsrType_RegId:
		    		key = "regid";
		    		data = value;
		    		break;	
		    	case UsrType_FreindList:
		    		key = "flst";
		    		data = value;
		    		break;	
		    	case UsrType_FriendName:
		    		char front[] = new char[10];
					char latter[] = new char[20];
					//Log.d(this.getClass().getName(),strResult);
					value.getChars(0, value.indexOf('/'), front, 0);
					value.getChars(value.indexOf('/')+1, value.length(), latter, 0);
					//Const.RRet eRet = Const.RRet.valueOf(String.valueOf(tmp).trim());	
					key = "Freind_" + String.valueOf(front).trim();
					data = String.valueOf(latter).trim();
		    		break;
		    	case UsrType_HelpMsg:
		    		key = "helpmsg";
		    		data = value;
		    		break;
		    	case UsrType_ThanksMsg:
		    		key = "thanksmsg";
		    		data = value;
		    		break;
		    	case UsrType_FriendNameList:
		    		key = "friendlist";
		    		data = value;
		    		break;
		    	case UsrType_FragmentLocation:
		    		key = "flocation";
		    		data = value;
		    		break;	
    	    	case UsrType_Gmail:
    	    		key = "gmail";
    	    		data = value;
    	    		break;
    	    	case UsrType_FBId:
    	    		key = "fbid";
    	    		data = value;
    	    		break;
    	    	case UsrType_LoginType:
    	    		key = "logintype";
    	    		data = value;
    	    		break;	
    	    	case UsrType_FBList:
    	    		key = "fblist";
    	    		data = value;
    	    		break;
    	    	case UsrType_PhoneList:
    	    		key = "phonelist";
    	    		data = value;
    	    		break;
    	    	case UsrType_FB_Status:
    	    		key = "fbstat";
    	    		data = value;
    	    		break;
    	    	case UsrType_Gmail_Status:
    	    		key = "gmailstat";
    	    		data = value;
    	    		break;
	    		default :
	    			//key = String.valueOf(type);
	    			break;
	        }
	        editor.putString(key, data);
	        editor.commit();
		} finally {
			rwlock.writeLock().unlock();
		}
        
    }

    public static String getStringFromPreferences(Context context,String defaultValue, Const.eUsrType type, String pKey) {    	
    	String temp = null;
    	rwlock.writeLock().lock();
    	try{
    		SharedPreferences sharedPreferences = context.getSharedPreferences(pKey, Context.MODE_PRIVATE);
        	String key = null;
        	switch(type){
    	    	case UsrType_Email:
    	    		key = "name";
    	    		break;
    	    	case UsrType_Pass:
    	    		key = "pass";
    	    		break;
    	    	case UsrType_NickName:
    	    		key = "nickname";
    	    		break;
    	    	case UsrType_SearchId:
    	    		key = "searchid";
    	    		break;
    	    	case UsrType_Fone:
    	    		key = "phone";
    	    		break;	
    	    	case UsrType_UserId:
    	    		key = "usrid";
    	    		break;
    	    	case UsrType_RegId:
    	    		key = "regid";
    	    		break;	
    	    	case UsrType_FreindList:
    	    		key = "flst";
    	    		break;
    	    	case UsrType_FriendName:
    				key = "Freind_" + defaultValue;
    				//data = String.valueOf(latter).trim();
    	    		break;	
    	    	case UsrType_HelpMsg:
    	    		key = "helpmsg";
    	    		break;	
    	    	case UsrType_ThanksMsg:
    	    		key = "thanksmsg";
    	    		//data = value;
    	    		break;	
    	    	case UsrType_FriendNameList:
    	    		key = "friendlist";
    	    		break;	
    	    	case UsrType_FragmentLocation:
    	    		key = "flocation";
    	    		break;	
    	    	case UsrType_LoginType:
    	    		key = "logintype";
    	    		break;
    	    	case UsrType_FBList:
    	    		key = "fblist";
    	    		break;	
    	    	case UsrType_Gmail:
    	    		key = "gmail";
    	    		break;
    	    	case UsrType_FBId:
    	    		key = "fbid";
    	    		break;	
    	    	case UsrType_PhoneList:
    	    		key = "phonelist";
    	    		break;	
    	    	case UsrType_FB_Status:
    	    		key = "fbstat";
    	    		break;
    	    	case UsrType_Gmail_Status:
    	    		key = "gmailstat";
    	    		break;	
        		default :
        			key = String.valueOf(type);
        			break;
            }
        	temp = sharedPreferences.getString(key, defaultValue);           
    	} finally{
    		rwlock.writeLock().unlock();
    	}
    	return temp;
    }
    
    public static void deleteStringInPreferences(Context context,Const.eUsrType type, String pKey) {
    	
		rwlock.writeLock().lock();
		try{
			SharedPreferences sharedPreferences = context.getSharedPreferences(pKey, Context.MODE_PRIVATE);
	        SharedPreferences.Editor editor = sharedPreferences.edit();
	    	String key = "";
	    	String data = "";
	    	switch(type){
		    	case UsrType_Email:
		    		key = "name";
		    		//data = value;
		    		break;
		    	case UsrType_Pass:
		    		key = "pass";
		    		//data = value;
		    		break;
		    	case UsrType_NickName:
		    		key = "nickname";
		    		//data = value;
		    		break;
		    	case UsrType_SearchId:
		    		key = "searchid";
		    		//data = value;
		    		break;
		    	case UsrType_Fone:
		    		key = "phone";
		    		//data = value;
		    		break;	
		    	case UsrType_UserId:
		    		key = "usrid";
		    		//data = value;
		    		break;		
		    	case UsrType_RegId:
		    		key = "regid";
		    		//data = value;
		    		break;	
		    	case UsrType_FreindList:
		    		key = "flst";
		    		//data = value;
		    		break;	
		    	case UsrType_FriendName:
		    		char front[] = new char[10];
					char latter[] = new char[20];
					//Log.d(this.getClass().getName(),strResult);
					//value.getChars(0, value.indexOf('/'), front, 0);
					//value.getChars(value.indexOf('/')+1, value.length(), latter, 0);
					//Const.RRet eRet = Const.RRet.valueOf(String.valueOf(tmp).trim());	
					key = "Freind_" + String.valueOf(front).trim();
					data = String.valueOf(latter).trim();
		    		break;
		    	case UsrType_HelpMsg:
		    		key = "helpmsg";
		    		//data = value;
		    		break;
		    	case UsrType_ThanksMsg:
		    		key = "thanksmsg";
		    		//data = value;
		    		break;
		    	case UsrType_FriendNameList:
		    		key = "friendlist";
		    		//data = value;
		    		break;
		    	case UsrType_FragmentLocation:
		    		key = "flocation";
		    		//data = value;
		    		break;	
    	    	case UsrType_Gmail:
    	    		key = "gmail";
    	    		//data = value;
    	    		break;
    	    	case UsrType_FBId:
    	    		key = "fbid";
    	    		//data = value;
    	    		break;
    	    	case UsrType_LoginType:
    	    		key = "logintype";
    	    		//data = value;
    	    		break;	
    	    	case UsrType_FBList:
    	    		key = "fblist";
    	    		//data = value;
    	    		break;
    	    	case UsrType_PhoneList:
    	    		key = "phonelist";
    	    		//data = value;
    	    		break;
    	    	case UsrType_FB_Status:
    	    		key = "fbstat";
    	    		break;
    	    	case UsrType_Gmail_Status:
    	    		key = "gmailstat";
    	    		break;	
	    		default :
	    			//key = String.valueOf(type);
	    			break;
	        }
	    	editor.remove(key);
	    	editor.apply();
	        //editor.putString(key, data);
	        editor.commit();
		} finally {
			rwlock.writeLock().unlock();
		}
        
    }

    
    public static String[] parseDataToPara(String source, String symbol){
		int i = 0;
		String content = source;
		String[] para = null;
		
		int num = content.length() - content.replace(symbol, "").length();
		para = new String[num];
		
    	while(content.contains(symbol)){
			int tmp = content.indexOf(symbol);
			String str = content.substring(0, tmp);
			para[i] = str;
			content = content.substring(tmp+1);			
			Log.d("parseDataToPara",para[i]);
			i++;
		}	
    	return para;
    }
   /* public static void FriendSort(Context context, String Name_new, String fid, int num){
    	if(num == 0){
			Const.contactinfo.name_list[num] = Name_new;
			Const.contactinfo.fid_list[num] = fid;//list[num] = fid;
			putStringInPreferences(context, num + "/" +Name_new, 
					Const.eUsrType.UsrType_FriendName, Const.projinfo.sSharePreferenceName);
		} else {
			for (int i = 0; i < num ; i++){
					//Log.d(this.getClass().getName(),Integer.toString(i)+ "//" +sfr.GetUserData().compareTo(Const.name_list[i]));
					if(Name_new.toLowerCase().compareTo(Const.contactinfo.name_list[i].toLowerCase()) < 0){
						for (int j = num ; j > i ; j--){
							Const.contactinfo.name_list[j] =  Const.contactinfo.name_list[j-1];
							Const.contactinfo.fid_list[j] = Const.contactinfo.fid_list[j-1];//list[j] = list[j-1];
							//edit.remove("Freind_" + j);
							//edit.putString("Freind_" + j, Const.name_list[j-1]);
							FileAccess.putStringInPreferences(context, j + "/" +Const.contactinfo.name_list[j-1], 
									Const.eUsrType.UsrType_FriendName, Const.projinfo.sSharePreferenceName);
						}
						//String temp = Const.name_list[i];
						Const.contactinfo.name_list[i] = Name_new;
						Const.contactinfo.fid_list[i] = fid;//list[i] = fid;
						//edit.remove("Freind_" + i);
						//edit.putString("Freind_" + i, sfr.GetUserData());
						FileAccess.putStringInPreferences(context, i + "/" +Name_new, 
								Const.eUsrType.UsrType_FriendName, Const.projinfo.sSharePreferenceName);
						break;
					} else if (i == (num - 1)){
						//Log.d(this.getClass().getName(),"fsfesfwwe");
						Const.contactinfo.name_list[i + 1] = Name_new;
						Const.contactinfo.fid_list[i + 1] = fid;//list[i + 1] = fid;
						//edit.remove("Freind_" + i + 1);
						//edit.putString("Freind_" + i + 1, sfr.GetUserData());
						FileAccess.putStringInPreferences(context, i + 1 + "/" +Name_new, 
								Const.eUsrType.UsrType_FriendName, Const.projinfo.sSharePreferenceName);
					}
			}				
		}	
    	
		Const.userinfo.flst = "";
		for(int i = 0;i < num + 1; i++)
			Const.userinfo.flst = Const.userinfo.flst + Const.contactinfo.fid_list[i]+"/";
		FileAccess.putStringInPreferences(context, Const.userinfo.flst, 
    			Const.eUsrType.UsrType_FreindList, Const.projinfo.sSharePreferenceName);
		Log.d(context.getClass().getName(),Const.userinfo.flst);
    }
    */
    
    public static String getNameEmailDetails(Context context){
        ArrayList<String> names = new ArrayList<String>();
        String retlist="";
        ContentResolver cr = context.getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                Cursor cur1 = cr.query( 
                        ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                        ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
                                new String[]{id}, null); 
                while (cur1.moveToNext()) { 
                    //to get the contact names
                    String name=cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    Log.e("Name :", name);
                    String email = cur1.getString(cur1.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                    Log.e("Email", email);
                    if(email!=null){
                        names.add(name);
                        retlist = retlist + email+"/";
                    }
                } 
                cur1.close();
            }
        }
        return retlist;
    }
    
    
	public static boolean check_friend_exist(String id, String list){
		boolean ret = false;
		while(list.contains("/")){
			//ServerFuncRun sfr = new ServerFuncRun();
			int tmp = list.indexOf("/");
			//int tmpn = namelist.indexOf("/");
			String fid = list.substring(0, tmp);
			
			if(fid.compareTo(id)==0){
				ret = true;
				break;
			}			
			list = list.substring(tmp+1);
		}			
		return ret;		
	}
	
	public static String formatphone(String ...args){
		String ret ="";
		ret = args[0];
		ret = ret.trim();
		ret = ret.replaceAll("\\s+", "");
		ret = ret.replaceAll("-", "");
		ret = ret.replaceAll("\\(", "");
		ret = ret.replaceAll("\\)", "");
		if(ret.substring(0, 1).equals("0")&&args.length>1){
			ret = ret.substring(1, ret.length());
			ret = args[1]+ret;
		}
		//System.out.println("....ret..."+ret);
		return ret;
	}
	
	public static String getContactphone(ContentResolver cr, String countycode){
		
		String ret = "";
	    Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
	            null, null, null);
	    if (cur.getCount() > 0) {
	        while (cur.moveToNext()) {
	            String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
	            String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));							            
	            if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) 
	            {
	                // Query phone here. Covered next
	                Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null); 
	                while (phones.moveToNext()) {    	
                     String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                     phoneNumber = phoneNumber.replaceAll(" " ,"");
                     //Log.i("Number1", phoneNumber);
                     if(PhoneNumberUtils.isGlobalPhoneNumber(phoneNumber.trim())==true){
                    	 if(countycode.equals("tw")){               		 
                    		 phoneNumber = FileAccess.formatphone(phoneNumber,"+886");
                    	 } else {
                    		 phoneNumber = FileAccess.formatphone(phoneNumber);
                         }
                    	 Log.i("Number", phoneNumber);
                    	 ret = ret + "/" + phoneNumber;
                     }
                                                                             
                    } 
	                phones.close(); 
	            }

	        }
	    }
	    return ret;
	
	}
	
	public static void getContactphoneList(Context context, ContentResolver cr){
		getcontactInBackground(context, cr);
	}
	
	private static void getcontactInBackground(final Context context, final ContentResolver cr) {
		new AsyncTask<Void, Void, String>() {			
	        protected void onPreExecute() {
	        	//pdialog = ProgressDialog.show(WelcomPage.this, "Connecting", "Wait...",true);				
	        }
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {			
					if(Looper.myLooper() == null)
						Looper.prepare();
					String list = FileAccess.getContactphone(cr, Const.getlocalcode(context));
					
					if(!FileAccess.getStringFromPreferences(context, 
							"", Const.eUsrType.UsrType_PhoneList, 
							Const.projinfo.sSharePreferenceName).equals(list)){
						FileAccess.putStringInPreferences(context, 
								list, Const.eUsrType.UsrType_PhoneList, 
								Const.projinfo.sSharePreferenceName);
						Const.refresh_friends |= 0x02;
						//System.out.println(Const.refresh_friends);
					}
					
				} catch (Exception ex) {
					msg = "Error :" + ex.getMessage();
					Log.d(this.getClass().getName(), "Error: " + msg);
				}
				Log.d(this.getClass().getName(), "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				//Toast.makeText(getApplicationContext(),
					//	"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
						//.show();

			}
		}.execute(null, null, null);
	}
	
    public static void convertByteArrayToFile(byte[] array, File file) {
        FileOutputStream outStream = null;
      try {
        outStream = new FileOutputStream(file);
        outStream.write(array);
        outStream.close();
      }
      catch (IOException e) {
        Log.e("convertByteArrayToFile","Error converting ByteArray to File\n e="+e);
        e.printStackTrace();
      }
      finally {
        try {
            outStream.close();
        } catch (IOException e) {
            Log.e("convertFileToByteArray","Error closing inputStream\n     e="+e);
            e.printStackTrace();
        }
      }
    }
    
    public static byte[] convertFileToByteArray(File f) {
        byte[] byteArray = null;
        InputStream inputStream = null;

        try {
          inputStream = new FileInputStream(f);
          ByteArrayOutputStream bos = new ByteArrayOutputStream();
          byte[] b = new byte[BUFFER_SIZE];
          int bytesRead = 0;

          while ((bytesRead = inputStream.read(b)) != -1)
          {
              bos.write(b, 0, bytesRead);
          }

          byteArray = bos.toByteArray();
          //inputStream.close();
          Log.d("FileAccess", "+++ convertFileToByteArray");
        }
        catch (IOException e) {
          Log.e("convertFileToByteArray","Error converting file\n e="+e);
          e.printStackTrace();
        }
        finally {
          try {
              inputStream.close();
          } catch (IOException e) {
              Log.e("convertFileToByteArray","Error closing inputStream\n     e="+e);
              e.printStackTrace();
          }
        }

        return byteArray;
      }
    
    public static void deletefile(String path){
    	File file = new File(path);
    	boolean deleted = file.delete();
    }
    
    public static FileOutputStream saveToFile(String filename,InputStream ip) {
        try {
      	  //System.out.println(filename);
            FileOutputStream out = new FileOutputStream(filename);
            //bmp.compress(CompressFormat.PNG, 100, out);
            
            int read = 0;
    		  byte[] bytes = new byte[1024];

    		  while ((read = ip.read(bytes)) != -1) {
    			  out.write(bytes, 0, read);
    		  }
            
            out.flush();
            out.close();
            return out;
        } catch(Exception e) {
      	  return null;
        }
        
    }
    
    public static void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }
    
    public static void clearApplicationData(Context con) {
        File cache = con.getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    FileAccess.deleteDir(new File(appDir, s));
                    Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" + s + " DELETED *******************");
                }
            }
        }
    }
    
    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }

        return dir.delete();
    }
    
    public static boolean isString(String s) {
	  return s.matches("^[a-zA-Z]*");
	 }
    
    public static boolean isStringorDigit(String s) {
//	  return s.matches("^[a-zA-Z1-9]*");
		return s.matches("^[a-zA-Z][a-zA-Z0-9_]{5,19}$");
	 }
}
