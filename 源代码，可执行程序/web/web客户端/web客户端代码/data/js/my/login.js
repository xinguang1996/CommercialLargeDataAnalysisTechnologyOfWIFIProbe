function login(){
	var id = document.getElementById("id").value;
	var password = document.getElementById("password").value;
	if(id == "" || password ==""){
		alert("账号密码不能为空！");
	}else{
		var obj = new Object();
		obj.id = id;
		obj.password = password;
		var my_url = project_name + "/login";
		$.ajax({
			data: obj,
			type: "POST",
			url: my_url,
			success: login_callback,
			error: function(data){
				alert("连接服务器失败！");
			}
		});
	}
}

function login_callback(data){
	var index = data.indexOf("ok");
	if(index == 0){
		window.location.href = project_name + "/data/index.html";
	}else{
		alert(data);
	}
}

function forget_password(){
	var id = document.getElementById("re_id").value;
	var name = document.getElementById("re_name").value;
	var mail = document.getElementById("re_mail").value;
	var tel = document.getElementById("re_tel").value;
	var addr = document.getElementById("re_addr").value;
	var password = document.getElementById("re_password").value;
	if(id == "" || name == "" || mail == "" || tel == "" || addr == "" || password == ""){
		alert("所有字段不能为空！");
	}else{
		var obj = new Object();
		obj.id = id;
		obj.name = name;
		obj.mail = mail;
		obj.tel = tel;
		obj.addr = addr;
		obj.password = password;
		var my_url = project_name + "/forget_password";
		$.ajax({
			data: obj,
			type: "POST",
			url: my_url,
			success: forget_password_callback,
			error: function(data){
				alert("连接服务器失败！");
			}
		});
	}
}

function forget_password_callback(data){
	var index = data.indexOf("ok");
	if(index == 0){
		alert("设置密码成功！");
		window.location.href = project_name + "/data/index.html";
	}else{
		alert(data);
	}
}