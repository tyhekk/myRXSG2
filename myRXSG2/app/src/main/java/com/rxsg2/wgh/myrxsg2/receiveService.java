package com.rxsg2.wgh.myrxsg2;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;

import java.net.Socket;
import java.util.PriorityQueue;

//TODO:用于接受数据，并做初步处理
public class receiveService extends IntentService {
    private Socket socket = null;
    public receiveService(Socket socket) {
        super("receiveService");
        this.socket = socket;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            System.out.println("receiveService");
        }
    }


}
