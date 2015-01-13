package it.fsm.mosaic.servlet;

import it.fsm.mosaic.model.CacheMongoObject;
import it.fsm.mosaic.model.I2B2ComorbidtyObservation;
import it.fsm.mosaic.model.I2B2Observation;
import it.fsm.mosaic.model.I2B2TherapyObservation;
import it.fsm.mosaic.mongodb.MongoDbUtil;
import it.fsm.mosaic.util.DBUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

@SuppressWarnings("serial")
public class I2B2Servlet extends HttpServlet{

	private static final String observationTable = "OBSERVATION_FACT_DEV";
	private static final String observationTableOld = "OBSERVATION_FACT_OLD";
	private static final HashMap<String, Integer> complicationsMap = new HashMap<String, Integer>();

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		PrintWriter out = resp.getWriter();
		String step = req.getParameter("step");
		String chartType = req.getParameter("chart_type");

		if(step.equalsIgnoreCase("1")){
			if(chartType.equalsIgnoreCase("gender")){
				out.println(getI2B2GenderData());
			}
			else if(chartType.equalsIgnoreCase("bmi")){
				out.println(getI2B2BMIData());
			}
			else if(chartType.equalsIgnoreCase("comorbidity")){
				out.println(getI2B2ComorbidityData());
			}
			else if(chartType.equalsIgnoreCase("age_diagnosis")){
				out.println(getI2B2AgeDiagnosisData());
			}
			else if(chartType.equalsIgnoreCase("cvr")){
				out.println(getI2B2CardiovascularRiskData());
			}
		}
		else if(step.equalsIgnoreCase("2")){
			String selectedValue = req.getParameter("selected_value");
			if(chartType.equalsIgnoreCase("gender_process")){
				//String data = getI2B2dataByGender_LOC(Integer.parseInt(selectedValue));
				//String data = getI2B2dataByGender_CVR(Integer.parseInt(selectedValue));
				String data = getI2B2dataByGender_DRUG(Integer.parseInt(selectedValue));
				out.println(callProcessWS(data));
			}
			else if(chartType.equalsIgnoreCase("age_process")){
				//String data = getI2B2dataByAge_LOC(selectedValue);
				//String data = getI2B2dataByAge_CVR(selectedValue);
				String data = getI2B2dataByAge_DRUG(selectedValue);
				out.println(callProcessWS(data));
			}
			else if(chartType.equalsIgnoreCase("cvr_process")){
				//String data = getI2B2dataByCVR_LOC(Integer.parseInt(selectedValue));
				//String data = getI2B2dataByCVR_CVR(Integer.parseInt(selectedValue));
				String data = getI2B2dataByCVR_DRUG(Integer.parseInt(selectedValue));
				out.println(callProcessWS(data));
			}
			else if(chartType.equalsIgnoreCase("comorb_process")){
				//String data = getI2B2dataByComorb_LOC(selectedValue);
				//String data = getI2B2dataByComorb_CVR(selectedValue);
				String data = getI2B2dataByComorb_DRUG(selectedValue);
				out.println(callProcessWS(data));
			}
		}
		else if(step.equalsIgnoreCase("3")){
			String patientNums = req.getParameter("patient_nums");
			if(chartType.equalsIgnoreCase("comorb")){
				out.println(getI2B2DataForDrillDown(patientNums));
			}
		}else if(step.equalsIgnoreCase("0")){
			String patientId = req.getParameter("patient_id");
			if(chartType.equalsIgnoreCase("hba1c")){
				out.println(getI2B2DataForHba1c(patientId));
			}else if (chartType.equalsIgnoreCase("therapy")){
				out.println(getI2B2DataForTherapy(patientId));
			}else if (chartType.equalsIgnoreCase("adherence")){
				out.println(getI2B2DataForTherapyAdherence(patientId));
			}else if (chartType.equalsIgnoreCase("adherence2")){
				out.println(getI2B2DataForTherapyAdherence2(patientId));
			}else if (chartType.equalsIgnoreCase("diet")){
				out.println(getI2B2DataForDiet(patientId));
			}else if (chartType.equalsIgnoreCase("atcList")){
				out.println(getI2B2DataForAtcList(patientId));
			}else if (chartType.equalsIgnoreCase("adherence3")){
				out.println(getI2B2DataForTherapyAdherence3(patientId));
			}else if (chartType.equalsIgnoreCase("adherence3Filtered")){
				String atcFilter = req.getParameter("atc_filter");
				out.println(getI2B2DataForTherapyAdherence3Filtered(patientId, atcFilter));
			}else if (chartType.equalsIgnoreCase("loc")){
				out.println(getI2B2DataForLOC(patientId));
			}else if (chartType.equalsIgnoreCase("cvr")){
				out.println(getI2B2DataForCVR(patientId));
			}else if (chartType.equalsIgnoreCase("weight")){
				out.println(getI2B2DataForWeight(patientId));
			}else if (chartType.equalsIgnoreCase("complication")){
				out.println(getI2B2DataForComplication(patientId));
			}else if (chartType.equalsIgnoreCase("complication2")){
				out.println(getI2B2DataForComplication2(patientId));
			}
		}

	}

	@SuppressWarnings("unchecked")
	private String getI2B2GenderData() {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select t2.NAME_CHAR, count(t1.PATIENT_NUM) as count " +
					"from I2B2DEMODATA.OBSERVATION_FACT_OLD  t1, I2B2DEMODATA.CONCEPT_DIMENSION_OLD  t2 " +
					"where t1.CONCEPT_CD in (?,?) " +
					"and t1.CONCEPT_CD = t2.CONCEPT_CD " +
					"GROUP by t2.NAME_CHAR";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("gender_male"));
			pstmt.setString(2, prop.getProperty("gender_female"));

			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();

			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Gender");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();

			while(rs.next()){
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", rs.getString(1));
				//row_1.put("f", null);

				JSONObject row_2 = new JSONObject();
				row_2.put("v", rs.getInt(2));

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}

			obj.put("rows", rows);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();

			//	    System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}

	private String getI2B2BMIData() {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, q1.START_DATE, q1.NVAL_NUM " +
					"from I2B2DEMODATA.OBSERVATION_FACT_OLD q1 " +
					"where q1.CONCEPT_CD in (?) " +
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("bmi"));

			rs = pstmt.executeQuery();

			List<I2B2Observation> obs = new ArrayList<I2B2Observation>();

			while(rs.next()){
				I2B2Observation ob = new I2B2Observation();

				ob.setPatientNum(rs.getInt(1));
				ob.setStartDate(rs.getDate(2));
				ob.setnValNum(rs.getDouble(3));

				obs.add(ob);
			}

			jsonText = getBMIjson(obs);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2ComorbidityData(){

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);
			prop.load(input);

			//			String sql = "select t2.NAME_CHAR, count(distinct t1.PATIENT_NUM) as count " +
			//					"from I2B2DEMODATA.OBSERVATION_FACT_OLD  t1, I2B2DEMODATA.CONCEPT_DIMENSION_OLD  t2 " +
			//					"where t1.CONCEPT_CD  like (?) " +
			//					"and t1.CONCEPT_CD = t2.CONCEPT_CD " +
			//					"GROUP by t2.NAME_CHAR";
			//
			//			conn = DBUtil.getI2B2Connection();
			//			pstmt = conn.prepareStatement(sql);
			//			pstmt.setString(1, prop.getProperty("comorbidity"));
			//
			//			rs = pstmt.executeQuery();
			//
			//			JSONObject obj = new JSONObject();
			//
			//			JSONArray cols = new JSONArray();
			//
			//			JSONObject col_1 = new JSONObject();
			//			col_1.put("id", 1);
			//			col_1.put("label", "Comorbidity");
			//			col_1.put("type", "string");
			//
			//			JSONObject col_2 = new JSONObject();
			//			col_2.put("id", 2);
			//			col_2.put("label", "Count");
			//			col_2.put("type", "number");
			//
			//			cols.add(col_1);
			//			cols.add(col_2);
			//
			//			obj.put("cols", cols);
			//
			//			JSONArray rows = new JSONArray();
			//
			//			while(rs.next()){
			//				JSONArray row_arr = new JSONArray();
			//				JSONObject row_obj = new JSONObject();
			//
			//				JSONObject row_1 = new JSONObject();
			//				row_1.put("v", rs.getString(1));
			//				//row_1.put("f", null);
			//
			//				JSONObject row_2 = new JSONObject();
			//				row_2.put("v", rs.getInt(2));
			//
			//				row_arr.add(row_1);
			//				row_arr.add(row_2);
			//
			//				row_obj.put("c",row_arr);
			//				rows.add(row_obj);
			//			}
			//
			//			obj.put("rows", rows);

			//get counter for MACRO MICRO NONVASCULAR CLASS
			String sql = "select concept_cd, observation_blob, patient_num " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV t1 " +
					"where t1.CONCEPT_CD  like (?) ";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("comorbidity"));

			rs = pstmt.executeQuery();
			HashMap<String, I2B2ComorbidtyObservation> comorbidityMap = new HashMap<String, I2B2ComorbidtyObservation>();


			while(rs.next()){
				String conceptCd = rs.getString("concept_cd");
				Integer patientNumFromRS = rs.getInt("patient_num");
				String complClass = conceptCd.substring(0,6);
				I2B2ComorbidtyObservation cObs = comorbidityMap.get(conceptCd);
				if(cObs==null){ //concept_cd non presente
					//metto l'obs relativa alla singola comorbidit�
					I2B2ComorbidtyObservation cObsNew = new I2B2ComorbidtyObservation();
					List<Integer> patientNumList = new ArrayList<Integer>();
					patientNumList.add(patientNumFromRS);
					cObsNew.setComorbidityDescr(rs.getString("observation_blob"));
					cObsNew.setPatientNumList(patientNumList);
					cObsNew.setConceptCd(conceptCd);
					comorbidityMap.put(conceptCd, cObsNew);
					//metto l'obs relativa alla classe (MACRO MICRO o NONVASCULAR)					
					I2B2ComorbidtyObservation cObs4Class = comorbidityMap.get(complClass);
					if(cObs4Class==null){ //classe non presente
						I2B2ComorbidtyObservation cObsNew4Class = new I2B2ComorbidtyObservation();
						List<Integer> patientNumList4Class = new ArrayList<Integer>();
						patientNumList4Class.add(rs.getInt("patient_num"));
						if(complClass.equals("COM|MA")){
							cObsNew4Class.setComorbidityDescr("Macro");
							cObsNew4Class.setConceptCd("_Macro");
							cObsNew.setComorbClassId(0);
						}else if(complClass.equals("COM|MI")){
							cObsNew4Class.setComorbidityDescr("Micro");
							cObsNew4Class.setConceptCd("_Micro");
							cObsNew.setComorbClassId(1);
						}else if(complClass.equals("COM|NV")){
							cObsNew4Class.setComorbidityDescr("Non vascular");
							cObsNew4Class.setConceptCd("_NotVascular");
							cObsNew.setComorbClassId(2);
						}
						cObsNew4Class.setPatientNumList(patientNumList4Class);
						comorbidityMap.put(complClass, cObsNew4Class);
					}else{ //classe presente
						//controllo se c'� il patientNum
						List<Integer> patientNumList4Class2 = cObs4Class.getPatientNumList();
						if(!patientNumList4Class2.contains(patientNumFromRS)){ //se non c'� lo aggiungo
							patientNumList4Class2.add(patientNumFromRS);
						}
						//setto la classe
						if(complClass.equals("COM|MA")){
							cObsNew.setComorbClassId(0);
						}else if(complClass.equals("COM|MI")){
							cObsNew.setComorbClassId(1);
						}else if(complClass.equals("COM|NV")){
							cObsNew.setComorbClassId(2);
						}
					}	
				}else{ //concept_cd gi� presente
					//controllo il paziente
					List<Integer> patientNumList4Obs = cObs.getPatientNumList();
					if(!patientNumList4Obs.contains(patientNumFromRS)){
						patientNumList4Obs.add(patientNumFromRS);
						//controllo se il paz c'� nelle classi (potrebbe anche esserci gi�)
						I2B2ComorbidtyObservation cObs4Class = comorbidityMap.get(complClass); //la classe c'� di sicuro xke c'� il conceptcd
						List<Integer> patientNumList4Class2 = cObs4Class.getPatientNumList();
						if(!patientNumList4Class2.contains(patientNumFromRS)){ //se non c'� lo aggiungo
							patientNumList4Class2.add(patientNumFromRS);
						}
					}
					//se il paz c'� qui, c'� anche nella categoria macro (x forza, quindi non controllo nemmeno)				
				}
			}

			//Creo gli oggetti
			JSONObject objOuter = new JSONObject();
			JSONObject objComplicationClassContainer = new JSONObject();
			JSONObject objMacroContainer = new JSONObject();
			JSONObject objMicroContainer = new JSONObject();
			JSONObject objNonVascularContainer = new JSONObject();

			JSONObject objComplicationClassChartData = new JSONObject();
			JSONObject objMacroChartData = new JSONObject();
			JSONObject objMicroChartData = new JSONObject();
			JSONObject objNonVascularChartData= new JSONObject();

			JSONArray objComplicationClassRawData = new JSONArray();
			JSONArray objMacroRawData = new JSONArray();
			JSONArray objMicroRawData = new JSONArray();
			JSONArray objNonVascularRawData = new JSONArray();

			objComplicationClassContainer.put("chart_data",objComplicationClassChartData);
			objMacroContainer.put("chart_data",objMacroChartData);
			objMicroContainer.put("chart_data",objMicroChartData);
			objNonVascularContainer.put("chart_data",objNonVascularChartData);
			objComplicationClassContainer.put("raw_data",objComplicationClassRawData);
			objMacroContainer.put("raw_data",objMacroRawData);
			objMicroContainer.put("raw_data",objMicroRawData);
			objNonVascularContainer.put("raw_data",objNonVascularRawData);


			JSONArray colsClass = new JSONArray();
			JSONArray colsMacro = new JSONArray();
			JSONArray colsMicro = new JSONArray();
			JSONArray colsNotVascular = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Comorbidity");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			colsClass.add(col_1);
			colsClass.add(col_2);
			colsMacro.add(col_1);
			colsMacro.add(col_2);
			colsMicro.add(col_1);
			colsMicro.add(col_2);
			colsNotVascular.add(col_1);
			colsNotVascular.add(col_2);

			objComplicationClassChartData.put("cols", colsClass);
			objMacroChartData.put("cols", colsMacro);
			objMicroChartData.put("cols", colsMicro);
			objNonVascularChartData.put("cols", colsNotVascular);

			Set<String> keys = comorbidityMap.keySet();
			List<I2B2ComorbidtyObservation> obsClassList = new ArrayList<I2B2ComorbidtyObservation>();
			List<I2B2ComorbidtyObservation> obsList = new ArrayList<I2B2ComorbidtyObservation>();
			for(String key : keys){
				I2B2ComorbidtyObservation obs = comorbidityMap.get(key);
				if(obs.getConceptCd().startsWith("_")){
					obsClassList.add(obs);
				}else{
					obsList.add(obs);
				}
			}
			//ordine alfabetico in base a observation_blob in modo che il piechart non mi incasini le slice
			Collections.sort(obsClassList, I2B2ComorbidtyObservation.nameComparator);
			Collections.sort(obsList, I2B2ComorbidtyObservation.nameComparator);

			JSONArray rows = new JSONArray();
			for(I2B2ComorbidtyObservation ob : obsClassList){			
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", ob.getComorbidityDescr());

				JSONObject row_2 = new JSONObject();
				row_2.put("v", ob.getPatientNumList().size());

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);

				String patientList = ob.createPatientNumListString();
				JSONObject raw_data= new JSONObject();
				raw_data.put("patient_nums", patientList);
				objComplicationClassRawData.add(raw_data);

			}
			objComplicationClassChartData.put("rows", rows);

			JSONArray rowsMacro = new JSONArray();
			JSONArray rowsMicro = new JSONArray();
			JSONArray rowsNotVascular = new JSONArray();
			for(I2B2ComorbidtyObservation ob : obsList){			
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", ob.getComorbidityDescr());

				JSONObject row_2 = new JSONObject();
				row_2.put("v", ob.getPatientNumList().size());

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);

				String patientList = ob.createPatientNumListString();
				JSONObject raw_data= new JSONObject();
				raw_data.put("patient_nums", patientList);

				if(ob.getComorbClassId()==0){
					rowsMacro.add(row_obj);
					objMacroRawData.add(raw_data);
				}else if (ob.getComorbClassId()==1){
					rowsMicro.add(row_obj);
					objMicroRawData.add(raw_data);
				}else if (ob.getComorbClassId()==2){
					rowsNotVascular.add(row_obj);
					objNonVascularRawData.add(raw_data);
				}
			}

			objMacroChartData.put("rows", rowsMacro);
			objMicroChartData.put("rows", rowsMicro);
			objNonVascularChartData.put("rows", rowsNotVascular);


			objOuter.put("comorb_class", objComplicationClassContainer);
			objOuter.put("macro", objMacroContainer);
			objOuter.put("micro", objMicroContainer);
			objOuter.put("not_vascular", objNonVascularContainer);


			StringWriter out = new StringWriter();
			objOuter.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2CardiovascularRiskData(){

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select t2.NAME_CHAR, count(distinct t1.PATIENT_NUM) as count " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV  t1, I2B2DEMODATA.CONCEPT_DIMENSION  t2 " +
					"where t1.CONCEPT_CD  like (?) " +
					"and t1.CONCEPT_CD = t2.CONCEPT_CD " +
					"GROUP by t2.NAME_CHAR";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));

			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();

			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Comorbidity");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();

			while(rs.next()){
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", rs.getString(1));
				//row_1.put("f", null);

				JSONObject row_2 = new JSONObject();
				row_2.put("v", rs.getInt(2));

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}

			obj.put("rows", rows);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();

			//	    System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2AgeDiagnosisData(){

		int age_range_0_10 = 0;
		int age_range_10_20 = 0;
		int age_range_20_30 = 0;
		int age_range_30_40 = 0;
		int age_range_40_50 = 0;
		int age_range_50_60 = 0;
		int age_range_60_70 = 0;
		int age_range_70_80 = 0;
		int age_range_80_90 = 0;
		int age_range_90_100 = 0;
		int age_range_sup_100 = 0;

		String patient_num_0_10 = "";
		String patient_num_10_20 = "";
		String patient_num_20_30 = "";
		String patient_num_30_40 = "";
		String patient_num_40_50 = "";
		String patient_num_50_60 = "";
		String patient_num_60_70 = "";
		String patient_num_70_80 = "";
		String patient_num_80_90 = "";
		String patient_num_90_100 = "";
		String patient_num_sup_100 = "";

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select t2.PATIENT_NUM, t2.NVAL_NUM as \"YOD\", q1.NVAL_NUM as \"YOB\", t2.NVAL_NUM-q1.NVAL_NUM as \"AGE\" " +
					"from I2B2DEMODATA.OBSERVATION_FACT_OLD t2, " +
					"(select t1.PATIENT_NUM, t1.NVAL_NUM " +
					"from I2B2DEMODATA.OBSERVATION_FACT_OLD t1 " +
					"where t1.CONCEPT_CD like ?) q1 " + //YOB
					"where t2.PATIENT_NUM = q1.patient_num " +
					"and t2.CONCEPT_CD like ?"; //AN:Y

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("year_of_birth"));
			pstmt.setString(2, prop.getProperty("year_of_diagnosis"));

			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();
			JSONArray raw_values = new JSONArray(); 
			JSONObject chart_json = new JSONObject();

			obj.put( "raw_values", raw_values);
			obj.put("chart_json", chart_json);

			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "AgeRange");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			chart_json.put("cols", cols);

			JSONArray rows = new JSONArray();

			while(rs.next()){
				int age = rs.getInt(4);

				if(age<=10){
					age_range_0_10++;
					patient_num_0_10 = patient_num_0_10.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>10 && age<=20){
					age_range_10_20++;
					patient_num_10_20 = patient_num_10_20.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>20 && age<=30){
					age_range_20_30++;
					patient_num_20_30 = patient_num_20_30.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>30 && age<=40){
					patient_num_30_40 = patient_num_30_40.concat("-").concat(Integer.toString(rs.getInt(1)));
					age_range_30_40++;
				}
				else if(age>40 && age<=50){
					age_range_40_50++;
					patient_num_40_50 = patient_num_40_50.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>50 && age<=60){
					age_range_50_60++;
					patient_num_50_60 = patient_num_50_60.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>60 && age<=70){
					age_range_60_70++;
					patient_num_60_70 = patient_num_60_70.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>70 && age<=80){
					age_range_70_80++;
					patient_num_70_80 = patient_num_70_80.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>80 && age<=90){
					age_range_80_90++;
					patient_num_80_90 = patient_num_80_90.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>90 && age<=100){
					age_range_90_100++;
					patient_num_90_100 = patient_num_90_100.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
				else if(age>100){
					age_range_sup_100++;
					patient_num_sup_100 = patient_num_sup_100.concat("-").concat(Integer.toString(rs.getInt(1)));
				}
			}

			HashMap<String, Integer> ageMap = new HashMap<String, Integer>();
			ageMap.put("0-10", age_range_0_10);
			ageMap.put("10-20", age_range_10_20);
			ageMap.put("20-30", age_range_20_30);
			ageMap.put("30-40", age_range_30_40);
			ageMap.put("40-50", age_range_40_50);
			ageMap.put("50-60", age_range_50_60);
			ageMap.put("60-70", age_range_60_70);
			ageMap.put("70-80", age_range_70_80);
			ageMap.put("80-90", age_range_80_90);
			ageMap.put("90-100", age_range_90_100);
			ageMap.put("sup-100", age_range_sup_100);

			HashMap<String, String> patientNumsMap = new HashMap<String, String>();
			patientNumsMap.put("0-10", patient_num_0_10);
			patientNumsMap.put("10-20", patient_num_10_20);
			patientNumsMap.put("20-30", patient_num_20_30);
			patientNumsMap.put("30-40", patient_num_30_40);
			patientNumsMap.put("40-50", patient_num_40_50);
			patientNumsMap.put("50-60", patient_num_50_60);
			patientNumsMap.put("60-70", patient_num_60_70);
			patientNumsMap.put("70-80", patient_num_70_80);
			patientNumsMap.put("80-90", patient_num_80_90);
			patientNumsMap.put("90-100", patient_num_90_100);
			patientNumsMap.put("sup-100", patient_num_sup_100);

			for(int i=0; i<=10; i++){
				String key = "";

				if(i<10){
					key = (i*10)+"-"+((i+1)*10);
				}
				else{
					key ="sup-100";
				}

				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				JSONObject row_2 = new JSONObject();

				row_1.put("v", key);
				row_2.put("v", ageMap.get(key));
				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);

				JSONObject raw_data= new JSONObject();
				raw_data.put("id", i);
				raw_data.put("patient_nums", patientNumsMap.get(key));

				raw_values.add(raw_data);
			}

			chart_json.put("rows", rows);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();

			//   System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;

	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByGender_LOC(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);

			//			String sql = "select o1.PATIENT_NUM, o1.ENCOUNTER_NUM, o1.START_DATE, o1.END_DATE, o1.CONCEPT_CD, c1.NAME_CHAR " +
			//					"from I2B2DEMODATA.OBSERVATION_FACT o1, I2B2DEMODATA.CONCEPT_DIMENSION c1 " +
			//					"where o1.PATIENT_NUM in (select distinct o.PATIENT_NUM from I2B2DEMODATA.OBSERVATION_FACT o, I2B2DEMODATA.CONCEPT_DIMENSION c " +
			//					"where o.CONCEPT_CD = c.CONCEPT_CD " +
			//					"and c.CONCEPT_CD like 'PAT|SEX:"+(selectedValue+1)+"' " +
			//					"and (o1.CONCEPT_CD like ? or o1.CONCEPT_CD like ? or o1.CONCEPT_CD like ?)) " +
			//					"and o1.CONCEPT_CD = c1.CONCEPT_CD " +
			//					"ORDER by o1.PATIENT_NUM, o1.ENCOUNTER_NUM, o1.START_DATE";


			String sql ="select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year_start, "+
					//					"extract (month from q1.start_date) as h_month_start, "+
					//					"extract (day from q1.start_date) as h_day_start, "+
					//					"extract (year from q1.end_date) as h_year_end, "+
					//					"extract (month from q1.end_date) as h_month_end, "+
					//					"extract (day from q1.end_date) as h_day_end, "+
					"q1.start_date, q1.end_date, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV q1 " +
					"where q1.CONCEPT_CD like  ? " +
					"and q1.patient_num in "+
					"(select distinct q2.patient_num from I2B2DEMODATA.OBSERVATION_FACT_DEV q2 " +
					"where q2.concept_cd like 'PAT|SEX:"+(selectedValue+1)+"')"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));	
				observation.put("obs_label", rs.getString("obs_blob"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//				observation.put("value", null);
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "LOC");

			//			conn = DBUtil.getI2B2Connection();
			//			pstmt = conn.prepareStatement(sql);
			//			pstmt.setString(1, prop.getProperty("contact_details_admission"));
			//			pstmt.setString(2, prop.getProperty("contact_details_course"));
			//			pstmt.setString(3, prop.getProperty("contact_details_discharge"));
			//
			//			rs = pstmt.executeQuery();
			//
			//			int patientNum = -1;
			//			int encounterNum = -1;
			//
			//			JSONObject obj = new JSONObject();
			//			JSONArray patients = new JSONArray();
			//
			//			JSONObject patient = null;
			//			JSONArray visits = null;
			//			JSONObject visit = null;
			//
			//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			//
			//			while(rs.next()){
			//				int currentPatientNum = rs.getInt(1);
			//				int currentEncounterNum = rs.getInt(2);
			//
			//				if(patientNum!=currentPatientNum){
			//					patientNum = currentPatientNum;
			//					patient = new JSONObject();
			//					visits = new JSONArray();
			//					patient.put("patient_num", patientNum);
			//					patient.put("visits", visits);
			//
			//					patients.add(patient);
			//				}
			//				if(encounterNum!=currentEncounterNum){
			//					encounterNum = currentEncounterNum;
			//					visit = new JSONObject();
			//					visit.put("start_date", df.format(rs.getDate(3)));
			//					visit.put("end_date", df.format(rs.getDate(4)));
			//
			//					visits.add(visit);
			//				}
			//
			//
			//
			//				if(rs.getString(5).contains(prop.getProperty("contact_details_course").replace("%", ""))){
			//					visit.put("course", rs.getString(6));
			//				}
			//				else if(rs.getString(5).contains(prop.getProperty("contact_details_admission").replace("%", ""))){
			//					visit.put("admission", rs.getString(6));
			//				}
			//				else if(rs.getString(5).contains(prop.getProperty("contact_details_discharge").replace("%", ""))){
			//					visit.put("discharge", rs.getString(6));
			//				}
			//
			//			}
			//
			//			obj.put("patients", patients);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByGender_CVR(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String sql = "select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year, " +
					//					"extract (month from q1.start_date) as h_month, " +
					//					"extract (day from q1.start_date) as h_day, "+
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.NVAL_NUM as cvr_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? " +
					"and q1.patient_num in "+
					"(select distinct q2.patient_num from I2B2DEMODATA.OBSERVATION_FACT_DEV q2 " +
					"where q2.concept_cd like 'PAT|SEX:"+(selectedValue+1)+"')"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				if(rs.getDate("end_date")!=null){
					observation.put("end_date", df.format(rs.getDate("end_date")));	
				}else{
					observation.put("end_date", df.format(today));	
				}

				if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_I"))){
					observation.put("obs_label", "I");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_II"))){
					observation.put("obs_label", "II");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_III"))){
					observation.put("obs_label", "III");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_IV"))){
					observation.put("obs_label", "IV");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_V"))){
					observation.put("obs_label", "V");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_VI"))){
					observation.put("obs_label", "VI");
				}

				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getInt("cvr_value"));

				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "CVR");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByGender_DRUG(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);


		try {
			prop.load(input);
			String sql = "select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year, " +
					//					"extract (month from q1.start_date) as h_month, " +
					//					"extract (day from q1.start_date) as h_day, "+
					"q1.start_date, q1.end_date, q1.concept_cd, "+
					"q1.NVAL_NUM as ddd_value, " +
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num in "+
					"(select distinct q2.patient_num from I2B2DEMODATA.OBSERVATION_FACT_DEV q2 " +
					"where q2.concept_cd like 'PAT|SEX:"+(selectedValue+1)+"')"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				//				observation.put("end_date", null);
				observation.put("obs_label", rs.getString("atc_descr"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getDouble("ddd_value"));

				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "DRUG");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByAge_LOC(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		if(patientNums.startsWith("-")){
			patientNums = patientNums.substring(1);
		}

		try {
			prop.load(input);

			//			String sql = "select o1.PATIENT_NUM, o1.ENCOUNTER_NUM, o1.START_DATE, o1.END_DATE, o1.CONCEPT_CD, c1.NAME_CHAR " +
			//					"from I2B2DEMODATA.OBSERVATION_FACT o1, I2B2DEMODATA.CONCEPT_DIMENSION c1 " +
			//					"where o1.PATIENT_NUM in (select distinct o.PATIENT_NUM from I2B2DEMODATA.OBSERVATION_FACT o, I2B2DEMODATA.CONCEPT_DIMENSION c " +
			//					"where o.CONCEPT_CD = c.CONCEPT_CD " +
			//					"and o.PATIENT_NUM IN ("+patientNums.replaceAll("-", ",")+")" +
			//					"and (o1.CONCEPT_CD like ? or o1.CONCEPT_CD like ? or o1.CONCEPT_CD like ?)) " +
			//					"and o1.CONCEPT_CD = c1.CONCEPT_CD " +
			//					"ORDER by o1.PATIENT_NUM, o1.ENCOUNTER_NUM, o1.START_DATE";



			String sql ="select q1.PATIENT_NUM, " +

					"q1.start_date, q1.end_date, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"("+patientNums.replaceAll("-", ",")+" )"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			rs = pstmt.executeQuery();


			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));	
				observation.put("obs_label", rs.getString("obs_blob"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//				observation.put("value", null);
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "LOC");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();



			//			
			//			
			//			
			//
			//			conn = DBUtil.getI2B2Connection();
			//			pstmt = conn.prepareStatement(sql);
			//			pstmt.setString(1, prop.getProperty("contact_details_admission"));
			//			pstmt.setString(2, prop.getProperty("contact_details_course"));
			//			pstmt.setString(3, prop.getProperty("contact_details_discharge"));
			//
			//			rs = pstmt.executeQuery();
			//
			//			int patientNum = -1;
			//			int encounterNum = -1;
			//
			//			JSONObject obj = new JSONObject();
			//			JSONArray patients = new JSONArray();
			//
			//			JSONObject patient = null;
			//			JSONArray visits = null;
			//			JSONObject visit = null;
			//
			//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			//
			//			while(rs.next()){
			//				int currentPatientNum = rs.getInt(1);
			//				int currentEncounterNum = rs.getInt(2);
			//
			//				if(patientNum!=currentPatientNum){
			//					patientNum = currentPatientNum;
			//					patient = new JSONObject();
			//					visits = new JSONArray();
			//					patient.put("patient_num", patientNum);
			//					patient.put("visits", visits);
			//
			//					patients.add(patient);
			//				}
			//				if(encounterNum!=currentEncounterNum){
			//					encounterNum = currentEncounterNum;
			//					visit = new JSONObject();
			//					visit.put("start_date", df.format(rs.getDate(3)));
			//					visit.put("end_date", df.format(rs.getDate(4)));
			//
			//					visits.add(visit);
			//				}
			//
			//
			//
			//				if(rs.getString(5).contains(prop.getProperty("contact_details_course").replace("%", ""))){
			//					visit.put("course", rs.getString(6));
			//				}
			//				else if(rs.getString(5).contains(prop.getProperty("contact_details_admission").replace("%", ""))){
			//					visit.put("admission", rs.getString(6));
			//				}
			//				else if(rs.getString(5).contains(prop.getProperty("contact_details_discharge").replace("%", ""))){
			//					visit.put("discharge", rs.getString(6));
			//				}
			//
			//			}
			//
			//			obj.put("patients", patients);
			//			StringWriter out = new StringWriter();
			//			obj.writeJSONString(out);
			//			jsonText = out.toString();
			//			System.out.println(jsonText);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByAge_CVR(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		if(patientNums.startsWith("-")){
			patientNums = patientNums.substring(1);
		}

		try {
			prop.load(input);
			String sql ="select q1.PATIENT_NUM, " +

					"q1.start_date, q1.end_date,q1.concept_cd,  "+
					"q1.NVAL_NUM as cvr_value " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"("+patientNums.replaceAll("-", ",")+" )"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				if(rs.getDate("end_date")!=null){
					observation.put("end_date", df.format(rs.getDate("end_date")));	
				}else{
					observation.put("end_date", df.format(today));	
				}

				if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_I"))){
					observation.put("obs_label", "I");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_II"))){
					observation.put("obs_label", "II");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_III"))){
					observation.put("obs_label", "III");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_IV"))){
					observation.put("obs_label", "IV");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_V"))){
					observation.put("obs_label", "V");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_VI"))){
					observation.put("obs_label", "VI");
				}
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getInt("cvr_value"));
				observations.add(observation);		
			}
			obj.put("patients", patients);
			obj.put("concept", "CVR");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByAge_DRUG(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		if(patientNums.startsWith("-")){
			patientNums = patientNums.substring(1);
		}

		try {
			prop.load(input);	

			String sql ="select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date,q1.concept_cd,  "+
					"q1.NVAL_NUM as ddd_value, " +
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num in "+
					"("+patientNums.replaceAll("-", ",")+" )"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				//				observation.put("end_date", null);
				observation.put("obs_label", rs.getString("atc_descr"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getDouble("ddd_value"));
				observations.add(observation);		
			}
			obj.put("patients", patients);
			obj.put("concept", "DRUG");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByCVR_LOC(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String rcvConcept = "";
			if(selectedValue==0){
				rcvConcept = prop.getProperty("cardiovascular_risk_I");
			}else if(selectedValue==1){
				rcvConcept = prop.getProperty("cardiovascular_risk_II");
			}else if(selectedValue==2){
				rcvConcept = prop.getProperty("cardiovascular_risk_III");
			}else if(selectedValue==3){
				rcvConcept = prop.getProperty("cardiovascular_risk_IV");
			}else if(selectedValue==4){
				rcvConcept = prop.getProperty("cardiovascular_risk_V");
			}else if(selectedValue==5){
				rcvConcept = prop.getProperty("cardiovascular_risk_VI");
			}

			String sql ="select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.start_date, q1.end_date, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"(select distinct q2.patient_num from I2B2DEMODATA.OBSERVATION_FACT_DEV q2 " +
					"where q2.concept_cd like ? )"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			pstmt.setString(2, rcvConcept);
			rs = pstmt.executeQuery();
			//			String sql = "select o1.PATIENT_NUM, o1.ENCOUNTER_NUM, o1.START_DATE, o1.END_DATE, o1.CONCEPT_CD, c1.NAME_CHAR " +
			//					"from I2B2DEMODATA.OBSERVATION_FACT o1, I2B2DEMODATA.CONCEPT_DIMENSION c1 " +
			//					"where o1.PATIENT_NUM in (select distinct o.PATIENT_NUM from I2B2DEMODATA.OBSERVATION_FACT o, I2B2DEMODATA.CONCEPT_DIMENSION c " +
			//					"where o.CONCEPT_CD = c.CONCEPT_CD " +
			//					"and c.CONCEPT_CD like ? "+
			//					"and (o1.CONCEPT_CD like ? or o1.CONCEPT_CD like ? or o1.CONCEPT_CD like ?)) " +
			//					"and o1.CONCEPT_CD = c1.CONCEPT_CD " +
			//					"ORDER by o1.PATIENT_NUM, o1.ENCOUNTER_NUM, o1.START_DATE";
			//
			//			conn = DBUtil.getI2B2Connection();
			//			pstmt = conn.prepareStatement(sql);
			//			pstmt.setString(1, rcvConcept);
			//			pstmt.setString(2, prop.getProperty("contact_details_admission"));
			//			pstmt.setString(3, prop.getProperty("contact_details_course"));
			//			pstmt.setString(4, prop.getProperty("contact_details_discharge"));
			//			rs = pstmt.executeQuery();
			//			int patientNum = -1;
			//			int encounterNum = -1;
			//			JSONObject obj = new JSONObject();
			//			JSONArray patients = new JSONArray();
			//			JSONObject patient = null;
			//			JSONArray visits = null;
			//			JSONObject visit = null;
			//			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			//			while(rs.next()){
			//				int currentPatientNum = rs.getInt(1);
			//				int currentEncounterNum = rs.getInt(2);
			//
			//				if(patientNum!=currentPatientNum){
			//					patientNum = currentPatientNum;
			//					patient = new JSONObject();
			//					visits = new JSONArray();
			//					patient.put("patient_num", patientNum);
			//					patient.put("visits", visits);
			//
			//					patients.add(patient);
			//				}
			//				if(encounterNum!=currentEncounterNum){
			//					encounterNum = currentEncounterNum;
			//					visit = new JSONObject();
			//					visit.put("start_date", df.format(rs.getDate(3)));
			//					visit.put("end_date", df.format(rs.getDate(4)));
			//
			//					visits.add(visit);
			//				}
			//				if(rs.getString(5).contains(prop.getProperty("contact_details_course").replace("%", ""))){
			//					visit.put("course", rs.getString(6));
			//				}
			//				else if(rs.getString(5).contains(prop.getProperty("contact_details_admission").replace("%", ""))){
			//					visit.put("admission", rs.getString(6));
			//				}
			//				else if(rs.getString(5).contains(prop.getProperty("contact_details_discharge").replace("%", ""))){
			//					visit.put("discharge", rs.getString(6));
			//				}
			//			}
			//			obj.put("patients", patients);
			//			StringWriter out = new StringWriter();
			//			obj.writeJSONString(out);
			//			jsonText = out.toString();

			int patientNum = -1;
			int encounterNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");
				int currentEncounterNum = rs.getInt(2);

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));	
				observation.put("obs_label", rs.getString("obs_blob"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//				observation.put("value", null);
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "LOC");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByCVR_CVR(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String rcvConcept = "";
			if(selectedValue==0){
				rcvConcept = prop.getProperty("cardiovascular_risk_I");
			}else if(selectedValue==1){
				rcvConcept = prop.getProperty("cardiovascular_risk_II");
			}else if(selectedValue==2){
				rcvConcept = prop.getProperty("cardiovascular_risk_III");
			}else if(selectedValue==3){
				rcvConcept = prop.getProperty("cardiovascular_risk_IV");
			}else if(selectedValue==4){
				rcvConcept = prop.getProperty("cardiovascular_risk_V");
			}else if(selectedValue==5){
				rcvConcept = prop.getProperty("cardiovascular_risk_VI");
			}

			String sql ="select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year_start, "+
					//					"extract (month from q1.start_date) as h_month_start, "+
					//					"extract (day from q1.start_date) as h_day_start, "+
					//					"extract (year from q1.end_date) as h_year_end, "+
					//					"extract (month from q1.end_date) as h_month_end, "+
					//					"extract (day from q1.end_date) as h_day_end, "+
					"q1.start_date, q1.end_date, q1.concept_cd,"+
					"q1.NVAL_NUM as cvr_value " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"(select distinct q2.patient_num from I2B2DEMODATA.OBSERVATION_FACT_DEV q2 " +
					"where q2.concept_cd like ? )"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));
			pstmt.setString(2, rcvConcept);
			rs = pstmt.executeQuery();	

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				if(rs.getDate("end_date")!=null){
					observation.put("end_date", df.format(rs.getDate("end_date")));	
				}else{
					observation.put("end_date", df.format(today));	
				}
				if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_I"))){
					observation.put("obs_label", "I");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_II"))){
					observation.put("obs_label", "II");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_III"))){
					observation.put("obs_label", "III");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_IV"))){
					observation.put("obs_label", "IV");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_V"))){
					observation.put("obs_label", "V");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_VI"))){
					observation.put("obs_label", "VI");
				}
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getInt("cvr_value"));
				observations.add(observation);

			}
			obj.put("patients", patients);
			obj.put("concept", "CVR");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByCVR_DRUG(int selectedValue){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);
			String rcvConcept = "";
			if(selectedValue==0){
				rcvConcept = prop.getProperty("cardiovascular_risk_I");
			}else if(selectedValue==1){
				rcvConcept = prop.getProperty("cardiovascular_risk_II");
			}else if(selectedValue==2){
				rcvConcept = prop.getProperty("cardiovascular_risk_III");
			}else if(selectedValue==3){
				rcvConcept = prop.getProperty("cardiovascular_risk_IV");
			}else if(selectedValue==4){
				rcvConcept = prop.getProperty("cardiovascular_risk_V");
			}else if(selectedValue==5){
				rcvConcept = prop.getProperty("cardiovascular_risk_VI");
			}

			String sql ="select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date,q1.concept_cd,  "+
					"q1.NVAL_NUM as ddd_value, " +
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num in "+
					"(select distinct q2.patient_num from I2B2DEMODATA.OBSERVATION_FACT_DEV q2 " +
					"where q2.concept_cd like ? )"+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, rcvConcept);
			rs = pstmt.executeQuery();	

			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				//				observation.put("end_date", null);
				observation.put("obs_label", rs.getString("atc_descr"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getDouble("ddd_value"));	
				observations.add(observation);

			}
			obj.put("patients", patients);
			obj.put("concept", "DRUG");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByComorb_LOC(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);
		String patientNumsMod = patientNums.substring(0, patientNums.length()-1);
		try {
			prop.load(input);

			String sql ="select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.start_date, q1.end_date, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"("+patientNumsMod.replaceAll("-", ",")+" ) "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			rs = pstmt.executeQuery();


			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				observation.put("end_date", df.format(rs.getDate("end_date")));	
				observation.put("obs_label", rs.getString("obs_blob"));
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				//				observation.put("value", null);
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "LOC");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//			System.out.println("LOC from Comorbidity");
			//			System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2dataByComorb_CVR(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);
		String patientNumsMod = patientNums.substring(0, patientNums.length()-1);
		try {
			prop.load(input);
			String sql ="select q1.PATIENT_NUM, " +
					//					"extract (year from q1.start_date) as h_year_start, "+
					//					"extract (month from q1.start_date) as h_month_start, "+
					//					"extract (day from q1.start_date) as h_day_start, "+
					//					"extract (year from q1.end_date) as h_year_end, "+
					//					"extract (month from q1.end_date) as h_month_end, "+
					//					"extract (day from q1.end_date) as h_day_end, "+
					"q1.start_date, q1.end_date, q1.concept_cd,"+
					"q1.NVAL_NUM as cvr_value " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num in "+
					"("+patientNumsMod.replaceAll("-", ",")+" ) "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				if(rs.getDate("end_date")!=null){
					observation.put("end_date", df.format(rs.getDate("end_date")));	
				}else{
					observation.put("end_date", df.format(today));	
				}
				if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_I"))){
					observation.put("obs_label", "I");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_II"))){
					observation.put("obs_label", "II");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_III"))){
					observation.put("obs_label", "III");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_IV"))){
					observation.put("obs_label", "IV");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_V"))){
					observation.put("obs_label", "V");
				}else if(rs.getString("concept_cd").equals(prop.getProperty("cardiovascular_risk_VI"))){
					observation.put("obs_label", "VI");
				}
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getInt("cvr_value"));
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "CVR");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//			System.out.println("LOC from Comorbidity");
			//			System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}
	
	@SuppressWarnings("unchecked")
	private String getI2B2dataByComorb_DRUG(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);
		String patientNumsMod = patientNums.substring(0, patientNums.length()-1);
		try {
			prop.load(input);
			
			String sql ="select q1.PATIENT_NUM, " +
					"q1.start_date, q1.end_date,q1.concept_cd,  "+
					"q1.NVAL_NUM as ddd_value, " +
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num in "+
					"("+patientNumsMod.replaceAll("-", ",")+" ) "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";
			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			int patientNum = -1;
			JSONObject obj = new JSONObject();
			JSONArray patients = new JSONArray();
			JSONObject patient = null;
			JSONArray observations = null;
			JSONObject observation = null;
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			java.util.Date today = new java.util.Date();
			while(rs.next()){
				int currentPatientNum = rs.getInt("PATIENT_NUM");

				if(patientNum!=currentPatientNum){
					patientNum = currentPatientNum;
					patient = new JSONObject();
					observations = new JSONArray();
					patient.put("patient_num", patientNum);
					patient.put("observations", observations);
					patients.add(patient);
				}
				observation = new JSONObject();
				observation.put("start_date", df.format(rs.getDate("start_date")));
				//				observation.put("end_date", null);
				observation.put("obs_label", rs.getString("atc_descr"));	
				//				observation.put("start_label", null);
				//				observation.put("end_label", null);
				observation.put("value", rs.getDouble("ddd_value"));	
				observations.add(observation);
			}
			obj.put("patients", patients);
			obj.put("concept", "DRUG");
			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();
			//			System.out.println("LOC from Comorbidity");
			System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}
	

	@SuppressWarnings("unchecked")
	private String getBMIjson(List<I2B2Observation> obs) throws IOException {
		List<Integer> patientNumList = new ArrayList<Integer>();

		HashMap<String, Integer[]> rangeTable = new HashMap<String, Integer[]>();

		int min_inf_14_range = 0;
		int min_14_15_range = 0;
		int min_15_16_range = 0;
		int min_16_17_range = 0;
		int min_17_18_range = 0;
		int min_18_19_range = 0;
		int min_19_20_range = 0;
		int min_20_21_range = 0;
		int min_21_22_range = 0;
		int min_22_23_range = 0;
		int min_23_24_range = 0;
		int min_24_25_range = 0;
		int min_25_26_range = 0;
		int min_26_27_range = 0;
		int min_27_28_range = 0;
		int min_28_29_range = 0;
		int min_29_30_range = 0;
		int min_30_31_range = 0;
		int min_31_32_range = 0;
		int min_32_33_range = 0;
		int min_33_34_range = 0;
		int min_34_35_range = 0;
		int min_35_36_range = 0;
		int min_36_37_range = 0;
		int min_37_38_range = 0;
		int min_38_39_range = 0;
		int min_39_40_range = 0;
		int min_40_41_range = 0;
		int min_41_42_range = 0;
		int min_42_43_range = 0;
		int min_43_44_range = 0;
		int min_44_45_range = 0;
		int min_45_46_range = 0;
		int min_46_47_range = 0;
		int min_47_48_range = 0;
		int min_48_49_range = 0;
		int min_49_50_range = 0;
		int min_50_51_range = 0;
		int min_51_52_range = 0;
		int min_52_53_range = 0;
		int min_53_54_range = 0;
		int min_54_55_range = 0;
		int min_sup_55_range = 0;

		int max_inf_14_range = 0;
		int max_14_15_range = 0;
		int max_15_16_range = 0;
		int max_16_17_range = 0;
		int max_17_18_range = 0;
		int max_18_19_range = 0;
		int max_19_20_range = 0;
		int max_20_21_range = 0;
		int max_21_22_range = 0;
		int max_22_23_range = 0;
		int max_23_24_range = 0;
		int max_24_25_range = 0;
		int max_25_26_range = 0;
		int max_26_27_range = 0;
		int max_27_28_range = 0;
		int max_28_29_range = 0;
		int max_29_30_range = 0;
		int max_30_31_range = 0;
		int max_31_32_range = 0;
		int max_32_33_range = 0;
		int max_33_34_range = 0;
		int max_34_35_range = 0;
		int max_35_36_range = 0;
		int max_36_37_range = 0;
		int max_37_38_range = 0;
		int max_38_39_range = 0;
		int max_39_40_range = 0;
		int max_40_41_range = 0;
		int max_41_42_range = 0;
		int max_42_43_range = 0;
		int max_43_44_range = 0;
		int max_44_45_range = 0;
		int max_45_46_range = 0;
		int max_46_47_range = 0;
		int max_47_48_range = 0;
		int max_48_49_range = 0;
		int max_49_50_range = 0;
		int max_50_51_range = 0;
		int max_51_52_range = 0;
		int max_52_53_range = 0;
		int max_53_54_range = 0;
		int max_54_55_range = 0;
		int max_sup_55_range = 0;

		Date minDate = null;
		Date maxDate = null;
		double minValue = -1;
		double maxValue = -1;
		int prevPatientNum = -1;

		JSONObject obj = new JSONObject();

		JSONArray cols = new JSONArray();

		JSONObject col_1 = new JSONObject();
		col_1.put("id", 1);
		col_1.put("label", "BMI_Range");
		col_1.put("type", "string");

		JSONObject col_2 = new JSONObject();
		col_2.put("id", 2);
		col_2.put("label", "Baseline");
		col_2.put("type", "number");

		JSONObject col_3 = new JSONObject();
		col_3.put("id", 3);
		col_3.put("label", "LastVisit");
		col_3.put("type", "number");

		cols.add(col_1);
		cols.add(col_2);
		cols.add(col_3);

		obj.put("cols", cols);

		JSONArray rows = new JSONArray();

		for(I2B2Observation ob : obs){
			if(!patientNumList.contains(ob.getPatientNum()) || (obs.indexOf(ob)==obs.size()-1)){
				patientNumList.add(ob.getPatientNum());

				if(prevPatientNum>0){
					//MIN RANGES
					if(minValue<=14){
						min_inf_14_range++;
					}
					else if(14<minValue && minValue <= 15){
						min_14_15_range++;
					}
					else if(15<minValue && minValue <= 16){
						min_15_16_range++;
					}
					else if(16<minValue && minValue <= 17){
						min_16_17_range++;
					}
					else if(17<minValue && minValue <= 18){
						min_17_18_range++;
					}
					else if(18<minValue && minValue <= 19){
						min_18_19_range++;
					}
					else if(19<minValue && minValue <= 20){
						min_19_20_range++;
					}
					else if(20<minValue && minValue <= 21){
						min_20_21_range++;
					}
					else if(21<minValue && minValue <= 22){
						min_21_22_range++;
					}
					else if(22<minValue && minValue <= 23){
						min_22_23_range++;
					}
					else if(23<minValue && minValue <= 24){
						min_23_24_range++;
					}
					else if(24<minValue && minValue <= 25){
						min_24_25_range++;
					}
					else if(25<minValue && minValue <= 26){
						min_25_26_range++;
					}
					else if(26<minValue && minValue <= 27){
						min_26_27_range++;
					}
					else if(27<minValue && minValue <= 28){
						min_27_28_range++;
					}
					else if(28<minValue && minValue <= 29){
						min_28_29_range++;
					}
					else if(29<minValue && minValue <= 30){
						min_29_30_range++;
					}
					else if(30<minValue && minValue <= 31){
						min_30_31_range++;
					}
					else if(31<minValue && minValue <= 32){
						min_31_32_range++;
					}
					else if(32<minValue && minValue <= 33){
						min_32_33_range++;
					}
					else if(33<minValue && minValue <= 34){
						min_33_34_range++;
					}
					else if(34<minValue && minValue <= 35){
						min_34_35_range++;
					}
					else if(35<minValue && minValue <= 36){
						min_35_36_range++;
					}
					else if(36<minValue && minValue <= 37){
						min_36_37_range++;
					}
					else if(37<minValue && minValue <= 38){
						min_37_38_range++;
					}
					else if(38<minValue && minValue <= 39){
						min_38_39_range++;
					}
					else if(39<minValue && minValue <= 40){
						min_39_40_range++;
					}
					else if(40<minValue && minValue <= 41){
						min_40_41_range++;
					}
					else if(41<minValue && minValue <= 42){
						min_41_42_range++;
					}
					else if(42<minValue && minValue <= 43){
						min_42_43_range++;
					}
					else if(43<minValue && minValue <= 44){
						min_43_44_range++;
					}
					else if(44<minValue && minValue <= 45){
						min_44_45_range++;
					}
					else if(45<minValue && minValue <= 46){
						min_45_46_range++;
					}
					else if(46<minValue && minValue <= 47){
						min_46_47_range++;
					}
					else if(47<minValue && minValue <= 48){
						min_47_48_range++;
					}
					else if(48<minValue && minValue <= 49){
						min_48_49_range++;
					}
					else if(49<minValue && minValue <= 50){
						min_49_50_range++;
					}
					else if(50<minValue && minValue <= 51){
						min_50_51_range++;
					}
					else if(51<minValue && minValue <= 52){
						min_51_52_range++;
					}
					else if(52<minValue && minValue <= 53){
						min_52_53_range++;
					}
					else if(53<minValue && minValue <= 54){
						min_53_54_range++;
					}
					else if(54<minValue && minValue <= 55){
						min_54_55_range++;
					}
					else if(55<minValue ){
						min_sup_55_range++;
					}

					//MAX RANGES
					if(maxValue<=14){
						max_inf_14_range++;
					}
					else if(14<maxValue && maxValue <= 15){
						max_14_15_range++;
					}
					else if(15<maxValue && maxValue <= 16){
						max_15_16_range++;
					}
					else if(16<maxValue && maxValue <= 17){
						max_16_17_range++;
					}
					else if(17<maxValue && maxValue <= 18){
						max_17_18_range++;
					}
					else if(18<maxValue && maxValue <= 19){
						max_18_19_range++;
					}
					else if(19<maxValue && maxValue <= 20){
						max_19_20_range++;
					}
					else if(20<maxValue && maxValue <= 21){
						max_20_21_range++;
					}
					else if(21<maxValue && maxValue <= 22){
						max_21_22_range++;
					}
					else if(22<maxValue && maxValue <= 23){
						max_22_23_range++;
					}
					else if(23<maxValue && maxValue <= 24){
						max_23_24_range++;
					}
					else if(24<maxValue && maxValue <= 25){
						max_24_25_range++;
					}
					else if(25<maxValue && maxValue <= 26){
						max_25_26_range++;
					}
					else if(26<maxValue && maxValue <= 27){
						max_26_27_range++;
					}
					else if(27<maxValue && maxValue <= 28){
						max_27_28_range++;
					}
					else if(28<maxValue && maxValue <= 29){
						max_28_29_range++;
					}
					else if(29<maxValue && maxValue <= 30){
						max_29_30_range++;
					}
					else if(30<maxValue && maxValue <= 31){
						max_30_31_range++;
					}
					else if(31<maxValue && maxValue <= 32){
						max_31_32_range++;
					}
					else if(32<maxValue && maxValue <= 33){
						max_32_33_range++;
					}
					else if(33<maxValue && maxValue <= 34){
						max_33_34_range++;
					}
					else if(34<maxValue && maxValue <= 35){
						max_34_35_range++;
					}
					else if(35<maxValue && maxValue <= 36){
						max_35_36_range++;
					}
					else if(36<maxValue && maxValue <= 37){
						max_36_37_range++;
					}
					else if(37<maxValue && maxValue <= 38){
						max_37_38_range++;
					}
					else if(38<maxValue && maxValue <= 39){
						max_38_39_range++;
					}
					else if(39<maxValue && maxValue <= 40){
						max_39_40_range++;
					}
					else if(40<maxValue && maxValue <= 41){
						max_40_41_range++;
					}
					else if(41<maxValue && maxValue <= 42){
						max_41_42_range++;
					}
					else if(42<maxValue && maxValue <= 43){
						max_42_43_range++;
					}
					else if(43<maxValue && maxValue <= 44){
						max_43_44_range++;
					}
					else if(44<maxValue && maxValue <= 45){
						max_44_45_range++;
					}
					else if(45<maxValue && maxValue <= 46){
						max_45_46_range++;
					}
					else if(46<maxValue && maxValue <= 47){
						max_46_47_range++;
					}
					else if(47<maxValue && maxValue <= 48){
						max_47_48_range++;
					}
					else if(48<maxValue && maxValue <= 49){
						max_48_49_range++;
					}
					else if(49<maxValue && maxValue <= 50){
						max_49_50_range++;
					}
					else if(50<maxValue && maxValue <= 51){
						max_50_51_range++;
					}
					else if(51<maxValue && maxValue <= 52){
						max_51_52_range++;
					}
					else if(52<maxValue && maxValue <= 53){
						max_52_53_range++;
					}
					else if(53<maxValue && maxValue <= 54){
						max_53_54_range++;
					}
					else if(54<maxValue && maxValue <= 55){
						max_54_55_range++;
					}
					else if(55<maxValue ){
						max_sup_55_range++;
					}
				}

				minDate = ob.getStartDate();
				maxDate = ob.getStartDate();
				minValue = ob.getnValNum();
				maxValue = ob.getnValNum();

			}

			if(ob.getStartDate().compareTo(minDate)<0){
				minDate = ob.getStartDate();
				minValue = ob.getnValNum();
			}
			else if(ob.getStartDate().compareTo(maxDate)>0){
				maxDate = ob.getStartDate();
				maxValue = ob.getnValNum();
			}

			prevPatientNum = ob.getPatientNum();
		}

		rangeTable.put("inf-14", new Integer[]{min_inf_14_range, max_inf_14_range});
		rangeTable.put("14-15", new Integer[]{min_14_15_range, max_14_15_range});
		rangeTable.put("15-16", new Integer[]{min_15_16_range, max_15_16_range});
		rangeTable.put("16-17", new Integer[]{min_16_17_range, max_16_17_range});
		rangeTable.put("17-18", new Integer[]{min_17_18_range, max_17_18_range});
		rangeTable.put("18-19", new Integer[]{min_18_19_range, max_18_19_range});
		rangeTable.put("19-20", new Integer[]{min_19_20_range, max_19_20_range});
		rangeTable.put("20-21", new Integer[]{min_20_21_range, max_20_21_range});
		rangeTable.put("21-22", new Integer[]{min_21_22_range, max_21_22_range});
		rangeTable.put("22-23", new Integer[]{min_22_23_range, max_22_23_range});
		rangeTable.put("23-24", new Integer[]{min_23_24_range, max_23_24_range});
		rangeTable.put("24-25", new Integer[]{min_24_25_range, max_24_25_range});
		rangeTable.put("25-26", new Integer[]{min_25_26_range, max_25_26_range});
		rangeTable.put("26-27", new Integer[]{min_26_27_range, max_26_27_range});
		rangeTable.put("27-28", new Integer[]{min_27_28_range, max_27_28_range});
		rangeTable.put("28-29", new Integer[]{min_28_29_range, max_28_29_range});
		rangeTable.put("29-30", new Integer[]{min_29_30_range, max_29_30_range});
		rangeTable.put("30-31", new Integer[]{min_30_31_range, max_30_31_range});
		rangeTable.put("31-32", new Integer[]{min_31_32_range, max_31_32_range});
		rangeTable.put("32-33", new Integer[]{min_32_33_range, max_32_33_range});
		rangeTable.put("33-34", new Integer[]{min_33_34_range, max_33_34_range});
		rangeTable.put("34-35", new Integer[]{min_34_35_range, max_34_35_range});
		rangeTable.put("35-36", new Integer[]{min_35_36_range, max_35_36_range});
		rangeTable.put("36-37", new Integer[]{min_36_37_range, max_36_37_range});
		rangeTable.put("37-38", new Integer[]{min_37_38_range, max_37_38_range});
		rangeTable.put("38-39", new Integer[]{min_38_39_range, max_38_39_range});
		rangeTable.put("39-40", new Integer[]{min_39_40_range, max_39_40_range});
		rangeTable.put("40-41", new Integer[]{min_40_41_range, max_40_41_range});
		rangeTable.put("41-42", new Integer[]{min_41_42_range, max_41_42_range});
		rangeTable.put("42-43", new Integer[]{min_42_43_range, max_42_43_range});
		rangeTable.put("43-44", new Integer[]{min_43_44_range, max_43_44_range});
		rangeTable.put("44-45", new Integer[]{min_44_45_range, max_44_45_range});
		rangeTable.put("45-46", new Integer[]{min_45_46_range, max_45_46_range});
		rangeTable.put("46-47", new Integer[]{min_46_47_range, max_46_47_range});
		rangeTable.put("47-48", new Integer[]{min_47_48_range, max_47_48_range});
		rangeTable.put("48-49", new Integer[]{min_48_49_range, max_48_49_range});
		rangeTable.put("49-50", new Integer[]{min_49_50_range, max_49_50_range});
		rangeTable.put("50-51", new Integer[]{min_50_51_range, max_50_51_range});
		rangeTable.put("51-52", new Integer[]{min_51_52_range, max_51_52_range});
		rangeTable.put("52-53", new Integer[]{min_52_53_range, max_52_53_range});
		rangeTable.put("53-54", new Integer[]{min_53_54_range, max_53_54_range});
		rangeTable.put("54-55", new Integer[]{min_54_55_range, max_54_55_range});
		rangeTable.put("sup-55", new Integer[]{min_sup_55_range, max_sup_55_range});

		for(int i=13; i<=55; i++){
			JSONArray row_arr = new JSONArray();
			JSONObject row_obj = new JSONObject();
			JSONObject row_1 = new JSONObject();
			JSONObject row_2 = new JSONObject();
			JSONObject row_3 = new JSONObject();

			String key = "";

			if(i==13){
				key = "inf-14";	
			}
			else if(i==55){
				key = "sup-55";			
			}
			else{
				key = i+"-"+(i+1);
			}
			//System.out.println("key: " + key);
			row_1.put("v", key);
			row_2.put("v", rangeTable.get(key)[0]);
			row_3.put("v", rangeTable.get(key)[1]);

			row_arr.add(row_1);
			row_arr.add(row_2);
			row_arr.add(row_3);
			row_obj.put("c",row_arr);
			rows.add(row_obj);
		}

		obj.put("rows", rows);

		StringWriter out = new StringWriter();
		obj.writeJSONString(out);

		return out.toString();
	}

	private String getI2B2DataForDrillDown(String patientNums){

		String comorbChartJSON = getI2B2ComorbByPatientNum(patientNums);
		String timeToComorbJSON = getI2b2TimeToComorbByPatientNum(patientNums);

		return comorbChartJSON;
	}

	@SuppressWarnings("unchecked")
	private String getI2B2ComorbByPatientNum(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select t2.NAME_CHAR, count(distinct t1.PATIENT_NUM) as count " +
					"from I2B2DEMODATA.OBSERVATION_FACT  t1, I2B2DEMODATA.CONCEPT_DIMENSION  t2 " +
					"where t1.CONCEPT_CD  like (?) " +
					"and t1.PATIENT_NUM in ("+ patientNums.substring(0,patientNums.lastIndexOf(";")).replaceAll(";", ",")+")"+
					"and t1.CONCEPT_CD = t2.CONCEPT_CD " +
					"GROUP by t2.NAME_CHAR";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("comorbidity"));

			rs = pstmt.executeQuery();

			JSONObject obj = new JSONObject();

			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Comorbidity");
			col_1.put("type", "string");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Count");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();

			while(rs.next()){
				JSONArray row_arr = new JSONArray();
				JSONObject row_obj = new JSONObject();

				JSONObject row_1 = new JSONObject();
				row_1.put("v", rs.getString(1));
				//row_1.put("f", null);

				JSONObject row_2 = new JSONObject();
				row_2.put("v", rs.getInt(2));

				row_arr.add(row_1);
				row_arr.add(row_2);

				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}

			obj.put("rows", rows);

			StringWriter out = new StringWriter();
			obj.writeJSONString(out);
			jsonText = out.toString();

			//	    System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}

	private String getI2b2TimeToComorbByPatientNum(String patientNums){
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select * from (select o.PATIENT_NUM, c.NAME_CHAR, min(o.START_DATE), 2 as ord " +
					"from I2B2DEMODATA.OBSERVATION_FACT o, I2B2DEMODATA.CONCEPT_DIMENSION c " +
					"where o.PATIENT_NUM in ("+patientNums.substring(0,patientNums.lastIndexOf(";")).replaceAll(";", ",")+") " +
					"and o.CONCEPT_CD = c.CONCEPT_CD " +
					"and c.CONCEPT_CD like ? " +
					"group by o.PATIENT_NUM, c.NAME_CHAR " +
					"union " +
					"select o.PATIENT_NUM, c.NAME_CHAR, o.START_DATE, 1 as ord " +
					"from I2B2DEMODATA.OBSERVATION_FACT o, I2B2DEMODATA.CONCEPT_DIMENSION c " +
					"where o.PATIENT_NUM in ("+patientNums.substring(0,patientNums.lastIndexOf(";")).replaceAll(";", ",")+") " +
					"and o.CONCEPT_CD = c.CONCEPT_CD " +
					"and c.CONCEPT_CD like ?) q1 " +
					"order by q1.PATIENT_NUM, Q1.ord";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("comorbidity"));
			pstmt.setString(2, prop.getProperty("year_of_diagnosis"));

			rs = pstmt.executeQuery();

			//			JSONObject obj = new JSONObject();
			//			
			//			JSONArray cols = new JSONArray();
			//			
			//			JSONObject col_1 = new JSONObject();
			//			col_1.put("id", 1);
			//			col_1.put("label", "Comorbidity");
			//			col_1.put("type", "string");
			//			
			//			JSONObject col_2 = new JSONObject();
			//			col_2.put("id", 2);
			//			col_2.put("label", "Count");
			//			col_2.put("type", "number");
			//			
			//			cols.add(col_1);
			//			cols.add(col_2);
			//			
			//			obj.put("cols", cols);
			//			
			//			JSONArray rows = new JSONArray();
			//			
			//			while(rs.next()){
			//				JSONArray row_arr = new JSONArray();
			//				JSONObject row_obj = new JSONObject();
			//				
			//				JSONObject row_1 = new JSONObject();
			//				row_1.put("v", rs.getString(1));
			//				//row_1.put("f", null);
			//				
			//				JSONObject row_2 = new JSONObject();
			//				row_2.put("v", rs.getInt(2));
			//				
			//				row_arr.add(row_1);
			//				row_arr.add(row_2);
			//				
			//				row_obj.put("c",row_arr);
			//				rows.add(row_obj);
			//			}
			//			
			//			obj.put("rows", rows);
			//			
			//			StringWriter out = new StringWriter();
			//		    obj.writeJSONString(out);
			//		    jsonText = out.toString();

			//	    System.out.print(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}


	private String callProcessWS(String data){
		MongoDbUtil mdb = new MongoDbUtil();
		CacheMongoObject mongoObj = mdb.insertObj(data);
		int mongoObjStatus = mongoObj.getStatus();
		String jsonResult="";
		switch (mongoObjStatus) {
		case 0:
			//jsonResult = callMatlabService((String) mongoObj.getMongoObj().get("patients"), "LOC");
			//System.out.println(mongoObj.getMongoObj().get("patients"));
			jsonResult = "MATLAB SERVICE CALLED";
			break;

		case 1:
			jsonResult = "JSON RESULT MISSING";
			break;
		case 2:
			jsonResult = "JSON RESULT FOUND: "+(String) mongoObj.getMongoObj().get("results");
			break;
		}
		System.out.println(jsonResult);
		return jsonResult;

	}

	private String callMatlabService (String jsonIn, String chartType){
		//chartType : LOC, COMPLICATION, DRUG, CVR, HOSPITALIZATION
		StringBuffer result = new StringBuffer();
		Properties prop = new Properties();
		InputStream input = null;

		String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
		input = getServletContext().getResourceAsStream(path);

		try {
			prop.load(input);

			String url = prop.getProperty("process_url");

			HttpClient client = HttpClientBuilder.create().build();
			HttpPost post = new HttpPost(url);

			// add header
			//post.setHeader("User-Agent", USER_AGENT);
			post.addHeader("content-type", "application/x-www-form-urlencoded");


			//params: cont=0&ths=0&length=0&path=test

			List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
			urlParameters.add(new BasicNameValuePair("cont", prop.getProperty("cont_default_value")));
			urlParameters.add(new BasicNameValuePair("ths", prop.getProperty("ths_default_value")));
			urlParameters.add(new BasicNameValuePair("length", prop.getProperty("length_default_value")));
			urlParameters.add(new BasicNameValuePair("path", jsonIn.replaceAll(" ", "")));

			post.setEntity(new UrlEncodedFormEntity(urlParameters));

			HttpResponse response = client.execute(post);
			//		System.out.println("Response Code : " 
			//	                + response.getStatusLine().getStatusCode());

			BufferedReader rd = new BufferedReader(
					new InputStreamReader(response.getEntity().getContent()));

			String line ="";

			while ((line = rd.readLine()) != null) {
				result.append(line);
			}	
		} catch (IOException e) {
			e.printStackTrace();
		}

		String encoded_result = "";;
		try {
			encoded_result = URLDecoder.decode(result.toString(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		return encoded_result;

	}
	private String getI2B2DataForHba1c(String patientId) {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.NVAL_NUM as hba1c_value " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV q1 " +
					"where q1.CONCEPT_CD in (?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("hba1c"));

			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Exam date");
			col_1.put("type", "date");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Hba1c");
			col_2.put("type", "number");

			cols.add(col_1);
			cols.add(col_2);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			while(rs.next()){
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				int year = rs.getInt("h_year");
				int month = rs.getInt("h_month")-1;
				int day = rs.getInt("h_day");
				row_1.put("v","Date("+year+","+month+","+day+")");

				JSONObject row_2 = new JSONObject();
				BigDecimal myTipe = rs.getBigDecimal("hba1c_value");
				row_2.put("v", myTipe);
				row_arr.add(row_1);
				row_arr.add(row_2);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}

	private String getI2B2DataForTherapy(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		PreparedStatement pstmt3 = null;
		ResultSet rs3 = null;
		PreparedStatement pstmt4 = null;
		ResultSet rs4 = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();
			String sqlDateRange = "select q1.PATIENT_NUM,extract (year from min(start_date)) as minsd, " +
					"extract (year from max(start_date)) as maxsd "+
					"from OBSERVATION_FACT q1 , DRUG_CLASSES d " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num =" +patientId+" "+
					"group by patient_num";

			pstmt2 = conn.prepareStatement(sqlDateRange);
			rs2 = pstmt2.executeQuery();
			int minDate = 1990;
			int maxDate = Calendar.getInstance().get(Calendar.YEAR);
			while(rs2.next()){
				minDate = rs2.getInt("minsd");
				maxDate = rs2.getInt("maxsd");
			}

			String sqlMaxValue = "select max(q1.NVAL_NUM) as ddd_value, d.atc_class as atc_class "+
					"from OBSERVATION_FACT q1 , DRUG_CLASSES d "+
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num= " +patientId+" "+
					"group by atc_class";

			pstmt3 = conn.prepareStatement(sqlMaxValue);
			rs3 = pstmt3.executeQuery();
			HashMap<String, Double> maxValueMap = new HashMap<String, Double>();
			while(rs3.next()){
				maxValueMap.put(rs3.getString("atc_class"), rs3.getDouble("ddd_value"));
			}

			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.NVAL_NUM as ddd_value, " +
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d " +
					"where q1.CONCEPT_CD = d.basecode and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";


			String sqlMagicSquare = "select q1.PATIENT_NUM, "+
					"q1.observation_blob, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId+" "+
					"order by q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			HashMap<String, HashMap<String, List<I2B2TherapyObservation>>> myOuterMap = new HashMap<String, HashMap<String,List<I2B2TherapyObservation>>>();
			//			HashMap<String, List<I2B2TherapyObservation>> myMap = new HashMap<String, List<I2B2TherapyObservation>>();
			while(rs.next()){
				String atcClass = rs.getString("atc_class");
				if(myOuterMap.get(atcClass)!=null){//classe gi� presente nella mappa
					String atcDescr = rs.getString("atc_descr");
					HashMap<String, List<I2B2TherapyObservation>> innerMap = myOuterMap.get(atcClass);
					if(innerMap.get(atcDescr)!=null){ //farmaco gi� presente nella mappa inner
						List<I2B2TherapyObservation> therapyList = innerMap.get(atcDescr);
						I2B2TherapyObservation newTherapyObs = new I2B2TherapyObservation();
						newTherapyObs.setAtcClass(atcClass);
						newTherapyObs.setAtcCode(rs.getString("atc"));
						newTherapyObs.setAtcDescr(rs.getString("atc_descr"));
						newTherapyObs.setnValNum(rs.getDouble("ddd_value"));
						newTherapyObs.setPatientNum(rs.getInt("PATIENT_NUM"));
						newTherapyObs.setStartDateDay(rs.getInt("h_day"));
						newTherapyObs.setStartDateMonth(rs.getInt("h_month"));
						newTherapyObs.setStartDateYear(rs.getInt("h_year"));
						therapyList.add(newTherapyObs);
					}else{ //farmaco da aggiungere alla mappa inner
						List<I2B2TherapyObservation> therapyList = new ArrayList<I2B2TherapyObservation>();
						I2B2TherapyObservation newTherapyObs = new I2B2TherapyObservation();
						newTherapyObs.setAtcClass(atcClass);
						newTherapyObs.setAtcCode(rs.getString("atc"));
						newTherapyObs.setAtcDescr(rs.getString("atc_descr"));
						newTherapyObs.setnValNum(rs.getDouble("ddd_value"));
						newTherapyObs.setPatientNum(rs.getInt("PATIENT_NUM"));
						newTherapyObs.setStartDateDay(rs.getInt("h_day"));
						newTherapyObs.setStartDateMonth(rs.getInt("h_month"));
						newTherapyObs.setStartDateYear(rs.getInt("h_year"));
						therapyList.add(newTherapyObs);
						innerMap.put(atcDescr, therapyList);
					}	
				}else{ //classe da inserire nella mappa
					String atcDescr = rs.getString("atc_descr");
					HashMap<String, List<I2B2TherapyObservation>> innerMap = new HashMap<String, List<I2B2TherapyObservation>>();
					List<I2B2TherapyObservation> therapyList = new ArrayList<I2B2TherapyObservation>();
					I2B2TherapyObservation newTherapyObs = new I2B2TherapyObservation();
					newTherapyObs.setAtcClass(atcClass);
					newTherapyObs.setAtcCode(rs.getString("atc"));
					newTherapyObs.setAtcDescr(rs.getString("atc_descr"));
					newTherapyObs.setnValNum(rs.getDouble("ddd_value"));
					newTherapyObs.setPatientNum(rs.getInt("PATIENT_NUM"));
					newTherapyObs.setStartDateDay(rs.getInt("h_day"));
					newTherapyObs.setStartDateMonth(rs.getInt("h_month"));
					newTherapyObs.setStartDateYear(rs.getInt("h_year"));
					therapyList.add(newTherapyObs);
					innerMap.put(atcDescr, therapyList);
					myOuterMap.put(atcClass, innerMap);
				}
				//				if(myMap.get(atcClass)!=null){//classe gi� presente nella mappa
				//					
				//				}else{ //classe da inserire nella mappa
				//					
				//				}
			}//fine del while

			Set<String> atcClassesSet = myOuterMap.keySet();
			JSONObject jsonContainer = new JSONObject();
			JSONArray resultsJsonArray = new JSONArray();
			for(String atcClass : atcClassesSet){
				//mappa dei farmaci relativa a una classe di farmaci
				HashMap<String, List<I2B2TherapyObservation>> innerMap = myOuterMap.get(atcClass);
				//1 oggetto con due attributi
				JSONObject colClass = new JSONObject();
				colClass.put("atc_class", atcClass);
				//metto il valore massimo
				Double maxValue = maxValueMap.get(atcClass);
				Double maxRoundedValue = roundMaxValue(maxValue);
				colClass.put("max_value", maxRoundedValue);
				JSONObject chartData = new JSONObject();
				JSONArray cols = new JSONArray();
				JSONObject col_1 = new JSONObject();
				col_1.put("id", "A");
				col_1.put("label", "A");
				col_1.put("type", "date");
				cols.add(col_1);
				//faccio tante colonne quante sono i farmaci
				Set<String> atcSet = innerMap.keySet();
				for(String atc : atcSet){
					JSONObject col_2 = new JSONObject();
					col_2.put("id", atc);
					col_2.put("label", atc);
					col_2.put("type", "number");
					cols.add(col_2);
				}	
				//				JSONObject col_22 = new JSONObject();
				//				col_22.put("id", "D");
				//				col_22.put("label", "D");
				//				col_22.put("type", "number");
				//				JSONObject col_3 = new JSONObject();
				//				col_3.put("id", "C");
				//				col_3.put("label", "C");
				//				col_3.put("type", "string");
				//				col_3.put("role", "tooltip");			
				//				cols.add(col_22);
				//cols.add(col_3);
				JSONArray rows = new JSONArray();
				for(String atc : atcSet){ //per ogni farmaco
					List<I2B2TherapyObservation> therapyList = innerMap.get(atc);
					for(I2B2TherapyObservation to : therapyList){
						JSONObject row_obj = new JSONObject();
						JSONArray row_arr = new JSONArray();
						JSONObject row_1 = new JSONObject();
						int year = to.getStartDateYear();
						int month = to.getStartDateMonth()-1;
						int day =to.getStartDateDay();
						row_1.put("v","Date("+year+","+month+","+day+")");  //scrivo il primo elemento della riga
						row_arr.add(row_1);
						//devo cercare in che colonna mettere la dose!
						double dose = to.getnValNum();
						for(int i=1; i<cols.size(); i++){ //la prima la salto perch� � la data
							JSONObject col = (JSONObject) cols.get(i);
							JSONObject row_2 = new JSONObject();
							if(col.containsValue(atc)){
								row_2.put("v", dose);
							}else{
								row_2.put("v", null);
							}
							row_arr.add(row_2);
						}
						row_obj.put("c",row_arr);
						rows.add(row_obj);
					}
				}
				//				List<I2B2TherapyObservation> therapyList = myMap.get(atcClass);
				//				int i=1;
				//				for(I2B2TherapyObservation to : therapyList){
				//					JSONObject row_obj = new JSONObject();
				//					JSONArray row_arr = new JSONArray();
				//					JSONObject row_1 = new JSONObject();
				//					int year = to.getStartDateYear();
				//					int month = to.getStartDateMonth();
				//					int day =to.getStartDateDay();
				//					row_1.put("v","Date("+year+","+month+","+day+")");
				//
				//					double dose = to.getnValNum();
				//					JSONObject row_2 = new JSONObject();
				//					JSONObject row_22 = new JSONObject();
				////					row_2.put("v", 10);
				////					row_22.put("v", 5);
				//					if(i%2==0){
				//						row_2.put("v", dose);
				//						row_22.put("v", 5);
				//					}else{
				//						row_22.put("v", dose);
				//						row_2.put("v", 5);
				//					}
				//					i++;
				//					String myTooltip = to.getAtcDescr()+" ("+to.getAtcCode()+"): "+to.getnValNum();
				//					JSONObject row_3 = new JSONObject();
				//					row_3.put("v", myTooltip);
				//
				//					row_arr.add(row_1);
				//					row_arr.add(row_2);
				//					row_arr.add(row_22);
				//					//row_arr.add(row_3);
				//					row_obj.put("c",row_arr);
				//					rows.add(row_obj);
				//				}
				chartData.put("cols", cols);
				chartData.put("rows", rows);
				colClass.put("data", chartData);
				//
				resultsJsonArray.add(colClass);
			}

			//MagicBox Section
			pstmt4 = conn.prepareStatement(sqlMagicSquare);
			rs4 = pstmt4.executeQuery();
			HashMap<String,String> atcBlobMap = new HashMap<String,String>();
			//JSONObject myObj = new JSONObject();
			while(rs4.next()){
				String atcClass = rs4.getString("atc_class");		
				if(atcBlobMap.get(atcClass)!=null){//farmaco gi� presente nella mappa
					//do nothing
				}else{
					//myObj.put("js", rs.getString("observation_blob").replace("\"", ""));
					atcBlobMap.put(atcClass,rs4.getString("observation_blob"));
					//					System.out.println("----");
					//					System.out.println(rs.getString("observation_blob"));
					//					System.out.println("----");
				}
			}//fine while

			Set<String> keySet = atcBlobMap.keySet();
			//String JsonDIY = "{\"my_data\":[";
			JSONArray magicBoxArray = new JSONArray();
			for(String s: keySet){
				JSONObject atcObj = new JSONObject();
				atcObj.put("atcClass", s);
				JSONParser jParser = new JSONParser();
				JSONObject blobObj = (JSONObject) jParser.parse(atcBlobMap.get(s));
				atcObj.put("atcBlob", blobObj);
				//JsonDIY = JsonDIY.concat(atcBlobMap.get(s)).concat(",");
				//outerArray.add(atcBlobMap.get(s));
				//outerArray.add(atcObj);
				magicBoxArray.add(atcObj);
			}
			//			JsonDIY = JsonDIY.substring(0, JsonDIY.length()-1);
			//			JsonDIY = JsonDIY.concat("]}");
			//			jsonContainer.put("myData", outerArray);
			//			StringWriter swout = new StringWriter();
			//			jsonContainer.writeJSONString(swout);
			//			jsonText = swout.toString();
			//			System.out.println(jsonText);			
			//jsonText = JsonDIY;

			jsonContainer.put("results", resultsJsonArray);
			jsonContainer.put("endYear", maxDate);
			jsonContainer.put("startYear", minDate);
			jsonContainer.put("result4MagicBox", magicBoxArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//	System.out.println(jsonText);


		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(rs3 != null){
					rs3.close();
				}
				if(pstmt3 != null){
					pstmt3.close();
				}
				if(rs4 != null){
					rs4.close();
				}
				if(pstmt4 != null){
					pstmt4.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}
	private String getI2B2DataForTherapyAdherence(String patientId) {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();


			String sql = "select q1.PATIENT_NUM, "+
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.tval_char as aderenza, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			HashMap<String, List<I2B2TherapyObservation>> myOuterMap = new HashMap<String, List<I2B2TherapyObservation>>();
			while(rs.next()){
				String atcClass = rs.getString("atc_class");
				if(myOuterMap.get(atcClass)!=null){//classe gi� presente nella mappa
					List<I2B2TherapyObservation> therapyList = myOuterMap.get(atcClass);
					I2B2TherapyObservation newTherapyObs = new I2B2TherapyObservation();
					newTherapyObs.setAtcClass(atcClass);
					newTherapyObs.setAtcCode(rs.getString("atc"));
					newTherapyObs.setAtcDescr(rs.getString("atc_descr"));
					newTherapyObs.settValChar(rs.getString("aderenza"));
					newTherapyObs.setPatientNum(rs.getInt("PATIENT_NUM"));
					newTherapyObs.setStartDateDay(rs.getInt("h_day_start"));
					newTherapyObs.setStartDateMonth(rs.getInt("h_month_start"));
					newTherapyObs.setStartDateYear(rs.getInt("h_year_start"));
					newTherapyObs.setEndDateDay(rs.getInt("h_day_end"));
					newTherapyObs.setEndDateMonth(rs.getInt("h_month_end"));
					newTherapyObs.setEndDateYear(rs.getInt("h_year_end"));

					therapyList.add(newTherapyObs);
				}else{ //classe da inserire nella mappa
					List<I2B2TherapyObservation> therapyList = new ArrayList<I2B2TherapyObservation>();
					I2B2TherapyObservation newTherapyObs = new I2B2TherapyObservation();
					newTherapyObs.setAtcClass(atcClass);
					newTherapyObs.setAtcCode(rs.getString("atc"));
					newTherapyObs.setAtcDescr(rs.getString("atc_descr"));
					newTherapyObs.settValChar(rs.getString("aderenza"));
					newTherapyObs.setPatientNum(rs.getInt("PATIENT_NUM"));
					newTherapyObs.setStartDateDay(rs.getInt("h_day_start"));
					newTherapyObs.setStartDateMonth(rs.getInt("h_month_start"));
					newTherapyObs.setStartDateYear(rs.getInt("h_year_start"));
					newTherapyObs.setEndDateDay(rs.getInt("h_day_end"));
					newTherapyObs.setEndDateMonth(rs.getInt("h_month_end"));
					newTherapyObs.setEndDateYear(rs.getInt("h_year_end"));
					therapyList.add(newTherapyObs);
					myOuterMap.put(atcClass, therapyList);
				}
			}//fine del while

			Set<String> atcClassesSet = myOuterMap.keySet();
			JSONObject jsonContainer = new JSONObject();
			JSONArray resultsJsonArray = new JSONArray();
			for(String atcClass : atcClassesSet){ //faccio una timeline per ogni classe di farmaci
				//1 oggetto con due attributi
				JSONObject colClass = new JSONObject();
				colClass.put("atc_class", atcClass);
				JSONObject chartData = new JSONObject();
				JSONArray cols = new JSONArray();
				JSONObject col_1 = new JSONObject();
				col_1.put("id", "Therapy");
				col_1.put("label", "Therapy");
				col_1.put("type", "string");

				JSONObject col_2 = new JSONObject();
				col_2.put("id", "TherapyName");
				col_2.put("label", "TherapyName");
				col_2.put("type", "string");

				JSONObject col_3 = new JSONObject();
				col_3.put("id", "Start");
				col_3.put("label", "Start");
				col_3.put("type", "date");

				JSONObject col_4 = new JSONObject();
				col_4.put("id", "End");
				col_4.put("label", "End");
				col_4.put("type", "date");
				cols.add(col_1);
				cols.add(col_2);
				cols.add(col_3);
				cols.add(col_4);
				List<I2B2TherapyObservation> therapyList = myOuterMap.get(atcClass);
				JSONArray rows = new JSONArray();
				for(I2B2TherapyObservation to : therapyList){
					JSONObject row_obj = new JSONObject();
					JSONArray row_arr = new JSONArray();
					JSONObject row_1 = new JSONObject();
					row_1.put("v",to.getAtcDescr());

					JSONObject row_2 = new JSONObject();
					row_2.put("v",to.gettValChar());

					JSONObject row_3 = new JSONObject();
					int monthStart = to.getStartDateMonth()-1;
					row_3.put("v", "Date("+to.getStartDateYear()+","+monthStart+","+to.getStartDateDay()+")");


					JSONObject row_4 = new JSONObject();
					int monthEnd = to.getEndDateMonth()-1;
					row_4.put("v", "Date("+to.getEndDateYear()+","+monthEnd+","+to.getEndDateDay()+")");

					row_arr.add(row_1);
					row_arr.add(row_2);
					row_arr.add(row_3);
					row_arr.add(row_4);
					row_obj.put("c",row_arr);
					rows.add(row_obj);
				}
				chartData.put("cols", cols);
				chartData.put("rows", rows);
				colClass.put("data", chartData);
				resultsJsonArray.add(colClass);
			}
			jsonContainer.put("results", resultsJsonArray);
			//			jsonContainer.put("endYear", maxDate);
			//			jsonContainer.put("startYear", minDate);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//	System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}


	private String getI2B2DataForTherapyAdherence2(String patientId) {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			List<String> atcDescrSet = new ArrayList<String>();
			List<String>  aderenzaLabelSet = new ArrayList<String>();
			String sql2 = "select distinct q1.tval_char "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId;

			pstmt2 = conn.prepareStatement(sql2);
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				aderenzaLabelSet.add(rs2.getString("tval_char"));
			}

			String sql = "select q1.PATIENT_NUM, "+
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.tval_char as aderenza, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId+" "+
					"order by q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "Therapy");
			col_1.put("label", "Therapy");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "TherapyName");
			col_2.put("label", "TherapyName");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			//			JSONObject col_5 = new JSONObject();
			//			col_5.put("id", "atc");
			//			col_5.put("label", "atc");
			//			col_5.put("type", "string");
			//			JSONObject col_6 = new JSONObject();
			//			col_6.put("id", "C");
			//			col_6.put("label", "C");
			//			col_6.put("type", "string");
			//			col_6.put("role", "tooltip");	
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			//			cols.add(col_5);
			//			cols.add(col_6);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int counter =0;
			HashMap<String, List<String>> atcLabelMap = new HashMap<String, List<String>>();
			while(rs.next()){
				//Lista ordinata di farmaci per data di comparizione
				String atcDescr = rs.getString("atc_descr");
				if(!atcDescrSet.contains(atcDescr)){
					atcDescrSet.add(atcDescr);
				}
				//Mappa per colori			
				if(atcLabelMap.get(atcDescr)!=null){//farmaco gi� presente nella mappa
					List<String> labelOrder4Atc = atcLabelMap.get(atcDescr);
					if(!labelOrder4Atc.contains(rs.getString("aderenza"))){
						labelOrder4Atc.add(rs.getString("aderenza"));
					}
				}else{
					List<String> labelOrder4Atc = new ArrayList<String>();
					labelOrder4Atc.add(rs.getString("aderenza"));
					atcLabelMap.put(atcDescr, labelOrder4Atc);
				}
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v",rs.getString("atc_descr"));

				JSONObject row_2 = new JSONObject();
				//row_2.put("v", rs.getString("atc_descr")+": "+rs.getString("aderenza").concat(String.valueOf(counter++)));
				row_2.put("v",rs.getString("aderenza"));

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");

				JSONObject row_4 = new JSONObject();
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");

				//				JSONObject row_5 = new JSONObject();
				//				row_5.put("v",rs.getString("aderenza"));
				//				
				//				JSONObject row_6 = new JSONObject();
				//				row_6.put("v","pippo");

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				//				row_arr.add(row_5);
				//				row_arr.add(row_6);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);
			jsonContainer.put("therapyData", obj);
			//			jsonContainer.put("endYear", maxDate);
			//			jsonContainer.put("startYear", minDate);
			List<String> labels = getLabelOrder(atcDescrSet, aderenzaLabelSet, atcLabelMap);
			JSONArray labelsArray = new JSONArray();
			for(String s:labels){
				labelsArray.add(s);
			}
			jsonContainer.put("labelsArray", labelsArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);


		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}
	private String getI2B2DataForTherapyAdherence3(String patientId) {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			List<String> atcDescrSet = new ArrayList<String>();
			List<String>  aderenzaLabelSet = new ArrayList<String>();
			String sql2 = "select distinct q1.tval_char "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId;

			pstmt2 = conn.prepareStatement(sql2);
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				aderenzaLabelSet.add(rs2.getString("tval_char"));
			}

			String sql = "select q1.PATIENT_NUM, "+
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.tval_char as aderenza, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd ,q1.nval_num "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId+" "+
					"order by q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "Therapy");
			col_1.put("label", "Therapy");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "TherapyName");
			col_2.put("label", "TherapyName");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			//			JSONObject col_5 = new JSONObject();
			//			col_5.put("id", "atc");
			//			col_5.put("label", "atc");
			//			col_5.put("type", "string");
			//			JSONObject col_6 = new JSONObject();
			//			col_6.put("id", "C");
			//			col_6.put("label", "C");
			//			col_6.put("type", "string");
			//			col_6.put("role", "tooltip");	
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			//			cols.add(col_5);
			//			cols.add(col_6);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int counter =0;
			HashMap<String, List<String>> atcLabelMap = new HashMap<String, List<String>>();
			HashMap<String, List<String>> atcMap = new HashMap<String, List<String>>();	
			while(rs.next()){
				//Lista ordinata di farmaci per data di comparizione
				String atcDescr = rs.getString("atc_descr");
				if(!atcDescrSet.contains(atcDescr)){
					atcDescrSet.add(atcDescr);
				}
				//Mappa per colori			
				Double aderenzaValue = rs.getDouble("nval_num");
				String aderenzaRangeString = roundAdherenceValue2(aderenzaValue);
				if(atcLabelMap.get(atcDescr)!=null){//farmaco gi� presente nella mappa
					List<String> labelOrder4Atc = atcLabelMap.get(atcDescr);
					//					if(!labelOrder4Atc.contains(rs.getString("aderenza"))){
					//						labelOrder4Atc.add(rs.getString("aderenza"));
					//					}
					if(!labelOrder4Atc.contains(aderenzaRangeString)){
						labelOrder4Atc.add(aderenzaRangeString);
					}
				}else{
					List<String> labelOrder4Atc = new ArrayList<String>();
					//labelOrder4Atc.add(rs.getString("aderenza"));
					labelOrder4Atc.add(aderenzaRangeString);
					atcLabelMap.put(atcDescr, labelOrder4Atc);
				}
				//Mappa dei farmaci
				String atcClass = rs.getString("atc_class");		
				if(atcMap.get(atcClass)!=null){//farmaco gi� presente nella mappa
					List<String> atcList = atcMap.get(atcClass);
					if(!atcList.contains(atcDescr)){
						atcList.add(atcDescr);
					}
				}else{
					List<String> atcList = new ArrayList<String>();
					atcList.add(atcDescr);
					atcMap.put(atcClass, atcList);
				}
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v",rs.getString("atc_descr"));

				JSONObject row_2 = new JSONObject();
				//row_2.put("v", rs.getString("atc_descr")+": "+rs.getString("aderenza").concat(String.valueOf(counter++)));

				//row_2.put("v",rs.getString("aderenza"));
				row_2.put("v",aderenzaRangeString);

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");

				JSONObject row_4 = new JSONObject();
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");

				//				JSONObject row_5 = new JSONObject();
				//				row_5.put("v",rs.getString("aderenza"));
				//				
				//				JSONObject row_6 = new JSONObject();
				//				row_6.put("v","pippo");

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				//				row_arr.add(row_5);
				//				row_arr.add(row_6);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);
			jsonContainer.put("therapyData", obj);
			//			jsonContainer.put("endYear", maxDate);
			//			jsonContainer.put("startYear", minDate);
			List<String> labels = getLabelOrder(atcDescrSet, aderenzaLabelSet, atcLabelMap);
			JSONArray labelsArray = new JSONArray();
			for(String s:labels){
				labelsArray.add(s);
			}
			jsonContainer.put("labelsArray", labelsArray);

			//Mappa farmaci per bottoni
			Set<String> keySet = atcMap.keySet();
			JSONArray outerArray = new JSONArray();
			for(String s: keySet){
				JSONObject atcObj = new JSONObject();
				atcObj.put("atcClass", s);
				List<String> atcs = atcMap.get(s);
				JSONArray atcArray = new JSONArray();
				for(String atc: atcs){
					atcArray.add(atc);
				}
				atcObj.put("atcList", atcArray);
				outerArray.add(atcObj);
			}
			jsonContainer.put("atcListData", outerArray);

			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//	System.out.println(jsonText);


		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}

	private String getI2B2DataForTherapyAdherence3Filtered(String patientId, String atcFilter) {
		//System.out.println(atcFilter);
		String[] atcArrayFilter = atcFilter.substring(0, atcFilter.length()-1).split(",");
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			List<String> atcDescrSet = new ArrayList<String>();
			List<String>  aderenzaLabelSet = new ArrayList<String>();
			String sql2 = "select distinct q1.tval_char "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and d.atc_descr not in (";
			String sql = "select q1.PATIENT_NUM, "+
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.tval_char as aderenza, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd ,q1.nval_num "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' " +
					"and d.atc_descr not in (";

			for (String atc : atcArrayFilter){
				sql2 = sql2.concat("'"+atc+"',");
				sql = sql.concat("'"+atc+"',");	
			}
			sql = sql.substring(0, sql.length()-1);
			sql2 = sql2.substring(0, sql2.length()-1);
			sql2 = sql2.concat(") and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId);
			sql = sql.concat(") and q1.patient_num= " +patientId+" order by q1.START_DATE");

			pstmt2 = conn.prepareStatement(sql2);
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				aderenzaLabelSet.add(rs2.getString("tval_char"));
			}



			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "Therapy");
			col_1.put("label", "Therapy");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "TherapyName");
			col_2.put("label", "TherapyName");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			//			JSONObject col_5 = new JSONObject();
			//			col_5.put("id", "atc");
			//			col_5.put("label", "atc");
			//			col_5.put("type", "string");
			//			JSONObject col_6 = new JSONObject();
			//			col_6.put("id", "C");
			//			col_6.put("label", "C");
			//			col_6.put("type", "string");
			//			col_6.put("role", "tooltip");	
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			//			cols.add(col_5);
			//			cols.add(col_6);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int counter =0;
			HashMap<String, List<String>> atcLabelMap = new HashMap<String, List<String>>();
			HashMap<String, List<String>> atcMap = new HashMap<String, List<String>>();	
			while(rs.next()){
				//Lista ordinata di farmaci per data di comparizione
				String atcDescr = rs.getString("atc_descr");
				if(!atcDescrSet.contains(atcDescr)){
					atcDescrSet.add(atcDescr);
				}
				//Mappa per colori			
				Double aderenzaValue = rs.getDouble("nval_num");
				String aderenzaRangeString = roundAdherenceValue2(aderenzaValue);
				if(atcLabelMap.get(atcDescr)!=null){//farmaco gi� presente nella mappa
					List<String> labelOrder4Atc = atcLabelMap.get(atcDescr);
					//					if(!labelOrder4Atc.contains(rs.getString("aderenza"))){
					//						labelOrder4Atc.add(rs.getString("aderenza"));
					//					}
					if(!labelOrder4Atc.contains(aderenzaRangeString)){
						labelOrder4Atc.add(aderenzaRangeString);
					}
				}else{
					List<String> labelOrder4Atc = new ArrayList<String>();
					//labelOrder4Atc.add(rs.getString("aderenza"));
					labelOrder4Atc.add(aderenzaRangeString);
					atcLabelMap.put(atcDescr, labelOrder4Atc);
				}
				//Mappa dei farmaci
				String atcClass = rs.getString("atc_class");		
				if(atcMap.get(atcClass)!=null){//farmaco gi� presente nella mappa
					List<String> atcList = atcMap.get(atcClass);
					if(!atcList.contains(atcDescr)){
						atcList.add(atcDescr);
					}
				}else{
					List<String> atcList = new ArrayList<String>();
					atcList.add(atcDescr);
					atcMap.put(atcClass, atcList);
				}
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v",rs.getString("atc_descr"));

				JSONObject row_2 = new JSONObject();
				//row_2.put("v", rs.getString("atc_descr")+": "+rs.getString("aderenza").concat(String.valueOf(counter++)));

				//row_2.put("v",rs.getString("aderenza"));
				row_2.put("v",aderenzaRangeString);

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");

				JSONObject row_4 = new JSONObject();
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");

				//				JSONObject row_5 = new JSONObject();
				//				row_5.put("v",rs.getString("aderenza"));
				//				
				//				JSONObject row_6 = new JSONObject();
				//				row_6.put("v","pippo");

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				//				row_arr.add(row_5);
				//				row_arr.add(row_6);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);
			jsonContainer.put("therapyData", obj);
			//			jsonContainer.put("endYear", maxDate);
			//			jsonContainer.put("startYear", minDate);
			List<String> labels = getLabelOrder(atcDescrSet, aderenzaLabelSet, atcLabelMap);
			JSONArray labelsArray = new JSONArray();
			for(String s:labels){
				labelsArray.add(s);
			}
			jsonContainer.put("labelsArray", labelsArray);

			//Mappa farmaci per bottoni
			Set<String> keySet = atcMap.keySet();
			JSONArray outerArray = new JSONArray();
			for(String s: keySet){
				JSONObject atcObj = new JSONObject();
				atcObj.put("atcClass", s);
				List<String> atcs = atcMap.get(s);
				JSONArray atcArray = new JSONArray();
				for(String atc: atcs){
					atcArray.add(atc);
				}
				atcObj.put("atcList", atcArray);
				outerArray.add(atcObj);
			}
			jsonContainer.put("atcListData", outerArray);

			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//	System.out.println(jsonText);


		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}

	private String getI2B2DataForLOC(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			List<String>  locLabelSet = new ArrayList<String>();
			String sql2 = "select q1.observation_blob as obs_blob "+
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" order by q1.START_DATE";

			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setString(1, prop.getProperty("level_of_complexity"));
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				if(!locLabelSet.contains(rs2.getString("obs_blob"))){
					locLabelSet.add(rs2.getString("obs_blob"));
				}
			}

			String sql ="select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.observation_blob as obs_blob, "+
					"q1.TVAL_CHAR as loc_value " +
					"from I2B2DEMODATA.OBSERVATION_FACT_DEV q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE desc";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("level_of_complexity"));
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "LOC");
			col_1.put("label", "LOC");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "LOC_DESCR");
			col_2.put("label", "LOC_DESCR");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int prevYear =0;
			int prevMonth = 0;
			int prevDay = 0;
			int i=0;
			while(rs.next()){
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v","Level of Complexity");

				JSONObject row_2 = new JSONObject();
				//row_2.put("v",rs.getString("loc_value"));
				row_2.put("v",rs.getString("obs_blob"));

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");

				JSONObject row_4 = new JSONObject();
				//QUANDO MANCA END DATE
				//				if(i==0){
				//					prevYear = Calendar.getInstance().get(Calendar.YEAR);
				//					prevMonth = Calendar.getInstance().get(Calendar.MONTH);
				//					prevDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				//				}
				//				row_4.put("v", "Date("+prevYear+","+prevMonth+","+prevDay+")");
				//				prevYear = rs.getInt("h_year_start");
				//				prevMonth = rs.getInt("h_month_start")-1;
				//				prevDay = rs.getInt("h_day_start");
				//				i++;

				//END DATE DISPONIBILE
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);
			jsonContainer.put("locData", obj);
			JSONArray labelsArray = new JSONArray();
			for(String s:locLabelSet){
				labelsArray.add(s);
			}
			jsonContainer.put("locLabels", labelsArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}
	private String getI2B2DataForCVR(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.NVAL_NUM as cvr_value " +
					"from "+observationTable+" q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("cardiovascular_risk"));

			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Date");
			col_1.put("type", "date");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "CVR");
			col_2.put("type", "number");

			JSONObject col_3 = new JSONObject();
			col_3.put("id", "C");
			col_3.put("label", "C");
			col_3.put("type", "string");
			col_3.put("role", "style");

			JSONObject col_4 = new JSONObject();
			col_4.put("id", "D");
			col_4.put("label", "D");
			col_4.put("type", "string");
			col_4.put("role", "tooltip");

			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			while(rs.next()){
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				int year = rs.getInt("h_year");
				int month = rs.getInt("h_month")-1;
				int day = rs.getInt("h_day");
				row_1.put("v","Date("+year+","+month+","+day+")");

				JSONObject row_2 = new JSONObject();
				//TODO
				//cambiare quando inseriranno obs giuste
				double myType = rs.getDouble("cvr_value");
				//double myType = Math.random() * 100;
				myType = Math.round(myType);
				//row_2.put("v", myType);

				String myStyle = "point { size: 5; shape-type: circle; fill-color: #";
				if(myType<=5){
					myStyle = myStyle.concat("84d1ad");
					row_2.put("v", 1);
				}else if(myType>5 && myType<=10){
					myStyle = myStyle.concat("01ae76");
					row_2.put("v", 2);
				}else if(myType>10 && myType<=15){
					myStyle = myStyle.concat("fdd687");
					row_2.put("v", 3);
				}else if(myType>15 && myType<=20){
					myStyle = myStyle.concat("ff944c");
					row_2.put("v", 4);
				}else if(myType>20 && myType<=30){
					myStyle = myStyle.concat("f34930");
					row_2.put("v", 5);
				}else if(myType>30){
					myStyle = myStyle.concat("c479b1");
					row_2.put("v", 6);
				}

				JSONObject row_3 = new JSONObject();
				myStyle = myStyle.concat("}");
				row_3.put("v", myStyle);

				JSONObject row_4 = new JSONObject();
				row_4.put("v", year+"/"+(month+1)+"/"+day + "\nCVR: " + myType);

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);

				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();
			//			System.out.println(jsonText);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}
	private String getI2B2DataForDiet(String patientId) {

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();


			List<String>  dietLabelSet = new ArrayList<String>();
			String sql2 = "select q1.observation_blob as obs_blob "+
					"from "+observationTable+"  q1 " +
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" order by q1.START_DATE";

			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setString(1, prop.getProperty("diet_ta_good"));
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				if(!dietLabelSet.contains(rs2.getString("obs_blob"))){
					dietLabelSet.add(rs2.getString("obs_blob"));
				}
			}


			String sql = "select q1.PATIENT_NUM, "+
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.observation_blob as diet, "+
					"q1.concept_cd as concept_cd "+
					"from "+observationTable+" q1 "+
					"where q1.CONCEPT_CD like ? and q1.patient_num= " +patientId+" "+
					"order by q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("diet_ta_good"));
			rs = pstmt.executeQuery();

			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "Diet");
			col_1.put("label", "Diet");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "DietAdherence");
			col_2.put("label", "DietAdherence");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int counter =0;
			while(rs.next()){
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v","Diet");

				JSONObject row_2 = new JSONObject();
				//row_2.put("v", rs.getString("atc_descr")+": "+rs.getString("aderenza").concat(String.valueOf(counter++)));
				row_2.put("v",rs.getString("diet"));

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");


				JSONObject row_4 = new JSONObject();
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");


				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}
			obj.put("rows", rows);
			jsonContainer.put("dietData", obj);
			JSONArray labelsArray = new JSONArray();
			for(String s:dietLabelSet){
				labelsArray.add(s);
			}
			jsonContainer.put("dietLabels", labelsArray);
			//			jsonContainer.put("endYear", maxDate);
			//			jsonContainer.put("startYear", minDate);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}

	private String getI2B2DataForAtcList(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			String sql = "select q1.PATIENT_NUM, "+
					"d.atc_class as atc_class, d.atc_descr as atc_descr, d.atc as atc, q1.concept_cd as concept_cd "+
					"from I2B2DEMODATA.OBSERVATION_FACT q1 , I2B2DEMODATA.DRUG_CLASSES d "+
					"where substr(q1.CONCEPT_CD,2) = substr(d.basecode,2) and q1.concept_cd like 'A_ATC%' and q1.patient_num= " +patientId+" "+
					"order by q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			HashMap<String, List<String>> atcLabelMap = new HashMap<String, List<String>>();
			while(rs.next()){
				String atcClass = rs.getString("atc_class");		
				if(atcLabelMap.get(atcClass)!=null){//farmaco gi� presente nella mappa
					List<String> atcList = atcLabelMap.get(atcClass);
					if(!atcList.contains(rs.getString("atc_descr"))){
						atcList.add(rs.getString("atc_descr"));
					}
				}else{
					List<String> atcList = new ArrayList<String>();
					atcList.add(rs.getString("atc_descr"));
					atcLabelMap.put(atcClass, atcList);
				}
			}//fine while
			Set<String> keySet = atcLabelMap.keySet();
			JSONArray outerArray = new JSONArray();
			for(String s: keySet){
				JSONObject atcObj = new JSONObject();
				atcObj.put("atcClass", s);
				List<String> atcs = atcLabelMap.get(s);
				JSONArray labelsArray = new JSONArray();
				for(String atc: atcs){
					labelsArray.add(atc);
				}
				atcObj.put("atcList", labelsArray);
				outerArray.add(atcObj);
			}
			jsonContainer.put("myData", outerArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//	System.out.println(jsonText);


		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}

	private String getI2B2DataForWeight(String patientId) {
		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		PreparedStatement pstmt2 = null;
		ResultSet rs2 = null;
		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);
			conn = DBUtil.getI2B2Connection();

			List<String>  weightLabelSet = new ArrayList<String>();
			String sql2 = "select q1.observation_blob as obs_blob "+
					"from "+observationTable+"  q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num= " +patientId+" order by q1.START_DATE";

			pstmt2 = conn.prepareStatement(sql2);
			pstmt2.setString(1, prop.getProperty("weight_timetotarget"));
			pstmt2.setString(2, prop.getProperty("weight_decrease"));
			rs2 = pstmt2.executeQuery();
			while(rs2.next()){
				if(!weightLabelSet.contains(rs2.getString("obs_blob"))){
					weightLabelSet.add(rs2.getString("obs_blob"));
				}
			}

			String sql ="select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year_start, "+
					"extract (month from q1.start_date) as h_month_start, "+
					"extract (day from q1.start_date) as h_day_start, "+
					"extract (year from q1.end_date) as h_year_end, "+
					"extract (month from q1.end_date) as h_month_end, "+
					"extract (day from q1.end_date) as h_day_end, "+
					"q1.observation_blob as obs_blob "+
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("weight_timetotarget"));
			pstmt.setString(2, prop.getProperty("weight_decrease"));
			rs = pstmt.executeQuery();
			JSONObject jsonContainer = new JSONObject();
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();
			JSONObject col_1 = new JSONObject();
			col_1.put("id", "WEIGHT");
			col_1.put("label", "WEIGHT");
			col_1.put("type", "string");
			JSONObject col_2 = new JSONObject();
			col_2.put("id", "WEIGHT_DESCR");
			col_2.put("label", "WEIGHT_DESCR");
			col_2.put("type", "string");
			JSONObject col_3 = new JSONObject();
			col_3.put("id", "Start");
			col_3.put("label", "Start");
			col_3.put("type", "date");
			JSONObject col_4 = new JSONObject();
			col_4.put("id", "End");
			col_4.put("label", "End");
			col_4.put("type", "date");
			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_3);
			cols.add(col_4);
			obj.put("cols", cols);
			JSONArray rows = new JSONArray();
			int prevYear =0;
			int prevMonth = 0;
			int prevDay = 0;
			int i=0;
			while(rs.next()){
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				row_1.put("v","WEIGHT");

				JSONObject row_2 = new JSONObject();
				//row_2.put("v",rs.getString("loc_value"));
				row_2.put("v",rs.getString("obs_blob"));

				JSONObject row_3 = new JSONObject();
				int year = rs.getInt("h_year_start");
				int month = rs.getInt("h_month_start")-1;
				int day = rs.getInt("h_day_start");
				row_3.put("v", "Date("+year+","+month+","+day+")");

				JSONObject row_4 = new JSONObject();
				//QUANDO MANCA END DATE
				//				if(i==0){
				//					prevYear = Calendar.getInstance().get(Calendar.YEAR);
				//					prevMonth = Calendar.getInstance().get(Calendar.MONTH);
				//					prevDay = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
				//				}
				//				row_4.put("v", "Date("+prevYear+","+prevMonth+","+prevDay+")");
				//				prevYear = rs.getInt("h_year_start");
				//				prevMonth = rs.getInt("h_month_start")-1;
				//				prevDay = rs.getInt("h_day_start");
				//				i++;

				//END DATE DISPONIBILE
				int yearEnd = rs.getInt("h_year_end");
				int monthEnd = rs.getInt("h_month_end")-1;
				int dayEnd = rs.getInt("h_day_end");
				row_4.put("v", "Date("+yearEnd+","+monthEnd+","+dayEnd+")");

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_3);
				row_arr.add(row_4);
				row_obj.put("c",row_arr);
				rows.add(row_obj);
			}//fine del while
			obj.put("rows", rows);
			jsonContainer.put("weightData", obj);
			JSONArray labelsArray = new JSONArray();
			for(String s:weightLabelSet){
				labelsArray.add(s);
			}
			jsonContainer.put("weightLabels", labelsArray);
			StringWriter swout = new StringWriter();
			jsonContainer.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(rs2 != null){
					rs2.close();
				}
				if(pstmt2 != null){
					pstmt2.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonText;
	}


	private String getI2B2DataForComplication(String patientId) {

		//setting ComplicationsMap
		complicationsMap.put("COM|MAC:AMI",6);
		complicationsMap.put("COM|MAC:ANG",7);
		complicationsMap.put("COM|MAC:CIHD",8);
		complicationsMap.put("COM|MAC:OCC",9);
		complicationsMap.put("COM|MAC:PVD",10);
		complicationsMap.put("COM|MAC:STR",11);
		complicationsMap.put("COM|MIC:DF",3);
		complicationsMap.put("COM|MIC:NEPH",4);
		complicationsMap.put("COM|MIC:RET",5);
		complicationsMap.put("COM|NV:FLD",1);
		complicationsMap.put("COM|NV:NEU",2);

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.concept_cd, " +
					"q1.observation_blob as obs_blob " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("macro_complication"));
			pstmt.setString(2, prop.getProperty("micro_complication"));
			pstmt.setString(3, prop.getProperty("nonvascular_complication"));


			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Exam date");
			col_1.put("type", "date");

			JSONObject col_2 = new JSONObject();
			col_2.put("id", 2);
			col_2.put("label", "Complication");
			col_2.put("type", "number");

			JSONObject col_3 = new JSONObject();
			col_3.put("id", 3);
			col_3.put("label", "Tooltip");
			col_3.put("type", "string");
			col_3.put("role", "tooltip");

			JSONObject col_mac = new JSONObject();
			col_mac.put("id", 4);
			col_mac.put("label", "MAC");
			col_mac.put("type", "number");

			JSONObject col_min = new JSONObject();
			col_min.put("id", 5);
			col_min.put("label", "MIN");
			col_min.put("type", "number");

			JSONObject col_nv = new JSONObject();
			col_nv.put("id", 6);
			col_nv.put("label", "NV");
			col_nv.put("type", "number");

			JSONObject col_annotation = new JSONObject();
			col_annotation.put("id", 7);
			col_annotation.put("label", "Annotation");
			col_annotation.put("type", "string");
			col_annotation.put("role", "annotation");

			//			JSONObject col_style = new JSONObject();
			//			col_style.put("id", "C");
			//			col_style.put("label", "C");
			//			col_style.put("type", "string");
			//			col_style.put("role", "style");

			cols.add(col_1);
			cols.add(col_2);
			cols.add(col_annotation);
			cols.add(col_3);
			cols.add(col_mac);
			cols.add(col_min);
			cols.add(col_nv);

			//			cols.add(col_style);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			int year = 0;
			int month =0;
			int day =0;
			int i=0;
			int complicationNum = 0;
			String tooltip = "";
			while(rs.next()){
				year = rs.getInt("h_year");
				if(i==0){//creo una riga finta 01/01/annoPrimaComplicanza per non avere le obs addossate allo start
					JSONObject row_objPlus = new JSONObject();
					JSONArray row_arrPlus = new JSONArray();
					JSONObject row_1Plus = new JSONObject();
					row_1Plus.put("v","Date("+year+",0,01)");

					JSONObject row_2Plus = new JSONObject();
					row_2Plus.put("v", null);

					JSONObject row_3Plus = new JSONObject();
					row_3Plus.put("v", null);

					JSONObject row_macPlus = new JSONObject();
					row_macPlus.put("v", 12);

					JSONObject row_micPlus = new JSONObject();
					row_micPlus.put("v", 5.5);

					JSONObject row_nvPlus = new JSONObject();
					row_nvPlus.put("v", 2.5);

					JSONObject row_annotationPlus = new JSONObject();
					row_annotationPlus.put("v", null);

					row_arrPlus.add(row_1Plus);
					row_arrPlus.add(row_2Plus);
					row_arrPlus.add(row_annotationPlus);
					row_arrPlus.add(row_3Plus);
					row_arrPlus.add(row_macPlus);
					row_arrPlus.add(row_micPlus);
					row_arrPlus.add(row_nvPlus);
					row_objPlus.put("c",row_arrPlus);
					rows.add(row_objPlus);
				}
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				//year = rs.getInt("h_year");
				month = rs.getInt("h_month")-1;
				day = rs.getInt("h_day");
				row_1.put("v","Date("+year+","+month+","+day+")");

				JSONObject row_2 = new JSONObject();
				String conceptCd = rs.getString("concept_cd");
				complicationNum = complicationsMap.get(conceptCd);
				row_2.put("v", complicationNum);

				JSONObject row_3 = new JSONObject();
				String dateString = "Date: ";
				dateString = dateString.concat(String.valueOf(day)).concat("/").concat(String.valueOf(month+1)).concat("/").concat(String.valueOf(year)).concat("\n");
				tooltip = dateString.concat(rs.getString("obs_blob"));
				row_3.put("v", tooltip);

				JSONObject row_mac = new JSONObject();
				row_mac.put("v", 12);

				JSONObject row_mic = new JSONObject();
				row_mic.put("v", 5.5);

				JSONObject row_nv = new JSONObject();
				row_nv.put("v", 2.5);

				JSONObject row_annotation = new JSONObject();
				row_annotation.put("v", rs.getString("obs_blob"));

				row_arr.add(row_1);
				row_arr.add(row_2);
				row_arr.add(row_annotation);
				row_arr.add(row_3);
				row_arr.add(row_mac);
				row_arr.add(row_mic);
				row_arr.add(row_nv);				
				row_obj.put("c",row_arr);
				rows.add(row_obj);
				i++;
			}//fine del while
			//			if(i==1){
			//				JSONObject row_obj = new JSONObject();
			//				JSONArray row_arr = new JSONArray();
			//				JSONObject row_1 = new JSONObject();
			//				year = year-1;
			//				row_1.put("v","Date("+year+","+month+","+day+")");
			//
			//				JSONObject row_2 = new JSONObject();
			//				row_2.put("v", null);
			//
			//				JSONObject row_3 = new JSONObject();
			//				row_3.put("v", null);
			//
			//				JSONObject row_mac = new JSONObject();
			//				row_mac.put("v", 12);
			//
			//				JSONObject row_mic = new JSONObject();
			//				row_mic.put("v", 5.5);
			//
			//				JSONObject row_nv = new JSONObject();
			//				row_nv.put("v", 2.5);
			//
			//				JSONObject row_annotation = new JSONObject();
			//				row_annotation.put("v", null);
			//
			//				row_arr.add(row_1);
			//				row_arr.add(row_2);
			//				row_arr.add(row_annotation);
			//				row_arr.add(row_3);
			//				row_arr.add(row_mac);
			//				row_arr.add(row_mic);
			//				row_arr.add(row_nv);
			//				row_obj.put("c",row_arr);
			//				rows.add(row_obj);
			//			}
			JSONObject row_objPlus = new JSONObject();
			JSONArray row_arrPlus = new JSONArray();
			JSONObject row_1Plus = new JSONObject();
			row_1Plus.put("v","Date("+year+",11,31)");

			JSONObject row_2Plus = new JSONObject();
			row_2Plus.put("v", null);

			JSONObject row_3Plus = new JSONObject();
			row_3Plus.put("v", null);

			JSONObject row_macPlus = new JSONObject();
			row_macPlus.put("v", 12);

			JSONObject row_micPlus = new JSONObject();
			row_micPlus.put("v", 5.5);

			JSONObject row_nvPlus = new JSONObject();
			row_nvPlus.put("v", 2.5);

			JSONObject row_annotationPlus = new JSONObject();
			row_annotationPlus.put("v", null);

			row_arrPlus.add(row_1Plus);
			row_arrPlus.add(row_2Plus);
			row_arrPlus.add(row_annotationPlus);
			row_arrPlus.add(row_3Plus);
			row_arrPlus.add(row_macPlus);
			row_arrPlus.add(row_micPlus);
			row_arrPlus.add(row_nvPlus);
			row_objPlus.put("c",row_arrPlus);
			rows.add(row_objPlus);
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}


	private String getI2B2DataForComplication2(String patientId) {

		//setting ComplicationsMap
		complicationsMap.put("COM|MAC:AMI",6);
		complicationsMap.put("COM|MAC:ANG",7);
		complicationsMap.put("COM|MAC:CIHD",8);
		complicationsMap.put("COM|MAC:OCC",9);
		complicationsMap.put("COM|MAC:PVD",10);
		complicationsMap.put("COM|MAC:STR",11);
		complicationsMap.put("COM|MIC:DF",3);
		complicationsMap.put("COM|MIC:NEPH",4);
		complicationsMap.put("COM|MIC:RET",5);
		complicationsMap.put("COM|NV:FLD",1);
		complicationsMap.put("COM|NV:NEU",2);

		Properties prop = new Properties();
		InputStream input = null;
		String jsonText = null; 
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		try {
			String path = getServletContext().getInitParameter("mosaic_i2b2_properties");
			input = getServletContext().getResourceAsStream(path);

			prop.load(input);

			String sql = "select q1.PATIENT_NUM, " +
					"extract (year from q1.start_date) as h_year, " +
					"extract (month from q1.start_date) as h_month, " +
					"extract (day from q1.start_date) as h_day, "+
					"q1.concept_cd, " +
					"q1.observation_blob as obs_blob " +
					"from "+observationTable+" q1 " +
					"where (q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ? or q1.CONCEPT_CD like ?) and q1.patient_num= " +patientId+" "+
					"order by q1.PATIENT_NUM, q1.START_DATE";

			conn = DBUtil.getI2B2Connection();
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, prop.getProperty("macro_complication"));
			pstmt.setString(2, prop.getProperty("micro_complication"));
			pstmt.setString(3, prop.getProperty("nonvascular_complication"));


			rs = pstmt.executeQuery();
			//object principale
			JSONObject obj = new JSONObject();
			JSONArray cols = new JSONArray();

			JSONObject col_1 = new JSONObject();
			col_1.put("id", 1);
			col_1.put("label", "Exam date");
			col_1.put("type", "date");

			JSONObject col_2_mac = new JSONObject();
			col_2_mac.put("id", 2);
			col_2_mac.put("label", "Macro");
			col_2_mac.put("type", "number");

			JSONObject col_tooltip_mac = new JSONObject();
			col_tooltip_mac.put("id", 3);
			col_tooltip_mac.put("label", "Tooltip MAC");
			col_tooltip_mac.put("type", "string");
			col_tooltip_mac.put("role", "tooltip");

			JSONObject col_annotation_mac = new JSONObject();
			col_annotation_mac.put("id",4);
			col_annotation_mac.put("label", "Annotation MAC");
			col_annotation_mac.put("type", "string");
			col_annotation_mac.put("role", "annotation");

			JSONObject col_2_mic = new JSONObject();
			col_2_mic.put("id", 5);
			col_2_mic.put("label", "Micro");
			col_2_mic.put("type", "number");

			JSONObject col_tooltip_mic = new JSONObject();
			col_tooltip_mic.put("id", 6);
			col_tooltip_mic.put("label", "Tooltip MIC");
			col_tooltip_mic.put("type", "string");
			col_tooltip_mic.put("role", "tooltip");

			JSONObject col_annotation_mic = new JSONObject();
			col_annotation_mic.put("id",7);
			col_annotation_mic.put("label", "Annotation MIC");
			col_annotation_mic.put("type", "string");
			col_annotation_mic.put("role", "annotation");

			JSONObject col_2_nv = new JSONObject();
			col_2_nv.put("id", 8);
			col_2_nv.put("label", "Non Vascular");
			col_2_nv.put("type", "number");

			JSONObject col_tooltip_nv = new JSONObject();
			col_tooltip_nv.put("id", 9);
			col_tooltip_nv.put("label", "Tooltip NV");
			col_tooltip_nv.put("type", "string");
			col_tooltip_nv.put("role", "tooltip");

			JSONObject col_annotation_nv = new JSONObject();
			col_annotation_nv.put("id",10);
			col_annotation_nv.put("label", "Annotation NV");
			col_annotation_nv.put("type", "string");
			col_annotation_nv.put("role", "annotation");

			JSONObject col_mac = new JSONObject();
			col_mac.put("id", 11);
			col_mac.put("label", "MAC");
			col_mac.put("type", "number");

			JSONObject col_min = new JSONObject();
			col_min.put("id", 12);
			col_min.put("label", "MIN");
			col_min.put("type", "number");

			JSONObject col_nv = new JSONObject();
			col_nv.put("id", 13);
			col_nv.put("label", "NV");
			col_nv.put("type", "number");

			cols.add(col_1);
			cols.add(col_2_mac);	
			cols.add(col_tooltip_mac);
			cols.add(col_annotation_mac);
			cols.add(col_2_mic);
			cols.add(col_tooltip_mic);
			cols.add(col_annotation_mic);
			cols.add(col_2_nv);
			cols.add(col_tooltip_nv);
			cols.add(col_annotation_nv);
			cols.add(col_mac);
			cols.add(col_min);
			cols.add(col_nv);

			obj.put("cols", cols);

			JSONArray rows = new JSONArray();
			int year = 0;
			int month =0;
			int day =0;
			int i=0;
			int complicationNum = 0;
			String tooltip = "";
			while(rs.next()){
				year = rs.getInt("h_year");
				if(i==0){//creo una riga finta 01/01/annoPrimaComplicanza per non avere le obs addossate allo start
					JSONObject row_objPlus = new JSONObject();
					JSONArray row_arrPlus = new JSONArray();
					JSONObject row_1Plus = new JSONObject();
					row_1Plus.put("v","Date("+year+",00,01)");

					JSONObject row_2_mac_Plus = new JSONObject();
					row_2_mac_Plus.put("v", null);

					JSONObject row_tooltipPlus_mac = new JSONObject();
					row_tooltipPlus_mac.put("v", null);

					JSONObject row_annotationPlus_mac = new JSONObject();
					row_annotationPlus_mac.put("v", null);

					JSONObject row_2_mic_Plus = new JSONObject();
					row_2_mic_Plus.put("v", null);

					JSONObject row_tooltipPlus_mic = new JSONObject();
					row_tooltipPlus_mic.put("v", null);

					JSONObject row_annotationPlus_mic = new JSONObject();
					row_annotationPlus_mic.put("v", null);

					JSONObject row_2_nv_Plus = new JSONObject();
					row_2_nv_Plus.put("v", null);

					JSONObject row_tooltipPlus_nv = new JSONObject();
					row_tooltipPlus_nv.put("v", null);

					JSONObject row_annotationPlus_nv = new JSONObject();
					row_annotationPlus_nv.put("v", null);

					JSONObject row_macPlus = new JSONObject();
					row_macPlus.put("v", 12);

					JSONObject row_micPlus = new JSONObject();
					row_micPlus.put("v", 5.5);

					JSONObject row_nvPlus = new JSONObject();
					row_nvPlus.put("v", 2.5);


					row_arrPlus.add(row_1Plus);
					row_arrPlus.add(row_2_mac_Plus);
					row_arrPlus.add(row_tooltipPlus_mac);
					row_arrPlus.add(row_annotationPlus_mac);
					row_arrPlus.add(row_2_mic_Plus);
					row_arrPlus.add(row_tooltipPlus_mic);
					row_arrPlus.add(row_annotationPlus_mic);
					row_arrPlus.add(row_2_nv_Plus);
					row_arrPlus.add(row_tooltipPlus_nv);
					row_arrPlus.add(row_annotationPlus_nv);
					row_arrPlus.add(row_macPlus);
					row_arrPlus.add(row_micPlus);
					row_arrPlus.add(row_nvPlus);
					row_objPlus.put("c",row_arrPlus);
					rows.add(row_objPlus);
				}
				JSONObject row_obj = new JSONObject();
				JSONArray row_arr = new JSONArray();
				JSONObject row_1 = new JSONObject();
				//year = rs.getInt("h_year");
				month = rs.getInt("h_month")-1;
				day = rs.getInt("h_day");
				row_1.put("v","Date("+year+","+month+","+day+")");

				String conceptCd = rs.getString("concept_cd");
				complicationNum = complicationsMap.get(conceptCd);
				JSONObject row_2_mac = new JSONObject();
				JSONObject row_2_mic = new JSONObject();
				JSONObject row_2_nv = new JSONObject();
				JSONObject row_annotation_mac = new JSONObject();
				JSONObject row_annotation_mic = new JSONObject();
				JSONObject row_annotation_nv = new JSONObject();
				JSONObject row_tooltip_mac = new JSONObject();
				JSONObject row_tooltip_mic = new JSONObject();
				JSONObject row_tooltip_nv = new JSONObject();
				String dateString = "Date: ";
				dateString = dateString.concat(String.valueOf(day)).concat("/").concat(String.valueOf(month+1)).concat("/").concat(String.valueOf(year)).concat("\n");
				tooltip = dateString.concat(rs.getString("obs_blob"));
				if(complicationNum<=2){
					row_2_mac.put("v", null);
					row_2_mic.put("v", null);
					row_2_nv.put("v", complicationNum);
					row_tooltip_mac.put("v", null);
					row_tooltip_mic.put("v", null);
					row_tooltip_nv.put("v", tooltip);
					row_annotation_mac.put("v", null);
					row_annotation_mic.put("v", null);
					row_annotation_nv.put("v", rs.getString("obs_blob"));
				}else if(complicationNum>2 && complicationNum<=5){
					row_2_mac.put("v", null);
					row_2_mic.put("v", complicationNum);
					row_2_nv.put("v", null);
					row_tooltip_mac.put("v", null);
					row_tooltip_mic.put("v", tooltip);
					row_tooltip_nv.put("v", null);
					row_annotation_mac.put("v", null);
					row_annotation_mic.put("v", rs.getString("obs_blob"));
					row_annotation_nv.put("v", null);
				}else if(complicationNum>5 && complicationNum<=11){
					row_2_mac.put("v", complicationNum);
					row_2_mic.put("v", null);
					row_2_nv.put("v", null);
					row_tooltip_mac.put("v", tooltip);
					row_tooltip_mic.put("v", null);
					row_tooltip_nv.put("v", null);
					row_annotation_mac.put("v", rs.getString("obs_blob"));
					row_annotation_mic.put("v", null);
					row_annotation_nv.put("v", null);
				}
				JSONObject row_mac = new JSONObject();
				row_mac.put("v", 12);

				JSONObject row_mic = new JSONObject();
				row_mic.put("v", 5.5);

				JSONObject row_nv = new JSONObject();
				row_nv.put("v", 2.5);

				row_arr.add(row_1);
				row_arr.add(row_2_mac);
				row_arr.add(row_tooltip_mac);
				row_arr.add(row_annotation_mac);
				row_arr.add(row_2_mic);
				row_arr.add(row_tooltip_mic);
				row_arr.add(row_annotation_mic);
				row_arr.add(row_2_nv);
				row_arr.add(row_tooltip_nv);
				row_arr.add(row_annotation_nv);
				row_arr.add(row_mac);
				row_arr.add(row_mic);
				row_arr.add(row_nv);			

				row_obj.put("c",row_arr);
				rows.add(row_obj);
				i++;
			}//fine del while
			//			if(i==1){
			//				JSONObject row_obj = new JSONObject();
			//				JSONArray row_arr = new JSONArray();
			//				JSONObject row_1 = new JSONObject();
			//				year = year-1;
			//				row_1.put("v","Date("+year+","+month+","+day+")");
			//
			//				JSONObject row_2 = new JSONObject();
			//				row_2.put("v", null);
			//
			//				JSONObject row_3 = new JSONObject();
			//				row_3.put("v", null);
			//
			//				JSONObject row_mac = new JSONObject();
			//				row_mac.put("v", 12);
			//
			//				JSONObject row_mic = new JSONObject();
			//				row_mic.put("v", 5.5);
			//
			//				JSONObject row_nv = new JSONObject();
			//				row_nv.put("v", 2.5);
			//
			//				JSONObject row_annotation = new JSONObject();
			//				row_annotation.put("v", null);
			//
			//				row_arr.add(row_1);
			//				row_arr.add(row_2);
			//				row_arr.add(row_annotation);
			//				row_arr.add(row_3);
			//				row_arr.add(row_mac);
			//				row_arr.add(row_mic);
			//				row_arr.add(row_nv);
			//				row_obj.put("c",row_arr);
			//				rows.add(row_obj);
			//			}
			//			JSONObject row_objPlus = new JSONObject();
			//			JSONArray row_arrPlus = new JSONArray();
			//			JSONObject row_1Plus = new JSONObject();
			//			row_1Plus.put("v","Date("+year+",12,31)");
			//
			//			JSONObject row_2Plus = new JSONObject();
			//			row_2Plus.put("v", null);
			//
			//			JSONObject row_3Plus = new JSONObject();
			//			row_3Plus.put("v", null);
			//
			//			JSONObject row_macPlus = new JSONObject();
			//			row_macPlus.put("v", 12);
			//
			//			JSONObject row_micPlus = new JSONObject();
			//			row_micPlus.put("v", 5.5);
			//
			//			JSONObject row_nvPlus = new JSONObject();
			//			row_nvPlus.put("v", 2.5);
			//
			//			JSONObject row_annotationPlus = new JSONObject();
			//			row_annotationPlus.put("v", null);
			//
			//			row_arrPlus.add(row_1Plus);
			//			row_arrPlus.add(row_2Plus);
			//			row_arrPlus.add(row_annotationPlus);
			//			row_arrPlus.add(row_3Plus);
			//			row_arrPlus.add(row_macPlus);
			//			row_arrPlus.add(row_micPlus);
			//			row_arrPlus.add(row_nvPlus);
			//			row_objPlus.put("c",row_arrPlus);
			//			rows.add(row_objPlus);
			obj.put("rows", rows);

			StringWriter swout = new StringWriter();
			obj.writeJSONString(swout);
			jsonText = swout.toString();
			//System.out.println(jsonText);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			try {
				if(rs != null){
					rs.close();
				}
				if(pstmt != null){
					pstmt.close();
				}
				if(conn != null){
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return jsonText;
	}


	private double roundMaxValue(double myValue){
		double result = 0;
		if(myValue>=0 && myValue<=10){
			result = 15;
		} else if (myValue>10 && myValue<=30){
			result = 35;
		}else if (myValue>30 && myValue<=50){
			result = 60;
		}else if (myValue>50 && myValue<=100){
			result = 120;
		}else if (myValue>100 && myValue<=150){
			result = 200;
		}else if (myValue>150 && myValue<=200){
			result = 250;
		}else if (myValue>200 && myValue<=400){
			result = 450;
		}else if (myValue>400 && myValue<=800){
			result = 850;
		}else if (myValue>800){
			result = myValue;
		}
		return result;
	}

	//	private String roundAdherenceValue(double myValue){
	//		String result = "";
	//		if(myValue==0){
	//			result = "INTERRUPTION";
	//		}else if(myValue>0 && myValue<=20){
	//			result = "[0-20]";
	//		} else if (myValue>20 && myValue<=40){
	//			result = "[20-40]";
	//		}else if (myValue>40 && myValue<=60){
	//			result = "[40-60]";
	//		}else if (myValue>60 && myValue<=80){
	//			result = "[60-80]";
	//		}else if (myValue>80 && myValue<=100){
	//			result = "[80-100]";
	//		}else if (myValue>100 && myValue<=120){
	//			result = "[100-120]";
	//		}else if (myValue>120){
	//			result = "OVER";
	//		}
	//		return result;
	//	}

	private String roundAdherenceValue2(double myValue){
		String result = "";
		if(myValue==0){
			result = "INTERRUPTION";
		}else if(myValue>0 && myValue<=40){
			result = "[0-40]";
		}else if(myValue>40 && myValue<=80){
			result = "[40-80]";
		} else if (myValue>80 && myValue<=100){
			result = "[80-100]";
		}else if (myValue>100){
			result = "OVER";
		}
		return result;
	}

	private List<String> getLabelOrder (List<String> atcDescrSet, List<String> aderenzaLabelSet, HashMap<String, List<String>> atcLabelMap){
		List<String> result = new ArrayList<String>();
		for(String atcDescr: atcDescrSet){
			List<String> aderenzaLabel = atcLabelMap.get(atcDescr);
			for(String label : aderenzaLabel){
				if(!result.contains(label)){
					result.add(label);
				}
			}
			//if(result.size()==aderenzaLabelSet.size()){
			if(result.size()==7){
				break;
			}			
		}
		//		System.out.println("********* LUnghezza******* "+result.size());
		//		for(String s: result){
		//			System.out.println(s);
		//		}
		return result;
	}
}
