package shiftschedule;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class Message {
	// Default Locale
	private static final Map<Language,Locale> locales = createLocales();
	private static ResourceBundle messages =ResourceBundle.getBundle("messages",Locale.getDefault());;
	

	/**
	 * Function to create the map
	 * @return
	 */
	private static Map<Language,Locale> createLocales(){
		Map<Language,Locale> locales = new HashMap<>();
		locales.put(Language.DEFAULT, Locale.getDefault());
		locales.put(Language.ZH, new Locale ("zh","cn"));
		return locales;
	}
	
	
	/**
	 * Get the message from the resource file
	 * 
	 * @param key key for the message
	 * @return message for the key value
	 */
	public static String getMessage(String key) {
		return messages.getString(key);
	}

	public static String getMessage(MessageName messagename) {
		try {
			return messages.getString(messagename.name());
		} catch (MissingResourceException e) {
			System.err.println(e.toString());
			return "";
		}
	}
	public static void setLocal(Language lang) {
		Locale locale = locales.get(lang);
		messages = ResourceBundle.getBundle("messages",locale);
	}

}
