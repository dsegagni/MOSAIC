package it.fsm.mosaic.model;



public class I2B2TherapyObservation {
	
	private int patientNum;
	private int startDateYear;
	private int startDateMonth;
	private int startDateDay;
	private double nValNum;
	private String atcClass;
	private String atcDescr;
	private String atcCode;
	private String tValChar;
	private int endDateYear;
	private int endDateMonth;
	private int endDateDay;
	
	public I2B2TherapyObservation(){
		
	}

	public int getPatientNum() {
		return patientNum;
	}



	public double getnValNum() {
		return nValNum;
	}

	public String getAtcClass() {
		return atcClass;
	}

	public String getAtcDescr() {
		return atcDescr;
	}

	public String getAtcCode() {
		return atcCode;
	}

	public void setPatientNum(int patientNum) {
		this.patientNum = patientNum;
	}



	public void setnValNum(double nValNum) {
		this.nValNum = nValNum;
	}

	public void setAtcClass(String atcClass) {
		this.atcClass = atcClass;
	}

	public void setAtcDescr(String atcDescr) {
		this.atcDescr = atcDescr;
	}

	public void setAtcCode(String atcCode) {
		this.atcCode = atcCode;
	}

	public int getStartDateYear() {
		return startDateYear;
	}

	public int getStartDateMonth() {
		return startDateMonth;
	}

	public int getStartDateDay() {
		return startDateDay;
	}

	public void setStartDateYear(int startDateYear) {
		this.startDateYear = startDateYear;
	}

	public void setStartDateMonth(int startDateMonth) {
		this.startDateMonth = startDateMonth;
	}

	public void setStartDateDay(int startDateDay) {
		this.startDateDay = startDateDay;
	}

	public String gettValChar() {
		return tValChar;
	}

	public void settValChar(String tValChar) {
		this.tValChar = tValChar;
	}

	public int getEndDateYear() {
		return endDateYear;
	}

	public int getEndDateMonth() {
		return endDateMonth;
	}

	public int getEndDateDay() {
		return endDateDay;
	}

	public void setEndDateYear(int endDateYear) {
		this.endDateYear = endDateYear;
	}

	public void setEndDateMonth(int endDateMonth) {
		this.endDateMonth = endDateMonth;
	}

	public void setEndDateDay(int endDateDay) {
		this.endDateDay = endDateDay;
	}

}
