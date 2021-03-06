var processJSON;
var agediagnosis_chart_data_json;
var gender_chart_data_json;
var bmi_chart_data_json;
var comorb_chart_data_json;
var cvr_chart_data_json;

var complClassJsonRawValuesMacro;
var complClassJsonRawValuesMicro;
var complClassJsonRawValuesNonVascular; 
var complClassJsonRawValues;
var comorb_chart_macro;
var comorb_chart_micro;
var comorb_chart_nv;
var comorb_chart2;

$(function(){
	renderContainer();

});

$( window ).resize(function() {
	renderContainer();    		
});

function renderContainer(){
	$('#chart_container').width(Math.floor($('#dashboard_container').innerWidth() - $('#menu_container').innerWidth()-50));

	$('#chart_container').height($('#dashboard_container').height());
	$('#menu_container').height($('#dashboard_container').height());

	//setloader position
	$('.loader').css('left',($('#dashboard_container').innerWidth()/2)-$('.loader').innerWidth());

	$("#singlePatientDataContainer").hide();

	hideFilters();
	hideLoader();
	hideGraphs(2);
	hideGraphs(3);
	showTooltip(1);

	//setting search patient action
	bindSearchPatient();
}


google.load("visualization", "1", {packages:["corechart", "timeline"]});
google.setOnLoadCallback(drawCharts);

function drawCharts() {
	$('#comorb_chart_ex_container').hide();
//	GENDER
	gender_chart_data_json = $.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: false,
		data: {step: "1",chart_type: "gender"}
	}).responseText;

	var gender_chart_data = new google.visualization.DataTable(gender_chart_data_json);

	var gender_options = {
			chartArea:{top:20,width:"80%",height:"80%"},
			legend: {position: 'right', alignment: 'center', textStyle: { fontName: 'MyriadPro', fontSize: 14 }},
			slices: {0: {color: '#015E84'}, 1:{color: '#90C8D1'}},
			tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
			pieSliceText: 'value'
	};

	var gender_chart = new google.visualization.PieChart(document.getElementById('gender_chart'));
	gender_chart.draw(gender_chart_data, gender_options);

//	BMI        

	bmi_chart_data_json = $.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: false,
		data: {step: "1",chart_type: "bmi"}
	}).responseText;

	var bmi_chart_data = new google.visualization.DataTable(bmi_chart_data_json);


	var bmi_options = {
			chartArea:{top:20,width:"80%",height:"80%"},
			hAxis: {slantedTextAngle: 90},
			//isStacked: true,
			tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
			legend: {position: 'top', alignment: 'center', textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
			explorer: { actions: ['dragToZoom', 'rightClickToReset'],  maxZoomIn: .01 },
			series: [{color: '#015E84', visibleInLegend: true}, 
			         {color: '#AF5E5E', visibleInLegend: true}],
			         curveType: 'function'
	};

	var bmi_chart = new google.visualization.ColumnChart(document.getElementById('bmi_chart'));
//	var bmi_chart = new google.visualization.LineChart(document.getElementById('bmi_chart'));
	bmi_chart.draw(bmi_chart_data, bmi_options);


//	COMORBIDITY
	comorb_chart_data_json = $.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: false,
		data: {step: "1",chart_type: "comorbidity"}
	}).responseText;

	var outerJson = jQuery.parseJSON(comorb_chart_data_json);
	var complClassJson = outerJson.comorb_class;
	var complClassJsonData = complClassJson.chart_data;
	complClassJsonRawValues = complClassJson.raw_data;
	var comorb_chart_data = new google.visualization.DataTable(complClassJsonData);

	//var comorb_chart_data = new google.visualization.DataTable(comorb_chart_data_json);

	var comorb_options = {
			chartArea:{top:20,width:"80%",height:"80%"},
			tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
			legend: {position: 'right', alignment: 'center', textStyle: { fontName: 'MyriadPro', fontSize: 14 }},
			slices: {0: {color: '#015E84'}, 1:{color: '#90C8D1'},  2:{color: '#D4B667'},3:{color: '#927D62'},4:{color: '#D8C4A0'},
				5:{color: '#D18369'},6:{color: '#AF5E5E'},7:{color: '#BC7D7D'},8:{color: '#01827C'},9:{color: '#7CC3AE'},10:{color: '#015E84'}},
				pieSliceText: 'value'
	};

	comorb_chart2 = new google.visualization.PieChart(document.getElementById('comorb_chart'));
	comorb_chart2.draw(comorb_chart_data, comorb_options);

//	AGE@DIAGNOSIS

	agediagnosis_chart_data_json = $.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: false,
		data: {step: "1", chart_type: "age_diagnosis"}
	}).responseText;

	agediagnosis_chart_data_json = jQuery.parseJSON(agediagnosis_chart_data_json);

	var agediagnosis_chart_data = new google.visualization.DataTable(agediagnosis_chart_data_json.chart_json);

	var agediagnosis_options = {
			chartArea:{top:20,width:"80%",height:"80%"},
			legend: {position: 'right', alignment: 'center',textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
			tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
			slices: {0: {color: '#D18369'}, 1:{color: '#AF5E5E'},  2:{color: '#BC7D7D'},3:{color: '#927D62'},4:{color: '#D8C4A0'},
				5:{color: '#015E84'},6:{color: '#90C8D1'},7:{color: '#D4B667'},8:{color: '#01827C'},9:{color: '#7CC3AE'},10:{color: '#D18369'}},
				pieSliceText: 'value'
	};

	var agediagnosis_chart = new google.visualization.PieChart(document.getElementById('agediagnosis_chart'));
	agediagnosis_chart.draw(agediagnosis_chart_data, agediagnosis_options);


	//CARDIOVASCULAR RISK
	cvr_chart_data_json = $.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: false,
		data: {step: "1",chart_type: "cvr"}
	}).responseText;

	var cvr_chart_data = new google.visualization.DataTable(cvr_chart_data_json);

	var cvr_options = {
			chartArea:{top:20,width:"80%",height:"80%"},
			tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
			legend: {position: 'right', alignment: 'center', textStyle: { fontName: 'MyriadPro', fontSize: 14 }},
			slices: {0: {color: '#015E84'}, 1:{color: '#90C8D1'},  2:{color: '#D4B667'},
				3: {color: '#AF5E5E'}, 4:{color: '#BC7D7D'},  5:{color: '#01827C'}},
				pieSliceText: 'value'
	};

	var cvr_chart = new google.visualization.PieChart(document.getElementById('cvr_chart'));
	cvr_chart.draw(cvr_chart_data, cvr_options);

//	ADD SELECTORS FOR STEP2

	//GENDER SELECTOR
	google.visualization.events.addListener(gender_chart, 'select', selectGenderHandler);

	function selectGenderHandler(e) {
		//alert('A table row was selected');
		var selected = gender_chart.getSelection();

		var selectedStr = "";
		if(parseInt(selected[0].row)==0){
			selectedStr = "M";
		}
		else{
			selectedStr = "F";
		}

		hideGraphs(1);
		showLoader();

		$.ajax({
			url: "./i2b2Servlet/",
			dataType:"json",
			async: true,
			data: { step: "2",
				chart_type: "gender_process",
				selected_value:  selected[0].row  
			},
			complete: function(results){
				$('#step_item_1 > span').text("Gender: " + selectedStr);
				$('#step_item_1').show();
				showTooltip(2);
				gender_chart.setSelection(null);
				getProcess("gender_process", results.responseText);
			}
		});
	
	}

	//AGE@DIAGNOSIS SELECTOR
	google.visualization.events.addListener(agediagnosis_chart, 'select', selectAgeHandler);

	function selectAgeHandler(e) {
		//alert('A table row was selected');
		var selected = agediagnosis_chart.getSelection();

		var selectedStr = "";
		if(parseInt(selected[0].row)==0){
			selectedStr = "0-10";
		}
		else if(parseInt(selected[0].row)<=9){
			selectedStr = selected[0].row +"0-"+(parseInt(selected[0].row)+1)+"0"; 
		}
		else{
			selectedStr = "sup-100";
		}

		hideGraphs(1);
		showLoader();

		$.ajax({
			url: "./i2b2Servlet/",
			dataType:"json",
			async: true,
			data: { step: "2",
				chart_type: "age_process",
				selected_value:  agediagnosis_chart_data_json.raw_values[selected[0].row].patient_nums  
			},
			complete: function(results){
				$('#step_item_1 > span').text("Age: " + selectedStr);
				$('#step_item_1').show();
				showTooltip(2);
				agediagnosis_chart.setSelection(null);
				getProcess("age_process", results.responseText);
			}
		});
	}

	//CVR SELECTOR
	google.visualization.events.addListener(cvr_chart, 'select', selectVRHandler);
	function selectVRHandler(e) {	
		var selected = cvr_chart.getSelection();
		alert('A table row was selected '+selected[0].row);
		var selectedStr = "";
		if(parseInt(selected[0].row)==0){
			selectedStr = "I";
		}
		else if(parseInt(selected[0].row)==1){
			selectedStr = "II";
		}else if(parseInt(selected[0].row)==2){
			selectedStr = "III";
		}else if(parseInt(selected[0].row)==3){
			selectedStr = "IV";
		}else if(parseInt(selected[0].row)==4){
			selectedStr = "V";
		}
		else{
			selectedStr = "VI";
		}

		hideGraphs(1);
		showLoader();

		$.ajax({
			url: "./i2b2Servlet/",
			dataType:"json",
			async: true,
			data: { step: "2",
				chart_type: "cvr_process",
				selected_value:  selected[0].row  
			},
			complete: function(results){
				$('#step_item_1 > span').text("CVR: " + selectedStr);
				$('#step_item_1').show();
				showTooltip(2);
				cvr_chart.setSelection(null);
				getProcess("cvr_process", results.responseText);
			}
		});
	}

	//COMORBIDITY SELECTOR
