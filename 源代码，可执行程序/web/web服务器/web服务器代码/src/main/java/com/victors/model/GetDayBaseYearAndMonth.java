package com.victors.model;

/**
 * Created by Victors on 2017/8/20.
 */
public class GetDayBaseYearAndMonth {
    public static int getDay(int year, int month)
    {
        int[] init_day = {31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
        int day;
        day = init_day[month - 1];
        //判断处理2月
        if(month == 2)
        {
            //判断处理闰年
            if(judgeLeapYear(year))
            {
                day = 29;
            }
        }
        return day;
    }

    public static Boolean judgeLeapYear(int year)
    {
        //判断是否整除100
        if(year % 100 == 0)
        {
            //判断是否整除400
            if(year % 400 == 0)
            {
                return true;
            }
        }
        else
        {
            //判断是否整除4
            if(year % 4 == 0)
            {
                return true;
            }
        }
        return false;
    }
}
