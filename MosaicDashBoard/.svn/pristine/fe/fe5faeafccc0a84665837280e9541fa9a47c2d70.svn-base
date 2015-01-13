package it.fsm.mosaic.model;

import com.mongodb.DBObject;

public class CacheMongoObject {
	
	//************** STATUS ************
	// 0: json ingresso mancante, nuova creazione (ANALISI MATLAB da lanciare)
	// 1: json ingresso presente, ma json result mancante (a regime non dovrebbe mai accadere)
	// 2: json ingresso presente, json result presente (ANALISI MATLAB non lanciata)

	private int status;
	private  DBObject mongoObj;
	
	
	public CacheMongoObject(){
		
	}


	public int getStatus() {
		return status;
	}


	public DBObject getMongoObj() {
		return mongoObj;
	}


	public void setStatus(int status) {
		this.status = status;
	}


	public void setMongoObj(DBObject mongoObj) {
		this.mongoObj = mongoObj;
	}
}
