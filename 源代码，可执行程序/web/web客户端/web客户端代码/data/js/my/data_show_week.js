var the_traffic_week_data;//客流量按周计
var the_store_amount_week_data;//入店量按周计
var traffic_and_the_amount_of_store_week_time;//客流量/入店量按周计时间轴

var the_into_the_store_rate_week_data;//入店率按周计

var new_and_old_customers_week_time;//新老顾客按周计计时间轴
var the_new_customers_week_data;//新顾客按周计
var the_old_customers_week_data;//老顾客按周计

var the_resident_time_week_number;//驻店时长按周计人数
var the_resident_time_week_time;//驻店时长按周计时间

var the_bounce_rate_week_data;//跳出率按周计
var the_deep_rate_week_data;//深访率按周计
var bounce_rate_and_deep_rate_week_time;//跳出率/深访率按周计时间轴

var visiting_cycle_week_time;//来访周期时间按周计
var visiting_cycle_week_number;//来访周期人数按周计

var the_high_activity_week_data;//高活跃度按周计
var the_mid_activity_week_data;//中活跃度按周计
var the_low_activity_week_data;//低活跃度按周计
var the_sleep_activity_week_data;//沉睡活跃度按周计
$(document).ready(function(){
	request_traffic_amount_and_the_amount_of_store_week();
	//chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_week();
	//chart_last_into_the_shore_rate_week();
	request_the_new_and_old_customers_week();
	//chart_the_new_and_old_customers_week();
	request_the_resident_time_week();
	//chart_the_resident_time_week();
	request_deep_rate_and_bounce_rate_week();
	//chart_deep_rate_and_bounce_rate_week();
	//chart_last_deep_rate_and_bounce_rate_week();
	request_visiting_cycle_week();
	//chart_visiting_cycle_week();
	request_customer_active_week();
	//chart_customer_active_week();
});
function request_traffic_amount_and_the_amount_of_store_week(){
	var my_url = project_name + "/get_traffic_amount_and_the_amount_of_store_week";
	$.ajax({
		type: "GET",
		url: my_url,
		success: traffic_amount_and_the_amount_of_store_week_callback
	});
}

function traffic_amount_and_the_amount_of_store_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_traffic_week_data = obj['traffic'];
		the_store_amount_week_data = obj['store_amount'];
		traffic_and_the_amount_of_store_week_time = obj['time'];
		var start_time = obj["show_start_time"];
		var end_time = obj["show_end_time"];
		document.getElementById("start_date").value = start_time;
		document.getElementById("end_date").value = end_time; 
		request_into_the_store_rate_week();
	}
}

function request_into_the_store_rate_week(){
	var my_url = project_name + "/get_into_the_store_rate_week";
	$.ajax({
		type: "GET",
		url: my_url,
		success: into_the_store_rate_week_callback
	});
}

function into_the_store_rate_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_into_the_store_rate_week_data = obj['into_the_store_rate'];
		chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_week();
		chart_last_into_the_shore_rate_week();
	}
}

function chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_week(){
	var traffic_amount_and_the_amount_of_store_and_into_the_store_rate_week = echarts.init(document.getElementById("traffic_amount_and_the_amount_of_store_and_into_the_store_rate_week"));
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
				data: traffic_and_the_amount_of_store_week_time
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
				data: the_traffic_week_data
			},
			{
				name:'入店量',
				type:'bar',
				//data:[2.6, 5.9, 9.0, 26.4, 28.7, 70.7, 175.6, 182.2, 48.7, 18.8, 6.0, 2.3]
				data: the_store_amount_week_data
			},
			{
				name:'入店率',
				type:'line',
				yAxisIndex: 1,
				//data:[2.0, 2.2, 3.3, 4.5, 6.3, 10.2, 20.3, 23.4, 23.0, 16.5, 12.0, 6.2]
				data: the_into_the_store_rate_week_data
			}
		]
	};
	traffic_amount_and_the_amount_of_store_and_into_the_store_rate_week.setOption(option);
	window.addEventListener("resize",function(){
		traffic_amount_and_the_amount_of_store_and_into_the_store_rate_week.resize();
	});
}

