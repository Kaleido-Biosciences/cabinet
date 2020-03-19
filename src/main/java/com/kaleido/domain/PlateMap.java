package com.kaleido.domain;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import javax.validation.constraints.*;

import org.springframework.data.elasticsearch.annotations.FieldType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.kaleido.domain.enumeration.Status;

/**
 * A PlateMap.
 */
@Entity
@Table(name = "plate_map")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
@org.springframework.data.elasticsearch.annotations.Document(indexName = "platemap")
public class PlateMap implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @org.springframework.data.elasticsearch.annotations.Field(type = FieldType.Keyword)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @Column(name = "last_modified")
    private ZonedDateTime lastModified;

    /**
     * The checksum is used when saving a new draft, as the last checksum has to be passed\nand match the most recent timestamp. Otherwise it is considered attempting to save a stale draft
     */
    @ApiModelProperty(value = "The checksum is used when saving a new draft, as the last checksum has to be passed\nand match the most recent timestamp. Otherwise it is considered attempting to save a stale draft")
    @Column(name = "checksum")
    private String checksum;

    /**
     * The name of the activity. Used for grouping on
     */
    @ApiModelProperty(value = "The name of the activity. Used for grouping on")
    @Column(name = "activity_name")
    private String activityName;

    /**
     * The data field is a gzip -> base64 encoded string of the plate map data
     */
    @Size(max = 10485760)
    @ApiModelProperty(value = "The data field is a gzip -> base64 encoded string of the plate map data")
    @Column(name = "data", length = 10485760)
    private String data;
    
    /**
     * The number of plates that are in the platemap
     */
    @ApiModelProperty(value = "The number of plates that are in the platemap")
    @Column(name = "num_plates")
    private Integer numPlates;
    
    // jhipster-needle-entity-add-field - JHipster will add fields here, do not remove
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Status getStatus() {
        return status;
    }

    public PlateMap status(Status status) {
        this.status = status;
        return this;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ZonedDateTime getLastModified() {
        return lastModified;
    }

    public PlateMap lastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
        return this;
    }

    public void setLastModified(ZonedDateTime lastModified) {
        this.lastModified = lastModified;
    }

    public String getChecksum() {
        return checksum;
    }

    public PlateMap checksum(String checksum) {
        this.checksum = checksum;
        return this;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public String getActivityName() {
        return activityName;
    }

    public PlateMap activityName(String activityName) {
        this.activityName = activityName;
        return this;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public String getData() {
        return data;
    }

    public PlateMap data(String data) {
        this.data = data;
        return this;
    }

    public void setData(String data) {
        this.data = data;
    }
    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here, do not remove

    public Integer getNumPlates() {
        return numPlates;
    }
    
    public PlateMap numPlates(Integer numPlates) {
        this.numPlates = numPlates;
        return this;
    }
    
    public void setNumPlates(Integer numPlates) {
        this.numPlates = numPlates;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PlateMap)) {
            return false;
        }
        return id != null && id.equals(((PlateMap) o).id);
    }

    @Override
    public int hashCode() {
        return 31;
    }

    @Override
    public String toString() {
        return "PlateMap{" +
            "id=" + getId() +
            ", status='" + getStatus() + "'" +
            ", lastModified='" + getLastModified() + "'" +
            ", checksum='" + getChecksum() + "'" +
            ", numPlates='" + getNumPlates() + "'" +
            ", activityName='" + getActivityName() + "'" +
            ", data='" + getData() + "'" +
            "}";
    }
    
    public String prepareStringForChecksum() {
    	DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
    	return getStatus().toString()+getLastModified().format(formatter)+getActivityName()+getData();
    }
}
