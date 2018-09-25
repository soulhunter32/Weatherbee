package io.redbee.weatherbee.api;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.redbee.weatherbee.api.entity.ErrorEntity;
import io.redbee.weatherbee.domain.Board;
import io.redbee.weatherbee.domain.Forecast;
import io.redbee.weatherbee.domain.Location;
import io.redbee.weatherbee.domain.User;
import io.redbee.weatherbee.exception.LocationNotFoundException;
import io.redbee.weatherbee.exception.YahooApiException;
import io.redbee.weatherbee.repository.BoardRepository;
import io.redbee.weatherbee.repository.LocationRepository;
import io.redbee.weatherbee.repository.UserRepository;
import io.redbee.weatherbee.service.WeatherServiceChecker;
import io.redbee.weatherbee.util.DateValidator;


/**
 * Rest service in charge of managing all board actions from all users.-
 * 
 * @author skapcitzky
 */

@RestController
@RequestMapping({"/boards"})
public class WeatherBoardRestService {

	private static final Logger logger = LogManager.getLogger(WeatherBoardRestService.class);
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private WeatherServiceChecker weatherServiceChecker;
	
	/**
	 * Access the user board, if the user has none, a new one is created.-
	 * 
	 * @param username current user
	 * @return the board of the user with its locations
	 */
	@RequestMapping(value = "/{username}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Board> loadBoard(@PathVariable("username") String username) {
		logger.info("[loadBoard] - Loading board for user " + username);
		
		User actualUser = userRepository.findByUsername(username);
		
		if (actualUser == null) {
			logger.info("[loadBoard] - User " + username + " not found. Creating default board...");
			User user = new User();
			user.setUsername(username);
			
			Board board = new Board();
			user.addBoard(board);
			userRepository.save(user);
			actualUser = user;
		}

		//TODO: verificar refactor para cuando la logica acepte mas de un board con crud
//		return actualUser.getBoardList().get(0).getLocationList();
		
		logger.info("[loadBoard] - Locations for user " + username + ": " + actualUser.getBoardList().get(0).getLocations().size());

		return new ResponseEntity<Board>(actualUser.getBoardList().get(0), HttpStatus.OK);
	}
	
