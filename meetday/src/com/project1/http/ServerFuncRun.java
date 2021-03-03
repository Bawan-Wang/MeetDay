package com.project1.http;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;
//import org.apache.http.protocol.HTTP;
//import org.apache.http.util.EntityUtils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
//import com.givemepass.progressdialogdemo.ProgressDialogDemo;
//import java.util.Arrays;
//import android.util.Log;

//import android.content.Context;
//import android.content.SharedPreferences;

public class ServerFuncRun implements Runnable{

	//private String loginuriAPI =Const.sServerAdr+"LoginServlet";
	private String loginuriAPI;// =Const.projinfo.sServerAdr+Const.projinfo.sServerDBFunc;
	private String UserData = null;
	private Const.RRet Servret = null;
	public String name = null;
	public String pass = null;
	public String phone = null;
	public String nick = null;
	public String data = null;
	public String usr_id = null;
	public String nameadded = null;
	public String search_id = null;
	public String imageStr = null;
	public String rec_id = null;
	public String message = null;
	public String fidlist = null;
	public String loc_id = null;
	public String url = null;
	public Const.Command cmd = null;
	public Const.eUsrType datatype = null;
	public String dstaddr = null;
	public String smbody = null;
	//public Context context = null;
	//username=帳號&password=密碼&dstaddr=0933853653&
	//smbody=簡訊王api簡訊測試 &response=http://回報網址/response.php
	
	public ServerFuncRun(){

	}

