package com.victors;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.Session;
import com.victors.model.User;
import com.victors.model.WriteAndSendBootAndShutdownTime;
import com.victors.model.WriteProbe;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by Victors on 2017/9/2.
 */
@RestController
public class ProbeController {

    @RequestMapping(value="/set_probe")
    public String setProbe(@RequestParam String probe_ip, @RequestParam String probe_post, @RequestParam String send_frequency, @RequestParam String visit_the_time, @RequestParam String out_of_time, @RequestParam String shop_distance, @RequestParam String sleep_threshold, @RequestParam String low_activity_threshold, @RequestParam String middle_activity_threshold, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        if(probe_ip.equals("") || probe_post.equals("") || send_frequency.equals("") || visit_the_time.equals("") || out_of_time.equals("") || shop_distance.equals("") || sleep_threshold.equals("") || low_activity_threshold.equals("") || middle_activity_threshold.equals("")){
            return "error:所有字段不能为空！";
        }else{
            JSONObject dataJson = new JSONObject();
            dataJson.accumulate("Server Ip", probe_ip);
            dataJson.accumulate("Server Post", probe_post);
            dataJson.accumulate("Send frequency", send_frequency);
            dataJson.accumulate("Visit the time", visit_the_time);
            dataJson.accumulate("Out of time", out_of_time);
            dataJson.accumulate("Shop distance", shop_distance);
            dataJson.accumulate("Sleepy threshold", send_frequency);
            dataJson.accumulate("Low activity threshold", low_activity_threshold);
            dataJson.accumulate("Middle activity threshold", middle_activity_threshold);
            String s_data = dataJson.toString();
            String filepath = File.separator + "home" + File.separator + "pi" + File.separator + "probe";
            String filename = "config.json";
            String writeResult = WriteProbe.writeProbeConfig(filepath, filename, s_data);
            if(writeResult.equals("ok")){
                ChannelSftp sftp = null;
                Session session = null;
                String host = "192.168.1.100";//发送的IP
                Integer port = null;//默认端口
                String username = "pi";//用户名
                String password = "raspberry";//密码
                String directory = ".configuration";//传送的目录
                String uploadFile = File.separator + "home" + File.separator + "pi" + File.separator + "probe" + File.separator + "config.json";
                try
                {
                    session = WriteProbe.connect(host, port, username, password);
                    com.jcraft.jsch.Channel channel = session.openChannel("sftp");
                    channel.connect();
                    sftp = (ChannelSftp)channel;
                    String upload_is_ok = WriteProbe.upload(directory, uploadFile, sftp);
                    if(upload_is_ok.equals("ok")){
                        return "ok";
                    }else{
                        if(sftp != null)
                        {
                            sftp.disconnect();
                        }
                        if(session != null)
                        {
                            session.disconnect();
                        }
                        return upload_is_ok;
                    }
                }catch(Exception e)
                {
                    return "error:传送配置文件出现错误！";
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
            }else{
                return writeResult;
            }
        }
    }

    @RequestMapping(value="/get_probe")
    public String getProbe(HttpServletRequest request, HttpServletResponse response) {
        response.setDateHeader("Expires", -1);
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        String filename = "config.json";//json文件名
        String path;//文件路径
        path = File.separator + "home" + File.separator + "pi" + File.separator + "probe" + File.separator + filename;
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
                return s;
            }catch (Exception e){
                return "error:服务器读取配置文件出错！";
            }
        }
        else
        {
            return "error:未找到配置文件！";
        }
    }
}
