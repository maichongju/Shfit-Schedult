package shiftschedule;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class EmployeeList {
	private ArrayList<Employee> employees = new ArrayList<Employee>();

	private final String JSON_KEY_EMPLOYEES = "employees";
	private final String JSON_KEY_VERSION = "version";

	/**
	 * Default constructor
	 */
	public EmployeeList() {
	}

	/**
	 * Create Employee List with JSON Object
	 * 
	 * @param json JSON object
	 * @throws JSONException Invalid JSON Object
	 */
	public EmployeeList(JSONObject json) throws JSONException {
		// Key to check if the JSON is valid 
		json.getString(JSON_KEY_VERSION);
		JSONArray ja = json.getJSONArray(JSON_KEY_EMPLOYEES);
		for (Object jo : ja) {
			try {
				employees.add(new Employee((JSONObject) jo));
			} catch (JSONException e) {
				System.err.println(e.getMessage());
			}
		}
	}

	/**
	 * Add the given employee to employee list, if the employee name exist then the
	 * insert fail
	 * 
	 * @param employee employee need to add
	 * @return return true if insert success, otherwise return false
	 */
	public boolean addEmployee(Employee employee) {
		/*
		 * Search through the whole list to find if same employee is exist
		 */
		for (Employee e : employees) {
			// If employee exist then return false
			if (e.equals(employee)) {
				return false;
			}
		}

		employees.add(employee); // New employee add to the list
		System.out.println(employee.name + " Added to Employee List");
		return true;
	}

	/**
	 * Remove the given employee from employee list
	 * 
	 * @param employee employee need to be remove
	 */
	public void removeEmployee(Employee employee) {
		employees.remove(employee);
	}

	/**
	 * Clear all the content in Employees
	 */
	public void clearEmployee() {
		employees.clear();
	}

	/**
	 * Get All the Employees
	 * 
	 * @return All the Employees in ArrayList<Employee>
	 */
	public ArrayList<Employee> getContent() {
		return employees;
	}

	/**
	 * Get the employee by index
	 * 
	 * @param index
	 * @return
	 */
	public Employee getEmployee(int index) {
		return employees.get(index);
	}

	/**
	 * Get employee by name
	 * 
	 * @param name Name of the employee
	 * @return null for result not found
	 */
	public Employee getEmployee(String name) {
		for (Employee employee : employees) {
			if (employee.name.equals(name)) {
				return employee;
			}
		}
		return null;
	}

	/**
	 * Check if employee list is empty
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return employees.isEmpty();
	}

	/**
	 * Get the total count of employee
	 * 
	 * @return
	 */
	public int getCount() {
		return employees.size();
	}

	public JSONObject getJSON() {
		JSONObject ssJSONObject = new JSONObject();
		JSONArray employeesJSON = new JSONArray();
		for (Employee employee : employees) {
			employeesJSON.put(employee.getJSON());
		}

		ssJSONObject.put(JSON_KEY_EMPLOYEES, employeesJSON);
		ssJSONObject.put(JSON_KEY_VERSION, Message.getMessage(MessageName.APP_VERSION));

		return ssJSONObject;
	}

}