function chart_last_into_the_shore_rate_week(){
	var last_into_the_shore_rate_week = echarts.init(document.getElementById("last_into_the_shore_rate_week"));
	var temp = the_into_the_store_rate_week_data.length;
	var last_into_the_store_rate = parseFloat(the_into_the_store_rate_week_data[temp - 1].toFixed(4));
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
				//value: 0.3456,
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
				//value: 1 - 0.3456,
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
	last_into_the_shore_rate_week.setOption(option);
	window.addEventListener("resize",function(){
		last_into_the_shore_rate_week.resize();
	});
}

function request_the_new_and_old_customers_week(){
	var my_url = project_name + "/get_the_new_and_old_customers_week";
	$.ajax({
		type: "GET",
		url: my_url,
		success: the_new_and_old_customers_week_callback
	});
}

function the_new_and_old_customers_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_new_customers_week_data = obj['new_customers'];
		the_old_customers_week_data = obj['old_customers'];
		new_and_old_customers_week_time = obj['time'];
		chart_the_new_and_old_customers_week();
	}
}

function chart_the_new_and_old_customers_week(){
	the_new_and_old_customers_week = echarts.init(document.getElementById("the_new_and_old_customers_week"));
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
				data: new_and_old_customers_week_time
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
				data: the_new_customers_week_data,
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
				data: the_old_customers_week_data,
				markLine : {
					data : [
						{type : 'average', name : '平均值'}
					]
				}
			}
		]
	};
	the_new_and_old_customers_week.setOption(option);
	window.addEventListener("resize",function(){
		the_new_and_old_customers_week.resize();
	});
}

function request_the_resident_time_week(){
	var my_url = project_name + "/get_the_resident_time_week";
	$.ajax({
		type: "GET",
		url: my_url,
		success: the_resident_time_week_callback
	});
}

function the_resident_time_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_resident_time_week_number = obj['the_resident_time_number'];
		the_resident_time_week_time = obj['the_resident_time_time']
		chart_the_resident_time_week();
	}
}

function chart_the_resident_time_week(){
	the_resident_time_week = echarts.init(document.getElementById("the_resident_time_week"));
	var option = {
		tooltip: {
			trigger: 'axis'
		},
		legend: {
			data:['驻店时长']
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
			data: the_resident_time_week_time
		},
		yAxis: {
			type: 'value',
			axisLabel: {
				formatter: '{value} 人'
			}
		},
		series: [
			{
				name:'驻店时长',
				type:'line',
				//data:[92, 96, 99, 91, 96, 103, 100,92, 96, 99, 91, 96, 103, 100,115]
				data: the_resident_time_week_number
			}
		]
	};
	the_resident_time_week.setOption(option);
	window.addEventListener("resize",function(){
		the_resident_time_week.resize();
	});
}

function request_deep_rate_and_bounce_rate_week(){
	var my_url = project_name + "/get_deep_rate_and_bounce_rate_week";
	$.ajax({
		type: "GET",
		url: my_url,
		success: deep_rate_and_bounce_rate_week_callback
	});
}

function deep_rate_and_bounce_rate_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_bounce_rate_week_data = obj['bounce_rate'];
		the_deep_rate_week_data = obj['deep_rate'];
		bounce_rate_and_deep_rate_week_time = obj['time'];
		chart_deep_rate_and_bounce_rate_week();
		chart_last_deep_rate_and_bounce_rate_week();
	}
}

function chart_deep_rate_and_bounce_rate_week(){
	deep_rate_and_bounce_rate_week = echarts.init(document.getElementById("deep_rate_and_bounce_rate_week"));
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
				data: bounce_rate_and_deep_rate_week_time
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
				data: the_bounce_rate_week_data
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
				data: the_deep_rate_week_data
            }
        ]
	};
	deep_rate_and_bounce_rate_week.setOption(option);
	window.addEventListener("resize",function(){
		deep_rate_and_bounce_rate_week.resize();
	});
}

