package com.victors;

import com.victors.model.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.cglib.transform.impl.InterceptFieldCallback;
import org.springframework.expression.spel.ast.FloatLiteral;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Victors on 2017/8/20.
 */
@RestController
public class DataShowController {

    @RequestMapping(value="/get_traffic_amount_and_the_amount_of_store_hour")
    public String getTrafficAmountAndAmountOfStoreHour(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-hour";
        String id = "";
        ArrayList<Integer> the_traffic = new ArrayList<Integer>();
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
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //计算开始时间
        start_hour = hour - MAX_X;
        start_year = year;
        start_month = month;
        start_day = day;
        //开始时间时为非整数时变换计算开始时间
        if(start_hour <= 0)
        {
            start_day = day - 1;
            //如果开始时间日为0时，进行变换计算
            if(start_day == 0)
            {
                start_month = start_month - 1;
                //如果开始时间月为0时，进行变换计算
                if(start_month == 0)
                {
                    start_month = 12;
                    start_year = start_year - 1;
                    start_day = 31;
                }
                else
                {
                    start_day = GetDayBaseYearAndMonth.getDay(start_year, start_month);
                }
            }
            if(start_hour < 0)
            {
                start_hour = 24 + start_hour;
            }
        }
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + " " + ProcessNumber.processNumber(start_hour);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        int temp_day = start_day;//用于循环临时日
        int temp_hour = start_hour;//用于循环临时小时
        int s_hour, s_day, s_month, s_year;//用于显示时间
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(temp_hour);
            show_end_time = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + " " + ProcessNumber.processNumber(temp_hour);
            //组合时间
            if(temp_hour == 0)
            {
                s_hour = 24;
                s_day = temp_day - 1;
                s_month = temp_month;
                s_year = temp_year;
                if(s_day <= 0)
                {
                    s_month--;
                    if(s_month <= 0)
                    {
                        s_year--;
                    }
                    s_day = GetDayBaseYearAndMonth.getDay(s_year, s_month);
                }
            }
            else
            {
                s_year = temp_year;
                s_month = temp_month;
                s_day = temp_day;
                s_hour = temp_hour;
            }
            s_time = ProcessNumber.processNumber(s_month) + "." + ProcessNumber.processNumber(s_day) + "." + ProcessNumber.processNumber(s_hour);
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
                if(dataJson.has("Traffic amount")) {
                    traffic_amount = dataJson.getString("Traffic amount");//客流量
                }else{
                    traffic_amount = "0";
                }
                if(dataJson.has("The amount of store")) {
                    the_amount_of_store = dataJson.getString("The amount of store");//入店量
                }else{
                    the_amount_of_store = "0";
                }
            }
            the_traffic.add(Integer.parseInt(traffic_amount));//加入客流量数据
            the_store_amount.add(Integer.parseInt(the_amount_of_store));//加入入店量
            //处理下一个时间
            temp_hour++;
            if(temp_hour >= 24)
            {
                temp_hour = 0;
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
            }
            if(temp_year == year && temp_month == month && temp_day == day && temp_hour == hour)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("traffic", the_traffic);
        new_json_obj.accumulate("store_amount", the_store_amount);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_the_new_and_old_customers_hour")
    public String getTheNewAndOldCustomersHour(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-hour";
        String id = "";
        ArrayList<Integer> the_new_customers = new ArrayList<Integer>();//新顾客
        ArrayList<Integer> the_old_customers = new ArrayList<Integer>();//老顾客
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
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //计算开始时间
        start_hour = hour - MAX_X;
        start_year = year;
        start_month = month;
        start_day = day;
        //开始时间时为非整数时变换计算开始时间
        if(start_hour <= 0)
        {
            start_day = day - 1;
            //如果开始时间日为0时，进行变换计算
            if(start_day == 0)
            {
                start_month = start_month - 1;
                //如果开始时间月为0时，进行变换计算
                if(start_month == 0)
                {
                    start_month = 12;
                    start_year = start_year - 1;
                    start_day = 31;
                }
                else
                {
                    start_day = GetDayBaseYearAndMonth.getDay(start_year, start_month);
                }
            }
            if(start_hour < 0)
            {
                start_hour = 24 + start_hour;
            }
        }
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + " " + ProcessNumber.processNumber(start_hour);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        int temp_day = start_day;//用于循环临时日
        int temp_hour = start_hour;//用于循环临时小时
        int s_hour, s_day, s_month, s_year;//用于显示时间
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(temp_hour);
            show_end_time = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + " " + ProcessNumber.processNumber(temp_hour);
            //组合时间
            if(temp_hour == 0)
            {
                s_hour = 24;
                s_day = temp_day - 1;
                s_month = temp_month;
                s_year = temp_year;
                if(s_day <= 0)
                {
                    s_month--;
                    if(s_month <= 0)
                    {
                        s_year--;
                    }
                    s_day = GetDayBaseYearAndMonth.getDay(s_year, s_month);
                }
            }
            else
            {
                s_year = temp_year;
                s_month = temp_month;
                s_day = temp_day;
                s_hour = temp_hour;
            }
            s_time = ProcessNumber.processNumber(s_month) + "." + ProcessNumber.processNumber(s_day) + "." + ProcessNumber.processNumber(s_hour);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String new_customer;//新顾客
            String old_customer;//老顾客
            if(data.equals(""))
            {
                new_customer = "0";
                old_customer = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The new and old customers")){
                    JSONObject new_and_old_customers = dataJson.getJSONObject("The new and old customers");//新老顾客
                    if(new_and_old_customers.has("new")){
                        new_customer = new_and_old_customers.getString("new");//新顾客
                    }else{
                        new_customer = "0";
                    }
                    if(new_and_old_customers.has("old")){
                        old_customer = new_and_old_customers.getString("old");//老顾客
                    }else{
                        old_customer = "0";
                    }
                }else{
                    new_customer = "0";
                    old_customer = "0";
                }
            }
            the_new_customers.add(Integer.parseInt(new_customer));
            the_old_customers.add(Integer.parseInt(old_customer));
            //处理下一个时间
            temp_hour++;
            if(temp_hour >= 24)
            {
                temp_hour = 0;
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
            }
            if(temp_year == year && temp_month == month && temp_day == day && temp_hour == hour)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("new_customers", the_new_customers);
        new_json_obj.accumulate("old_customers", the_old_customers);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_traffic_amount_and_the_amount_of_store_day")
    public String getTrafficAmountAndTheAmountOfStoreDay(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
        String id = "";
        ArrayList<Integer> the_traffic = new ArrayList<Integer>();
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
            //String data = GetESData.select_data(host, port, index, type);
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
                if(dataJson.has("Traffic amount")){
                    traffic_amount = dataJson.getString("Traffic amount");//客流量
                }else{
                    traffic_amount = "0";
                }
                if(dataJson.has("The amount of store")){
                    the_amount_of_store = dataJson.getString("The amount of store");//入店量
                }else{
                    the_amount_of_store = "0";
                }
            }
            the_traffic.add(Integer.parseInt(traffic_amount));//加入客流量数据
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
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("traffic", the_traffic);
        new_json_obj.accumulate("store_amount", the_store_amount);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_the_new_and_old_customers_day")
    public String getTheNewAndOldCustomersDay(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
        String id = "";
        ArrayList<Integer> the_new_customers = new ArrayList<Integer>();//新顾客
        ArrayList<Integer> the_old_customers = new ArrayList<Integer>();//老顾客
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
            String new_customer;//新顾客
            String old_customer;//老顾客
            if(data.equals(""))
            {
                new_customer = "0";
                old_customer = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The new and old customers")){
                    JSONObject new_and_old_customers = dataJson.getJSONObject("The new and old customers");//新老顾客
                    if(new_and_old_customers.has("new")){
                        new_customer = new_and_old_customers.getString("new");//新顾客
                    }else{
                        new_customer = "0";
                    }
                    if(new_and_old_customers.has("old")){
                        old_customer = new_and_old_customers.getString("old");//老顾客
                    }else{
                        old_customer = "0";
                    }
                }else{
                    new_customer = "0";
                    old_customer = "0";
                }
            }
            the_new_customers.add(Integer.parseInt(new_customer));
            the_old_customers.add(Integer.parseInt(old_customer));
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
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("new_customers", the_new_customers);
        new_json_obj.accumulate("old_customers", the_old_customers);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_traffic_amount_and_the_amount_of_store_week")
    public String getTrafficAmountAndTheAmountOfStoreWeek(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        ArrayList<Integer> the_traffic = new ArrayList<Integer>();
        ArrayList<Integer> the_store_amount = new ArrayList<Integer>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        int week;//系统时间周
        int day_of_week;//系统时间周的第几天
        int start_year;//开始时间年
        int start_month;//开始时间月
        int start_day;//开始时间日
        int start_hour;//开始时间小时
        int start_week;//开始时间周
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        week = system_time.get(Calendar.WEEK_OF_YEAR);
        day_of_week = system_time.get(Calendar.DAY_OF_WEEK);
        if(week == 1)
        {
            if(day > 7)
            {
                year++;
            }
        }
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = day;
        start_month = month;
        start_week = week;
        //如果为周日，则处理
        if(day_of_week == 1)
        {
            //如果时为0，则上周数据不可取
            if(hour == 0)
            {
                start_week = week - MAX_X;
            }
            else
            {
                start_week = week - MAX_X + 1;
                week++;
                temp_time.set(Calendar.YEAR, year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
                {
                    week = 1;
                    year++;
                }
            }
        }
        else
        {
            start_week = week - MAX_X + 1;
            week++;
            temp_time.set(Calendar.YEAR, year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                week = 1;
                year++;
            }
        }
        //处理非正数周
        if(start_week <= 0)
        {
            start_year--;
            temp_time.set(Calendar.YEAR, start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + start_week;
        }
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;

        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = + temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }
            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            time.add(s_time);
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
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
                if(dataJson.has("Traffic amount")){
                    traffic_amount = dataJson.getString("Traffic amount");//客流量
                }else{
                    traffic_amount = "0";
                }
                if(dataJson.has("The amount of store")){
                    the_amount_of_store = dataJson.getString("The amount of store");//入店量
                }else{
                    the_amount_of_store = "0";
                }
            }
            the_traffic.add(Integer.parseInt(traffic_amount));//加入客流量数据
            the_store_amount.add(Integer.parseInt(the_amount_of_store));//加入入店量
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == year && temp_week == week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("traffic", the_traffic);
        new_json_obj.accumulate("store_amount", the_store_amount);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_the_new_and_old_customers_week")
    public String getTheNewAndOldCustomersWeek(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        ArrayList<Integer> the_new_customers = new ArrayList<Integer>();//新顾客
        ArrayList<Integer> the_old_customers = new ArrayList<Integer>();//老顾客
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        int week;//系统时间周
        int day_of_week;//系统时间周的第几天
        int start_year;//开始时间年
        int start_month;//开始时间月
        int start_day;//开始时间日
        int start_hour;//开始时间小时
        int start_week;//开始时间周
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        week = system_time.get(Calendar.WEEK_OF_YEAR);
        day_of_week = system_time.get(Calendar.DAY_OF_WEEK);
        if(week == 1)
        {
            if(day > 7)
            {
                year++;
            }
        }
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = day;
        start_month = month;
        start_week = week;
        //如果为周日，则处理
        if(day_of_week == 1)
        {
            //如果时为0，则上周数据不可取
            if(hour == 0)
            {
                start_week = week - MAX_X;
            }
            else
            {
                start_week = week - MAX_X + 1;
                week++;
                temp_time.set(Calendar.YEAR, year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
                {
                    week = 1;
                    year++;
                }
            }
        }
        else
        {
            start_week = week - MAX_X + 1;
            week++;
            temp_time.set(Calendar.YEAR, year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                week = 1;
                year++;
            }
        }
        //处理非正数周
        if(start_week <= 0)
        {
            start_year--;
            temp_time.set(Calendar.YEAR, start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + start_week;
        }
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;

        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }
            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            time.add(s_time);
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
            //String data = GetESData.select_data(host, port, index, type);
            String data = Data.getData(index, type, id);
            String new_customer;//新顾客
            String old_customer;//老顾客
            if(data.equals(""))
            {
                new_customer = "0";
                old_customer = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The new and old customers")){
                    JSONObject new_and_old_customers = dataJson.getJSONObject("The new and old customers");//新老顾客
                    if(new_and_old_customers.has("new")){
                        new_customer = new_and_old_customers.getString("new");//新顾客
                    }else{
                        new_customer = "0";
                    }
                    if(new_and_old_customers.has("old")){
                        old_customer = new_and_old_customers.getString("old");//老顾客
                    }else{
                        old_customer = "0";
                    }
                }else{
                    new_customer = "0";
                    old_customer = "0";
                }
            }
            the_new_customers.add(Integer.parseInt(new_customer));
            the_old_customers.add(Integer.parseInt(old_customer));
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == year && temp_week == week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("new_customers", the_new_customers);
        new_json_obj.accumulate("old_customers", the_old_customers);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_traffic_amount_and_the_amount_of_store_month")
    public String getTrafficAmountAndAmountOfStoreMonth(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        ArrayList<Integer> the_traffic = new ArrayList<Integer>();
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
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = 1;
        //如果日为1，则处理
        if(day == 1)
        {
            //如果时为0，则上月数据不可获取
            if(hour == 0)
            {
                start_month = month - MAX_X;
            }
            else
            {
                start_month = month - MAX_X + 1;
                month++;
                if(month > 12)
                {
                    month = 1;
                    year++;
                }
            }
        }
        else
        {
            start_month = month - MAX_X +1;
            month++;
            if(month > 12)
            {
                month = 1;
                year++;
            }
        }
        //处理非正数月
        if(start_month <= 0)
        {
            start_year--;
            start_month = 12 + start_month;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_month = start_month - 1;
        if(temp_start_month <= 0)
        {
            temp_start_year--;
            temp_start_month = 12 + temp_start_month;
        }
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            show_end_time = show_year + "-" + ProcessNumber.processNumber(show_month);
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
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
                if(dataJson.has("Traffic amount")){
                    traffic_amount = dataJson.getString("Traffic amount");//客流量
                }else{
                    traffic_amount = "0";
                }
                if(dataJson.has("The amount of store")){
                    the_amount_of_store = dataJson.getString("The amount of store");//入店量e
                }else{
                    the_amount_of_store = "0";
                }
            }
            the_traffic.add(Integer.parseInt(traffic_amount));//加入客流量数据
            the_store_amount.add(Integer.parseInt(the_amount_of_store));//加入入店量
            //处理下一个时间
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == year && temp_month == month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("traffic", the_traffic);
        new_json_obj.accumulate("store_amount", the_store_amount);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_the_new_and_old_customers_month")
    public String getTheNewAndOldCustomersMonth(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        ArrayList<Integer> the_new_customers = new ArrayList<Integer>();//新顾客
        ArrayList<Integer> the_old_customers = new ArrayList<Integer>();//老顾客
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
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = 1;
        //如果日为1，则处理
        if(day == 1)
        {
            //如果时为0，则上月数据不可获取
            if(hour == 0)
            {
                start_month = month - MAX_X;
            }
            else
            {
                start_month = month - MAX_X + 1;
                month++;
                if(month > 12)
                {
                    month = 1;
                    year++;
                }
            }
        }
        else
        {
            start_month = month - MAX_X +1;
            month++;
            if(month > 12)
            {
                month = 1;
                year++;
            }
        }
        //处理非正数月
        if(start_month <= 0)
        {
            start_year--;
            start_month = 12 + start_month;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_month = start_month - 1;
        if(temp_start_month <= 0)
        {
            temp_start_year--;
            temp_start_month = 12 + temp_start_month;
        }
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            show_end_time = show_year + "-" + ProcessNumber.processNumber(show_month);
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String new_customer;//新顾客
            String old_customer;//老顾客
            if(data.equals(""))
            {
                new_customer = "0";
                old_customer = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The new and old customers")){
                    JSONObject new_and_old_customers = dataJson.getJSONObject("The new and old customers");//新老顾客
                    if(new_and_old_customers.has("new")){
                        new_customer = new_and_old_customers.getString("new");//新顾客
                    }else{
                        new_customer = "0";
                    }
                    if(new_and_old_customers.has("old")){
                        old_customer = new_and_old_customers.getString("old");//老顾客
                    }else{
                        old_customer = "0";
                    }
                }else{
                    new_customer = "0";
                    old_customer = "0";
                }
            }
            the_new_customers.add(Integer.parseInt(new_customer));
            the_old_customers.add(Integer.parseInt(old_customer));
            //处理下一个时间
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == year && temp_month == month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("new_customers", the_new_customers);
        new_json_obj.accumulate("old_customers", the_old_customers);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_deep_rate_and_bounce_rate_hour")
    public String getDeepRateAndBounceRateHour(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-hour";
        String id = "";
        ArrayList<Float> the_bounce_rate = new ArrayList<Float>();//跳出率
        ArrayList<Float> the_deep_rate = new ArrayList<Float>();//深访率
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
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //计算开始时间
        start_hour = hour - MAX_X;
        start_year = year;
        start_month = month;
        start_day = day;
        //开始时间时为非整数时变换计算开始时间
        if(start_hour <= 0)
        {
            start_day = day - 1;
            //如果开始时间日为0时，进行变换计算
            if(start_day == 0)
            {
                start_month = start_month - 1;
                //如果开始时间月为0时，进行变换计算
                if(start_month == 0)
                {
                    start_month = 12;
                    start_year = start_year - 1;
                    start_day = 31;
                }
                else
                {
                    start_day = GetDayBaseYearAndMonth.getDay(start_year, start_month);
                }
            }
            if(start_hour < 0)
            {
                start_hour = 24 + start_hour;
            }
        }
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + " " + ProcessNumber.processNumber(start_hour);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        int temp_day = start_day;//用于循环临时日
        int temp_hour = start_hour;//用于循环临时小时
        int s_hour, s_day, s_month, s_year;//用于显示时间
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(temp_hour);
            show_end_time = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + " " + ProcessNumber.processNumber(temp_hour);
            //组合时间
            if(temp_hour == 0)
            {
                s_hour = 24;
                s_day = temp_day - 1;
                s_month = temp_month;
                s_year = temp_year;
                if(s_day <= 0)
                {
                    s_month--;
                    if(s_month <= 0)
                    {
                        s_year--;
                    }
                    s_day = GetDayBaseYearAndMonth.getDay(s_year, s_month);
                }
            }
            else
            {
                s_year = temp_year;
                s_month = temp_month;
                s_day = temp_day;
                s_hour = temp_hour;
            }
            s_time = ProcessNumber.processNumber(s_month) + "." + ProcessNumber.processNumber(s_day) + "." + ProcessNumber.processNumber(s_hour);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String bounce_rate;//跳出率
            String deep_rate;//深访率
            if(data.equals(""))
            {
                bounce_rate = "0";
                deep_rate = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Bounce rate")){
                    bounce_rate = dataJson.getString("Bounce rate");//跳出率
                }else{
                    bounce_rate = "0";
                }
                if(dataJson.has("Deep rate")){
                    deep_rate = dataJson.getString("Deep rate");//深访率
                }else{
                    deep_rate = "0";
                }
            }
            the_bounce_rate.add(Float.parseFloat(bounce_rate));
            the_deep_rate.add(Float.parseFloat(deep_rate));
            //处理下一个时间
            temp_hour++;
            if(temp_hour >= 24)
            {
                temp_hour = 0;
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
            }
            if(temp_year == year && temp_month == month && temp_day == day && temp_hour == hour)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("bounce_rate", the_bounce_rate);
        new_json_obj.accumulate("deep_rate", the_deep_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_deep_rate_and_bounce_rate_day")
    public String getDeepRateAndBounceRateDay(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
        String id = "";
        ArrayList<Float> the_bounce_rate = new ArrayList<Float>();//跳出率
        ArrayList<Float> the_deep_rate = new ArrayList<Float>();//深访率
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
            //String data = GetESData.select_data(host, port, index, type);
            String data = Data.getData(index, type, id);
            String bounce_rate;//跳出率
            String deep_rate;//深访率
            if(data.equals(""))
            {
                bounce_rate = "0";
                deep_rate = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Bounce rate")){
                    bounce_rate = dataJson.getString("Bounce rate");//跳出率
                }else{
                    bounce_rate = "0";
                }
                if(dataJson.has("Deep rate")){
                    deep_rate = dataJson.getString("Deep rate");//深访率
                }else{
                    deep_rate = "0";
                }
            }
            the_bounce_rate.add(Float.parseFloat(bounce_rate));
            the_deep_rate.add(Float.parseFloat(deep_rate));
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
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("bounce_rate", the_bounce_rate);
        new_json_obj.accumulate("deep_rate", the_deep_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_deep_rate_and_bounce_rate_week")
    public String getDeepRateAndBounceRateWeek(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        ArrayList<Float> the_bounce_rate = new ArrayList<Float>();//跳出率
        ArrayList<Float> the_deep_rate = new ArrayList<Float>();//深访率
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        int week;//系统时间周
        int day_of_week;//系统时间周的第几天

        int start_year;//开始时间年
        int start_month;//开始时间月
        int start_day;//开始时间日
        int start_hour;//开始时间小时
        int start_week;//开始时间周
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        week = system_time.get(Calendar.WEEK_OF_YEAR);
        day_of_week = system_time.get(Calendar.DAY_OF_WEEK);
        if(week == 1)
        {
            if(day > 7)
            {
                year++;
            }
        }
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = day;
        start_month = month;
        start_week = week;
        //如果为周日，则处理
        if(day_of_week == 1)
        {
            //如果时为0，则上周数据不可取
            if(hour == 0)
            {
                start_week = week - MAX_X;
            }
            else
            {
                start_week = week - MAX_X + 1;
                week++;
                temp_time.set(Calendar.YEAR, year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
                {
                    week = 1;
                    year++;
                }
            }
        }
        else
        {
            start_week = week - MAX_X + 1;
            week++;
            temp_time.set(Calendar.YEAR, year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                week = 1;
                year++;
            }
        }
        //处理非正数周
        if(start_week <= 0)
        {
            start_year--;
            temp_time.set(Calendar.YEAR, start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + start_week;
        }
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;

        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }

            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            time.add(s_time);
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
            //String data = GetESData.select_data(host, port, index, type);
            String data = Data.getData(index, type, id);
            String bounce_rate;//跳出率
            String deep_rate;//深访率
            if(data.equals(""))
            {
                bounce_rate = "0";
                deep_rate = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Bounce rate")){
                    bounce_rate = dataJson.getString("Bounce rate");//跳出率
                }else{
                    bounce_rate = "0";
                }
                if(dataJson.has("Deep rate")){
                    deep_rate = dataJson.getString("Deep rate");//深访率
                }else{
                    deep_rate = "0";
                }
            }
            the_bounce_rate.add(Float.parseFloat(bounce_rate));
            the_deep_rate.add(Float.parseFloat(deep_rate));
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == year && temp_week == week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("bounce_rate", the_bounce_rate);
        new_json_obj.accumulate("deep_rate", the_deep_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_deep_rate_and_bounce_rate_month")
    public String getDeepRateAndBounceRateMonth(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        ArrayList<Float> the_bounce_rate = new ArrayList<Float>();//跳出率
        ArrayList<Float> the_deep_rate = new ArrayList<Float>();//深访率
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
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = 1;
        //如果日为1，则处理
        if(day == 1)
        {
            //如果时为0，则上月数据不可获取
            if(hour == 0)
            {
                start_month = month - MAX_X;
            }
            else
            {
                start_month = month - MAX_X + 1;
                month++;
                if(month > 12)
                {
                    month = 1;
                    year++;
                }
            }
        }
        else
        {
            start_month = month - MAX_X +1;
            month++;
            if(month > 12)
            {
                month = 1;
                year++;
            }
        }
        //处理非正数月
        if(start_month <= 0)
        {
            start_year--;
            start_month = 12 + start_month;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_month = start_month - 1;
        if(temp_start_month <= 0)
        {
            temp_start_year--;
            temp_start_month = 12 + temp_start_month;
        }
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id =  temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            show_end_time = show_year + "-" + ProcessNumber.processNumber(show_month);
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String bounce_rate;//跳出率
            String deep_rate;//深访率
            if(data.equals(""))
            {
                bounce_rate = "0";
                deep_rate = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Bounce rate")){
                    bounce_rate = dataJson.getString("Bounce rate");//跳出率
                }else{
                    bounce_rate = "0";
                }
                if(dataJson.has("Deep rate")){
                    deep_rate = dataJson.getString("Deep rate");//深访率
                }else{
                    deep_rate = "0";
                }
            }
            the_bounce_rate.add(Float.parseFloat(bounce_rate));
            the_deep_rate.add(Float.parseFloat(deep_rate));
            //处理下一个时间
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == year && temp_month == month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("bounce_rate", the_bounce_rate);
        new_json_obj.accumulate("deep_rate", the_deep_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_into_the_store_rate_month")
    public String getIntoTheStoreRateMonth(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
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
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = 1;
        //如果日为1，则处理
        if(day == 1)
        {
            //如果时为0，则上月数据不可获取
            if(hour == 0)
            {
                start_month = month - MAX_X;
            }
            else
            {
                start_month = month - MAX_X + 1;
                month++;
                if(month > 12)
                {
                    month = 1;
                    year++;
                }
            }
        }
        else
        {
            start_month = month - MAX_X +1;
            month++;
            if(month > 12)
            {
                month = 1;
                year++;
            }
        }
        //处理非正数月
        if(start_month <= 0)
        {
            start_year--;
            start_month = 12 + start_month;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_month = start_month - 1;
        if(temp_start_month <= 0)
        {
            temp_start_year--;
            temp_start_month = 12 + temp_start_month;
        }
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            show_end_time = show_year + "-" + ProcessNumber.processNumber(show_month);
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
            time.add(s_time);
            String data = Data.getData(index, type, id);
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
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == year && temp_month == month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("into_the_store_rate", the_into_the_store_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_into_the_store_rate_hour")
    public String getIntoTheStoreRateHour(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-hour";
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
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //计算开始时间
        start_hour = hour - MAX_X;
        start_year = year;
        start_month = month;
        start_day = day;
        //开始时间时为非整数时变换计算开始时间
        if(start_hour <= 0)
        {
            start_day = day - 1;
            //如果开始时间日为0时，进行变换计算
            if(start_day == 0)
            {
                start_month = start_month - 1;
                //如果开始时间月为0时，进行变换计算
                if(start_month == 0)
                {
                    start_month = 12;
                    start_year = start_year - 1;
                    start_day = 31;
                }
                else
                {
                    start_day = GetDayBaseYearAndMonth.getDay(start_year, start_month);
                }
            }
            if(start_hour < 0)
            {
                start_hour = 24 + start_hour;
            }
        }
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + " " + ProcessNumber.processNumber(start_hour);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        int temp_day = start_day;//用于循环临时日
        int temp_hour = start_hour;//用于循环临时小时
        int s_hour, s_day, s_month, s_year;//用于显示时间
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id =  temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(temp_hour);
            show_end_time = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + " " + ProcessNumber.processNumber(temp_hour);
            //组合时间
            if(temp_hour == 0)
            {
                s_hour = 24;
                s_day = temp_day - 1;
                s_month = temp_month;
                s_year = temp_year;
                if(s_day <= 0)
                {
                    s_month--;
                    if(s_month <= 0)
                    {
                        s_year--;
                    }
                    s_day = GetDayBaseYearAndMonth.getDay(s_year, s_month);
                }
            }
            else
            {
                s_year = temp_year;
                s_month = temp_month;
                s_day = temp_day;
                s_hour = temp_hour;
            }
            s_time = ProcessNumber.processNumber(s_month) + "." + ProcessNumber.processNumber(s_day) + "." + ProcessNumber.processNumber(s_hour);
            time.add(s_time);
            String data = Data.getData(index, type, id);
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
            temp_hour++;
            if(temp_hour >= 24)
            {
                temp_hour = 0;
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
            }
            if(temp_year == year && temp_month == month && temp_day == day && temp_hour == hour)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("into_the_store_rate", the_into_the_store_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_into_the_store_rate_day")
    public String getIntoTheStoreRateDay(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
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
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("into_the_store_rate", the_into_the_store_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_into_the_store_rate_week")
    public String getIntoTheStoreRateWeek(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 12;//表示横坐标最大的显示数目,修改时不要超过24
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        ArrayList<Float> the_into_the_store_rate = new ArrayList<Float>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        int week;//系统时间周
        int day_of_week;//系统时间周的第几天

        int start_year;//开始时间年
        int start_month;//开始时间月
        int start_day;//开始时间日
        int start_hour;//开始时间小时
        int start_week;//开始时间周
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        week = system_time.get(Calendar.WEEK_OF_YEAR);
        day_of_week = system_time.get(Calendar.DAY_OF_WEEK);
        if(week == 1)
        {
            if(day > 7)
            {
                year++;
            }
        }
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = day;
        start_month = month;
        start_week = week;
        //如果为周日，则处理
        if(day_of_week == 1)
        {
            //如果时为0，则上周数据不可取
            if(hour == 0)
            {
                start_week = week - MAX_X;
            }
            else
            {
                start_week = week - MAX_X + 1;
                week++;
                temp_time.set(Calendar.YEAR, year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
                {
                    week = 1;
                    year++;
                }
            }
        }
        else
        {
            start_week = week - MAX_X + 1;
            week++;
            temp_time.set(Calendar.YEAR, year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                week = 1;
                year++;
            }
        }
        //处理非正数周
        if(start_week <= 0)
        {
            start_year--;
            temp_time.set(Calendar.YEAR, start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + start_week;
        }
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH);
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;

        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }
            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            time.add(s_time);
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
            String data = Data.getData(index, type, id);
            String into_the_store_rate;
            if(data.equals(""))
            {
                into_the_store_rate = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                into_the_store_rate = dataJson.getString("Into the store rate");//入店率
            }
            the_into_the_store_rate.add(Float.parseFloat(into_the_store_rate));//加入入店率数据
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == year && temp_week == week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("into_the_store_rate", the_into_the_store_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_the_resident_time_day")
    public String getTheResidentTimeDay(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 1;
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
        String id = "";
        ArrayList<String> the_resident_time_time = new ArrayList<String>();
        ArrayList<String> the_resident_time_number = new ArrayList<String>();
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
        int second_per_minute = 60;//表示一分钟有60秒
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
            //组合索引名
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
            String data = Data.getData(index, type, id);
            ArrayList<ArrayList> resident_time = new ArrayList<ArrayList>();
            if(data.equals(""))
            {
                the_resident_time_time.add(0, "0");
                the_resident_time_number.add(0, "0");
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The resident time")){
                    JSONArray jsonArray = dataJson.getJSONArray("The resident time");
                    JSONArray jsonArray1 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(1);
                    for(int i = 0; i < jsonArray1.length(); i++){
                        the_resident_time_time.add(jsonArray1.getString(i));
                    }
                    for(int i = 0; i < jsonArray2.length(); i++){
                        the_resident_time_number.add(jsonArray2.getString(i));
                    }
                }else{
                    the_resident_time_time.add(0, "0");
                    the_resident_time_number.add(0, "0");
                }
            }
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
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("the_resident_time_number", the_resident_time_number);
        new_json_obj.accumulate("the_resident_time_time", the_resident_time_time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_the_resident_time_week")
    public String getTheResidentTimeWeek(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 1;
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        ArrayList<String> the_resident_time_time = new ArrayList<String>();
        ArrayList<String> the_resident_time_number = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        int week;//系统时间周
        int day_of_week;//系统时间周的第几天

        int start_year;//开始时间年
        int start_month;//开始时间月
        int start_day;//开始时间日
        int start_hour;//开始时间小时
        int start_week;//开始时间周
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        int second_per_minute = 60;//表示一分钟有60秒
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        week = system_time.get(Calendar.WEEK_OF_YEAR);
        day_of_week = system_time.get(Calendar.DAY_OF_WEEK);
        if(week == 1)
        {
            if(day > 7)
            {
                year++;
            }
        }
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = day;
        start_month = month;
        start_week = week;
        //如果为周日，则处理
        if(day_of_week == 1)
        {
            //如果时为0，则上周数据不可取
            if(hour == 0)
            {
                start_week = week - MAX_X;
            }
            else
            {
                start_week = week - MAX_X + 1;
                week++;
                temp_time.set(Calendar.YEAR, year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
                {
                    week = 1;
                    year++;
                }
            }
        }
        else
        {
            start_week = week - MAX_X + 1;
            week++;
            temp_time.set(Calendar.YEAR, year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                week = 1;
                year++;
            }
        }
        //处理非正数周
        if(start_week <= 0)
        {
            start_year--;
            temp_time.set(Calendar.YEAR, start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + start_week;
        }
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;

        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id =  temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }
            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
            String data = Data.getData(index, type, id);
            ArrayList<ArrayList> resident_time = new ArrayList<ArrayList>();
            if(data.equals(""))
            {
                the_resident_time_time.add(0, "0");
                the_resident_time_number.add(0, "0");
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The resident time")){
                    JSONArray jsonArray = dataJson.getJSONArray("The resident time");
                    JSONArray jsonArray1 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(1);
                    for(int i = 0; i < jsonArray1.length(); i++){
                        the_resident_time_time.add(jsonArray1.getString(i));
                    }
                    for(int i = 0; i < jsonArray2.length(); i++){
                        the_resident_time_number.add(jsonArray2.getString(i));
                    }
                }else{
                    the_resident_time_time.add(0, "0");
                    the_resident_time_number.add(0, "0");
                }
            }
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == year && temp_week == week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("the_resident_time_number", the_resident_time_number);
        new_json_obj.accumulate("the_resident_time_time", the_resident_time_time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_the_resident_time_month")
    public String getTheResidentTimeMonth(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 1;
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        ArrayList<String> the_resident_time_time = new ArrayList<String>();
        ArrayList<String> the_resident_time_number = new ArrayList<String>();
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
        int second_per_minute = 60;//表示一分钟有60秒
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = 1;
        //如果日为1，则处理
        if(day == 1)
        {
            //如果时为0，则上月数据不可获取
            if(hour == 0)
            {
                start_month = month - MAX_X;
            }
            else
            {
                start_month = month - MAX_X + 1;
                month++;
                if(month > 12)
                {
                    month = 1;
                    year++;
                }
            }
        }
        else
        {
            start_month = month - MAX_X +1;
            month++;
            if(month > 12)
            {
                month = 1;
                year++;
            }
        }
        //处理非正数月
        if(start_month <= 0)
        {
            start_year--;
            start_month = 12 + start_month;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_month = start_month - 1;
        if(temp_start_month <= 0)
        {
            temp_start_year--;
            temp_start_month = 12 + temp_start_month;
        }
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            show_end_time = show_year + "-" + ProcessNumber.processNumber(show_month);
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
            String data = Data.getData(index, type, id);
            ArrayList<ArrayList> resident_time = new ArrayList<ArrayList>();
            if(data.equals(""))
            {
                the_resident_time_time.add(0, "0");
                the_resident_time_number.add(0, "0");
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The resident time")){
                    JSONArray jsonArray = dataJson.getJSONArray("The resident time");
                    JSONArray jsonArray1 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(1);
                    for(int i = 0; i < jsonArray1.length(); i++){
                        the_resident_time_time.add(jsonArray1.getString(i));
                    }
                    for(int i = 0; i < jsonArray2.length(); i++){
                        the_resident_time_number.add(jsonArray2.getString(i));
                    }
                }else{
                    the_resident_time_time.add(0, "0");
                    the_resident_time_number.add(0, "0");
                }
            }
            //处理下一个时间
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == year && temp_month == month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("the_resident_time_number", the_resident_time_number);
        new_json_obj.accumulate("the_resident_time_time", the_resident_time_time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_visiting_cycle_week")
    public String getVisitingCycleWeek(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 1;
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        ArrayList<String> visiting_cycle_time = new ArrayList<String>();
        ArrayList<String> visiting_cycle_number = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        int week;//系统时间周
        int day_of_week;//系统时间周的第几天
        int start_year;//开始时间年
        int start_month;//开始时间月
        int start_day;//开始时间日
        int start_hour;//开始时间小时
        int start_week;//开始时间周
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        int second_per_day = 24*60*60;//表示一天的秒数
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        week = system_time.get(Calendar.WEEK_OF_YEAR);
        day_of_week = system_time.get(Calendar.DAY_OF_WEEK);
        if(week == 1)
        {
            if(day > 7)
            {
                year++;
            }
        }
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = day;
        start_month = month;
        start_week = week;
        //如果为周日，则处理
        if(day_of_week == 1)
        {
            //如果时为0，则上周数据不可取
            if(hour == 0)
            {
                start_week = week - MAX_X;
            }
            else
            {
                start_week = week - MAX_X + 1;
                week++;
                temp_time.set(Calendar.YEAR, year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
                {
                    week = 1;
                    year++;
                }
            }
        }
        else
        {
            start_week = week - MAX_X + 1;
            week++;
            temp_time.set(Calendar.YEAR, year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                week = 1;
                year++;
            }
        }
        //处理非正数周
        if(start_week <= 0)
        {
            start_year--;
            temp_time.set(Calendar.YEAR, start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + start_week;
        }
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;
        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }
            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
            String data = Data.getData(index, type, id);
            ArrayList<ArrayList> visiting_cycle = new ArrayList<ArrayList>();
            if(data.equals(""))
            {
                visiting_cycle_number.add(0, "0");
                visiting_cycle_time.add(0, "0");
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Visiting cycle")){
                    JSONArray jsonArray = dataJson.getJSONArray("Visiting cycle");
                    JSONArray jsonArray1 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(1);
                    for(int i = 0; i < jsonArray1.length(); i++){
                        visiting_cycle_time.add(jsonArray1.getString(i));
                    }
                    for(int i = 0; i < jsonArray2.length(); i++){
                        visiting_cycle_number.add(jsonArray2.getString(i));
                    }
                }else{
                    visiting_cycle_number.add(0, "0");
                    visiting_cycle_time.add(0, "0");
                }
            }
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == year && temp_week == week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("visiting_cycle_time", visiting_cycle_time);
        new_json_obj.accumulate("visiting_cycle_number", visiting_cycle_number);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_visiting_cycle_month")
    public String getVisitingCycleMonth(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        final int MAX_X = 1;
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        ArrayList<String> visiting_cycle_time = new ArrayList<String>();
        ArrayList<String> visiting_cycle_number = new ArrayList<String>();
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
        int second_per_day = 24*60*60;//表示一天的秒数
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = 1;
        //如果日为1，则处理
        if(day == 1)
        {
            //如果时为0，则上月数据不可获取
            if(hour == 0)
            {
                start_month = month - MAX_X;
            }
            else
            {
                start_month = month - MAX_X + 1;
                month++;
                if(month > 12)
                {
                    month = 1;
                    year++;
                }
            }
        }
        else
        {
            start_month = month - MAX_X +1;
            month++;
            if(month > 12)
            {
                month = 1;
                year++;
            }
        }
        //处理非正数月
        if(start_month <= 0)
        {
            start_year--;
            start_month = 12 + start_month;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_month = start_month - 1;
        if(temp_start_month <= 0)
        {
            temp_start_year--;
            temp_start_month = 12 + temp_start_month;
        }
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month);
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            show_end_time = show_year + "-" + ProcessNumber.processNumber(show_month);
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
            String data = Data.getData(index, type, id);
            ArrayList<ArrayList> visiting_cycle = new ArrayList<ArrayList>();
            if(data.equals(""))
            {
                visiting_cycle_number.add(0, "0");
                visiting_cycle_time.add(0, "0");
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Visiting cycle")){
                    JSONArray jsonArray = dataJson.getJSONArray("Visiting cycle");
                    JSONArray jsonArray1 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(1);
                    for(int i = 0; i < jsonArray1.length(); i++){
                        visiting_cycle_time.add(jsonArray1.getString(i));
                    }
                    for(int i = 0; i < jsonArray2.length(); i++){
                        visiting_cycle_number.add(jsonArray2.getString(i));
                    }
                }else{
                    visiting_cycle_number.add(0, "0");
                    visiting_cycle_time.add(0, "0");
                }
            }
            //处理下一个时间
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == year && temp_month == month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("visiting_cycle_time", visiting_cycle_time);
        new_json_obj.accumulate("visiting_cycle_number", visiting_cycle_number);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_customer_active_week")
    public String getCustomerActiveWeek(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        int the_high_activity = 0;//高活跃度
        int the_mid_activity = 0;//中活跃度
        int the_low_activity = 0;//低活跃度
        int the_sleep_activity = 0;//沉睡活跃度
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        int week;//系统时间周
        int day_of_week;//系统时间周的第几天

        int start_year;//开始时间年
        int start_month;//开始时间月
        int start_day;//开始时间日
        int start_hour;//开始时间小时
        int start_week;//开始时间周
        String show_start_time;//展示开始时间
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        week = system_time.get(Calendar.WEEK_OF_YEAR);
        day_of_week = system_time.get(Calendar.DAY_OF_WEEK);
        if(week == 1)
        {
            if(day > 7)
            {
                year++;
            }
        }
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = day;
        start_month = month;
        start_week = week;
        //如果为周日，则处理
        if(day_of_week == 1)
        {
            //如果时为0，则上周数据不可取
            if(hour == 0)
            {
                start_week = week - 1;
            }
            else
            {
                start_week = week;
            }
        }
        else
        {
            start_week = week;
        }
        //处理非正数周
        if(start_week <= 0)
        {
            start_year--;
            temp_time.set(Calendar.YEAR, start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + start_week;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time.set(Calendar.YEAR, temp_start_year);
        temp_time.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time.get(Calendar.YEAR);
        temp_start_month = temp_time.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);

        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time.set(Calendar.DAY_OF_WEEK, 1);
        start_year = temp_time.get(Calendar.YEAR);
        start_month = temp_time.get(Calendar.MONTH) + 1;
        start_day = temp_time.get(Calendar.DATE);
        //组合id
        id =  start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
        String data = Data.getData(index, type, id);
        if(!data.equals(""))
        {
            JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
            if(dataJson.has("Customer active")){
                JSONObject customer_active = dataJson.getJSONObject("Customer active");//顾客活跃度
                String high_activity = "0";
                String mid_activity = "0";
                String low_activity = "0";
                String sleep_activity = "0";
                if(customer_active.has("High activity")){
                    high_activity = customer_active.getString("High activity");//高活跃度
                }
                if(customer_active.has("Mid activity")){
                    mid_activity = customer_active.getString("Mid activity");//中活跃度
                }
                if(customer_active.has("Low activity")){
                    low_activity = customer_active.getString("Low activity");//低活跃度
                }
                if(customer_active.has("Sleep activity")){
                    sleep_activity = customer_active.getString("Sleep activity");//沉睡活跃度
                }
                the_high_activity = Integer.parseInt(high_activity);
                the_mid_activity = Integer.parseInt(mid_activity);
                the_low_activity = Integer.parseInt(low_activity);
                the_sleep_activity = Integer.parseInt(sleep_activity);
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("high_activity", the_high_activity);
        new_json_obj.accumulate("mid_activity", the_mid_activity);
        new_json_obj.accumulate("low_activity", the_low_activity);
        new_json_obj.accumulate("sleep_activity", the_sleep_activity);
        new_json_obj.accumulate("show_start_time", show_start_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_customer_active_month")
    public String getCustomerActiveMonth(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";


        int the_high_activity = 0;//高活跃度
        int the_mid_activity = 0;//中活跃度
        int the_low_activity = 0;//低活跃度
        int the_sleep_activity = 0;//沉睡活跃度
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
        //下面为展示开始时间的临时变量
        int temp_start_year;
        int temp_start_month;
        //依次获取系统时间
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        //初始开始时间
        start_year = year;
        start_hour = 0;
        start_day = 1;
        //如果日为1，则处理
        if(day == 1)
        {
            //如果时为0，则上月数据不可获取
            if(hour == 0)
            {
                start_month = month - 1;
            }
            else
            {
                start_month = month;
            }
        }
        else
        {
            start_month = month;
        }
        //处理非正数月
        if(start_month <= 0)
        {
            start_year--;
            start_month = 12 + start_month;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_month = start_month - 1;
        if(temp_start_month <= 0)
        {
            temp_start_year--;
            temp_start_month = 12 + temp_start_month;
        }
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month);
        //组合id
        id = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
        String data = Data.getData(index, type, id);
        if(!data.equals(""))
        {
            JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
            if(dataJson.has("Customer active")){
                JSONObject customer_active = dataJson.getJSONObject("Customer active");//顾客活跃度
                String high_activity = "0";
                String mid_activity = "0";
                String low_activity = "0";
                String sleep_activity = "0";
                if(customer_active.has("High activity")){
                    high_activity = customer_active.getString("High activity");//高活跃度
                }
                if(customer_active.has("Mid activity")){
                    mid_activity = customer_active.getString("Mid activity");//中活跃度
                }
                if(customer_active.has("Low activity")){
                    low_activity = customer_active.getString("Low activity");//低活跃度
                }
                if(customer_active.has("Sleep activity")){
                    sleep_activity = customer_active.getString("Sleep activity");//沉睡活跃度
                }
                the_high_activity = Integer.parseInt(high_activity);
                the_mid_activity = Integer.parseInt(mid_activity);
                the_low_activity = Integer.parseInt(low_activity);
                the_sleep_activity = Integer.parseInt(sleep_activity);
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("high_activity", the_high_activity);
        new_json_obj.accumulate("mid_activity", the_mid_activity);
        new_json_obj.accumulate("low_activity", the_low_activity);
        new_json_obj.accumulate("sleep_activity", the_sleep_activity);
        new_json_obj.accumulate("show_start_time", show_start_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_recent_data")
    public String getRecentData(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
        String id = "";
        String last_day_traffic_amount_number;
        String last_day_traffic_amount_status;
        String last_day_traffic_amount_compare;
        String temp_traffic_amount_number;

        String last_day_the_amount_of_store_number;
        String last_day_the_amount_of_store_status;
        String last_day_the_amount_of_store_compare;
        String temp_the_amount_of_store_number;

        String average_the_resident_time_number;
        String average_the_resident_time_status;
        String average_the_resident_time_compare;
        String temp_average_the_resident_time_number;

        String last_day_new_customers_number;
        String last_day_new_customers_status;
        String last_day_new_customers_compare;
        String temp_new_customers_number;

        String last_day_old_customers_number;
        String last_day_old_customers_status;
        String last_day_old_customers_compare;
        String temp_old_customers_number;

        String last_day_deep_number;
        String last_day_deep_status;
        String last_day_deep_compare;
        String temp_deep_number;

        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        int last_year;
        int last_month;
        int last_day;
        int last_hour;
        int yesterday_year;//昨日年
        int yesterday_month;//昨日月
        int yesterday_day;//昨日日
        int yesterday_hour;//昨日小时
        int the_day_after_yesterday_year;
        int the_day_after_yester_month;
        int the_day_after_yesterday_day;
        int the_day_after_yesterday_hour;
        yesterday_year = system_time.get(Calendar.YEAR);
        yesterday_month = system_time.get(Calendar.MONTH) + 1;
        yesterday_day = system_time.get(Calendar.DATE);
        yesterday_hour = system_time.get(Calendar.HOUR_OF_DAY);
        year = yesterday_year;
        month = yesterday_month;
        day = yesterday_day;
        hour = yesterday_hour;
        last_year = year;
        last_month = month;
        last_day = day;
        last_hour = hour;
        the_day_after_yesterday_year = year;
        the_day_after_yester_month = month;
        the_day_after_yesterday_day = day;
        the_day_after_yesterday_hour = hour;
        //如果时间为0时刻，则昨日数据不可取
        if(yesterday_hour == 0){
            yesterday_day = yesterday_day - 1;
        }
        yesterday_hour = 0;
        //日为非整数时进行时间变幻
        if(yesterday_day <= 0){
            yesterday_month--;
            if(yesterday_month == 0){
                yesterday_month = 12;
                yesterday_year--;
            }
            yesterday_day = GetDayBaseYearAndMonth.getDay(yesterday_year, yesterday_month) + yesterday_day;
        }
        //计算上一时刻时间
        hour--;
        if(hour < 0){
            hour = 24 + hour;
            day--;
            if(day == 0){
                month--;
                if(month == 0){
                    month = 12;
                    year--;
                    day = 31;
                }else{
                    day = GetDayBaseYearAndMonth.getDay(year, month);
                }
            }
        }
        //计算上上时刻时间
        last_hour = last_hour - 2;
        if(last_hour < 0){
            last_hour = 24 + last_hour;
            last_day--;
            if(last_day == 0){
                last_month--;
                if(last_month == 0){
                    last_month = 12;
                    last_year--;
                    last_day = 31;
                }else{
                    last_day = GetDayBaseYearAndMonth.getDay(last_year, last_month);
                }
            }
        }
        if(the_day_after_yesterday_hour == 0){
            the_day_after_yesterday_day = the_day_after_yesterday_day - 2;
        }else{
            the_day_after_yesterday_day = the_day_after_yesterday_day - 1;
        }
        the_day_after_yesterday_hour = 0;
        if(the_day_after_yesterday_day <= 0){
            the_day_after_yester_month--;
            if(the_day_after_yester_month == 0){
                the_day_after_yester_month = 12;
                the_day_after_yesterday_year--;
            }
            the_day_after_yesterday_day= GetDayBaseYearAndMonth.getDay(the_day_after_yesterday_year, the_day_after_yester_month) + the_day_after_yesterday_day;
        }
        //组合id
        id = yesterday_year + "-" + ProcessNumber.processNumber(yesterday_month) + "-" + ProcessNumber.processNumber(yesterday_day) + "-" + ProcessNumber.processNumber(yesterday_hour);
        String data = Data.getData(index, type, id);
        if(data.equals("")){
            last_day_traffic_amount_number = "0";
            last_day_the_amount_of_store_number = "0";
            last_day_new_customers_number = "0";
            last_day_old_customers_number = "0";
            last_day_deep_number = "0";
        }else{
            JSONObject dataJson = new JSONObject(data);
            if(dataJson.has("Traffic amount")){
                last_day_traffic_amount_number = dataJson.getString("Traffic amount");
            }else{
                last_day_traffic_amount_number = "0";
            }
            if(dataJson.has("The amount of store")){
                last_day_the_amount_of_store_number = dataJson.getString("The amount of store");
            }else{
                last_day_the_amount_of_store_number = "0";
            }
            if(dataJson.has("The new and old customers")){
                JSONObject newAndOldCustomers = new JSONObject();
                newAndOldCustomers = dataJson.getJSONObject("The new and old customers");
                if(newAndOldCustomers.has("new")){
                    last_day_new_customers_number = newAndOldCustomers.getString("new");
                }else{
                    last_day_new_customers_number = "0";
                }
                if(newAndOldCustomers.has("old")){
                    last_day_old_customers_number = newAndOldCustomers.getString("old");
                }else{
                    last_day_old_customers_number = "0";
                }
            }else{
                last_day_new_customers_number = "0";
                last_day_old_customers_number = "0";
            }
            if(dataJson.has("Deep rate")){
                String last_day_deep_rate = dataJson.getString("Deep rate");
                float float_last_day_deep_rate = Float.parseFloat(last_day_deep_rate);
                float float_last_day_deep_number = Integer.parseInt(last_day_the_amount_of_store_number) * float_last_day_deep_rate;
                last_day_deep_number = (int)float_last_day_deep_number + "";
            }else{
                last_day_deep_number = "0";
            }
        }
        id = the_day_after_yesterday_year + "-" + ProcessNumber.processNumber(the_day_after_yester_month) + "-" + ProcessNumber.processNumber(the_day_after_yesterday_day) + "-" + ProcessNumber.processNumber(the_day_after_yesterday_hour);
        data = Data.getData(index, type, id);
        if(data.equals("")){
            temp_traffic_amount_number = "0";
            temp_the_amount_of_store_number = "0";
            temp_new_customers_number = "0";
            temp_old_customers_number = "0";
            temp_deep_number = "0";
        }else{
            JSONObject dataJson = new JSONObject(data);
            if(dataJson.has("Traffic amount")){
                temp_traffic_amount_number = dataJson.getString("Traffic amount");
            }else{
                temp_traffic_amount_number = "0";
            }
            if(dataJson.has("The amount of store")){
                temp_the_amount_of_store_number = dataJson.getString("The amount of store");
            }else{
                temp_the_amount_of_store_number = "0";
            }
            if(dataJson.has("The new and old customers")){
                JSONObject the_new_and_old_customers = new JSONObject();
                the_new_and_old_customers = dataJson.getJSONObject("The new and old customers");
                if(the_new_and_old_customers.has("new")){
                    temp_new_customers_number = the_new_and_old_customers.getString("new");
                }else{
                    temp_new_customers_number = "0";
                }
                if(the_new_and_old_customers.has("old")){
                    temp_old_customers_number = the_new_and_old_customers.getString("old");
                }else{
                    temp_old_customers_number = "0";
                }
            }else{
                temp_new_customers_number = "0";
                temp_old_customers_number = "0";
            }
            if(dataJson.has("Deep rate")){
                String deep_rate = dataJson.getString("Deep rate");
                temp_deep_number = (int)(Integer.parseInt(temp_the_amount_of_store_number) * Float.parseFloat(deep_rate)) + "";
            }else{
                temp_deep_number = "0";
            }
        }
        type = my_id + "-hour";
        id = year + "-" + ProcessNumber.processNumber(month) + "-" + ProcessNumber.processNumber(day) + "-" + ProcessNumber.processNumber(hour);
        data = Data.getData(index, type, id);
        if(data.equals("")){
            average_the_resident_time_number = "0";
        }else{
            JSONObject dataJson = new JSONObject(data);
            if(dataJson.has("Average the resident time")){
                average_the_resident_time_number = dataJson.getString("Average the resident time");
            }else{
                average_the_resident_time_number = "0";
            }
        }
        id = last_year + "-" + ProcessNumber.processNumber(last_month) + "-" + ProcessNumber.processNumber(last_day) + "-" + ProcessNumber.processNumber(last_hour);
        data = Data.getData(index, type, id);
        if(data.equals("")){
            temp_average_the_resident_time_number = "0";
        }else{
            JSONObject dataJson = new JSONObject(data);
            if(dataJson.has("Average the resident time")){
                temp_average_the_resident_time_number = dataJson.getString("Average the resident time");
            }else{
                temp_average_the_resident_time_number = "0";
            }
        }
        if(Integer.parseInt(last_day_traffic_amount_number) >= Integer.parseInt(temp_traffic_amount_number)){
            last_day_traffic_amount_status = "up";
            int temp = Integer.parseInt(last_day_traffic_amount_number) - Integer.parseInt(temp_traffic_amount_number);
            if(temp_traffic_amount_number.equals("0")){
                last_day_traffic_amount_compare = "--%";
            }else{
                float rate = ((float)temp / Float.parseFloat(temp_traffic_amount_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                last_day_traffic_amount_compare = df.format(rate) + "%";
            }
         }else{
            last_day_traffic_amount_status = "down";
            int temp = Integer.parseInt(temp_traffic_amount_number) - Integer.parseInt(last_day_traffic_amount_number);
            if(temp_traffic_amount_number.equals("0")){
                last_day_traffic_amount_compare = "--%";
            }else{
                float rate = ((float)temp / Float.parseFloat(temp_traffic_amount_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                last_day_traffic_amount_compare = df.format(rate) + "%";
            }
        }
        if(Integer.parseInt(last_day_the_amount_of_store_number) >= Integer.parseInt(temp_the_amount_of_store_number)){
            last_day_the_amount_of_store_status = "up";
            int temp = Integer.parseInt(last_day_the_amount_of_store_number) - Integer.parseInt(temp_the_amount_of_store_number);
            if(temp_the_amount_of_store_number.equals("0")){
                last_day_the_amount_of_store_compare = "--%";
            }else{
                float rate = ((float)temp / Float.parseFloat(temp_the_amount_of_store_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                last_day_the_amount_of_store_compare = df.format(rate) + "%";
            }
        }else{
            last_day_the_amount_of_store_status = "down";
            int temp = Integer.parseInt(temp_the_amount_of_store_number) - Integer.parseInt(last_day_the_amount_of_store_number);
            if(temp_the_amount_of_store_number.equals("0")){
                last_day_the_amount_of_store_compare = "--%";
            }else{
                float rate = ((float)temp / Float.parseFloat(temp_the_amount_of_store_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                last_day_the_amount_of_store_compare = df.format(rate) + "%";
            }
        }
        if(Float.parseFloat(average_the_resident_time_number) >= Float.parseFloat(temp_average_the_resident_time_number)){
            average_the_resident_time_status = "up";
            float temp = Float.parseFloat(average_the_resident_time_number) - Float.parseFloat(temp_average_the_resident_time_number);
            if(average_the_resident_time_number.equals("0")){
                average_the_resident_time_compare = "--%";
            }else{
                float rate = (temp / Float.parseFloat(temp_average_the_resident_time_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                average_the_resident_time_compare = df.format(rate) + "%";
            }
        }else{
            average_the_resident_time_status = "down";
            float temp = Float.parseFloat(temp_average_the_resident_time_number) - Float.parseFloat(average_the_resident_time_number);
            if(average_the_resident_time_number.equals("0")){
                average_the_resident_time_compare = "--%";
            }else{
                float rate = (temp / Float.parseFloat(temp_average_the_resident_time_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                average_the_resident_time_compare = df.format(rate) + "%";
            }
        }
        if(Integer.parseInt(last_day_new_customers_number) >= Integer.parseInt(temp_new_customers_number)){
            last_day_new_customers_status = "up";
            int temp = Integer.parseInt(last_day_new_customers_number) - Integer.parseInt(temp_new_customers_number);
            if(temp_new_customers_number.equals("0")){
                last_day_new_customers_compare = "--%";
            }else{
                float rate = ((float)temp / Float.parseFloat(temp_new_customers_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                last_day_new_customers_compare = df.format(rate) + "%";
            }
        }else{
            last_day_new_customers_status = "down";
            int temp = Integer.parseInt(temp_new_customers_number) - Integer.parseInt(last_day_new_customers_number);
            if(temp_new_customers_number.equals("0")){
                last_day_new_customers_compare = "--%";
            }else{
                float rate = ((float)temp / Float.parseFloat(temp_new_customers_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                last_day_new_customers_compare = df.format(rate) + "%";
            }
        }
        if(Integer.parseInt(last_day_old_customers_number) >= Integer.parseInt(temp_old_customers_number)){
            last_day_old_customers_status = "up";
            int temp = Integer.parseInt(last_day_old_customers_number) - Integer.parseInt(temp_old_customers_number);
            if(temp_old_customers_number.equals("0")){
                last_day_old_customers_compare = "--%";
            }else{
                float rate = ((float)temp / Float.parseFloat(temp_old_customers_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                last_day_old_customers_compare = df.format(rate) + "%";
            }
        }else{
            last_day_old_customers_status = "down";
            int temp = Integer.parseInt(temp_old_customers_number) - Integer.parseInt(last_day_old_customers_number);
            if(temp_old_customers_number.equals("0")){
                last_day_old_customers_compare = "--%";
            }else{
                float rate = ((float)temp / Float.parseFloat(temp_old_customers_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                last_day_old_customers_compare = df.format(rate) + "%";
            }
        }
        if(Integer.parseInt(last_day_deep_number) >= Integer.parseInt(temp_deep_number)){
            last_day_deep_status = "up";
            int temp = Integer.parseInt(last_day_deep_number) - Integer.parseInt(temp_deep_number);
            if(temp_deep_number.equals("0")){
                last_day_deep_compare = "--%";
            }else{
                float rate = ((float)temp / Float.parseFloat(temp_deep_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                last_day_deep_compare = df.format(rate) + "%";
            }
        }else{
            last_day_deep_status = "down";
            int temp = Integer.parseInt(temp_deep_number) - Integer.parseInt(last_day_deep_number);
            if(temp_deep_number.equals("0")){
                last_day_deep_compare = "--%";
            }else{
                float rate = ((float)temp / Float.parseFloat(temp_deep_number)) * 100;
                DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
                last_day_deep_compare = df.format(rate) + "%";
            }
        }
        JSONObject dataJson = new JSONObject();
        JSONObject last_day_traffic_amount = new JSONObject();
        last_day_traffic_amount.accumulate("number", last_day_traffic_amount_number);
        last_day_traffic_amount.accumulate("status", last_day_traffic_amount_status);
        last_day_traffic_amount.accumulate("compare", last_day_traffic_amount_compare);
        dataJson.accumulate("last_day_traffic_amount", last_day_traffic_amount);

        JSONObject last_day_the_amount_of_store = new JSONObject();
        last_day_the_amount_of_store.accumulate("number", last_day_the_amount_of_store_number);
        last_day_the_amount_of_store.accumulate("status", last_day_the_amount_of_store_status);
        last_day_the_amount_of_store.accumulate("compare", last_day_the_amount_of_store_compare);
        dataJson.accumulate("last_day_the_amount_of_store", last_day_the_amount_of_store);

        JSONObject average_the_resident_time = new JSONObject();
        average_the_resident_time.accumulate("number", average_the_resident_time_number);
        average_the_resident_time.accumulate("status", average_the_resident_time_status);
        average_the_resident_time.accumulate("compare", average_the_resident_time_compare);
        dataJson.accumulate("average_the_resident_time", average_the_resident_time);

        JSONObject last_day_new_customers = new JSONObject();
        last_day_new_customers.accumulate("number", last_day_new_customers_number);
        last_day_new_customers.accumulate("status", last_day_new_customers_status);
        last_day_new_customers.accumulate("compare", last_day_new_customers_compare);
        dataJson.accumulate("last_day_new_customers", last_day_new_customers);

        JSONObject last_day_old_customers = new JSONObject();
        last_day_old_customers.accumulate("number", last_day_old_customers_number);
        last_day_old_customers.accumulate("status", last_day_old_customers_status);
        last_day_old_customers.accumulate("compare", last_day_old_customers_compare);
        dataJson.accumulate("last_day_old_customers", last_day_old_customers);

        JSONObject last_day_deep = new JSONObject();
        last_day_deep.accumulate("number", last_day_deep_number);
        last_day_deep.accumulate("status", last_day_deep_status);
        last_day_deep.accumulate("compare", last_day_deep_compare);
        dataJson.accumulate("last_day_deep", last_day_deep);

        return dataJson.toString();
    }

    @RequestMapping(value="/get_custom_traffic_and_the_amount_of_store_hour")
    public String getCustomTrafficAndAmountOfStoreHour(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int start_hour, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, @RequestParam int end_hour, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-hour";
        String id = "";
        ArrayList<Integer> the_traffic = new ArrayList<Integer>();
        ArrayList<Integer> the_store_amount = new ArrayList<Integer>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month) + "-" + ProcessNumber.processNumber(end_day) + " " + ProcessNumber.processNumber(end_hour);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + " " + ProcessNumber.processNumber(start_hour);
        //末尾时间加1
        end_hour++;
        if(end_hour >=24)
        {
            end_hour = 0;
            end_day++;
            if(end_day > GetDayBaseYearAndMonth.getDay(end_year, end_month))
            {
                end_day = 1;
                end_month++;
                if(end_month > 12)
                {
                    end_month = 1;
                    end_year++;
                }
            }
        }
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        int temp_day = start_day;//用于循环临时日
        int temp_hour = start_hour;//用于循环临时小时
        int s_hour, s_day, s_month, s_year;//用于显示时间
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(temp_hour);
            //组合时间
            if(temp_hour == 0)
            {
                s_hour = 24;
                s_day = temp_day - 1;
                s_month = temp_month;
                s_year = temp_year;
                if(s_day <= 0)
                {
                    s_month--;
                    if(s_month <= 0)
                    {
                        s_year--;
                    }
                    s_day = GetDayBaseYearAndMonth.getDay(s_year, s_month);
                }
            }
            else
            {
                s_year = temp_year;
                s_month = temp_month;
                s_day = temp_day;
                s_hour = temp_hour;
            }
            s_time = ProcessNumber.processNumber(s_month) + "." + ProcessNumber.processNumber(s_day) + "." + ProcessNumber.processNumber(s_hour);
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
                if(dataJson.has("Traffic amount")){
                    traffic_amount = dataJson.getString("Traffic amount");//客流量
                }else{
                    traffic_amount = "0";
                }
                if(dataJson.has("The amount of store")){
                    the_amount_of_store = dataJson.getString("The amount of store");//入店量
                }else{
                    the_amount_of_store = "0";
                }
            }
            the_traffic.add(Integer.parseInt(traffic_amount));//加入客流量数据
            the_store_amount.add(Integer.parseInt(the_amount_of_store));//加入入店量
            //处理下一个时间
            temp_hour++;
            if(temp_hour >= 24)
            {
                temp_hour = 0;
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
            }
            if(temp_year == end_year && temp_month == end_month && temp_day == end_day && temp_hour == end_hour)
            {
                break;
            }
        }

        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("traffic", the_traffic);
        new_json_obj.accumulate("store_amount", the_store_amount);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_into_the_store_rate_hour")
    public String getCustomIntoTheStoreRateHour(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int start_hour, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, @RequestParam int end_hour, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-hour";
        String id = "";
        ArrayList<Float> the_into_the_store_rate = new ArrayList<Float>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month) + "-" + ProcessNumber.processNumber(end_day) + " " + ProcessNumber.processNumber(end_hour);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + " " + ProcessNumber.processNumber(start_hour);
        //末尾时间加1
        end_hour++;
        if(end_hour >=24)
        {
            end_hour = 0;
            end_day++;
            if(end_day > GetDayBaseYearAndMonth.getDay(end_year, end_month))
            {
                end_day = 1;
                end_month++;
                if(end_month > 12)
                {
                    end_month = 1;
                    end_year++;
                }
            }
        }
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        int temp_day = start_day;//用于循环临时日
        int temp_hour = start_hour;//用于循环临时小时
        int s_hour, s_day, s_month, s_year;//用于显示时间
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(temp_hour);
            //组合时间
            if(temp_hour == 0)
            {
                s_hour = 24;
                s_day = temp_day - 1;
                s_month = temp_month;
                s_year = temp_year;
                if(s_day <= 0)
                {
                    s_month--;
                    if(s_month <= 0)
                    {
                        s_year--;
                    }
                    s_day = GetDayBaseYearAndMonth.getDay(s_year, s_month);
                }
            }
            else
            {
                s_year = temp_year;
                s_month = temp_month;
                s_day = temp_day;
                s_hour = temp_hour;
            }
            s_time = ProcessNumber.processNumber(s_month) + "." + ProcessNumber.processNumber(s_day) + "." + ProcessNumber.processNumber(s_hour);
            time.add(s_time);
            String data = Data.getData(index, type, id);
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
            temp_hour++;
            if(temp_hour >= 24)
            {
                temp_hour = 0;
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
            }
            if(temp_year == end_year && temp_month == end_month && temp_day == end_day && temp_hour == end_hour)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("into_the_store_rate", the_into_the_store_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_the_new_and_old_customers_hour")
    public String getCustomTheNewAndOldCustomersHour(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int start_hour, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, @RequestParam int end_hour, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-hour";
        String id = "";
        ArrayList<Integer> the_new_customers = new ArrayList<Integer>();//新顾客
        ArrayList<Integer> the_old_customers = new ArrayList<Integer>();//老顾客
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month) + "-" + ProcessNumber.processNumber(end_day) + " " + ProcessNumber.processNumber(end_hour);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + " " + ProcessNumber.processNumber(start_hour);
        //末尾时间加1
        end_hour++;
        if(end_hour >=24)
        {
            end_hour = 0;
            end_day++;
            if(end_day > GetDayBaseYearAndMonth.getDay(end_year, end_month))
            {
                end_day = 1;
                end_month++;
                if(end_month > 12)
                {
                    end_month = 1;
                    end_year++;
                }
            }
        }
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        int temp_day = start_day;//用于循环临时日
        int temp_hour = start_hour;//用于循环临时小时
        int s_hour, s_day, s_month, s_year;//用于显示时间
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(temp_hour);
            //组合时间
            if(temp_hour == 0)
            {
                s_hour = 24;
                s_day = temp_day - 1;
                s_month = temp_month;
                s_year = temp_year;
                if(s_day <= 0)
                {
                    s_month--;
                    if(s_month <= 0)
                    {
                        s_year--;
                    }
                    s_day = GetDayBaseYearAndMonth.getDay(s_year, s_month);
                }
            }
            else
            {
                s_year = temp_year;
                s_month = temp_month;
                s_day = temp_day;
                s_hour = temp_hour;
            }
            s_time = ProcessNumber.processNumber(s_month) + "." + ProcessNumber.processNumber(s_day) + "." + ProcessNumber.processNumber(s_hour);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String new_customer;//新顾客
            String old_customer;//老顾客
            if(data.equals(""))
            {
                new_customer = "0";
                old_customer = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The new and old customers")){
                    JSONObject new_and_old_customers = dataJson.getJSONObject("The new and old customers");//新老顾客
                    if(new_and_old_customers.has("new")){
                        new_customer = new_and_old_customers.getString("new");//新顾客
                    }else{
                        new_customer = "0";
                    }
                    if(new_and_old_customers.has("old")){
                        old_customer = new_and_old_customers.getString("old");//老顾客
                    }else{
                        old_customer = "0";
                    }
                }else{
                    new_customer = "0";
                    old_customer = "0";
                }
            }
            the_new_customers.add(Integer.parseInt(new_customer));
            the_old_customers.add(Integer.parseInt(old_customer));
            //处理下一个时间
            temp_hour++;
            if(temp_hour >= 24)
            {
                temp_hour = 0;
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
            }
            if(temp_year == end_year && temp_month == end_month && temp_day == end_day && temp_hour == end_hour)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("new_customers", the_new_customers);
        new_json_obj.accumulate("old_customers", the_old_customers);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_deep_rate_and_bounce_rate_hour")
    public String getCustomDeepRateAndBounceRateHour(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int start_hour, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, @RequestParam int end_hour, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-hour";
        String id = "";
        ArrayList<Float> the_bounce_rate = new ArrayList<Float>();//跳出率
        ArrayList<Float> the_deep_rate = new ArrayList<Float>();//深访率
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month) + "-" + ProcessNumber.processNumber(end_day) + " " + ProcessNumber.processNumber(end_hour);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + " " + ProcessNumber.processNumber(start_hour);
        //末尾时间加1
        end_hour++;
        if(end_hour >=24)
        {
            end_hour = 0;
            end_day++;
            if(end_day > GetDayBaseYearAndMonth.getDay(end_year, end_month))
            {
                end_day = 1;
                end_month++;
                if(end_month > 12)
                {
                    end_month = 1;
                    end_year++;
                }
            }
        }
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        int temp_day = start_day;//用于循环临时日
        int temp_hour = start_hour;//用于循环临时小时
        int s_hour, s_day, s_month, s_year;//用于显示时间
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(temp_hour);
            //组合时间
            if(temp_hour == 0)
            {
                s_hour = 24;
                s_day = temp_day - 1;
                s_month = temp_month;
                s_year = temp_year;
                if(s_day <= 0)
                {
                    s_month--;
                    if(s_month <= 0)
                    {
                        s_year--;
                    }
                    s_day = GetDayBaseYearAndMonth.getDay(s_year, s_month);
                }
            }
            else
            {
                s_year = temp_year;
                s_month = temp_month;
                s_day = temp_day;
                s_hour = temp_hour;
            }
            s_time = ProcessNumber.processNumber(s_month) + "." + ProcessNumber.processNumber(s_day) + "." + ProcessNumber.processNumber(s_hour);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String bounce_rate;//跳出率
            String deep_rate;//深访率
            if(data.equals(""))
            {
                bounce_rate = "0";
                deep_rate = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Bounce rate")){
                    bounce_rate = dataJson.getString("Bounce rate");//跳出率
                }else{
                    bounce_rate = "0";
                }
                if(dataJson.has("Deep rate")){
                    deep_rate = dataJson.getString("Deep rate");//深访率e
                }else{
                    deep_rate = "0";
                }
            }
            the_bounce_rate.add(Float.parseFloat(bounce_rate));
            the_deep_rate.add(Float.parseFloat(deep_rate));
            //处理下一个时间
            temp_hour++;
            if(temp_hour >= 24)
            {
                temp_hour = 0;
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
            }
            if(temp_year == end_year && temp_month == end_month && temp_day == end_day && temp_hour == end_hour)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("bounce_rate", the_bounce_rate);
        new_json_obj.accumulate("deep_rate", the_deep_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_traffic_and_the_amount_of_store_day")
    public String getCustomTrafficAndTheAmountOfStoreDay(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
        String id = "";
        int start_hour = 0;
        ArrayList<Integer> the_traffic = new ArrayList<Integer>();
        ArrayList<Integer> the_store_amount = new ArrayList<Integer>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month) + "-" + ProcessNumber.processNumber(end_day);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day);
        //开始时间加1
        start_day++;
        if(start_day > GetDayBaseYearAndMonth.getDay(start_year, start_month))
        {
            start_day = 1;
            start_month++;
            if(start_month > 12)
            {
                start_month = 1;
                start_year++;
            }
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_day++;
            if(end_day > GetDayBaseYearAndMonth.getDay(end_year, end_month))
            {
                end_day = 1;
                end_month++;
                if(end_month > 12)
                {
                    end_month = 1;
                    end_year++;
                }
            }
        }
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
                if(dataJson.has("Traffic amount")){
                    traffic_amount = dataJson.getString("Traffic amount");//客流量
                }else{
                    traffic_amount = "0";
                }
                if(dataJson.has("The amount of store")){
                    the_amount_of_store = dataJson.getString("The amount of store");//入店量
                }else{
                    the_amount_of_store = "0";
                }
            }
            the_traffic.add(Integer.parseInt(traffic_amount));//加入客流量数据
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
            if(temp_year == end_year && temp_month == end_month && temp_day == end_day)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("traffic", the_traffic);
        new_json_obj.accumulate("store_amount", the_store_amount);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_into_the_store_rate_day")
    public String getCustomIntoTheStoreRateDay(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
        String id = "";
        int start_hour = 0;
        ArrayList<Float> the_into_the_store_rate = new ArrayList<Float>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month) + "-" + ProcessNumber.processNumber(end_day);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day);
        //开始时间加1
        start_day++;
        if(start_day > GetDayBaseYearAndMonth.getDay(start_year, start_month))
        {
            start_day = 1;
            start_month++;
            if(start_month > 12)
            {
                start_month = 1;
                start_year++;
            }
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_day++;
            if(end_day > GetDayBaseYearAndMonth.getDay(end_year, end_month))
            {
                end_day = 1;
                end_month++;
                if(end_month > 12)
                {
                    end_month = 1;
                    end_year++;
                }
            }
        }
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
            if(temp_year == end_year && temp_month == end_month && temp_day == end_day)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("into_the_store_rate", the_into_the_store_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_the_new_and_old_customers_day")
    public String getCustomTheNewAndOldCustomersDay(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
        String id = "";
        int start_hour = 0;
        ArrayList<Integer> the_new_customers = new ArrayList<Integer>();//新顾客
        ArrayList<Integer> the_old_customers = new ArrayList<Integer>();//老顾客
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month) + "-" + ProcessNumber.processNumber(end_day);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day);
        //开始时间加1
        start_day++;
        if(start_day > GetDayBaseYearAndMonth.getDay(start_year, start_month))
        {
            start_day = 1;
            start_month++;
            if(start_month > 12)
            {
                start_month = 1;
                start_year++;
            }
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_day++;
            if(end_day > GetDayBaseYearAndMonth.getDay(end_year, end_month))
            {
                end_day = 1;
                end_month++;
                if(end_month > 12)
                {
                    end_month = 1;
                    end_year++;
                }
            }
        }
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
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month) + "." + ProcessNumber.processNumber(show_day);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String new_customer;//新顾客
            String old_customer;//老顾客
            if(data.equals(""))
            {
                new_customer = "0";
                old_customer = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The new and old customers")){
                    JSONObject new_and_old_customers = dataJson.getJSONObject("The new and old customers");//新老顾客
                    if(new_and_old_customers.has("new")){
                        new_customer = new_and_old_customers.getString("new");//新顾客
                    }else{
                        new_customer = "0";
                    }
                    if(new_and_old_customers.has("old")){
                        old_customer = new_and_old_customers.getString("old");//老顾客
                    }else{
                        old_customer = "0";
                    }
                }else{
                    new_customer = "0";
                    old_customer = "0";
                }
            }
            the_new_customers.add(Integer.parseInt(new_customer));
            the_old_customers.add(Integer.parseInt(old_customer));
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
            if(temp_year == end_year && temp_month == end_month && temp_day == end_day)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("new_customers", the_new_customers);
        new_json_obj.accumulate("old_customers", the_old_customers);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_the_resident_time_day")
    public String getCustomTheResidentTimeDay(@RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
        String id = "";
        ArrayList<String> the_resident_time_time = new ArrayList<String>();
        ArrayList<String> the_resident_time_number = new ArrayList<String>();
        int start_hour = 0;
        int start_year = end_year;
        int start_month = end_month;
        int start_day = end_day;
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month) + "-" + ProcessNumber.processNumber(end_day);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day);
        //开始时间加1
        start_day++;
        if(start_day > GetDayBaseYearAndMonth.getDay(start_year, start_month))
        {
            start_day = 1;
            start_month++;
            if(start_month > 12)
            {
                start_month = 1;
                start_year++;
            }
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_day++;
            if(end_day > GetDayBaseYearAndMonth.getDay(end_year, end_month))
            {
                end_day = 1;
                end_month++;
                if(end_month > 12)
                {
                    end_month = 1;
                    end_year++;
                }
            }
        }
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
            String data = Data.getData(index, type, id);
            ArrayList<ArrayList> resident_time = new ArrayList<ArrayList>();
            if(data.equals(""))
            {
                the_resident_time_time.add(0, "0");
                the_resident_time_number.add(0, "0");
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The resident time")){
                    JSONArray jsonArray = dataJson.getJSONArray("The resident time");
                    JSONArray jsonArray1 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(1);
                    for(int i = 0; i < jsonArray1.length(); i++){
                        the_resident_time_time.add(jsonArray1.getString(i));
                    }
                    for(int i = 0; i < jsonArray2.length(); i++){
                        the_resident_time_number.add(jsonArray2.getString(i));
                    }
                }else{
                    the_resident_time_time.add(0, "0");
                    the_resident_time_number.add(0, "0");
                }
            }
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
            if(temp_year == end_year && temp_month == end_month && temp_day == end_day)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("the_resident_time_number", the_resident_time_number);
        new_json_obj.accumulate("the_resident_time_time", the_resident_time_time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_deep_rate_and_bounce_rate_day")
    public String  getCustomDeepRateAndBounceRateDay(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-day";
        String id = "";
        int start_hour = 0;
        ArrayList<Float> the_bounce_rate = new ArrayList<Float>();//跳出率
        ArrayList<Float> the_deep_rate = new ArrayList<Float>();//深访率
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month) + "-" + ProcessNumber.processNumber(end_day);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day);
        //开始时间加1
        start_day++;
        if(start_day > GetDayBaseYearAndMonth.getDay(start_year, start_month))
        {
            start_day = 1;
            start_month++;
            if(start_month > 12)
            {
                start_month = 1;
                start_year++;
            }
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_day++;
            if(end_day > GetDayBaseYearAndMonth.getDay(end_year, end_month))
            {
                end_day = 1;
                end_month++;
                if(end_month > 12)
                {
                    end_month = 1;
                    end_year++;
                }
            }
        }
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
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month) + "." + ProcessNumber.processNumber(show_day);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String bounce_rate;//跳出率
            String deep_rate;//深访率
            if(data.equals(""))
            {
                bounce_rate = "0";
                deep_rate = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Bounce rate")){
                    bounce_rate = dataJson.getString("Bounce rate");//跳出率
                }else{
                    bounce_rate = "0";
                }
                if(dataJson.has("Deep rate")){
                    deep_rate = dataJson.getString("Deep rate");//深访率
                }else{
                    deep_rate = "0";
                }
            }
            the_bounce_rate.add(Float.parseFloat(bounce_rate));
            the_deep_rate.add(Float.parseFloat(deep_rate));
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
            if(temp_year == end_year && temp_month == end_month && temp_day == end_day)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("bounce_rate", the_bounce_rate);
        new_json_obj.accumulate("deep_rate", the_deep_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_traffic_amount_and_the_amount_of_store_week")
    public String getCustomTrafficAmountAndTheAmountOfStoreWeek(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        int start_hour = 0;
        ArrayList<Integer> the_traffic = new ArrayList<Integer>();
        ArrayList<Integer> the_store_amount = new ArrayList<Integer>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, start_month-1);
        temp_time.set(Calendar.DATE, start_day);
        int start_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(start_week == 1)
        {
            if(start_day > 7)
            {
                start_year++;
            }
        }
        start_week++;
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, 11);
        temp_time.set(Calendar.DATE, 31);
        if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
        {
            temp_time.set(Calendar.DATE, 24);
        }
        if(start_week > temp_time.get(Calendar.WEEK_OF_YEAR))
        {
            start_year++;
            start_week = 1;
        }
        temp_time.set(Calendar.YEAR, end_year);
        temp_time.set(Calendar.MONTH, end_month-1);
        temp_time.set(Calendar.DATE, end_day);
        int end_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(end_week == 1)
        {
            if(end_day > 7)
            {
                end_year++;
            }
        }
        for(int i = 0; i < 2; i++)
        {
            end_week++;
            temp_time.set(Calendar.YEAR, end_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(end_week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                end_year++;
                end_week = 1;
            }
        }
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;
        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }
            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            time.add(s_time);
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
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
                if(dataJson.has("Traffic amount")){
                    traffic_amount = dataJson.getString("Traffic amount");//客流量
                }else{
                    traffic_amount = "0";
                }
                if(dataJson.has("The amount of store")){
                    the_amount_of_store = dataJson.getString("The amount of store");//入店量
                }else{
                    the_amount_of_store = "0";
                }
            }
            the_traffic.add(Integer.parseInt(traffic_amount));//加入客流量数据
            the_store_amount.add(Integer.parseInt(the_amount_of_store));//加入入店量
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == end_year && temp_week == end_week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("traffic", the_traffic);
        new_json_obj.accumulate("store_amount", the_store_amount);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_into_the_store_rate_week")
    public String getCustomIntoTheStoreRateWeek(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        int start_hour = 0;
        ArrayList<Float> the_into_the_store_rate = new ArrayList<Float>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, start_month-1);
        temp_time.set(Calendar.DATE, start_day);
        int start_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(start_week == 1)
        {
            if(start_day > 7)
            {
                start_year++;
            }
        }
        start_week++;
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, 11);
        temp_time.set(Calendar.DATE, 31);
        if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
        {
            temp_time.set(Calendar.DATE, 24);
        }
        if(start_week > temp_time.get(Calendar.WEEK_OF_YEAR))
        {
            start_year++;
            start_week = 1;
        }
        temp_time.set(Calendar.YEAR, end_year);
        temp_time.set(Calendar.MONTH, end_month-1);
        temp_time.set(Calendar.DATE, end_day);
        int end_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(end_week == 1)
        {
            if(end_day > 7)
            {
                end_year++;
            }
        }
        for(int i = 0; i < 2; i++)
        {
            end_week++;
            temp_time.set(Calendar.YEAR, end_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(end_week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                end_year++;
                end_week = 1;
            }
        }
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;
        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }
            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            time.add(s_time);
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
            String data = Data.getData(index, type, id);
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
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == end_year && temp_week == end_week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("into_the_store_rate", the_into_the_store_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_the_new_and_old_customers_week")
    public String getCustomTheNewAndOldCustomersWeek(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        int start_hour = 0;
        ArrayList<Integer> the_new_customers = new ArrayList<Integer>();//新顾客
        ArrayList<Integer> the_old_customers = new ArrayList<Integer>();//老顾客
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, start_month-1);
        temp_time.set(Calendar.DATE, start_day);
        int start_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(start_week == 1)
        {
            if(start_day > 7)
            {
                start_year++;
            }
        }
        start_week++;
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, 11);
        temp_time.set(Calendar.DATE, 31);
        if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
        {
            temp_time.set(Calendar.DATE, 24);
        }
        if(start_week > temp_time.get(Calendar.WEEK_OF_YEAR))
        {
            start_year++;
            start_week = 1;
        }
        temp_time.set(Calendar.YEAR, end_year);
        temp_time.set(Calendar.MONTH, end_month-1);
        temp_time.set(Calendar.DATE, end_day);
        int end_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(end_week == 1)
        {
            if(end_day > 7)
            {
                end_year++;
            }
        }
        for(int i = 0; i < 2; i++)
        {
            end_week++;
            temp_time.set(Calendar.YEAR, end_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(end_week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                end_year++;
                end_week = 1;
            }
        }
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;
        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }
            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            time.add(s_time);
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
            String data = Data.getData(index, type, id);
            String new_customer;//新顾客
            String old_customer;//老顾客
            if(data.equals(""))
            {
                new_customer = "0";
                old_customer = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The new and old customers")){
                    JSONObject new_and_old_customers = dataJson.getJSONObject("The new and old customers");//新老顾客
                    if(new_and_old_customers.has("new")){
                        new_customer = new_and_old_customers.getString("new");//新顾客
                    }else{
                        new_customer = "0";
                    }
                    if(new_and_old_customers.has("old")){
                        old_customer = new_and_old_customers.getString("old");//老顾客
                    }else{
                        old_customer = "0";
                    }
                }else{
                    new_customer = "0";
                    old_customer = "0";
                }
            }
            the_new_customers.add(Integer.parseInt(new_customer));
            the_old_customers.add(Integer.parseInt(old_customer));
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == end_year && temp_week == end_week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("new_customers", the_new_customers);
        new_json_obj.accumulate("old_customers", the_old_customers);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_the_resident_time_week")
    public String getCustomTheResidentTimeWeek(@RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        int start_hour = 0;
        int start_year = end_year;
        int start_month = end_month;
        int start_day = end_day;
        ArrayList<String> the_resident_time_time = new ArrayList<String>();
        ArrayList<String> the_resident_time_number = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
        int second_per_minute = 60;//表示一分钟有60秒
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, start_month-1);
        temp_time.set(Calendar.DATE, start_day);
        int start_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(start_week == 1)
        {
            if(start_day > 7)
            {
                start_year++;
            }
        }
        start_week++;
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, 11);
        temp_time.set(Calendar.DATE, 31);
        if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
        {
            temp_time.set(Calendar.DATE, 24);
        }
        if(start_week > temp_time.get(Calendar.WEEK_OF_YEAR))
        {
            start_year++;
            start_week = 1;
        }
        temp_time.set(Calendar.YEAR, end_year);
        temp_time.set(Calendar.MONTH, end_month-1);
        temp_time.set(Calendar.DATE, end_day);
        int end_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(end_week == 1)
        {
            if(end_day > 7)
            {
                end_year++;
            }
        }
        for(int i = 0; i < 2; i++)
        {
            end_week++;
            temp_time.set(Calendar.YEAR, end_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(end_week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                end_year++;
                end_week = 1;
            }
        }
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;
        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }
            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
            String data = Data.getData(index, type, id);
            ArrayList<ArrayList> resident_time = new ArrayList<ArrayList>();
            if(data.equals(""))
            {
                the_resident_time_time.add(0, "0");
                the_resident_time_number.add(0, "0");
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The resident time")){
                    JSONArray jsonArray = dataJson.getJSONArray("The resident time");
                    JSONArray jsonArray1 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(1);
                    for(int i = 0; i < jsonArray1.length(); i++){
                        the_resident_time_time.add(jsonArray1.getString(i));
                    }
                    for(int i = 0; i < jsonArray2.length(); i++){
                        the_resident_time_number.add(jsonArray2.getString(i));
                    }
                }else{
                    the_resident_time_time.add(0, "0");
                    the_resident_time_number.add(0, "0");
                }
            }
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == end_year && temp_week == end_week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("the_resident_time_number", the_resident_time_number);
        new_json_obj.accumulate("the_resident_time_time", the_resident_time_time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_deep_rate_and_bounce_rate_week")
    public String getCustomDeepRateAndBounceRateWeek(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int start_day, @RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        int start_hour = 0;
        ArrayList<Float> the_bounce_rate = new ArrayList<Float>();//跳出率
        ArrayList<Float> the_deep_rate = new ArrayList<Float>();//深访率
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, start_month-1);
        temp_time.set(Calendar.DATE, start_day);
        int start_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(start_week == 1)
        {
            if(start_day > 7)
            {
                start_year++;
            }
        }
        start_week++;
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, 11);
        temp_time.set(Calendar.DATE, 31);
        if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
        {
            temp_time.set(Calendar.DATE, 24);
        }
        if(start_week > temp_time.get(Calendar.WEEK_OF_YEAR))
        {
            start_year++;
            start_week = 1;
        }
        temp_time.set(Calendar.YEAR, end_year);
        temp_time.set(Calendar.MONTH, end_month-1);
        temp_time.set(Calendar.DATE, end_day);
        int end_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(end_week == 1)
        {
            if(end_day > 7)
            {
                end_year++;
            }
        }
        for(int i = 0; i < 2; i++)
        {
            end_week++;
            temp_time.set(Calendar.YEAR, end_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(end_week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                end_year++;
                end_week = 1;
            }
        }
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;
        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }

            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            time.add(s_time);
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
            String data = Data.getData(index, type, id);
            String bounce_rate;//跳出率
            String deep_rate;//深访率
            if(data.equals(""))
            {
                bounce_rate = "0";
                deep_rate = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Bounce rate")){
                    bounce_rate = dataJson.getString("Bounce rate");//跳出率
                }else{
                    bounce_rate = "0";
                }
                if(dataJson.has("Deep rate")){
                    deep_rate = dataJson.getString("Deep rate");//深访率
                }else{
                    deep_rate = "0";
                }
            }
            the_bounce_rate.add(Float.parseFloat(bounce_rate));
            the_deep_rate.add(Float.parseFloat(deep_rate));
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH) + 1;
            }
            if(s_year == end_year && temp_week == end_week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("bounce_rate", the_bounce_rate);
        new_json_obj.accumulate("deep_rate", the_deep_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_visiting_cycle_week")
    public String getCustomVisitingCycleWeek(@RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        int start_hour = 0;
        int start_year = end_year;
        int start_month = end_month;
        int start_day = end_day;
        ArrayList<String> visiting_cycle_time = new ArrayList<String>();
        ArrayList<String> visiting_cycle_number = new ArrayList<String>();
        String s_time;//用于临时存放时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
        int second_per_day = 24*60*60;//表示一天的秒数
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, start_month-1);
        temp_time.set(Calendar.DATE, start_day);
        int start_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(start_week == 1)
        {
            if(start_day > 7)
            {
                start_year++;
            }
        }
        start_week++;
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, 11);
        temp_time.set(Calendar.DATE, 31);
        if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
        {
            temp_time.set(Calendar.DATE, 24);
        }
        if(start_week > temp_time.get(Calendar.WEEK_OF_YEAR))
        {
            start_year++;
            start_week = 1;
        }
        temp_time.set(Calendar.YEAR, end_year);
        temp_time.set(Calendar.MONTH, end_month-1);
        temp_time.set(Calendar.DATE, end_day);
        int end_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(end_week == 1)
        {
            if(end_day > 7)
            {
                end_year++;
            }
        }
        for(int i = 0; i < 2; i++)
        {
            end_week++;
            temp_time.set(Calendar.YEAR, end_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            if(end_week > temp_time.get(Calendar.WEEK_OF_YEAR))
            {
                end_year++;
                end_week = 1;
            }
        }
        //下面为展示开始和结束时间的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        int temp_end_year;
        int temp_end_month;
        int temp_end_day;
        int temp_end_week;
        Calendar temp_time2 = Calendar.getInstance();//用于临时处理时间
        temp_time2.set(Calendar.YEAR, start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        int temp_year = temp_time2.get(Calendar.YEAR);
        int temp_month = temp_time2.get(Calendar.MONTH) + 1;
        int temp_day = temp_time2.get(Calendar.DATE);
        int temp_week = start_week;
        //下面为计算开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time2.set(Calendar.YEAR, temp_start_year);
        temp_time2.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time2.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time2.get(Calendar.YEAR);
        temp_start_month = temp_time2.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time2.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);
        //下面变量为展示显示时间的变量
        int show_year;
        int show_week;
        int s_year = temp_year;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(temp_day) + "-" + ProcessNumber.processNumber(start_hour);
            //处理展示时间
            show_year = s_year;
            show_week = temp_week - 1;
            //处理非正数周
            if(show_week <= 0)
            {
                show_year--;
                temp_time.set(Calendar.YEAR, show_year);
                temp_time.set(Calendar.MONTH, 11);
                temp_time.set(Calendar.DATE, 31);
                if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
                {
                    temp_time.set(Calendar.DATE, 24);
                }
                show_week = temp_time.get(Calendar.WEEK_OF_YEAR) + show_week;
            }
            //组合时间
            s_time = show_year + "年" + ProcessNumber.processNumber(show_week)+"周";
            //处理结束时间
            temp_end_year = show_year;
            temp_end_week = show_week;
            temp_time2.set(Calendar.YEAR, temp_end_year);
            temp_time2.set(Calendar.WEEK_OF_YEAR, temp_end_week);
            temp_time2.set(Calendar.DAY_OF_WEEK, 7);
            temp_end_year = temp_time2.get(Calendar.YEAR);
            temp_end_month = temp_time2.get(Calendar.MONTH) + 1;
            temp_end_day = temp_time2.get(Calendar.DATE);
            show_end_time = temp_end_year + "-" + ProcessNumber.processNumber(temp_end_month) + "-" + ProcessNumber.processNumber(temp_end_day);
            String data = Data.getData(index, type, id);
            ArrayList<ArrayList> visiting_cycle = new ArrayList<ArrayList>();
            if(data.equals(""))
            {
                visiting_cycle_number.add(0, "0");
                visiting_cycle_time.add(0, "0");
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Visiting cycle")){
                    JSONArray jsonArray = dataJson.getJSONArray("Visiting cycle");
                    JSONArray jsonArray1 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(1);
                    for(int i = 0; i < jsonArray1.length(); i++){
                        visiting_cycle_time.add(jsonArray1.getString(i));
                    }
                    for(int i = 0; i < jsonArray2.length(); i++){
                        visiting_cycle_number.add(jsonArray2.getString(i));
                    }
                }else{
                    visiting_cycle_number.add(0, "0");
                    visiting_cycle_time.add(0, "0");
                }
            }
            //处理下一个时间
            temp_week++;
            Calendar temp_time3 = Calendar.getInstance();//用于临时处理时间
            if(s_year != temp_year)
            {
                temp_year++;
            }
            temp_time3.set(Calendar.YEAR, temp_year);
            temp_time3.set(Calendar.MONTH, 11);
            temp_time3.set(Calendar.DATE, 31);
            if(temp_time3.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time3.set(Calendar.DATE, 24);
            }
            if(temp_week > temp_time3.get(Calendar.WEEK_OF_YEAR))
            {
                s_year++;
                temp_year++;
                temp_week = 1;
                temp_month = 1;
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, 1);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                if(temp_day > 7)
                {
                    temp_year--;
                    temp_month = 12;
                }
            }
            else
            {
                temp_time3.set(Calendar.YEAR, temp_year);
                temp_time3.set(Calendar.WEEK_OF_YEAR, temp_week);
                temp_time3.set(Calendar.DAY_OF_WEEK, 1);
                temp_day = temp_time3.get(Calendar.DATE);
                temp_month = temp_time3.get(Calendar.MONTH);
            }
            if(s_year == end_year && temp_week == end_week)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("visiting_cycle_time", visiting_cycle_time);
        new_json_obj.accumulate("visiting_cycle_number", visiting_cycle_number);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_customer_active_week")
    public String getCustomCustomerActiveWeek(@RequestParam int end_year, @RequestParam int end_month, @RequestParam int end_day, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-week";
        String id = "";
        int start_hour = 0;
        int start_year = end_year;
        int start_month = end_month;
        int start_day = end_day;
        int the_high_activity = 0;//高活跃度
        int the_mid_activity = 0;//中活跃度
        int the_low_activity = 0;//低活跃度
        int the_sleep_activity = 0;//沉睡活跃度
        String show_start_time;//展示开始时间
        Calendar temp_time = Calendar.getInstance();//用于临时处理时间
        //下面为展示开始的临时变量
        int temp_start_year;
        int temp_start_month;
        int temp_start_day;
        int temp_start_week;
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, start_month-1);
        temp_time.set(Calendar.DATE, start_day);
        int start_week = temp_time.get(Calendar.WEEK_OF_YEAR);
        if(start_week == 1)
        {
            if(start_day > 7)
            {
                start_year++;
            }
        }
        start_week++;
        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.MONTH, 11);
        temp_time.set(Calendar.DATE, 31);
        if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
        {
            temp_time.set(Calendar.DATE, 24);
        }
        if(start_week > temp_time.get(Calendar.WEEK_OF_YEAR))
        {
            start_year++;
            start_week = 1;
        }
        //处理展示开始时间
        temp_start_year = start_year;
        temp_start_week = start_week - 1;
        if(temp_start_week <= 0)
        {
            temp_start_year--;
            temp_time.set(Calendar.YEAR, temp_start_year);
            temp_time.set(Calendar.MONTH, 11);
            temp_time.set(Calendar.DATE, 31);
            if(temp_time.get(Calendar.WEEK_OF_YEAR) == 1)
            {
                temp_time.set(Calendar.DATE, 24);
            }
            temp_start_week = temp_time.get(Calendar.WEEK_OF_YEAR) + temp_start_week;
        }
        temp_time.set(Calendar.YEAR, temp_start_year);
        temp_time.set(Calendar.WEEK_OF_YEAR, temp_start_week);
        temp_time.set(Calendar.DAY_OF_WEEK, 1);
        temp_start_year = temp_time.get(Calendar.YEAR);
        temp_start_month = temp_time.get(Calendar.MONTH) + 1;
        temp_start_day = temp_time.get(Calendar.DATE);
        show_start_time = temp_start_year + "-" + ProcessNumber.processNumber(temp_start_month) + "-" + ProcessNumber.processNumber(temp_start_day);

        temp_time.set(Calendar.YEAR, start_year);
        temp_time.set(Calendar.WEEK_OF_YEAR, start_week);
        temp_time.set(Calendar.DAY_OF_WEEK, 1);
        start_year = temp_time.get(Calendar.YEAR);
        start_month = temp_time.get(Calendar.MONTH) + 1;
        start_day = temp_time.get(Calendar.DATE);
        //组合id
        id = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
        String data = Data.getData(index, type, id);
        if(!data.equals(""))
        {
            JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
            if(dataJson.has("Customer active")){
                JSONObject customer_active = dataJson.getJSONObject("Customer active");//顾客活跃度
                String high_activity;
                String mid_activity;
                String low_activity;
                String sleep_activity;
                if(customer_active.has("High activity")){
                    high_activity = customer_active.getString("High activity");//高活跃度
                }else{
                    high_activity = "0";
                }
                if(customer_active.has("Mid activity")){
                     mid_activity = customer_active.getString("Mid activity");//中活跃度
                }else{
                    mid_activity = "0";
                }
                if(customer_active.has("Low activity")){
                    low_activity = customer_active.getString("Low activity");//低活跃度
                }else{
                    low_activity = "0";
                }
                if(customer_active.has("Sleep activity")){
                    sleep_activity = customer_active.getString("Sleep activity");//沉睡活跃度
                }else{
                    sleep_activity = "0";
                }
                the_high_activity = Integer.parseInt(high_activity);
                the_mid_activity = Integer.parseInt(mid_activity);
                the_low_activity = Integer.parseInt(low_activity);
                the_sleep_activity = Integer.parseInt(sleep_activity);
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("high_activity", the_high_activity);
        new_json_obj.accumulate("mid_activity", the_mid_activity);
        new_json_obj.accumulate("low_activity", the_low_activity);
        new_json_obj.accumulate("sleep_activity", the_sleep_activity);
        new_json_obj.accumulate("show_start_time", show_start_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_traffic_amount_and_the_amount_of_store_month")
    public String getCustomTrafficAmountAndTheAmountOfStoreMonth(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int end_year, @RequestParam int end_month, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        int start_hour = 0;
        int start_day = 1;
        ArrayList<Integer> the_traffic = new ArrayList<Integer>();
        ArrayList<Integer> the_store_amount = new ArrayList<Integer>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month);
        //开始时间加1
        start_month++;
        if(start_month > 12)
        {
            start_month = 1;
            start_year++;
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_month++;
            if(end_month > 12)
            {
                end_month = 1;
                end_year++;
            }
        }
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
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
                if(dataJson.has("Traffic amount")){
                    traffic_amount = dataJson.getString("Traffic amount");//客流量
                }else{
                    traffic_amount = "0";
                }
                if(dataJson.has("The amount of store")){
                    the_amount_of_store = dataJson.getString("The amount of store");//入店量
                }else{
                    the_amount_of_store = "0";
                }
            }
            the_traffic.add(Integer.parseInt(traffic_amount));//加入客流量数据
            the_store_amount.add(Integer.parseInt(the_amount_of_store));//加入入店量
            //处理下一个时间
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == end_year && temp_month == end_month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("traffic", the_traffic);
        new_json_obj.accumulate("store_amount", the_store_amount);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_into_the_store_rate_month")
    public String getCustomIntoTheStoreRateMonth(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int end_year, @RequestParam int end_month, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        int start_hour = 0;
        int start_day = 1;
        ArrayList<Float> the_into_the_store_rate = new ArrayList<Float>();
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month);
        //开始时间加1
        start_month++;
        if(start_month > 12)
        {
            start_month = 1;
            start_year++;
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_month++;
            if(end_month > 12)
            {
                end_month = 1;
                end_year++;
            }
        }
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
            time.add(s_time);
            String data = Data.getData(index, type, id);
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
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == end_year && temp_month == end_month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("into_the_store_rate", the_into_the_store_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_the_new_and_old_customers_month")
    public String getCustomTheNewAndOldCustomersMonth(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int end_year, @RequestParam int end_month, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        int start_hour = 0;
        int start_day = 1;
        ArrayList<Integer> the_new_customers = new ArrayList<Integer>();//新顾客
        ArrayList<Integer> the_old_customers = new ArrayList<Integer>();//老顾客
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month);
        //开始时间加1
        start_month++;
        if(start_month > 12)
        {
            start_month = 1;
            start_year++;
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_month++;
            if(end_month > 12)
            {
                end_month = 1;
                end_year++;
            }
        }
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String new_customer;//新顾客
            String old_customer;//老顾客
            if(data.equals(""))
            {
                new_customer = "0";
                old_customer = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The new and old customers")){
                    JSONObject new_and_old_customers = dataJson.getJSONObject("The new and old customers");//新老顾客
                    if(new_and_old_customers.has("new")){
                        new_customer = new_and_old_customers.getString("new");//新顾客
                    }else{
                        new_customer = "0";
                    }
                    if(new_and_old_customers.has("old")){
                        old_customer = new_and_old_customers.getString("old");//老顾客
                    }else{
                        old_customer = "0";
                    }
                }else{
                    new_customer = "0";
                    old_customer = "0";
                }
            }
            the_new_customers.add(Integer.parseInt(new_customer));
            the_old_customers.add(Integer.parseInt(old_customer));
            //处理下一个时间
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == end_year && temp_month == end_month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("new_customers", the_new_customers);
        new_json_obj.accumulate("old_customers", the_old_customers);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_the_resident_time_month")
    public String getCustomTheResidentTimeMonth(@RequestParam int end_year, @RequestParam int end_month, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        int start_hour = 0;
        int start_day = 1;
        int start_year = end_year;
        int start_month = end_month;
        ArrayList<String> the_resident_time_time = new ArrayList<String>();
        ArrayList<String> the_resident_time_number = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month);
        DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
        int second_per_minute = 60;//表示一分钟有60秒
        //开始时间加1
        start_month++;
        if(start_month > 12)
        {
            start_month = 1;
            start_year++;
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_month++;
            if(end_month > 12)
            {
                end_month = 1;
                end_year++;
            }
        }
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
            String data = Data.getData(index, type, id);
            ArrayList<ArrayList> resident_time = new ArrayList<ArrayList>();
            if(data.equals(""))
            {
                the_resident_time_time.add(0, "0");
                the_resident_time_number.add(0, "0");
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("The resident time")){
                    JSONArray jsonArray = dataJson.getJSONArray("The resident time");
                    JSONArray jsonArray1 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(1);
                    for(int i = 0; i < jsonArray1.length(); i++){
                        the_resident_time_time.add(jsonArray1.getString(i));
                    }
                    for(int i = 0; i < jsonArray2.length(); i++){
                        the_resident_time_number.add(jsonArray2.getString(i));
                    }
                }else{
                    the_resident_time_time.add(0, "0");
                    the_resident_time_number.add(0, "0");
                }
            }
            //处理下一个时间
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == end_year && temp_month == end_month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("the_resident_time_number", the_resident_time_number);
        new_json_obj.accumulate("the_resident_time_time", the_resident_time_time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_deep_rate_and_bounce_rate_month")
    public String getCustomDeepRateAndBounceRateMonth(@RequestParam int start_year, @RequestParam int start_month, @RequestParam int end_year, @RequestParam int end_month, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        int start_hour = 0;
        int start_day = 1;
        ArrayList<Float> the_bounce_rate = new ArrayList<Float>();//跳出率
        ArrayList<Float> the_deep_rate = new ArrayList<Float>();//深访率
        ArrayList<String> time = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month);
        //开始时间加1
        start_month++;
        if(start_month > 12)
        {
            start_month = 1;
            start_year++;
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_month++;
            if(end_month > 12)
            {
                end_month = 1;
                end_year++;
            }
        }
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
            time.add(s_time);
            String data = Data.getData(index, type, id);
            String bounce_rate;//跳出率
            String deep_rate;//深访率
            if(data.equals(""))
            {
                bounce_rate = "0";
                deep_rate = "0";
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Bounce rate")){
                    bounce_rate = dataJson.getString("Bounce rate");//跳出率
                }else{
                    bounce_rate = "0";
                }
                if(dataJson.has("Deep rate")){
                    deep_rate = dataJson.getString("Deep rate");//深访率
                }else{
                    deep_rate  = "0";
                }
            }
            the_bounce_rate.add(Float.parseFloat(bounce_rate));
            the_deep_rate.add(Float.parseFloat(deep_rate));
            //处理下一个时间
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == end_year && temp_month == end_month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("bounce_rate", the_bounce_rate);
        new_json_obj.accumulate("deep_rate", the_deep_rate);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_visiting_cycle_month")
    public String getCustomVisitingCycleMonth(@RequestParam int end_year, @RequestParam int end_month, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        int start_hour = 0;
        int start_day = 1;
        int start_year = end_year;
        int start_month = end_month;
        ArrayList<String> visiting_cycle_time = new ArrayList<String>();
        ArrayList<String> visiting_cycle_number = new ArrayList<String>();
        String s_time;//用于临时存放时间
        String show_start_time;//展示开始时间
        String show_end_time;//展示结束时间
        show_end_time = end_year + "-" + ProcessNumber.processNumber(end_month);
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month);
        DecimalFormat df = new DecimalFormat("0.00");//用于设置保留两位小数
        int second_per_day = 24*60*60;//表示一天的秒数
        //开始时间加1
        start_month++;
        if(start_month > 12)
        {
            start_month = 1;
            start_year++;
        }
        //处理结束时间
        for(int i = 0; i < 2; i++)
        {
            end_month++;
            if(end_month > 12)
            {
                end_month = 1;
                end_year++;
            }
        }
        int temp_year = start_year;//用于循环临时年
        int temp_month = start_month;//用于循环临时月
        //下列为展示显示时间的变量
        int show_year;
        int show_month;
        //循环进行数据获取处理
        while(true)
        {
            //组合id
            id = temp_year + "-" + ProcessNumber.processNumber(temp_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
            show_year = temp_year;
            show_month = temp_month - 1;
            if(show_month <= 0)
            {
                show_year--;
                show_month = 12 + show_month;
            }
            //组合时间
            s_time = show_year + "." + ProcessNumber.processNumber(show_month);
            String data = Data.getData(index, type, id);
            ArrayList<ArrayList> visiting_cycle = new ArrayList<ArrayList>();
            if(data.equals(""))
            {
                visiting_cycle_number.add(0, "0");
                visiting_cycle_time.add(0, "0");
            }
            else
            {
                JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
                if(dataJson.has("Visiting cycle")){
                    JSONArray jsonArray = dataJson.getJSONArray("Visiting cycle");
                    JSONArray jsonArray1 = (JSONArray) jsonArray.get(0);
                    JSONArray jsonArray2 = (JSONArray) jsonArray.get(1);
                    for(int i = 0; i < jsonArray1.length(); i++){
                        visiting_cycle_time.add(jsonArray1.getString(i));
                    }
                    for(int i = 0; i < jsonArray2.length(); i++){
                        visiting_cycle_number.add(jsonArray2.getString(i));
                    }
                }else{
                    visiting_cycle_number.add(0, "0");
                    visiting_cycle_time.add(0, "0");
                }
            }
            //处理下一个时间
            temp_month++;
            if(temp_month > 12)
            {
                temp_month = 1;
                temp_year++;
            }
            if(temp_year == end_year && temp_month == end_month)
            {
                break;
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("visiting_cycle_time", visiting_cycle_time);
        new_json_obj.accumulate("visiting_cycle_number", visiting_cycle_number);
        new_json_obj.accumulate("show_start_time", show_start_time);
        new_json_obj.accumulate("show_end_time", show_end_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_custom_customer_active_month")
    public String getCustomCustomerActiveMonth(@RequestParam int end_year, @RequestParam int end_month, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-month";
        String id = "";
        int start_hour = 0;
        int start_day = 1;
        int start_year = end_year;
        int start_month = end_month;
        int the_high_activity = 0;//高活跃度
        int the_mid_activity = 0;//中活跃度
        int the_low_activity = 0;//低活跃度
        int the_sleep_activity = 0;//沉睡活跃度
        String show_start_time;//展示开始时间
        show_start_time = start_year + "-" + ProcessNumber.processNumber(start_month);
        //开始时间加1
        start_month++;
        if(start_month > 12)
        {
            start_month = 1;
            start_year++;
        }
        //组合id
        id = start_year + "-" + ProcessNumber.processNumber(start_month) + "-" + ProcessNumber.processNumber(start_day) + "-" + ProcessNumber.processNumber(start_hour);
        String data = Data.getData(index, type, id);
        if(!data.equals(""))
        {
            JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
            if(dataJson.has("Customer active")){
                JSONObject customer_active = dataJson.getJSONObject("Customer active");//顾客活跃度
                if(customer_active.has("High activity")){
                    String high_activity = customer_active.getString("High activity");//高活跃度
                    the_high_activity = Integer.parseInt(high_activity);
                }
                if(customer_active.has("Mid activity")){
                    String mid_activity = customer_active.getString("Mid activity");//中活跃度
                    the_mid_activity = Integer.parseInt(mid_activity);
                }
                if(customer_active.has("Low activity")){
                    String low_activity = customer_active.getString("Low activity");//低活跃度
                    the_low_activity = Integer.parseInt(low_activity);
                }
                if(customer_active.has("Sleep activity")){
                    String sleep_activity = customer_active.getString("Sleep activity");//沉睡活跃度
                    the_sleep_activity = Integer.parseInt(sleep_activity);
                }
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("high_activity", the_high_activity);
        new_json_obj.accumulate("mid_activity", the_mid_activity);
        new_json_obj.accumulate("low_activity", the_low_activity);
        new_json_obj.accumulate("sleep_activity", the_sleep_activity);
        new_json_obj.accumulate("show_start_time", show_start_time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_test")
    public String getTest(HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        ArrayList<String> time = new ArrayList<String>();
        ArrayList<ArrayList<Integer>> user_the_amount_of_store_data = new ArrayList<ArrayList<Integer>>();
        ArrayList<ArrayList> temp = new ArrayList<ArrayList>();
        temp = UserData.getUserTheAmountOfStore("0000");
        user_the_amount_of_store_data.add(temp.get(0));
        time = temp.get(1);
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("user_the_amount_of_store_data", user_the_amount_of_store_data);
        new_json_obj.accumulate("time", time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }
/*
    @RequestMapping(value="/get_user_the_amount_of_store")
    public String getUserTheAmountOfStore(@RequestParam String user_list, HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        ArrayList<String> time = new ArrayList<String>();
        ArrayList<ArrayList<Integer>> user_the_amount_of_store_data = new ArrayList<ArrayList<Integer>>();
        ArrayList<String> select_user = new ArrayList<String>();
        //ArrayList<String> temp = new ArrayList<String>();
        String[] user_temp;
        user_temp = user_list.split("*");
        for(int i = 0; i < user_temp.length; i++){
            select_user.add(user_temp[i]);
        }
        for(int i = 0; i < select_user.size(); i++){
            ArrayList<ArrayList> temp = new ArrayList<ArrayList>();
            temp = UserData.getUserTheAmountOfStore(select_user.get(i));
            user_the_amount_of_store_data.add(temp.get(0));
            time = temp.get(1);
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("user_the_amount_of_store_data", user_the_amount_of_store_data);
        new_json_obj.accumulate("time", time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_user_into_the_store_rate")
    public String getUserIntoTheStoreRate(@RequestParam String user_list, HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        ArrayList<String> time = new ArrayList<String>();
        ArrayList<ArrayList<Float>> user_into_the_store_rate_data = new ArrayList<ArrayList<Float>>();
        ArrayList<String> select_user = new ArrayList<String>();
        //ArrayList<String> temp = new ArrayList<String>();
        String[] user_temp;
        user_temp = user_list.split("*");
        for(int i = 0; i < user_temp.length; i++){
            select_user.add(user_temp[i]);
        }
        for(int i = 0; i < select_user.size(); i++){
            ArrayList<ArrayList> temp = new ArrayList<ArrayList>();
            temp = UserData.getUserIntoTheStoreRate(select_user.get(i));
            user_into_the_store_rate_data.add(temp.get(0));
            time = temp.get(1);
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("user_into_the_store_rate_data", user_into_the_store_rate_data);
        new_json_obj.accumulate("time", time);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }
    */

    @RequestMapping(value="/get_user_the_amount_of_store")
    public String getUserTheAmountOfStore(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        ArrayList<String> all_id = new ArrayList<String>();
        ArrayList<String> time = new ArrayList<String>();
        ArrayList<ArrayList<Integer>> user_the_amount_of_store_data = new ArrayList<ArrayList<Integer>>();
        all_id = UserInfo.getAllUserId();
        if(all_id != null){
            for(int i = 0; i < all_id.size(); i++){
                ArrayList<ArrayList> temp = new ArrayList<ArrayList>();
                temp = UserData.getUserTheAmountOfStore(all_id.get(i));
                user_the_amount_of_store_data.add(temp.get(0));
                time = temp.get(1);
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("user_the_amount_of_store_data", user_the_amount_of_store_data);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("all_id", all_id);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_user_into_the_store_rate")
    public String getUserIntoTheStoreRate(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        ArrayList<String> all_id = new ArrayList<String>();
        ArrayList<String> time = new ArrayList<String>();
        ArrayList<ArrayList<Float>> user_into_the_store_rate_data = new ArrayList<ArrayList<Float>>();
        all_id = UserInfo.getAllUserId();
        if(all_id != null){
            for(int i = 0; i < all_id.size(); i++){
                ArrayList<ArrayList> temp = new ArrayList<ArrayList>();
                temp = UserData.getUserIntoTheStoreRate(all_id.get(i));
                user_into_the_store_rate_data.add(temp.get(0));
                time = temp.get(1);
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("user_into_the_store_rate_data", user_into_the_store_rate_data);
        new_json_obj.accumulate("time", time);
        new_json_obj.accumulate("all_id", all_id);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_all_user_data")
    public String getAllUserData(HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        ArrayList<String> all_id = new ArrayList<String>();
        ArrayList<String> all_name = new ArrayList<String>();
        ArrayList<Integer> all_traffic_amount = new ArrayList<Integer>();
        ArrayList<Integer> all_the_amount_of_store = new ArrayList<Integer>();
        ArrayList<Float> all_into_the_store_rate = new ArrayList<Float>();
        ArrayList<Float> all_deep_rate = new ArrayList<Float>();
        ArrayList<Float> all_bounce_rate = new ArrayList<Float>();
        all_id = UserInfo.getAllUserId();
        if(all_id != null) {
            all_name = UserInfo.getUserName(all_id);
            if (all_name == null) {
                all_id = null;
            } else {
                all_traffic_amount = UserData.getAllTrafficMount(all_id);
                all_the_amount_of_store = UserData.getAllTheAmountOfStore(all_id);
                all_into_the_store_rate = UserData.getAllIntoTheStoreRate(all_id);
                all_deep_rate = UserData.getAllDeepRate(all_id);
                all_bounce_rate = UserData.getAllBounceRate(all_id);
            }
        }
        //创建并处理
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("all_id", all_id);
        new_json_obj.accumulate("all_name", all_name);
        new_json_obj.accumulate("all_traffic_amount", all_traffic_amount);
        new_json_obj.accumulate("all_the_amount_of_store", all_the_amount_of_store);
        new_json_obj.accumulate("all_into_the_store_rate", all_into_the_store_rate);
        new_json_obj.accumulate("all_deep_rate", all_deep_rate);
        new_json_obj.accumulate("all_bounce_rate",all_bounce_rate);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_probe_position")
    public String getProbePosition(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String index = "res";
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        String my_id = user.getId();
        String type = my_id + "-hour";
        String id = "";
        Calendar system_time = Calendar.getInstance();//用于获取当前系统时间
        int year;//系统时间年
        int month;//系统时间月
        int day;//系统时间日
        int hour;//系统时间小时
        year = system_time.get(Calendar.YEAR);
        month = system_time.get(Calendar.MONTH) + 1;
        day = system_time.get(Calendar.DATE);
        hour = system_time.get(Calendar.HOUR_OF_DAY);
        hour = hour - 1;
        if(hour < 0)
        {
            hour = 24 + hour;
            day = day - 1;
            if(day == 0)
            {
                month = month - 1;
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
        id = year + "-" + ProcessNumber.processNumber(month) + "-" + ProcessNumber.processNumber(day) + "-" + ProcessNumber.processNumber(hour);
        String data = Data.getData(index, type, id);
        double lat;//经度
        double lon;//维度
        if(data.equals(""))
        {
            lon = 116.404;
            lat = 39.915;
        }
        else
        {
            JSONObject dataJson = new JSONObject(data);//创建一个包含json串的json对象
            if(dataJson.has("lat")){
                lat = Double.parseDouble(dataJson.getString("lat"));//获取维度
            }else{
                lat = 39.915;
            }
            if(dataJson.has("lon")){
                lon = Double.parseDouble(dataJson.getString("lon"));//获取经度
            }else{
                lon = 116.404;
            }
        }
        JSONObject new_json_obj = new JSONObject();
        new_json_obj.accumulate("lat", lat);
        new_json_obj.accumulate("lon", lon);
        String new_s_json_obj = new_json_obj.toString();
        return new_s_json_obj;
    }

    @RequestMapping(value="/get_real_time_data_show")
    public String getRealTimeDataShow(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        ArrayList<Integer> real_time_traffic_amount_data = new ArrayList<Integer>();
        ArrayList<Integer> real_time_the_amount_of_store_data = new ArrayList<Integer>();
        ArrayList<Float> real_time_into_the_store_rate_data = new ArrayList<Float>();
        ArrayList<String> time = new ArrayList<String>();
        String wid = user.getWid();
        String data = Data.getRealTimeData(wid);
        if(data.startsWith("error")){
            return data;
        }else {
            JSONObject obj = new JSONObject(data);
            JSONObject aggregations = new JSONObject();
            aggregations = obj.getJSONObject("aggregations");
            JSONObject range1 = new JSONObject();
            range1 = aggregations.getJSONObject("range1");
            JSONArray buckets = new JSONArray();
            buckets = range1.getJSONArray("buckets");
            JSONObject buckets_inner_obj = new JSONObject();
            buckets_inner_obj = buckets.optJSONObject(0);
            JSONObject range2 = new JSONObject();
            range2 = buckets_inner_obj.getJSONObject("range2");
            JSONArray buckets2 = new JSONArray();
            buckets2 = range2.getJSONArray("buckets");
            JSONObject temp_obj = new JSONObject();
            for(int i = 0; i < buckets2.length(); i++){
                temp_obj  = buckets2.getJSONObject(i);
                String temp_time = temp_obj.getString("key_as_string");
                String[] arr_time = temp_time.split("T");
                temp_time = arr_time[1];
                String[] arr_time2 = temp_time.split(".000Z");
                temp_time = arr_time2[0];
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                try{
                    java.util.Date date_util = sdf.parse(temp_time);
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(date_util);
                    cal.add(Calendar.HOUR_OF_DAY, +8);// 往后加8小时
                    SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
                    temp_time = format.format(cal.getTime());
                    time.add(temp_time);
                }catch (Exception e){
                    return "error:时间转换出错！";
                }
                JSONObject temp_data = new JSONObject();
                temp_data = temp_obj.getJSONObject("data");
                JSONObject all_store = new JSONObject();
                JSONObject in_store = new JSONObject();
                all_store = temp_data.getJSONObject("all_store");
                in_store = temp_data.getJSONObject("in_store");
                JSONArray all_store_buckets = new JSONArray();
                JSONArray in_store_buckets = new JSONArray();
                all_store_buckets = all_store.getJSONArray("buckets");
                in_store_buckets = in_store.getJSONArray("buckets");
                int traffic_amount = all_store_buckets.getJSONObject(0).getInt("doc_count");
                int the_amount_of_store = in_store_buckets.getJSONObject(0).getInt("doc_count");
                float into_the_store_rate;
                if(traffic_amount == 0){
                    into_the_store_rate = 0;
                }else{
                    into_the_store_rate = (float)the_amount_of_store / (float)traffic_amount;
                }
                real_time_traffic_amount_data.add(traffic_amount);
                real_time_the_amount_of_store_data.add(the_amount_of_store);
                real_time_into_the_store_rate_data.add(into_the_store_rate);
            }
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("real_time_traffic_amount_data", real_time_traffic_amount_data);
            jsonObject.accumulate("real_time_the_amount_of_store_data", real_time_the_amount_of_store_data);
            jsonObject.accumulate("real_time_into_the_store_rate_data", real_time_into_the_store_rate_data);
            jsonObject.accumulate("time", time);
            String s_obj = jsonObject.toString();
            return s_obj;
        }
    }
}