//	var complClassJsonRawValuesMacro;
//	var complClassJsonRawValuesMicro;
//	var complClassJsonRawValuesNonVascular; 
//	var comorb_chart_macro;
//	var comorb_chart_micro;
//	var comorb_chart_nv;
	google.visualization.events.addListener(comorb_chart2, 'select', selectComorbHandler);
	function selectComorbHandler(e) {
		var selected = comorb_chart2.getSelection();
		//	alert('A table row was selected '+selected[0].row);
		var checked = document.getElementById('comorbidity_cb').checked;
		if(!checked){
			$('#comorb_chart_ex_container').show();
			$('#comorb_chart_container').hide();
			if(parseInt(selected[0].row)==0){
				var outerJson = jQuery.parseJSON(comorb_chart_data_json);
				var complClassJson = outerJson.macro;
				var complClassJsonData = complClassJson.chart_data;
				complClassJsonRawValuesMacro = complClassJson.raw_data;
				var comorb_chart_data_macro = new google.visualization.DataTable(complClassJsonData);

				//var comorb_chart_data = new google.visualization.DataTable(comorb_chart_data_json);

				var comorb_options = {
						chartArea:{top:20,width:"80%",height:"80%"},
						tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
						legend: {position: 'right', alignment: 'center', textStyle: { fontName: 'MyriadPro', fontSize: 14 }},
						slices: {0: {color: '#015E84'}, 1:{color: '#90C8D1'},  2:{color: '#D4B667'},3:{color: '#927D62'},4:{color: '#D8C4A0'},
							5:{color: '#D18369'},6:{color: '#AF5E5E'},7:{color: '#BC7D7D'},8:{color: '#01827C'},9:{color: '#7CC3AE'},10:{color: '#015E84'}},
							pieSliceText: 'value'
				};

				comorb_chart_macro = new google.visualization.PieChart(document.getElementById('comorb_chart_ex'));
				comorb_chart_macro.draw(comorb_chart_data_macro, comorb_options);	
				google.visualization.events.addListener(comorb_chart_macro, 'select', selectComorbHandlerMacro);
			}
			else if(parseInt(selected[0].row)==1){
				var outerJson = jQuery.parseJSON(comorb_chart_data_json);
				var complClassJson = outerJson.micro;
				var complClassJsonData = complClassJson.chart_data;
				complClassJsonRawValuesMicro = complClassJson.raw_data;
				var comorb_chart_data_micro = new google.visualization.DataTable(complClassJsonData);

				//var comorb_chart_data = new google.visualization.DataTable(comorb_chart_data_json);

				var comorb_options = {
						chartArea:{top:20,width:"80%",height:"80%"},
						tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
						legend: {position: 'right', alignment: 'center', textStyle: { fontName: 'MyriadPro', fontSize: 14 }},
						slices: {0: {color: '#015E84'}, 1:{color: '#90C8D1'},  2:{color: '#D4B667'},3:{color: '#927D62'},4:{color: '#D8C4A0'},
							5:{color: '#D18369'},6:{color: '#AF5E5E'},7:{color: '#BC7D7D'},8:{color: '#01827C'},9:{color: '#7CC3AE'},10:{color: '#015E84'}},
							pieSliceText: 'value'
				};

				comorb_chart_micro = new google.visualization.PieChart(document.getElementById('comorb_chart_ex'));
				comorb_chart_micro.draw(comorb_chart_data_micro, comorb_options);
				google.visualization.events.addListener(comorb_chart_micro, 'select', selectComorbHandlerMicro);
			}else if(parseInt(selected[0].row)==2){
				var outerJson = jQuery.parseJSON(comorb_chart_data_json);
				var complClassJson = outerJson.not_vascular;
				var complClassJsonData = complClassJson.chart_data;
				complClassJsonRawValuesNonVascular = complClassJson.raw_data;
				var comorb_chart_data_nv = new google.visualization.DataTable(complClassJsonData);

				//var comorb_chart_data = new google.visualization.DataTable(comorb_chart_data_json);

				var comorb_options = {
						chartArea:{top:20,width:"80%",height:"80%"},
						tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
						legend: {position: 'right', alignment: 'center', textStyle: { fontName: 'MyriadPro', fontSize: 14 }},
						slices: {0: {color: '#015E84'}, 1:{color: '#90C8D1'},  2:{color: '#D4B667'},3:{color: '#927D62'},4:{color: '#D8C4A0'},
							5:{color: '#D18369'},6:{color: '#AF5E5E'},7:{color: '#BC7D7D'},8:{color: '#01827C'},9:{color: '#7CC3AE'},10:{color: '#015E84'}},
							pieSliceText: 'value'
				};

				comorb_chart_nv = new google.visualization.PieChart(document.getElementById('comorb_chart_ex'));
				comorb_chart_nv.draw(comorb_chart_data_nv, comorb_options);
				google.visualization.events.addListener(comorb_chart_nv, 'select', selectComorbHandlerNV);
			}
		}else{
			var selectedComorb = comorb_chart2.getSelection();
			var comorbIndexSlice = selectedComorb[0].row;

			//alert('A table row was selected from Comorb '+comorbIndexSlice+ " "+complClassJsonRawValues[comorbIndexSlice].patient_nums);
			hideGraphs(1);
			showLoader();
			$.ajax({
				url: "./i2b2Servlet/",
				dataType:"json",
				async: true,
				data: { step: "2",
					chart_type: "comorb_process",
					selected_value:  complClassJsonRawValues[comorbIndexSlice].patient_nums  
				},
				complete: function(results){
					$('#step_item_1 > span').text("Comorbidity Class: " + selectedStr);
					$('#step_item_1').show();
					showTooltip(2);
					comorb_chart2.setSelection(null);
					getProcess("comorb_process", results.responseText);
				}
			});
		}
	}

	function selectComorbHandlerMacro(e) {
		var selectedMacro = comorb_chart_macro.getSelection();
		var macroIndexSlice = selectedMacro[0].row;
		//alert('A table row was selected from Macro '+macroIndexSlice+ " "+complClassJsonRawValuesMacro[macroIndexSlice].patient_nums);
		hideGraphs(1);
		showLoader();
		$.ajax({
			url: "./i2b2Servlet/",
			dataType:"json",
			async: true,
			data: { step: "2",
				chart_type: "comorb_process",
				selected_value:  complClassJsonRawValuesMacro[macroIndexSlice].patient_nums  
			},
			complete: function(results){
				$('#step_item_1 > span').text("Comorbidity: " + selectedStr);
				$('#step_item_1').show();
				showTooltip(2);
				comorb_chart_macro.setSelection(null);
				getProcess("comorb_process", results.responseText);
			}
		});
	}

	function selectComorbHandlerMicro(e) {
		var selectedMicro = comorb_chart_micro.getSelection();
		var microIndexSlice = selectedMicro[0].row;
		//alert('A table row was selected from Micro '+microIndexSlice+ " "+complClassJsonRawValuesMicro[microIndexSlice].patient_nums);
		hideGraphs(1);
		showLoader();
		$.ajax({
			url: "./i2b2Servlet/",
			dataType:"json",
			async: true,
			data: { step: "2",
				chart_type: "comorb_process",
				selected_value:  complClassJsonRawValuesMicro[microIndexSlice].patient_nums  
			},
			complete: function(results){
				$('#step_item_1 > span').text("Comorbidity: " + selectedStr);
				$('#step_item_1').show();
				showTooltip(2);
				comorb_chart_micro.setSelection(null);
				getProcess("comorb_process", results.responseText);
			}
		});
	}

	function selectComorbHandlerNV(e) {
		var selectedNV = comorb_chart_nv.getSelection();
		var nvIndexSlice = selectedNV[0].row;
		//alert('A table row was selected from Non Vascular '+nvIndexSlice+ " "+complClassJsonRawValuesNonVascular[nvIndexSlice].patient_nums);
		hideGraphs(1);
		showLoader();
		$.ajax({
			url: "./i2b2Servlet/",
			dataType:"json",
			async: true,
			data: { step: "2",
				chart_type: "comorb_process",
				selected_value:  complClassJsonRawValuesNonVascular[nvIndexSlice].patient_nums 
			},
			complete: function(results){
				$('#step_item_1 > span').text("Comorbidity: " + selectedStr);
				$('#step_item_1').show();
				showTooltip(2);
				comorb_chart_nv.setSelection(null);
				getProcess("comorb_process", results.responseText);
			}
		});
	}


}

function backToComorbidityClasses(){
	$('#comorb_chart_ex_container').hide();
	$('#comorb_chart_container').show();	
}

function getProcess(chart_type, data){
	hideLoader();
	setMenu(2);
	showGraphs(2);
	drawProcessChart(data);	
}

function hideGraphs(step){
	manageGraphs(step, false);
}

function showGraphs(step){
	manageGraphs(step, true);
}

