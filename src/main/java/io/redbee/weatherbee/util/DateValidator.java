package io.redbee.weatherbee.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Date validation utility.-
 * 
 * @author skapcitzky
 */
public final class DateValidator {

	public static final String DATE_FORMAT = "dd/MM/yyyy";

	public static boolean isDateValid(String dateToValidate){
		if(dateToValidate == null){
			return false;
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		sdf.setLenient(false);
		
		try {
			sdf.parse(dateToValidate);
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
}
