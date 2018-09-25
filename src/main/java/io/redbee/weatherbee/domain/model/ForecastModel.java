package io.redbee.weatherbee.domain.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.annotations.SerializedName;

public class ForecastModel {

	private static final Logger logger = LogManager.getLogger(ForecastModel.class);
	
	private Integer code;
	private String date;
	private String day;
	@SerializedName("high")
	private Integer maxTemp;
	@SerializedName("low")
	private Integer minTemp;
	@SerializedName("text")
	private String description;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	public Date getDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
		Date date = null;
		try {
			date = sdf.parse(this.date);
		} catch (ParseException e) {
			logger.error("[getDate] - There was an error while parsing the date");
			e.printStackTrace();
			return null;
		}
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	public String getDay() {
		return day;
	}
	public void setDay(String day) {
		this.day = day;
	}
	public Integer getMaxTemp() {
		return maxTemp;
	}
	public void setMaxTemp(Integer maxTemp) {
		this.maxTemp = maxTemp;
	}
	public Integer getMinTemp() {
		return minTemp;
	}
	public void setMinTemp(Integer minTemp) {
		this.minTemp = minTemp;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "ForecastModel [code=" + code + ", date=" + date + ", day=" + day + ", maxTemp=" + maxTemp + ", minTemp="
				+ minTemp + ", description=" + description + "]";
	}
	
	
}