function manageGraphs(step, show){
	var classStr = ".";
	if (step == 1) {
		classStr += "step_one";
	} 
	else if (step == 2) {
		classStr += "step_two";
	}
	else if (step == 3) {
		classStr += "step_three";
	}

	$(classStr).each(function() {
		if(show){
			$(this).show();
		}
		else{
			$(this).hide();
		}
	});
}

function setMenu(level_int){
	$(".menu_item_selected").removeClass("menu_item_selected");
	$("#menu_item_"+level_int).addClass("menu_item_selected");
}

function showLoader() {
	$('#loader').show();
}

function showTooltip(step){
	$('#menu_tooltip > div').each(function(){
		$(this).hide();
	});
	$('#step_'+step+'_tooltip').show();

}

function hideLoader() {
	$('#loader').hide();
}

function hidePatientsData(){
	$('#singlePatientDataContainer').hide();
}

function hideFilters(step){
	if(!step){
		$('.step_item').hide();
	}
	else{
		$('#step_item_'+step).hide();
	}
}

function reloadPage(){
	window.location.reload();
}

function homeClick(){
	showTooltip(1);
	hideFilters();
	hideGraphs(2);
	hideGraphs(3);
	hidePatientsData();
	showGraphs(1);
	$('#comorb_chart_ex_container').hide();
	setMenu(1);
}


var processChart;

