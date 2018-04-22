package com.rxsg2.wgh.myrxsg2;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 该类用来请求cookie和登陆参数
 */
public class httpService extends IntentService {


    public httpService() {
        super("httpService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String action = intent.getAction();
        // 登陆网页操作，要返回cookie
        if("login".equals(action))
        {
            // 获取用户名，密码，验证码，验证码cookieID
            String name = "";
            String password = "";
            String verficationCode = intent.getExtras().getString("verficationCode");
            String verificationCookie = intent.getExtras().getString("verificationCookie");

            String url = "http://web.data.service.ledu.com/pass/ajax_login/index?callback=callback&username="+ name +"&password=" + password + "&fast=0&keeplogin=1&code="+ verficationCode +"&_=" + System.currentTimeMillis();

            String host = "web.data.service.ledu.com";
            String refer = "http://www.ledu.com/login.html";
            String cookie = "image_verifycode_key=" + verificationCookie;
            //System.out.println(cookie);

            HttpURLConnection urlConnect = getHttpURLConnection(url, host, refer, cookie);
            Map<String,String> set_cookie = getCookie(urlConnect);
            set_cookie.put("image_verifycode_key", verificationCookie);


            List list =  getHttpText(urlConnect);
            if((int)list.get(0) == 200){
                System.out.println("登陆结果："+(String)list.get(1));
            }
            url = "http://s2113.sg2.ledu.com/";
            host = "s2113.sg2.ledu.com";
            refer = "";
            cookie = createCookie(set_cookie);
            //System.out.println(cookie);

            urlConnect = getHttpURLConnection(url, host, refer, cookie);
            set_cookie.putAll(getCookie(urlConnect));
            list =  getHttpText(urlConnect);
            System.out.println(set_cookie);

            if((int)list.get(0) == 200){
                //System.out.println("主页结果："+(String)list.get(1));
            }

            url = "http://pass.ledu.com/api/game/start?gs_domain=http%3A%2F%2Fs2113.server-sg2.ledu.com";
            host = "pass.ledu.com";
            refer = "http://s2113.sg2.ledu.com/";
            cookie = createCookie(set_cookie);

            urlConnect = getHttpURLConnection(url, host, refer, cookie);
            set_cookie.putAll(getCookie(urlConnect));
            list =  getHttpText(urlConnect);
            if((int)list.get(0) == 302){
                //System.out.println("主页结果："+(String)list.get(1));
                url = (String)list.get(1);
                cookie = createCookie(set_cookie);
                host = "s2113.server-sg2.ledu.com";
                refer = "http://s2113.sg2.ledu.com/";

                urlConnect = getHttpURLConnection(url, host, refer, cookie);
                set_cookie.putAll(getCookie(urlConnect));
            }

            url = "http://s2113.server-sg2.ledu.com/";
            cookie = createCookie(set_cookie);
            host = "s2113.server-sg2.ledu.com";
            refer = "http://s2113.sg2.ledu.com/";

            urlConnect = getHttpURLConnection(url, host, refer, cookie);
            set_cookie.putAll(getCookie(urlConnect));
            list =  getHttpText(urlConnect);
            if((int)list.get(0) == 200){
                //System.out.println((String)list.get(1));
                String context = (String)list.get(1);

                String g_server_id =  matchOneString("flashvars: .+g_server_id=(.+?)&",context);
                String g_pass_type   = matchOneString("flashvars: .+pass_type=(.+?)&",context);
                String g_port   = matchOneString("flashvars: .+g_port=(.+?)&",context);
                String g_pass_token   = matchOneString("flashvars: .+g_pass_token=(.+?)&",context);
                String g_version   = matchOneString("flashvars: .+g_version=(.+?)&",context);
                String g_host   = matchOneString("flashvars: .+g_host=(.+?)&",context);
                String g_pass_port = matchOneString("flashvars: .+g_pass_port=(.+?)&",context);

                /*System.out.println("g_server_id:"+g_server_id);
                System.out.println("g_pass_type:"+g_pass_type);
                System.out.println("g_port:"+g_port);
                System.out.println("g_pass_token:"+g_pass_token);
                System.out.println("g_version:"+g_version);
                System.out.println("g_host:"+g_host);
                System.out.println("g_pass_port:"+g_pass_port);*/

                Intent paramter = new Intent();
                paramter.setAction("mainActivity");
                paramter.putExtra("ACTION","LoginParamter");
                paramter.putExtra("g_server_id",g_server_id);
                paramter.putExtra("g_pass_type",g_pass_type);
                paramter.putExtra("g_port",g_port);
                paramter.putExtra("g_pass_token",g_pass_token);
                paramter.putExtra("g_version",g_version);
                paramter.putExtra("g_host",g_host);
                paramter.putExtra("g_pass_port",g_pass_port);
                sendBroadcast(paramter);


            }

        }
    }

    // 返回值是setCookie
    private HttpURLConnection getHttpURLConnection(String url,String host,String referer,String cookie) {
        try {
            URL pathUrl = new URL(url);
            HttpURLConnection urlConnect = (HttpURLConnection) pathUrl.openConnection();
            urlConnect.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
            urlConnect.setRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
            urlConnect.setRequestProperty("Cache-Control", "max-age=0");
            urlConnect.setRequestProperty("Connection", "keep-alive");
            urlConnect.setRequestProperty("Referer", referer);
            urlConnect.setRequestProperty("Host", host);
            urlConnect.setRequestProperty("Upgrade-Insecure-Requests", "1");
            urlConnect.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.221 Safari/537.36 SE 2.X MetaSr 1.0");
            urlConnect.setRequestProperty("Cookie", cookie);

            urlConnect.setRequestMethod("GET");
            urlConnect.setInstanceFollowRedirects(false);

            //System.out.println(host);
            System.out.println( urlConnect.getResponseCode() + ":"+ host);

            return urlConnect;
        } catch (Exception e) {
            Log.i("System", "IOException:" + e.toString());
            return null;
        }
    }

    private List getHttpText(HttpURLConnection urlConnect) {
        List list = new ArrayList();
        String httpString = "";
        try{
            urlConnect.setConnectTimeout(1000);
            urlConnect.connect();
            // 如果是302网页，那么获取Location
            list.add(urlConnect.getResponseCode());
            if(urlConnect.getResponseCode() == 302) {
                String location = urlConnect.getHeaderField("Location");
                list.add(location);
                return list;
            }
            InputStreamReader in = new InputStreamReader(urlConnect.getInputStream());
            BufferedReader buffer = new BufferedReader(in);

            String inputLine = null;
            while (((inputLine = buffer.readLine())) != null) {
                httpString += inputLine;

                //System.out.println(inputLine);
            }
            list.add(httpString);
            return list;
        } catch (Exception e) {
            Log.i("System", "IOException:" + e.toString());
            return null;
        }
    }

    // 获取cookie
    private Map<String,String> getCookie(HttpURLConnection urlConnect){
        Map<String, List<String>> header = urlConnect.getHeaderFields();
        Map<String,String> cookie = new HashMap<String,String>();;
        Set<String> hdrKeys = header.keySet();
        //System.out.println(header.get("Set-Cookie"));
        for (String key : hdrKeys)
        {
            if("Set-Cookie".equals(key))
            {
                List tmp = header.get(key);
                for(int i = 0; i < tmp.size();i++) {
                    String[] tmp2 = tmp.get(i).toString().split(";");
                    for (int k = 0; k < tmp2.length; k++) {
                        String[] tmp3 = tmp2[k].split("=");
                        if (tmp3.length == 2) {
                            if(tmp3[1].indexOf("deleted") > 0){
                                continue;
                            }
                            cookie.put(tmp3[0], tmp3[1]);
                            //System.out.println(tmp3[0] +":"+ tmp3[1]);

                        }
                    }
                }
            }
        }
        return cookie;
    }
    private String createCookie(Map<String,String> cookieMap){
        String cookie= "";
        Set<String> cookieKeys = cookieMap.keySet();
        for(String key : cookieKeys)
        {
            cookie += (key + "=" + cookieMap.get(key) + "; ");
        }
        return cookie;
    }

    private String matchOneString(String reg,String text){
        Pattern p = Pattern.compile(reg);
        Matcher m = p.matcher(text);
        while(m.find()) {
            String result = m.group(1);
            return result;
        }
        return "";
    }
}


