package io.redbee.weatherbee.exception;

public class LocationNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public LocationNotFoundException(String location) {
        super("The location " + location + " was not found, please try again");
    }

    public LocationNotFoundException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