function drawProcessChart(data){
	var FROM_MS_TO_DAY = 86400000;

	//il file in ingresso � ancora da modificare con gli attributi label e steps corretti
	var obj = jQuery.parseJSON(data);
	//var obj = jQuery.parseJSON('{"histories":[{"label":"story_1","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"out","id":"event","n_pts":"6","time":2,"idcod":"4043;5353;11567;11813;14364;17409;"}]},{"label":"story_1_1","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"193"},{"label":"Day_Hospital","id":"event","n_pts":4,"time":5,"idcod":"11769;16172;16807;18777;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_1_1_1","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"193"},{"label":"Day_Hospital","id":"event","n_pts":4,"time":5,"idcod":"11769;16172;16807;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"-57"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":1,"idcod":"16807;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_1_1_1_2","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"193"},{"label":"Day_Hospital","id":"event","n_pts":4,"time":5,"idcod":"11769;16172;16807;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"-57"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":1,"idcod":"16807;"},{"label":"wait","id":"transition","n_pts":0,"time":"1593"},{"label":"In_Hospital","id":"event","n_pts":1,"time":10,"idcod":"16807;"},{"label":"out","id":"event","n_pts":"1","time":2625,"idcod":"16807;"}]},{"label":"story_1_1_2","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"193"},{"label":"Day_Hospital","id":"event","n_pts":4,"time":5,"idcod":"11769;16172;16807;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"34"},{"label":"In_Hospital","id":"event","n_pts":3,"time":3,"idcod":"11769;16172;18777;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_1_1_2_1","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"193"},{"label":"Day_Hospital","id":"event","n_pts":4,"time":5,"idcod":"11769;16172;16807;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"34"},{"label":"In_Hospital","id":"event","n_pts":3,"time":3,"idcod":"11769;16172;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"20"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":37,"idcod":"16172;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_1_1_2_1_2","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"193"},{"label":"Day_Hospital","id":"event","n_pts":4,"time":5,"idcod":"11769;16172;16807;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"34"},{"label":"In_Hospital","id":"event","n_pts":3,"time":3,"idcod":"11769;16172;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"20"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":37,"idcod":"16172;"},{"label":"wait","id":"transition","n_pts":0,"time":"1280"},{"label":"In_Hospital","id":"event","n_pts":1,"time":11,"idcod":"16172;"},{"label":"out","id":"event","n_pts":"1","time":1451,"idcod":"16172;"}]},{"label":"story_1_1_2_2","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"193"},{"label":"Day_Hospital","id":"event","n_pts":4,"time":5,"idcod":"11769;16172;16807;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"34"},{"label":"In_Hospital","id":"event","n_pts":3,"time":3,"idcod":"11769;16172;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"-34"},{"label":"In_Hospital","id":"event","n_pts":2,"time":5,"idcod":"11769;18777;"},{"label":"out","id":"event","n_pts":"1","time":735,"idcod":"18777;"}]},{"label":"story_1_1_2_2_1","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"193"},{"label":"Day_Hospital","id":"event","n_pts":4,"time":5,"idcod":"11769;16172;16807;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"34"},{"label":"In_Hospital","id":"event","n_pts":3,"time":3,"idcod":"11769;16172;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"-34"},{"label":"In_Hospital","id":"event","n_pts":2,"time":5,"idcod":"11769;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"-124"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":24,"idcod":"11769;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_1_1_2_2_1_1","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"193"},{"label":"Day_Hospital","id":"event","n_pts":4,"time":5,"idcod":"11769;16172;16807;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"34"},{"label":"In_Hospital","id":"event","n_pts":3,"time":3,"idcod":"11769;16172;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"-34"},{"label":"In_Hospital","id":"event","n_pts":2,"time":5,"idcod":"11769;18777;"},{"label":"wait","id":"transition","n_pts":0,"time":"-124"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":24,"idcod":"11769;"},{"label":"wait","id":"transition","n_pts":0,"time":"53"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":4,"idcod":"11769;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_1_2","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"376"},{"label":"In_Hospital","id":"event","n_pts":7,"time":7,"idcod":"5121;8281;14256;19261;20855;21006;21586;"},{"label":"out","id":"event","n_pts":"2","time":1506,"idcod":"5121;21586;"}]},{"label":"story_1_2_1","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"376"},{"label":"In_Hospital","id":"event","n_pts":7,"time":7,"idcod":"5121;8281;14256;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"343"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":2,"idcod":"21006;"},{"label":"out","id":"event","n_pts":"1","time":730,"idcod":"21006;"}]},{"label":"story_1_2_2","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"376"},{"label":"In_Hospital","id":"event","n_pts":7,"time":7,"idcod":"5121;8281;14256;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"827"},{"label":"In_Hospital","id":"event","n_pts":4,"time":6,"idcod":"8281;14256;19261;20855;"},{"label":"out","id":"event","n_pts":"1","time":1514,"idcod":"14256;"}]},{"label":"story_1_2_2_1","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"376"},{"label":"In_Hospital","id":"event","n_pts":7,"time":7,"idcod":"5121;8281;14256;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"827"},{"label":"In_Hospital","id":"event","n_pts":4,"time":6,"idcod":"8281;14256;19261;20855;"},{"label":"wait","id":"transition","n_pts":0,"time":"365"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":1,"idcod":"20855;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_1_2_2_1_2","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"376"},{"label":"In_Hospital","id":"event","n_pts":7,"time":7,"idcod":"5121;8281;14256;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"827"},{"label":"In_Hospital","id":"event","n_pts":4,"time":6,"idcod":"8281;14256;19261;20855;"},{"label":"wait","id":"transition","n_pts":0,"time":"365"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":1,"idcod":"20855;"},{"label":"wait","id":"transition","n_pts":0,"time":"2183"},{"label":"In_Hospital","id":"event","n_pts":1,"time":5,"idcod":"20855;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_1_2_2_1_2_2","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"376"},{"label":"In_Hospital","id":"event","n_pts":7,"time":7,"idcod":"5121;8281;14256;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"827"},{"label":"In_Hospital","id":"event","n_pts":4,"time":6,"idcod":"8281;14256;19261;20855;"},{"label":"wait","id":"transition","n_pts":0,"time":"365"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":1,"idcod":"20855;"},{"label":"wait","id":"transition","n_pts":0,"time":"2183"},{"label":"In_Hospital","id":"event","n_pts":1,"time":5,"idcod":"20855;"},{"label":"wait","id":"transition","n_pts":0,"time":"46"},{"label":"In_Hospital","id":"event","n_pts":1,"time":14,"idcod":"20855;"},{"label":"out","id":"event","n_pts":"1","time":2805,"idcod":"20855;"}]},{"label":"story_1_2_2_2","steps":[{"label":"Day_Hospital","id":"event","n_pts":17,"time":1,"idcod":"4043;5121;5353;8281;11567;11769;11813;14256;14364;16172;16807;17409;18777;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"376"},{"label":"In_Hospital","id":"event","n_pts":7,"time":7,"idcod":"5121;8281;14256;19261;20855;21006;21586;"},{"label":"wait","id":"transition","n_pts":0,"time":"827"},{"label":"In_Hospital","id":"event","n_pts":4,"time":6,"idcod":"8281;14256;19261;20855;"},{"label":"wait","id":"transition","n_pts":0,"time":"480"},{"label":"In_Hospital","id":"event","n_pts":2,"time":13,"idcod":"8281;19261;"},{"label":"out","id":"event","n_pts":"2","time":2194,"idcod":"8281;19261;"}]},{"label":"story_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"out","id":"event","n_pts":"10","time":3,"idcod":"123;11509;14483;16809;18724;18817;20994;21375;21539;21575;"}]},{"label":"story_2_1","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"283"},{"label":"Day_Hospital","id":"event","n_pts":2,"time":17,"idcod":"7863;8387;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_2_1_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"283"},{"label":"Day_Hospital","id":"event","n_pts":2,"time":17,"idcod":"7863;8387;"},{"label":"wait","id":"transition","n_pts":0,"time":"-77"},{"label":"In_Hospital","id":"event","n_pts":2,"time":7,"idcod":"7863;8387;"},{"label":"out","id":"event","n_pts":"2","time":240,"idcod":"7863;8387;"}]},{"label":"story_2_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"out","id":"event","n_pts":"3","time":60,"idcod":"4388;18411;21585;"}]},{"label":"story_2_2_1","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"120"},{"label":"Day_Hospital","id":"event","n_pts":2,"time":5,"idcod":"12653;16000;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_2_2_1_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"120"},{"label":"Day_Hospital","id":"event","n_pts":2,"time":5,"idcod":"12653;16000;"},{"label":"wait","id":"transition","n_pts":0,"time":"396"},{"label":"In_Hospital","id":"event","n_pts":2,"time":6,"idcod":"12653;16000;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_2_2_1_2_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"120"},{"label":"Day_Hospital","id":"event","n_pts":2,"time":5,"idcod":"12653;16000;"},{"label":"wait","id":"transition","n_pts":0,"time":"396"},{"label":"In_Hospital","id":"event","n_pts":2,"time":6,"idcod":"12653;16000;"},{"label":"wait","id":"transition","n_pts":0,"time":"1840"},{"label":"In_Hospital","id":"event","n_pts":2,"time":3,"idcod":"12653;16000;"},{"label":"out","id":"event","n_pts":"2","time":2330,"idcod":"12653;16000;"}]},{"label":"story_2_2_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"104"},{"label":"In_Hospital","id":"event","n_pts":11,"time":7,"idcod":"182;212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_2_2_2_1","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"104"},{"label":"In_Hospital","id":"event","n_pts":11,"time":7,"idcod":"182;212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"402"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":26,"idcod":"182;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_2_2_2_1_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"104"},{"label":"In_Hospital","id":"event","n_pts":11,"time":7,"idcod":"182;212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"402"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":26,"idcod":"182;"},{"label":"wait","id":"transition","n_pts":0,"time":"-257"},{"label":"In_Hospital","id":"event","n_pts":1,"time":8,"idcod":"182;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_2_2_2_1_2_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"104"},{"label":"In_Hospital","id":"event","n_pts":11,"time":7,"idcod":"182;212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"402"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":26,"idcod":"182;"},{"label":"wait","id":"transition","n_pts":0,"time":"-257"},{"label":"In_Hospital","id":"event","n_pts":1,"time":8,"idcod":"182;"},{"label":"wait","id":"transition","n_pts":0,"time":"288"},{"label":"In_Hospital","id":"event","n_pts":1,"time":14,"idcod":"182;"},{"label":"out","id":"event","n_pts":"1","time":1030,"idcod":"182;"}]},{"label":"story_2_2_2_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"104"},{"label":"In_Hospital","id":"event","n_pts":11,"time":7,"idcod":"182;212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"90"},{"label":"In_Hospital","id":"event","n_pts":10,"time":10,"idcod":"212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"out","id":"event","n_pts":"4","time":2156,"idcod":"8548;14968;18254;21576;"}]},{"label":"story_2_2_2_2_1","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"104"},{"label":"In_Hospital","id":"event","n_pts":11,"time":7,"idcod":"182;212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"90"},{"label":"In_Hospital","id":"event","n_pts":10,"time":10,"idcod":"212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"291"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":23,"idcod":"212;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_2_2_2_2_1_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"104"},{"label":"In_Hospital","id":"event","n_pts":11,"time":7,"idcod":"182;212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"90"},{"label":"In_Hospital","id":"event","n_pts":10,"time":10,"idcod":"212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"291"},{"label":"Day_Hospital","id":"event","n_pts":1,"time":23,"idcod":"212;"},{"label":"wait","id":"transition","n_pts":0,"time":"1920"},{"label":"In_Hospital","id":"event","n_pts":1,"time":26,"idcod":"212;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]},{"label":"story_2_2_2_2_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"104"},{"label":"In_Hospital","id":"event","n_pts":11,"time":7,"idcod":"182;212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"90"},{"label":"In_Hospital","id":"event","n_pts":10,"time":10,"idcod":"212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"89"},{"label":"In_Hospital","id":"event","n_pts":5,"time":4,"idcod":"427;1010;5620;8024;11871;"},{"label":"out","id":"event","n_pts":"1","time":48,"idcod":"11871;"}]},{"label":"story_2_2_2_2_2_2","steps":[{"label":"In_Hospital","id":"event","n_pts":28,"time":8,"idcod":"123;182;212;427;1010;4388;5620;7863;8024;8387;8548;11509;11871;12653;14483;14968;16000;16809;18254;18411;18724;18817;20994;21375;21539;21575;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"316"},{"label":"In_Hospital","id":"event","n_pts":16,"time":13,"idcod":"182;212;427;1010;4388;5620;8024;8548;11871;12653;14968;16000;18254;18411;21576;21585;"},{"label":"wait","id":"transition","n_pts":0,"time":"104"},{"label":"In_Hospital","id":"event","n_pts":11,"time":7,"idcod":"182;212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"90"},{"label":"In_Hospital","id":"event","n_pts":10,"time":10,"idcod":"212;427;1010;5620;8024;8548;11871;14968;18254;21576;"},{"label":"wait","id":"transition","n_pts":0,"time":"89"},{"label":"In_Hospital","id":"event","n_pts":5,"time":4,"idcod":"427;1010;5620;8024;11871;"},{"label":"wait","id":"transition","n_pts":0,"time":"321"},{"label":"In_Hospital","id":"event","n_pts":4,"time":14,"idcod":"427;1010;5620;8024;"},{"label":"out","id":"event","n_pts":"0","time":0,"idcod":"0"}]}]}');
	//var obj = jQuery.parseJSON('{"histories":[{"label":"story_1","steps":[{"label":"DayHospital","id":"event","n_pts":44,"time":1,"idcod":"1382;3679;4567;5121;5353;5367;5983;6197;6416;6840;7024;7995;8333;9155;10050;10350;11065;11567;11811;11813;11814;13755;14364;15016;16504;16807;17181;17409;17921;18336;18621;19006;19287;19496;19570;19758;19966;20011;20119;21003;21006;21261;21586;21596;"},{"label":"out","id":"event","n_pts":"13","time":1,"idcod":"5353;5983;8333;11813;14364;15016;16504;17409;18336;19287;19496;20119;21003;"}]},{"label":"story_2","steps":[{"label":"InHospital","id":"event","n_pts":104,"time":6,"idcod":"123;182;212;231;427;512;752;1010;1347;2004;2255;3381;3474;4043;4067;4156;4388;4438;4516;4633;5620;5719;5774;5796;5836;5862;6039;6342;6662;6828;6906;7243;7367;7752;7863;7990;8024;8281;8370;8387;8457;8548;9136;9185;9266;9306;9737;10667;10808;10812;11018;11284;11421;11422;11509;11769;11871;11934;12609;12653;12743;13126;13529;14107;14177;14256;14483;14700;14968;15009;15034;15370;15418;15981;16000;16172;16690;16809;17262;17458;17738;17773;18254;18411;18724;18777;18817;19261;19732;19960;20046;20855;20994;21071;21179;21213;21288;21507;21539;21575;21576;21585;21591;21597;"},{"label":"out","id":"event","n_pts":"19","time":4,"idcod":"1347;4067;4156;4633;8370;9306;9737;10812;11509;12743;14483;16809;18724;18817;20994;21213;21539;21575;21597;"}]}]}');
	processJSON = obj;

	var container = document.getElementById('path_chart');
	var processChart = new google.visualization.Timeline(container);

	var dataTable = new google.visualization.DataTable();
	dataTable.addColumn({ type: 'string', id: 'Story' });
	dataTable.addColumn({ type: 'string', id: 'Step' });
	dataTable.addColumn({ type: 'number', id: 'Start' });
	dataTable.addColumn({ type: 'number', id: 'End' });

	var dtRows = 0;
	for(k=0;k<obj.histories.length;k++){
		dtRows += obj.histories[k].steps.length;
	}

	dataTable.addRows(dtRows);

	var row = 0;

	for(i=0; i<obj.histories.length;i++){
		var start = 0;
		var path = obj.histories[i];
		for(j=0;j<path.steps.length;j++){
			dataTable.setCell(row, 0, path.label);
			dataTable.setCell(row, 1, path.steps[j].label);
			dataTable.setCell(row, 2, start*FROM_MS_TO_DAY);
			dataTable.setCell(row, 3, (start+parseInt(path.steps[j].time))*FROM_MS_TO_DAY);

			path.steps[j].fb=row;

			start+=parseInt(path.steps[j].time);
			row++;
		}
	}



	var options = {
			avoidOverlappingGridLines: false
	};

	processChart.draw(dataTable, options);
	//hide x-labels
	$($('svg', $('#path_chart')).children()[2]).hide();


	google.visualization.events.addListener(processChart, 'select', selectHandler);

	function selectHandler() {
		var selected = processChart.getSelection();
		//alert('A table row was selected');

		hideGraphs(2);
		showLoader();

		var p_num_param = "";
		var history ="";
		var step ="";

		for(i=0; i<processJSON.histories.length;i++){
			var steps = processJSON.histories[i].steps;
			for(j=0;j<steps.length;j++){
				if(steps[j].fb == selected[0].Fb){        //TO-DO: da controllare perch� non funziona
					p_num_param = steps[j].idcod;
					history = processJSON.histories[i].label;
					step = steps[j].label;
					break;
				}
			}
		}

		$.ajax({
			url: "./i2b2Servlet/",
			dataType:"json",
			async: true,
			data: { step: "3",
				chart_type: "comorb",
				patient_nums: p_num_param
			},
			complete: function(results){
				$('#step_item_2 > span').text("History: " + history);
				$('#step_item_3 > span').text("Step: " + step);
				$('#step_item_2').show();
				$('#step_item_3').show();
				showTooltip(3);
				hideLoader();
				setMenu(3);
				showGraphs(3);
				drawDrillDown(results.responseText);
			}
		});
	}
}

