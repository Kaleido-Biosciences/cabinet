package com.kaleido.service.dto;

import com.kaleido.domain.PlateMap;

public class DataDTO {
	
	private String data;
	
	public DataDTO() {
		
	}
	
	public DataDTO(PlateMap plateMap) {
		this.data = plateMap.getData();
	}
	
	public String getData() {
		return data;
	}
	
	public void setData(String data) {
		this.data = data;
	}

}
