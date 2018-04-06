package com.victors;

import com.victors.model.MD5;
import com.victors.model.User;
import com.victors.model.UserInfo;
import org.json.JSONObject;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.PrintWriter;
import java.util.List;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
/**
 * Created by Victors on 2017/8/18.
 */
@RestController
public class LoginController {

    @RequestMapping(value="/login")
    public String login(@RequestParam String id, @RequestParam String password, HttpServletRequest request, HttpServletResponse response) {
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        if(id != null && !id.equals(""))
        {
            if(password != null && !password.equals(""))
            {
                User user = new User();
                user.setId(id);
                user.setPassword(password);
                if(UserInfo.testUser(user))
                {
                    user = UserInfo.getUser(id);
                    if(user != null)
                    {
                        HttpSession session = request.getSession();
                        session.setAttribute("user", user);
                        return "ok";
                    }
                    else
                    {
                        return "error:写入session出错";
                    }
                }
                else
                {
                    return "error:账号或密码错误！";
                }
            }
            else
            {
                return "error:密码不能为空！";
            }
        }
        else
        {
            return "error:id不能为空！";
        }
    }

    @RequestMapping(value="/forget_password")
    public String forgetPassword(@RequestParam String id, @RequestParam String name, @RequestParam String mail, @RequestParam String tel, @RequestParam String addr, @RequestParam String password, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        if(id.equals("") || name.equals("")|| mail.equals("") || tel.equals("") || addr.equals("") || password.equals("")){
            return "error:所有字段不能为空！";
        }else{
            User user = new User();
            user.setId(id);
            user.setPassword(password);
            user.setName(name);
            user.setMail(mail);
            user.setAddr(addr);
            user.setTel(tel);
            String info = UserInfo.setForgetPassword(user);
            if(info.equals("ok")){
                user = UserInfo.getUser(id);
                HttpSession session = request.getSession();
                session.setAttribute("user", user);
            }
            return info;
        }
    }