function drawDrillDown(dataJSON){
	//COMPLICATION CHART

	var drill_down_comorb_data = new google.visualization.DataTable(dataJSON);

	var drill_down_comorb_options = {
			chartArea:{top:20,width:"80%",height:"80%"},
			legend: {position: 'right', alignment: 'center'},
			pieSliceText: 'value'
	};

	var drill_down_comorb_chart = new google.visualization.PieChart(document.getElementById('drill_down_comorb_chart'));
	drill_down_comorb_chart.draw(drill_down_comorb_data, drill_down_comorb_options);
	//setting the process source of the drill down chart
	//$('#drill_down_comorb_chart').siblings('.chart_subtitle').text(processSource);

	//TIMETOCOMPLICATION

	var ttc_data = google.visualization.arrayToDataTable([
	                                                      ['Complication', '0-6', '6-12', '12-18','18-24','24-30','30-36'],
	                                                      ['Hypertension',  7, 8,9,10,4,2],
	                                                      ['Nephropathy',  5,9,2,4,7,11],
	                                                      ['Neuropathy',  2,7,1,0,8,19],
	                                                      ['Retinopathy',  4,12,9,7,4,1]
	                                                      ]);

	var options = {
			isStacked: true,
			hAxis: {title: 'Complications', titleTextStyle: {color: 'red'}},
			vAxis: {title: 'Time in Months', titleTextStyle: {color: 'red'}},
			legend: { position: 'top', maxLines: 3 },
			bar: { groupWidth: '75%' },
			series: {0:{color: '#F0F2B8'},
				1:{color: '#D1E49F'},
				2:{color: '#9BD087'},
				3:{color: '#4CB972'},
				4:{color: '#00A957'},
				5:{color: '#008646'}
			}
	};

	var drill_down_timetocomp_chart = new google.visualization.ColumnChart(document.getElementById('drill_down_timetocomp_chart'));
	drill_down_timetocomp_chart.draw(ttc_data, options);


}

function backToProcess(){
	hideFilters(2);
	hideFilters(3);
	showTooltip(2);
	setMenu(2);
	hideGraphs(3);
	showGraphs(2);
}

function bindSearchPatient(){
	$('#searchPatientBtn').click(function(){
		var patient = $("#patientTxt").val();

		if(patient==null || patient==""){
			alert("Please inserta patient name");
		}
		else{
			hideFilters();
			hideLoader();
			hideGraphs(1);
			hideGraphs(2);
			hideGraphs(3);

			showSinglePatientData();
		}
	});
}

function showSinglePatientData(){

	//single patient page menu settings
	$("div", $("#singlePatientMenu")).each(function(){
		$(this).width($("#menu_return").width());
		$(this).height($("#menu_return").height());
	});

	//$("#patientDetailsIntro").html("<b>Charts for Patient " + $("#patientTxt").val().toUpperCase() +"</b>");

	//show status timeline
	$("#singlePatientDataContainer").show();

	selectPatientDataView($("#patientmenu1"));


}

function selectPatientDataView(caller){
	var selected = $(".singlePatientMenuItemSelected"); 

	if(selected.attr("id")!=caller.attr("id")){
		selected.removeClass("singlePatientMenuItemSelected").addClass("singlePatientMenuItem");
		$("span",selected).removeClass("singlePatientMenuItemLabelSelected").addClass("singlePatientMenuItemLabel");
		caller.removeClass("singlePatientMenuItem").addClass("singlePatientMenuItemSelected");
		$("span",caller).removeClass("singlePatientMenuItemLabel").addClass("singlePatientMenuItemLabelSelected");
	}

	if(caller.attr("id").indexOf("1")>0){
		createHba1cChart();
		//TODO
		createWeightChart();
		createDietChart();
		createLOCChart();
		createCVRChart();
		createComplicationsChart();
		createComplicationsChart2();
		$("#clinicalDataChartContainer").show();
		$("#clinicalDataChartContainer").siblings().hide();
	}
	else if(caller.attr("id").indexOf("2")>0){
		createTherapiesChart();
		//createTherapiesAdherenceChart();
		createTherapiesAdherenceChart2();
		$("#DrugChartContainer").show();
		$("#DrugChartContainer").siblings().hide();
	}

}

//function createTherapiesAdherenceChart(){
////Therapies Adherence
//$.ajax({
//url: "./i2b2Servlet/",
//dataType:"json",
//async: true,
//data: { step: "0",
//chart_type: "adherence2",
//patient_id: $("#patientTxt").val()
//},
//complete: function(results){
//var obj = $.parseJSON(results.responseText);
//var timelineData = new google.visualization.DataTable(obj.therapyData);
//var labelArray = obj.labelsArray;
//var colorsArray = [];
//var colorMap = {
//// should contain a map of category -> color for every category
//'YES'	: '#90cad2',
//'NO'	: '#9a1c1f',
//'OVER'	: '#125e84',
//'INTERRUPTION'	: '#d3d3d2',
//};
//for (var i = 0; i < labelArray.length; i++) {
////var colorAdded = colorMap[timelineData.getValue(i, 1)];
////var index = $.inArray(colorAdded, colorsArray);
////if(index < 0){
////colorsArray.push(colorAdded);
////}
//colorsArray.push(colorMap[labelArray[i]]);
//}

//var timeLineChart_options = {
//timeline: { 
//groupByRowLabel: true
//},                          
//avoidOverlappingGridLines: true,
//height: 1600,
//width: 1600,
//colors: colorsArray,
//backgroundColor: '#ffd'
//};

//var view = new google.visualization.DataView(timelineData);
//view.setColumns([0, 1, 2, 3]);
//var timeline_chart = new  google.visualization.Timeline(document.getElementById('therapyAdherenceDiv2'));
//timeline_chart.draw(view, timeLineChart_options);
////timeline_chart.draw(timelineData, timeLineChart_options);
//}

//});

//}

function createTherapiesAdherenceChart2(){
	//Therapies Adherence
	$.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: true,
		data: { step: "0",
			chart_type: "adherence3",
			patient_id: $("#patientTxt").val()
		},
		complete: function(results){
			$('.buttonTherapyDivClass').remove();
			var obj = $.parseJSON(results.responseText);
			var timelineData = new google.visualization.DataTable(obj.therapyData);
			var labelArray = obj.labelsArray;
			var atcListData = obj.atcListData;
			var colorsArray = [];
			var colorMap = {
//					'INTERRUPTION'	: '#7f9392',
//					'[0-40]'	: '#a8e9e7',
//					'[40-80]'	: '#48b5b0',
//					'[80-100]'	: '#0e8e89',
//					'OVER'	: '#226764',
					'INTERRUPTION'	: '#72818E',
					'[0-40]'	: '#B9E3E8',
					'[40-80]'	: '#61B3C6',
					'[80-100]'	: '#358FAA',
					'OVER'	: '#015E84',
			};
			for (var i = 0; i < labelArray.length; i++) {
//				var colorAdded = colorMap[timelineData.getValue(i, 1)];
//				var index = $.inArray(colorAdded, colorsArray);
//				if(index < 0){
//				colorsArray.push(colorAdded);
//				}
				colorsArray.push(colorMap[labelArray[i]]);
			}
			// Calculate height
			var rowHeight = 41;
			var chartHeight = (timelineData.getNumberOfRows() + 1) * rowHeight;

			var timeLineChart_options = {
					timeline: { 
						groupByRowLabel: true,
						rowLabelStyle: {fontName: 'MyriadPro', fontSize: 14},
						barLabelStyle: { fontName: 'MyriadPro', fontSize: 12 }
					},                          
					avoidOverlappingGridLines: true,
					height: chartHeight,
//					height: auto,
//					width: 1600,
					colors: colorsArray,
					tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
					backgroundColor: '##F5F6F7'
			};

			//Button Div
			var outerHtml = '<div class="buttonTherapyDivClass" id=outerDiv>';
			var myHtml ='<div  id=atcClassDiv>'+
			'<table class="example-table" width="75%" border="1"><tr>';
			var innerHtml='<div  id=atcListDiv>';
			for (var i = 0; i < atcListData.length; i++) {
				var myObj = atcListData[i];
				var atcList = myObj.atcList;
				var atcClass = myObj.atcClass;

				var myDivName = "atc"+i;
				var myTableName = "sub_atc"+i;
				myHtml = myHtml.concat("<td><div  class='atcItem'  id="+myDivName+" atcListLenght="+atcList.length+"><span><label style='cursor: pointer' onclick='showHideAtcs($(\"#"+myDivName+"\"))'>"+atcClass+
						"</label><input type='checkbox' id='"+myDivName+"_cb' checked onclick='handleClick(this);'/></span></div></td>");

				innerHtml = innerHtml.concat('<table id='+myTableName+' class="example-table" width="75%" border="1"><tr>');
				for (var j = 0; j < atcList.length; j++) {
					var myObj2 = atcList[j];
					var myDivName2 = myTableName+"_"+j+"_";
					innerHtml = innerHtml.concat("<td><div class='atcSelected' onclick='buildFilter($(\"#"+myDivName2+"\"))' id="+myDivName2+">"+myObj2+"</div></td>");
				}
				innerHtml = innerHtml.concat('</tr></table>');
			}
			myHtml = myHtml.concat('</tr></table></div>'); //chiudo table delle classi di farmaci
			innerHtml = innerHtml.concat("</div>"); //chiudo table dei singoli farmaci

			//outerHtml = outerHtml.concat(myHtml).concat(innerHtml).concat("<input onclick='applyFilter()' id=goFilter type='submit' value='Apply Filter' class='green-sea-flat-button'>");
			outerHtml = outerHtml.concat(myHtml).concat(innerHtml).concat("<div onclick='applyFilter()' id=goFilter class='filterBtn' align='right'>Apply Filter</div></div>");
			$('#therapyAdherenceFilterDiv').append(outerHtml);
			$('#atcListDiv').hide();
			var view = new google.visualization.DataView(timelineData);
			view.setColumns([0, 1, 2, 3]);
			var timeline_chart = new  google.visualization.Timeline(document.getElementById('therapyAdherenceDiv3'));
			timeline_chart.draw(view, timeLineChart_options);
			//timeline_chart.draw(timelineData, timeLineChart_options);
		}

	});
}

