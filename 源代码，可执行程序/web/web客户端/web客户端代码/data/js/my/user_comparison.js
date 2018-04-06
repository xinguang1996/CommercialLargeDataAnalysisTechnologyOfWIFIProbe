var user_list = new Array();
var user_the_amount_of_store_time;
var user_the_amount_of_store_data;
var user_into_the_store_rate_data;
var user_into_the_store_rate_time;

var all_id;
var all_name;
var all_traffic_amount;
var all_the_amount_of_store;
var all_into_the_store_rate;
var all_deep_rate;
var all_bounce_rate;

var my_the_amount_of_store;
var my_time;

var my_into_the_store_rate;
var my_into_the_store_rate_time;

var all_id1;
var all_id2;
$(document).ready(function(){
	request_all_user_data();
	request_user_the_amount_of_store();
	request_user_into_the_store_rate();
});

function request_all_user_data(){
	var my_url = project_name + "/get_all_user_data";
	$.ajax({
		type: "GET",
		url: my_url,
		success: all_user_data_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function all_user_data_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		all_id = obj["all_id"];
		all_name = obj["all_name"];
		all_traffic_amount = obj["all_traffic_amount"];
		all_the_amount_of_store = obj["all_the_amount_of_store"];
		all_into_the_store_rate = obj["all_into_the_store_rate"];
		all_deep_rate = obj["all_deep_rate"];
		all_bounce_rate = obj["all_bounce_rate"];
		show_all_user_data();
	}
}

function show_all_user_data(){
	var str = "<div class='table-responsive'><table id='datatable' class='table table-striped table-bordered'><thead><tr><th>账号</th><th>负责人姓名</th><th>近7日客流量</th><th>近7日入店量</th><th>近7日入店率</th><th>近7日深仿率</th><th>近7日跳出率</th></tr></thead>";
	str += "<tbody>";
	var temp = "";
	for(var i = 0; i < all_id.length; i++){
		temp = "<tr>";
		temp = temp + "<td>" + all_id[i] + "</td>";
		temp = temp + "<td>" + all_name[i] + "</td>";
		temp = temp + "<td>" + all_traffic_amount[i] + "</td>";
		temp = temp + "<td>" + all_the_amount_of_store[i] + "</td>";
		temp = temp + "<td>" + all_into_the_store_rate[i] + "</td>";
		temp = temp + "<td>" + all_deep_rate[i] + "</td>";
		temp = temp + "<td>" + all_bounce_rate[i] + "</td>";
		temp = temp + "</tr>";
		str += temp;
	}
	str += "</tbody></table></div>"
	document.getElementById("all_user_data").innerHTML = str;
}
/*
function get_user(){
	var user = $("input[name='table_records']:checked").serialize();
	if(user == ""){
		alert("没有选择任何用户!");
	}else{
		var temp = user.split("&");
		for(var i = 0; i < temp.length; i++){
			user_list[i] = temp[i].split("=")[1];
		}
		request_user_the_amount_of_store();
		request_user_into_the_store_rate();
	}
}
*/
function request_user_the_amount_of_store(){
	var my_url = project_name + "/get_user_the_amount_of_store";
    //var obj = new Object();
	//obj.user_list = user_list;
	$.ajax({
		type: "GET",
		url: my_url,
		//data: obj,
		//traditional = true,
		success: user_the_amount_of_store_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function user_the_amount_of_store_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		user_the_amount_of_store_data = obj['user_the_amount_of_store_data'];
		user_the_amount_of_store_time = obj['time'];
		all_id1 = obj["all_id"];
		chart_user_the_amount_of_store();
	}
}

function chart_user_the_amount_of_store(){
	var user_the_amount_of_store = echarts.init(document.getElementById("user_the_amount_of_store"));
	var user_the_amount_of_store_data_series = new Array();
	for(var i = 0; i < user_the_amount_of_store_data.length; i++){
		var temp_obj = new Object();
		var temp = user_the_amount_of_store_data[i];
		temp_obj.name = all_id1[i];
		temp_obj.type = "line";
		temp_obj.data = temp;
		user_the_amount_of_store_data_series[i] = temp_obj;
	}
	var option = {
		tooltip: {
			trigger: 'axis'
		},
		legend: {
			//data:['驻店时长']
			data: all_id1
		},
		grid: [{
	        left: 75,
	        right: 50
	    }],
		toolbox: {
			show : true,
			feature : {
				dataView : {show: true, readOnly: false},
				magicType : {show: true, type: ['line', 'bar']},
				restore : {show: true},
				saveAsImage : {show: true}
			}
		},
		xAxis:  {
			type: 'category',
			boundaryGap: false,
			//data: ['6/1','6/4','6/7','6/10','6/13','6/16','6/19','6/21','6/24','6/27','6/30','7/2','7/5','7/8','7/11']
			data: user_the_amount_of_store_time
		},
		yAxis: {
			type: 'value',
			axisLabel: {
				formatter: '{value} 人'
			}
		},
			/*
		series: [
			{
				name:'入店量',
				type:'line',
				//data:[92, 96, 99, 91, 96, 103, 100,92, 96, 99, 91, 96, 103, 100,115]
				data: the_resident_time_day_number
			}
		]*/
		series: user_the_amount_of_store_data_series
	};
	user_the_amount_of_store.setOption(option);
	window.addEventListener("resize",function(){
		user_the_amount_of_store.resize();
	});
}

function request_user_into_the_store_rate(){
	var my_url = project_name + "/get_user_into_the_store_rate";
    //var obj = new Object();
	//obj.user_list = user_list;
	$.ajax({
		type: "GET",
		url: my_url,
		//data: obj,
		//traditional = true,
		success: user_into_the_store_rate_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function user_into_the_store_rate_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		user_into_the_store_rate_data = obj['user_into_the_store_rate_data'];
		user_into_the_store_rate_time = obj['time'];
		all_id2 = obj["all_id"];
		chart_user_into_the_store_rate();
	}
}

function chart_user_into_the_store_rate(){
	var user_into_the_store_rate = echarts.init(document.getElementById("user_into_the_store_rate"));
	var user_into_the_store_rate_data_series = new Array();
	for(var i = 0; i < user_into_the_store_rate_data.length; i++){
		var temp_obj = new Object();
		var temp = user_into_the_store_rate_data[i];
		temp_obj.name = all_id2[i];
		temp_obj.type = "line";
		temp_obj.data = temp;
		user_into_the_store_rate_data_series[i] = temp_obj;
	}
	var option = {
		tooltip: {
			trigger: 'axis'
		},
		legend: {
			//data:['驻店时长']
			data: all_id2
		},
		toolbox: {
			show : true,
			feature : {
				dataView : {show: true, readOnly: false},
				magicType : {show: true, type: ['line', 'bar']},
				restore : {show: true},
				saveAsImage : {show: true}
			}
		},
		xAxis:  {
			type: 'category',
			boundaryGap: false,
			//data: ['6/1','6/4','6/7','6/10','6/13','6/16','6/19','6/21','6/24','6/27','6/30','7/2','7/5','7/8','7/11']
			data: user_into_the_store_rate_time
		},
		yAxis: {
			type: 'value',
			axisLabel: {
				formatter: '{value}'
			}
		},
			/*
		series: [
			{
				name:'入店量',
				type:'line',
				//data:[92, 96, 99, 91, 96, 103, 100,92, 96, 99, 91, 96, 103, 100,115]
				data: the_resident_time_day_number
			}
		]*/
		series: user_into_the_store_rate_data_series
	};
	user_into_the_store_rate.setOption(option);
	window.addEventListener("resize",function(){
		user_into_the_store_rate.resize();
	});
}