    @RequestMapping(value="/get_user")
    public String getUser(HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session.getAttribute("user") != null){
            User user = new User();
            user = (User)session.getAttribute("user");
            String name = user.getName();
            String id = user.getId();
            String mail  = user.getMail();
            String tel = user.getTel();
            String wid = user.getWid();
            String addr = user.getAddr();
            if(name.equals("")){
                return "error";
            }else {
                JSONObject obj = new JSONObject();
                obj.accumulate("name", name);
                obj.accumulate("id", id);
                obj.accumulate("mail", mail);
                obj.accumulate("tel", tel);
                obj.accumulate("wid", wid);
                obj.accumulate("addr", addr);
                return obj.toString();
            }
        }else{
            return "error";
        }
    }

    /*
    @RequestMapping(value="/add_user_web")
    public void addUserWeb(HttpServletRequest request, HttpServletResponse response){
        final long serialVersionUID = 1L;
        // 上传文件存储目录
        final String UPLOAD_DIRECTORY = "head_portrait";
        // 上传配置
        final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
        final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
        final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
        response.setContentType("text/html;charset=utf-8");
        User user = new User();
        String id = "";
        String name = "";
        String mail = "";
        String tel = "";
        String wid = "";
        String addr = "";
        String password = "";
        String repassword = "";
        try {
            PrintWriter out = response.getWriter();
            request.setCharacterEncoding("utf-8");
            // 检测是否为多媒体上传
            if (!ServletFileUpload.isMultipartContent(request)) {
                // 如果不是则停止
                PrintWriter writer = response.getWriter();
                writer.println("Error: 表单必须包含 enctype=multipart/form-data");
                writer.flush();
                return;
            }
            // 配置上传参数
            DiskFileItemFactory factory = new DiskFileItemFactory();
            // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
            factory.setSizeThreshold(MEMORY_THRESHOLD);
            // 设置临时存储目录
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
            ServletFileUpload upload = new ServletFileUpload(factory);
            // 设置最大文件上传值
            //upload.setFileSizeMax(MAX_FILE_SIZE);
            // 设置最大请求值 (包含文件和表单数据)
           //upload.setSizeMax(MAX_REQUEST_SIZE);
            // 中文处理
            upload.setHeaderEncoding("UTF-8");
            // 构造临时路径来存储上传的文件
            // 这个路径相对当前应用的目录
            String uploadPath = UPLOAD_DIRECTORY;
            // 如果目录不存在则创建
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            try {
                // 解析请求的内容提取文件数据
                @SuppressWarnings("unchecked")
                List<FileItem> formItems = upload.parseRequest(request);
                out.print(formItems);
                if (formItems != null && formItems.size() > 0) {
                    // 迭代表单数据
                    for (FileItem item : formItems) {
                        // 处理不在表单中的字段
                        if (!item.isFormField()) {
                            String fileName = new File(item.getName()).getName();
                            String filePath = uploadPath + File.separator + id + ".jpg";
                            String img = "/softbei/head_portrait/" + id + ".jpg";
                            File storeFile = new File(filePath);
                            // 保存文件到硬盘
                            item.write(storeFile);
                            user.setImg(img);
                        }
                        //如果是普通表单字段
                        else
                        {
                            String temp_name = item.getFieldName();
                            if(temp_name.equals("id")){
                                String value = item.getString();
                                id = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(id.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("add_user_err", "error:账号不能为空！");
                                    response.sendRedirect("/softbei/data/add_user_error.html");
                                    return;
                                }else{
                                    String result = UserInfo.testId(id);
                                    if(!result.equals("ok")){
                                        HttpSession session = request.getSession();
                                        session.setAttribute("add_user_err", result);
                                        response.sendRedirect("/softbei/data/add_user_error.html");
                                        return;
                                    }else{
                                        out.print(id);
                                        user.setId(id);
                                    }
                                }
                            }
                            if(temp_name.equals("name")){
                                String value = item.getString();
                                name = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(name.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("add_user_err", "error:姓名不能为空！");
                                    response.sendRedirect("/softbei/data/add_user_error.html");
                                    return;
                                }else{
                                    out.print(name);
                                    user.setName(name);
                                }
                            }
                            if(temp_name.equals("mail")){
                                String value = item.getString();
                                mail = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(mail.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("add_user_err", "error:电子邮箱不能为空！");
                                    response.sendRedirect("/softbei/data/add_user_error.html");
                                    return;
                                }else{
                                    out.print(mail);
                                    user.setMail(mail);
                                }
                            }
                            if(temp_name.equals("tel")){
                                String value = item.getString();
                                tel = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(tel.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("add_user_err", "error:电话不能为空！");
                                    response.sendRedirect("/softbei/data/add_user_error.html");
                                    return;
                                }else{
                                    out.print(tel);
                                    user.setTel(tel);
                                }
                            }
                            if(temp_name.equals("wid")){
                                String value = item.getString();
                                wid = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(wid.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("add_user_err", "error:探针id不能为空！");
                                    response.sendRedirect("/softbei/data/add_user_error.html");
                                    return;
                                }else{
                                    out.print(wid);
                                    user.setWid(wid);
                                }
                            }
                            if(temp_name.equals("addr")){
                                String value = item.getString();
                                addr = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(addr.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("add_user_err", "error:地址不能为空！");
                                    response.sendRedirect("/softbei/data/add_user_error.html");
                                    return;
                                }else{
                                    out.print(addr);
                                    user.setAddr(addr);
                                }
                            }
                            if(temp_name.equals("password")){
                                String value = item.getString();
                                password = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(password.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("add_user_err", "error:密码不能为空！");
                                    response.sendRedirect("/softbei/data/add_user_error.html");
                                    return;
                                }else{
                                    out.print(password);
                                }
                            }
                            if(temp_name.equals("repassword")){
                                String value = item.getString();
                                repassword = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(password.equals(repassword)){
                                    user.setPassword(password);
                                }else{
                                    HttpSession session = request.getSession();
                                    session.setAttribute("add_user_err", "error:两次密码不一致！");
                                    response.sendRedirect("/softbei/data/add_user_error.html");
                                    return;
                                }
                            }
                        }
                    }
                }
                String add_result = UserInfo.addUser(user);
                if(add_result.equals("ok")){
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    response.sendRedirect("/softbei/data/index.html");
                    return;
                }else{
                    HttpSession session = request.getSession();
                    session.setAttribute("add_user_err", add_result);
                    response.sendRedirect("/softbei/data/add_user_error.html");
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    */
    @RequestMapping(value="/add_user")
    public String addUser(@RequestParam String id, @RequestParam String name, @RequestParam String mail, @RequestParam String tel, @RequestParam String wid, @RequestParam String addr, @RequestParam  String password, @RequestParam String repassword, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        if(id.equals("") || name.equals("") || mail.equals("") || tel.equals("") || wid.equals("") || addr.equals("") || password.equals("") || repassword.equals("")){
            return "error:所有字段不能为空！";
        }else{
            if(password.equals(repassword)){
                User user = new User();
                String result = UserInfo.testId(id);
                if(!result.equals("ok")){
                    return result;
                }else{
                    user.setId(id);
                    user.setName(name);
                    user.setMail(mail);
                    user.setTel(tel);
                    user.setWid(wid);
                    user.setAddr(addr);
                    user.setPassword(password);
                    String add_result = UserInfo.addUser(user);
                    if(add_result.equals("ok")){
                        HttpSession session = request.getSession();
                        session.setAttribute("user", user);
                        return "ok";
                    }else{
                        return add_result;
                    }
                }
            }else{
                return "error:两次密码不一致！";
            }
        }
    }
    /*
    @RequestMapping(value="/get_add_user_error")
    public String getAddUserError(HttpServletRequest request){
        HttpSession session = request.getSession();
        if(session.getAttribute("add_user_err") != null){
            return (String)session.getAttribute("add_user_err");
        }else{
            return "ok";
        }
    }*/

    @RequestMapping(value="/log_out")
    public String logOut(HttpServletRequest request){
        HttpSession session = request.getSession();
        session.setAttribute("user", null);
        return "ok";
    }

    @RequestMapping(value="/reset_password")
    public String resetPassword(@RequestParam String name, @RequestParam String mail, @RequestParam String tel, @RequestParam String old_password, @RequestParam String password, @RequestParam String re_password, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        HttpSession session = request.getSession();
        if(session.getAttribute("user") != null){
            User user = new User();
            user = (User)session.getAttribute("user");
            if(name.equals("") || mail.equals("") || tel.equals("") || old_password.equals("") || password.equals("") || re_password.equals("")){
                return "error:所有字段不能为空！";
            }else{
                if(password.equals(re_password)){
                    if(user.getPassword().equals(MD5.md5(old_password))){
                        user.setName(name);
                        user.setMail(mail);
                        user.setTel(tel);
                        user.setPassword(password);
                        String info = UserInfo.setForgetPassword(user);
                        return info;
                    }else{
                        return "error:密码错误！";
                    }
                }else{
                    return "error:两次密码不一致！";
                }
            }
        }else{
            return "error:账户未登录！";
        }
    }