function handleClick(cb) {
	//  alert("Clicked, new value = " + cb.checked+" "+cb.name);
	var cbName = cb.id.split("_")[0];
	if(cb.checked){
		//$("#"+cbName).removeClass("atcItem").addClass("atcItemSelected");
		$("[id^=sub_"+cbName+"][id$=_]").removeClass("atcUNSelected").addClass("atcSelected");		 
		// $("#sub_"+cb.name).removeClass("atcSelected").addClass("atcUNSelected");	
	}else{
		//$("#"+cbName).removeClass("atcItemSelected").addClass("atcItem");	
		//  $("#sub_"+cb.name).removeClass("atcUNSelected").addClass("atcSelected");	
		$("[id^=sub_"+cbName+"][id$=_]").removeClass("atcSelected").addClass("atcUNSelected");	
	}
	showHideAtcs($("#"+cbName));
//	var atcListLength = $("#"+cbName).attr('atcListLenght');
//	var classes = $("[id^=sub_"+cbName+"][id$=_]").attr('class');
//	var unselected = $(".atcUNSelected"); 
//	alert("Clicked, unselected num = " + unselected.length+ " atc Count: "+atcListLength+" classes:"+classes);
//	if(caller.hasClass("atcUNSelected")){
//	//selected.removeClass("atcUNSelected").addClass("atcSelected");

//	}else if(caller.hasClass("atcSelected")){
//	caller.removeClass("atcSelected").addClass("atcUNSelected");	
//	}

//	var selected = $(".atcItemSelected"); 
//	if(selected.attr("id")!=caller.attr("id")){
//	selected.removeClass("atcItemSelected").addClass("atcItem");
//	caller.removeClass("atcItem").addClass("atcItemSelected");	
//	$('#atcListDiv').show();
//	$("#sub_"+caller.attr("id")).siblings().hide();
//	$("#sub_"+caller.attr("id")).show();

//	}

}

function createTherapiesAdherenceChart2Filtered(atcFilter){
	//Therapies Adherence
	$.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: true,
		data: { step: "0",
			chart_type: "adherence3Filtered",
			patient_id: $("#patientTxt").val(),
			atc_filter : atcFilter
		},
		complete: function(results){
			//$('.buttonTherapyDivClass').remove();
			var obj = $.parseJSON(results.responseText);
			var timelineData = new google.visualization.DataTable(obj.therapyData);
			var labelArray = obj.labelsArray;
			var atcListData = obj.atcListData;
			var colorsArray = [];
			var colorMap = {
//					'INTERRUPTION'	: '#7f9392',
//					'[0-40]'	: '#a8e9e7',
//					'[40-80]'	: '#48b5b0',
//					'[80-100]'	: '#0e8e89',
//					'OVER'	: '#226764',
					'INTERRUPTION'	: '#72818E',
					'[0-40]'	: '#B9E3E8',
					'[40-80]'	: '#61B3C6',
					'[80-100]'	: '#358FAA',
					'OVER'	: '#015E84',
			};
			for (var i = 0; i < labelArray.length; i++) {
//				var colorAdded = colorMap[timelineData.getValue(i, 1)];
//				var index = $.inArray(colorAdded, colorsArray);
//				if(index < 0){
//				colorsArray.push(colorAdded);
//				}
				colorsArray.push(colorMap[labelArray[i]]);
			}
			// Calculate height
			var rowHeight = 41;
			var chartHeight = (timelineData.getNumberOfRows() + 1) * rowHeight;
			var timeLineChart_options = {
					timeline: { 
						groupByRowLabel: true,
						rowLabelStyle: {fontName: 'MyriadPro', fontSize: 14},
						barLabelStyle: { fontName: 'MyriadPro', fontSize: 12 }
					},                          
					avoidOverlappingGridLines: true,
					height: chartHeight,
					//width: 1600,
					colors: colorsArray,
					tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
					backgroundColor: '##F5F6F7'
			};

			//Button Div
//			var outerHtml = '<div class="buttonTherapyDivClass" id=outerDiv>';
//			var myHtml ='<div class="buttonTherapyDivClass" style="height: 200" id=atcClassDiv>'+
//			'<table class="example-table" width="75%" border="1"><tr>';
//			var innerHtml='<div class="buttonTherapyDivClass"  id=atcListDiv>';
//			for (var i = 0; i < atcListData.length; i++) {
//			var myObj = atcListData[i];
//			var atcList = myObj.atcList;
//			var atcClass = myObj.atcClass;

//			var myDivName = "atc"+i;
//			var myTableName = "sub_atc"+i;
//			myHtml = myHtml.concat("<td><div  class='atcItem' onclick='showHideAtcs($(\"#"+myDivName+"\"))' id="+myDivName+">"+atcClass+"</div></td>");

//			innerHtml = innerHtml.concat('<table id='+myTableName+' class="example-table" width="75%" border="1"><tr>');
//			for (var j = 0; j < atcList.length; j++) {
//			var myObj2 = atcList[j];
//			var myDivName2 = myTableName+"_"+j;
//			innerHtml = innerHtml.concat("<td><div class='atcSelected' onclick='buildFilter($(\"#"+myDivName2+"\"))' id="+myDivName2+">"+myObj2+"</div></td>");
//			}
//			innerHtml = innerHtml.concat('</tr></table>');
//			}
//			myHtml = myHtml.concat('</tr></table></div>'); //chiudo table delle classi di farmaci
//			innerHtml = innerHtml.concat("</div>"); //chiudo table dei singoli farmaci

//			outerHtml = outerHtml.concat(myHtml).concat(innerHtml).concat("<div onclick='applyFilter()' id=goFilter>Apply Filter</div></div>");
//			$('#therapyAdherenceFilterDiv').append(outerHtml);
//			$('#atcListDiv').hide();
			var view = new google.visualization.DataView(timelineData);
			view.setColumns([0, 1, 2, 3]);
			var timeline_chart = new  google.visualization.Timeline(document.getElementById('therapyAdherenceDiv3'));
			timeline_chart.draw(view, timeLineChart_options);
			//timeline_chart.draw(timelineData, timeLineChart_options);
		}

	});
}

function buildFilter(caller){
	var checkBoxName = caller.selector.split("_")[1].concat("_cb");
	if(caller.hasClass("atcUNSelected")){
		//selected.removeClass("atcUNSelected").addClass("atcSelected");
		caller.removeClass("atcUNSelected").addClass("atcSelected");	
	}else if(caller.hasClass("atcSelected")){
		caller.removeClass("atcSelected").addClass("atcUNSelected");

		$("#"+checkBoxName).attr("checked",false);		 
	}
	var atcListLength = $("#"+caller.selector.split("_")[1]).attr('atcListLenght');
	var classes = $("[id^=sub_"+caller.selector.split("_")[1]+"][id$=_]");
	var atcUnSelectedInThisClass=0;
	for (var i = 0; i < classes.length; i++) {
		var myAtcInner = $(classes[i]).hasClass('atcUNSelected');
		if(myAtcInner){
			atcUnSelectedInThisClass++;
		}
	}
	var unselected = $(".atcUNSelected"); 
	if(atcUnSelectedInThisClass==0){
		$("#"+checkBoxName).trigger('click');	
	}
	// alert("Clicked, unselected tot num = " + unselected.length+ " atc Count: "+atcListLength+" unselected in this AtcCat:"+atcUnSelectedInThisClass);
}

