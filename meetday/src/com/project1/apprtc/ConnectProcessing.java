package com.project1.apprtc;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.webkit.URLUtil;

import com.project1.http.Const;
import com.project1.meetday.CallActivity;
import com.project1.meetday.R;

public class ConnectProcessing {

  private final String TAG = "ConnectPrcessing";
	private static Activity srcActivity;

	private static final int CONNECTION_REQUEST = 1;
  private static final int REMOVE_FAVORITE_INDEX = 0;
	private static boolean commandLineRun = false;

  private SharedPreferences sharedPref;
  private SharedPreferences.Editor sharedPrefEditor;
  private String keyprefVideoCallEnabled;
  private String keyprefCamera2;
  private String keyprefResolution;
  private String keyprefFps;
  private String keyprefCaptureQualitySlider;
  private String keyprefVideoBitrateType;
  private String keyprefVideoBitrateValue;
  private String keyprefVideoCodec;
  private String keyprefAudioBitrateType;
  private String keyprefAudioBitrateValue;
  private String keyprefAudioCodec;
  private String keyprefHwCodecAcceleration;
  private String keyprefCaptureToTexture;
  private String keyprefNoAudioProcessingPipeline;
  private String keyprefAecDump;
  private String keyprefOpenSLES;
  private String keyprefDisableBuiltInAec;
  private String keyprefDisableBuiltInAgc;
  private String keyprefDisableBuiltInNs;
  private String keyprefDisplayHud;
  private String keyprefTracing;
  private String keyprefRoomServerUrl;
  private String keyprefRoom;

  public ConnectProcessing(Context context) {
    // Get setting keys.
    srcActivity = (Activity) context;
    PreferenceManager.setDefaultValues(context, R.xml.preferences, false);
    sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
    sharedPrefEditor = sharedPref.edit();
    keyprefVideoCallEnabled = srcActivity.getResources().getString(R.string.pref_videocall_key);
    keyprefCamera2 = srcActivity.getResources().getString(R.string.pref_camera2_key);
    keyprefResolution = srcActivity.getResources().getString(R.string.pref_resolution_key);
    keyprefFps = srcActivity.getResources().getString(R.string.pref_fps_key);
    keyprefCaptureQualitySlider = srcActivity.getResources().getString(R.string.pref_capturequalityslider_key);
    keyprefVideoBitrateType = srcActivity.getResources().getString(R.string.pref_startvideobitrate_key);
    keyprefVideoBitrateValue = srcActivity.getResources().getString(R.string.pref_startvideobitratevalue_key);
    keyprefVideoCodec = srcActivity.getResources().getString(R.string.pref_videocodec_key);
    keyprefHwCodecAcceleration = srcActivity.getResources().getString(R.string.pref_hwcodec_key);
    keyprefCaptureToTexture = srcActivity.getResources().getString(R.string.pref_capturetotexture_key);
    keyprefAudioBitrateType = srcActivity.getResources().getString(R.string.pref_startaudiobitrate_key);
    keyprefAudioBitrateValue = srcActivity.getResources().getString(R.string.pref_startaudiobitratevalue_key);
    keyprefAudioCodec = srcActivity.getResources().getString(R.string.pref_audiocodec_key);
    keyprefNoAudioProcessingPipeline = srcActivity.getResources().getString(R.string.pref_noaudioprocessing_key);
    keyprefAecDump = srcActivity.getResources().getString(R.string.pref_aecdump_key);
    keyprefOpenSLES = srcActivity.getResources().getString(R.string.pref_opensles_key);
    keyprefDisableBuiltInAec = srcActivity.getResources().getString(R.string.pref_disable_built_in_aec_key);
    keyprefDisableBuiltInAgc = srcActivity.getResources().getString(R.string.pref_disable_built_in_agc_key);
    keyprefDisableBuiltInNs = srcActivity.getResources().getString(R.string.pref_disable_built_in_ns_key);
    keyprefDisplayHud = srcActivity.getResources().getString(R.string.pref_displayhud_key);
    keyprefTracing = srcActivity.getResources().getString(R.string.pref_tracing_key);
    keyprefRoomServerUrl = srcActivity.getResources().getString(R.string.pref_room_server_url_key);
    keyprefRoom = srcActivity.getResources().getString(R.string.pref_room_key);
	}

