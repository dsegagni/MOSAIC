package it.fsm.mosaic.model;

import java.sql.Date;

public class I2B2Observation {
	
	private int patientNum;
	private int encounterNum;
	private Date startDate;
	private double nValNum;
	
	public int getPatientNum() {
		return patientNum;
	}
	public int getEncounterNum() {
		return encounterNum;
	}
	public Date getStartDate() {
		return startDate;
	}
	public double getnValNum() {
		return nValNum;
	}
	public void setPatientNum(int patientNum) {
		this.patientNum = patientNum;
	}
	public void setEncounterNum(int encounterNum) {
		this.encounterNum = encounterNum;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public void setnValNum(double nValNum) {
		this.nValNum = nValNum;
	}
	
	

}
