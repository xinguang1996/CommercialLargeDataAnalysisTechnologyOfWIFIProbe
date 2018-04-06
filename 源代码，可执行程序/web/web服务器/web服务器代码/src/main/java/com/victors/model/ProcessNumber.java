package com.victors.model;

/**
 * Created by Victors on 2017/8/20.
 */
public class ProcessNumber {
    public static String processNumber(int number)
    {
        if(number < 10)
        {
            return "0" + number + "";
        }
        else
        {
            return number + "";
        }
    }
}
