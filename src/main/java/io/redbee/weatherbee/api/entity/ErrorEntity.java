package io.redbee.weatherbee.api.entity;

public class ErrorEntity {

	private Integer code;
	private String message;

	public ErrorEntity(String error) {
		super();
		this.message = error;
	}
	public Integer getCode() {
		return code;
	}
	public void setMessage(Integer code) {
		this.code = code;
	}
	public String getMessage() {
		return message;
	}
	public void setError(String error) {
		this.message = error;
	}
}