function chart_last_deep_rate_and_bounce_rate_week(){
	var last_deep_rate_and_bounce_rate_week = echarts.init(document.getElementById("last_deep_rate_and_bounce_rate_week"));
	var temp = the_bounce_rate_week_data.length;
	var last_bounce_rate = parseFloat(the_bounce_rate_week_data[temp - 1].toFixed(4));
	var last_deep_rate = parseFloat(the_deep_rate_week_data[temp - 1].toFixed(4))
	var option = {
		tooltip: {
			trigger: 'item',
			formatter: "{a} <br/>{b}: {c} ({d}%)"
		},
		legend: {
			orient: 'vertical',
			x: 'left',
			data:['跳出率','深仿访率','其他']
		},
		series: [
			{
				name:'跳出率/深访率',
				type:'pie',
				radius: ['50%', '70%'],
				avoidLabelOverlap: false,
				label: {
					normal: {
						show: false,
						position: 'center'
					},
					emphasis: {
						show: true,
						textStyle: {
							fontSize: '30',
							fontWeight: 'bold'
						}
					}
				},
				labelLine: {
					normal: {
						show: false
					}
				},
				data:[
					{value:last_bounce_rate, name:'跳出率'},
					{value:last_deep_rate, name:'深访率'},
					{value:1 - last_bounce_rate - last_deep_rate, name:'其他'}
				]
				/*
				data:[
					{value:0.31, name:'跳出率'},
					{value:0.32, name:'深仿率'},
					{value:1 - 0.31 - 0.32, name:'其他'}
				]*/
			}
		]
	};
	last_deep_rate_and_bounce_rate_week.setOption(option);
	window.addEventListener("resize",function(){
		last_deep_rate_and_bounce_rate_week.resize();
	});
}

function request_visiting_cycle_week(){
	var my_url = project_name + "/get_visiting_cycle_week";
	$.ajax({
		type: "GET",
		url: my_url,
		success: visiting_cycle_week_callback
	});
}

function visiting_cycle_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		visiting_cycle_week_number = obj['visiting_cycle_number'];
		visiting_cycle_week_time = obj['visiting_cycle_time']
		chart_visiting_cycle_week();
	}
}

function chart_visiting_cycle_week(){
	var visiting_cycle_week = echarts.init(document.getElementById("visiting_cycle_week"));
	var option = {
		tooltip: {
			trigger: 'axis'
		},
		legend: {
			data:['来访周期']
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
			data: visiting_cycle_week_time
		},
		yAxis: {
			type: 'value',
			axisLabel: {
				formatter: '{value} 人'
			}
		},
		series: [
			{
				name:'来访周期',
				type:'line',
				//data:[92, 96, 99, 91, 96, 103, 100,92, 96, 99, 91, 96, 103, 100,115]
				data: visiting_cycle_week_number
			}
		]
	};
	visiting_cycle_week.setOption(option);
	window.addEventListener("resize",function(){
		visiting_cycle_week.resize();
	});
}

function request_customer_active_week(){
	var my_url = project_name + "/get_customer_active_week";
	$.ajax({
		type: "GET",
		url: my_url,
		success: customer_active_week_callback
	});
}

function customer_active_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_high_activity_week_data = obj["high_activity"];
        the_mid_activity_week_data = obj["mid_activity"];
        the_low_activity_week_data = obj["low_activity"];
        the_sleep_activity_week_data = obj["sleep_activity"];
		chart_customer_active_week();
	}
}

