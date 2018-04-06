$(document).ready(function(){
	request_add_user_error();
});

function request_add_user_error(){
	var my_url = project_name + "/get_add_user_error";
	$.ajax({
		type: "GET",
		url: my_url,
		success: add_user_error_callback,
		error: function(data){
			alert("请求服务器出错！");
			window.location.href = project_name + "/data/add_user.html";
		}
	});
}

function add_user_error_callback(data){
	var index = data.indexOf("ok");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		document.getElementById("add_user_error").innerHTML = data;
	}
}