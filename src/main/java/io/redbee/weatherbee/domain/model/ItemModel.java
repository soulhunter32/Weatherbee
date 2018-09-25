package io.redbee.weatherbee.domain.model;

import java.util.List;

public class ItemModel {

	private List<ForecastModel> forecast;

	public List<ForecastModel> getForecast() {
		return forecast;
	}
	public void setForecast(List<ForecastModel> forecast) {
		this.forecast = forecast;
	}

	@Override
	public String toString() {
		return "ItemModel [forecast=" + forecast + "]";
	}
}
