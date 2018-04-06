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

var traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour;
var the_new_and_old_customers_hour;
var deep_rate_and_bounce_rate_hour;

mui.ready(function(){
	/*
	request_traffic_amount_and_the_amount_of_store_hour();
	document.getElementById("traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour").addEventListener("tap", function(){
		request_traffic_amount_and_the_amount_of_store_hour();
	});
	document.getElementById("the_new_and_old_customers_hour").addEventListener("tap", function(){
		request_the_new_and_old_customers_hour();
	});
	document.getElementById("deep_rate_and_bounce_rate_hour").addEventListener("tap", function(){
		request_deep_rate_and_bounce_rate_hour();
	});*/
	
	//request_traffic_amount_and_the_amount_of_store_hour();
	//chart_the_new_and_old_customers_hour();
});
function chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour(){
	traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour = echarts.init(document.getElementById("data_show"));
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
	            data: traffic_and_the_amount_of_store_hour_time,
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
	            data:the_traffic_hour_data
	        },
	        {
	            name:'入店量',
	            type:'bar',
	            data:the_store_amount_hour_data
	        },
	        {
	            name:'入店率',
	            type:'line',
	            yAxisIndex: 1,
	            data:the_into_the_store_rate_hour_data
	        }
	    ]
	};
	traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour.setOption(option);
}

function request_traffic_amount_and_the_amount_of_store_hour(){
	mui.ajax(my_ip + "/get_traffic_amount_and_the_amount_of_store_hour", {
		type: 'get',
		timeout: 8000,
		success: traffic_amount_and_the_amount_of_store_hour_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function traffic_amount_and_the_amount_of_store_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_traffic_hour_data = obj['traffic'];
		the_store_amount_hour_data = obj['store_amount'];
		traffic_and_the_amount_of_store_hour_time = obj['time'];
		request_into_the_store_rate_hour();
	}
}

function request_into_the_store_rate_hour(){
	mui.ajax(my_ip + "/get_into_the_store_rate_hour", {
		type: 'get',
		timeout: 8000,
		success: into_the_store_rate_hour_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function into_the_store_rate_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_into_the_store_rate_hour_data = obj['into_the_store_rate'];
		chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour();
		traffic_amount_and_the_amount_of_store_and_into_the_store_rate_hour.resize();
	}
}

function request_the_new_and_old_customers_hour(){
	mui.ajax(my_ip + "/get_the_new_and_old_customers_hour", {
		type: 'get',
		timeout: 8000,
		success: the_new_and_old_customers_hour_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function the_new_and_old_customers_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_new_customers_hour_data = obj['new_customers'];
		the_old_customers_hour_data = obj['old_customers'];
		new_and_old_customers_hour_time = obj['time'];
		chart_the_new_and_old_customers_hour();
		the_new_and_old_customers_hour.resize();
	}
}

function chart_the_new_and_old_customers_hour(){
	the_new_and_old_customers_hour = echarts.init(document.getElementById("data_show"));
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
		},	
		xAxis:  {
    		//type: 'category',
    		//boundaryGap: false,
    		data: new_and_old_customers_hour_time
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
            	data:the_new_customers_hour_data,
           		markLine: {
                	data: [
                    	{type: 'average', name: '平均值'}
                	]
            	}
        	},
        	{
            	name:'老顾客',
            	type:'bar',
            	data:the_old_customers_hour_data,
            	markLine: {
                	data: [
                    	{type: 'average', name: '平均值'}
                	]
            	}
        	}
    	]
	};
	the_new_and_old_customers_hour.setOption(option);
}

function request_deep_rate_and_bounce_rate_hour(){
	mui.ajax(my_ip + "/get_deep_rate_and_bounce_rate_hour", {
		type: 'get',
		timeout: 8000,
		success: deep_rate_and_bounce_rate_hour_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function deep_rate_and_bounce_rate_hour_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_bounce_rate_hour_data = obj['bounce_rate'];
		the_deep_rate_hour_data = obj['deep_rate'];
		bounce_rate_and_deep_rate_hour_time = obj['time'];
		chart_deep_rate_and_bounce_rate_hour();
		deep_rate_and_bounce_rate_hour.resize();
	}
}

function chart_deep_rate_and_bounce_rate_hour(){
	deep_rate_and_bounce_rate_hour = echarts.init(document.getElementById("data_show"));
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
	            data: bounce_rate_and_deep_rate_hour_time
	        },
	        {
	            gridIndex: 1,
	            type : 'category',
	            boundaryGap : false,
	            axisLine: {onZero: true},
	            data: bounce_rate_and_deep_rate_hour_time,
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
	            data:the_bounce_rate_hour_data
	        },
	        {
	            name:'深访率',
	            type:'line',
	            xAxisIndex: 1,
	            yAxisIndex: 1,
	            symbolSize: 8,
	            hoverAnimation: false,
	            data: the_deep_rate_hour_data
	        }
	    ]
	};
	deep_rate_and_bounce_rate_hour.setOption(option);
}
