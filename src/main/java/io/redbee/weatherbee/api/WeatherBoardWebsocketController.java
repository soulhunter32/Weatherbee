package io.redbee.weatherbee.api;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import io.redbee.weatherbee.api.message.BoardUpdateRequest;
import io.redbee.weatherbee.domain.Board;
import io.redbee.weatherbee.domain.Location;
import io.redbee.weatherbee.repository.BoardRepository;
import io.redbee.weatherbee.repository.LocationRepository;
import io.redbee.weatherbee.service.WeatherServiceChecker;

@Controller
public class WeatherBoardWebsocketController {

	private static final Logger logger = LogManager.getLogger(WeatherBoardWebsocketController.class);
	
	@Autowired
	private BoardRepository boardRepository;
	
	@Autowired
	private LocationRepository locationRepository;
	
	@Autowired
	private WeatherServiceChecker weatherServiceChecker;

	private SimpMessagingTemplate template;
	
	public WeatherBoardWebsocketController(SimpMessagingTemplate messagingTemplate) {
		this.template = messagingTemplate;
	}
	
	
	@MessageMapping("/weather-updates")
	@SendTo("/send/messages")
	public void sendBoardUpdate(BoardUpdateRequest boardRequest) throws Exception {
		
		boolean updateErrors = false;
		
		logger.info("[sendBoardUpdate] - Updating board " + boardRequest.getBoardId() + " of user ");
		
		Optional<Board> board = boardRepository.findById(boardRequest.getBoardId());
		
		if (board.get().getLocations().isEmpty()) {
			this.template.convertAndSend("/weather-updates", "No locations for current board");
		}
		
		for (Location location : board.get().getLocations()) {
			Location locationToUpdate = weatherServiceChecker.updateLocation(location, null);
			if (locationToUpdate != null) {
				locationRepository.save(locationToUpdate);
			} else {
				updateErrors = true;
			}
		}
		
		logger.info("[sendBoardUpdate] - Board " + boardRequest.getBoardId() + " successfully updated");

		if (updateErrors) {
			logger.debug("[sendBoardUpdate] - Sending board info to queue '/weather-updates'");
		}
		
		logger.debug("[sendBoardUpdate] - Sending board info to queue '/weather-updates'");
		
		this.template.convertAndSend("/weather-updates", board.get());
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
