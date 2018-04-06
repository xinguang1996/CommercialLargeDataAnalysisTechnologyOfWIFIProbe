package com.victors.model;

import org.json.JSONObject;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InputMismatchException;

/**
 * Created by Victors on 2017/8/30.
 */
public class UserData {

    public static ArrayList<ArrayList> getUserTheAmountOfStore(String userId){
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        String type = userId + "-day";
        String id = "";
        ArrayList<Integer> the_store_amount = new ArrayList<Integer>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        int start_year;//开始时间年
        int start_month;//开始时间月
        int start_day;//开始时间日
        int start_hour;//开始时间小时
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        //下面为展示开始时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //初始化开始时间
        start_hour = 0;
        start_year = year;
        start_month = month;
        //如果时间为0时刻，则昨天数据不可获取
        if(hour == 0)
        {
            start_day = day - MAX_X;
        }
        else
        {
            start_day = day - MAX_X + 1;

            //可以算前一日的
            day++;
            if(day > GetDayBaseYearAndMonth.getDay(year, month))
            {
                day = 1;
                month++;
                if(month > 12)
                {
                    year++;
                }
            }
        }
        //开始时间日为非正数时变换计算开始时间
        if(start_day <= 0)
        {
            start_month = start_month - 1;
            if(start_month == 0)
            {
                start_month = 12;
                start_year = start_year - 1;
            }
            start_day = GetDayBaseYearAndMonth.getDay(start_year, start_month) + start_day;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_month = start_month;
        temp_start_day = start_day - 1;
        if(temp_start_day <= 0)
        {
            temp_start_month = temp_start_month - 1;
            if(temp_start_month == 0)
            {
                temp_start_month = 12;
                temp_start_year = temp_start_year - 1;
            }
            temp_start_day = GetDayBaseYearAndMonth.getDay(temp_start_year, temp_start_month) + temp_start_day;
        }
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        int temp_day = start_day;//用于循环临时日
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        int show_day;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month;
            show_day = temp_day - 1;
            if(show_day <= 0)
            {
                show_month = show_month - 1;
                if(show_month == 0)
                {
                    show_month = 12;
                    show_year = show_year - 1;
                }
                show_day = GetDayBaseYearAndMonth.getDay(show_year, show_month) + show_day;
            }
            show_end_time = show_year + "-" + ProcessNumber.processNumber(show_month) + "-" + ProcessNumber.processNumber(show_day);
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month) + "." + ProcessNumber.processNumber(show_day);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String traffic_amount;
            String the_amount_of_store;
            if(data.equals(""))
            {
                traffic_amount = "0";
                the_amount_of_store = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The amount of store")){
                    the_amount_of_store = dataJson.getString("The amount of store");//入店量
                }else{
                    the_amount_of_store = "0";
                }
            }
            the_store_amount.add(Integer.parseInt(the_amount_of_store));//加入入店量
            //处理下一个时间
            temp_day++;
            if(temp_day > GetDayBaseYearAndMonth.getDay(temp_year, temp_month))
            {
                temp_day = 1;
                temp_month++;
                if(temp_month > 12)
                {
                    temp_month = 1;
                    temp_year++;
                }
            }
            if(temp_year == year && temp_month == month && temp_day == day)
            {
                break;
            }
        }
        //创建并处理
        ArrayList<ArrayList> result = new ArrayList<ArrayList>();
        result.add(the_store_amount);
        result.add(time);
        return result;
    }

    public static ArrayList<ArrayList> getUserIntoTheStoreRate(String user_id){
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        String type = user_id + "-day";
        String id = "";
        ArrayList<Float> the_into_the_store_rate = new ArrayList<Float>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        int start_year;//开始时间年
        int start_month;//开始时间月
        int start_day;//开始时间日
        int start_hour;//开始时间小时
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        //下面为展示开始时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //初始化开始时间
        start_hour = 0;
        start_year = year;
        start_month = month;
        //如果时间为0时刻，则昨天数据不可获取
        if(hour == 0)
        {
            start_day = day - MAX_X;
        }
        else
        {
            start_day = day - MAX_X + 1;

            //可以算前一日的
            day++;
            if(day > GetDayBaseYearAndMonth.getDay(year, month))
            {
                day = 1;
                month++;
                if(month > 12)
                {
                    year++;
                }
            }
        }
        //开始时间日为非正数时变换计算开始时间
        if(start_day <= 0)
        {
            start_month = start_month - 1;
            if(start_month == 0)
            {
                start_month = 12;
                start_year = start_year - 1;
            }
            start_day = GetDayBaseYearAndMonth.getDay(start_year, start_month) + start_day;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_month = start_month;
        temp_start_day = start_day - 1;
        if(temp_start_day <= 0)
        {
            temp_start_month = temp_start_month - 1;
            if(temp_start_month == 0)
            {
                temp_start_month = 12;
                temp_start_year = temp_start_year - 1;
            }
            temp_start_day = GetDayBaseYearAndMonth.getDay(temp_start_year, temp_start_month) + temp_start_day;
        }
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        int temp_day = start_day;//用于循环临时日
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        int show_day;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id =  temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month;
            show_day = temp_day - 1;
            if(show_day <= 0)
            {
                show_month = show_month - 1;
                if(show_month == 0)
                {
                    show_month = 12;
                    show_year = show_year - 1;
                }
                show_day = GetDayBaseYearAndMonth.getDay(show_year, show_month) + show_day;
            }
            show_end_time = show_year + "-" + ProcessNumber.processNumber(show_month) + "-" + ProcessNumber.processNumber(show_day);
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month) + "." + ProcessNumber.processNumber(show_day);
            time.add(s_time);
            String data = Data.getData(index, type, id);;
            String into_the_store_rate;
            if(data.equals(""))
            {
                into_the_store_rate = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Into the store rate")){
                    into_the_store_rate = dataJson.getString("Into the store rate");//入店率
                }else{
                    into_the_store_rate = "0";
                }
            }
            the_into_the_store_rate.add(Float.parseFloat(into_the_store_rate));//加入入店率数据
            //处理下一个时间
            temp_day++;
            if(temp_day > GetDayBaseYearAndMonth.getDay(temp_year, temp_month))
            {
                temp_day = 1;
                temp_month++;
                if(temp_month > 12)
                {
                    temp_month = 1;
                    temp_year++;
                }
            }
            if(temp_year == year && temp_month == month && temp_day == day)
            {
                break;
            }
        }
        //创建并处理
        ArrayList<ArrayList> result = new ArrayList<ArrayList>();
        result.add(the_into_the_store_rate);
        result.add(time);
        return result;
    }

    public static ArrayList<Integer> getAllTrafficMount(ArrayList<String> allId){
        ArrayList<Integer> allTrafficMount = new ArrayList<Integer>();
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        //依次获取系统时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        if(hour == 0){
            day--;
            if(day == 0){
                month--;
                //如果开始时间月为0时，进行变换计算
                if(month == 0)
                {
                    month = 12;
                    year = year - 1;
                    day = 31;
                }
                else
                {
                    day = GetDayBaseYearAndMonth.getDay(year, month);
                }
            }
        }
        hour = 0;
        //组合id
        String id = year + "-" + ProcessNumber.processNumber(month) + "-" + ProcessNumber.processNumber(day) + "-" + ProcessNumber.processNumber(hour);
        String index = "res";
        String type = "";
        for(int i = 0; i < allId.size(); i++){
            String traffic_amount;
            type = allId.get(i) + "-week";
            String data = Data.getData(index, type, id);
            if(data.equals("")){
                traffic_amount = "0";
            }else{
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Traffic amount")) {
                    traffic_amount = dataJson.getString("Traffic amount");//客流量
                }else{
                    traffic_amount = "0";
                }
            }
            allTrafficMount.add(Integer.parseInt(traffic_amount));
        }
        return allTrafficMount;
    }

    public static ArrayList<Integer> getAllTheAmountOfStore(ArrayList<String> allId){
        ArrayList<Integer> allTheAmountOfStore = new ArrayList<Integer>();
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        //依次获取系统时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        if(hour == 0){
            day--;
            if(day == 0){
                month--;
                //如果开始时间月为0时，进行变换计算
                if(month == 0)
                {
                    month = 12;
                    year = year - 1;
                    day = 31;
                }
                else
                {
                    day = GetDayBaseYearAndMonth.getDay(year, month);
                }
            }
        }
        hour = 0;
        //组合id
        String id = year + "-" + ProcessNumber.processNumber(month) + "-" + ProcessNumber.processNumber(day) + "-" + ProcessNumber.processNumber(hour);
        String index = "res";
        String type = "";
        for(int i = 0; i < allId.size(); i++){
            String the_traffic_of_store;
            type = allId.get(i) + "-week";
            String data = Data.getData(index, type, id);
            if(data.equals("")){
                the_traffic_of_store = "0";
            }else{
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The amount of store")) {
                    the_traffic_of_store = dataJson.getString("The amount of store");
                }else{
                    the_traffic_of_store = "0";
                }
            }
            allTheAmountOfStore.add(Integer.parseInt(the_traffic_of_store));
        }
        return allTheAmountOfStore;
    }

    public static ArrayList<Float> getAllIntoTheStoreRate(ArrayList<String> allId){
        ArrayList<Float> allIntoTheStoreRate = new ArrayList<Float>();
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        //依次获取系统时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        if(hour == 0){
            day--;
            if(day == 0){
                month--;
                //如果开始时间月为0时，进行变换计算
                if(month == 0)
                {
                    month = 12;
                    year = year - 1;
                    day = 31;
                }
                else
                {
                    day = GetDayBaseYearAndMonth.getDay(year, month);
                }
            }
        }
        hour = 0;
        //组合id
        String id = year + "-" + ProcessNumber.processNumber(month) + "-" + ProcessNumber.processNumber(day) + "-" + ProcessNumber.processNumber(hour);
        String index = "res";
        String type = "";
        for(int i = 0; i < allId.size(); i++){
            String into_the_store_rate;
            type = allId.get(i) + "-week";
            String data = Data.getData(index, type, id);
            if(data.equals("")){
                into_the_store_rate = "0";
            }else{
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Into the store rate")) {
                    into_the_store_rate = dataJson.getString("Into the store rate");
                }else{
                    into_the_store_rate = "0";
                }
            }
            allIntoTheStoreRate.add(Float.parseFloat(into_the_store_rate));
        }
        return allIntoTheStoreRate;
    }

    public static ArrayList<Float> getAllDeepRate(ArrayList<String> allId){
        ArrayList<Float> allDeepRate = new ArrayList<Float>();
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        //依次获取系统时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        if(hour == 0){
            day--;
            if(day == 0){
                month--;
                //如果开始时间月为0时，进行变换计算
                if(month == 0)
                {
                    month = 12;
                    year = year - 1;
                    day = 31;
                }
                else
                {
                    day = GetDayBaseYearAndMonth.getDay(year, month);
                }
            }
        }
        hour = 0;
        //组合id
        String id = year + "-" + ProcessNumber.processNumber(month) + "-" + ProcessNumber.processNumber(day) + "-" + ProcessNumber.processNumber(hour);
        String index = "res";
        String type = "";
        for(int i = 0; i < allId.size(); i++){
            String deep_rate;
            type = allId.get(i) + "-week";
            String data = Data.getData(index, type, id);
            if(data.equals("")){
                deep_rate = "0";
            }else{
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Deep rate")) {
                    deep_rate = dataJson.getString("Deep rate");
                }else{
                    deep_rate = "0";
                }
            }
            allDeepRate.add(Float.parseFloat(deep_rate));
        }
        return allDeepRate;
    }

    public static ArrayList<Float> getAllBounceRate(ArrayList<String> allId){
        ArrayList<Float> allBounceRate = new ArrayList<Float>();
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        //依次获取系统时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        if(hour == 0){
            day--;
            if(day == 0){
                month--;
                //如果开始时间月为0时，进行变换计算
                if(month == 0)
                {
                    month = 12;
                    year = year - 1;
                    day = 31;
                }
                else
                {
                    day = GetDayBaseYearAndMonth.getDay(year, month);
                }
            }
        }
        hour = 0;
        //组合id
        String id = year + "-" + ProcessNumber.processNumber(month) + "-" + ProcessNumber.processNumber(day) + "-" + ProcessNumber.processNumber(hour);
        String index = "res";
        String type = "";
        for(int i = 0; i < allId.size(); i++){
            String bounce_rate;
            type = allId.get(i) + "-week";
            String data = Data.getData(index, type, id);
            if(data.equals("")){
                bounce_rate = "0";
            }else{
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Bounce rate")) {
                    bounce_rate = dataJson.getString("Bounce rate");
                }else{
                    bounce_rate = "0";
                }
            }
            allBounceRate.add(Float.parseFloat(bounce_rate));
        }
        return allBounceRate;
    }
}
