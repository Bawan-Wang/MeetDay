package com.project1.http;


public class FileManager {

	public static String getSaveFilePath() {
		if (CommonUtil.hasSDCard()) {
			return CommonUtil.getRootFilePath() + Const.projinfo.sLocalFile+"/files/";//"com.geniusgithub/files/";
		} else {
			return CommonUtil.getRootFilePath() + Const.projinfo.sLocalFile+"/files/";//"com.geniusgithub/files/";
		}
	}
}
