var the_traffic_day_data;//客流量按日计
var the_store_amount_day_data;//入店量按日计
var traffic_and_the_amount_of_store_day_time;//客流量/入店量按日计时间轴

var the_into_the_store_rate_day_data;//入店率按日计

var new_and_old_customers_day_time;//新老顾客按日计计时间轴
var the_new_customers_day_data;//新顾客按日计
var the_old_customers_day_data;//老顾客按日计

var the_bounce_rate_day_data;//跳出率按日计
var the_deep_rate_day_data;//深访率按日计
var bounce_rate_and_deep_rate_day_time;//跳出率/深访率按日计时间轴

var the_resident_time_day_number;//驻店时长按日计人数
var the_resident_time_day_time;//驻店时长按日计时间

var traffic_amount_and_the_amount_of_store_and_into_the_store_rate_day;
var the_new_and_old_customers_day;
var deep_rate_and_bounce_rate_day;
var the_resident_time_day;

mui.ready(function(){
	/*
	request_traffic_amount_and_the_amount_of_store_day();
	document.getElementById("traffic_amount_and_the_amount_of_store_and_into_the_store_rate_day").addEventListener("tap", function(){
		request_traffic_amount_and_the_amount_of_store_day();
	});
	document.getElementById("the_new_and_old_customers_day").addEventListener("tap", function(){
		request_the_new_and_old_customers_day();
	});
	document.getElementById("deep_rate_and_bounce_rate_day").addEventListener("tap", function(){
		request_deep_rate_and_bounce_rate_day();
	});
	document.getElementById("the_resident_time_day").addEventListener("tap", function(){
		request_the_resident_time_day();
	});*/
});
function chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_day(){
	traffic_amount_and_the_amount_of_store_and_into_the_store_rate_day = echarts.init(document.getElementById("data_show"));
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
	        left: 100,
	        right: 50
	    }],
	    xAxis: [
	        {
	            type: 'category',
	            data: traffic_and_the_amount_of_store_day_time,
	            //data: ['21','22','23','24','25'],
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
	            data:the_traffic_day_data
	            //data: [21,34,53,62,73]
	        },
	        {
	            name:'入店量',
	            type:'bar',
	            data:the_store_amount_day_data
	            //data: [31,24,53,26,73]
	        },
	        {
	            name:'入店率',
	            type:'line',
	            yAxisIndex: 1,
	            data:the_into_the_store_rate_day_data
	            //data:[0.31,0.42,0.53,0.54,0.63]
	        }
	    ]
	};
	traffic_amount_and_the_amount_of_store_and_into_the_store_rate_day.setOption(option);
}

function request_traffic_amount_and_the_amount_of_store_day(){
	mui.ajax(my_ip + "/get_traffic_amount_and_the_amount_of_store_day", {
		type: 'get',
		timeout: 8000,
		success: traffic_amount_and_the_amount_of_store_day_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function traffic_amount_and_the_amount_of_store_day_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_traffic_day_data = obj['traffic'];
		the_store_amount_day_data = obj['store_amount'];
		traffic_and_the_amount_of_store_day_time = obj['time'];
		request_into_the_store_rate_day();
	}
}

function request_into_the_store_rate_day(){
	mui.ajax(my_ip + "/get_into_the_store_rate_day", {
		type: 'get',
		timeout: 8000,
		success: into_the_store_rate_day_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function into_the_store_rate_day_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_into_the_store_rate_day_data = obj['into_the_store_rate'];
		chart_traffic_amount_and_the_amount_of_store_and_into_the_store_rate_day();
		traffic_amount_and_the_amount_of_store_and_into_the_store_rate_day.resize();
	}
}

function request_the_new_and_old_customers_day(){
	mui.ajax(my_ip + "/get_the_new_and_old_customers_day", {
		type: 'get',
		timeout: 8000,
		success: the_new_and_old_customers_day_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function the_new_and_old_customers_day_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_new_customers_day_data = obj['new_customers'];
		the_old_customers_day_data = obj['old_customers'];
		new_and_old_customers_day_time = obj['time'];
		chart_the_new_and_old_customers_day();
		the_new_and_old_customers_day.resize();
	}
}

function chart_the_new_and_old_customers_day(){
	the_new_and_old_customers_day = echarts.init(document.getElementById("data_show"));
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
    		data: new_and_old_customers_day_time
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
            	data:the_new_customers_day_data,
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
            	data:the_old_customers_day_data,
            	//data:[21,23,34,45,67],
            	markLine: {
                	data: [
                    	{type: 'average', name: '平均值'}
                	]
            	}
        	}
    	]
	};
	the_new_and_old_customers_day.setOption(option);
}

function request_deep_rate_and_bounce_rate_day(){
	mui.ajax(my_ip + "/get_deep_rate_and_bounce_rate_day", {
		type: 'get',
		timeout: 8000,
		success: deep_rate_and_bounce_rate_day_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function deep_rate_and_bounce_rate_day_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_bounce_rate_day_data = obj['bounce_rate'];
		the_deep_rate_day_data = obj['deep_rate'];
		bounce_rate_and_deep_rate_day_time = obj['time'];
		chart_deep_rate_and_bounce_rate_day();
		deep_rate_and_bounce_rate_day.resize();
	}
}

function chart_deep_rate_and_bounce_rate_day(){
	deep_rate_and_bounce_rate_day = echarts.init(document.getElementById("data_show"));
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
	            data: bounce_rate_and_deep_rate_day_time
	            //data: ['21','22','23','24','25']
	        },
	        {
	            gridIndex: 1,
	            type : 'category',
	            boundaryGap : false,
	            axisLine: {onZero: true},
	            data: bounce_rate_and_deep_rate_day_time,
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
	            data:the_bounce_rate_day_data
	            //data: [0.12,0.13,0.42,0.12,0.52]
	        },
	        {
	            name:'深访率',
	            type:'line',
	            xAxisIndex: 1,
	            yAxisIndex: 1,
	            symbolSize: 8,
	            hoverAnimation: false,
	            data: the_deep_rate_day_data
	            //data: [0.32,0.21,0.54,0.31,0.21]
	        }
	    ]
	};
	deep_rate_and_bounce_rate_day.setOption(option);
}

function request_the_resident_time_day(){
	mui.ajax(my_ip + "/get_the_resident_time_day", {
		type: 'get',
		timeout: 8000,
		success: the_resident_time_day_callback,
		error: function(xhr, type, errorThrown){
			mui.toast("网络错误！");
		}
	});
}

function the_resident_time_day_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		mui.openWindow({
			url: "../login/login.html",
			id: "login.html"
		});
	}else{
		var obj = JSON.parse(data);
		the_resident_time_day_number = obj['the_resident_time_number'];
		the_resident_time_day_time = obj['the_resident_time_time']
		chart_the_resident_time_day();
		the_resident_time_day.resize();
	}
}

function chart_the_resident_time_day(){
	the_resident_time_day = echarts.init(document.getElementById("data_show"));
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
	        data: the_resident_time_day_time
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
	            data: the_resident_time_day_number
	        }
	    ]
	};
	the_resident_time_day.setOption(option);
}
