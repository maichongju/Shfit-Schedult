package shiftschedule;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import shiftschedule.Week.Day;

public class Employee {
	public String name = "";
	private Week week = new Week();

	private final String JSON_KEY_EMPLOYEE = "employee";
	private final String JSON_KEY_WEEK = "week";

	public Employee(String name) {
		this.name = name;
	}

	/**
	 * Construction with JSON
	 * @param json
	 * @throws JSONException
	 */
	public Employee(JSONObject json) throws JSONException {
		name = json.getString(JSON_KEY_EMPLOYEE);
		week = new Week(json.getJSONArray(JSON_KEY_WEEK), this);
	}

	/**
	 * get the Shifts for this employee for the given day
	 * 
	 * @param day the day need to get
	 * @return the shift of the given day
	 */
	public ArrayList<Shift> getShifts(Week.Day day) {
		return week.getShifts(day);
	}

	/**
	 * 
	 * @param day
	 * @param shift
	 * @param overlap
	 */
	public void addShift(Week.Day day, Shift shift, boolean overlap) {
		week.getDay(day).addShift(shift, overlap);
	}

	/**
	 * Compare two employee by name
	 * 
	 * @param otherEmployee
	 * @return true if two employee name is the same
	 */
	public boolean equals(Employee otherEmployee) {
		return name.equals(otherEmployee.name);
	}

	/**
	 * get the maximum shifts for the week
	 * 
	 * @return maximum shifts of the week
	 */
	public int getMaxShift() {
		return week.getMaxShift();
	}

	/**
	 * Total hour of the week
	 * 
	 * @return
	 */
	public float getTotalHours() {
		return week.getTotalHours();
	}

	/**
	 * get the day object
	 * 
	 * @param day
	 * @return
	 */
	public shiftschedule.Day getDay(Day day) {
		return week.getDay(day);
	}

	public JSONObject getJSON() {
		JSONObject employeeJSON = new JSONObject();
		employeeJSON.put(JSON_KEY_WEEK, week.getJSON());
		employeeJSON.put(JSON_KEY_EMPLOYEE, name);

		return employeeJSON;
	}

	public String toString() {
		return name;
	}

}
