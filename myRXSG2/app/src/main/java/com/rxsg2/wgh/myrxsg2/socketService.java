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
            //placeTreasure();
            //guanXing();
            //SigninPerMonth();
            //getGiftFromLuoYang();
            for(int i = 0;i < 100;i++) {
                patrol(10000, 50);
            }

            // TODO:获取君主信息
            //getGeralsInfo();
            // 此处循环执行游戏
            if(true){
                print("while");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    // TODO:放置珍宝
    private void placeTreasure(){
        for(int i = 0; i < 4;i++){
            send(link("0100ca8b0100",SockTools.convertInt2bytestr(1,4) + SockTools.convertInt2bytestr(i,4)));
        }

    }
    // TODO:皇家军营
    private void HuangjiaJunYing(){
        print(link("0100bc8a0100","0400313533356d0000000a00000000000000"));
        send(link("0100bc8a0100","0400313533356d0000000a00000000000000"));
    }
    // TODO:每个月的累计签到
    private void SigninPerMonth(){
        send(link("0100d48b0100",""));
    }
    // TODO:观星
    private void guanXing(){
        send(link("0100148d0100",SockTools.convertInt2Hexstr(6,4) + SockTools.convertInt2Hexstr(10,8)));
    }
    // TODO:领取洛阳福利
    private void getGiftFromLuoYang(){
        for(int i = 0; i < 10;i++){
            send(link("01002a8d0100",SockTools.convertInt2Hexstr(i,4)));
        }
    }
    // TODO:巡墙
    private void patrol(int start,int times){
        getGeralsInfo();
        String id = generals.getLowGeneral();
        // 没有低级将领
        if(id == null){
            print("Three is no low general");
            return;
        }
        else{
            print(id);
        }
        for(int i = start;i < start + times;i++){
            // 发送指令
            send(link("0100438d0100",convertInt2bytestr(i,4) + SockTools.converID2Hexstr(id)));
            //receiveSocketNoReturn("0200438d0100");
        }
        // 解雇将领
        send(link("010002880100",SockTools.converID2Hexstr(id)));
        receiveSocketNoReturn("020002880100");
    }
    
    // TODO:签到，领取礼包
    private void giftForSignin(){
        try {
            String data = link("0100e2890100", "");
            send(data);
            data = link("0100428a0100", "");
            send(data);
        }catch (Exception e){
            print("void giftForSignin() Error");
            return;
        }
    }

    // TODO:获取将领信息
    private String getGeralsInfo(){
        generals.clear();
        try {
            String data = link("010031870100", "00000000");

            while (send(data)) {
                if((data = receiveSocket("020031870100")) == null){
                    continue;
                }
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
            print("String getGeralsInfo() Error");
            return null;
        }
        return null;
    }

    // TODO:获取君主ID
    private String getMonarchInfo(){
        try {
            String data = link("0100a6880100", "01000000");
            print(data);
            while (send(data)) {
                if((data = receiveSocket("03002c880100")) == null){
                    continue;
                }
                int num = convertHexstr2Int(data.substring(28 * 2, 30 * 2));
                return new String(convertHexstr2byte(data.substring(30 * 2, (30 + num) * 2)), "utf-8");
            }
        }catch (Exception e){
            print("String getMonarchInfo() Error");
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
            Thread.sleep(10);
            return true;
        }catch (Exception e){
            print("boolean send(String data) error");
            e.printStackTrace();
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
                if(s == null){
                    print(header + ":没有接收到！");
                    return null;
                }
                if(s.startsWith(header)){
                    return getLengthString(inputStream,getLength(s),true);
                }
                else
                {
                    getLengthString(inputStream,getLength(s),false);
                }
            }
        }catch (Exception e){
            print("String receiveSocket(String header) Error");
            e.printStackTrace();
            return null;
        }
    }
    // TODO:获取头信息为header数据，但不返回任何东西
    private boolean receiveSocketNoReturn(String header){
        try {
            InputStream inputStream = socket.getInputStream();
            while(true){
                String s = getLengthString(inputStream,18,true);
                if(s == null){
                    print(header + ":没有接收到！");
                    return false;
                }
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
            print("boolean receiveSocketNoReturn(String header) Error");
            e.printStackTrace();
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
        int temp = 0,s_num = 0;
        String s = "";
        if(length < 0){length = 1000;}
        if(length == 0){return s;}
        // 相读取length个字符
        byte[] buffer = new byte[length];
        try {
            while (true) {
                // 给10次机会,每次延时5ms
                for(int i = 0;i < 20;i++){
                    if(inputStream.available()!= 0){
                        break;
                    }
                    if(i == 20){
                        return null;
                    }
                    Thread.sleep(3);
                }
                if((temp = inputStream.read(buffer)) < 0){
                    continue;
                }
                s_num += temp;
                if(isreturn){
                    //print("tmp:" + temp);
                    s += SockTools.convertBytes2Hexstr(buffer, temp);
                }
                if (s_num < length) {
                    buffer = new byte[length - s_num];
                } else {
                    // 如果读取长度正好，那么久返回s,如果大于，则返回null
                    if(s_num != length){
                        return null;
                    }
                    else {
                        return s;
                    }
                }
            }

        }catch(Exception e){
            print("String getLenthString Error");
            e.printStackTrace();
            return null;
        }
    }
    // TODO:登陆
    private Socket login(String host,String port,String g_server_id,String pass_type,String g_pass_port,String g_pass_token,String g_version){

        print("服务器IP:" + host);
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
    private void print(Object str){
        System.out.println(str);
    }
}
