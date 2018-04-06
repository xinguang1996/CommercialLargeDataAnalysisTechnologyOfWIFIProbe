mui.plusReady(function(){
	//监听确认按钮
	document.getElementById("set_ok").addEventListener("tap", function(){
		var name = document.getElementById("name").value;
		var mail = document.getElementById("mail").value;
		var tel = document.getElementById("tel").value;
		var old_password = document.getElementById("old_password").value;
		var password = document.getElementById("password").value;
		var re_password = document.getElementById("password2").value;
		if(name == "" || mail == "" || tel == "" || old_password == "" || password == "" || re_password == ""){
			alert("所有字段不能为空！");
		}else{
			if(password != re_password){
				alert("两次密码输入不一致！");
			}else{
				var obj = new Object();
				obj.name = name;
				obj.mail = mail;
				obj.tel = tel;
				obj.old_password = old_password;
				obj.password = password;
				obj.re_password = re_password;
				mui.ajax(my_ip + "/reset_password", {
					data: obj,
					type: "post",
					timeout: 8000,
					success: function(data){
						var index = data.indexOf("ok");
						if(index == 0){
							mui.toast("修改密码成功！");
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
		}
	});
});
