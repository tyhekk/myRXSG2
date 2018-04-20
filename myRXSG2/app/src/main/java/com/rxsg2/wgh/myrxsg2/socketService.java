package com.rxsg2.wgh.myrxsg2;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.text.TextUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class socketService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    Socket socket = null;
    Socket receiveSocket = null;
    Generals generals = new Generals();
    public socketService() {
        super("socketService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getExtras().getString("ACTION");
        if("Login".equals(action)){

            String g_server_id = intent.getExtras().getString("g_server_id");
            String g_pass_type = intent.getExtras().getString("g_pass_type");
            String g_port = intent.getExtras().getString("g_port");
            String g_pass_token = intent.getExtras().getString("g_pass_token");
            String g_version = intent.getExtras().getString("g_version");
            String g_host = intent.getExtras().getString("g_host");
            String g_pass_port = intent.getExtras().getString("g_pass_port");
            //TODO: 登陆
            do {
                login(g_host, g_port, g_server_id, g_pass_type, g_pass_port, g_pass_token, g_version);
            }while(receiveSocketNoReturn("030067000000") == false);
            giftForSignin();
            //System.out.println(getMonarchInfo());
            // TODO:获取君主信息
            //getGeralsInfo();
            // 此处循环执行游戏
            if(true){
                //SockTools.analyzeHexStr(null);
            }
        }


    }
    
    // TODO:签到，领取礼包
    private void giftForSignin(){
        try {
            String data = link("0100e2890100", "");
            send(data);
            data = link("0100428a0100", "");
            send(data);
        }catch (Exception e){
            System.out.println("void giftForSignin() Error");
            return;
        }
    }

    // TODO:获取将领信息
    private String getGeralsInfo(){
        try {
            String data = link("010031870100", "00000000");
            //System.out.println(data);
            while (send(data)) {
                while((data = receiveSocket("020031870100")) == null);
                // 分析数据
                AnalyzeData analysis = new AnalyzeData();
                analysis.setData(data);

                if((int)analysis.analyze("bool") == 0){
                    return "";
                }
                else
                {
                    while((int)analysis.analyze("bool") == 1){
                        generals.addGeneral((String)analysis.analyze("id"),(int)analysis.analyze("int"),
                                (String)analysis.analyze("id"),(int)analysis.analyze("int"),
                                (int)analysis.analyze("int"),(int)analysis.analyze("int"),
                                (String)analysis.analyze("id"),(int)analysis.analyze("int"),
                                (double)analysis.analyze("double"),(double)analysis.analyze("double"),
                                (double)analysis.analyze("double"),(double)analysis.analyze("double"),
                                (double)analysis.analyze("double"));
                    }
                }

                return "";
            }
        }catch (Exception e){
            System.out.println("String getGeralsInfo() Error");
            return null;
        }
        return null;
    }

    // TODO:获取君主ID
    private String getMonarchInfo(){
        try {
            String data = link("0100a6880100", "01000000");
            System.out.println(data);
            while (send(data)) {
                data = receiveSocket("03002c880100");
                int num = convertHexstr2Int(data.substring(28 * 2, 30 * 2));
                return new String(convertHexstr2byte(data.substring(30 * 2, (30 + num) * 2)), "utf-8");
            }
        }catch (Exception e){
            System.out.println("String getMonarchInfo() Error");
            return null;
        }
        return null;
    }

    // TODO:发送数据
    private boolean send(String data){
        try {
            OutputStream os = socket.getOutputStream();
            os.write(convertHexstr2byte(data));
            os.flush();
            return true;
        }catch (Exception e){
            System.out.println("boolean send(String data) error");
            return false;
        }
    }
    // TODO:连接header和data，补充中间的信息
    private String link(String header,String data){
        return header + "0100000000000000" + convertInt2bytestr(data.length()/2,4) + data;
    }
    // TODO：接收头信息为header字符串并返回，接收失败返回null
    private String receiveSocket(String header){
        try {
            InputStream inputStream = socket.getInputStream();
            while(true){
                String s = getLengthString(inputStream,18,true);
                //System.out.println(s);

                if(s.startsWith(header)){
                    return getLengthString(inputStream,getLength(s),true);
                }
                else
                {
                    getLengthString(inputStream,getLength(s),false);
                }
            }
        }catch (Exception e){
            System.out.println("String receiveSocket(String header) Error");
            return null;
        }
    }
    // TODO:获取头信息为header数据，但不返回任何东西
    private boolean receiveSocketNoReturn(String header){
        try {
            InputStream inputStream = socket.getInputStream();
            while(true){
                String s = getLengthString(inputStream,18,true);

                if(s.startsWith(header)){
                    getLengthString(inputStream,getLength(s),false);
                    return true;
                }
                else
                {
                    getLengthString(inputStream,getLength(s),false);
                }
            }
        }catch (Exception e){
            System.out.println("boolean receiveSocketNoReturn(String header) Error");
            return false;
        }
    }
    // TODO:从字符头中获取长度信息
    private int getLength(String s){
        s = s.substring(28,36);
        return convertHexstr2Int(s);
    }
    // TODO:获取固定长度的Hex字符串,获取失败返回null
    private String getLengthString(InputStream inputStream,int length,boolean isreturn){
        int temp = 0;
        int s_num = 0;
        String s = "";
        // 相读取length个字符
        byte[] buffer = new byte[length];
        try {
            while ((temp = inputStream.read(buffer)) > 0) {
                s_num += temp;
                if(isreturn){
                    s += convertBytes2bytestr(buffer, temp);
                }
                if (s_num < length) {
                    buffer = new byte[length - s_num];
                } else {
                    break;
                }
            }
            if(s_num != length){
                return null;
            }
            else
            {
                return s;
            }
        }catch(Exception e){
            System.out.println("String getLenthString Error");
            return null;
        }
    }
    // TODO:登陆
    private Socket login(String host,String port,String g_server_id,String pass_type,String g_pass_port,String g_pass_token,String g_version){

        try {
            socket = new Socket(host, Integer.valueOf(port).intValue());
            socket.setSoTimeout(10000);

            //TODO:发送测试数据
            OutputStream os = socket.getOutputStream();
            String data = "0000000000000000000000000000000000000000000000";
            os.write(convertHexstr2byte(data));
            os.flush();

            //TODO:发送登录信息
            data = "0100c8000000010000000000000061000000";
            data += convertInt2bytestr(Integer.parseInt(g_server_id),4);
            data += convertStr2IDmode(pass_type);
            data += convertStr2IDmode(g_pass_port);
            data += convertStr2IDmode(g_pass_token);
            data += convertStr2IDmode(g_version);
            data += convertStr2IDmode(md5(g_pass_port,g_version));
            os.write(convertHexstr2byte(data));

            os.flush();
            Thread.sleep(10);
        }catch (Exception e){

        }
        return socket;
    }
    // TODO:将HEX字符串转化为BYTE[]
    private byte[] convertHexstr2byte(String str){
        byte[] bytes = new byte[str.length()/2];

        for(int i = 0 ; i < str.length()/2;i++){
            String  bytestr = str.substring(2*i,2*i + 2);
            bytes[i] = (byte)Integer.parseInt(bytestr, 16);
        }
        return bytes;
    }
    // TODO:将byte[]变为十六进制的字符串
    private String convertBytes2bytestr(byte[] bytes,int length){
        char[] c = new char[length*2];
        for(int i = 0;i < length;i++) {
            String s = convertInt2bytestr(bytes[i],1);
            c[2*i] = s.charAt(0);
            c[2*i + 1] = s.charAt(1);
        }
        return new String(c);
    }
    // TODO:将十六进制的字符串变为整数
    private int convertHexstr2Int(String numstr) {
        int num = 0;
        for(int i = 0;i < numstr.length()/2;i++){
            num = num + (Integer.parseInt(numstr.substring(2*i,2*i + 2), 16)<<(i*8));
            //System.out.println(Integer.parseInt(numstr.substring(2*i,2*i + 2), 16));
        }
        return num;
    }
    // TODO:将整数变为十六进制的字符串
    private String convertInt2bytestr(int num,int length) {
        String str = "";
        String tmp = "";
        for(int i = 0;i < length;i++){
            tmp = Integer.toHexString(num - ((num>>8)<<8));
            if(tmp.length() == 1){
                tmp = "0" + tmp;
            }
            str += tmp;
            num = num>>8;
        }
        return str;
    }

    private String convertStr2Hexstr(String str){

        return null;
    }
    //TODO:字符串转换为ID形式（长度 + ID）
    private String convertStr2IDmode(String str){
        byte[] bytes = str.getBytes();
        int len = str.length();
        String result = convertInt2bytestr(len,2);
        for(int i = 0 ;i < len;i++){
            result += Integer.toHexString(bytes[i]);
        }
        return result;
    }

    private static String md5(String g_pass_port,String g_version) {

        String key = "8Ij18Hisl1na0Ous2f";
        String sign = "PROC_SIGN_DEFAULT";
        String string = g_pass_port+g_version+key+sign;

        byte[] hash;

        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        }catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
