package com.victors.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Victors on 2017/8/18.
 */
public class UserInfo {
    public static Boolean testUser(User user)
    {
        String id = user.getId();
        String password = user.getPassword();
        String targeturl = "http://192.168.1.52:9200/user_table/" + id + "/" + id + "/_source";
        try
        {
            URL restServiceURL = new URL(targeturl);
            HttpURLConnection httpConnection = (HttpURLConnection)restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            if(httpConnection.getResponseCode() != 200)
            {
                return false;
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String output = "";
            String temp = "";
            while((temp = responseBuffer.readLine()) != null)
            {
                output += temp;
            }
            httpConnection.disconnect();
            if(output.equals(""))
            {
                return false;
            }
            else
            {
                JSONObject dataJson = new JSONObject(output);
                if(dataJson.has("id"))
                {
                    String test_id = "";
                    String test_password = "";
                    test_id = dataJson.getString("id");
                    test_password = dataJson.getString("password");
                    if(test_id.equals(id))
                    {
                        password = MD5.md5(password);
                        if(test_password.equals(password))
                        {
                            return true;
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else
                    {
                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
        }catch(Exception e)
        {
            return false;
        }
    }

    public static User getUser(String id)
    {
        String targeturl = "http://192.168.1.52:9200/user_table/" + id + "/" + id + "/_source";
        try
        {
            URL restServiceURL = new URL(targeturl);
            HttpURLConnection httpConnection = (HttpURLConnection)restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            if(httpConnection.getResponseCode() != 200)
            {
                return null;
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String output = "";
            String temp = "";
            while((temp = responseBuffer.readLine()) != null)
            {
                output += temp;
            }
            httpConnection.disconnect();
            if(output.equals(""))
            {
                return null;
            }
            else
            {
                JSONObject dataJson = new JSONObject(output);
                if(dataJson.has("id"))
                {
                    String password = "";
                    String wid = "";
                    String name = "";
                    String tel = "";
                    String mail = "";
                    String addr = "";
                    password = dataJson.getString("password");
                    if(dataJson.has("wid")) {
                        wid = dataJson.getString("wid");
                    }
                    name = dataJson.getString("name");
                    tel = dataJson.getString("tel");
                    mail = dataJson.getString("mail");
                    addr = dataJson.getString("addr");
                    User user = new User();
                    user.setId(id);
                    user.setPassword(password);
                    user.setWid(wid);
                    user.setName(name);
                    user.setTel(tel);
                    user.setMail(mail);
                    user.setAddr(addr);
                    return user;
                }
                else
                {
                    return null;
                }
            }
        }catch(Exception e)
        {
            return null;
        }
    }

    public static String setForgetPassword(User user){
        String id = user.getId();
        String targeturl = "http://192.168.1.52:9200/user_table/" + id + "/" + id;
        try{
            URL restServiceURL = new URL(targeturl);
            HttpURLConnection httpConnection = (HttpURLConnection)restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            if(httpConnection.getResponseCode() != 200)
            {
                return "error:请求出错！";
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String output = "";
            String temp = "";
            while((temp = responseBuffer.readLine()) != null)
            {
                output += temp;
            }
            httpConnection.disconnect();
            if(output.equals("")) {
                return "error:没有此用户！";
            }else{
                JSONObject dataJson = new JSONObject(output);
                Boolean found = dataJson.getBoolean("found");
                if(found){
                    User user1 = getUser(id);
                    String name = user.getName();
                    String mail = user.getMail();
                    String tel = user.getTel();
                    String addr = user.getAddr();
                    if(id.equals(user1.getId()) && name.equals(user1.getName()) && mail.equals(user1.getMail()) && tel.equals(user1.getTel()) && addr.equals(user1.getTel())){
                        String password = user.getPassword();
                        password = MD5.md5(password);
                        String targetURL1 = "http://192.168.1.10:9200/user_table/" + id + "/" + id + "/_update";
                        try{
                            URL targetUrl1 = new URL(targetURL1);
                            HttpURLConnection httpConnection1 = (HttpURLConnection)targetUrl1.openConnection();
                            httpConnection1.setDoOutput(true);
                            httpConnection1.setRequestMethod("POST");
                            httpConnection1.setRequestProperty("Content-Type", "application/json");
                            JSONObject obj = new JSONObject();
                            JSONObject update = new JSONObject();
                            update.accumulate("password", password);
                            obj.accumulate("doc", update);
                            String input = obj.toString();
                            OutputStream outputStream = httpConnection.getOutputStream();
                            outputStream.write(input.getBytes("utf-8"));
                            outputStream.flush();
                            if(httpConnection.getResponseCode() != 200 && httpConnection.getResponseCode() != 201)
                            {
                                return "error:请求出错！";
                            }else{
                                BufferedReader responseBuffer1 = new BufferedReader(new InputStreamReader(httpConnection1.getInputStream()));
                                String output1 = "";
                                String temp1= "";
                                while((temp1 = responseBuffer.readLine()) != null)
                                {
                                    output1 += temp1;
                                }
                                httpConnection.disconnect();
                                return "ok";
                            }
                        }catch (Exception e){
                            return "error:请求出错！";
                        }
                    }else{
                        return "error:身份验证错误！";
                    }
                }else{
                    return "error:没有此用户！";
                }
            }
        }catch(Exception e){
            return "error:请求出错！";
        }
    }

    public static String testId(String id){
        String targeturl = "http://192.168.1.52:9200/user_table/" + id + "/" + id;
        try{
            URL restServiceURL = new URL(targeturl);
            HttpURLConnection httpConnection = (HttpURLConnection)restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            if(httpConnection.getResponseCode() != 200)
            {
                return "ok";
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String output = "";
            String temp = "";
            while((temp = responseBuffer.readLine()) != null)
            {
                output += temp;
            }
            httpConnection.disconnect();
            if(output.equals("")) {
                return "error:请求elasticsearch出错！";
            }else{
                JSONObject dataJson = new JSONObject(output);
                Boolean found = dataJson.getBoolean("found");
                if(found){
                    return "error:账户已存在！";
                }else{
                    return "ok";
                }
            }
        }catch(Exception e){
            return "error:请求elasticsearch出错！";
        }
    }

    public static String addUser(User user){
        String id = user.getId();
        String targetURL = "http://192.168.1.52:9200/user_table/" + id + "/" + id;
        try
        {
            URL targetUrl = new URL(targetURL);
            HttpURLConnection httpConnection = (HttpURLConnection)targetUrl.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Content-Type", "application/json");
            JSONObject obj = new JSONObject();
            String password = user.getPassword();
            password = MD5.md5(password);
            obj.accumulate("wid", user.getWid());
            obj.accumulate("tel", user.getTel());
            obj.accumulate("addr", user.getAddr());
            obj.accumulate("mail", user.getMail());
            obj.accumulate("password", password);
            obj.accumulate("id", id);
            obj.accumulate("name", user.getName());
            String input = obj.toString();
            OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(input.getBytes("utf-8"));
            outputStream.flush();
            if(httpConnection.getResponseCode() != 200 && httpConnection.getResponseCode() != 201)
            {
                return "error:elasticseatch写入数据出错！";
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String output = "";
            String temp = "";
            while((temp = responseBuffer.readLine()) != null)
            {
                output += temp;
            }
            httpConnection.disconnect();
            return "ok";
        }catch(Exception e)
        {
            return "error:连接elasticsearch出错！";
        }
    }

    public static String resetInfo(User user) {
        String id = user.getId();
        String targeturl = "http://192.168.1.52:9200/user_table/" + id + "/" + id;
        try {
            URL restServiceURL = new URL(targeturl);
            HttpURLConnection httpConnection = (HttpURLConnection) restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            if (httpConnection.getResponseCode() != 200) {
                return "error:请求出错！";
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String output = "";
            String temp = "";
            while ((temp = responseBuffer.readLine()) != null) {
                output += temp;
            }
            httpConnection.disconnect();
            if (output.equals("")) {
                return "error:没有此用户！";
            } else {
                JSONObject dataJson = new JSONObject(output);
                Boolean found = dataJson.getBoolean("found");
                if (found) {
                    String mail = user.getMail();
                    String tel = user.getTel();
                    String addr = user.getAddr();
                    String wid = user.getWid();
                    String targetURL1 = "http://192.168.1.52:9200/user_table/" + id + "/" + id + "/_update";
                    try {
                        URL targetUrl1 = new URL(targetURL1);
                        HttpURLConnection httpConnection1 = (HttpURLConnection) targetUrl1.openConnection();
                        httpConnection1.setDoOutput(true);
                        httpConnection1.setRequestMethod("POST");
                        httpConnection1.setRequestProperty("Content-Type", "application/json");
                        JSONObject obj = new JSONObject();
                        JSONObject update = new JSONObject();
                        update.accumulate("mail", mail);
                        update.accumulate("tel", tel);
                        update.accumulate("addr", addr);
                        update.accumulate("wid", wid);
                        obj.accumulate("doc", update);
                        String input = obj.toString();
                        OutputStream outputStream = httpConnection1.getOutputStream();
                        outputStream.write(input.getBytes("utf-8"));
                        outputStream.flush();
                        if (httpConnection1.getResponseCode() != 200 && httpConnection1.getResponseCode() != 201) {
                            return "error:elasticsearch再修改信息时出错！";
                        } else {
                            BufferedReader responseBuffer1 = new BufferedReader(new InputStreamReader(httpConnection1.getInputStream()));
                            String output1 = "";
                            String temp1 = "";
                            while ((temp1 = responseBuffer1.readLine()) != null) {
                                output1 += temp1;
                            }
                            httpConnection1.disconnect();
                            return "ok";
                        }
                    } catch (Exception e) {
                        return "error:请求elasticsearch出错！";
                    }
                } else {
                    return "error:没有此用户！";
                }
            }
        } catch (Exception e) {
            return "error:请求elasticsearch出错！";
        }
    }

    public static ArrayList<String> getAllUserId(){
        ArrayList<String> test = new ArrayList<String>();
        test.add("1");
        String targeturl = "http://192.168.1.52:9200/user_table/_search?_source_include=hits";
        try
        {
            URL restServiceURL = new URL(targeturl);
            HttpURLConnection httpConnection = (HttpURLConnection)restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            if(httpConnection.getResponseCode() != 200)
            {
                return null;
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String output = "";
            String temp = "";
            while((temp = responseBuffer.readLine()) != null)
            {
                output += temp;
            }
            httpConnection.disconnect();
            if(output.equals(""))
            {
                return null;
            }
            else
            {
                JSONObject dataJson = new JSONObject(output);
                if(dataJson.has("hits"))
                {
                    JSONObject hitsJson = new JSONObject();
                    hitsJson = dataJson.getJSONObject("hits");
                    if(hitsJson.has("hits")){
                        ArrayList<String> allUserList = new ArrayList<String>();
                        JSONArray allUser = hitsJson.getJSONArray("hits");
                        for(int i = 0; i < allUser.length(); i++){
                            JSONObject oneUser = new JSONObject();
                            oneUser = (JSONObject) allUser.get(i);
                            String id = oneUser.getString("_type");
                            allUserList.add(id);
                        }
                        return allUserList;
                    }else {
                        return null;
                    }
                }
                else
                {
                    return null;
                }
            }
        }catch(Exception e)
        {
            return null;
        }
    }

    public static ArrayList<String> getUserName(ArrayList<String> allId){
        ArrayList<String> allName = new ArrayList<String>();
        for(int i = 0; i < allId.size(); i++){
            String id = allId.get(i);
            String targeturl = "http://192.168.1.52:9200/user_table/" + id + "/" + id + "/_source";
            try
            {
                URL restServiceURL = new URL(targeturl);
                HttpURLConnection httpConnection = (HttpURLConnection)restServiceURL.openConnection();
                httpConnection.setRequestMethod("GET");
                httpConnection.setRequestProperty("Accept", "application/json");
                if(httpConnection.getResponseCode() != 200)
                {
                    return null;
                }
                BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
                String output = "";
                String temp = "";
                while((temp = responseBuffer.readLine()) != null)
                {
                    output += temp;
                }
                httpConnection.disconnect();
                if(output.equals(""))
                {
                    return null;
                }
                else
                {
                    JSONObject dataJson = new JSONObject(output);
                    if(dataJson.has("name"))
                    {
                        String name = dataJson.getString("name");
                        allName.add(name);
                    }
                    else
                    {
                        return null;
                    }
                }
            }catch(Exception e)
            {
                return null;
            }
        }
        return allName;
    }
}
