package com.rxsg2.wgh.myrxsg2;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

public class SockTools {

    public static List analyzeHexStr(String s,List listData){
        List<String> list = new ArrayList<String>();
        list.add(s);
        return analyzeHexStr(list,listData);
    }

    public static List analyzeHexStr(List<String> list,List listData){

        String hexstr = (String)listData.get(listData.size() - 1);
        List result = new ArrayList<>();
        // TODO:将Hex字符串转为Byte[]处理
        byte[] bytes = convertHexstr2byte(hexstr);
        int index = 0;
        int length = (hexstr.length()/2);
        if(index >= length){return null;}
        for(String s:list){
            // 一个字节bool
            if("bool".equals(s)){
                if((index + 1) > length){result.add(null); return result;};
                result.add((int)bytes[index]);
                index += 1;
            }
            // id两位长度+字符串
            else if("id".equals(s)){
                if(( index + 2 ) > length){result.add(null); return result;};
                int num = converBytes2Int(bytes,index,index + 2);

                index += 2;
                if(( index + num ) > length){result.add(null); return result;};
                result.add( converBytes2Str(bytes,index,index + num) );
                index += num;
            }
            // int 4字节
            else if("int".equals(s)){
                if(( index + 4 ) > length){result.add(null); return result;};
                result.add(converBytes2Int(bytes,index,index + 4));
                index += 4;
            }
            // double 8字节
            else if("double".equals(s)){
                if(( index + 8 ) > length){result.add(null); return result;};
                result.add(converBytes2Double(bytes,index,index + 8));
                index += 8;
            }
        }
        // 没有分析完的，返回剩余的String
        if(index >= length){
            result.add(null); return result;
        }
        else
        {
            result.add(hexstr.substring(index*2)); return result;
        }
    }

    // TODO:将BYTE[]中的某一段转为double
    public static double converBytes2Double(byte[] bytes,int start,int end) {
        byte[] buff = new byte[end - start];
        for(int i = start;i < end;i++){
            buff[i - start] = bytes[i];
        }
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (buff[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }
    // TODO:将BYTE[]中的某一段转为String
    public static String converBytes2Str(byte[] bytes,int start,int end){
        byte[] buff = new byte[end - start];
        for(int i = start;i < end;i++){
            buff[i - start] = bytes[i];
        }
        try {
            return new String(buff, "utf-8");
        } catch (UnsupportedEncodingException e) {
            System.out.println("public static String converBytes2Str(byte[] bytes,int start,int end) Error");
            return null;
        }
    }
    // TODO:将BYTE[]中的某一段转为数字
    public static int converBytes2Int(byte[] bytes,int start,int end){
        int num = 0;
        for(int i = start;i < end;i++){
            num = num + (bytes[i]<<((i - start)*8));
        }
        return num;
    }
    // TODO:将HEX字符串转化为BYTE[]
    public static  byte[] convertHexstr2byte(String str){
        byte[] bytes = new byte[str.length()/2];

        for(int i = 0 ; i < str.length()/2;i++){
            String  bytestr = str.substring(2*i,2*i + 2);
            bytes[i] = (byte)Integer.parseInt(bytestr, 16);
        }
        return bytes;
    }
    // TODO:将byte[]变为十六进制的字符串
    public static  String convertBytes2bytestr(byte[] bytes,int length){
        char[] c = new char[length*2];
        for(int i = 0;i < length;i++) {
            String s = convertInt2bytestr(bytes[i],1);
            c[2*i] = s.charAt(0);
            c[2*i + 1] = s.charAt(1);
        }
        return new String(c);
    }
    // TODO:将十六进制的字符串变为整数
    public static  int convertHexstr2Int(String numstr) {
        int num = 0;
        for(int i = 0;i < numstr.length()/2;i++){
            num = num + (Integer.parseInt(numstr.substring(2*i,2*i + 2), 16)<<(i*8));
            //System.out.println(Integer.parseInt(numstr.substring(2*i,2*i + 2), 16));
        }
        return num;
    }
    // TODO:将整数变为十六进制的字符串
    public static  String convertInt2bytestr(int num,int length) {
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

    public static  String convertStr2Hexstr(String str){

        return null;
    }
    //TODO:字符串转换为ID形式（长度 + ID）
    public static  String convertStr2IDmode(String str){
        byte[] bytes = str.getBytes();
        int len = str.length();
        String result = convertInt2bytestr(len,2);
        for(int i = 0 ;i < len;i++){
            result += Integer.toHexString(bytes[i]);
        }
        return result;
    }

    public  static String md5(String g_pass_port,String g_version) {

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
