var last_day_traffic_amount_number;
var last_day_traffic_amount_status;
var last_day_traffic_amount_compare;
var last_day_the_amount_of_store_number;
var last_day_the_amount_of_store_status;
var last_day_the_amount_of_store_compare;
var average_the_resident_time_number;
var average_the_resident_time_status;
var average_the_resident_time_compare;
var last_day_new_customers_number;
var last_day_new_customers_status;
var last_day_new_customers_compare;
var last_day_old_customers_number;
var last_day_old_customers_status;
var last_day_old_customers_compare;
var last_day_deep_number;
var last_day_deep_status;
var last_day_deep_compare;
mui.ready(function(){
	request_recent_data();
});
function request_recent_data(){
	/*
	mui.plusReady(function(){
		mui.ajax(my_ip + "/get_recent_data", {
			type: 'get',
			timeout: 8000,
			success: recent_data_callback,
			error: function(xhr, type, errorThrown){
				mui.toast("网络错误！");
			}
		});
	});*/
	mui.ajax(my_ip + "/get_recent_data", {
		type: 'get',
		timeout: 8000,
		success: recent_data_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
	
}

function recent_data_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		var last_day_traffic_amount = obj["last_day_traffic_amount"];
		last_day_traffic_amount_number = last_day_traffic_amount["number"];
		last_day_traffic_amount_status = last_day_traffic_amount["status"];
		last_day_traffic_amount_compare = last_day_traffic_amount["compare"];
		var last_day_the_amount_of_store = obj["last_day_the_amount_of_store"];
		last_day_the_amount_of_store_number = last_day_the_amount_of_store["number"];
		last_day_the_amount_of_store_status = last_day_the_amount_of_store["status"];
		last_day_the_amount_of_store_compare = last_day_the_amount_of_store["compare"];
		var average_the_resident_time = obj["average_the_resident_time"];
		average_the_resident_time_number = average_the_resident_time["number"];
		average_the_resident_time_status = average_the_resident_time["status"];
		average_the_resident_time_compare = average_the_resident_time["compare"];
		var last_day_new_customers = obj["last_day_new_customers"];
		last_day_new_customers_number = last_day_new_customers["number"];
		last_day_new_customers_status = last_day_new_customers["status"];
		last_day_new_customers_compare = last_day_new_customers["compare"];
		var last_day_old_customers = obj["last_day_old_customers"];
		last_day_old_customers_number = last_day_old_customers["number"];
		last_day_old_customers_status = last_day_old_customers["status"];
		last_day_old_customers_compare = last_day_old_customers["compare"];
		var last_day_deep = obj["last_day_deep"];
		last_day_deep_number = last_day_deep["number"];
		last_day_deep_status = last_day_deep["status"];
		last_day_deep_compare = last_day_deep["compare"];
		
		document.getElementById("last_day_traffic_amount_number").innerHTML = last_day_traffic_amount_number;
		if(last_day_traffic_amount_status.indexOf("up") == 0){
			document.getElementById("last_day_traffic_amount_status").innerHTML = "上升";
			document.getElementById("last_day_traffic_amount").style.color = "green";
		}else{
			document.getElementById("last_day_traffic_amount_status").innerHTML = "下降";
			document.getElementById("last_day_traffic_amount").style.color = "red";
		}
		document.getElementById("last_day_traffic_amount_compare").innerHTML = last_day_traffic_amount_compare;
		
		document.getElementById("last_day_the_amount_of_store_number").innerHTML = last_day_the_amount_of_store_number;
		if(last_day_the_amount_of_store_status.indexOf("up") == 0){
			document.getElementById("last_day_the_amount_of_store_status").innerHTML = "上升";
			document.getElementById("last_day_the_amount_of_store").style.color = "green";
		}else{
			document.getElementById("last_day_the_amount_of_store_status").innerHTML = "下降";
			document.getElementById("last_day_the_amount_of_store").style.color = "red";
		}
		document.getElementById("last_day_the_amount_of_store_compare").innerHTML = last_day_the_amount_of_store_compare;
		
		document.getElementById("average_the_resident_time_number").innerHTML = average_the_resident_time_number;
		if(average_the_resident_time_status.indexOf("up") == 0){
			document.getElementById("average_the_resident_time_status").innerHTML = "上升";
			document.getElementById("average_the_resident_time").style.color = "green";
		}else{
			document.getElementById("average_the_resident_time_status").innerHTML = "下降";
			document.getElementById("average_the_resident_time").style.color = "red";
		}
		document.getElementById("average_the_resident_time_compare").innerHTML = average_the_resident_time_compare;
		
		document.getElementById("last_day_new_customers_number").innerHTML = last_day_new_customers_number;
		if(last_day_new_customers_status.indexOf("up") == 0){
			document.getElementById("last_day_new_customers_status").innerHTML = "上升";
			document.getElementById("last_day_new_customers").style.color = "green";
		}else{
			document.getElementById("last_day_new_customers_status").innerHTML = "下降";
			document.getElementById("last_day_new_customers").style.color = "red";
		}
		document.getElementById("last_day_new_customers_compare").innerHTML = last_day_new_customers_compare;
		
		document.getElementById("last_day_old_customers_number").innerHTML = last_day_old_customers_number;
		if(last_day_old_customers_status.indexOf("up") == 0){
			document.getElementById("last_day_old_customers_status").innerHTML = "上升";
			document.getElementById("last_day_old_customers").style.color = "green";
		}else{
			document.getElementById("last_day_old_customers_status").innerHTML = "下降";
			document.getElementById("last_day_old_customers").style.color = "red";
		}
		document.getElementById("last_day_old_customers_compare").innerHTML = last_day_old_customers_compare;
		
		document.getElementById("last_day_deep_number").innerHTML = last_day_deep_number;
		if(last_day_deep_status.indexOf("up") == 0){
			document.getElementById("last_day_deep_status").innerHTML = "上升";
			document.getElementById("last_day_deep").style.color = "green";
		}else{
			document.getElementById("last_day_deep_status").innerHTML = "下降";
			document.getElementById("last_day_deep").style.color = "red";
		}
		document.getElementById("last_day_deep_compare").innerHTML = last_day_deep_compare;
	}
}
