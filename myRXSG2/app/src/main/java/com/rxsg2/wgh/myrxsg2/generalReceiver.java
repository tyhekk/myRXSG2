package com.rxsg2.wgh.myrxsg2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.rxsg2.wgh.myrxsg2.OnUpdateUI;

public class generalReceiver extends BroadcastReceiver {

    private OnUpdateUI onUpdateUI;
    @Override
    public void onReceive(Context context, Intent intent) {
        onUpdateUI.updateUI(intent);
    }

    public void setOnUpdateUI(OnUpdateUI onUpdateUI){
        this.onUpdateUI = onUpdateUI;
    }
}
