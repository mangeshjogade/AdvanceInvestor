/**
 * 
 */

var stompClient = null;
	
	var time;
	var dataPoints1;
	var chart = null;
	var price;
	
	function resetChart(){
		
		dataPoints1 = [];
		
		// initial time to 09:30 AM		
		time = new Date;
		time.setHours(9);
		time.setMinutes(30);
		time.setSeconds(00);
		time.setMilliseconds(00);
		
	}
	
	function initializeChart(){
		
		chart = new CanvasJS.Chart("chartContainer", {
			zoomEnabled : true,
			title : {
				text : "Price TrendLine"
			},
			toolTip : {
				shared : true
	
			},
			legend : {
				verticalAlign : "top",
				horizontalAlign : "center",
				fontSize : 14,
				fontWeight : "bold",
				fontFamily : "calibri",
				fontColor : "dimGrey"
			},
			axisX : {
				title : "Price TrendLine"
			},
			axisY : {
				prefix : '$',
				includeZero : false
			},
			data : [ {
				// dataSeries1
				type : "line",
				xValueType : "dateTime",
				showInLegend : true,
				name : "Company A",
				dataPoints : dataPoints1
			} ],
			legend : {
				cursor : "pointer",
	
				itemclick : function(e) {
					if (typeof (e.dataSeries.visible) === "undefined"
							|| e.dataSeries.visible) {
						e.dataSeries.visible = false;
					} else {
						e.dataSeries.visible = true;
					}
					chart.render();
				}
			}
		});
	}
	
	function updateChart(priceData) {
		
		price = JSON.parse(priceData.body).price;
		price = Math.round(price * 100) / 100
		
		$("#currentPrice").replaceWith("<div id=\"currentPrice\"><h3>"+price+"</h3></div>");
		
		time.setTime(time.getTime()+ 1000);
		
		// pushing the new values
		dataPoints1.push({
			x : time.getTime(),
			y : price
		});

		// updating legend text with  updated with y Value 
		chart.options.data[0].legendText = " Current Price $" + price;
		chart.render();

	};

	
	function connect() {
		
		resetChart();
		initializeChart();
		
		var socket = new SockJS('/advance-investor-exchange-websocket');
		
		stompClient = Stomp.over(socket);
	    stompClient.connect({}, function (frame) {
	        //console.log('Connected: ' + frame);
	        stompClient.subscribe('/topic/live-price', function (priceData) {
	        	updateChart(priceData);
	        });
	    });
	}
	
	function disconnect() {
	    if (stompClient != null) {
	        stompClient.disconnect();
	    }
	    console.log("Disconnected");
	}
	
	$(function () {
		$( "#connect" ).click(function() { connect(); });
	    $( "#disconnect" ).click(function() { disconnect(); });
	});	
