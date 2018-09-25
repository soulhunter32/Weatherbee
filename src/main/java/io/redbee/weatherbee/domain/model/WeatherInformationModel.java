package io.redbee.weatherbee.domain.model;

import java.util.List;

public class WeatherInformationModel {

	private QueryModel query;

	public QueryModel getQuery() {
		return query;
	}

	public void setQuery(QueryModel query) {
		this.query = query;
	}

	public List<ForecastModel> getForecastList(){
		return getQuery().getResults().getChannel().getItem().getForecast();
	} 
	
	public LocationModel getLocation() {
		return getQuery().getResults().getChannel().getLocation();
	}

	@Override
	public String toString() {
		return "WeatherInformationModel [query=" + query + "]";
	}
}
