var name = "";
var mail = "";
var tel = "";
var old_password = "";
var password = "";
var re_password = "";
function set_ok(){
	name = document.getElementById("name").value;
	mail = document.getElementById("mail").value;
	tel = document.getElementById("tel").value;
	old_password = document.getElementById("old_password").value;
	password = document.getElementById("password").value;
	re_password = document.getElementById("password2").value;
	if(name == "" || mail == "" || tel == "" || old_password == "" || password == "" || re_password == ""){
		alert("所有字段不能为空！");
	}else{
		if(password != re_password){
			alert("两次密码输入不一致！");
		}else{
			request_reset_password();
		}
	}
}

function request_reset_password(){
	var my_url = project_name + "/reset_password";
	var my_info = new Object();
	my_info.name = name;
	my_info.mail = mail;
	my_info.tel = tel;
	my_info.old_password = old_password;
	my_info.password = password;
	my_info.re_password = re_password;
	$.ajax({
		type: "GET",
		url: my_url,
		data: my_info,
		success: reset_password_callback,
		error: function(data){
			alert("连接服务器出错！");
		}
	});
}

function reset_password_callback(data){
	var temp = data.indexOf("ok");
	if(temp == 0){
		alert("修改密码成功！");
		window.location.href = project_name + "/data/index.html";
	}else{
		alert(data);
	}
}