/*
    @RequestMapping(value="/reset_info_web")
    public void resetInfoWeb(HttpServletRequest request, HttpServletResponse response){
        final long serialVersionUID = 1L;
        // 上传文件存储目录
        final String UPLOAD_DIRECTORY = "head_portrait";
        // 上传配置
        final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
        final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
        final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
        response.setContentType("text/html;charset=utf-8");
        if(request.getSession().getAttribute("user") == null){
            try{
                response.sendRedirect("/softbei/data/login.html");
                return;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        User user = new User();
        user = (User) request.getSession().getAttribute("user");
        String id = user.getId();
        String name = user.getName();
        String mail = "";
        String tel = "";
        String wid = "";
        String addr = "";
        String img = "";
        try {
            PrintWriter out = response.getWriter();
            request.setCharacterEncoding("utf-8");
            // 检测是否为多媒体上传
            if (!ServletFileUpload.isMultipartContent(request)) {
                // 如果不是则停止
                PrintWriter writer = response.getWriter();
                writer.println("Error: 表单必须包含 enctype=multipart/form-data");
                writer.flush();
                return;
            }
            // 配置上传参数
            DiskFileItemFactory factory = new DiskFileItemFactory();
            // 设置内存临界值 - 超过后将产生临时文件并存储于临时目录中
            factory.setSizeThreshold(MEMORY_THRESHOLD);
            // 设置临时存储目录
            factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
            ServletFileUpload upload = new ServletFileUpload(factory);
            // 中文处理
            upload.setHeaderEncoding("UTF-8");
            // 构造临时路径来存储上传的文件
            // 这个路径相对当前应用的目录
            String uploadPath = UPLOAD_DIRECTORY;
            // 如果目录不存在则创建
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdir();
            }
            try {
                // 解析请求的内容提取文件数据
                @SuppressWarnings("unchecked")
                List<FileItem> formItems = upload.parseRequest(request);
                if (formItems != null && formItems.size() > 0) {
                    // 迭代表单数据
                    for (FileItem item : formItems) {
                        // 处理不在表单中的字段
                        if (!item.isFormField()) {
                            String fileName = new File(item.getName()).getName();
                            String filePath = uploadPath + File.separator + id + ".jpg";
                            img = "/softbei/head_portrait/" + id + ".jpg";
                            File storeFile = new File(filePath);
                            // 保存文件到硬盘
                            item.write(storeFile);
                            user.setImg(img);
                        }
                        //如果是普通表单字段
                        else
                        {
                            String temp_name = item.getFieldName();
                            if(temp_name.equals("mail")){
                                String value = item.getString();
                                mail = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(mail.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("reset_info_err", "error:电子邮箱不能为空！");
                                    response.sendRedirect("/softbei/data/reset_info_error.html");
                                    return;
                                }else{
                                    user.setMail(mail);
                                }
                            }
                            if(temp_name.equals("tel")){
                                String value = item.getString();
                                tel = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(tel.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("reset_info_err", "error:电话不能为空！");
                                    response.sendRedirect("/softbei/data/reset_info_error.html");
                                    return;
                                }else{
                                    user.setTel(tel);
                                }
                            }
                            if(temp_name.equals("wid")){
                                String value = item.getString();
                                wid = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(wid.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("reset_info_err", "error:电子邮箱不能为空！");
                                    response.sendRedirect("/softbei/data/reset_info_error.html");
                                    return;
                                }else{
                                    user.setWid(wid);
                                }
                            }
                            if(temp_name.equals("addr")){
                                String value = item.getString();
                                addr = new String(value.getBytes("iso-8859-1"),"utf-8");
                                if(addr.equals("")){
                                    HttpSession session = request.getSession();
                                    session.setAttribute("reset_info_err", "error:电话不能为空！");
                                    response.sendRedirect("/softbei/data/reset_info_error.html");
                                    return;
                                }else{
                                    user.setAddr(addr);
                                }
                            }
                        }
                    }
                }
                String reset_info_result = UserInfo.resetInfo(user);
                if(reset_info_result.equals("ok")){
                    HttpSession session = request.getSession();
                    session.setAttribute("user", user);
                    response.sendRedirect("/softbei/data/index.html");
                    return;
                }else{
                    HttpSession session = request.getSession();
                    session.setAttribute("reset_info_err", reset_info_result);
                    response.sendRedirect("/softbei/data/reset_info_error.html");
                    return;
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }*/

    @RequestMapping(value="/reset_info")
    public String resetInfo(@RequestParam String mail, @RequestParam String tel, @RequestParam String wid, @RequestParam String addr, HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        HttpSession session = request.getSession();
        if(session.getAttribute("user") != null){
            User user = new User();
            user = (User)session.getAttribute("user");
            if(mail.equals("") || tel.equals("") || wid.equals("") || addr.equals("")){
                return "error:所有字段不能为空！";
            }else{
                user.setMail(mail);
                user.setTel(tel);
                user.setWid(wid);
                user.setAddr(addr);
                String reset_info_result = UserInfo.resetInfo(user);
                if(reset_info_result.equals("ok")){
                    session.setAttribute("user", user);
                    return "ok";
                }else{
                    return reset_info_result;
                }
            }
        }else{
            return "error:账户未登录！";
        }
    }

    @RequestMapping(value="/get_reset_info_error")
    public String getResetInfoError(HttpServletRequest request, HttpServletResponse response){
        response.setDateHeader("Expires",-1);
        response.setHeader("Cache-Control","no-cache");
        response.setHeader("Pragma","no-cache");
        HttpSession session = request.getSession();
        if(session.getAttribute("reset_info_err") != null){
            return (String)session.getAttribute("reset_info_err");
        }else{
            return "ok";
        }
    }
}
