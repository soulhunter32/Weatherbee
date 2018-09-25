package io.redbee.weatherbee.task;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import io.redbee.weatherbee.domain.Board;
import io.redbee.weatherbee.domain.Location;
import io.redbee.weatherbee.exception.LocationNotFoundException;
import io.redbee.weatherbee.exception.YahooApiException;
import io.redbee.weatherbee.repository.BoardRepository;
import io.redbee.weatherbee.repository.LocationRepository;
import io.redbee.weatherbee.service.WeatherServiceChecker;

@Component
public class WeatherUpdaterTask {
    private static final Logger logger = LogManager.getLogger(WeatherServiceChecker.class);

    @Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private WeatherServiceChecker weatherServiceChecker;
	
//    @Scheduled(fixedDelay = 60000)
    public void updateAllBoardLocations() {
    	
    	logger.info("***************************************************");
    	logger.info("[updateAllBoardLocations] - Starting scheduled task...");
    	
    	Iterable<Board> allBoards = boardRepository.findAll();
    	int totalCount = 0;
    	int successCount = 0;
    	
    	for (Board board : allBoards) {
			for (Location location : board.getLocations()) {
				Location updatedLocation = null;
				try {
					updatedLocation = weatherServiceChecker.updateLocation(location, null);
				} catch (LocationNotFoundException e) {
					e.printStackTrace();
					logger.error("There was an error while retrieving Yahoo API weather data", e.getMessage());
				} catch (YahooApiException e) {
					e.printStackTrace();
					logger.error(e.getMessage());
				}
				if (updatedLocation != null) {
					locationRepository.save(updatedLocation);
					successCount++;
				}
				totalCount++;
			}
		}
    	
    	logger.info("[updateAllBoardLocations] - " + successCount + " of " + totalCount + " locations updated. Ending scheduled task...");
    	logger.info("***************************************************");
    }

	public void setBoardRepository(BoardRepository boardRepository) {
		this.boardRepository = boardRepository;
	}

	public void setLocationRepository(LocationRepository locationRepository) {
		this.locationRepository = locationRepository;
	}

	public void setWeatherServiceChecker(WeatherServiceChecker weatherServiceChecker) {
		this.weatherServiceChecker = weatherServiceChecker;
	}
}
