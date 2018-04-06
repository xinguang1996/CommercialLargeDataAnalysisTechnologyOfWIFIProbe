package com.victors.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Victors on 2017/8/19.
 */
public class Client {

    public static String sendMessage(String host, int port, String message){
        String info = "";
        try
        {
            //创建Socket对象
            Socket socket = new Socket(host, port);
            //根据输入输出流和服务器连接
            OutputStream outputStream = socket.getOutputStream();//获取一个输出流，向服务器发送信息
            PrintWriter printWriter = new PrintWriter(outputStream);//将输出流包装成打印流
            printWriter.print(message);
            printWriter.flush();
            socket.shutdownOutput();//关闭输出流

            InputStream inputStream = socket.getInputStream();//获取一个输入流，接收服务器的信息
            //InputStreamReader inputStreamReader = new InputStreamReader(inputStream);//包装成字符流，提高效率
            //BufferedReader bufferedReader = new BufferedReader(inputStreamReader);//缓冲区
            byte[] buf = new byte[1024];
            int len = inputStream.read(buf);
            info = new String(buf,0,len);
            //String temp = null;//临时变量
			/*
			while((temp = bufferedReader.readLine()) != null)
			{
				info += temp;
			}*/
            //关闭相对应的资源
            //bufferedReader.close();
            inputStream.close();
            printWriter.close();
            outputStream.close();
            socket.close();
        }catch(UnknownHostException e)
        {
            e.printStackTrace();
        }catch(IOException e)
        {
            e.printStackTrace();
        }
        return info;
    }
}
