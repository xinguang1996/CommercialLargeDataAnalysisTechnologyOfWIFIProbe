var the_traffic_month_data;//客流量按月计
var the_store_amount_month_data;//入店量按月计
var traffic_and_the_amount_of_store_month_time;//客流量/入店量按月计时间轴

var the_into_the_store_rate_month_data;//入店率按月计

var new_and_old_customers_month_time;//新老顾客按月计时间轴
var the_new_customers_month_data;//新顾客按月计
var the_old_customers_month_data;//老顾客按月计

var the_bounce_rate_month_data;//跳出率按月计
var the_deep_rate_month_data;//深访率按月计
var bounce_rate_and_deep_rate_month_time;//跳出率/深访率按月计时间轴

var the_resident_time_month_number;//驻店时长按月计人数
var the_resident_time_month_time;//驻店时长按月计时间

var visiting_cycle_month_time;//来访周期时间按月计
var visiting_cycle_month_number;//来访周期人数按月计

var the_high_activity_month_data;//高活跃度按月计
var the_mid_activity_month_data;//中活跃度按月计
var the_low_activity_month_data;//低活跃度按月计
var the_sleep_activity_month_data;//沉睡活跃度按月计

var traffic_amount_and_the_amount_of_store_and_into_the_store_rate_month;
var the_new_and_old_customers_month;
var deep_rate_and_bounce_rate_month;
var the_resident_time_month;
var visiting_cycle_month;
var customer_active_month;
mui.ready(function(){
	/*
	request_traffic_amount_and_the_amount_of_store_month();
	//chart_traffic_amount_and_the_amount_of_store_month();
	document.getElementById("traffic_amount_and_the_amount_of_store_and_into_the_store_rate_month").addEventListener("tap", function(){
		request_traffic_amount_and_the_amount_of_store_month();
	});
	document.getElementById("the_new_and_old_customers_month").addEventListener("tap", function(){
		request_the_new_and_old_customers_month();
	});
	document.getElementById("deep_rate_and_bounce_rate_month").addEventListener("tap", function(){
		request_deep_rate_and_bounce_rate_month();
	});
	document.getElementById("the_resident_time_month").addEventListener("tap", function(){
		request_the_resident_time_month();
	});
	document.getElementById("visiting_cycle_month").addEventListener("tap", function(){
		request_visiting_cycle_month();
	});
	document.getElementById("customer_active_month").addEventListener("tap", function(){
		request_customer_active_month();
	});*/
	//request_traffic_amount_and_the_amount_of_store_hour();
	//chart_the_new_and_old_customers_hour();
});
function chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_month(){
	traffic_amount_and_the_amount_of_store_and_into_the_store_rate_month = echarts.init(document.getElementById("data_show"));
	var option = {
	    tooltip: {
	        trigger: 'axis',
	        axisPointer: {
	            type: 'cross',
	            crossStyle: {
	                color: '#999'
	            }
	        }
	    },
	    legend: {
	        data:['客流量','入店量','入店率']
	    },
	    grid: [{
	        left: 75,
	        right: 50
	    }, {
	        left: 75,
	        right: 50
	    }],
	    xAxis: [
	        {
	            type: 'category',
	            data: traffic_and_the_amount_of_store_month_time,
	            axisPointer: {
	                type: 'shadow'
	            }
	        }
	    ],
	    yAxis: [
	        {
	        	name: '人数',
	            type: 'value',
	            //interval: 50,
	            axisLabel: {
	                formatter: '{value} 人'
	            }
	        },
	        {
	        	name: '入店率',
	            type: 'value',
	            //interval: 5,
	            axisLabel: {
	                formatter: '{value}'
	            }
	        }
	    ],
	    series: [
	        {
	            name:'客流量',
	            type:'bar',
	            data:the_traffic_month_data
	        },
	        {
	            name:'入店量',
	            type:'bar',
	            data:the_store_amount_month_data
	        },
	        {
	            name:'入店率',
	            type:'line',
	            yAxisIndex: 1,
	            data:the_into_the_store_rate_month_data
	        }
	    ]
	};
	traffic_amount_and_the_amount_of_store_and_into_the_store_rate_month.setOption(option);
}