function applyFilter(){
	var unselected = $(".atcUNSelected"); 
	var selected =  $(".atcSelected"); 
	var msg="";
	for (var j = 0; j < unselected.length; j++) {
		msg = msg.concat(unselected[j].textContent).concat(",");
	}
	if(selected.length==0){
		alert("Please select at least one ATC in order to view data");
	}else{
		if(unselected.length>0){
			createTherapiesAdherenceChart2Filtered(msg);
		}else{
			//alert("Please select ATC to filter");
			createTherapiesAdherenceChart2();
		}
	}

}
function showHideAtcs(caller){
	var selected = $(".atcItemSelected"); 
	if(selected.attr("id")!=caller.attr("id")){
		selected.removeClass("atcItemSelected").addClass("atcItem");
		caller.removeClass("atcItem").addClass("atcItemSelected");	
		$('#atcListDiv').show();
		$("#sub_"+caller.attr("id")).siblings().hide();
		$("#sub_"+caller.attr("id")).show();
	}
}
function createTherapiesChart(){
	//set therapies submenu css
	$('#therapyChartTab').css("margin-left",$('#patientmenu1').width()+3);

	//Therapies
	$.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: true,
		data: { step: "0",
			chart_type: "therapy",
			patient_id: $("#patientTxt").val()
		},
		complete: function(results){
			//	alert(results.responseText)
			$('.atcTitleClass').remove();
			$('.dynamicTherapyDiv').remove();
			$('.magicSquareDivClass').remove();
			var obj = $.parseJSON(results.responseText);
			var results = obj.results;
			var startYear = obj.startYear;
			var endYear = obj.endYear;
			var index;
			var myData;
			var atcClass;
			var chartData;
			var maxRoundedValue;
			//result4magicbox		
			var myDataMB = obj.result4MagicBox;
			var therapyHtml="";
			for (index = 0; index < results.length; ++index) {
				myData = results[index];
				atcClass = myData.atc_class;
				var myDivName = "thDiv_"+index;
				therapyHtml = therapyHtml.concat('<div class="atcTitleClass" >'+atcClass+'</div><div class="dynamicTherapyDiv"  id='+myDivName+'>'+atcClass+'</div>');

				var myObjMBOuter = myDataMB[index];
				var myObjMB = myObjMBOuter.atcBlob;
				var groupingClass = myObjMB.grouping_class;
				var atcClassMB = groupingClass.atc_class;
				var mediaPaz = groupingClass.csa_atc_class_paz_med;
				var mediaClass = groupingClass.csa_atc_class_med;
				var pValue = groupingClass.p_value_atc_class;
				var sign = groupingClass.sign_med_atc_class;
				var myDivNameMB = "msDiv_"+index;

				var myHtmlMB ='<div class="magicSquareDivClass" id='+myDivNameMB+'>'+
				'<div class="magicSquareItem magicSquareFirstRow"><label>patient median: '+mediaPaz+'</label></div><div class="magicSquareItem magicSquareFirstRow"><label>therapy class median: '+mediaClass+'</label></div>'+
				'<div style="clear:both;"></div>';
				if(sign<0){
					myHtmlMB = myHtmlMB.concat('<div class="magicSquareItem"><img src="images/arrow_down.png" title="Adherence lesser compared with therapy group" width="63"></img></div>');
				}else{
					myHtmlMB = myHtmlMB.concat('<div class="magicSquareItem"><img src="images/arrow_up.png" title="Adherence greater compared with therapy group" width="63"></img></div>');
				}
				if(pValue<0.05){
					myHtmlMB = myHtmlMB.concat('<div class="magicSquareItem"><img src="images/approve.png" title="significative difference between medians" width=30></img><br>p-value: '+pValue+'</div>');
				}else{
					myHtmlMB = myHtmlMB.concat('<div class="magicSquareItem"><img src="images/remove.png" title="no significative difference between medians" width=30></img><br>p-value: '+pValue+'</div>');
				}
				myHtmlMB = myHtmlMB.concat('<div style="clear:both;"></div></div>');
				therapyHtml = therapyHtml.concat(myHtmlMB);
				therapyHtml = therapyHtml.concat('<div style="clear:both;"></div>');

			}

			$('#therapyDiv').append(therapyHtml);

			//do ScatterChart
			for (index = 0; index < results.length; ++index) {
				myData = results[index];
				atcClass = myData.atc_class;
				maxRoundedValue = myData.max_value;
				chartData = myData.data;
				var scatterChartData = new google.visualization.DataTable(chartData);
				var scatterChartOptions = {
						//title: atcClass,
						tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
						vAxis: {title: "DDD", minValue: 0, maxValue: maxRoundedValue ,textStyle:{fontSize: '14',fontName: 'MyriadPro' }, titleTextStyle:{fontSize: '14',fontName: 'MyriadPro' }},
						hAxis: {viewWindow: {  min: new Date(startYear,1,1),  max: new Date(endYear,12,31)},
							textStyle:{fontSize: '14',fontName: 'MyriadPro' },
							viewWindowMode: 'explicit'},
							explorer: { actions: ['dragToZoom', 'rightClickToReset'],  maxZoomIn: .01 },
							legend : { position:"bottom", textStyle: {color: 'gray', fontName: 'MyriadPro'}},
							series: [{color: '#015E84', visibleInLegend: true}, 
							         {color: '#90C8D1', visibleInLegend: true},
							         {color: '#BC7D7D', visibleInLegend: true},
							         {color: '#D4B667', visibleInLegend: true},
							         {color: '#D18369', visibleInLegend: true},
							         {color: '#7CC3AE', visibleInLegend: true},
							         {color: '#01827C', visibleInLegend: true}]
				};
				var myDivName = "thDiv_"+index;
				var scatter_chart = new google.visualization.ScatterChart(document.getElementById(myDivName));
				scatter_chart.draw(scatterChartData, scatterChartOptions);
			}
		}
	});
}

function createHba1cChart(){
	//Hba1c
	$.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: true,
		data: { step: "0",
			chart_type: "hba1c",
			patient_id: $("#patientTxt").val()
		},
		complete: function(results){
			var scatterChartData = new google.visualization.DataTable(results.responseText);
			var scatterChartOptions = {
					//title: 'Hba1c' ,
					tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
					vAxis: { title: "Percentage", minValue: 0, textStyle:{fontSize: '14',fontName: 'MyriadPro' }, titleTextStyle:{fontSize: '14',fontName: 'MyriadPro' }},
					hAxis: {textStyle:{fontSize: '14',fontName: 'MyriadPro' }},	
					lineDashStyle: [2, 2],
					colors: ['#015E84'],
					pointSize: 5,
					pointShape: 'circle',
					legend: {position: 'none'},
					explorer: { actions: ['dragToZoom', 'rightClickToReset'],  maxZoomIn: .01 },
			};
			var scatter_chart = new google.visualization.LineChart(document.getElementById('hba1c_chart'));
			scatter_chart.draw(scatterChartData, scatterChartOptions);
		}
	});
}

function createLOCChart(){
	//Therapies Adherence
	$.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: true,
		data: { step: "0",
			chart_type: "loc",
			patient_id: $("#patientTxt").val()
		},
		complete: function(results){
			var obj = $.parseJSON(results.responseText);
			var timelineData = new google.visualization.DataTable(obj.locData);
			var labelArray = obj.locLabels;
			if(labelArray.length>0){
				var colorsArray = [];
				var colorMap = {
						'1st Level': '#7CC3AE',
						'2nd Level': '#DDBF79',
						'Stable': '#B9E3E8',
						'3rd Level': '#D18369'
				};
				for (var i = 0; i < labelArray.length; i++) {
					colorsArray.push(colorMap[labelArray[i]]);
				}

				var timeLineChart_options = {
						timeline: { 
							groupByRowLabel: true,
							rowLabelStyle: {fontName: 'MyriadPro', fontSize: 14},
							barLabelStyle: { fontName: 'MyriadPro', fontSize: 12 }
						},                          
						avoidOverlappingGridLines: true,
						height: 200,
						//width: 1600,
						colors: colorsArray,
						tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
						backgroundColor: '#E6E6E8'
				};
				var view = new google.visualization.DataView(timelineData);
				view.setColumns([0, 1, 2, 3]);
				var timeline_chart = new  google.visualization.Timeline(document.getElementById('loc_chart'));
				timeline_chart.draw(view, timeLineChart_options);
				//timeline_chart.draw(timelineData, timeLineChart_options);
			}else{
				$("#loc_chart").empty();
			}
		}
	});
}

function createWeightChart(){
	//Therapies Adherence
	$.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: true,
		data: { step: "0",
			chart_type: "weight",
			patient_id: $("#patientTxt").val()
		},
		complete: function(results){
			var obj = $.parseJSON(results.responseText);
			var timelineData = new google.visualization.DataTable(obj.weightData);
			var labelArray = obj.weightLabels;
			if(labelArray.length>0){
				var colorsArray = [];
				var colorMap = {
						'Decrease': '#B9E3E8',
						'TimeToTarget': '#358FAA'
				};
				for (var i = 0; i < labelArray.length; i++) {
					colorsArray.push(colorMap[labelArray[i]]);
				}

				var timeLineChart_options = {
						timeline: { 
							groupByRowLabel: true,
							rowLabelStyle: {fontName: 'MyriadPro', fontSize: 14},
							barLabelStyle: { fontName: 'MyriadPro', fontSize: 12 }
						},                          
						avoidOverlappingGridLines: true,
						height: 200,
						//width: 1600,
						colors: colorsArray,
						tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
						backgroundColor: '#E6E6E8'
				};
				var view = new google.visualization.DataView(timelineData);
				view.setColumns([0, 1, 2, 3]);
				var timeline_chart = new  google.visualization.Timeline(document.getElementById('weight_chart'));
				timeline_chart.draw(view, timeLineChart_options);
				//timeline_chart.draw(timelineData, timeLineChart_options);
			}else{
				$("#weight_chart").empty();
			}
		}
	});
}

