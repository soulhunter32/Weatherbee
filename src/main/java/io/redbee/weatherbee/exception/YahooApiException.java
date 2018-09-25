package io.redbee.weatherbee.exception;

public class YahooApiException extends Exception {

	private static final long serialVersionUID = 1L;

	public YahooApiException(String message) {
        super(message);
    }

    public YahooApiException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
