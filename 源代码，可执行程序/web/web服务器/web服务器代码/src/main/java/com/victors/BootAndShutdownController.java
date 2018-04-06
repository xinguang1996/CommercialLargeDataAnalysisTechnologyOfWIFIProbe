package com.victors;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.victors.model.Client;
import com.victors.model.WriteAndSendBootAndShutdownTime;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Victors on 2017/8/19.
 */
@RestController
public class BootAndShutdownController {

    @RequestMapping(value="/get_boot_and_shutdown_time")
    public String getBootAndShutdownTime(HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String filename = "automatic.json";//json文件名
        String path;//文件路径
        path =  File.separator + "home" + File.separator + "pi" + File.separator + "Automatic" + File.separator + filename;
        File file = new File(path);
        if(file.exists())
        {
            try {
                BufferedReader br = new BufferedReader(new FileReader(path));//读取json文件
                String temp = null;
                String s = "";
                while ((temp = br.readLine()) != null) {
                    s += temp;
                }
                br.close();
                try {
                    JSONObject dataJson = new JSONObject(s);//创建一个包含json串的json对象
                    String s_dataJson = dataJson.toString();
                    return s_dataJson;
                } catch (Exception e) {
                    String boot_time = null;
                    String shutdown_time = null;
                    JSONObject dataJson = new JSONObject();
                    dataJson.accumulate("boot", boot_time);
                    dataJson.accumulate("shutdown", shutdown_time);
                    String s_dataJson = dataJson.toString();
                    return s_dataJson;
                }
            }catch (Exception e){
                String boot_time = null;
                String shutdown_time = null;
                JSONObject dataJson = new JSONObject();
                dataJson.accumulate("boot", boot_time);
                dataJson.accumulate("shutdown", shutdown_time);
                String s_dataJson = dataJson.toString();
                return s_dataJson;
            }
        }
        else
        {
            String boot_time = null;
            String shutdown_time = null;
            JSONObject dataJson = new JSONObject();
            dataJson.accumulate("boot", boot_time);
            dataJson.accumulate("shutdown", shutdown_time);
            String s_dataJson = dataJson.toString();
            return s_dataJson;
        }
    }

    @RequestMapping(value="/boot_reboot_shutdown")
    public String bootRebootShutdown(@RequestParam String signal, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        String host = "192.168.1.201";//主机地址
        int port = 12346;//端口号
        String callback_signal = Client.sendMessage(host, port, signal);//发送信号
        return callback_signal;
    }

    @RequestMapping(value="set_boot_and_shutdown_time")
    public String setBootAndShutdownTime(@RequestParam String boot, @RequestParam String shutdown, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        JSONObject dataJson = new JSONObject();
        dataJson.accumulate("boot", boot);
        dataJson.accumulate("shutdown", shutdown);
        String s_data = dataJson.toString();
        String filepath = File.separator + "home" + File.separator + "pi" + File.separator + "Automatic";
        String filename = "automatic.json";
        Boolean file_is_ok = WriteAndSendBootAndShutdownTime.WriteBootAndShutdownTime(filepath, filename, s_data);
        if(!file_is_ok)
        {
            return "false";
        }
        ChannelSftp sftp = null;
        Session session = null;
        String host = "192.168.1.201";//发送的IP
        Integer port = null;//默认端口
        String username = "pi";//用户名
        String password = "raspberry";//密码
        String directory = "Automatic";//传送的目录
        String uploadFile = File.separator + "home" + File.separator + "pi" + File.separator + "Automatic" + File.separator + "automatic.json";
        try
        {
            session = WriteAndSendBootAndShutdownTime.connect(host, port, username, password);
            com.jcraft.jsch.Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp)channel;
            Boolean upload_is_ok = WriteAndSendBootAndShutdownTime.upload(directory, uploadFile, sftp);
            if(!upload_is_ok)
            {
                if(sftp != null)
                {
                    sftp.disconnect();
                }
                if(session != null)
                {
                    session.disconnect();
                }
                return "false";
            }
            return "ok";
        }catch(Exception e)
        {
            return "false";
        }finally
        {
            if(sftp != null)
            {
                sftp.disconnect();
            }
            if(session != null)
            {
                session.disconnect();
            }
        }
    }
}
