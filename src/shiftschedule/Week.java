package shiftschedule;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Week {
	/**
	 * Use this to identify which day is it
	 * 
	 * @author Chongju Mai
	 *
	 */
	public static enum Day {
		MON, TUE, WED, THU, FRI, SAT, SUN;

		public static Day getDay(String value) {
			for (Day day : Day.values()) {
				if (day.name().equals(value)) {
					return day;
				}
			}
			return null;
		}
	}

	private Map<Day, shiftschedule.Day> days = new HashMap<Day, shiftschedule.Day>();

	public Week() {
		days.put(Day.MON, new shiftschedule.Day(Day.MON));
		days.put(Day.TUE, new shiftschedule.Day(Day.TUE));
		days.put(Day.WED, new shiftschedule.Day(Day.WED));
		days.put(Day.THU, new shiftschedule.Day(Day.THU));
		days.put(Day.FRI, new shiftschedule.Day(Day.FRI));
		days.put(Day.SAT, new shiftschedule.Day(Day.SAT));
		days.put(Day.SUN, new shiftschedule.Day(Day.SUN));
	}

	/**
	 * Week constructor with JSON
	 * @param json
	 * @param employee
	 * @throws JSONException
	 */
	public Week(JSONArray json, Employee employee) throws JSONException {
		for (Object jo : json) {
			try {
				shiftschedule.Day day = new shiftschedule.Day((JSONObject) jo, employee);
				days.put(day.day, day);
			} catch (JSONException e) {
				System.err.println(e.getMessage());
			}
		}

		// Fill the fail day with new day
		for (Day day : Week.Day.values()) {
			if (!days.containsKey(day)) {
				days.put(day, new shiftschedule.Day(day));
				System.err.println("Missing " + day.name() + " for " + employee.name);
			}
		}
	}

	/**
	 * Get all the shifts for the given day
	 * 
	 * @param day day need to search for
	 * @return All the shift for that day
	 */
	public ArrayList<Shift> getShifts(Day day) {
		return days.get(day).getShifts();
	}

	/**
	 * get the day object
	 * 
	 * @param day
	 * @return
	 */
	public shiftschedule.Day getDay(Week.Day day) {
		return days.get(day);
	}

	/**
	 * Get the maximum shift for the week
	 * 
	 * @return
	 */
	public int getMaxShift() {
		int maxShift = 0;
		for (Map.Entry<Day, shiftschedule.Day> element : days.entrySet()) {
			if (element.getValue().countShifts() > maxShift) {
				maxShift = element.getValue().countShifts();
			}
		}
		return maxShift;
	}

	/**
	 * 
	 * @return
	 */
	public float getTotalHours() {
		float totalHours = 0;
		for (Map.Entry<Day, shiftschedule.Day> element : days.entrySet()) {
			totalHours += element.getValue().getTotalHours();
		}
		return totalHours;
	}

	/**
	 * Get the JSON array for this week object
	 * 
	 * @return
	 */
	public JSONArray getJSON() {
		JSONArray weekArray = new JSONArray();
		for (Map.Entry<Day, shiftschedule.Day> day : days.entrySet()) {
			weekArray.put(day.getValue().getJSON());
		}
		return weekArray;
	}

}
