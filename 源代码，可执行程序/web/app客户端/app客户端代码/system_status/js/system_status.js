var web;
var http;
var elasticsearch;
var spark;
var wechat;
var probe;
var sms;
var my_id;
mui.plusReady(function(){
	request_status();
	setInterval("request_status()",1000);
});

function request_status(){
	console.log("请求！");
	mui.ajax(my_ip + "/get_system_status", {
		type: "get",
		timeout: 8000,
		success: status_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}


function status_callback(data){
	console.log("进入status_callback");
	var index = data.indexOf("error");
	if(index == 0){
		mui.toast("请先登陆！");
		mui.openWindow({
			url: '../login/login.html',
			id: 'login.html'
		});
	}else{
		index = data.indexOf("错误");
		if(index == 0){
			alert(data);
		}else{
			var obj = JSON.parse(data);
			web = obj["web"];
			http = obj["http"];
			elasticsearch = obj["es"];
			spark = obj["spark"];
			wechat = obj["wechat"];
			probe = obj["probe"];
			sms = obj["sms"];
			request_my_info();
		}
	}
}

function request_my_info(){
	console.log("请求用户信息");
	//获取登陆者信息
	mui.ajax(my_ip + "/get_user", {
		type: "get",
		timeout: 8000,
		success: function(data){
			console.log("请求用户信息成功");
			var temp = data.indexOf("error");
			if(temp != -1){
				mui.plusReady(function(){
					mui.openWindow({
						url: '../login/login.html',
						id: 'login.html'
					});
				});
			}else{
				console.log("用户已登录！");
				var obj = JSON.parse(data);
				my_id = obj["id"];
				show_status();
			}
		},
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}
function show_status(){
	console.log("进入展示页面");
	var str = "<tr><th>名称</th><th>ip</th><th>状态</th><th>服务</th></tr>";
	var temp;
	var elasticsearch_obj;
	var spark_obj;
	var http_obj;
	var web_obj;
	var sms_obj;
	var wechat_obj;
	var probe_obj;
	for(var i = 0; i < elasticsearch.length; i++){
		var node_name;
		var node_ip;
		var node_status;
		var node_server;
		elasticsearch_obj = elasticsearch[i];
		node_name = elasticsearch_obj["name"];
		node_ip = elasticsearch_obj["host"];
		node_status = elasticsearch_obj["status"];
		node_server = elasticsearch_obj["server"];
		temp = "<tr>";
		temp = temp + "<td>" + node_name + "</td>";
		temp = temp + "<td>" + node_ip + "</td>";
		if(node_status.indexOf("true") == 0){
			temp = temp + "<td style='color: green;'>正常</td>";
		}else{
			temp = temp + "<td style='color: red;'>终止</td>";
		}
		temp = temp + "<td>" + node_server + "</td>";
		temp = temp + "</tr>";
		str += temp;
	}
	console.log("开始循环spark");
	for(var i = 0; i < spark.length; i++){
		var node_name;
		var node_ip;
		var node_status;
		var node_server;
		spark_obj = spark[i];
		node_name = spark_obj["name"];
		node_ip = spark_obj["host"];
		node_status = spark_obj["status"];
		node_server = spark_obj["server"];
		temp = "<tr>";
		temp = temp + "<td>" + node_name + "</td>";
		temp = temp + "<td>" + node_ip + "</td>";
		if(node_status.indexOf("true") == 0){
			temp = temp + "<td style='color: green;'>正常</td>";
		}else{
			temp = temp + "<td style='color: red;'>终止</td>";
		}
		temp = temp + "<td>" + node_server + "</td>";
		temp = temp + "</tr>";
		str += temp;
	}
	console.log("开始循环http");
	for(var i = 0; i < http.length; i++){
		var node_name;
		var node_ip;
		var node_status;
		var node_server;
		http_obj = http[i];
		node_name = http_obj["name"];
		node_ip = http_obj["host"];
		node_status = http_obj["status"];
		node_server = http_obj["server"];
		temp = "<tr>";
		temp = temp + "<td>" + node_name + "</td>";
		temp = temp + "<td>" + node_ip + "</td>";
		if(node_status.indexOf("true") == 0){
			temp = temp + "<td style='color: green;'>正常</td>";
		}else{
			temp = temp + "<td style='color: red;'>终止</td>";
		}
		temp = temp + "<td>" + node_server + "</td>";
		temp = temp + "</tr>";
		str += temp;
	}
	console.log("开始循环其他");
	for(var i = 0; i < web.length; i++){
		var node_name;
		var node_ip;
		var node_status;
		var node_server;
		web_obj = web[i];
		node_name = web_obj["name"];
		node_ip = web_obj["host"];
		node_status = web_obj["status"];
		node_server = web_obj["server"];
		temp = "<tr>";
		temp = temp + "<td>" + node_name + "</td>";
		temp = temp + "<td>" + node_ip + "</td>";
		if(node_status.indexOf("true") == 0){
			temp = temp + "<td style='color: green'>正常</td>";
		}else{
			temp = temp + "<td style='color: red'>终止</td>";
		}
		temp = temp + "<td>" + node_server + "</td>";
		temp = temp + "</tr>";
		str += temp;
	}
	for(var i = 0; i < sms.length; i++){
		var node_name;
		var node_ip;
		var node_status;
		var node_server;
		sms_obj = sms[i];
		node_name = sms_obj["name"];
		node_ip = sms_obj["host"];
		node_status = sms_obj["status"];
		node_server = sms_obj["server"];
		temp = "<tr>";
		temp = temp + "<td>" + node_name + "</td>";
		temp = temp + "<td>" + node_ip + "</td>";
		if(node_status.indexOf("true") == 0){
			temp = temp + "<td style='color: green'>正常</td>";
		}else{
			temp = temp + "<td style='color: red'>终止</td>";
		}
		temp = temp + "<td>" + node_server + "</td>";
		temp = temp + "</tr>";
		str += temp;
	}
	for(var i = 0; i < wechat.length; i++){
		var node_name;
		var node_ip;
		var node_status;
		var node_server;
		wechat_obj = wechat[i];
		node_name = wechat_obj["name"];
		node_ip = wechat_obj["host"];
		node_status = wechat_obj["status"];
		node_server = wechat_obj["server"];
		temp = "<tr>";
		temp = temp + "<td>" + node_name + "</td>";
		temp = temp + "<td>" + node_ip + "</td>";
		if(node_status.indexOf("true") == 0){
			temp = temp + "<td style='color: green;'>正常</td>";
		}else{
			temp = temp + "<td style='color: red;'>终止</td>";
		}
		temp = temp + "<td>" + node_server + "</td>";
		temp = temp + "</tr>";
		str += temp;
	}
	for(var i = 0; i < probe.length; i++){
		var node_name;
		var node_ip;
		var node_status;
		var node_server;
		probe_obj = probe[i];
		var my_probe = probe_obj[my_id];
		for(var j = 0; j < my_probe.length; j++){
			var my_probe_obj = my_probe[j];
			node_name = my_probe_obj["name"];
			node_ip = my_probe_obj["host"];
			node_status = my_probe_obj["status"];
			node_server = my_probe_obj["server"];
			temp = "<tr>";
			temp = temp + "<td>" + node_name + "</td>";
			temp = temp + "<td>" + node_ip + "</td>";
			if(node_status.indexOf("true") == 0){
				temp = temp + "<td style='color: green;'>正常</td>";
			}else{
				temp = temp + "<td style='color: red;'>终止</td>";
			}
			temp = temp + "<td>" + node_server + "</td>";
			temp = temp + "</tr>";
			str += temp;
		}
	}
	document.getElementById("check_system_status").innerHTML = str;
}
