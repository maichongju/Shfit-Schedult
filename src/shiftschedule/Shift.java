package shiftschedule;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

import org.json.JSONException;
import org.json.JSONObject;

public class Shift {
	public Employee employee;
	public LocalTime startTime;
	public LocalTime endTime;

	private final String JSON_KEY_STARTTIME = "startTime";
	private final String JSON_KEY_ENDTIME = "endTime";

	public Shift(Employee employee,LocalTime startTime,LocalTime endTime) {
		this.employee = employee;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	/**
	 * Use the given JSON to create Shift
	 * @param json JSON object create from
	 * @param employee employee for this shift
	 * @throws JSONException Invalid Shift 
	 */
	public Shift(JSONObject json,Employee employee) throws JSONException {
		this.employee = employee;
		try {
			startTime = LocalTime.parse(json.getString(JSON_KEY_STARTTIME));
			endTime = LocalTime.parse(json.getString(JSON_KEY_ENDTIME));
		}catch(DateTimeParseException e) {
			throw new JSONException(employee.name + " is not a valid shift");
		}
	}
	
	/**
	 * Calculate the total time for this shift in hour
	 * 
	 * @return total time in this shift
	 */
	public float getTotalHour() {
		float totalHour = (startTime.until(endTime, ChronoUnit.MINUTES));
		if (totalHour < 0) {
			totalHour = (24 * 60 + totalHour);
		}
		totalHour /= 60.0;
		return totalHour;
	}

	/**
	 * return shift as follow format
	 * 
	 */
	public String toString(boolean use24) {
		String pattern = use24 ? "HH:mm" : "hh:mma";
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return startTime.format(formatter) + "-" + endTime.format(formatter);
	}
	
	/**
	 * Create a JSONObject for this object. 
	 * For create JSON
	 * @return
	 */
	public JSONObject getJSON() {
		JSONObject shiftJSONObject = new JSONObject();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		shiftJSONObject.put(JSON_KEY_STARTTIME,startTime.format(formatter));
		shiftJSONObject.put(JSON_KEY_ENDTIME, endTime.format(formatter));
		return shiftJSONObject;
	}
}
