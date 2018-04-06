var the_traffic_hour_data;//客流量按小时计
var the_store_amount_hour_data;//入店量按小时计
var traffic_and_the_amount_of_store_hour_time;//客流量/入店量按小时记时间轴
var the_into_the_store_rate_hour_data;//入店率按小时计

var new_and_old_customers_hour_time;//新老顾客按小时计时间轴
var the_new_customers_hour_data;//新顾客按小时计
var the_old_customers_hour_data;//老顾客按小时计

var the_bounce_rate_hour_data;//跳出率按小时计
var the_deep_rate_hour_data;//深访率按小时计
var bounce_rate_and_deep_rate_hour_time;//跳出率/深访率按小时计时间轴
$(document).ready(function(){
	request_traffic_and_the_amount_of_store_hour();
	//chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour();
	//chart_last_into_the_shore_rate_hour();
	request_the_new_and_old_customers_hour();
	//chart_the_new_and_old_customers_hour();
	request_deep_rate_and_bounce_rate_hour();
	//chart_deep_rate_and_bounce_rate_hour();
});

function request_traffic_and_the_amount_of_store_hour()
{
	var my_url = project_name + "/get_traffic_amount_and_the_amount_of_store_hour";
	$.ajax({
		type: "GET",
		url: my_url,
		success: traffic_and_the_amount_of_store_hour_callback
	});
}
function traffic_and_the_amount_of_store_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_traffic_hour_data = obj["traffic"];
		the_store_amount_hour_data = obj["store_amount"];
		traffic_and_the_amount_of_store_hour_time = obj["time"];
		var start_time = obj["show_start_time"];
		var end_time = obj["show_end_time"];
		document.getElementById("start_date").value = start_time;
		document.getElementById("end_date").value = end_time; 
		request_into_the_store_rate_hour();
	}
}

function request_into_the_store_rate_hour()
{
	var my_url = project_name + "/get_into_the_store_rate_hour";
	$.ajax({
		type: "GET",
		url: my_url,
		success: into_the_store_rate_hour_callback
	});
}
function into_the_store_rate_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_into_the_store_rate_hour_data = obj["into_the_store_rate"];
		chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour();
		chart_last_into_the_shore_rate_hour();
	}
}

function chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour(){
	var traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour = echarts.init(document.getElementById("traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour"));
	var colors = ['#5793f3', '#d14a61', '#675bba'];
	var option = {
		color: colors,

		tooltip: {
			trigger: 'axis',
			axisPointer: {type: 'cross'}
		},
		grid: {
			right: '20%'
		},
		toolbox: {
			feature: {
				dataView: {show: true, readOnly: false},
				restore: {show: true},
				saveAsImage: {show: true}
			}
		},
		legend: {
			data:['客流量','入店量','入店率']
		},
		xAxis: [
			{
				type: 'category',
				axisTick: {
					alignWithLabel: true
				},
				//data: ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
				data: traffic_and_the_amount_of_store_hour_time
			}
		],
		yAxis: [
			{
				type: 'value',
				name: '客流量/入店量',
				position: 'left',
				axisLine: {
					lineStyle: {
						color: colors[0]
					}
				},
				axisLabel: {
					formatter: '{value} 人'
				}
			},
			{
				type: 'value',
				name: '入店率',
				position: 'right',
				offset: 80,
				axisLine: {
					lineStyle: {
						color: colors[1]
					}
				},
				axisLabel: {
					formatter: '{value}'
				}
			}
		],
		series: [
			{
				name:'客流量',
				type:'bar',
				//data:[2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3]
				data: the_traffic_hour_data
			},
			{
				name:'入店量',
				type:'bar',
				//data:[2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3]
				data: the_store_amount_hour_data
			},
			{
				name:'入店率',
				type:'line',
				yAxisIndex: 1,
				//data:[2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2]
				data: the_into_the_store_rate_hour_data
			}
		]
	};
	traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour.setOption(option);
	window.addEventListener("resize",function(){
		traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour.resize();
	});
}

