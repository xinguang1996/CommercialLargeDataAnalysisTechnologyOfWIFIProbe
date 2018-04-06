var real_time_traffic_amount_data;
var real_time_the_amount_of_store_data;
var real_time_into_the_store_rate_data;
var time;
$(document).ready(function(){
	//chart_real_time_traffic_amount();
	request_real_time_data_show();
	setInterval("request_real_time_data_show()",10000);
});

function request_real_time_data_show(){
	var my_url = project_name + "/get_real_time_data_show";
	$.ajax({
		url: my_url,
		type: "GET",
		success: real_time_data_show_callback,
		error: function(data){
			alert("请求服务器失败！");
		}
	});
}

function real_time_data_show_callback(data){
	var index = data.indexOf("error");
	if(index == 0){
		alert(data);
	}else{
		var obj = JSON.parse(data);
		real_time_traffic_amount_data = obj["real_time_traffic_amount_data"];
		real_time_the_amount_of_store_data = obj["real_time_the_amount_of_store_data"];
		real_time_into_the_store_rate_data = obj["real_time_into_the_store_rate_data"];
		time = obj["time"];
		chart_real_time_traffic_amount();
		chart_real_time_the_amount_of_store();
		chart_real_time_into_the_store_rate();
	}
}

function chart_real_time_traffic_amount(){
	var real_time_traffic_amount = echarts.init(document.getElementById("real_time_traffic_amount"));
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
			data:['客流量']
		},
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                //data: ['1', '5', '10', '15', '20', '25', '31']
				data: time
            }
        ],
        yAxis: [
            {
                name: '人数',
                type: 'value'
            }
        ],
        series: [
            {
                name: '客流量',
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
				data: real_time_traffic_amount_data
            }
        ]
	};
	real_time_traffic_amount.setOption(option);
	window.addEventListener("resize",function(){
		real_time_traffic_amount.resize();
	});
}

function chart_real_time_the_amount_of_store(){
	var real_time_the_amount_of_store = echarts.init(document.getElementById("real_time_the_amount_of_store"));
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
			data:['入店量']
		},
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                //data: ['1', '5', '10', '15', '20', '25', '31']
				data: time
            }
        ],
        yAxis: [
            {
                name: '人数',
                type: 'value'
            }
        ],
        series: [
            {
                name: '入店量',
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
				data: real_time_the_amount_of_store_data
            }
        ]
	};
	real_time_the_amount_of_store.setOption(option);
	window.addEventListener("resize",function(){
		real_time_the_amount_of_store.resize();
	});
}

function chart_real_time_into_the_store_rate(){
	var real_time_into_the_store_rate = echarts.init(document.getElementById("real_time_into_the_store_rate"));
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
			data:['入店率']
		},
        xAxis: [
            {
                type: 'category',
                boundaryGap: false,
                //data: ['1', '5', '10', '15', '20', '25', '31']
				data: time
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
                name: '入店率',
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
				data: real_time_into_the_store_rate_data
            }
        ]
	};
	real_time_into_the_store_rate.setOption(option);
	window.addEventListener("resize",function(){
		real_time_into_the_store_rate.resize();
	});
}