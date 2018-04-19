package com.rxsg2.wgh.myrxsg2;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class verificationCodeService extends IntentService {
    public verificationCodeService() {
        super("verificationCodeService");
    }
    private Messenger mMessenger;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent intent1 = new Intent();
        intent1.setAction("UPDATE");

        try {

            List list = getHttpBitmap("http://web.data.service.ledu.com/pass/ajax_verifycode/create");
            Bitmap bitmap = (Bitmap)list.get(0);
            String set_cookie = (String)list.get(1);
            //System.out.println("验证码："+set_cookie);
            intent1.putExtra("bitmap",bitmap);
            intent1.putExtra("verificationCookie",set_cookie);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sendBroadcast(intent1);
    }

    final String getImage() throws Exception {
        try {
            String path = "http://www.ledu.com/login.html";

            URL pathUrl = new URL(path);
            HttpURLConnection urlConnect = (HttpURLConnection) pathUrl.openConnection();
            urlConnect.setRequestProperty("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            urlConnect.setRequestProperty("Accept-Language","zh-CN,zh;q=0.8");
            urlConnect.setRequestProperty("Cache-Control","max-age=0");
            urlConnect.setRequestProperty("Connection","keep-alive");
            urlConnect.setRequestProperty("Cookie","");
            urlConnect.setRequestProperty("Host","www.ledu.com");
            urlConnect.setRequestProperty("Upgrade-Insecure-Requests","1");
            urlConnect.setRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0");

            urlConnect.setRequestMethod("GET");

            urlConnect.setConnectTimeout(1000);
            urlConnect.connect();

            InputStreamReader in = new InputStreamReader(urlConnect.getInputStream());
            BufferedReader buffer = new BufferedReader(in);

            String httpString = "";
            String inputLine = null;
            while (((inputLine = buffer.readLine())) != null) {
                //httpString += inputLine;
                //System.out.println(inputLine);
            }

            return httpString;

        } catch (Exception e) {
            Log.i("System", "IOException:" + e.toString());
        }
        return "";
    }{}

    // 获取网络上图片资源
    public static List getHttpBitmap(String url){
        URL myFileURL;
        Bitmap bitmap=null;
        String set_cookie = "";
        List list = new ArrayList();
        try{
            myFileURL = new URL(url);
            //获得连接
            HttpURLConnection conn=(HttpURLConnection)myFileURL.openConnection();
            //设置超时时间为6000毫秒，conn.setConnectionTiem(0);表示没有时间限制
            conn.setConnectTimeout(6000);
            //连接设置获得数据流
            conn.setDoInput(true);
            //不使用缓存
            conn.setUseCaches(false);
            //这句可有可无，没有影响
            //conn.connect();
            // 获取相应头的Set_Cookie
            Map<String, List<String>> hdrs = conn.getHeaderFields();
            Set<String> hdrKeys = hdrs.keySet();

            for (String key : hdrKeys)
            {
                if("Set-Cookie".equals(key))
                {
                    set_cookie = hdrs.get(key).get(0);
                    set_cookie = (set_cookie.split(";"))[0];
                    set_cookie = (set_cookie.split("="))[1];

                }
            }
            //得到数据流
            InputStream is = conn.getInputStream();
            //解析得到图片
            bitmap = BitmapFactory.decodeStream(is);
            //关闭数据流
            is.close();

            list.add(bitmap);
            list.add(set_cookie);
        }catch(Exception e){
            e.printStackTrace();
        }
        return list;
    }
}
