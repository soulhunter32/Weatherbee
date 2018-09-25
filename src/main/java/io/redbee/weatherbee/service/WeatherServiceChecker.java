package io.redbee.weatherbee.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import io.redbee.weatherbee.domain.Forecast;
import io.redbee.weatherbee.domain.Location;
import io.redbee.weatherbee.domain.model.ForecastModel;
import io.redbee.weatherbee.domain.model.WeatherInformationModel;
import io.redbee.weatherbee.exception.LocationNotFoundException;
import io.redbee.weatherbee.exception.YahooApiException;

/**
 * Service in charge of obtaining the weather information from the Yahoo wheater API.-
 * 
 * @author skapcitzky
 */

@Service
public class WeatherServiceChecker {

	private static final Logger logger = LogManager.getLogger(WeatherServiceChecker.class);
	
	private static final String API_QUERY = "https://query.yahooapis.com/v1/public/yql?"
			+ "q=select%20*%20from%20weather.forecast%20where%20woeid%20in%20(select%20woeid%20from%20geo.places(1)%20"
			+ "where%20text%3D%22locationName"
			+ "%22)&format=json&env=store%3A%2F%2Fdatatables.org%2Falltableswithkeys";
	
    /**
     * Consults the weather service for the location passed by parameter. It returns a new {@link Location} model.-
     * @param location the location name string
     * @return a {@link Location} with it's populated data from the service
     * @throws YahooApiException 
     * @throws LocationNotFoundException 
     */
	public Location findLocationInformation(String location) throws LocationNotFoundException, YahooApiException {
        logger.info("[findLocationInformation] - Starting Wheather check for location " + location);
        
        WeatherInformationModel weatherInformation = retrieveWeather(location);
        
        if (weatherInformation != null) {
        	return locationModelToLocation(null, null, weatherInformation);
		}

        return null;
	}

	/**
	 * Updates the location passed by parameter via consulting weather service and updating it's data.-
	 *
	 * @param location the location to update
	 * @param date the date to update in each location
	 * @return the updated location
	 * @throws YahooApiException 
	 * @throws LocationNotFoundException 
	 */
	public Location updateLocation(Location location, Date date) throws LocationNotFoundException, YahooApiException{
		logger.info("[updateLocation] - Starting Wheather check for location " + location.getCity());
        
        WeatherInformationModel weatherInformation = retrieveWeather(location.getCity());
		
		if (weatherInformation != null) {
			return locationModelToLocation(location, date, weatherInformation);
		}
		
		return null;
	}

	/**
	 * Calls the Yahoo! API in order to get the weather information for the corresponding location.-
	 * 
	 * @param location the location name
	 * @return a {@link WeatherInformationModel} with all the updated information from the service for the location
	 * @throws YahooApiException 
	 */
	protected WeatherInformationModel checkLocationWheather(String location) throws YahooApiException {
		URL url;
		HttpURLConnection connection;
        StringBuffer response = null;
        BufferedReader reader = null;
        
		try {
			url = new URL(new String(API_QUERY).replaceAll("locationName", location).replaceAll(" ", "%20"));
			
			connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			
			logger.debug("[checkLocationWheather] - Querying URL: " + url);
			
			reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			
			response = new StringBuffer();
			while ((inputLine = reader.readLine()) != null) {
				response.append(inputLine);
			}
		} catch (IOException e) {
			logger.error("[checkLocationWheather] - There was an error while parsing weather response");
			e.printStackTrace();
			throw new YahooApiException(e.getMessage());
		} finally {
			try {
				if (reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				logger.error("[checkLocationWheather] - There was an error while closing weather response");
				e.printStackTrace();
				return null;
			}
		}

        Gson gson = new Gson();
		WeatherInformationModel weatherInformation = (WeatherInformationModel) (gson.fromJson(response.toString(), WeatherInformationModel.class));
		
		return weatherInformation;
	}
	
	/**
	 * Transform the API response model to a Location domain model. If location paramenter is present, the location is updated, if not, a new location is created.-
	 * @param location the location to update
	 * @param date 
	 * @param weatherInformation the API response model
	 * @return an updated location
	 */
	private Location locationModelToLocation(Location location, Date date, WeatherInformationModel weatherInformation) {
		if (location == null) {
			location = new Location();
		}
		
		location.setCity(weatherInformation.getLocation().getCity());
		location.setRegion(weatherInformation.getLocation().getRegion());
		location.setCountry(weatherInformation.getLocation().getCountry());
		
		for (ForecastModel forecastItem : weatherInformation.getForecastList()) {
			if (date == null || (date != null && forecastItem.getDate().compareTo(date) == 0)) {
				Forecast forecast = new Forecast();
				forecast.setDate(forecastItem.getDate());
				forecast.setMinTemperature(forecastItem.getMinTemp());
				forecast.setMaxTemperature(forecastItem.getMaxTemp());
				forecast.setWeatherDescription(forecastItem.getDescription());
				forecast.setLocation(location);
				location.getForecast().add(forecast);
			}
		}
		
		return location;
	}
	
	/**
	 * Retrieves the weather information from the Yahoo API.-
	 *  
	 * @param location the location's to retrieve information for
	 * @return a {@link WeatherInformationModel} with the location weather information
	 * @throws LocationNotFoundException 
	 * @throws YahooApiException 
	 */
	private WeatherInformationModel retrieveWeather(String location) throws LocationNotFoundException, YahooApiException {
		WeatherInformationModel weatherInformation = null;
		weatherInformation = checkLocationWheather(location);
		if (weatherInformation.getQuery().getResults() == null) {
			throw new LocationNotFoundException(location);
		}
		
		logger.info("[findLocationInformation] - Weather response object: " + weatherInformation);
		
		return weatherInformation;
	}
}
