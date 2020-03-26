package com.kaleido.service.dto;

import java.time.ZonedDateTime;
import java.util.stream.Collectors;

import com.kaleido.domain.Authority;
import com.kaleido.domain.PlateMap;
import com.kaleido.domain.User;
import com.kaleido.domain.enumeration.Status;

public class PlateMapDTO {
	
	private String activityName;
	private String checksum;
	private Long id;
	private ZonedDateTime lastModified;
	private Status status;
	private int numPlates;
	
	public PlateMapDTO() {
        // Empty constructor needed for Jackson.
    }

    public PlateMapDTO(PlateMap plateMap) {
        this.id = plateMap.getId();
        this.activityName = plateMap.getActivityName();
        this.checksum = plateMap.getChecksum();
        this.lastModified = plateMap.getLastModified();
        this.status = plateMap.getStatus();
        this.numPlates = plateMap.getNumPlates();
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getActivityName() {
    	return activityName;
    }
    
    public void setActivityName(String activityName) {
    	this.activityName = activityName;
    }
    
    public String getChecksum() {
    	return checksum;
    }
    
    public void setChecksum(String checksum) {
    	this.checksum = checksum;
    }
    
    public ZonedDateTime getLastModified() {
    	return lastModified;
    }
    
    public void setLastModified(ZonedDateTime lastModified) {
    	this.lastModified = lastModified;
    }
    
    public Status getStatus() {
    	return status;
    }
    
    public void setStatus(Status status) {
    	this.status = status;
    }
    
    public int getNumPlates() {
        return numPlates;
    }

    public void setNumPlates(int numPlates) {
        this.numPlates = numPlates;
    }

    @Override
    public String toString() {
    	return "PlateMapDTO{" +
    			"activityName='" + activityName + '\'' +
                ", checksum='" + checksum + '\'' +
                ", numPlates='" + numPlates + '\'' +
                ", lastModified='" + lastModified + '\'' +
                ", status=" + status +
                "}";
    }

}
