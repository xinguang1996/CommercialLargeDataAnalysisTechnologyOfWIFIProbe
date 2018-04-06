$(document).ready(function(){
    var my_url = project_name + "/get_boot_and_shutdown_time";
    $.ajax({
       type: "GET",
       url: my_url,
       success: get_boot_and_shutdown_callback
     });
});

function get_boot_and_shutdown_callback(data)
{
    var obj = JSON.parse(data);
    var boot_time = obj["boot"];
    var shutdown_time = obj["shutdown"];
    if(boot_time == "" || boot_time == null)
    {
        boot_time = "----";
    }
    if(shutdown_time == "" || shutdown_time == null)
    {
        shutdown_time = "----";
    }
    document.getElementById("boot_time").value = boot_time;
    document.getElementById("shutdown_time").value = shutdown_time;
}

function cancel_boot()
{
    document.getElementById("boot_time").value = "----";
}

function cancel_shutdown()
{
    document.getElementById("shutdown_time").value = "----";
}

function set_ok()
{
    var boot_time = document.getElementById("boot_time").value;
    var shutdown_time = document.getElementById("shutdown_time").value;
    var automatic = new Object();
    if(boot_time == "----")
    {
        automatic.boot = null;
    }
    else
    {
        automatic.boot = boot_time;
    }
    if(shutdown_time == "----")
    {
        automatic.shutdown = null;
    }
    else
    {
        automatic.shutdown = shutdown_time;
    }
    var my_url = project_name + "/set_boot_and_shutdown_time";
    $.ajax({
       type: "GET",
       url: my_url,
       data: automatic,
       success: set_boot_and_shutdown_callback,
       error: function(data)
       {
           alert("发送请求失败！");
       }
     });
}

function set_boot_and_shutdown_callback(data)
{
    var index = data.indexOf("ok");
    if(index == 0)
    {
        alert("设置成功！");
    }
    else
    {
        alert("设置失败！");
    }
}

function boot()
{
    var my_url = project_name + "/boot_reboot_shutdown";
    var obj = new Object();
    obj.signal = "boot"
    $.ajax({
       type: "GET",
       url: my_url,
       data: obj,
       success: boot_callback,
       error: function(data)
       {
           alert("发送开机请求失败！");
       }
     });
}

function boot_callback(data)
{
    var index = data.indexOf("yes");
    if(index == 0)
    {
        alert("开机成功！");
    }
    else
    {
        var test = data.indexOf("no");
        if(test == 0)
        {
            alert("开机失败！机器已开机！");
        }
        else
        {
            alert("开机失败！连接控制设备出错！");
        }
    }
}

function shutdown()
{
    var my_url = project_name + "/boot_reboot_shutdown";
    var obj = new Object();
    obj.signal = "shutdown"
    $.ajax({
       type: "GET",
       url: my_url,
       data: obj,
       success: shutdown_callback,
       error: function(data)
       {
           alert("发送关机请求失败！");
       }
     });
}

function shutdown_callback(data)
{
    var index = data.indexOf("yes");
    if(index == 0)
    {
        alert("关机成功！");
    }
    else
    {
        var test = data.indexOf("no");
        if(test == 0)
        {
            alert("关机失败！机器已关机！");
        }
        else
        {
            alert("关机失败！连接控制设备出错！");
        }
    }
}

function reboot()
{
    var my_url = project_name + "/boot_reboot_shutdown";
    var obj = new Object();
    obj.signal = "reboot"
    $.ajax({
       type: "GET",
       url: my_url,
       data: obj,
       success: reboot_callback,
       error: function(data)
       {
           alert("发送重启请求失败！");
       }
     });
}

function reboot_callback(data)
{
    var index = data.indexOf("yes");
    if(index == 0)
    {
        alert("重启成功！");
    }
    else
    {
        var test = data.indexOf("no");
        if(test == 0)
        {
            alert("重启失败！系统已关机！");
        }
        else
        {
            alert("重启失败！连接控制设备出错！");
        }
    }
}