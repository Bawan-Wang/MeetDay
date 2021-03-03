package com.project1.meetday;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class BoardSwitchDialog extends DialogFragment {
	
	static private Boolean isRemoteOk = false;
    static private String strRemoteName;
    static BoardSwitchDialog newInstance(String remotename) {
	   BoardSwitchDialog fragment = new BoardSwitchDialog();
        strRemoteName = remotename;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

    	return new AlertDialog.Builder(getActivity()).setTitle(strRemoteName + " " + getResources().getString(R.string.comm_dialog_request_switch_wb))
            .setPositiveButton(getResources().getString(R.string.dialog_eraseall_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                    isRemoteOk = true;
                    ((CallActivity) getActivity()).boardSwitchPositiveClick();
                }
            }).setNegativeButton(getResources().getString(R.string.dialog_eraseall_reject), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	((CallActivity) getActivity()).boardSwitchNegativeClick();
            }
        }).create();
	}

    @Override
	public void onPause(){
		super.onPause();
			((CallActivity) getActivity()).boardSwitchOnPause(isRemoteOk);
	}  	    
}
