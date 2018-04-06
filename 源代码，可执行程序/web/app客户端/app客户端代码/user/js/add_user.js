var id;
var name;
var mail;
var tel;
var wid;
var addr;
var password;
var repassword;
mui.plusReady(function(){
	//监听确认按钮
	document.getElementById("set_ok").addEventListener("tap", function(){
		id = document.getElementById("id").value;
		name = document.getElementById("name").value;
		mail = document.getElementById("mail").value;
		tel = document.getElementById("tel").value;
		wid = document.getElementById("wid").value;
		addr = document.getElementById("addr").value;
		password = document.getElementById("password").value;
		repassword = document.getElementById("repassword").value;
		if (id == "" || name == "" || mail == "" || tel == "" || wid == "" || addr == "" || password == "" || repassword == "") {
			mui.alert("所有字段不能为空！");
		} else {
			if (password != repassword) {
				mui.alert("两次密码不一致！");
			} else {
				request_add_user();
			}
		}
	});
});

function request_add_user(){
	var obj = new Object();
	obj.id = id;
	obj.name = name;
	obj.mail = mail;
	obj.tel = tel;
	obj.wid = wid;
	obj.addr = addr;
	obj.password = password;
	obj.repassword = repassword;
	mui.ajax(my_ip + "/add_user", {
		data: obj,
		type: "post",
		timeout: 8000,
		success: function(data){
			var index = data.indexOf("ok");
			if(index == 0){
				mui.toast("注册成功！");
				mui.openWindow({
					url: '../main/recent_data.html',
					id: 'recent_data.html'
				});
			}else{
				mui.alert(data);
			}
		},
		error: function(xhr, type, errorThrown){
			mui.alert("网络错误！");
		}
	});
}
