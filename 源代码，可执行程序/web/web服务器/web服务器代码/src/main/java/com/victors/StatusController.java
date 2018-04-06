package com.victors;

import com.victors.model.User;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Victors on 2017/8/29.
 */
@RestController
public class StatusController {

    @RequestMapping(value="/get_system_status")
    public String getSystemStatus(HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session.getAttribute("user") == null){
            return "error";
        }
        User user = new User();
        user = (User)session.getAttribute("user");
        session.setAttribute("user", user);
        //获得状态json文件路径
        String path = File.separator + "home" + File.separator + "pi" + File.separator + "Monitor" + File.separator + "status.json";
        try
        {
            File file = new File(path);
            if(file.exists())
            {
                BufferedReader br = new BufferedReader(new FileReader(path));//读取json文件
                String temp = "";
                String s = "";
                while((temp = br.readLine()) != null)
                {
                    s += temp;
                }
                return s;
            }
            else
            {
                return "错误！没有监控信息！";
            }
        }catch(Exception e)
        {
            return "错误！服务器出错！";
        }
    }
}