function createDietChart(){
	//Therapies Adherence
	$.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: true,
		data: { step: "0",
			chart_type: "diet",
			patient_id: $("#patientTxt").val()
		},
		complete: function(results){
			var obj = $.parseJSON(results.responseText);
			var timelineData = new google.visualization.DataTable(obj.dietData);
			var labelArray = obj.dietLabels;
			if(labelArray.length>0){
				var colorsArray = [];
				var colorMap = {
						'Good': '#B9E3E8',
						'Bad': '#358FAA'
				};
				for (var i = 0; i < labelArray.length; i++) {
					colorsArray.push(colorMap[labelArray[i]]);
				}

				var timeLineChart_options = {
						timeline: { 
							groupByRowLabel: true,
							rowLabelStyle: {fontName: 'MyriadPro', fontSize: 14},
							barLabelStyle: { fontName: 'MyriadPro', fontSize: 12 }
						},                          
						avoidOverlappingGridLines: true,
						height: 200,
						//width: 1600,
						colors: colorsArray,
						tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
						backgroundColor: '#E6E6E8'
				};
				var view = new google.visualization.DataView(timelineData);
				view.setColumns([0, 1, 2, 3]);
				var timeline_chart = new  google.visualization.Timeline(document.getElementById('diet_chart'));
				timeline_chart.draw(view, timeLineChart_options);
				//timeline_chart.draw(timelineData, timeLineChart_options);
			}else{
				$("#diet_chart").empty();
			}
		}
	});
}

function createComplicationsChart(){
	//Hba1c
	$.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: true,
		data: { step: "0",
			chart_type: "complication",
			patient_id: $("#patientTxt").val()
		},

		complete: function(results){
			var scatterChartData = new google.visualization.DataTable(results.responseText);
			var scatterChartOptions = {
					//title: 'Complications' ,
					vAxis: { minValue: 0, textStyle:{fontSize: '14',fontName: 'MyriadPro' },
						ticks: [{v:1.25, f:'Non vascular'}, {v:3.75, f:'Micro'}, {v:8.75, f:'Macro'}]},
						hAxis: {textStyle:{fontSize: '14',fontName: 'MyriadPro' }},
						//	lineWidth: 0,
//						lineDashStyle: [2, 2],
//						colors: ['#4a9ecf'],
						//pointSize: 5,
						//pointShape: 'circle',
						explorer: { actions: ['dragToZoom', 'rightClickToReset'],  maxZoomIn: .01 },
						lineWidth: 0,
						pointSize: 5,
						pointShape: 'circle',
						colors: ['#015e84'],
						tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
						legend: {position: 'none'},
						annotations: {
//							boxStyle: {
//							stroke: '#888',           // Color of the box outline.
//							strokeWidth: 1,           // Thickness of the box outline.
//							rx: 0,                   // x-radius of the corner curvature.
//							ry: 0,                   // y-radius of the corner curvature.
////							gradient: {               // Attributes for linear gradient fill.
////							color1: '#fbf6a7',      // Start color for gradient.
////							color2: '#33b679',      // Finish color for gradient.
////							x1: '0%', y1: '0%',     // Where on the boundary to start and end the
////							x2: '100%', y2: '100%', // color1/color2 gradient, relative to the
////							// upper left corner of the boundary.
////							useObjectBoundingBoxUnits: true // If true, the boundary for x1, y1,
////							// x2, and y2 is the box. If false,
////							// it's the entire chart.
////							}
//							},
							textStyle: {
								fontName: 'MyriadPro',
								fontSize: 14,
								bold: false,
								italic: false,
								color: '#015e84',     // The color of the text.
							}

						},


						seriesType: "line",
						series: {1: {type: "area", isStacked: true, pointSize: 0,lineWidth:0, color:"#bae2e0", visibleInLegend: false, enableInteractivity: false, tooltip: 'none' },
							2: {type: "area", isStacked: true, pointSize: 0,lineWidth:0, color:"#e2a971", visibleInLegend: false , enableInteractivity: false, tooltip: 'none'},
							3: {type: "area", isStacked: true, pointSize: 0,lineWidth:0, color:"#e7dc71", visibleInLegend: false , enableInteractivity: false, tooltip: 'none'}}
			};
			var scatter_chart = new google.visualization.ComboChart(document.getElementById('complication_chart'));
			scatter_chart.draw(scatterChartData, scatterChartOptions);
		}
	});
}

function createComplicationsChart2(){
	//Hba1c
	$.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: true,
		data: { step: "0",
			chart_type: "complication2",
			patient_id: $("#patientTxt").val()
		},

		complete: function(results){
			var scatterChartData = new google.visualization.DataTable(results.responseText);
			var scatterChartOptions = {
					//title: 'Complications' ,
					vAxis: { minValue: 0, textStyle:{fontSize: '14',fontName: 'MyriadPro' }
			,ticks: [{v:1.25, f:''}, {v:3.75, f:''}, {v:8.75, f:''}]
					},
					hAxis: {textStyle:{fontSize: '14',fontName: 'MyriadPro' }},
					//	lineWidth: 0,
//					lineDashStyle: [2, 2],
//					colors: ['#4a9ecf'],
					//pointSize: 5,
					//pointShape: 'circle',
					explorer: { actions: ['dragToZoom', 'rightClickToReset'],  maxZoomIn: .01 },
//					lineWidth: 0,
//					pointSize: 5,
//					pointShape: 'circle',
//					colors: ['#015e84'],
					tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
					legend: {position: 'bottom'},
					annotations: {
//						boxStyle: {
//						stroke: '#888',           // Color of the box outline.
//						strokeWidth: 1,           // Thickness of the box outline.
//						rx: 0,                   // x-radius of the corner curvature.
//						ry: 0,                   // y-radius of the corner curvature.
////						gradient: {               // Attributes for linear gradient fill.
////						color1: '#fbf6a7',      // Start color for gradient.
////						color2: '#33b679',      // Finish color for gradient.
////						x1: '0%', y1: '0%',     // Where on the boundary to start and end the
////						x2: '100%', y2: '100%', // color1/color2 gradient, relative to the
////						// upper left corner of the boundary.
////						useObjectBoundingBoxUnits: true // If true, the boundary for x1, y1,
////						// x2, and y2 is the box. If false,
////						// it's the entire chart.
////						}
//						},
						textStyle: {
							fontName: 'MyriadPro',
							fontSize: 14,
							bold: false,
							italic: false,
							color: '#015e84',     // The color of the text.
						}

					},


					seriesType: "line",
					series: {
						0: {type: "line", isStacked: true, pointSize: 5,lineWidth:0, color:"#90C8D1", visibleInLegend: true },
						1: {type: "line", isStacked: true, pointSize: 5,lineWidth:0, color:"#BC7D7D", visibleInLegend: true },
						2: {type: "line", isStacked: true, pointSize: 5,lineWidth:0, color:"#D4B667", visibleInLegend: true },
						3: {type: "line", isStacked: true, pointSize: 0,lineWidth:1, color:"#015e84", visibleInLegend: false , enableInteractivity: false, tooltip: 'none'},
						4: {type: "line", isStacked: true, pointSize: 0,lineWidth:1, color:"#015e84", visibleInLegend: false , enableInteractivity: false, tooltip: 'none'},
						5: {type: "line", isStacked: true, pointSize: 0,lineWidth:1, color:"#015e84", visibleInLegend: false , enableInteractivity: false, tooltip: 'none'}
					}
			};
			var scatter_chart = new google.visualization.ComboChart(document.getElementById('complication_chart2'));
			scatter_chart.draw(scatterChartData, scatterChartOptions);
		}
	});
}

function createCVRChart(){
	//Hba1c
	$.ajax({
		url: "./i2b2Servlet/",
		dataType:"json",
		async: true,
		data: { step: "0",
			chart_type: "cvr",
			patient_id: $("#patientTxt").val()
		},
		complete: function(results){
			$('.legend').remove();
			var scatterChartData = new google.visualization.DataTable(results.responseText);
			var scatterChartOptions = {
					//title: 'CVR' ,
					vAxis: { title: "Percentage",textStyle:{fontSize: '14',fontName: 'MyriadPro'},titleTextStyle:{fontSize: '14',fontName: 'MyriadPro' }, minValue: 0, maxValue: 7},
					hAxis: {textStyle:{fontSize: '14',fontName: 'MyriadPro' }},					
					lineDashStyle: [2, 2],
					colors: ['#015e84'],
					pointSize: 5,
					pointShape: 'circle',
					height: 300,
					//width: 1000,
					explorer: { actions: ['dragToZoom', 'rightClickToReset'],  maxZoomIn: .01 },
					tooltip: { textStyle: { fontName: 'MyriadPro', fontSize: 14 } },
					legend : { position:"none"}
			};
			var scatter_chart = new google.visualization.LineChart(document.getElementById('pat_cvr_chart'));
			scatter_chart.draw(scatterChartData, scatterChartOptions);
			var myLegendHtml = '<ul class="legend"><li><span class="Irisk"></span>0 - 5%</li>'+
			'<li><span class="IIrisk"></span>5%-10%</li>'+
			'<li><span class="IIIrisk"></span>10%-15%</li>'+
			'<li><span class="IVrisk"></span>15%-20%</li>'+
			'<li><span class="Vrisk"></span>20%-30%</li>'+
			'<li><span class="VIrisk"></span>30%-100%</li></ul><div class="legend"><a href="http://www.cuore.iss.it/" target="_blank"><img src="images/progettoCuore.gif"  width="170" height="30" style="cursor: pointer;"></img></a></div>';
			$('#pat_cvr_chart_legend').append(myLegendHtml);
		}
	});
}