function chart_last_into_the_shore_rate_hour(){
	var last_into_the_shore_rate_hour = echarts.init(document.getElementById("last_into_the_shore_rate_hour"));
	var temp = the_into_the_store_rate_hour_data.length
	var last_into_the_store_rate = parseFloat(the_into_the_store_rate_hour_data[temp - 1].toFixed(4));
	var option = {
		tooltip: {
			trigger: 'item',
			formatter: "{a} <br/>{b} : {c} ({d}%)"
		},
		series: [{
			name: '入店率',
			type: 'pie',
			radius: ['45%', '70%'],
			label: {
				normal: {
					position: 'center'
				}
			},
			data: [{
				value: last_into_the_store_rate,
				name: '入店率',
				label: {
					normal: {
						formatter: '{d}%',
						textStyle: {
							fontSize: 16,
							fontWeight: 'bold'
						}
					}
				},
				itemStyle: {
					normal: {
						color: '#007be8'
					},
					emphasis: {
						color: '#007be8'
					}
				}
			}, {
				value: 1 - last_into_the_store_rate,
				name: '其他',
				label: {
					normal: {
						formatter: '\n入店率',
						textStyle: {
							color: '#555',
							fontSize: 12
						}
					}
				},
				tooltip: {
					show: false
				},
				itemStyle: {
					normal: {
						color: '#aaa'
					},
					emphasis: {
						color: '#aaa'
					}
				},
				hoverAnimation: false
			}]
		}]
	};
	last_into_the_shore_rate_hour.setOption(option);
	window.addEventListener("resize",function(){
		last_into_the_shore_rate_hour.resize();
	});
}

function request_the_new_and_old_customers_hour(){
	var my_url = project_name + "/get_the_new_and_old_customers_hour";
	$.ajax({
		type: "GET",
		url: my_url,
		success: the_new_and_old_customers_hour_callback
	});
}

function the_new_and_old_customers_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_new_customers_hour_data = obj['new_customers'];
		the_old_customers_hour_data = obj['old_customers'];
		new_and_old_customers_hour_time = obj['time'];
		chart_the_new_and_old_customers_hour();
	}
}

function chart_the_new_and_old_customers_hour(){
	the_new_and_old_customers_hour = echarts.init(document.getElementById("the_new_and_old_customers_hour"));
	var option = {
		tooltip : {
			trigger: 'axis'
		},
		legend: {
			data:['新顾客','老顾客']
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
		calculable : true,
		xAxis : [
			{
				type : 'category',
				//data : ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月']
				data: new_and_old_customers_hour_time
			}
		],
		yAxis : [
			{
				type : 'value'
			}
		],
		series : [
			{
				name:'新顾客',
				type:'bar',
				//data:[2.0, 4.9, 7.0, 23.2, 25.6, 76.7, 135.6, 162.2, 32.6, 20.0, 6.4, 3.3],
				data: the_new_customers_hour_data,
				markLine : {
					data : [
						{type : 'average', name: '平均值'}
					]
				}
			},
			{
				name:'老顾客',
				type:'bar',
				//data:[2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3],
				data: the_old_customers_hour_data,
				markLine : {
					data : [
						{type : 'average', name : '平均值'}
					]
				}
			}
		]
	};
	the_new_and_old_customers_hour.setOption(option);
	window.addEventListener("resize",function(){
		the_new_and_old_customers_hour.resize();
	});
}

function request_deep_rate_and_bounce_rate_hour(){
	var my_url = project_name + "/get_deep_rate_and_bounce_rate_hour";
	$.ajax({
		type: "GET",
		url: my_url,
		success: deep_rate_and_bounce_rate_hour_callback
	});
}

function deep_rate_and_bounce_rate_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_bounce_rate_hour_data = obj['bounce_rate'];
		the_deep_rate_hour_data = obj['deep_rate'];
		bounce_rate_and_deep_rate_hour_time = obj['time'];
		chart_deep_rate_and_bounce_rate_hour();
	}
}

