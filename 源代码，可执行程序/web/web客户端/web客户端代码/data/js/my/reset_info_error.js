$(document).ready(function(){
	request_reset_info_error();
});

function request_reset_info_error(){
	var my_url = project_name + "/get_reset_info_error";
	$.ajax({
		type: "GET",
		url: my_url,
		success: reset_info_error_callback,
		error: function(data){
			alert("请求服务器出错！");
			window.location.href = project_name + "/data/reset_info.html";
		}
	});
}

function reset_info_error_callback(data){
	var index = data.indexOf("ok");
	if(index == 0){
		window.location.href = project_name + "/data/index.html";
	}else{
		document.getElementById("reset_info_error").innerHTML = data;
	}
}