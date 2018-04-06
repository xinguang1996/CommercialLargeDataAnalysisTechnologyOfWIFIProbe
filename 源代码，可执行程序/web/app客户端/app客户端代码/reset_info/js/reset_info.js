mui.plusReady(function(){
	//获得信息
	mui.ajax(my_ip + "/get_user", {
		type: "get",
		timeout: 8000,
		success: function(data){
			var temp = data.indexOf("error");
			if(temp != -1){
				mui.toast("请您登陆！");
				mui.openWindow({
					url: '../login/login.html',
					id: 'login.html'
				});
			}else{
				var obj = JSON.parse(data);
				var name = obj["name"];
				var my_name = name;
				var my_id = obj["id"];
				var my_mail = obj["mail"];
				var my_tel = obj["tel"];
				var my_wid = obj["wid"];
				var my_addr = obj["addr"];
				document.getElementById("id").value = my_id;
				document.getElementById("name").value = my_name;
				document.getElementById("mail").value = my_mail;
				document.getElementById("tel").value = my_tel;
				document.getElementById("wid").value = my_wid;
				document.getElementById("addr").value = my_addr;
			}
		},
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
	//监听确认按钮
	document.getElementById("set_ok").addEventListener("tap", function(){
		var mail = document.getElementById("mail").value;
		var tel = document.getElementById("tel").value;
		var wid = document.getElementById("wid").value;
		var addr = document.getElementById("addr").value;
		if(mail == "" || tel == "" || wid == "" || addr == ""){
			mui.alert("所有字段不能为空！");
		}else{
			var obj = new Object();
			obj.mail = mail;
			obj.tel = tel;
			obj.wid = wid;
			obj.addr = addr;
			mui.ajax(my_ip + "/reset_info", {
				data: obj,
				type: "post",
				timeout: 8000,
				success: function(data){
					var index = data.indexOf("ok");
					if(index == 0){
						mui.toast("修改信息成功！");
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
	});
});