function chart_deep_rate_and_bounce_rate_hour(){
	deep_rate_and_bounce_rate_hour = echarts.init(document.getElementById("deep_rate_and_bounce_rate_hour"));
	var option = {
		tooltip: {
            trigger: 'axis'
        },
		toolbox: {
			feature: {
				dataView: {show: true, readOnly: false},
				restore: {show: true},
				saveAsImage: {show: true}
			}
		},
		legend: {
			data:['跳出率','深访率']
		},
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                //data: ['1', '5', '10', '15', '20', '25', '31']
				data: bounce_rate_and_deep_rate_hour_time
            }
        ],
        yAxis: [
            {
                name: '比率',
                type: 'value'
            }
        ],
        series: [
            {
                name: '跳出率',
                type: 'line',
                tooltip: {
                    trigger: 'axis'
                },
                smooth: true,
                itemStyle: {
                    normal: {
                        color: 'rgba(2, 197, 233, 0.2)',
                        lineStyle: {
                            color: 'rgba(23, 107, 203, 0.2)'
                        },
                        areaStyle: {
                            color: 'rgba(223, 147, 233, 0.2)'
                        }
                    }
                },
                //data: [10, 12, 21, 54, 60, 80, 71]
				data: the_bounce_rate_hour_data
            },
            {
                name: '深访率',
                type: 'line',
                tooltip: {
                    trigger: 'axis'
                },
                smooth: true,
                itemStyle: {
                    normal: {
                        color: 'rgba(2, 197, 233, 0.2)',
                        lineStyle: {
                            color: 'rgba(2, 197, 233, 0.2)'
                        },
                        areaStyle: {
                            color: 'rgba(2, 197, 233, 0.2)'
                        }
                    }
                },
                //data: [30, 32, 61, 24, 20, 90, 20]
				data: the_deep_rate_hour_data
            }
        ]
	};
	deep_rate_and_bounce_rate_hour.setOption(option);
	window.addEventListener("resize",function(){
		deep_rate_and_bounce_rate_hour.resize();
	});
}

function select_data()
{
    var start_date = document.getElementById("start_date").value;
	var end_date = document.getElementById("end_date").value;
	var start_strs = new Array();
	var end_strs = new Array();
	var start_day_and_hour = new Array();
	var end_day_and_hour = new Array();
	start_strs = start_date.split("-");
	end_strs = end_date.split("-");
	var start_year = parseInt(start_strs[0]);
	var start_month = parseInt(start_strs[1]);
	var start_day_and_hour = start_strs[2].split(" ");
	var start_day = parseInt(start_day_and_hour[0]);
	var start_hour = parseInt(start_day_and_hour[1]);
	var end_year = parseInt(end_strs[0]);
	var end_month = parseInt(end_strs[1]);
	end_day_and_hour = end_strs[2].split(" ");
	var end_day = parseInt(end_day_and_hour[0]);
	var end_hour = parseInt(end_day_and_hour[1]);
	if(start_year > end_year)
	{
	    alert("抱歉，开始时间不能大于结束时间！");
	}
	else
	{
	    if(start_year == end_year)
		{
		    if(start_month > end_month)
			{
			    alert("抱歉，开始时间不能大于结束时间！");
			}
			else
			{
			    if(start_month == end_month)
				{
				    if(start_day > end_day)
					{
					    alert("抱歉，开始时间不能大于结束时间！");
					}
					else
					{
					    if(start_day == end_day)
						{
						    if(start_hour > end_hour)
							{
							    alert("抱歉，开始时间不能大于结束时间！");
							}
							else
							{
								request_data(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour);
							}
						}
						else
						{
							request_data(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour);
						}
					}
				}
				else
				{
					request_data(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour);
				}
			}
		}
		else
		{
		    request_data(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour);
		}
	}
}

