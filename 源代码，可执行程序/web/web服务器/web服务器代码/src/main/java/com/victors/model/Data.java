package com.victors.model;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by Victors on 2017/8/20.
 */
public class Data {
    public static String getData(String index, String type, String id){
        String targeturl = "http://192.168.1.52:9200/" + index + "/" + type + "/" + id + "/_source";
        try{
            URL restServiceURL = new URL(targeturl);
            HttpURLConnection httpConnection = (HttpURLConnection)restServiceURL.openConnection();
            httpConnection.setRequestMethod("GET");
            httpConnection.setRequestProperty("Accept", "application/json");
            if(httpConnection.getResponseCode() != 200)
            {
                return "";
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String output = "";
            String temp = "";
            while((temp = responseBuffer.readLine()) != null)
            {
                output += temp;
            }
            httpConnection.disconnect();
            return output;
        }catch (Exception e){
            return "";
        }
    }

    public static String getRealTimeData(String wid){
        String targeturl = "http://192.168.1.52:9200/sou/tz_" + wid + "/_search";
        try{
            URL restServiceURL = new URL(targeturl);
            HttpURLConnection httpConnection = (HttpURLConnection)restServiceURL.openConnection();
            httpConnection.setDoOutput(true);
            httpConnection.setRequestMethod("POST");
            httpConnection.setRequestProperty("Accept", "application/json");
            JSONObject obj = new JSONObject();
            obj.accumulate("size", 0);
            JSONObject aggs = new JSONObject();
            JSONObject range1 = new JSONObject();
            JSONObject date_range = new JSONObject();
            date_range.accumulate("field", "@timestamp");
            ArrayList<JSONObject> ranges = new ArrayList<JSONObject>();
            JSONObject ranges_obj = new JSONObject();
            ranges_obj.accumulate("from", "now-110s/s");
            ranges_obj.accumulate("to", "now/s");
            ranges.add(ranges_obj);
            date_range.accumulate("ranges", ranges);
            range1.accumulate("date_range", date_range);
            JSONObject inner_aggs = new JSONObject();
            JSONObject range2 = new JSONObject();
            JSONObject date_histogram = new JSONObject();
            date_histogram.accumulate("field", "@timestamp");
            date_histogram.accumulate("interval", "10s");
            range2.accumulate("date_histogram", date_histogram);
            JSONObject aggs_3 = new JSONObject();
            JSONObject data = new JSONObject();
            JSONObject nested = new JSONObject();
            nested.accumulate("path", "data");
            data.accumulate("nested", nested);
            JSONObject aggs_4 = new JSONObject();
            JSONObject in_store = new JSONObject();
            JSONObject range = new JSONObject();
            range.accumulate("field", "data.range");
            JSONObject temp_ranges = new JSONObject();
            temp_ranges.accumulate("from", 0);
            temp_ranges.accumulate("to", 30);
            range.accumulate("ranges", temp_ranges);
            in_store.accumulate("range", range);
            aggs_4.accumulate("in_store", in_store);
            JSONObject all_store = new JSONObject();
            JSONObject temp_range = new JSONObject();
            temp_range.accumulate("field", "data.range");
            JSONObject ranges_last = new JSONObject();
            ranges_last.accumulate("from", 0);
            temp_range.accumulate("ranges", ranges_last);
            all_store.accumulate("range", temp_range);
            aggs_4.accumulate("all_store", all_store);
            data.accumulate("aggs", aggs_4);
            aggs_3.accumulate("data", data);
            range2.accumulate("aggs", aggs_3);
            inner_aggs.accumulate("range2", range2);
            range1.accumulate("aggs", inner_aggs);
            aggs.accumulate("range1", range1);
            obj.accumulate("aggs", aggs);
            String input = obj.toString();
            OutputStream outputStream = httpConnection.getOutputStream();
            outputStream.write(input.getBytes("utf-8"));
            outputStream.flush();
            if(httpConnection.getResponseCode() != 200 && httpConnection.getResponseCode() != 201)
            {
                return "error:请求出错！" + httpConnection.getResponseCode();
            }
            BufferedReader responseBuffer = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
            String output = "";
            String temp = "";
            while((temp = responseBuffer.readLine()) != null)
            {
                output += temp;
            }
            return output;
        }catch (Exception e){
            return "error:请求elasticsearch出错！";
        }
    }
}
