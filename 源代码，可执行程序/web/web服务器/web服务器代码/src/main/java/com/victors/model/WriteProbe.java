package com.victors.model;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Victors on 2017/9/2.
 */
public class WriteProbe {

    public static String writeProbeConfig(String path, String filename, String jsondata){
        File filePath = new File(path);
        if(!filePath.exists())
        {
            filePath.mkdir();
        }
        String stringFile = path + File.separator + filename;
        File file = new File(stringFile);
        try{
            FileOutputStream out = new FileOutputStream(file);
            byte buy[] = jsondata.getBytes();
            out.write(buy);
            out.close();
            return "ok";
        }catch(Exception e)
        {
            return "error:写配置出错！";
        }
    }

    public static Session connect(String host, Integer port, String user, String password) throws Exception {
        Session session = null;
        try
        {
            JSch jsch = new JSch();
            if(port != null)
            {
                session = jsch.getSession(user, host, port.intValue());
            }
            else
            {
                session = jsch.getSession(user, host);
            }
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.setTimeout(30000);
            session.connect();
            return session;
        }catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public static String upload(String directory, String uploadFile, ChannelSftp sftp) throws Exception {
        File file = new File(uploadFile);
        if(file.exists())
        {
            try
            {
                java.util.Vector content = sftp.ls(directory);
                if(content == null)
                {
                    sftp.mkdir(directory);
                }
            }catch(SftpException e)
            {
                sftp.mkdir(directory);
            }
            //进入目标路径
            sftp.cd(directory);
            if(file.isFile())
            {
                InputStream ins = new FileInputStream(file);
                //中文名称的
                sftp.put(ins, new String(file.getName().getBytes(),"UTF-8"));
            }
            return "ok";
        }
        else
        {
            return "error:本地配置文件不存在！";
        }
    }
}