function request_traffic_amount_and_the_amount_of_store_month(){
	mui.ajax(my_ip + "/get_traffic_amount_and_the_amount_of_store_month", {
		type: 'get',
		timeout: 8000,
		success: traffic_amount_and_the_amount_of_store_month_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function traffic_amount_and_the_amount_of_store_month_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_traffic_month_data = obj['traffic'];
		the_store_amount_month_data = obj['store_amount'];
		traffic_and_the_amount_of_store_month_time = obj['time'];
		request_into_the_store_rate_month();
	}
}

function request_into_the_store_rate_month(){
	mui.ajax(my_ip + "/get_into_the_store_rate_month", {
		type: 'get',
		timeout: 8000,
		success: into_the_store_rate_month_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function into_the_store_rate_month_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_into_the_store_rate_month_data = obj['into_the_store_rate'];
		chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_month();
		traffic_amount_and_the_amount_of_store_and_into_the_store_rate_month.resize();
	}
}

function request_the_new_and_old_customers_month(){
	mui.ajax(my_ip + "/get_the_new_and_old_customers_month", {
		type: 'get',
		timeout: 8000,
		success: the_new_and_old_customers_month_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function the_new_and_old_customers_month_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_new_customers_month_data = obj['new_customers'];
		the_old_customers_month_data = obj['old_customers'];
		new_and_old_customers_month_time = obj['time'];
		chart_the_new_and_old_customers_month();
		the_new_and_old_customers_month.resize();
	}
}

function chart_the_new_and_old_customers_month(){
	the_new_and_old_customers_month = echarts.init(document.getElementById("data_show"));
	var option = {
		title: {
    		text: '新老顾客',
		},
		grid: [{
	        left: 75,
	        right: 50
	    }],
		tooltip: {
    		//trigger: 'axis'
		},
		legend: {
    		data:['新顾客','老顾客']
    		//data:['新顾客']
		},	
		xAxis:  {
    		//type: 'category',
    		//boundaryGap: false,
    		data: new_and_old_customers_month_time
    		//data: ['23','24','25','26','27']
    	},
    	yAxis: {
        	type: 'value',
        	axisLabel: {
            	formatter: '{value} 人'
        	}
    	},
    	series: [
        	{
            	name:'新顾客',
            	type:'bar',
            	data:the_new_customers_month_data,
            	//data:[34,45,56,67,78],
           		markLine: {
                	data: [
                    	{type: 'average', name: '平均值'}
                	]
            	}
        	},
        	{
            	name:'老顾客',
            	type:'bar',
            	data:the_old_customers_month_data,
            	//data:[21,23,34,45,67],
            	markLine: {
                	data: [
                    	{type: 'average', name: '平均值'}
                	]
            	}
        	}
    	]
	};
	the_new_and_old_customers_month.setOption(option);
}

function request_deep_rate_and_bounce_rate_month(){
	mui.ajax(my_ip + "/get_deep_rate_and_bounce_rate_month", {
		type: 'get',
		timeout: 8000,
		success: deep_rate_and_bounce_rate_month_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function deep_rate_and_bounce_rate_month_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_bounce_rate_month_data = obj['bounce_rate'];
		the_deep_rate_month_data = obj['deep_rate'];
		bounce_rate_and_deep_rate_month_time = obj['time'];
		chart_deep_rate_and_bounce_rate_month();
		deep_rate_and_bounce_rate_month.resize();
	}
}

function chart_deep_rate_and_bounce_rate_month(){
	deep_rate_and_bounce_rate_month = echarts.init(document.getElementById("data_show"));
	var option = {
	    title: {
	        text: '跳出率/深访率',
	        x: 'right'
	    },
	    tooltip: {
	        trigger: 'axis'
	    },
	    legend: {
	        data:['跳出率','深访率'],
	        x: 'left'
	    },
	    axisPointer: {
	        link: {xAxisIndex: 'all'}
	    },
	    grid: [{
	        left: 50,
	        right: 50,
	        height: '30%'
	    }, {
	        left: 50,
	        right: 50,
	        top: '55%',
	        height: '30%'
	    }],
	    xAxis : [
	        {
	            type : 'category',
	            boundaryGap : false,
	            axisLine: {onZero: true},
	            data: bounce_rate_and_deep_rate_month_time
	            //data: ['21','22','23','24','25']
	        },
	        {
	            gridIndex: 1,
	            type : 'category',
	            boundaryGap : false,
	            axisLine: {onZero: true},
	            data: bounce_rate_and_deep_rate_month_time,
	            //data: ['21','22','23','24','25'],
	            position: 'top'
	        }
	    ],
	    yAxis : [
	        {
	            name : '跳出率',
	            type : 'value'
	        },
	        {
	            gridIndex: 1,
	            name : '深访率',
	            type : 'value',
	            inverse: true
	        }
	    ],
	    series : [
	        {
	            name:'跳出率',
	            type:'line',
	            symbolSize: 8,
	            hoverAnimation: false,
	            data:the_bounce_rate_month_data
	            //data: [0.12,0.13,0.42,0.12,0.52]
	        },
	        {
	            name:'深访率',
	            type:'line',
	            xAxisIndex: 1,
	            yAxisIndex: 1,
	            symbolSize: 8,
	            hoverAnimation: false,
	            data: the_deep_rate_month_data
	            //data: [0.32,0.21,0.54,0.31,0.21]
	        }
	    ]
	};
	deep_rate_and_bounce_rate_month.setOption(option);
}

function request_the_resident_time_month(){
	mui.ajax(my_ip + "/get_the_resident_time_month", {
		type: 'get',
		timeout: 8000,
		success: the_resident_time_month_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function the_resident_time_month_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_resident_time_month_number = obj['the_resident_time_number'];
		the_resident_time_month_time = obj['the_resident_time_time']
		chart_the_resident_time_month();
		the_resident_time_month.resize();
	}
}

function chart_the_resident_time_month(){
	the_resident_time_month = echarts.init(document.getElementById("data_show"));
	var option = {
	    tooltip: {
	        trigger: 'axis'
	    },
	    title: {
	        left: 'center',
	        text: '驻店时长',
	    },
	    xAxis: {
	    	name: '时间',
	        type: 'category',
	        boundaryGap: false,
	        data: the_resident_time_month_time
	        //data: ['21','22','23','24','25']
	    },
	    yAxis: {
	    	name: '人数（个）',
	        type: 'value',
	        boundaryGap: [0, '100%']
	    },
	    grid: [{
	        left: 75,
	        right: 50
	    }],
	    series: [
	        {
	            name:'驻店时长',
	            type:'line',
	            smooth:true,
	            symbol: 'none',
	            sampling: 'average',
	            itemStyle: {
	                normal: {
	                    color: 'rgb(255, 70, 131)'
	                }
	            },
	            areaStyle: {
	                normal: {
	                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
	                        offset: 0,
	                        color: 'rgb(255, 158, 68)'
	                    }, {
	                        offset: 1,
	                        color: 'rgb(255, 70, 131)'
	                    }])
	                }
	            },
	            data: the_resident_time_month_number
	            //data: ['21','22', '32','32','64']
	        }
	    ]
	};
	the_resident_time_month.setOption(option);
}

function request_visiting_cycle_month(){
	mui.ajax(my_ip + "/get_visiting_cycle_month", {
		type: 'get',
		timeout: 8000,
		success: visiting_cycle_month_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function visiting_cycle_month_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		visiting_cycle_month_number = obj['visiting_cycle_number'];
		visiting_cycle_month_time = obj['visiting_cycle_time']
		chart_visiting_cycle_month();
		visiting_cycle_month.resize();
	}
}

function chart_visiting_cycle_month(){
	visiting_cycle_month = echarts.init(document.getElementById("data_show"));
	var option = {
	    tooltip: {
	        trigger: 'axis'
	    },
	    title: {
	        left: 'center',
	        text: '来访周期',
	    },
	    xAxis: {
	    	name: '时间',
	        type: 'category',
	        boundaryGap: false,
	        data: visiting_cycle_month_time
	        //data: ['21','22','23','24','25']
	    },
	    yAxis: {
	    	name: '人数（个）',
	        type: 'value',
	        boundaryGap: [0, '100%']
	    },
	    grid: [{
	        left: 75,
	        right: 50
	    }],
	    series: [
	        {
	            name:'来访周期',
	            type:'line',
	            smooth:true,
	            symbol: 'none',
	            sampling: 'average',
	            itemStyle: {
	                normal: {
	                    color: 'rgb(255, 70, 131)'
	                }
	            },
	            areaStyle: {
	                normal: {
	                    color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [{
	                        offset: 0,
	                        color: 'rgb(255, 158, 68)'
	                    }, {
	                        offset: 1,
	                        color: 'rgb(255, 70, 131)'
	                    }])
	                }
	            },
	            data: visiting_cycle_month_number
	            //data: ['21','22', '32','32','64']
	        }
	    ]
	};
	visiting_cycle_month.setOption(option);
}

function request_customer_active_month(){
	mui.ajax(my_ip + "/get_customer_active_month", {
		type: 'get',
		timeout: 8000,
		success: customer_active_month_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function customer_active_month_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_high_activity_month_data = obj["high_activity"];
        the_mid_activity_month_data = obj["mid_activity"];
        the_low_activity_month_data = obj["low_activity"];
        the_sleep_activity_month_data = obj["sleep_activity"];
		chart_customer_active_month();
		customer_active_month.resize();
	}
}

function chart_customer_active_month(){
	customer_active_month = echarts.init(document.getElementById("data_show"));
	var colors = ['#1790CF', '#1BB2D8', '#99D2DD', '#88B0BB', '#1C7099','#038CC4'];
	var option = {
	    color: colors,
	    tooltip : {
	        trigger: 'item',
	        formatter: "{a} <br/>{b} : {c} ({d}%)"
	    },
	    title: {
	        left: 'center',
	        text: '顾客活跃度',
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
	                {value:the_high_activity_month_data, name:'高活跃度'},
	                {value:the_mid_activity_month_data, name:'中活跃度'},
	                {value:the_low_activity_month_data, name:'低活跃度'},
	                {value:the_sleep_activity_month_data, name:'沉睡活跃度'}
	            ],
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

	customer_active_month.setOption(option);
}