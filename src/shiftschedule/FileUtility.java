package shiftschedule;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONException;
import org.json.JSONObject;

import log.Log;
import log.LogWriter;

public class FileUtility {
	private static final String TempFilePath = System.getenv("APPDATA") + "\\"
			+ Message.getMessage(MessageName.APP_AUTO_SAVE) + "\\autosave.json";
	private static final String ConfigFilePath = System.getenv("APPDATA") + "\\"
			+ Message.getMessage(MessageName.APP_AUTO_SAVE) + "\\config.json";
	private static final String Extension = ".json";

	/**
	 * Load the configuration form the save file
	 * 
	 * @return
	 */
	public static Config loadConfig() {
		Config config = new Config();
		try {
			File file = new File(ConfigFilePath);
			// If file does not exist
			if (!file.exists()) {
				LogWriter.LogMessage("Missing Config.json, use the default config", Log.WARNING);
				saveConfig(config);
			} else {
				String content = FileUtils.readFileToString(file, "utf-8");
				config = new Config(new JSONObject(content));
			}

		} catch (IOException e) {
			LogWriter.LogMessage(e.getLocalizedMessage(), Log.ERROR);
			return new Config();
		}
		return config;
	}

	/**
	 * Save the current configuration to the JSON
	 * 
	 * @param config
	 */
	public static void saveConfig(Config config) {
		try {
			File saveFile = new File(ConfigFilePath);
			saveFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
			writer.append(config.getJSON().toString());
			writer.close();
		} catch (IOException e) {
			LogWriter.LogMessage(e.getLocalizedMessage(), Log.ERROR);
		}
	}

	/**
	 * Call for auto load, load the file from the default location
	 * 
	 * @return
	 */
	public static EmployeeList load() {
		File file = new File(TempFilePath);
		EmployeeList employees = new EmployeeList();
		try {
			employees = _load(file);
			System.out.println("Auto save file loaded");
		} catch (IOException e) {
			System.err.println("Auto save file cannot find");
			System.err.println("Error: " + e.getMessage());
		} catch (JSONException e) {
			System.err.println("Load auto save file fail");
			System.err.println("Error: " + e.getMessage());
		}
		return employees;
	}

	public static EmployeeList load(File file) throws IOException, JSONException {
		return _load(file);
	}

	/**
	 * private helper function for load the file
	 * 
	 * @param file
	 * @return
	 * @throws IOException
	 * @throws JSONException indicate the JSON is invalid
	 */
	private static EmployeeList _load(File file) throws IOException, JSONException {
		String content = FileUtils.readFileToString(file, "utf-8");
		JSONObject jo = new JSONObject(content);
		return new EmployeeList(jo);
	}

	/**
	 * Call this function for auto save
	 * 
	 * @return
	 */
	public static boolean save(EmployeeList employees) {
		_save(TempFilePath, employees, false);
		return true;
	}

	/**
	 * Save the employee to the given path
	 * 
	 * @param path      path to save
	 * @param employees employee list need to save
	 * @return
	 */
	public static boolean save(String path, EmployeeList employees) {
		_save(path, employees, true);
		return true;
	}

	/**
	 * Helper for save file
	 * 
	 * @param path      path to save the file
	 * @param employees employee list to save
	 * @return true if the file is save
	 */
	private static boolean _save(String path, EmployeeList employees, boolean beauty) {
		if (!path.endsWith(Extension)) {
			path += Extension;
		}
		File saveFile = new File(path);
		saveFile.getParentFile().mkdirs();
		try {
			saveFile.createNewFile();
			BufferedWriter writer = new BufferedWriter(new FileWriter(saveFile));
			if (beauty) {
				writer.append(employees.getJSON().toString(4));
			} else {
				writer.append(employees.getJSON().toString());
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		System.out.println("File Saved to: " + path);
		return true;
	}

	public static void saveToExcel(File file, EmployeeList employees, boolean use24) throws IOException {

		try {
			OutputStream fileOut = new FileOutputStream(file);

			Workbook wb = new XSSFWorkbook();

			Map<String, CellStyle> styles = createStyles(wb);
			Sheet shiftSheet = wb.createSheet("Shift");
			Row row = shiftSheet.createRow(0);
			row.createCell(0).setCellValue(Message.getMessage(MessageName.EMPLOYEE_NAME));
			row.getCell(0).setCellStyle(styles.get("cell"));
			row.createCell(8).setCellValue(Message.getMessage(MessageName.TOTAL_HOURS));
			row.getCell(8).setCellStyle(styles.get("cell"));
			for (int i = 1; i < Week.Day.values().length + 1; i++) {
				row.createCell(i).setCellValue(Message.getMessage("DAY_" + Week.Day.values()[i - 1].name()));
				row.getCell(i).setCellStyle(styles.get("cell"));
			}
			// cs.setBorderTop(HSSCellStyle.);
			for (int i = 0; i < employees.getCount(); i++) {
				row = shiftSheet.createRow(1 + i);
				for (int j = 0; j < 9; j++) {
					Employee employee = employees.getEmployee(i);
					Cell cell = row.createCell(j);

					// Name
					if (j == 0) {
						cell.setCellValue(employee.name);
						cell.setCellStyle(styles.get("cell"));
					} else if (j == 8) {
						// Shift
						cell.setCellValue(employee.getTotalHours());
						cell.setCellStyle(styles.get("cell"));
					} else {
						// Total hour
						Week.Day day = Week.Day.values()[j - 1];
						cell.setCellValue(employee.getDay(day).toString(true));
						cell.setCellStyle(styles.get("shiftCell"));
					}

					// Auto adjust the column width
					shiftSheet.autoSizeColumn(j);
				}
			}

			wb.write(fileOut);
			wb.close();
			fileOut.close();
			System.out.println("Export succees");
		} catch (IOException e) {
			System.err.println(e.getMessage());
			throw e;
		}
	}

	/**
	 * Helper function for saveToExcel
	 * 
	 * @param wb
	 * @return
	 */
	private static Map<String, CellStyle> createStyles(Workbook wb) {
		Map<String, CellStyle> styles = new HashMap<>();
		CellStyle style;

		// Normal with border
		style = wb.createCellStyle();
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		styles.put("cell", style);

		// Shift Cell with border
		style = wb.createCellStyle();
		style.setWrapText(true);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		styles.put("shiftCell", style);

		return styles;
	}
}
