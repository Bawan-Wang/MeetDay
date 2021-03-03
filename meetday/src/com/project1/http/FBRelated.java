package com.project1.http;

import java.util.ArrayList;
import java.util.Arrays;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.project1.meetday.ConnectToServer;
import com.project1.meetday.LoginPage;
import com.project1.meetday.SignInWithSocial;
import com.project1.meetday.SignUpPage;
import com.project1.meetday.StartPage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class FBRelated {

	private static FBRelated instance;
	private Activity uiActivity;
	private FBData fbdata;
	private String photourl;
	private String userid,passwd,nickname,account,redirect;
	private Activity askact;
	private CallbackManager callbackManager;
	private AccessToken accessToken;

	public FBRelated(Activity askact){
		this.askact = askact;
		callbackManager = CallbackManager.Factory.create();
	}
	
	public class FBData{
		public  String photourl;
		public  String userid;
		public  String passwd;
		public  String nickname;
		public  String account;
		public  String redirect;
	}
	
	public void SetSignUpPara(String userid, String nickname, String account, String pass, String fotourl){
		this.userid = userid;
		this.nickname = nickname;
		this.account = account;
		this.passwd = pass;
		this.photourl = fotourl;
	}
	
	public void SetLoginPara(String account, String pass, String redirect){
		this.account = account;
		this.passwd = pass;
		this.redirect = redirect;
	}

	
	public static int GetFBFnum(String symbol, String content){
		return (content.length() - content.replace(symbol, "").length());
	}
	
	public static void AddFBFriend(Context context, String FBList, String uid){
		if(FBList!=null){
			ConnectToServer con = new ConnectToServer(context);
			con.DoAddFriendList(uid, FBList, Const.eUsrType.UsrType_FBId);	
		}
	}
	
	public  static void GetFBFIDList(final Context context){
		//this.context = context;
		new GraphRequest(
        		AccessToken.getCurrentAccessToken(),
                "/me/friends",//"/914102968611001/friendlists",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
                        /* handle the result */
                    	 try {
							 JSONArray rawName = null;
							 JSONObject responseJSONObject = response.getJSONObject();
							 if(null != responseJSONObject){
								 rawName = response.getJSONObject().getJSONArray("data");
							 }else{
								 Log.e(this.getClass().getName(),"responseJSONObject null");
								 return;
							 }
                             try {
                            	 JSONArray friendslist = new JSONArray(rawName.toString());
                            	  ArrayList<String> friends = new ArrayList<String>();
                            	  String FBFriendList = "";
                            	  Log.d(this.getClass().getName(),String.valueOf(friendslist.length()));
                                 for (int l=0; l < friendslist.length(); l++) {
                                	 friends.add(friendslist.getJSONObject(l).getString("id"));
                                	  //Log.d("XXXXX",friendslist.getJSONObject(l).getString("name"));
                                	  //Log.d("XXXXX",friendslist.getJSONObject(l).getString("id"));
                                	  FBFriendList = FBFriendList + friendslist.getJSONObject(l).getString("id")+"/";
                                 }

                                 if(!FBFriendList.equals(FileAccess.getStringFromPreferences(context, null, 
				    			 Const.eUsrType.UsrType_FBList, Const.projinfo.sSharePreferenceName))){
                                     FileAccess.putStringInPreferences(context, FBFriendList, 
              			        			Const.eUsrType.UsrType_FBList, Const.projinfo.sSharePreferenceName);
                                     Const.refresh_friends = Const.refresh_friends|0x01;
                                 }
                                                               

                             } catch (JSONException e) {
                                 e.printStackTrace();
                             }
                         } catch (JSONException e) {
                             e.printStackTrace();
                         }
                    	 
                         
                    }
                }
            ).executeAsync();
	}
	
	public void doFBConnect(){
		if (Const.isNetworkConnected(askact)){
			LoginManager.getInstance().logInWithReadPermissions(askact, Arrays.asList("public_profile", "user_friends", "email"));
			facebookLoginFlow();
		} else {
			Toast.makeText(askact, "You are offline!", Toast.LENGTH_LONG).show();
		}
	}	
	

	private void facebookLoginFlow() {
		LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

			@Override
			public void onSuccess(LoginResult loginResult) {

				accessToken = loginResult.getAccessToken();

				Log.d("FB", "access token got.");

				//send request and call graph api

				GraphRequest request = GraphRequest.newMeRequest(
						accessToken,
						new GraphRequest.GraphJSONObjectCallback() {

							@Override
							public void onCompleted(JSONObject object, GraphResponse response) {

								ConnectToServer con = new ConnectToServer(askact);

								String personPhotoUrl = "https://graph.facebook.com/" + object.optString("id")+"/picture?type=large&width=230";
								String reg_name;

								//FBRelated.GetFBFIDList(context);

								Log.d("FB", "complete");
								Log.d("FB", object.optString("name"));
								Log.d("FB", object.optString("email"));
								Log.d("FB", object.optString("id"));
								
								Intent broadcastIntent  = new Intent();
								
								if (object.has("email")){
									reg_name = object.optString("email");
								} else {
									Log.d("FB", "No email, Reg in With FB ID");
									reg_name = object.optString("id");
								}
								broadcastIntent.setAction(StartPage.BROADCAST_GETHELPRESPONSE);		
								broadcastIntent.putExtra(StartPage.FB_ID, object.optString("id"));					
								broadcastIntent.putExtra(StartPage.Start_type, "FB");
								broadcastIntent.putExtra(StartPage.Start_email, reg_name);
								broadcastIntent.putExtra(StartPage.Start_personName, object.optString("name"));
								broadcastIntent.putExtra(StartPage.Start_personPhotoUrl, personPhotoUrl);
								askact.sendBroadcast(broadcastIntent);	
							}
						});

				Bundle parameters = new Bundle();
				parameters.putString("fields", "id,name,link,email");
				request.setParameters(parameters);
				request.executeAsync();
			}

			@Override
			public void onCancel() {
				// App code

				Log.d("FB", "CANCEL");
			}


			@Override
			public void onError(FacebookException exception) {
				// App code

				Log.d("FB", exception.toString());
			}
		});
	}
	
}
