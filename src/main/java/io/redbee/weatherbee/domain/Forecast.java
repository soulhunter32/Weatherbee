package io.redbee.weatherbee.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
@Table(name = "FORECAST")
public class Forecast {

	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy = "increment")
	@Column(name = "ID")
	private Integer id;
	
	@Column(name = "ACTUAL_DATE")
	private Date date;
	
	@Column(name = "MAX_TEMPERATURE")
	private Integer maxTemperature;
	
	@Column(name = "MIN_TEMPERATURE")
	private Integer minTemperature;
	
	@Column(name = "WHEATER_DESCRIPTION")
	private String weatherDescription;
	
	@JsonBackReference
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "LOCATION_ID")
	private Location location;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Integer getMaxTemperature() {
		return maxTemperature;
	}

	public void setMaxTemperature(Integer maxTemperature) {
		this.maxTemperature = maxTemperature;
	}

	public Integer getMinTemperature() {
		return minTemperature;
	}

	public void setMinTemperature(Integer minTemperature) {
		this.minTemperature = minTemperature;
	}

	public String getWeatherDescription() {
		return weatherDescription;
	}

	public void setWeatherDescription(String weatherDescription) {
		this.weatherDescription = weatherDescription;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}
}
