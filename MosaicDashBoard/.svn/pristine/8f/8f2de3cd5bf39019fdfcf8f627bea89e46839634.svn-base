package it.fsm.mosaic.model;

import java.util.Comparator;
import java.util.List;

public class I2B2ComorbidtyObservation implements Comparable<I2B2ComorbidtyObservation>{

	private String comorbidityDescr;
	private String patientNumListString;
	private List<Integer> patientNumList;
	private String conceptCd;
	private int comorbClassId;

	public I2B2ComorbidtyObservation(){

	}

	public String getComorbidityDescr() {
		return comorbidityDescr;
	}


	public void setComorbidityDescr(String comorbidityDescr) {
		this.comorbidityDescr = comorbidityDescr;
	}


	public List<Integer> getPatientNumList() {
		return patientNumList;
	}

	public void setPatientNumList(List<Integer> patientNumList) {
		this.patientNumList = patientNumList;
	}

	public String getPatientNumListString() {
		return patientNumListString;
	}

	public void setPatientNumListString(String patientNumListString) {
		this.patientNumListString = patientNumListString;
	}


	public String createPatientNumListString(){
		String result = "";
		for(Integer i : patientNumList){
			result = result.concat(String.valueOf(i).concat("-"));
		}
		return result;
	}

	public String getConceptCd() {
		return conceptCd;
	}

	public void setConceptCd(String conceptCd) {
		this.conceptCd = conceptCd;
	}

	public static Comparator<I2B2ComorbidtyObservation> nameComparator = new Comparator<I2B2ComorbidtyObservation>() {

		public int compare(I2B2ComorbidtyObservation fruit1, I2B2ComorbidtyObservation fruit2) {

			String fruitName1 = fruit1.getComorbidityDescr().toUpperCase();
			String fruitName2 = fruit2.getComorbidityDescr().toUpperCase();

			//ascending order
			return fruitName1.compareTo(fruitName2);

			//descending order
			//return fruitName2.compareTo(fruitName1);
		}

	};

	@Override
	public int compareTo(I2B2ComorbidtyObservation arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getComorbClassId() {
		return comorbClassId;
	}

	public void setComorbClassId(int comorbClassId) {
		this.comorbClassId = comorbClassId;
	}
}