function chart_customer_active_week(){
	var customer_active_week = echarts.init(document.getElementById("customer_active_week"));
	var colors = ['#1790CF', '#1BB2D8', '#99D2DD', '#88B0BB', '#1C7099','#038CC4'];
	var option = {
	    color: colors,
	    tooltip : {
	        trigger: 'item',
	        formatter: "{a} <br/>{b} : {c} ({d}%)"
	    },
	    title: {
	        left: 'center'
	    },
		toolbox: {
			feature: {
				dataView: {show: true, readOnly: false},
				restore: {show: true},
				saveAsImage: {show: true}
			}
		},
	    legend: {
	        bottom: '20',
	        data:  ['高活跃度','中活跃度','低活跃度','沉睡活跃度'],
	        icon: 'square'
	    },
	    series : [
	        {
	            name: '顾客活跃度',
	            type: 'pie',
	            radius : '50%',
	            data:[
	                {value:the_high_activity_week_data, name:'高活跃度'},
	                {value:the_mid_activity_week_data, name:'中活跃度'},
	                {value:the_low_activity_week_data, name:'低活跃度'},
	                {value:the_sleep_activity_week_data, name:'沉睡活跃度'}
	            ],
				/*
				data:[
	                {value:34, name:'高活跃度'},
	                {value:42, name:'中活跃度'},
	                {value:64, name:'低活跃度'},
	                {value:83, name:'沉睡活跃度'}
	            ],*/
	            itemStyle: {
	                emphasis: {
	                    shadowBlur: 10,
	                    shadowOffsetX: 0,
	                    shadowColor: 'rgba(0, 0, 0, 0.5)'
	                }
	            }
	        }
	    ]
	};

	customer_active_week.setOption(option);
	window.addEventListener("resize",function(){
		customer_active_week.resize();
	});
}

function select_data()
{
    var start_date = document.getElementById("start_date").value;
	var end_date = document.getElementById("end_date").value;
	var start_strs = new Array();
	var end_strs = new Array();
	start_strs = start_date.split("-");
	end_strs = end_date.split("-");
	var start_year = parseInt(start_strs[0]);
	var start_month = parseInt(start_strs[1]);
	var start_day = parseInt(start_strs[2]);
	var end_year = parseInt(end_strs[0]);
	var end_month = parseInt(end_strs[1]);
	var end_day = parseInt(end_strs[2]);
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
					    request_data(start_year, start_month, start_day, end_year, end_month, end_day);
					}
				}
				else
				{
					request_data(start_year, start_month, start_day, end_year, end_month, end_day);
				}
			}
		}
		else
		{
		    request_data(start_year, start_month, start_day, end_year, end_month, end_day);
		}
	}
}

function request_data(start_year, start_month, start_day, end_year, end_month, end_day){
	custom_request_traffic_amount_and_the_amount_of_store_week(start_year, start_month, start_day, end_year, end_month, end_day);
	custom_request_the_new_and_old_customers_week(start_year, start_month, start_day, end_year, end_month, end_day);
	custom_request_the_resident_time_week(start_year, start_month, start_day, end_year, end_month, end_day);
	custom_request_deep_rate_and_bounce_rate_week(start_year, start_month, start_day, end_year, end_month, end_day);
	custom_request_visiting_cycle_week(start_year, start_month, start_day, end_year, end_month, end_day);
	custom_request_customer_active_week(start_year, start_month, start_day, end_year, end_month, end_day);
}

function custom_request_traffic_amount_and_the_amount_of_store_week(start_year, start_month, start_day, end_year, end_month, end_day){
	var my_url = project_name + "/get_custom_traffic_amount_and_the_amount_of_store_week";
    var request_time = new Object();
    request_time.start_year = start_year;
    request_time.start_month = start_month;
    request_time.start_day = start_day;
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_traffic_amount_and_the_amount_of_store_week_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_traffic_amount_and_the_amount_of_store_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_traffic_week_data = obj['traffic'];
		the_store_amount_week_data = obj['store_amount'];
		traffic_and_the_amount_of_store_week_time = obj['time'];
		var start_time = obj["show_start_time"];
		var end_time = obj["show_end_time"];
		document.getElementById("start_date").value = start_time;
		document.getElementById("end_date").value = end_time; 
		var start_strs = new Array();
		var end_strs = new Array();
		start_strs = start_time.split("-");
		end_strs = end_time.split("-");
		var start_year = parseInt(start_strs[0]);
		var start_month = parseInt(start_strs[1]);
		var start_day = parseInt(start_strs[2]);
		var end_year = parseInt(end_strs[0]);
		var end_month = parseInt(end_strs[1]);
		var end_day = parseInt(end_strs[2]);
		custom_request_into_the_store_rate_week(start_year, start_month, start_day, end_year, end_month, end_day);
	}
}

