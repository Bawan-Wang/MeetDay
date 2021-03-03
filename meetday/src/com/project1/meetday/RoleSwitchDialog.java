package com.project1.meetday;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class RoleSwitchDialog extends DialogFragment {

	static private Boolean isLocal;
	static private Boolean isRemoteOk = false;
	
    static RoleSwitchDialog newInstance(boolean isLocalCmd) {
    	RoleSwitchDialog fragment = new RoleSwitchDialog();
        Bundle args = new Bundle();
//        args.putBoolean("Local Dialog", isLocalCmd);
//        fragment.setArguments(args);
        isLocal = isLocalCmd;
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
//        isLocal = getArguments().getBoolean("Local Dialog");

    	return new AlertDialog.Builder(getActivity()).setTitle(getResources().getString(R.string.comm_dialog_role_switch))
            .setPositiveButton(getResources().getString(R.string.dialog_eraseall_ok), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	if(isLocal) {
                		((CallActivity) getActivity()).localRSDoPositiveClick();
                	}else{
                		isRemoteOk = true;
                		((CallActivity) getActivity()).remoteRSDoPositiveClick();
                	}
                }
            }).setNegativeButton(getResources().getString(R.string.dialog_eraseall_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int whichButton) {
                	if(isLocal) {
                		//Do nothing
                	}else{
                		isRemoteOk = false;
                		((CallActivity) getActivity()).remoteRSDoNegativeClick();
                	}
                }
            }).create();
    }
    
    @Override
    public void onResume(){
    	super.onResume();
    	if(isLocal) {
    		((CallActivity) getActivity()).localRSOnResume();
    	}else{
    		((CallActivity) getActivity()).remoteRSOnResume();
    	}
    }
    
    @Override
    public void onPause(){
    	super.onPause();
    	if(isLocal) {
    		((CallActivity) getActivity()).localRSOnPause();
    	}else{
    	    ((CallActivity) getActivity()).remoteRSOnPause(isRemoteOk);
    	}    	
    }    
    
}