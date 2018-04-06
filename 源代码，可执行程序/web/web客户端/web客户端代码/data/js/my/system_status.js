var web;
var http;
var elasticsearch;
var spark;
var wechat;
var probe;
var sms;
$(document).ready(function(){
	request_status();
	setInterval("request_status()",1000);
});

function request_status(){
	var my_url = project_name + "/get_system_status";
	$.ajax({
		type: "GET",
		url: my_url,
		success: status_callback,
		error: function(data){
			alert("连接服务器失败！");
		}
	});
}

function status_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
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
			elasticsearch_status();
			spark_status();
			http_status();
			other_status();
		}
	}
}

function elasticsearch_status(){
	var str = "";
	var temp;
	var elasticsearch_obj;
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
			temp = temp + "<td><span class='label label-success'>正常</span></td>";
		}else{
			temp = temp + "<td><span class='label label-danger'>终止</span></td>";
		}
		temp = temp + "<td>" + node_server + "</td>";
		temp = temp + "</tr>";
		str += temp;
	}
	document.getElementById("elasticsearch_status").innerHTML = str;
}

function spark_status(){
	var str = "";
	var temp;
	var spark_obj;
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
			temp = temp + "<td><span class='label label-success'>正常</span></td>";
		}else{
			temp = temp + "<td><span class='label label-danger'>终止</span></td>";
		}
		temp = temp + "<td>" + node_server + "</td>";
		temp = temp + "</tr>";
		str += temp;
	}
	document.getElementById("spark_status").innerHTML = str;
}

function http_status(){
	var str = "";
	var temp;
	var http_obj;
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
			temp = temp + "<td><span class='label label-success'>正常</span></td>";
		}else{
			temp = temp + "<td><span class='label label-danger'>终止</span></td>";
		}
		temp = temp + "<td>" + node_server + "</td>";
		temp = temp + "</tr>";
		str += temp;
	}
	document.getElementById("http_status").innerHTML = str;
}

function other_status(){
	var str = "";
	var temp;
	var web_obj;
	var sms_obj;
	var wechat_obj;
	var probe_obj;
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
			temp = temp + "<td><span class='label label-success'>正常</span></td>";
		}else{
			temp = temp + "<td><span class='label label-danger'>终止</span></td>";
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
			temp = temp + "<td><span class='label label-success'>正常</span></td>";
		}else{
			temp = temp + "<td><span class='label label-danger'>终止</span></td>";
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
			temp = temp + "<td><span class='label label-success'>正常</span></td>";
		}else{
			temp = temp + "<td><span class='label label-danger'>终止</span></td>";
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
				temp = temp + "<td><span class='label label-success'>正常</span></td>";
			}else{
				temp = temp + "<td><span class='label label-danger'>终止</span></td>";
			}
			temp = temp + "<td>" + node_server + "</td>";
			temp = temp + "</tr>";
			str += temp;
		}
	}
	document.getElementById("other_status").innerHTML = str;
}