    private static InputStream getImageStream(String path) throws Exception{
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
            return conn.getInputStream();
        }
        return null;
    }
    
    private String postStringMerge(String strParamName, String strParamValue) {
    	String strTmpParam = "";
    	
    	if(null != strParamName && null != strParamValue) {
			try {
		        if(strParamName.equals("cmd")) {
				    strTmpParam += URLEncoder.encode(strParamName, "UTF-8") + "=" +URLEncoder.encode(strParamValue, "UTF-8");
		        }else if(strParamName.equals("dstaddr")/*||strParamName.equals("smbody")*/){
				    strTmpParam += "&" +  strParamName + "=" +strParamValue;
		        }else if(strParamName.equals("smbody")){
				    strTmpParam += "&" +  URLEncoder.encode(strParamName, "UTF-8") + "=" +URLEncoder.encode(strParamValue, "BIG-5");
		        }else{
				    strTmpParam += "&" +  URLEncoder.encode(strParamName, "UTF-8") + "=" +URLEncoder.encode(strParamValue, "UTF-8");
		        }
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	
    	return strTmpParam;
    }

    private static String getStringFromInputStream(InputStream is)
            throws IOException {
	    ByteArrayOutputStream os = new ByteArrayOutputStream();
	    // 璅⊥隞�� 敹◆��
	    byte[] buffer = new byte[1024];
	    int len = -1;
	    // 銝��閬�len=is.read(buffer)
	    // 憒�while((is.read(buffer))!=-1)��瘜��唳�buffer銝�
	    while ((len = is.read(buffer)) != -1) {
	            os.write(buffer, 0, len);
	    }
	    is.close();
	    String state = os.toString();// ��銝剔��唳頧祆��蝚虫葡,����utf-8(璅⊥��券�霈斤���
	    os.close();
	    return state;
    }    
    
	private String sendDataToInternet(){
		String parameters = "";
//		HttpPost httpRequest = new HttpPost(loginuriAPI);
//		List<NameValuePair> params = new ArrayList<NameValuePair>();
		
//		params.add(new BasicNameValuePair("cmd",cmd.toString()));
//		parameters+=postStringMerge("cmd", cmd.toString());		
		if(cmd == Const.Command.Cmd_SendSMS_Auth){
			loginuriAPI = Const.projinfo.sSMSrAdr;
			parameters+=postStringMerge("username",Const.projinfo.sSMSUsername);
			parameters+=postStringMerge("password",Const.projinfo.sSMSPass);
		} else {
			loginuriAPI = Const.projinfo.sServerAdr+Const.projinfo.sServerDBFunc;
			parameters+=postStringMerge("cmd", cmd.toString());
		}
		if(name!=null){
//			params.add(new BasicNameValuePair("name",name));
			parameters+=postStringMerge("name",name);
		}
		if(pass!=null){
//			params.add(new BasicNameValuePair("pass",pass));
			parameters+=postStringMerge("pass",pass);
		}
		if(phone!=null){
//			params.add(new BasicNameValuePair("phone",phone));
			parameters+=postStringMerge("phone",phone);
		}
		if(nick!=null){
//			params.add(new BasicNameValuePair("nick",nick));
			parameters+=postStringMerge("nick",nick);
		}
		if(data!=null){
//			params.add(new BasicNameValuePair("data",data));
			parameters+=postStringMerge("data",data);
		}
		if(usr_id!=null){
//			params.add(new BasicNameValuePair("id",usr_id));
			parameters+=postStringMerge("id",usr_id);
		}
		if(search_id!=null){
//			params.add(new BasicNameValuePair("search_id",search_id));
			parameters+=postStringMerge("search_id",search_id);
		}
		if(nameadded!=null){
//			params.add(new BasicNameValuePair("nameadded",nameadded));
			parameters+=postStringMerge("nameadded",nameadded);
		}
		if(imageStr!=null){
//			params.add(new BasicNameValuePair("image",imageStr));
			parameters+=postStringMerge("image",imageStr);
		}
		if(datatype!=null){
//			params.add(new BasicNameValuePair("datatype",datatype.toString()));
			parameters+=postStringMerge("datatype",datatype.toString());
		}
		if(rec_id!=null){
//			params.add(new BasicNameValuePair("rec_id",rec_id));
			parameters+=postStringMerge("rec_id",rec_id);			
		}
		if(message!=null){
//			params.add(new BasicNameValuePair("message",message));
			parameters+=postStringMerge("message",message);				
		}
		if(fidlist!=null){
//			params.add(new BasicNameValuePair("fidlist",fidlist));
			parameters+=postStringMerge("fidlist",fidlist);					
		}
		if(loc_id!=null){
//			params.add(new BasicNameValuePair("loc_id",loc_id));
			parameters+=postStringMerge("loc_id",loc_id);				
		}
		if(url!=null){
//			params.add(new BasicNameValuePair("url",url));
			parameters+=postStringMerge("url",url);				
		}
		if(dstaddr!=null){
//			params.add(new BasicNameValuePair("url",url));
			parameters+=postStringMerge("dstaddr",dstaddr);				
		}
		if(smbody!=null){
//			params.add(new BasicNameValuePair("url",url));
			parameters+=postStringMerge("smbody",smbody);				
		}
//		Log.d(this.getClass().getName(),"post 1" + cmd.toString() + params.toString());
		Log.d(this.getClass().getName(),"post 2" + cmd.toString() + parameters);	
		try{
			URL mURL = new URL(loginuriAPI);
		      HttpURLConnection URLConn = (HttpURLConnection) mURL.openConnection(); 			
              URLConn.setDoOutput(true); 
		      URLConn.setDoInput(true); 
		      ((HttpURLConnection) URLConn).setRequestMethod("POST"); 
		      URLConn.setUseCaches(false); 
		      URLConn.setAllowUserInteraction(true); 
		      HttpURLConnection.setFollowRedirects(true); 
		      URLConn.setInstanceFollowRedirects(true); 
		      URLConn 
	          .setRequestProperty( 
	              "User-agent", 
	              "Mozilla/5.0 (Windows; U; Windows NT 6.0; zh-TW; rv:1.9.1.2) " 
	                  + "Gecko/20090729 Firefox/3.5.2 GTB5 (.NET CLR 3.5.30729)"); 
		      URLConn 
		          .setRequestProperty("Accept", 
		              "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8"); 
		      URLConn.setRequestProperty("Accept-Language", 
		          "zh-tw,en-us;q=0.7,en;q=0.3"); 
		      URLConn.setRequestProperty("Accept-Charse", 
		          "Big5,utf-8;q=0.7,*;q=0.7");		  
		      URLConn.setRequestProperty("Content-Type", 
		              "application/x-www-form-urlencoded");
		      //URLConn.setRequestProperty("Content-Length", String.valueOf(parameters 
		              //.getBytes().length)); 		
		      java.io.DataOutputStream dos = new java.io.DataOutputStream(URLConn 
		              .getOutputStream()); 
		          dos.writeBytes(parameters); 		                  
            int responseCode = URLConn.getResponseCode();
            Log.d(this.getClass().getName(),"responseCode " + responseCode);
//			httpRequest.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
//			HttpResponse httpResponse = new DefaultHttpClient().execute(httpRequest);
			
//			if(httpResponse.getStatusLine().getStatusCode()==200){
			if (responseCode == 200) {
				InputStream is = URLConn.getInputStream();
                String strResult = getStringFromInputStream(is);				
			    Log.d(this.getClass().getName(),"reponse " + strResult);
//				String strResult = EntityUtils.toString(httpResponse.getEntity(),"UTF_8");
				//String strResult1 = new String(strResult.getBytes("ISO-8859-1"), "UTF_8");

				char tmp[] = new char[30];
				char lst[] = new char[strResult.length()];
				//Log.d(this.getClass().getName(),strResult1);
				strResult.getChars(0, strResult.indexOf('%'), tmp, 0);
				strResult.getChars(strResult.indexOf('%')+1, strResult.length(), lst, 0);
				Const.RRet eRet = Const.RRet.valueOf(String.valueOf(tmp).trim());				
				//Toast.makeText(getApplicationContext(), strResult, Toast.LENGTH_SHORT).show();
				Servret = eRet;
				switch(cmd){
				case Cmd_UpdateUsrData:
					if(eRet==Const.RRet.RRET_SUCCESS){
						//System.out.println(eRet);
						//FuncResult = true;
					} else {
						//FuncResult = false;
					}
					break;	
				case Cmd_AddFriendList:	
				case Cmd_GetUsrDataList:	
				case Cmd_GetUsrData:
				case Cmd_AddFriend:
				case Cmd_DoSearch:	
				case Cmd_GetLoginData:	
				case Cmd_GetExpertList:		
				case Cmd_Register:	
				case Cmd_Register_Type:
				case Cmd_Login_Type:
				case Cmd_Login:	
					UserData = String.valueOf(lst).trim();
					break;	
				case Cmd_ForgetPwd:
					if(eRet==Const.RRet.RRET_SUCCESS){
						//FuncResult = true;
					}
					break;	
				case Cmd_SendSMS_Auth:
					Servret = Const.RRet.RRET_SUCCESS;
					break;
				case Cmd_UpdatePhoto:
				case Cmd_SendPushToOther:
				default :
					break;
				}
				return strResult;
			} else {
				Servret = Const.RRet.RRET_FAIL;
//				Log.d(this.getClass().getName(),String.valueOf(httpResponse.getStatusLine().getStatusCode()));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return null;
	}
	
	public String GetUserData(){
		return UserData;
	}
	
	
	public Const.RRet GetServResult(){
		return Servret;
	}
	
	public void run(){
		//String result = null;
		//dialog = ProgressDialog.show(context, "Connecting", "Wait...",true);
		if(cmd==Const.Command.Cmd_GetUsrPhoto){
			try {
				Bitmap mBitmap = BitmapFactory.decodeStream(getImageStream
						(/*Const.projinfo.sServerAdr+"upload/user_"+usr_id+".jpg"*/url));
				if(mBitmap!=null){
					PictUtil.saveToFile(Const.localProfilePicPath, mBitmap);
					Servret = Const.RRet.RRET_SUCCESS;
				} else {
					Servret = Const.RRet.RRET_FAIL;
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if(cmd==Const.Command.Cmd_GetExpertTable){
			try {
				InputStream ip = getImageStream(Const.projinfo.sServerAdr+"upload/"+Const.projinfo.sExpertTableFile);
				FileAccess.saveToFile(PictUtil.getSavePath().getAbsolutePath()+"/"+Const.projinfo.sExpertTableFile, ip);
				Servret = Const.RRet.RRET_SUCCESS;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Servret = Const.RRet.RRET_FAIL;
			}
		}else {
			sendDataToInternet();
		}		
		//System.out.println(result);
	}
	
	public void DoServerFunc(){
		Thread t = new Thread(this);		
		t.start();
		int retry = 10;
		switch(cmd){
		case Cmd_AddFriendList:	
			retry = 500;
			break;
		case Cmd_ForgetPwd:
		case Cmd_Login:	
		case Cmd_Login_Type:
		case Cmd_Register_Type:
		case Cmd_GetUsrData:
		case Cmd_SendPushToOther:			
			retry = 20;
			break;
		case Cmd_UpdatePhoto:
		case Cmd_GetUsrPhoto:	
		case Cmd_UpdateUsrData:
		case Cmd_GetUsrDataList:
		case Cmd_GetLoginData:		
			retry = 30;
			break;		
		case Cmd_Register:
		case Cmd_Logout:
		case Cmd_AddFriend:
		case Cmd_ChangePwd:		
		case Cmd_DoSearch:		
		case Cmd_GetExpertList:	
		case Cmd_SendSMS_Auth:
			break;	
		default:
			return;
		}
		do{
			try {
				Thread.sleep(500);
				retry--;
			} catch (InterruptedException e) {}					
		}while(retry >0 && Servret == null);
		//dialog.dismiss();
		if(Servret == null)
			Servret = Const.RRet.RRET_FAIL;
		Log.d(this.getClass().getName(),Integer.toString(retry));
	}
	
    public String doPost(String url, String param)
    {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        try
        {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl
                    .openConnection();
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setUseCaches(false);
            conn.setDoOutput(true);
            conn.setDoInput(true);

            if (param != null && !param.trim().equals(""))
            {
                out = new PrintWriter(conn.getOutputStream());
                out.print(param);
                out.flush();
            }
            in = new BufferedReader(
                    new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null)
            {
                result += line;
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
                if (in != null)
                {
                    in.close();
                }
            } catch (IOException ex)
            {
                ex.printStackTrace();
            }
        }
        return result;
    }	
	
}

