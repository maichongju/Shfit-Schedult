package shiftschedule;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Day {
	private ArrayList<Shift> shifts = new ArrayList<Shift>();
	public Week.Day day = null;

	private final String JSON_KEY_SHIFT = "shift";
	private final String JSON_KEY_DAY = "day";

	public Day() {
	}

	public Day(Week.Day day) {
		this.day = day;
	}

	/**
	 * Any invalid will throw JSONException
	 * 
	 * @param json     JSON Object
	 * @param employee
	 * @throws JSONException
	 */
	public Day(JSONObject json, Employee employee) throws JSONException {
		day = Week.Day.getDay(json.getString(JSON_KEY_DAY));
		// If day is invalid Day object will not be created
		if (day == null) {
			throw new JSONException(json.getString(JSON_KEY_DAY) + " is not a valid day");
		}
		JSONArray ja = json.getJSONArray(JSON_KEY_SHIFT);
		for (Object jo : ja) {
			try {
				shifts.add(new Shift((JSONObject) jo, employee));
			} catch (JSONException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	/**
	 * Add the given shift to the day
	 * 
	 * @param shift   the shift need to add
	 * @param overlap if overlap is allow
	 * @return Null if add success, if overlap it will return the the one overlap
	 *         with
	 */
	public Shift addShift(Shift shift, boolean overlap) {
		// TODO sort add and check if it is overlap
		shifts.add(shift);
		return null;
	}

	/**
	 * Clear all the shifts for the day
	 */
	public void clearAllShifts() {
		shifts.clear();
	}

	/**
	 * Get all the shifts for the day
	 * 
	 * @return ArrayList of Shift
	 */
	public ArrayList<Shift> getShifts() {
		return shifts;
	}

	/**
	 * Get the total shift for this employee
	 * 
	 * @return total shift
	 */
	public int countShifts() {
		return shifts.size();
	}

	/**
	 * Total Hours for the day
	 * 
	 * @return
	 */
	public float getTotalHours() {
		float totalHours = 0;
		for (Shift shift : shifts) {
			totalHours += shift.getTotalHour();
		}
		return totalHours;
	}

	/**
	 * This will remove the overlap shifts
	 */
	public void RemoveOverLap() {

	}

	/**
	 * Return JSON object for day
	 * 
	 * @return
	 */
	public JSONObject getJSON() {
		JSONObject dayJSONObject = new JSONObject();
		// Create Shift JSON Array
		JSONArray ja = new JSONArray();
		for (Shift shift : shifts) {
			ja.put(shift.getJSON());
		}

		dayJSONObject.put(JSON_KEY_DAY, day.name());
		dayJSONObject.put(JSON_KEY_SHIFT, ja);
		return dayJSONObject;
	}

	/**
	 * Get the string
	 * 
	 * @param use24
	 * @return
	 */
	public String toString(boolean use24) {
		String result = "";
		for (Shift shift : shifts) {
			result += shift.toString(use24) + "\n";
		}
		return result.trim();
	}

}