	/**
	 * Adds a new location to the user and board passed by parameter.-
	 * 
	 * @param username current user
	 * @param boardId board to add the location
	 * @param locationString location string to be added
	 * @return a new updated location
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
//	@RequestMapping(value = "/{username}", params = { "boardId", "location" }, method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	public ResponseEntity<Location> addLocationToBoard(@PathVariable("username") String username,  @RequestParam("boardId") Integer boardId, @RequestParam("location") String locationString) {
	@RequestMapping(value = "/{username}/{location}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Location> addLocationToBoard(@PathVariable("username") String username,  @PathVariable("location") String locationString) {
		logger.info("[addLocationToBoard] - Adding location " + locationString + " for user " + username);
		
		Location locationFound;
		try {
			locationFound = weatherServiceChecker.findLocationInformation(locationString);
		} catch (LocationNotFoundException e) {
			e.printStackTrace();
			logger.error("There was an error while retrieving Yahoo API weather data", e.getMessage());
			return new ResponseEntity(new ErrorEntity("There was an error while retrieving Yahoo API weather data"), HttpStatus.CONFLICT);
		} catch (YahooApiException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ResponseEntity(new ErrorEntity(e.getMessage()), HttpStatus.CONFLICT);
		}
		Board board = null;
		
		if (locationFound != null) {
			board = userRepository.findByUsername(username).getBoardList().get(0);
			if (board.getLocationByCity(locationFound.getCity()) != null) {
				logger.info("[addLocationToBoard] - Location " + locationString + " already added to board ");
				return new ResponseEntity(new ErrorEntity("Location " + locationString + " already added to board "), HttpStatus.CONFLICT);
			}
			board.addLocation(locationFound);
		} else {
			logger.error("[addLocationToBoard] - There was an error retrieving weather information for " + locationString);
			return new ResponseEntity(new ErrorEntity("There was an error retrieving weather information for " + locationString), HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<Location>(boardRepository.save(board).getLocationByCity(locationFound.getCity()), HttpStatus.OK);
		
	}
	
	/**
	 * Deletes a location from the user and board passed by parameter.-
	 * 
	 * @param username current user
	 * @param locationId the location to delete
	 * @return
	 */
	@RequestMapping(value = "/{username}/{locationId}",  method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> deleteLocationFromBoard(@PathVariable("username") String username,  @PathVariable("locationId") Integer locationId) {
		logger.info("[deleteLocationFromBoard] - Deleting location ID " + locationId + " of user " + username);
		
		locationRepository.deleteById(locationId);
		
		logger.info("[deleteLocationFromBoard] - Location successfully deleted");
		
		return new ResponseEntity<String>(HttpStatus.NO_CONTENT);
	}
	
	/**
	 * Updates the board by updating its location's information.-
	 * 
	 * @param username current user
	 * @param boardId board to update
	 * @return a list of updated locations
	 * @throws YahooApiException 
	 * @throws LocationNotFoundException 
	 */
	@RequestMapping(value = "/{username}", params = "boardId", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Location> updateBoard(@PathVariable("username") String username,  @RequestParam("boardId") Integer boardId) throws LocationNotFoundException, YahooApiException {
		logger.info("[updateBoard] - Updating board " + boardId + " of user " + username);
		
		Optional<Board> board = boardRepository.findById(boardId);
		
		for (Location location : board.get().getLocations()) {
			Location updatedLocationInformation = weatherServiceChecker.findLocationInformation(location.getCity());
			updateLocationInformation(location, updatedLocationInformation);
		}
		
		logger.info("[updateBoard] - Board " + boardId + " successfully updated");
		
		return boardRepository.save(board.get()).getLocations();
	}
	
	/**
	 * Forces the update of the board and date passed by parameter.- 
	 * 
	 * @param username current board
	 * @param boardId board id of the user
	 * @param date the location's date to update
	 * @return an updated location
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/{username}/board/{boardId}", params = "date", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Location>> forceUpdateBoardByDate(@PathVariable("username") String username, @PathVariable("boardId") Integer boardId, @RequestParam("date") String date ) {
		logger.info("[forceUpdateBoard] - Forcing update of board " + boardId + " of user " + username);
		
		if (!DateValidator.isDateValid(date)) {
			return new ResponseEntity(new ErrorEntity("Invalida date format for " + date + ". Please use dd/MM/yyy (01/01/2018) format"), HttpStatus.CONFLICT);
		}
		
		Calendar calendarDateQuery = Calendar.getInstance();
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(DateValidator.DATE_FORMAT);
		try {
			calendarDateQuery.setTime(dateFormat.parse(date));
		} catch (ParseException e) {
			logger.error("There was an error parsing the date: " + date);
			e.printStackTrace();
			return new ResponseEntity(new ErrorEntity("There was an error parsing the date: " + date), HttpStatus.CONFLICT);
		}
		Calendar calendarActualDate = Calendar.getInstance();
		
		int daysFromToday = calendarDateQuery.get(Calendar.DAY_OF_YEAR) - calendarActualDate.get(Calendar.DAY_OF_YEAR);
		
		if (daysFromToday > 10) {
			return new ResponseEntity(new ErrorEntity("Only 10 days after actual date can be query - Date of query: " + date), HttpStatus.CONFLICT);
		} else if (daysFromToday < 0) {
			return new ResponseEntity(new ErrorEntity("The date can't be previous today date - Date of query: " + date), HttpStatus.CONFLICT);
		}
		
		Optional<Board> board = boardRepository.findById(boardId);
		
		if (board.get().getLocations().isEmpty()) {
			return new ResponseEntity(new ErrorEntity("There are no locations for the board " + boardId), HttpStatus.NO_CONTENT);
		}
		
		List<Location> updatedLocations = new ArrayList<Location>();
		try {
			for (Iterator locIterator = board.get().getLocations().iterator(); locIterator.hasNext();) {
				Location location = (Location) locIterator.next();
				updatedLocations.add(weatherServiceChecker.updateLocation(location, calendarDateQuery.getTime()));
			}
		} catch (LocationNotFoundException e) {
			e.printStackTrace();
			logger.error("There was an error while retrieving Yahoo API weather data", e.getMessage());
			return new ResponseEntity(new ErrorEntity("There was an error while retrieving Yahoo API weather data"), HttpStatus.CONFLICT);
		} catch (YahooApiException e) {
			e.printStackTrace();
			logger.error(e.getMessage());
			return new ResponseEntity(new ErrorEntity(e.getMessage()), HttpStatus.CONFLICT);
		}
		
		
		if (!updatedLocations.isEmpty()) {
			updatedLocations.forEach(location -> locationRepository.save(location));
			
			logger.info("[forceUpdateBoard] - Board " + boardId + " successfully updated for date " + date);
			
			return new ResponseEntity<List<Location>>(updatedLocations, HttpStatus.OK);
		} else {
			return new ResponseEntity(new ErrorEntity("There was no locations to update"), HttpStatus.CONFLICT);
		}
	}
	
	/**
	 * Forces the update of all locations of the board passed by parameter.-
	 * 
	 * @param username current user
	 * @param boardId the id of the board to update
	 * @return an updated board
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@RequestMapping(value = "/{username}/board/{boardId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Board> forceUpdateCompleteBoard(@PathVariable("username") String username, @PathVariable("boardId") Integer boardId) {
		logger.info("[forceUpdateCompleteBoard] - Forcing update of board " + boardId + " of user " + username);
		
		boolean updateErrors = false;
		
		Optional<Board> board = boardRepository.findById(boardId);
		
		if (board.get().getLocations().isEmpty()) {
			return new ResponseEntity(new ErrorEntity("There are no locations for the board " + boardId), HttpStatus.NO_CONTENT);
		}
		
		for (Location location : board.get().getLocations()) {
			Location locationToUpdate;
			try {
				locationToUpdate = weatherServiceChecker.updateLocation(location, null);
			} catch (LocationNotFoundException e) {
				e.printStackTrace();
				logger.error("There was an error while retrieving Yahoo API weather data", e.getMessage());
				return new ResponseEntity(new ErrorEntity("There was an error while retrieving Yahoo API weather data"), HttpStatus.CONFLICT);
			} catch (YahooApiException e) {
				e.printStackTrace();
				logger.error(e.getMessage());
				return new ResponseEntity(new ErrorEntity(e.getMessage()), HttpStatus.CONFLICT);
			}
			if (locationToUpdate != null) {
				locationRepository.save(locationToUpdate);
			} else {
				updateErrors = true;
			}
		}
		
		logger.info("[forceUpdateCompleteBoard] - Board " + boardId + " successfully updated");
		
		if (updateErrors) {
			return new ResponseEntity(new ErrorEntity("There was an error updating one or more locations"), HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<Board>(board.get(), HttpStatus.OK);
	}

	/**
	 * Updates the location element with the new updated information.-
	 * 
	 * @param location the location to update
	 * @param updatedLocationInformation the updated information
	 */
	private void updateLocationInformation(Location location, Location updatedLocationInformation) {
		
		for (int i = 0; i < updatedLocationInformation.getForecast().size(); i++) {
			if (location.getForecast().get(i).getDate().equals(updatedLocationInformation.getForecast().get(i).getDate())) {
				location.getForecast().get(i).setMaxTemperature(updatedLocationInformation.getForecast().get(i).getMaxTemperature());
				location.getForecast().get(i).setMinTemperature(updatedLocationInformation.getForecast().get(i).getMinTemperature());
				location.getForecast().get(i).setWeatherDescription(updatedLocationInformation.getForecast().get(i).getWeatherDescription());
			}
		}
	}

	public void setBoardRepository(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}

	public void setUserRepository(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	public void setLocationRepository(LocationRepository locationRepository) {
		this.locationRepository = locationRepository;
	}

	public void setWeatherBoardService(WeatherServiceChecker weatherServiceChecker) {
		this.weatherServiceChecker = weatherServiceChecker;
	}
}
