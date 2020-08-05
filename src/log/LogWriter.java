package log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import shiftschedule.Message;
import shiftschedule.MessageName;

public class LogWriter {

	private static final String LogFilePath = System.getenv("APPDATA") + "\\"
			+ Message.getMessage(MessageName.APP_AUTO_SAVE) + "\\Log.txt";

	private static final String dateTimepattern = "MM/dd HH:mm:ss";
	
	public static void LogMessage(String msg, Log log) {
		try {
			File file = new File(LogFilePath);
			FileWriter fw = new FileWriter(file, true);
			DateFormat dateFormat = new SimpleDateFormat(dateTimepattern);
			fw.write(log + dateFormat.format(new Date())+ " " +msg);
			fw.close();
			// Also print the message to the console 
			if (log == Log.ERROR) {
				System.err.println(msg);
			}else {
				System.out.println(msg);
			}
		} catch (IOException e) {
			System.err.println("[Log file error] " + e.getLocalizedMessage());
			System.err.println(log + msg);
		}
	}
}