function request_data(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour){
	custom_request_traffic_and_the_amount_of_store_hour(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour);
	custom_request_the_new_and_old_customers_hour(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour);
	custom_request_deep_rate_and_bounce_rate_hour(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour)
}

function custom_request_traffic_and_the_amount_of_store_hour(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour){
	var my_url = project_name + "/get_custom_traffic_and_the_amount_of_store_hour";
    var request_time = new Object();
    request_time.start_year = start_year;
    request_time.start_month = start_month;
    request_time.start_day = start_day;
    request_time.start_hour = start_hour;
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
    request_time.end_hour = end_hour;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_traffic_and_the_amount_of_store_hour_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_traffic_and_the_amount_of_store_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_traffic_hour_data = obj["traffic"];
		the_store_amount_hour_data = obj["store_amount"];
		traffic_and_the_amount_of_store_hour_time = obj["time"];
		var start_time = obj["show_start_time"];
		var end_time = obj["show_end_time"];
		document.getElementById("start_date").value = start_time;
		document.getElementById("end_date").value = end_time; 
		var start_strs = new Array();
		var end_strs = new Array();
		var start_day_and_hour = new Array();
		var end_day_and_hour = new Array();
		start_strs = start_time.split("-");
		end_strs = end_time.split("-");
		var start_year = parseInt(start_strs[0]);
		var start_month = parseInt(start_strs[1]);
		var start_day_and_hour = start_strs[2].split(" ");
		var start_day = parseInt(start_day_and_hour[0]);
		var start_hour = parseInt(start_day_and_hour[1]);
		var end_year = parseInt(end_strs[0]);
		var end_month = parseInt(end_strs[1]);
		end_day_and_hour = end_strs[2].split(" ");
		var end_day = parseInt(end_day_and_hour[0]);
		var end_hour = parseInt(end_day_and_hour[1]);
		custom_request_into_the_store_rate_hour(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour);
	}
}

function custom_request_into_the_store_rate_hour(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour){
	var my_url = project_name + "/get_custom_into_the_store_rate_hour";
    var request_time = new Object();
    request_time.start_year = start_year;
    request_time.start_month = start_month;
    request_time.start_day = start_day;
    request_time.start_hour = start_hour;
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
    request_time.end_hour = end_hour;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_into_the_store_rate_hour_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_into_the_store_rate_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_into_the_store_rate_hour_data = obj["into_the_store_rate"];
		chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour();
		chart_last_into_the_shore_rate_hour();
	}
}

function custom_request_the_new_and_old_customers_hour(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour){
	var my_url = project_name + "/get_custom_the_new_and_old_customers_hour";
    var request_time = new Object();
    request_time.start_year = start_year;
    request_time.start_month = start_month;
    request_time.start_day = start_day;
    request_time.start_hour = start_hour;
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
    request_time.end_hour = end_hour;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_the_new_and_old_customers_hour_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_the_new_and_old_customers_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_new_customers_hour_data = obj['new_customers'];
		the_old_customers_hour_data = obj['old_customers'];
		new_and_old_customers_hour_time = obj['time'];
		chart_the_new_and_old_customers_hour();
	}
}

function custom_request_deep_rate_and_bounce_rate_hour(start_year, start_month, start_day, start_hour, end_year, end_month, end_day, end_hour){
	var my_url = project_name + "/get_custom_deep_rate_and_bounce_rate_hour";
    var request_time = new Object();
    request_time.start_year = start_year;
    request_time.start_month = start_month;
    request_time.start_day = start_day;
    request_time.start_hour = start_hour;
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
    request_time.end_hour = end_hour;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_deep_rate_and_bounce_rate_hour_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_deep_rate_and_bounce_rate_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_bounce_rate_hour_data = obj['bounce_rate'];
		the_deep_rate_hour_data = obj['deep_rate'];
		bounce_rate_and_deep_rate_hour_time = obj['time'];
		chart_deep_rate_and_bounce_rate_hour();
	}
}