package shiftschedule;

import org.json.JSONException;
import org.json.JSONObject;

import log.Log;
import log.LogWriter;

public class Config {
	
	private final String JSON_KEY_USE24 = "use24";
	private final String JSON_KEY_LANGUAGE = "language";
	
	public boolean use24 = true;
	public Language language = Language.DEFAULT;
	
	public Config() {
		
	}
	
	public Config (JSONObject json) {
		try {
			use24 = json.getBoolean(JSON_KEY_USE24);
			language = Language.getLanguage(json.getString(JSON_KEY_LANGUAGE));
		}catch (JSONException e) {
			LogWriter.LogMessage(e.getLocalizedMessage(), Log.ERROR);
		}
	}
	
	/**
	 * Return a JSON object for configuration 
	 * @return
	 */
	public JSONObject getJSON() {
		JSONObject json = new JSONObject();
		json.put(JSON_KEY_USE24, use24);
		json.put(JSON_KEY_LANGUAGE, language);
		return json;
	}
}