function custom_request_into_the_store_rate_week(start_year, start_month, start_day, end_year, end_month, end_day){
	var my_url = project_name + "/get_custom_into_the_store_rate_week";
    var request_time = new Object();
    request_time.start_year = start_year;
    request_time.start_month = start_month;
    request_time.start_day = start_day;
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_into_the_store_rate_week_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_into_the_store_rate_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_into_the_store_rate_week_data = obj['into_the_store_rate'];
		chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_week();
		chart_last_into_the_shore_rate_week();
	}
}

function custom_request_the_new_and_old_customers_week(start_year, start_month, start_day, end_year, end_month, end_day){
	var my_url = project_name + "/get_custom_the_new_and_old_customers_week";
    var request_time = new Object();
    request_time.start_year = start_year;
    request_time.start_month = start_month;
    request_time.start_day = start_day;
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_the_new_and_old_customers_week_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_the_new_and_old_customers_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_new_customers_week_data = obj['new_customers'];
		the_old_customers_week_data = obj['old_customers'];
		new_and_old_customers_week_time = obj['time'];
		chart_the_new_and_old_customers_week();
	}
}

function custom_request_the_resident_time_week(start_year, start_month, start_day, end_year, end_month, end_day){
	var my_url = project_name + "/get_custom_the_resident_time_week";
    var request_time = new Object();
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_the_resident_time_week_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_the_resident_time_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_resident_time_week_number = obj['the_resident_time_number'];
		the_resident_time_week_time = obj['the_resident_time_time']
		chart_the_resident_time_week();
	}
}

function custom_request_deep_rate_and_bounce_rate_week(start_year, start_month, start_day, end_year, end_month, end_day){
	var my_url = project_name + "/get_custom_deep_rate_and_bounce_rate_week";
    var request_time = new Object();
    request_time.start_year = start_year;
    request_time.start_month = start_month;
    request_time.start_day = start_day;
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_deep_rate_and_bounce_rate_week_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_deep_rate_and_bounce_rate_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_bounce_rate_week_data = obj['bounce_rate'];
		the_deep_rate_week_data = obj['deep_rate'];
		bounce_rate_and_deep_rate_week_time = obj['time'];
		chart_deep_rate_and_bounce_rate_week();
		chart_last_deep_rate_and_bounce_rate_week();
	}
}

function custom_request_visiting_cycle_week(start_year, start_month, start_day, end_year, end_month, end_day){
	var my_url = project_name + "/get_custom_visiting_cycle_week";
    var request_time = new Object();
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_visiting_cycle_week_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_visiting_cycle_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		visiting_cycle_week_number = obj['visiting_cycle_number'];
		visiting_cycle_week_time = obj['visiting_cycle_time']
		chart_visiting_cycle_week();
	}
}

function custom_request_customer_active_week(start_year, start_month, start_day, end_year, end_month, end_day){
	var my_url = project_name + "/get_custom_customer_active_week";
    var request_time = new Object();
    request_time.end_year = end_year;
    request_time.end_month = end_month;
    request_time.end_day = end_day;
	$.ajax({
		type: "GET",
		url: my_url,
		data: request_time,
		success: custom_customer_active_week_callback,
		error: function(data)
		{
		   alert("连接服务器失败！");
		}
	});
}

function custom_customer_active_week_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		window.location.href = project_name + "/data/login.html";
	}else{
		var obj = JSON.parse(data);
		the_high_activity_week_data = obj["high_activity"];
        the_mid_activity_week_data = obj["mid_activity"];
        the_low_activity_week_data = obj["low_activity"];
        the_sleep_activity_week_data = obj["sleep_activity"];
		chart_customer_active_week();
	}
}