	public void connectToRoom(String roomId, int runTimeMs, int activityCallType, Const.Connect_Info connect) {
    boolean isCaller = (activityCallType == 1);

    // Get room name (random for loopback).
//	    if (loopback) {
//	      roomId = Integer.toString((new Random()).nextInt(100000000));
//	    }

    //Log.d(this.getClass().getName(), "XXXXXXX");
//    String roomUrl = sharedPref.getString(
//        keyprefRoomServerUrl,
//        srcActivity.getResources().getString(R.string.pref_room_server_url_default));
//    String roomUrl = "https://apprtc.appspot.com";
      String roomUrl = "https://meetday-sylapp.appspot.com";

    // Video call enabled flag.
    boolean videoCallEnabled = sharedPref.getBoolean(keyprefVideoCallEnabled,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_videocall_default)));

    // Use Camera2 option.
    boolean useCamera2 = sharedPref.getBoolean(keyprefCamera2,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_camera2_default)));

    // Get default codecs.
    String videoCodec = sharedPref.getString(keyprefVideoCodec,
        srcActivity.getResources().getString(R.string.pref_videocodec_default));
    String audioCodec = sharedPref.getString(keyprefAudioCodec,
        srcActivity.getResources().getString(R.string.pref_audiocodec_default));

    // Check HW codec flag.
    boolean hwCodec = sharedPref.getBoolean(keyprefHwCodecAcceleration,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_hwcodec_default)));

    // Check Capture to texture.
    boolean captureToTexture = sharedPref.getBoolean(keyprefCaptureToTexture,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_capturetotexture_default)));
    if(captureToTexture == false){
        Log.e(TAG, "Update from old version? \"captureToTexture\" in SharedPreferences is false");
        Log.e(TAG, "Reset \"captureToTexture\" to true");
        sharedPrefEditor.putBoolean(keyprefCaptureToTexture, true);
        sharedPrefEditor.commit();
        captureToTexture = sharedPref.getBoolean(keyprefCaptureToTexture,
            Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_capturetotexture_default)));
    }

    // Check Disable Audio Processing flag.
    boolean noAudioProcessing = sharedPref.getBoolean(
        keyprefNoAudioProcessingPipeline,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_noaudioprocessing_default)));

    // Check Disable Audio Processing flag.
    boolean aecDump = sharedPref.getBoolean(
        keyprefAecDump,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_aecdump_default)));

    // Check OpenSL ES enabled flag.
    boolean useOpenSLES = sharedPref.getBoolean(
        keyprefOpenSLES,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_opensles_default)));

    // Check Disable built-in AEC flag.
    boolean disableBuiltInAEC = sharedPref.getBoolean(
        keyprefDisableBuiltInAec,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_disable_built_in_aec_default)));

    // Check Disable built-in AGC flag.
    boolean disableBuiltInAGC = sharedPref.getBoolean(
        keyprefDisableBuiltInAgc,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_disable_built_in_agc_default)));

    // Check Disable built-in NS flag.
    boolean disableBuiltInNS = sharedPref.getBoolean(
        keyprefDisableBuiltInNs,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_disable_built_in_ns_default)));

    // Get video resolution from settings.
    int videoWidth = 0;
    int videoHeight = 0;
    String resolution = sharedPref.getString(keyprefResolution,
        srcActivity.getResources().getString(R.string.pref_resolution_default));
    String[] dimensions = resolution.split("[ x]+");
    if (dimensions.length == 2) {
      try {
        videoWidth = Integer.parseInt(dimensions[0]);
        videoHeight = Integer.parseInt(dimensions[1]);
      } catch (NumberFormatException e) {
        videoWidth = 0;
        videoHeight = 0;
        Log.e(TAG, "Wrong video resolution setting: " + resolution);
      }
    }

    // Get camera fps from settings.
    int cameraFps = 0;
    String fps = sharedPref.getString(keyprefFps,
        srcActivity.getResources().getString(R.string.pref_fps_default));
    String[] fpsValues = fps.split("[ x]+");
    if (fpsValues.length == 2) {
      try {
        cameraFps = Integer.parseInt(fpsValues[0]);
      } catch (NumberFormatException e) {
        Log.e(TAG, "Wrong camera fps setting: " + fps);
      }
    }

    // Check capture quality slider flag.
    boolean captureQualitySlider = sharedPref.getBoolean(keyprefCaptureQualitySlider,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_capturequalityslider_default)));

    // Get video and audio start bitrate.
    int videoStartBitrate = 0;
    String bitrateTypeDefault = srcActivity.getResources().getString(
        R.string.pref_startvideobitrate_default);
    String bitrateType = sharedPref.getString(
        keyprefVideoBitrateType, bitrateTypeDefault);
    if (!bitrateType.equals(bitrateTypeDefault)) {
      String bitrateValue = sharedPref.getString(keyprefVideoBitrateValue,
          srcActivity.getResources().getString(R.string.pref_startvideobitratevalue_default));
      videoStartBitrate = Integer.parseInt(bitrateValue);
    }
    int audioStartBitrate = 0;
    bitrateTypeDefault = srcActivity.getResources().getString(R.string.pref_startaudiobitrate_default);
    bitrateType = sharedPref.getString(
        keyprefAudioBitrateType, bitrateTypeDefault);
    if (!bitrateType.equals(bitrateTypeDefault)) {
      String bitrateValue = sharedPref.getString(keyprefAudioBitrateValue,
          srcActivity.getResources().getString(R.string.pref_startaudiobitratevalue_default));
      audioStartBitrate = Integer.parseInt(bitrateValue);
    }

    // Check statistics display option.
    boolean displayHud = sharedPref.getBoolean(keyprefDisplayHud,
        Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_displayhud_default)));

    boolean tracing = sharedPref.getBoolean(
        keyprefTracing, Boolean.valueOf(srcActivity.getResources().getString(R.string.pref_tracing_default)));

    // Start AppRTCDemo activity.
    Log.d(TAG, "Connecting to room " + roomId + " at URL " + roomUrl);
    if (validateUrl(roomUrl)) {
      Uri uri = Uri.parse(roomUrl);
      Log.d(TAG, "Create intent");
      Intent intent = new Intent(srcActivity, CallActivity.class);
      intent.setData(uri);
      //Bundle bundle = new Bundle();
      //bundle.putParcelable(CallActivity.EXTRA_CONNECTINFO, (Parcelable) connect);
      //Log.d(this.getClass().getName(), "affd");
      intent.putExtra(CallActivity.EXTRA_CONNECTINFO, (Parcelable)connect);
      Log.d(this.getClass().getName(), connect.roomid);
      intent.putExtra(CallActivity.EXTRA_ISROLEPLAY, isCaller);
      intent.putExtra(CallActivity.EXTRA_ROOMID, roomId);
      //intent.putExtra("Connect", new Const.Connect_Info(connect.local_size, connect.client_size));
//	      intent.putExtra(CallActivity.EXTRA_LOOPBACK, loopback);
      intent.putExtra(CallActivity.EXTRA_VIDEO_CALL, videoCallEnabled);
      intent.putExtra(CallActivity.EXTRA_CAMERA2, useCamera2);
      intent.putExtra(CallActivity.EXTRA_VIDEO_WIDTH, videoWidth);
      intent.putExtra(CallActivity.EXTRA_VIDEO_HEIGHT, videoHeight);
      intent.putExtra(CallActivity.EXTRA_VIDEO_FPS, cameraFps);
      intent.putExtra(CallActivity.EXTRA_VIDEO_CAPTUREQUALITYSLIDER_ENABLED,
          captureQualitySlider);
      intent.putExtra(CallActivity.EXTRA_VIDEO_BITRATE, videoStartBitrate);
      intent.putExtra(CallActivity.EXTRA_VIDEOCODEC, videoCodec);
      intent.putExtra(CallActivity.EXTRA_HWCODEC_ENABLED, hwCodec);
      intent.putExtra(CallActivity.EXTRA_CAPTURETOTEXTURE_ENABLED, captureToTexture);
      intent.putExtra(CallActivity.EXTRA_NOAUDIOPROCESSING_ENABLED,
          noAudioProcessing);
      intent.putExtra(CallActivity.EXTRA_AECDUMP_ENABLED, aecDump);
      intent.putExtra(CallActivity.EXTRA_OPENSLES_ENABLED, useOpenSLES);
      intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AEC, disableBuiltInAEC);
      intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_AGC, disableBuiltInAGC);
      intent.putExtra(CallActivity.EXTRA_DISABLE_BUILT_IN_NS, disableBuiltInNS);
      intent.putExtra(CallActivity.EXTRA_AUDIO_BITRATE, audioStartBitrate);
      intent.putExtra(CallActivity.EXTRA_AUDIOCODEC, audioCodec);
      intent.putExtra(CallActivity.EXTRA_DISPLAY_HUD, displayHud);
      intent.putExtra(CallActivity.EXTRA_TRACING, tracing);
      intent.putExtra(CallActivity.EXTRA_CMDLINE, commandLineRun);
      intent.putExtra(CallActivity.EXTRA_RUNTIME, runTimeMs);
      srcActivity.startActivityForResult(intent, CONNECTION_REQUEST);
//	      startActivityForResult(intent, CONNECTION_REQUEST);
    }
	}

	private boolean validateUrl(String url) {
	    if (URLUtil.isHttpsUrl(url) || URLUtil.isHttpUrl(url)) {
	      return true;
	    }

	    new AlertDialog.Builder(srcActivity)
          .setTitle(srcActivity.getResources().getText(R.string.invalid_url_title))
          .setMessage(srcActivity.getResources().getString(R.string.invalid_url_text, url))
          .setCancelable(false)
          .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
              dialog.cancel();
	            }
	        }).create().show();
	    return false;
	}

}
